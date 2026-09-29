package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComptimeAliasTest extends BasePlatformTestCase
{
    public void testTypefromBitsizeAliasOk()
    {
        assertNoErrors("""
            module test;
            alias CInt = $typefrom(signed_int_from_bitsize($$C_INT_SIZE));
            alias CUInt = $typefrom(unsigned_int_from_bitsize($$C_INT_SIZE));
            alias CShort = $typefrom(signed_int_from_bitsize($$C_SHORT_SIZE));
            fn void take(CInt c) {}
            fn void foo()
            {
                char ch = 'a';
                take(ch);
                CInt x = ch;
                int y = x;
                CUInt u = 5;
                CShort s = 7;
            }
            """);
    }

    public void testTypefromBitsizeNarrowingStillError()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module test;
            alias CInt = $typefrom(signed_int_from_bitsize($$C_INT_SIZE));
            fn void foo()
            {
                CInt x = "s";
            }
            """), "Cannot assign");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testLongFollowsHostDataModel()
    {
        boolean windows = System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("win");
        // LP64 (Linux/macOS): C long is 64-bit -> C3 long; LLP64 (Windows): 32-bit -> C3 int.
        String longTarget = windows ? "int" : "long";
        assertNoErrors("""
            module test;
            alias CLong = $typefrom(signed_int_from_bitsize($$C_LONG_SIZE));
            fn void take_long(REPLACEME v) {}
            fn void foo()
            {
                CLong x = 1;
                take_long(x);
            }
            """.replace("REPLACEME", longTarget));
    }

    public void testUnresolvedTypefromTernaryIsLenient()
    {
        assertNoErrors("""
            module test;
            alias CChar = $typefrom($$C_CHAR_IS_SIGNED ? ichar.typeid : char.typeid);
            fn void take(CChar c) {}
            fn void foo()
            {
                char ch = 'a';
                take(ch);
            }
            """);
    }

    public void testTypefromTypeidAndStringOk()
    {
        assertNoErrors("""
            module test;
            alias TAlias = $typefrom(int.typeid);
            alias UAlias = $typefrom("uint");
            fn void foo()
            {
                TAlias a = 1;
                UAlias b = 2u;
            }
            """);
    }

    public void testComptimeTypeParamOk()
    {
        assertNoErrors("""
            module test;
            faultdef EMPTY_STRING;
            faultdef NEGATIVE_VALUE;
            macro String to_integer(self, $Type, int base = 10)
            {
                if ($Type.min == 0) return NEGATIVE_VALUE~;
                usz size = $Type.sizeof;
                $Type base_used = ($Type)base;
                if (base == 10) return ($Type)0;
                $Type value = 0;
                return value;
            }
            macro parse_int($Type, int base = 10)
            {
                $Type value = 0;
                return ($Type)value;
            }
            fn void foo()
            {
                String s = "42";
                int x = s.to_integer(int);
                int y = parse_int(int, 10);
            }
            """);
    }

    public void testInlineTypedefChainThroughComptimeAliasOk()
    {
        assertNoErrors("""
            module test;
            alias CInt = $typefrom(signed_int_from_bitsize($$C_INT_SIZE));
            typedef Errno @constinit = inline CInt;
            fn void errno_set(Errno e)
            {
                int x = (int)e;
                Errno back = (Errno)x;
            }
            """);
    }

    public void testDistinctTypedefCastOk()
    {
        assertNoErrors("""
            module test;
            typedef Handle = int;
            alias HandleAlias = Handle;
            fn void foo(Handle h)
            {
                int x = (int)h;
                Handle back = (Handle)x;
                HandleAlias again = (HandleAlias)h;
            }
            """);
    }

    public void testMethodMacroHeaderKeepsOwner()
    {
        String code = """
            module test;
            faultdef NEGATIVE_VALUE;
            macro String.to_integer(self, $Type, int base = 10)
            {
                if ($Type.min == 0) return NEGATIVE_VALUE~;
                $Type value = 0;
                return ($Type)value;
            }
            fn void foo()
            {
                String s = "42";
                int x = s.to_integer(int);
            }
            """;
        myFixture.configureByText("main.c3", code);
        // A dotted header is an owner without a return type: the dot must
        // reach macro_name, nothing may land in optional_type.
        Collection<C3MacroDefinition> macros = PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3MacroDefinition.class);
        assertEquals("Expected one macro, got: " + macros, 1, macros.size());
        C3MacroDefinition macro = macros.iterator().next();
        assertNull("No return type expected", macro.getMacroHeader().getOptionalType());
        assertNotNull("Owner expected", macro.getMacroHeader().getMacroName().getType());
        assertEquals("String", InterfaceService.methodOwnerTypeName(macro));
        assertTrue("Unexpected errors, got: " + myFixture.doHighlighting(),
            errorsWithText(myFixture.doHighlighting(), "Cannot").isEmpty());
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
