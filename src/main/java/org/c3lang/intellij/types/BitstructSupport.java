package org.c3lang.intellij.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.index.TypeIndex;
import org.c3lang.intellij.psi.C3BitstructBody;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3BitstructDef;
import org.c3lang.intellij.psi.C3BitstructSimpleDef;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

/**
 * Bitstruct semantics: declaration lookup, backing types, field types and
 * constant truncation checks. Pure static helpers shared by assignability,
 * casts, layout, inference and the annotator; anything unresolvable is
 * {@code null}. Rules verified against {@code c3c}.
 */
public final class BitstructSupport
{
    private BitstructSupport()
    {
    }
    /**
     * Bitstruct declaration by (possibly qualified) name, or {@code null}.
     * Pure index scan; same-module declarations win on name clashes.
     */
    private static @Nullable C3BitstructDeclaration findBitstructDecl(
            @NotNull String structName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        if (DumbService.isDumb(project)) return null;
        String clean = TypeChecker.normalize(structName).strip();
        if (!clean.matches("[A-Za-z_][A-Za-z_0-9.:]*")) return null;
        String wanted = TypeChecker.shortName(clean);
        List<C3TypeName> names;
        try
        {
            names = TypeCanonicalizer.findIndexElements(TypeIndex.KEY, C3TypeName.class, structName, project,
                typeName -> typeName.getText().strip().equals(wanted)
                    && typeName.getParent() instanceof C3BitstructDeclaration);
        }
        catch (Exception ignored)
        {
            return null;
        }
        C3TypeName best = TypeCanonicalizer.preferSameModule(names, contextModule);
        if (best == null) return null;
        return best.getParent() instanceof C3BitstructDeclaration bitstruct ? bitstruct : null;
    }

    /**
     * Whether the name denotes a bitstruct (verified against the type
     * index, dumb-safe).
     */
    public static boolean isBitstruct(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        return findBitstructDecl(typeName, project, contextModule) != null;
    }

    /**
     * Backing type of a bitstruct ({@code char} for
     * {@code bitstruct Sb : char}), or {@code null} when the name is not a
     * bitstruct. Pure index scan plus a guarded PSI read of the
     * declaration's type.
     */
    public static @Nullable String bitstructBacking(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        C3BitstructDeclaration match = findBitstructDecl(typeText, project, contextModule);
        if (match == null) return null;
        try
        {
            C3Type backing = match.getType();
            if (backing != null && backing.getText() != null && !backing.getText().isBlank())
            {
                return backing.getText().strip();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * Bitstruct field declaration ({@code C3BitstructDef} or
     * {@code C3BitstructSimpleDef}) by struct and field name, or
     * {@code null}. Index scan plus a guarded PSI read of the body.
     */
    public static @Nullable C3PsiElement findBitstructField(
            @NotNull String structName,
            @NotNull String field,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        C3BitstructDeclaration match = findBitstructDecl(structName, project, contextModule);
        if (match == null) return null;
        try
        {
            C3BitstructBody body = match.getBitstructBody();
            if (body == null) return null;
            for (C3BitstructDef def : body.getBitstructDefList())
            {
                if (field.equals(bitFieldName(def))) return def;
            }
            for (C3BitstructSimpleDef def : body.getBitstructSimpleDefList())
            {
                if (field.equals(bitFieldName(def))) return def;
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private static @Nullable String bitFieldName(@NotNull C3PsiElement def)
    {
        try
        {
            ASTNode ident = def.getNode().findChildByType(C3Types.IDENT);
            return ident != null ? ident.getText() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    /**
     * Declared type text of a bitstruct field ({@code int} for
     * {@code int a : 0..2}), or {@code null} for anything else.
     */
    public static @Nullable String bitstructFieldTypeText(@Nullable PsiElement field)
    {
        try
        {
            if (field instanceof C3BitstructDef def && def.getBaseType() != null)
            {
                return def.getBaseType().getText().strip();
            }
            if (field instanceof C3BitstructSimpleDef simple && simple.getBaseType() != null)
            {
                return simple.getBaseType().getText().strip();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * Error when a constant assigned to a bitstruct field does not fit the
     * field's bit range ({@code int a : 0..2} holds 0..3): c3c rejects it as
     * {@code This constant would be truncated if stored in the bitstruct...}.
     * Non-constant expressions stay unchecked (the compiler truncates them).
     */
    public static @Nullable String bitstructTruncationError(@Nullable PsiElement field, @Nullable InferredType source)
    {
        if (field == null || source == null || !source.isLiteral() || source.getIntValue() == null) return null;
        if (!(field instanceof C3BitstructDef def)) return null;
        int bits;
        try
        {
            List<C3Expr> bounds = def.getExprList();
            if (bounds.isEmpty()) return null;
            if (bounds.size() < 2)
            {
                // Single position (`bool b : 3`): exactly one bit.
                bits = 1;
            }
            else
            {
                ModuleName module = ModuleName.from(def);
                Long start = TypeChecker.evalSize(bounds.get(0).getText(), def.getProject(), module, 0);
                Long end = TypeChecker.evalSize(bounds.get(1).getText(), def.getProject(), module, 0);
                if (start == null || end == null || end <= start) return null;
                bits = (int) Math.min(end - start, 62);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        BigInteger value = source.getIntValue();
        if (value.signum() < 0) return null;
        if (value.bitLength() > bits)
        {
            return "This constant would be truncated if stored in the bitstruct, do you need a wider bit range?";
        }
        return null;
    }
}
