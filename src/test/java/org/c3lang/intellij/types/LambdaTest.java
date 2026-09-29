package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Anonymous functions (`fn (i) => i * i`) and expression bodies
 * (`fn int f(int x) => x * x`): scope rules (no capture), expected-type
 * parameter inference and body checking, all verified against {@code c3c}.
 */
public class LambdaTest extends BasePlatformTestCase
{
    private static final String PRELUDE = """
        module test;
        alias IntTransform = fn int(int);
        fn void apply(int[] arr, IntTransform t)
        {
        }
        """;

    public void testLambdaArgOk()
    {
        assertNoErrors(PRELUDE + """
            fn void main()
            {
                int[] x = { 1, 2, 5 };
                apply(x, fn (i) => i * i);
                apply(x, fn int(int i) => i * i);
            }
            """);
    }

    public void testExpressionBodyOk()
    {
        assertNoErrors("""
            module test;
            fn int square_short(int x) => x * x;
            """);
    }

    public void testExpressionBodyReturnMismatchIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn int bad(int x) => "s";
            """), "Cannot return 'String' from function returning 'int'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaCaptureLocalIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(PRELUDE + """
            fn void main()
            {
                int outer = 1;
                int[] x = { 1 };
                apply(x, fn (i) => i + outer);
            }
            """), "Cannot capture 'outer'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaCaptureParamIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(PRELUDE + """
            fn void main(int k)
            {
                int[] x = { 1 };
                apply(x, fn (i) => i + k);
            }
            """), "Cannot capture 'k'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaCaptureSelfIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            alias IntTransform = fn int(int);
            fn void apply(int[] arr, IntTransform t)
            {
            }
            struct Foo
            {
                int v;
            }
            fn int Foo.run(&self)
            {
                int[] x = { 1 };
                apply(x, fn (i) => i + self.v);
                return 0;
            }
            """), "Cannot capture 'self'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaGlobalOk()
    {
        assertNoErrors(PRELUDE + """
            const int G = 5;
            fn void main()
            {
                int[] x = { 1 };
                apply(x, fn (i) => i + G);
            }
            """);
    }

    public void testLambdaBodyUsesExpectedParamType()
    {
        // `i` infers as `int` from `IntTransform`: returning it where a
        // `String` is expected must fail.
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            alias IntTransform = fn int(int);
            alias StrTransform = fn String(int);
            fn void apply(int[] arr, IntTransform t)
            {
            }
            fn void apply_str(int[] arr, StrTransform t)
            {
            }
            fn void main()
            {
                int[] x = { 1 };
                apply_str(x, fn (i) => i);
            }
            """), "Cannot cast 'int' to 'String'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaWrongReturnIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(PRELUDE + """
            fn void main()
            {
                int[] x = { 1 };
                apply(x, fn (i) => "s");
            }
            """), "Cannot cast 'String' to 'int'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaArityMismatchIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check(PRELUDE + """
            fn void main()
            {
                int[] x = { 1 };
                apply(x, fn (a, b) => a);
            }
            """), "doesn't match the required type 'IntTransform' (fn int(int))");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLambdaShadowedOuterNameOk()
    {
        // Mirrors the reporting user's bformat: the lambda declares its own
        // `buffer`, so uses resolve inside the lambda (no capture of the
        // outer parameter). Byte-faithful: fault suffix `~`, `!!`, slice,
        // explicit cast, cross-module `io::` reference, `@format`.
        myFixture.addFileToProject("io.c3", """
            module io;
            fault BufferExceeded;
            """);
        assertNoErrors("""
            module test;
            alias OutputFn = fn void?(void* buf, char c);
            struct Formatter
            {
                int marker;
            }
            fn void Formatter.init(&self, OutputFn f, char[]* p)
            {
            }
            fn usz? Formatter.vprintf(&self, String fmt, args...)
            {
                return 0;
            }
            fn String bformat(char[] buffer, String fmt, args...) @format(1)
            {
                Formatter f;
                OutputFn format_fn = fn void?(void* buf, char c) {
                    char[]* buffer_ref = buf;
                    char[] buffer = *buffer_ref;
                    if (buffer.len == 0) return io::BufferExceeded~;
                    buffer[0] = c;
                    *buffer_ref = buffer[1..];
                };
                char[] buffer_copy = buffer;
                f.init(format_fn, &buffer_copy);
                usz len = f.vprintf(fmt, args)!!;
                return (String)buffer[:len];
            }
            """);
    }

    public void testLambdaHashParamInParamTypeOk()
    {
        // A `#`-macro-parameter inside a lambda parameter type
        // (`$typeof(#array[0])`) is folded before lambda analysis: c3c
        // accepts it, so it must not be flagged as a capture.
        assertNoErrors("""
            module test;
            macro typeid @predicate_fn(#array) @const
            {
                return $typeof(fn bool ($typeof(#array[0]) a, usz index = 0) => true).typeid;
            }
            fn void test()
            {
                int[4] arr = { 1, 2, 3, 4 };
                typeid t = @predicate_fn(arr);
            }
            """);
    }

    public void testLambdaComptimeTypeParamInParamTypeOk()
    {
        // Same for `$`-type-parameters (verified against c3c).
        assertNoErrors("""
            module test;
            macro typeid @pred3($Type) @const
            {
                return $typeof(fn bool ($Type a) => true).typeid;
            }
            fn void test()
            {
                typeid t = @pred3(int);
            }
            """);
    }

    public void testLambdaHashParamInBodyIsError()
    {
        // In the lambda body a `#`-parameter is still unresolvable
        // (c3c reports it as not found), so the capture error stays.
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            macro typeid @predicate_fn(#array) @const
            {
                return $typeof(fn bool (int a) => a == #array[0]).typeid;
            }
            """), "Cannot capture '#array'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
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
