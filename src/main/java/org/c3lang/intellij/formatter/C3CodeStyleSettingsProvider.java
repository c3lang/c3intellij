package org.c3lang.intellij.formatter;

import com.intellij.application.options.*;
import com.intellij.psi.codeStyle.*;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.*;

public class C3CodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
	@Override public @NotNull CustomCodeStyleSettings createCustomSettings(@NotNull CodeStyleSettings settings)
	{
		return new C3CodeStyleSettings(settings);
	}

	@Override public String getConfigurableDisplayName()
	{
		return "C3";
	}

	@Override
	public @NotNull String getConfigurableId()
	{
	    return "preferences.sourceCode.C3";
	}

	@Override public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings,
	                                                                   @NotNull CodeStyleSettings modelSettings)
	{
		return new CodeStyleAbstractConfigurable(settings, modelSettings, getConfigurableDisplayName())
		{
			@Override protected @NotNull CodeStyleAbstractPanel createPanel(@NotNull CodeStyleSettings settings)
			{
				return new C3CodeStyleMainPanel(getCurrentSettings(), settings);
			}
		};
	}

	private static class C3CodeStyleMainPanel extends TabbedLanguageCodeStylePanel
	{

		protected C3CodeStyleMainPanel(CodeStyleSettings currentSettings,
		                               @NotNull CodeStyleSettings settings)
		{
			super(C3Language.INSTANCE, currentSettings, settings);
		}

		@Override protected void initTabs(CodeStyleSettings settings)
		{
			addIndentOptionsTab(settings);
			addWrappingAndBracesTab(settings);
			addBlankLinesTab(settings);
		}
	}
}
