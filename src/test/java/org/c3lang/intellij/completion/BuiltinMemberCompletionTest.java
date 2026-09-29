package org.c3lang.intellij.completion;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuiltinMemberCompletionTest extends BasePlatformTestCase
{
    public void testAnyFields()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(any arg)
            {
                arg.<caret>
            }
            """);
        assertTrue("any.ptr, got: " + lookup, lookup.contains("ptr"));
        assertTrue("any.type, got: " + lookup, lookup.contains("type"));
    }

    public void testSliceFields()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(int[] s, String str)
            {
                s.<caret>
            }
            """);
        assertTrue("slice len, got: " + lookup, lookup.contains("len"));
        assertTrue("slice ptr, got: " + lookup, lookup.contains("ptr"));
    }

    public void testTypeidValueProperties()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(any arg)
            {
                arg.type.<caret>
            }
            """);
        assertTrue("typeid inner, got: " + lookup, lookup.contains("inner"));
        assertTrue("typeid kindof, got: " + lookup, lookup.contains("kindof"));
        assertTrue("typeid sizeof, got: " + lookup, lookup.contains("sizeof"));
    }

    public void testIntMethods()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(int i)
            {
                i.<caret>
            }
            """);
        assertTrue("popcount, got: " + lookup, lookup.contains("popcount"));
        assertTrue("overflow_add, got: " + lookup, lookup.contains("overflow_add"));
        assertTrue("rotl, got: " + lookup, lookup.contains("rotl"));
        assertFalse("no byteswap (does not exist), got: " + lookup, lookup.contains("byteswap"));
    }

    public void testFloatMethods()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(double d)
            {
                d.<caret>
            }
            """);
        assertTrue("floor, got: " + lookup, lookup.contains("floor"));
        assertTrue("fma, got: " + lookup, lookup.contains("fma"));
        assertFalse("no is_nan (does not exist), got: " + lookup, lookup.contains("is_nan"));
        assertFalse("no sqrt (does not exist), got: " + lookup, lookup.contains("sqrt"));
    }

    public void testCharMethods()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(char c)
            {
                c.<caret>
            }
            """);
        assertTrue("is_digit, got: " + lookup, lookup.contains("is_digit"));
        assertTrue("to_lower, got: " + lookup, lookup.contains("to_lower"));
    }

    public void testTypeNameProperties()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo()
            {
                int.<caret>
            }
            """);
        assertTrue("sizeof, got: " + lookup, lookup.contains("sizeof"));
        assertTrue("min, got: " + lookup, lookup.contains("min"));
        assertTrue("max, got: " + lookup, lookup.contains("max"));
        assertTrue("typeid, got: " + lookup, lookup.contains("typeid"));
    }

    public void testStructTypeNameProperties()
    {
        List<String> lookup = complete("""
            module test;
            struct Foo
            {
                int x;
            }
            fn void foo()
            {
                Foo.<caret>
            }
            """);
        assertTrue("members, got: " + lookup, lookup.contains("members"));
        assertTrue("sizeof, got: " + lookup, lookup.contains("sizeof"));
    }

    public void testVectorSwizzle()
    {
        List<String> lookup = complete("""
            module test;
            fn void foo(float[<4>] v)
            {
                v.<caret>
            }
            """);
        assertTrue("swizzle x, got: " + lookup, lookup.contains("x"));
        assertTrue("swizzle w, got: " + lookup, lookup.contains("w"));
        assertTrue("vector sum, got: " + lookup, lookup.contains("sum"));
    }

    private @NotNull List<String> complete(@NotNull String code)
    {
        myFixture.configureByText("main.c3", code);
        myFixture.completeBasic();
        List<String> lookup = myFixture.getLookupElementStrings();
        assertNotNull("Lookup should not be null", lookup);
        return lookup;
    }
}
