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
                const int VALUE=1;<* Value docs *>
                const int OTHER=2;// Other docs
                fn void main(){int local=3;<* Local docs *>
                int next=4;// Next docs
                }
                """);
        assertNoPsiErrors();

        reformat();

        assertNoPsiErrors();
        assertEquals("""
                module demo;
                const int VALUE = 1; <* Value docs *>
                const int OTHER = 2; // Other docs
                fn void main()
                {
                \tint local = 3; <* Local docs *>
                \tint next = 4; // Next docs
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
