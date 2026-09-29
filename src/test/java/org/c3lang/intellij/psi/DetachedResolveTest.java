package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Collection;

/**
 * Resolving on detached elements (copies used by intention previews and
 * quick fixes, fragments without a module section) must degrade to
 * "unresolved" instead of throwing from {@code getModuleDefinition()}.
 */
public class DetachedResolveTest extends BasePlatformTestCase
{
    public void testDetachedBaseTypeResolveDoesNotThrow()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Shape { Foo x; }
            fn void foo()
            {
                Foo y;
                foo();
            }
            """);
        Collection<C3BaseType> bases = PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3BaseType.class);
        assertFalse("Expected base types in fixture", bases.isEmpty());
        for (C3BaseType base : bases)
        {
            PsiElement copy = base.copy();
            // Copies live in a DummyHolder without any module section.
            assertNull(((C3PsiElement) copy).getModuleDefinition());
            PsiReference reference = ((C3BaseType) copy).getReference();
            if (reference != null)
            {
                // Must return null, not throw NullPointerException.
                assertNull(reference.resolve());
            }
        }
    }

    public void testDetachedPathIdentResolveDoesNotThrow()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void foo()
            {
                bar();
            }
            """);
        Collection<C3PathIdent> idents = PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3PathIdent.class);
        assertFalse("Expected path idents in fixture", idents.isEmpty());
        for (C3PathIdent ident : idents)
        {
            PsiElement copy = ident.copy();
            assertNull(((C3PsiElement) copy).getModuleDefinition());
            for (PsiReference reference : ((C3PathIdent) copy).getReferences())
            {
                reference.resolve();
            }
        }
    }
}
