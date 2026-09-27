package org.c3lang.intellij.types;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3CtCaseStmt;
import org.c3lang.intellij.psi.C3CtIfStmt;
import org.c3lang.intellij.psi.C3CtSwitchStmt;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3PsiNamedElement;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Detects duplicate function and method definitions. C3 has no overloading:
 * two {@code fn Type.name} (or two {@code fn name}) with the same owner and
 * name in one module is an error, even with different signatures.
 */
public final class DuplicateChecker
{
    private DuplicateChecker()
    {
    }

    public static void checkFunction(@NotNull C3FuncDef funcDef, @NotNull AnnotationHolder holder)
    {
        Project project = funcDef.getProject();
        if (DumbService.isDumb(project)) return;
        PsiElement parent = funcDef.getParent();
        if (!(parent instanceof C3FuncDefinition definition) || definition.getMacroFuncBody() == null) return;
        if (isConditionallyCompiled(funcDef)) return;

        String implName = funcDef.getNameIdent();
        if (implName == null) return;
        checkDuplicate(
            funcDef,
            InterfaceService.methodOwnerTypeName(funcDef),
            implName,
            false,
            ModuleName.from(funcDef),
            project,
            holder);
    }

    public static void checkMacro(@NotNull C3MacroDefinition macro, @NotNull AnnotationHolder holder)
    {
        Project project = macro.getProject();
        if (DumbService.isDumb(project)) return;
        if (macro.getMacroFuncBody() == null) return;
        if (isConditionallyCompiled(macro)) return;

        String implName = macro.getNameIdent();
        if (implName == null) return;
        checkDuplicate(
            macro,
            InterfaceService.methodOwnerTypeName(macro),
            implName,
            true,
            ModuleName.from(macro),
            project,
            holder);
    }

    private static void checkDuplicate(
            @NotNull C3CallablePsiElement callable,
            @Nullable String ownerText,
            @NotNull String implName,
            boolean isMacro,
            @Nullable ModuleName module,
            @NotNull Project project,
            @NotNull AnnotationHolder holder)
    {
        List<? extends C3CallablePsiElement> duplicates = findDuplicates(callable, ownerText, implName, isMacro, module, project);
        if (duplicates.isEmpty()) return;
        // `@weak`/`@weaklink` definitions are superseded by a non-weak one in
        // the same compilation: never duplicates (spec "Attributes" chapter).
        if (isWeak(callable))
        {
            boolean nonWeakExists = false;
            for (C3CallablePsiElement other : duplicates)
            {
                if (!isWeak(other))
                {
                    nonWeakExists = true;
                    break;
                }
            }
            if (nonWeakExists) return;
        }
        else
        {
            duplicates = duplicates.stream().filter(other -> !isWeak(other)).toList();
            if (duplicates.isEmpty()) return;
        }

        C3CallablePsiElement previousSameFile = null;
        boolean otherFile = false;
        for (C3CallablePsiElement other : duplicates)
        {
            if (isSameFile(other, callable))
            {
                if (other.getTextOffset() < callable.getTextOffset() && previousSameFile == null)
                {
                    previousSameFile = other;
                }
            }
            else
            {
                otherFile = true;
            }
        }
        if (previousSameFile == null && !otherFile) return;

        PsiElement anchor = nameAnchor(callable);
        String message;
        if (isMacro)
        {
            message = ownerText != null
                ? "This macro method is already defined for '" + ownerText + "'."
                : "Macro '" + implName + "' is already defined.";
        }
        else
        {
            message = ownerText != null
                ? "This method is already defined for '" + ownerText + "'."
                : "Function '" + implName + "' is already defined.";
        }
        if (previousSameFile != null)
        {
            int line = lineNumber(previousSameFile);
            if (line >= 0) message += " The previous definition is on line " + (line + 1) + ".";
        }
        holder.newAnnotation(HighlightSeverity.ERROR, message).range(anchor).create();
    }

