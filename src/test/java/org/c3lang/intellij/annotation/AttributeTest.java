package org.c3lang.intellij.annotation;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AttributeTest extends BasePlatformTestCase
{
    public void testUnknownAttributeIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo() @frobnicate
            {
            }
            """), "Unknown attribute '@frobnicate'.");
        assertEquals("Expected one unknown-attribute error, got: " + errors, 1, errors.size());
    }

    public void testUserAttributeIsKnown()
    {
        assertNoAttributeErrors("""
            module test;
            attrdef @MyAttr = @inline;
            fn void foo() @MyAttr
            {
            }
            """);
    }

    public void testContractAttributesAreKnown()
    {
        assertNoAttributeErrors("""
            module test;
            fn void foo(int x) @require(x > 0) @ensure(x > 0) @param(x) @return
            {
            }
            """);
    }

    public void testMisplacedAttributeIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct Foo @benchmark
            {
                int x;
            }
            """), "'@benchmark' cannot be used on struct.");
        assertEquals("Expected one placement error, got: " + errors, 1, errors.size());
    }

    public void testNoaliasOnFunctionIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo(int x) @noalias
            {
            }
            """), "'@noalias' cannot be used on function.");
        assertEquals("Expected one placement error, got: " + errors, 1, errors.size());
    }

    public void testConstOnMacroMethodIsOk()
    {
        assertNoAttributeErrors("""
            module test;
            struct AsciiCharset
            {
                char x;
            }
            macro bool AsciiCharset.@contains($set, char $c) @const => true;
            """);
    }

    public void testConstOnPlainMacroIsOk()
    {
        assertNoAttributeErrors("""
            module test;
            macro bool is_alpha(char c) @const => true;
            """);
    }

    public void testConstOnFnMethodIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct AsciiCharset
            {
                char x;
            }
            fn bool AsciiCharset.contains(AsciiCharset* self) @const
            {
                return true;
            }
            """), "'@const' cannot be used on method.");
        assertEquals("Expected one placement error, got: " + errors, 1, errors.size());
    }

    public void testInitSignatureIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn int bad() @init
            {
                return 1;
            }
            """), "'@init' requires a plain function without parameters returning void.");
        assertEquals("Expected one signature error, got: " + errors, 1, errors.size());
    }

    public void testInitSignatureOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void good() @init(100)
            {
            }
            """);
    }

    public void testTestSignatureIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn int bad(int x) @test
            {
                return x;
            }
            """), "'@test' requires a plain function without parameters returning void.");
        assertEquals("Expected one signature error, got: " + errors, 1, errors.size());
    }

    public void testWinmainOnlyOnMain()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo() @winmain
            {
            }
            """), "'@winmain' is only valid for the 'main' function.");
        assertEquals("Expected one winmain error, got: " + errors, 1, errors.size());
    }

    public void testWinmainOnMainOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void main() @winmain
            {
            }
            """);
    }

    public void testCallconvArgIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo() @callconv("fastcall")
            {
            }
            """), "@callconv must be one of");
        assertEquals("Expected one callconv error, got: " + errors, 1, errors.size());
    }

    public void testCallconvOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void foo() @callconv("stdcall")
            {
            }
            """);
    }

    public void testSafemacroDropsAtRequirement()
    {
        assertNoAttributeErrors("""
            module test;
            macro foo(&x) @safemacro
            {
            }
            """);
    }

    public void testOptionalMethodNeedsNoImpl()
    {
        List<HighlightInfo> highlights = check("""
            module test;
            interface Printer
            {
                fn void print();
                fn void setup() @optional;
            }
            struct Device (Printer)
            {
                int x;
            }
            fn void Device.print(&self) @dynamic
            {
            }
            """);
        assertTrue("Unexpected missing-impl error, got: " + highlights,
            errorsWithText(highlights, "does not implement").isEmpty());
    }

    public void testDeprecatedUseWarns()
    {
        List<HighlightInfo> warnings = warningsWithText(check("""
            module test;
            fn void old() @deprecated("use newf instead")
            {
            }
            fn void newf()
            {
            }
            fn void foo()
            {
                old();
                newf();
            }
            """), "deprecated");
        assertEquals("Expected one deprecation warning, got: " + warnings, 1, warnings.size());
        assertTrue("Warning should carry the message, got: " + warnings.get(0).getDescription(),
            warnings.get(0).getDescription() != null
                && warnings.get(0).getDescription().contains("use newf instead"));
    }

    public void testAllowDeprecatedSuppressesWarning()
    {
        List<HighlightInfo> warnings = warningsWithText(check("""
            module test;
            fn void old() @deprecated
            {
            }
            fn void foo() @allow_deprecated
            {
                old();
            }
            """), "deprecated");
        assertTrue("Unexpected deprecation warning, got: " + warnings, warnings.isEmpty());
    }

    public void testNodiscardDiscardWarns()
    {
        List<HighlightInfo> warnings = warningsWithText(check("""
            module test;
            fn int get() @nodiscard
            {
                return 1;
            }
            fn void foo()
            {
                get();
                int x = get();
            }
            """), "must not be discarded");
        assertEquals("Expected one discard warning, got: " + warnings, 1, warnings.size());
    }

    public void testExternAndStructlikeAreKnown()
    {
        assertNoAttributeErrors("""
            module test;
            extern fn void debugBreak() @extern("DebugBreak");
            typedef Time @structlike = long;
            """);
    }

    public void testTestOnOptionalReturnOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void? test_div() @test
            {
            }
            """);
    }

    public void testCallAttributesOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void set_date(int y) {}
            struct Dings { int x; }
            fn void foo()
            {
                Dings d;
                d.set_date(2024) @inline;
            }
            """);
    }

    public void testUnknownCallAttributeIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo()
            {
                foo() @frobnicate;
            }
            """), "Unknown call attribute '@frobnicate'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testRequiredArgIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct Foo @align
            {
                int x;
            }
            """), "'@align' requires an argument.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testAlignPowerOfTwo()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct Foo @align(24)
            {
                int x;
            }
            """), "'@align' requires a power-of-two argument.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testAlignOk()
    {
        assertNoAttributeErrors("""
            module test;
            struct Foo @align(32)
            {
                int a;
                int b @align(16);
            }
            """);
    }

    public void testInitPriorityRange()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void early() @init(0)
            {
            }
            """), "priority must be between 1 and 65535");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testFormatSignatureOk()
    {
        assertNoAttributeErrors("""
            module test;
            fn void log(String format, args...) @format(0)
            {
            }
            """);
    }

    public void testFormatIndexOutOfRange()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void log(String format, args...) @format(2)
            {
            }
            """), "'@format' index is out of range");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testNoaliasNonPointerIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void copy(int x @noalias)
            {
            }
            """), "'@noalias' requires a pointer parameter.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testNosanitizeRequiresArg()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo() @nosanitize
            {
            }
            """), "'@nosanitize' requires a check name");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testStackValueIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            fn void foo() @stackprobe("turbo")
            {
            }
            """), "Invalid value");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testSimdLengthIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            typedef Vec3 @simd = int[<3>];
            """), "'@simd' requires a vector type with power-of-two length");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testDynamicOnInterfaceIsError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            interface Speaks
            {
                fn void say();
            }
            struct Dog (Speaks)
            {
                int x;
            }
            fn void Speaks.say(&self) @dynamic
            {
            }
            """), "'@dynamic' cannot be used on methods of 'any' or an interface.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testWeakSupersedesIsNotDuplicate()
    {
        assertNoAttributeErrors("""
            module test;
            fn void helper() @weak
            {
            }
            fn void helper()
            {
            }
            """);
    }

    public void testMustinitRefusesNoinit()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct Guard @mustinit
            {
                int x;
            }
            fn void foo()
            {
                Guard g @noinit;
            }
            """), "'@noinit' cannot be used on type");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testBuiltinOnFaultdefAndAliasOk()
    {
        // Mirrors the stdlib: `faultdef NO_MORE_ELEMENT @builtin;` and
        // `alias mem @builtin = thread_allocator;`.
        assertNoAttributeErrors("""
            module test;
            faultdef NOT_FOUND @builtin;
            alias mem @builtin = allocator;
            """);
    }

    public void testOperatorComparisonArgsOk()
    {
        // Comparison operators are overloadable: `@operator(!=)` and friends
        // must parse as attribute arguments (matrix.c3 `neq` pattern).
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            struct Matrix2x2
            {
                float m;
            }
            fn bool Matrix2x2.neq(&self, Matrix2x2 mat2) @operator(!=) => self.m != mat2.m;
            fn bool Matrix2x2.eq(&self, Matrix2x2 mat2) @operator(==) => self.m == mat2.m;
            fn bool Matrix2x2.lt(&self, Matrix2x2 mat2) @operator(<) => self.m < mat2.m;
            fn bool Matrix2x2.gt(&self, Matrix2x2 mat2) @operator(>) => self.m > mat2.m;
            fn bool Matrix2x2.le(&self, Matrix2x2 mat2) @operator(<=) => self.m <= mat2.m;
            fn bool Matrix2x2.ge(&self, Matrix2x2 mat2) @operator(>=) => self.m >= mat2.m;
            """), "requires an argument");
        assertTrue("Unexpected operator-arg errors, got: " + errors, errors.isEmpty());
    }

    private @NotNull List<HighlightInfo> check(@NotNull String code)
    {
        myFixture.configureByText("main.c3", code);
        return myFixture.doHighlighting();
    }

    private void assertNoAttributeErrors(@NotNull String code)
    {
        List<HighlightInfo> problems = new ArrayList<>();
        for (HighlightInfo info : check(code))
        {
            if (info.getSeverity() == HighlightSeverity.ERROR && info.getDescription() != null)
            {
                String description = info.getDescription();
                if (description.contains("Unknown attribute")
                    || description.contains("cannot be used on")
                    || description.contains("requires a plain function")
                    || description.contains("only valid for the 'main'")
                    || description.contains("@callconv")
                    || description.contains("should have a name starting with '@'"))
                {
                    problems.add(info);
                }
            }
        }
        assertTrue("Unexpected attribute errors, got: " + problems, problems.isEmpty());
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

    private static @NotNull List<HighlightInfo> warningsWithText(@NotNull List<HighlightInfo> highlights, @NotNull String textPart)
    {
        List<HighlightInfo> result = new ArrayList<>();
        for (HighlightInfo info : highlights)
        {
            HighlightSeverity severity = info.getSeverity();
            if ((severity == HighlightSeverity.WARNING || severity == HighlightSeverity.WEAK_WARNING)
                && info.getDescription() != null
                && info.getDescription().contains(textPart))
            {
                result.add(info);
            }
        }
        return result;
    }
}
