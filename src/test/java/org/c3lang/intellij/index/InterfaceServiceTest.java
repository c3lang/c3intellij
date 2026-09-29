package org.c3lang.intellij.index;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3TypeName;

import java.util.List;
import java.util.stream.Collectors;

public class InterfaceServiceTest extends BasePlatformTestCase
{
    public void testFindModuleTypeDeclarations()
    {
        myFixture.configureByText("main.c3", """
            module testproject;

            fn void main()
            {
            }
            """);
        myFixture.doHighlighting();

        List<C3TypeName> declarations = InterfaceService.INSTANCE.findModuleTypeDeclarations(
            "std::collections::list", myFixture.getProject());
        List<String> names = declarations.stream()
            .map(typeName -> typeName.getFqName().getName())
            .collect(Collectors.toList());
        assertTrue("Should find List in std::collections::list, got: " + names, names.contains("List"));
    }

    public void testFindModuleCallables()
    {
        myFixture.configureByText("main.c3", """
            module testproject;

            fn void main()
            {
            }
            """);
        myFixture.doHighlighting();

        List<C3CallablePsiElement> callables = InterfaceService.INSTANCE.findModuleCallables(
            "std::io", myFixture.getProject());
        List<String> names = callables.stream()
            .map(callable -> callable.getFqName().getName())
            .collect(Collectors.toList());
        assertFalse("Should find callables in std::io, got empty", names.isEmpty());
        assertTrue("Should find printn in std::io, got: " + names.stream().limit(20).collect(Collectors.toList()),
            names.contains("printn"));
    }
}
