package org.c3lang.intellij.psi;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MacroSupportTest extends BasePlatformTestCase
{
    public void testAtMacroCallNavigatesToDefinition()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @myswap(#a, #b)
            {
                var temp = #a;
            }
            fn void test()
            {
                int a = 10;
                int b = 20;
                @mysw<caret>ap(a, b);
            }
            """);

        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        assertTrue("Expected a poly-variant reference, got: " + reference.getClass(),
            reference instanceof PsiPolyVariantReference);
        List<PsiElement> resolved = new ArrayList<>();
        for (ResolveResult result : ((PsiPolyVariantReference) reference).multiResolve(false))
        {
            if (result.getElement() != null) resolved.add(result.getElement());
        }
        assertEquals(describe(resolved), 1, resolved.size());
        assertTrue(resolved.get(0) instanceof C3MacroDefinition);
        assertEquals("@myswap", ((C3MacroDefinition) resolved.get(0)).getName());
    }

    public void testTrailingBlockReferenceDoesNotResolve()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @foreach(a; @body(index, value))
            {
                @bo<caret>dy(index, value);
            }
            """);

        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        List<PsiElement> resolved = new ArrayList<>();
        if (reference instanceof PsiPolyVariantReference poly)
        {
            for (ResolveResult result : poly.multiResolve(false))
            {
                if (result.getElement() != null) resolved.add(result.getElement());
            }
        }
        else if (reference.resolve() != null)
        {
            resolved.add(reference.resolve());
        }
        assertTrue("Trailing block call must not resolve globally, got: " + describe(resolved), resolved.isEmpty());
    }

    public void testMacroMethodCallResolves()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Foo
            {
                int x;
            }
            macro Foo.generate(&self)
            {
            }
            fn void test()
            {
                Foo f;
                f.gene<caret>rate();
            }
            """);

        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();
        assertNotNull("Macro method call should resolve, got null", resolved);
        assertTrue("Expected macro, got: " + resolved, resolved instanceof C3MacroDefinition);
    }

    public void testAtMacroCallArgChecks()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @swap(#a, #b)
            {
            }
            fn void test()
            {
                int a = 10;
                int b = 20;
                @swap(a);
                @swap(a, b, a);
            }
            """);

        List<HighlightInfo> missing = errorsWithText(myFixture.doHighlighting(), "Missing argument for parameter 'b'.");
        assertEquals("Expected one missing-arg error, got: " + missing, 1, missing.size());
        List<HighlightInfo> many = errorsWithText(myFixture.doHighlighting(), "Too many arguments.");
        assertEquals("Expected one too-many error, got: " + many, 1, many.size());
    }

    public void testAtMacroCallCorrectOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @swap(#a, #b)
            {
                var temp = #a;
            }
            macro int typed(int x)
            {
                return x;
            }
            fn void test()
            {
                int a = 10;
                int b = 20;
                @swap(a, b);
                int y = typed(a);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testMacroTrailingBlockChecks()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @foreach(a; @body(index, value))
            {
            }
            macro void plain(int x)
            {
            }
            fn void test()
            {
                int[] a = { 1, 2 };
                @foreach(a);
                plain(1) { };
            }
            """);

        List<HighlightInfo> missing = errorsWithText(myFixture.doHighlighting(), "Missing trailing block.");
        assertEquals("Expected one missing-block error, got: " + missing, 1, missing.size());
        List<HighlightInfo> extra = errorsWithText(myFixture.doHighlighting(), "takes no trailing block.");
        assertEquals("Expected one unexpected-block error, got: " + extra, 1, extra.size());
    }

    public void testMacroDuplicateIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro m(x)
            {
            }
            macro m(x)
            {
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Macro 'm' is already defined.");
        assertEquals("Expected one duplicate error, got: " + errors, 1, errors.size());
    }

    public void testAtMacroCompletion()
    {
        myFixture.configureByText("main.c3", """
            module test;
            macro void @swap(#a, #b)
            {
            }
            fn void test()
            {
                int a = 10;
                int b = 20;
                @sw<caret>
            }
            """);

        myFixture.completeBasic();
        List<String> lookupStrings = myFixture.getLookupElementStrings();
        assertNotNull("Lookup strings should not be null", lookupStrings);
        assertTrue("Should suggest '@swap', got: " + lookupStrings, lookupStrings.contains("@swap"));
    }

    private static @NotNull List<HighlightInfo> callErrors(@NotNull List<HighlightInfo> highlights)
    {
        List<HighlightInfo> result = new ArrayList<>();
        for (HighlightInfo info : highlights)
        {
            if (info.getSeverity() != HighlightSeverity.ERROR || info.getDescription() == null) continue;
            String description = info.getDescription();
            if (description.contains("Unknown parameter")
                || description.contains("already set")
                || description.contains("may not follow named arguments")
                || description.contains("Too many arguments")
                || description.contains("Missing argument")
                || description.contains("Missing trailing block")
                || description.contains("takes no trailing block")
                || description.contains("Cannot pass")
                || description.contains("already defined"))
            {
                result.add(info);
            }
        }
        return result;
    }

    private static @NotNull List<HighlightInfo> errorsWithText(@NotNull List<HighlightInfo> highlights, @NotNull String textPart)
    {
        List<HighlightInfo> result = new ArrayList<>();
        for (HighlightInfo info : highlights)
        {
            if (info.getSeverity() == HighlightSeverity.ERROR
                && info.getDescription() != null
                && info.getDescription().contains(textPart))
            {
                result.add(info);
            }
        }
        return result;
    }

    private static @NotNull String describe(@NotNull List<PsiElement> elements)
    {
        List<String> descriptions = new ArrayList<>();
        for (PsiElement element : elements)
        {
            descriptions.add(element.getClass().getSimpleName() + ": " + element.getText());
        }
        return String.join("\n", descriptions);
    }
}
