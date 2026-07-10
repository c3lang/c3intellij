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

			struct Point
			{
			    int x;
			    int y;
			}

			enum Direction : int
			{
			    NORTH,
			    EAST,
			    SOUTH,
			    WEST,
			}

			constdef Foo : int
			{
			    OK,
			    NOT_FOUND = 404,
			}

			bitstruct Bar : int
			{
			    int low : 0..7;
			    int high : 8..15;
			}

			faultdef
			    ALREADY_EXISTS,
			    BUSY;

			const Point ORIGIN = { .x = 0, .y = 0 };

			macro Point moved(Point p, int dx, int dy)
			{
			    return { .x = p.x + dx, .y = p.y + dy };
			}
			
			fn void main()
			{
			    Point p = { .x = 1, .y = 2 };
			    Callback cb = fn int(int value) {
			        return value * 2;
			    };
			    int doubled = apply(fn int(int value) => value * 2, p.x);
			    $if $is_stream:
			        return readline_impl{$typeof(stream)}(allocator, stream, limit);
			    $endif
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
		if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS)
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