    private static @NotNull PsiElement nameAnchor(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3PsiNamedElement named && named.getNameIdentifier() != null)
        {
            return named.getNameIdentifier();
        }
        return callable;
    }

    private static int lineNumber(@NotNull C3CallablePsiElement callable)
    {
        try
        {
            PsiElement anchor = nameAnchor(callable);
            com.intellij.openapi.editor.Document document = com.intellij.psi.PsiDocumentManager
                .getInstance(callable.getProject())
                .getDocument(callable.getContainingFile());
            if (document == null) return -1;
            return document.getLineNumber(anchor.getTextOffset());
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    private static @NotNull List<C3CallablePsiElement> findDuplicates(
            @NotNull C3CallablePsiElement callable,
            @Nullable String ownerText,
            @NotNull String implName,
            boolean isMacro,
            @Nullable ModuleName module,
            @NotNull Project project)
    {
        List<C3CallablePsiElement> result = new ArrayList<>();
        if (module == null) return result;
        // Fully qualified index key, mirroring the stub FQNs:
        // `test::foo` / `test::@swap`, `test::Test.test` for methods.
        String selfKey = ownerText != null
            ? module.getValue() + "::" + ownerText.strip() + "." + implName
            : module.getValue() + "::" + implName;
        if (DumbService.isDumb(project)) return result;
        if (!StubIndex.getInstance().getAllKeys(NameIndex.KEY, project).contains(selfKey)) return result;
        Collection<C3PsiElement> elements;
        try
        {
            elements = StubIndex.getElements(
                NameIndex.KEY,
                selfKey,
                project,
                C3ProjectService.getInstance(project).getSearchScope(),
                C3PsiElement.class);
        }
        catch (Exception ignored)
        {
            // Stale index entry for a file without a stub tree (e.g. indexed
            // as plain text before C3 association): degrade to no duplicates
            // instead of breaking highlighting.
            return result;
        }
        for (C3PsiElement element : elements)
        {
            if (isMacro && !(element instanceof C3MacroDefinition other)) continue;
            if (!isMacro && !(element instanceof C3FuncDef other)) continue;
            C3CallablePsiElement other = (C3CallablePsiElement) element;
            if (isSameElement(other, callable)) continue;
            if (!hasBody(other) || isConditionallyCompiled(other)) continue;
            if (!sameCondition(callable, other)) continue;
            if (!implName.equals(other.getName())) continue;
            // `@local` callables are file-confined (like C statics): the
            // same name in another file is a separate declaration, never a
            // duplicate (verified against c3c on math_nolibc acos/asin).
            if (!isSameFile(other, callable) && (isLocal(callable) || isLocal(other))) continue;
            if (ownerText != null)
            {
                String otherOwner = ownerOf(other);
                if (otherOwner == null || !ownerEquals(otherOwner, ownerText)) continue;
            }
            else if (ownerOf(other) != null)
            {
                continue;
            }
            if (!Objects.equals(ModuleName.from(other), module)) continue;
            result.add(other);
        }
        return result;
    }

    private static @Nullable String ownerOf(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef) return InterfaceService.methodOwnerTypeName(funcDef);
        if (callable instanceof C3MacroDefinition macro) return InterfaceService.methodOwnerTypeName(macro);
        return null;
    }

    private static boolean ownerEquals(@NotNull String a, @NotNull String b)
    {
        String cleanA = a.strip();
        String cleanB = b.strip();
        return cleanA.equals(cleanB);
    }

    private static boolean hasBody(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef)
        {
            PsiElement parent = funcDef.getParent();
            return parent instanceof C3FuncDefinition definition && definition.getMacroFuncBody() != null;
        }
        if (callable instanceof C3MacroDefinition macro)
        {
            return macro.getMacroFuncBody() != null;
        }
        return false;
    }

    private static boolean isConditionallyCompiled(@NotNull C3CallablePsiElement callable)
    {
        return PsiTreeUtil.getParentOfType(callable, C3CtIfStmt.class, C3CtSwitchStmt.class, C3CtCaseStmt.class) != null;
    }

    private static boolean isWeak(@NotNull C3CallablePsiElement callable)
    {
        try
        {
            if (callable instanceof C3FuncDef funcDef)
            {
                return AttributeSpecs.hasAttribute(funcDef.getAttributes(), "weak")
                    || AttributeSpecs.hasAttribute(funcDef.getAttributes(), "weaklink");
            }
            if (callable instanceof C3MacroDefinition macro)
            {
                return AttributeSpecs.hasAttribute(macro.getAttributes(), "weak")
                    || AttributeSpecs.hasAttribute(macro.getAttributes(), "weaklink");
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    /**
     * Whether the callable is file-confined (`@local`, like a C static):
     * unreadable attributes degrade to non-local (flag, don't miss).
     */
    private static boolean isLocal(@NotNull C3CallablePsiElement callable)
    {
        try
        {
            if (callable instanceof C3FuncDef funcDef)
            {
                return AttributeSpecs.hasAttribute(funcDef.getAttributes(), "local");
            }
            if (callable instanceof C3MacroDefinition macro)
            {
                return AttributeSpecs.hasAttribute(macro.getAttributes(), "local");
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    /**
     * True duplicate candidates share the same conditional gating: both
     * unconditional, or gated by textually identical {@code @if}/{@code @feat}
     * conditions. Declarations in different {@code module X @if(...);} sections
     * (e.g. per-platform branches) are alternatives, never conflicts.
     */
    private static boolean sameCondition(@NotNull C3CallablePsiElement a, @NotNull C3CallablePsiElement b)
    {
        return Objects.equals(conditionKeyOf(a), conditionKeyOf(b));
    }

    private static @Nullable String conditionKeyOf(@NotNull C3CallablePsiElement callable)
    {
        try
        {
            if (callable instanceof C3FuncDef funcDef) return funcDef.getConditionKey();
            if (callable instanceof C3MacroDefinition macro) return macro.getConditionKey();
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private static boolean isSameElement(@NotNull C3CallablePsiElement a, @NotNull C3CallablePsiElement b)
    {
        if (a.equals(b)) return true;
        if (!isSameFile(a, b)) return false;
        return a.getTextOffset() == b.getTextOffset();
    }

    private static boolean isSameFile(@NotNull C3CallablePsiElement a, @NotNull C3CallablePsiElement b)
    {
        if (a.getContainingFile() == null || b.getContainingFile() == null) return false;
        if (!a.getContainingFile().getName().equals(b.getContainingFile().getName())) return false;
        if (a.getContainingFile().getVirtualFile() == null || b.getContainingFile().getVirtualFile() == null)
        {
            return a.getContainingFile().equals(b.getContainingFile());
        }
        return a.getContainingFile().getVirtualFile().equals(b.getContainingFile().getVirtualFile());
    }
}
