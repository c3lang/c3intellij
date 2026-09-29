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

public class InterfaceContractTest extends BasePlatformTestCase
{
    private static final String INTERFACE = """
        module test;

        interface MyName
        {
        \tfn String myname();
        \tfn void greet(String greeting);
        }
        """;

    public void testContractNavigatesToInterface()
    {
        myFixture.configureByText("main.c3", INTERFACE + """
            
            struct Baz (My<caret>Name)
            {
            \tint x;
            }
            """);

        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        assertTrue("Expected a poly-variant reference, got: " + reference.getClass(), reference instanceof PsiPolyVariantReference);
        List<PsiElement> resolved = new ArrayList<>();
        for (ResolveResult result : ((PsiPolyVariantReference) reference).multiResolve(false))
        {
            if (result.getElement() != null) resolved.add(result.getElement());
        }
        assertEquals(describe(resolved), 1, resolved.size());
        assertTrue(resolved.get(0) instanceof C3InterfaceDefinition);
        assertEquals("MyName", ((C3InterfaceDefinition) resolved.get(0)).getTypeName().getText());
    }

    public void testUnresolvedContractIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;

            struct Baz (Nope)
            {
            \tint x;
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Unresolved interface 'Nope'");
        assertEquals("Expected one unresolved-interface error, got: " + errors, 1, errors.size());
    }

    public void testNonInterfaceContractIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;

            struct Other
            {
            \tint y;
            }

            struct Baz (Other)
            {
            \tint x;
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "'Other' is not an interface.");
        assertEquals("Expected one not-an-interface error, got: " + errors, 1, errors.size());
    }

    public void testExistingContractHasNoErrors()
    {
        myFixture.configureByText("main.c3", INTERFACE + """
            
            struct Baz (MyName)
            {
            \tint x;
            }

            fn String Baz.myname(Baz* self) @dynamic
            {
            \treturn "Baz";
            }

            fn void Baz.greet(Baz* self, String greeting) @dynamic
            {
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected unresolved-interface error, got: " + highlights,
            errorsWithText(highlights, "Unresolved interface").isEmpty());
        assertTrue("Unexpected not-an-interface error, got: " + highlights,
            errorsWithText(highlights, "is not an interface").isEmpty());
    }

    public void testImplementInterfaceMethodsFix()
    {
        myFixture.configureByText("main.c3", INTERFACE + """
            
            struct Ba<caret>z (MyName)
            {
            \tint x;
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "does not implement interface method");
        assertEquals("Expected two missing-impl errors, got: " + errors, 2, errors.size());

        myFixture.launchAction(myFixture.findSingleIntention("Implement methods of 'MyName'"));
        myFixture.checkResult(INTERFACE + """
            
            struct Baz (MyName)
            {
            \tint x;
            }

            fn String Baz.myname(&self) @dynamic
            {
            \t
            }

            fn void Baz.greet(&self, String greeting) @dynamic
            {
            \t
            }
            """);
    }

    public void testAmpSelfImplHasNoErrors()
    {
        myFixture.configureByText("main.c3", INTERFACE + """
            
            struct Baz (MyName)
            {
            \tint x;
            }

            fn String Baz.myname(&self) @dynamic
            {
            \treturn "Baz";
            }

            fn void Baz.greet(&self, String greeting) @dynamic
            {
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected @dynamic error, got: " + highlights,
            errorsWithText(highlights, "@dynamic").isEmpty());
        assertTrue("Unexpected missing-self error, got: " + highlights,
            errorsWithText(highlights, "must start with an argument").isEmpty());
        assertTrue("Unexpected missing-impl error, got: " + highlights,
            errorsWithText(highlights, "does not implement").isEmpty());
    }

    public void testImplementInterfaceMethodsFixPreview()
    {
        myFixture.configureByText("main.c3", INTERFACE + """
            
            struct Baz (MyName)
            {
            \tint x;
            }
            """);

        C3StructDeclaration structDecl = null;
        for (C3StructDeclaration candidate : com.intellij.psi.util.PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3StructDeclaration.class))
        {
            if ("Baz".equals(candidate.getTypeName().getText())) structDecl = candidate;
        }
        assertNotNull(structDecl);
        org.c3lang.intellij.annotation.fix.ImplementInterfaceMethodsFix fix =
            new org.c3lang.intellij.annotation.fix.ImplementInterfaceMethodsFix(
                structDecl,
                FullyQualifiedName.parse("test::MyName"));
        com.intellij.codeInsight.intention.preview.IntentionPreviewInfo preview =
            fix.generatePreview(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile());
        assertTrue("Expected a diff preview, got: " + preview,
            preview instanceof com.intellij.codeInsight.intention.preview.IntentionPreviewInfo.CustomDiff);
        String modified = ((com.intellij.codeInsight.intention.preview.IntentionPreviewInfo.CustomDiff) preview).modifiedText();
        assertTrue("Preview must contain generated myname impl, got:\n" + modified,
            modified.contains("fn String Baz.myname(&self) @dynamic"));
        assertTrue("Preview must contain generated greet impl, got:\n" + modified,
            modified.contains("fn void Baz.greet(&self, String greeting) @dynamic"));
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
