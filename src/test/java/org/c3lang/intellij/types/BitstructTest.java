package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BitstructTest extends BasePlatformTestCase
{
    private static final String HEADER = """
        module test;
        bitstruct Sb : char
        {
            int a : 0..2;
            bool b : 3;
        }
        """;

    public void testFieldReadHasDeclaredType()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                Sb s;
                int x = s.a;
                bool y = s.b;
            }
            """);
    }

    public void testFieldAssignWrongTypeIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb s;
                s.a = true;
            }
            """), "Cannot assign");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testConstantTooWideIsTruncationError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb s;
                s.a = 4;
            }
            """), "truncated");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testFittingConstantIsOk()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                Sb s;
                s.a = 3;
                s.a += 1;
                s.a++;
            }
            """);
    }

    public void testCastToBackingIsOk()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                Sb s;
                char c = (char)s;
                int i = (int)s;
            }
            """);
    }

    public void testCastFromWrongTypeIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo(uint u)
            {
                Sb s = (Sb)u;
            }
            """), "not possible to cast");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testImplicitConversionIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb s;
                int i = s;
            }
            """), "Implicitly casting");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testAddressOfFieldIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb s;
                char* p = &s.a;
            }
            """), "address of a bitstruct");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testAddressOfWholeIsOk()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                Sb s;
                Sb* p = &s;
            }
            """);
    }

    public void testUnknownMemberIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb s;
                int x = s.zzz;
            }
            """), "no field");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testSizeofUsesBacking()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                usz z = Sb.sizeof;
                usz a = Sb.alignof;
            }
            """);
    }

    public void testDesignatedInitOk()
    {
        assertNoErrors(HEADER + """
            fn void foo()
            {
                Sb t = { .a = 2, .b = true };
                Sb u = { .a = 2, .b };
            }
            """);
    }

    public void testDesignatedInitUnknownMemberIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb t = { .zzz = 1 };
            }
            """), "not a valid member");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testDesignatedInitTooWideIsTruncationError()
    {
        List<HighlightInfo> errors = errorsWithText(check(HEADER + """
            fn void foo()
            {
                Sb t = { .a = 5 };
            }
            """), "truncated");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testBitwiseStaysInBitstruct()
    {
        assertNoErrors("""
            module test;
            bitstruct BitMask : uint
            {
                bool abc : 0;
                bool def : 1;
                bool active : 5;
            }
            fn void foo()
            {
                BitMask a = { .abc, .def };
                BitMask b = { .active, .abc };
                BitMask c = a & b;
                bool x = c.active;
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
