package org.c3lang.intellij.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
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
                    if (a + b > 0)
                    {
                        return a + b;
                    }
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
                    if (a + b > 0) {
                        return a + b;
                    }
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
                    if (a + b > 0)
                    {
                        return a + b;
                    }
                }
                """, myFixture.getFile().getText());
    }

    private void setBraceStyle(int braceStyle)
    {
        this.braceStyle = braceStyle;
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
