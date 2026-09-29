package org.c3lang.intellij.psi;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StreamRegressionTest extends BasePlatformTestCase
{
    public void testStreamFileParsesAndResolves()
    {
        String text;
        try
        {
            Path candidate = Path.of("C:", "Program Files", "c3", "lib", "std", "io", "stream.c3");
            if (!Files.isRegularFile(candidate)) return;
            text = Files.readString(candidate);
        }
        catch (Exception e)
        {
            return;
        }

        myFixture.configureByText("stream.c3", text);

        List<String> errors = new ArrayList<>();
        for (PsiErrorElement error : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), PsiErrorElement.class))
        {
            errors.add(error.getErrorDescription() + " :: " + error.getText());
        }
        assertTrue("Parse errors in stream.c3: " + errors.subList(0, Math.min(10, errors.size())), errors.isEmpty());
    }

    public void testIszKeywordParsesAsType()
    {
        myFixture.configureByText("main.c3", """
            module test;

            interface InStream
            {
            \tfn usz? seek(isz offset, Seek seek) @optional;
            }
            """);

        List<String> errors = new ArrayList<>();
        for (PsiErrorElement error : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), PsiErrorElement.class))
        {
            errors.add(error.getErrorDescription() + " :: " + error.getText());
        }
        assertTrue("Parse errors: " + errors, errors.isEmpty());

        List<String> unresolved = new ArrayList<>();
        myFixture.doHighlighting().forEach(info -> {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null
                && info.getDescription().contains("Unresolved interface"))
            {
                unresolved.add(info.getDescription());
            }
        });
        assertTrue("Unexpected unresolved interfaces: " + unresolved, unresolved.isEmpty());
    }

    public void testDocContractWithAmpersand()
    {
        myFixture.configureByText("main.c3", """
            module test;

            <*
             @param [&out] ref
             @require @is_instream(stream) : "Expected a stream"
            *>
            macro usz? read_any(stream, any ref)
            {
            \treturn 0;
            }
            """);

        List<String> errors = new ArrayList<>();
        myFixture.doHighlighting().forEach(info -> {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null
                && info.getDescription().contains("no argument named"))
            {
                errors.add(info.getDescription());
            }
        });
        assertTrue("Unexpected doc errors: " + errors, errors.isEmpty());
    }

    public void testMacroCallErrorMentionsRawName()
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
                @swap(a, unknown_arg: 1);
            }
            """);

        List<String> descriptions = new ArrayList<>();
        myFixture.doHighlighting().forEach(info -> {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                descriptions.add(info.getDescription());
            }
        });
        assertTrue("Expected unknown-parameter error mentioning 'unknown_arg', got: " + descriptions,
            descriptions.stream().anyMatch(d -> d.contains("unknown_arg")));
    }
}
