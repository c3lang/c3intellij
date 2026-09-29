package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OptionalTest extends BasePlatformTestCase
{
    public void testOptionalDeclOk()
    {
        assertNoErrors("""
            module test;
            faultdef FILE_NOT_FOUND;
            fn void foo()
            {
                int? a = 1;
                int? b = FILE_NOT_FOUND~;
            }
            """);
    }

    public void testOptionalToOptionalOk()
    {
        assertNoErrors("""
            module test;
            fn void foo()
            {
                int? b = 1;
                int? a = b;
            }
            """);
    }

    public void testOptionalToPlainIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn int? maybe();
            fn void foo()
            {
                int x = maybe();
            }
            """), "Cannot assign 'int?' to 'int'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testRethrowUnwrapsOk()
    {
        assertNoErrors("""
            module test;
            fn int? maybe();
            fn void? test()
            {
                int x = maybe()!;
                maybe()!;
            }
            fn void plain()
            {
                maybe()!;
            }
            """);
    }

    public void testForceUnwrapOk()
    {
        assertNoErrors("""
            module test;
            fn int? maybe();
            fn void foo()
            {
                int x = maybe()!!;
            }
            """);
    }

    public void testNullCoalesceOk()
    {
        assertNoErrors("""
            module test;
            fn int? maybe();
            fn void foo()
            {
                int x = maybe() ?? -1;
            }
            """);
    }

    public void testCascadingCallOk()
    {
        assertNoErrors("""
            module test;
            fn int twice(int v);
            fn void foo()
            {
                int? o = 3;
                int? r = twice(o);
            }
            """);
    }

    public void testCascadingResultIsOptional()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn int twice(int v);
            fn void foo()
            {
                int? o = 3;
                int r = twice(o);
            }
            """), "Cannot assign 'int?' to 'int'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testFaultDeclAndCatchOk()
    {
        assertNoErrors("""
            module test;
            faultdef DIVISION_BY_ZERO;
            fn double? divide(int a, int b)
            {
                if (b == 0) return DIVISION_BY_ZERO~;
                return (double)a / (double)b;
            }
            fn void foo()
            {
                double? ratio = divide(1, 2);
                if (catch ex = ratio)
                {
                    String s = ex.description;
                }
                fault f = DIVISION_BY_ZERO;
                bool same = f == DIVISION_BY_ZERO;
            }
            """);
    }

    public void testCatchAtMacroOk()
    {
        assertNoErrors("""
            module test;
            fn int? maybe();
            fn void foo()
            {
                fault ex = @catch(maybe());
            }
            """);
    }

    public void testVoidOptionalReturnOk()
    {
        assertNoErrors("""
            module test;
            faultdef FILE_NOT_FOUND;
            fn void? test(int x)
            {
                if (x == 0) return FILE_NOT_FOUND~;
                return;
            }
            """);
    }

    public void testVoidOptionalReturnValueIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void? test()
            {
                return 5;
            }
            """), "Cannot return 'int' from function returning 'void?'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testVoidOptionalVariableIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void? test()
            {
                void? x;
            }
            """), "void?) cannot be used as a variable type");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testMainOptionalIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void? main()
            {
            }
            """), "The 'main' function cannot return an Optional.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testOptionalWideningOk()
    {
        assertNoErrors("""
            module test;
            fn void foo()
            {
                int? o = 1;
                long? l = o;
            }
            """);
    }

    private @NotNull List<HighlightInfo> check(@NotNull String code)
    {
        myFixture.configureByText("main.c3", code);
        return myFixture.doHighlighting();
    }

    private void assertNoErrors(@NotNull String code)
    {
        List<HighlightInfo> errors = new ArrayList<>();
        for (HighlightInfo info : check(code))
        {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                errors.add(info);
            }
        }
        assertTrue("Unexpected errors, got: " + errors, errors.isEmpty());
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
