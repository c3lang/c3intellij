package org.c3lang.intellij.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3PathAtIdent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Macro-callable support: {@code @}-callable resolution and generic type
 * parameters of enclosing modules, functions and macros. The shared
 * call-matching engine stays in {@link CallChecker}.
 */
public final class MacroSupport
{
    private MacroSupport()
    {
    }
    static @Nullable C3CallablePsiElement resolveAtCallable(@NotNull C3PathAtIdent atIdent)
    {
        for (PsiReference reference : atIdent.getReferences())
        {
            PsiElement resolved;
            try
            {
                resolved = reference.resolve();
            }
            catch (Exception e)
            {
                continue;
            }
            if (resolved instanceof C3CallablePsiElement callable) return callable;
        }
        return null;
    }
    /**
     * Whether the inferred argument type is a generic type parameter of an
     * enclosing module, function or macro (e.g. {@code Key} in
     * {@code module std::collections::map <Key, Value>}). Its concrete type
     * is only known at instantiation, so any conversion involving it is
     * allowed here and checked by the compiler per instantiation.
     * Pure PSI text walk: no index access.
     */
    static boolean isGenericTypeParam(@NotNull C3Expr argExpr, @NotNull InferredType inferred)
    {
        String clean = inferred.getName().strip();
        int separator = clean.lastIndexOf("::");
        String shortName = separator >= 0 ? clean.substring(separator + 2) : clean;
        // Only bare identifiers can be type parameters; pointers, slices,
        // optionals and the like already carry a concrete shape.
        if (!shortName.matches("[A-Za-z_][A-Za-z_0-9]*")) return false;
        PsiElement current = argExpr;
        while (current != null)
        {
            if (current instanceof org.c3lang.intellij.psi.C3ModuleSection section
                && section.getModule() != null
                && section.getModule().getGenericDecl() != null)
            {
                if (genericDeclNames(section.getModule().getGenericDecl()).contains(shortName)) return true;
            }
            if (current instanceof C3FuncDef funcDef && funcDef.getGenericDecl() != null)
            {
                if (genericDeclNames(funcDef.getGenericDecl()).contains(shortName)) return true;
            }
            if (current instanceof C3MacroDefinition macroDef && macroDef.getGenericDecl() != null)
            {
                if (genericDeclNames(macroDef.getGenericDecl()).contains(shortName)) return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static @NotNull java.util.Set<String> genericDeclNames(
            @NotNull org.c3lang.intellij.psi.C3GenericDecl genericDecl)
    {
        java.util.Set<String> names = new java.util.HashSet<>();
        for (org.c3lang.intellij.psi.C3ModuleParam param
            : genericDecl.getModuleParams().getModuleParamList())
        {
            // `Key`, `Value = int`, `Type...`: the parameter name is the
            // leading identifier of the raw text.
            String text = param.getText();
            if (text == null) continue;
            java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("[A-Za-z_][A-Za-z_0-9]*").matcher(text.strip());
            if (matcher.find()) names.add(matcher.group());
        }
        return names;
    }
}
