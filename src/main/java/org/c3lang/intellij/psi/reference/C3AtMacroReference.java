package org.c3lang.intellij.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PathAtIdent;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Reference from an {@code @macro} name in a call, e.g. {@code @swap} in
 * {@code @swap(a, b)}, to the {@link C3MacroDefinition}.
 */
public class C3AtMacroReference extends C3ReferenceBase<C3PathAtIdent>
{
    public C3AtMacroReference(@NotNull C3PathAtIdent element)
    {
        super(element);
    }

    @Override
    public @NotNull Collection<C3PsiElement> multiResolve()
    {
        String text = myElement.getText();
        if (text == null || !text.strip().startsWith("@")) return Collections.emptyList();
        if (InterfaceService.isTrailingBlockReference(myElement)) return Collections.emptyList();

        String name = text.strip();
        C3ModuleDefinition moduleDefinition = myElement.getModuleDefinition();
        if (moduleDefinition == null) return Collections.emptyList();
        List<C3PsiElement> result = new ArrayList<>();
        List<C3PsiElement> others = new ArrayList<>();
        ModuleName here;
        try
        {
            here = ModuleName.from(myElement);
        }
        catch (Exception e)
        {
            here = null;
        }
        for (C3MacroDefinition macro : findMacrosByName(name, myElement))
        {
            if (!moduleDefinition.containsImportOrSameModule(macro)) continue;
            if (macro.getModuleName() != null && macro.getModuleName().equals(here))
            {
                if (!result.contains(macro)) result.add(macro);
            }
            else if (!others.contains(macro))
            {
                others.add(macro);
            }
        }
        result.addAll(others);
        return result;
    }

    static @NotNull List<C3MacroDefinition> findMacrosByName(@NotNull String name, @NotNull C3PathAtIdent context)
    {
        // Macro index keys keep the `@`, e.g. `test::@swap`, same as the call text.
        List<C3MacroDefinition> result = new ArrayList<>();
        com.intellij.openapi.project.Project project = context.getProject();
        if (com.intellij.openapi.project.DumbService.isDumb(project)) return result;
        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (!key.equals(name) && !key.endsWith("::" + name)) continue;
            Collection<C3PsiElement> elements;
            try
            {
                elements = StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class);
            }
            catch (Exception e)
            {
                continue;
            }
            for (C3PsiElement element : elements)
            {
                if (element instanceof C3MacroDefinition macro && !result.contains(macro))
                {
                    result.add(macro);
                }
            }
        }
        return result;
    }

    @Override
    public @NotNull TextRange getRangeInElement()
    {
        // No ElementManipulator is registered for C3PathAtIdent; the element is just the name.
        return new TextRange(0, Math.max(0, myElement.getTextLength()));
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element)
    {
        if (element instanceof C3MacroDefinition macro)
        {
            String target = "@" + (macro.getNameIdent() != null ? macro.getNameIdent().replaceFirst("^@", "") : "");
            return myElement.getText().strip().equals(target)
                || myElement.getText().strip().endsWith("::" + target);
        }
        return super.isReferenceTo(element);
    }
}
