package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Conditional compilation gating of top-level declarations.
 *
 * <p>A {@code module X @if(cond);} header opens a section that lasts until
 * the next {@code module} header, so several sections can declare the same
 * module with mutually exclusive conditions (the platform branches in
 * {@code libc.c3} are the classic example). A declaration may also carry its
 * own {@code @if}/{@code @feat} attribute.
 *
 * <p>The condition key is {@code section-condition|own-condition} with
 * whitespace removed, or {@code null} when the declaration is unconditional.
 * Only textually equal keys count as true duplicates: declarations gated by
 * different conditions are never reported, since at most one branch is
 * active per build and a false "already defined" is worse than a miss
 * (the real compiler still catches genuine duplicates).
 *
 * <p>Must stay index-free: it runs during stub creation, where the file
 * being indexed may itself be mapped in the stub index.
 */
public final class ConditionalGating
{
    private ConditionalGating()
    {
    }

    public static @Nullable String conditionKey(@NotNull PsiElement declaration)
    {
        String section = sectionCondition(declaration);
        String own = ownCondition(declaration);
        if (section == null && own == null) return null;
        return (section != null ? section : "") + "|" + (own != null ? own : "");
    }

    private static @Nullable String sectionCondition(@NotNull PsiElement declaration)
    {
        try
        {
            C3ModuleSection section = PsiTreeUtil.getParentOfType(declaration, C3ModuleSection.class);
            if (section == null) return null;
            C3Module module = section.getModule();
            if (module == null) return null;
            return gatingAttributes(module.getAttributes());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static @Nullable String ownCondition(@NotNull PsiElement declaration)
    {
        try
        {
            C3Attributes attributes = null;
            if (declaration instanceof C3FuncDef funcDef) attributes = funcDef.getAttributes();
            else if (declaration instanceof C3MacroDefinition macro) attributes = macro.getAttributes();
            return gatingAttributes(attributes);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static @Nullable String gatingAttributes(@Nullable C3Attributes attributes)
    {
        if (attributes == null) return null;
        List<String> parts = new ArrayList<>();
        List<C3Attribute> attributeList;
        try
        {
            attributeList = attributes.getAttributeList();
        }
        catch (Exception e)
        {
            return null;
        }
        for (C3Attribute attribute : attributeList)
        {
            String name;
            try
            {
                name = attribute.getAttributeName().getText().strip();
            }
            catch (Exception e)
            {
                continue;
            }
            if (!name.equals("@if") && !name.equals("@feat")
                && !name.endsWith("::@if") && !name.endsWith("::@feat")) continue;
            String text = attribute.getText();
            if (text != null) parts.add(text.replaceAll("\\s+", ""));
        }
        if (parts.isEmpty()) return null;
        return String.join("&", parts);
    }
}
