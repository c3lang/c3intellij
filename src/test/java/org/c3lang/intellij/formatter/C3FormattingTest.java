package org.c3lang.intellij.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3SettingsState;

import java.util.List;

public class C3FormattingTest extends BasePlatformTestCase
{
    private int braceStyle;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        C3SettingsState.getInstance().setCompilerProfiles(List.of(
                new C3SettingsState.CompilerProfile(C3SettingsState.DEFAULT_COMPILER_NAME, "", "", "")
        ));
        setBraceStyle(C3CodeStyleSettings.BRACE_STYLE_DEFAULT);
    }

    public void testDefaultFormattingUsesAllmanBracesAndOperatorSpaces()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn int add(int a,int b){if(a+b>0){return a+b;}}
                """);

        reformat();

        assertEquals("""
                module demo;
                fn int add(int a, int b)
                {
                \tif (a + b > 0)
                \t{
                \t\treturn a + b;
                \t}
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingUsesKnrBracesWhenConfigured()
    {
        setBraceStyle(C3CodeStyleSettings.BRACE_STYLE_KNR);
        myFixture.configureByText("main.c3", """
                module demo;
                fn int add(int a,int b)
                {
                if(a+b>0)
                {
                return a+b;
                }
                }
                """);

        reformat();

        assertEquals("""
                module demo;
                fn int add(int a, int b) {
                \tif (a + b > 0) {
                \t\treturn a + b;
                \t}
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingIgnoresConfigurableSpacingSettings()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn int add(int a,int b){if(a+b>0){return a+b;}}
                """);

        reformat((settings) ->
        {
            CommonCodeStyleSettings commonSettings = settings.getCommonSettings(C3Language.INSTANCE);
            commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS = false;
            commonSettings.SPACE_AROUND_LOGICAL_OPERATORS = false;
            commonSettings.SPACE_AROUND_EQUALITY_OPERATORS = false;
            commonSettings.SPACE_AROUND_RELATIONAL_OPERATORS = false;
            commonSettings.SPACE_AROUND_BITWISE_OPERATORS = false;
            commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS = false;
            commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS = false;
            commonSettings.SPACE_AROUND_SHIFT_OPERATORS = false;
            commonSettings.SPACE_AFTER_COMMA = false;
            commonSettings.SPACE_BEFORE_COMMA = true;
            commonSettings.SPACE_WITHIN_PARENTHESES = true;
            commonSettings.SPACE_BEFORE_IF_PARENTHESES = false;
        });

        assertEquals("""
                module demo;
                fn int add(int a, int b)
                {
                \tif (a + b > 0)
                \t{
                \t\treturn a + b;
                \t}
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingCoversDeclarationsInitializersMacrosAndLambdas()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                struct Point{int x;int y;}
                enum Direction:int{NORTH,EAST,SOUTH,WEST,}
                constdef Foo:int{OK,NOT_FOUND=404,}
                bitstruct Bar:int{int low:0..7;int high:8..15;}
                faultdef
                ALREADY_EXISTS,
                BUSY;
                const Point ORIGIN={.x=0,.y=0};
                macro Point moved(Point p,int dx,int dy){return {.x=p.x+dx,.y=p.y+dy};}
                fn void main(){Point p={.x=1,.y=2};Callback cb=fn int(int value){return value*2;};int doubled=apply(fn int(int value)=>value*2,p.x);$if $is_stream:return readline_impl{$typeof(stream)}(allocator,stream,limit);$endif $foreach $member:MEMBERS:io::printfn($member.name);$endforeach}
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                struct Point
                {
                \tint x;
                \tint y;
                }
                enum Direction : int
                {
                \tNORTH,
                \tEAST,
                \tSOUTH,
                \tWEST,
                }
                constdef Foo : int
                {
                \tOK,
                \tNOT_FOUND = 404,
                }
                bitstruct Bar : int
                {
                \tint low : 0..7;
                \tint high : 8..15;
                }
                faultdef
                \tALREADY_EXISTS,
                \tBUSY;
                const Point ORIGIN = { .x = 0, .y = 0 };
                macro Point moved(Point p, int dx, int dy)
                {
                \treturn { .x = p.x + dx, .y = p.y + dy };
                }
                fn void main()
                {
                \tPoint p = { .x = 1, .y = 2 };
                \tCallback cb = fn int(int value) {
                \t\treturn value * 2;
                \t};
                \tint doubled = apply(fn int(int value) => value * 2, p.x);
                \t$if $is_stream:
                \t\treturn readline_impl{$typeof(stream)}(allocator, stream, limit);
                \t$endif
                \t$foreach $member : MEMBERS:
                \t\tio::printfn($member.name);
                \t$endforeach
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingKeepsEndOfLineDocCommentsAfterSemicolon()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                const int VALUE=1;   <* Value docs *>
                const int OTHER=2;    // Other docs
                const int THIRD=3;     /* Third docs */
                fn void main(){int local=4;  <* Local docs *>
                int next=4;      // Next docs
                int block=5;       /* Block docs */
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                const int VALUE = 1;   <* Value docs *>
                const int OTHER = 2;    // Other docs
                const int THIRD = 3;     /* Third docs */
                fn void main()
                {
                \tint local = 4;  <* Local docs *>
                \tint next = 4;      // Next docs
                \tint block = 5;       /* Block docs */
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingPreservesBlankLineBeforeStandaloneLineCommentAfterDeclaration()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                struct Foo
                {
                int a;
                }

                // Hello

                <* Docs *>

                /* Block */


                fn void test() {}
                """);
        assertNoPsiErrors();

        reformat((settings) ->
                settings.getCommonSettings(C3Language.INSTANCE).KEEP_BLANK_LINES_IN_DECLARATIONS = 1
        );

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                struct Foo
                {
                \tint a;
                }

                // Hello

                <* Docs *>

                /* Block */

                fn void test()
                {}
                """, myFixture.getFile().getText());
    }

    public void testFormattingMovesClosingBraceAfterTrailingCommaInMultilineInitializer()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn void main()
                {
                int[2][*] x = { {1,2},
                {2,3},
                {4,5},
                };
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn void main()
                {
                \tint[2][*] x = {
                \t\t{ 1, 2 },
                \t\t{ 2, 3 },
                \t\t{ 4, 5 },
                \t};
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingKeepsTypeModifiersTight()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn void main(){int *a;int ? b;int [2] * ? c;int [< 2 >] * ? d;int value=a * c;}
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn void main()
                {
                \tint* a;
                \tint? b;
                \tint[2]*? c;
                \tint[<2>]*? d;
                \tint value = a * c;
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingSpacesInterfaceImplsAndKeepsTypedVariadicsTight()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                struct MultiWriter(OutStream){}
                fn void write(int ... x,int * ... y){}
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                struct MultiWriter (OutStream)
                {}
                fn void write(int... x, int*... y)
                {}
                """, myFixture.getFile().getText());
    }

    public void testFormattingIndentsStatementsAfterCastsSwitchesAndEarlyReturns()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn int seek(int reader, int offset, int seek)
                {
                    int new_index;
                    switch (seek)
                    {
                        case SET: new_index = offset;
                        case CURSOR: new_index = seek + offset;
                    case END: new_index = reader + offset;
                    }
                    if (new_index < 0) return -1;
                reader = new_index;
                return(int)new_index;
                }
                fn int write_stream(int reader, Writer* writer)
                {
                    if (reader >= 10) return 0;
                int written = writer.write(reader)!;
                reader += written;
                    assert(reader <= 10);
                return written;
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn int seek(int reader, int offset, int seek)
                {
                \tint new_index;
                \tswitch (seek)
                \t{
                \t\tcase SET: new_index = offset;
                \t\tcase CURSOR: new_index = seek + offset;
                \t\tcase END: new_index = reader + offset;
                \t}
                \tif (new_index < 0) return -1;
                \treader = new_index;
                \treturn (int)new_index;
                }
                fn int write_stream(int reader, Writer* writer)
                {
                \tif (reader >= 10) return 0;
                \tint written = writer.write(reader)!;
                \treader += written;
                \tassert(reader <= 10);
                \treturn written;
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingIndentsMultilineCaseBodies()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn void main()
                {
                int a = 0;
                switch (a)
                {
                case FOO:
                a = a + 1;
                case BAR:
                a = a + 2;
                default:
                a = 0;
                }
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn void main()
                {
                \tint a = 0;
                \tswitch (a)
                \t{
                \t\tcase FOO:
                \t\t\ta = a + 1;
                \t\tcase BAR:
                \t\t\ta = a + 2;
                \t\tdefault:
                \t\t\ta = 0;
                \t}
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingKeepsCompileTimeIfCommentBodyIndented()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn void main()
                {
                $if env::TESTING:
                    // HACK: this is used for the purpose of unit test output hijacking
                    File* stdout = io::stdout();
                    assert(stdout.file);
                    libc::fputc(c, stdout.file);
                $endif
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn void main()
                {
                \t$if env::TESTING:
                \t\t// HACK: this is used for the purpose of unit test output hijacking
                \t\tFile* stdout = io::stdout();
                \t\tassert(stdout.file);
                \t\tlibc::fputc(c, stdout.file);
                \t$endif
                }
                """, myFixture.getFile().getText());
    }

    public void testFormattingKeepsCompileTimeCaseSingleLineBodies()
    {
        myFixture.configureByText("main.c3", """
                module demo;
                fn void main()
                {
                $switch $Type:
                        $case String: return out.write(x);
                        $case ZString: return out.write(x.str_view());
                        $case DString: return out.write(x.str_view());
                $endswitch
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                fn void main()
                {
                \t$switch $Type:
                \t\t$case String: return out.write(x);
                \t\t$case ZString: return out.write(x.str_view());
                \t\t$case DString: return out.write(x.str_view());
                \t$endswitch
                }
                """, myFixture.getFile().getText());
    }

    private void setBraceStyle(int braceStyle)
    {
        this.braceStyle = braceStyle;
    }

    private void assertNoPsiErrors()
    {
        PsiErrorElement error = PsiTreeUtil.findChildOfType(myFixture.getFile(), PsiErrorElement.class);
        assertNull(error == null ? null : error.getErrorDescription() + " at " + error.getText(), error);
    }

    private void reformat()
    {
        reformat((settings) -> {});
    }

    private void reformat(java.util.function.Consumer<CodeStyleSettings> settingsCustomizer)
    {
        CodeStyleSettingsManager settingsManager = CodeStyleSettingsManager.getInstance(getProject());
        CodeStyleSettings settings = settingsManager.createSettings();
        settings.copyFrom(settingsManager.getCurrentSettings());
        settings.getCustomSettings(C3CodeStyleSettings.class).C3_BRACE_STYLE = braceStyle;
        settingsCustomizer.accept(settings);
        CodeStyle.setTemporarySettings(getProject(), settings);
        try
        {
            assertEquals(braceStyle, CodeStyle.getCustomSettings(myFixture.getFile(), C3CodeStyleSettings.class)
                    .C3_BRACE_STYLE);
            WriteCommandAction.runWriteCommandAction(getProject(), (Runnable) () ->
                    CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile())
            );
        }
        finally
        {
            CodeStyle.dropTemporarySettings(getProject());
        }
    }
}
