package org.c3lang.intellij.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.NotNull;

public class C3LanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider
{
	private static final String[] BRACE_STYLE_OPTIONS = { "Default", "K&R" };
	private static final int[] BRACE_STYLE_VALUES = {
			C3CodeStyleSettings.BRACE_STYLE_DEFAULT, C3CodeStyleSettings.BRACE_STYLE_KNR
	};

	private static final String CODE_SAMPLE = """
			module demo;
			
			fn int add(int a, int b)
			{
			    return a + b;
			}
			
			fn void main()
			{
			    if (add(1, 2) > 2)
			    {
			        io::printfn("ok");
			        int x = 0;
			        for (int i = 0; i < 10; i++)
			        {
			            x = (x | i) * 2;
			            x = x >> 2;
			        }
			    }
			}
			""";

	@Override public @NotNull Language getLanguage()
	{
		return C3Language.INSTANCE;
	}

	@Override public IndentOptionsEditor getIndentOptionsEditor()
	{
		return new SmartIndentOptionsEditor();
	}

	@Override public String getCodeSample(@NotNull SettingsType settingsType)
	{
		return CODE_SAMPLE;
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType)
	{
		if (settingsType == SettingsType.SPACING_SETTINGS)
		{
			consumer.showStandardOptions();
		}
		else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS)
		{
			consumer.showStandardOptions("KEEP_SIMPLE_BLOCKS_IN_ONE_LINE");
			consumer.showCustomOption(C3CodeStyleSettings.class,
			                          "C3_BRACE_STYLE",
			                          "Brace style",
			                          "Braces",
			                          BRACE_STYLE_OPTIONS,
			                          BRACE_STYLE_VALUES);
		}
		else if (settingsType == SettingsType.BLANK_LINES_SETTINGS)
		{
			consumer.showStandardOptions("KEEP_BLANK_LINES_IN_DECLARATIONS",
			                             "KEEP_BLANK_LINES_IN_CODE",
			                             "KEEP_BLANK_LINES_BEFORE_RBRACE");
		}
		else
		{
			consumer.showAllStandardOptions();
		}
	}

	@Override protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
	                                           @NotNull CommonCodeStyleSettings.IndentOptions indentOptions)
	{
		indentOptions.INDENT_SIZE = 4;
		indentOptions.CONTINUATION_INDENT_SIZE = 8;
		indentOptions.TAB_SIZE = 4;
		indentOptions.SMART_TABS = true;
		indentOptions.USE_TAB_CHARACTER = true;
	}
}
