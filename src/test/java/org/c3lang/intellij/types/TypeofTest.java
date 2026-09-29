package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TypeofTest extends BasePlatformTestCase
{
    public void testTypeofDeclParsesAndInfers()
    {
        List<HighlightInfo> errors = new ArrayList<>();
        for (HighlightInfo info : check("""
            module test;
            macro void fetch_example(int* ptr)
            {
                $typeof(*ptr) old_value;
                $typeof(*ptr) new_value;
                old_value = 1;
                new_value = old_value;
                int x = old_value;
            }
            """))
        {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                errors.add(info);
            }
        }
        assertTrue("Unexpected errors, got: " + errors, errors.isEmpty());
    }

    public void testTypeofMismatchStillCaught()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            macro void fetch_example(int* ptr)
            {
                $typeof(*ptr) old_value;
                old_value = "s";
            }
            """), "Cannot assign 'String' to 'int'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testTypeofAsCastTargetOk()
    {
        List<HighlightInfo> errors = new ArrayList<>();
        for (HighlightInfo info : check("""
            module test;
            macro void fetch_example(int* ptr)
            {
                $typeof(*ptr) old_value;
                old_value = bitcast(1, $typeof(*ptr));
            }
            fn int bitcast(int x, $Type t);
            """))
        {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                errors.add(info);
            }
        }
        assertTrue("Unexpected errors, got: " + errors, errors.isEmpty());
    }

    public void testUppercaseTypeofStillWorks()
    {
        List<HighlightInfo> errors = new ArrayList<>();
        for (HighlightInfo info : check("""
            module test;
            fn void foo(int* ptr)
            {
                $Typeof(*ptr) x;
                x = 1;
            }
            """))
        {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                errors.add(info);
            }
        }
        assertTrue("Unexpected errors, got: " + errors, errors.isEmpty());
    }

    private @NotNull List<HighlightInfo> check(@NotNull String code)
    {
        myFixture.configureByText("main.c3", code);
        return myFixture.doHighlighting();
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
}
