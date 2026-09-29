package org.c3lang.intellij.psi.reference;

import com.intellij.psi.PsiElement;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Reference from an interface name in a struct contract, e.g. {@code MyName}
 * in {@code struct Baz (MyName)}, to the {@link C3InterfaceDefinition}.
 */
public class C3InterfaceReference extends C3ReferenceBase<C3TypeName>
{
    public C3InterfaceReference(@NotNull C3TypeName element)
    {
        super(element);
    }

    public static @NotNull FullyQualifiedName interfaceNameOf(@NotNull C3TypeName element)
    {
        String text = element.getText().strip();
        if (text.contains("::")) return FullyQualifiedName.parse(text);
        return new FullyQualifiedName(ModuleName.from(element), text);
    }

    @Override
    public @NotNull Collection<C3PsiElement> multiResolve()
    {
        String text = myElement.getText();
        if (text == null || text.isBlank()) return Collections.emptyList();
        ModuleName contextModule = ModuleName.from(myElement);
        List<ModuleName> imports = ModuleName.getImportList(myElement);
        List<C3PsiElement> result = new ArrayList<>();
        for (FullyQualifiedName candidate : InterfaceService.INSTANCE.contractCandidates(text, contextModule, imports))
        {
            for (C3InterfaceDefinition definition :
                InterfaceService.INSTANCE.findInterfaceDefinitions(candidate, myElement.getProject()))
            {
                if (!result.contains(definition)) result.add(definition);
            }
            if (!result.isEmpty()) return result;
        }
        return result;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element)
    {
        if (element instanceof C3InterfaceDefinition definition)
        {
            String wanted = myElement.getText().strip();
            String defName = definition.getTypeName().getText().strip();
            if (!wanted.endsWith(defName) && !wanted.equals(defName)) return false;
            ModuleName contextModule = ModuleName.from(myElement);
            List<ModuleName> imports = ModuleName.getImportList(myElement);
            for (FullyQualifiedName candidate :
                InterfaceService.INSTANCE.contractCandidates(wanted, contextModule, imports))
            {
                FullyQualifiedName target = new FullyQualifiedName(
                    ModuleName.from(definition), defName);
                if (target.equals(candidate)) return true;
            }
            return false;
        }
        return super.isReferenceTo(element);
    }
}
