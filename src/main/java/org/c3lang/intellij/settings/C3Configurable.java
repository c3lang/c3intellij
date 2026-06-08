package org.c3lang.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public final class C3Configurable implements Configurable
{
	private C3SettingsComponent c3SettingsComponent;

	@Override
	public @Nullable JComponent createComponent()
	{
		C3SettingsState settings = C3SettingsState.getInstance();

		c3SettingsComponent = new C3SettingsComponent();
		c3SettingsComponent.setStdlibPath(settings.stdlibPath);

		return c3SettingsComponent.getMainPanel();
	}

	@Override
	public boolean isModified()
	{
		C3SettingsState settings = C3SettingsState.getInstance();
		return c3SettingsComponent == null ? settings.stdlibPath != null
			: !c3SettingsComponent.getStdlibPath().equals(settings.stdlibPath);
	}

	@Override
	public void apply()
	{
		C3SettingsState settings = C3SettingsState.getInstance();
		settings.stdlibPath = c3SettingsComponent != null ? c3SettingsComponent.getStdlibPath() : "";
	}

	@Override
	public @Nls String getDisplayName()
	{
		return "C3";
	}
}
