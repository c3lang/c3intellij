package org.c3lang.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.List;

public final class C3Configurable implements Configurable
{
	private C3SettingsComponent c3SettingsComponent;

	@Override
	public @Nullable JComponent createComponent()
	{
		C3SettingsState settings = C3SettingsState.getInstance();

		c3SettingsComponent = new C3SettingsComponent();
		c3SettingsComponent.setCompilerProfiles(settings.getCompilerProfiles());

		return c3SettingsComponent.getMainPanel();
	}

	@Override
	public boolean isModified()
	{
		C3SettingsState settings = C3SettingsState.getInstance();
		return c3SettingsComponent != null
			&& !c3SettingsComponent.getCompilerProfiles().equals(settings.getCompilerProfiles());
	}

	@Override
	public void apply()
	{
		C3SettingsState settings = C3SettingsState.getInstance();
		if (c3SettingsComponent != null)
		{
			c3SettingsComponent.commitEditing();
		}
		List<C3SettingsState.CompilerProfile> profiles = c3SettingsComponent != null
			? c3SettingsComponent.getCompilerProfiles()
			: List.of();
		settings.setCompilerProfiles(profiles);
	}

	@Override
	public void reset()
	{
		if (c3SettingsComponent != null)
		{
			c3SettingsComponent.setCompilerProfiles(C3SettingsState.getInstance().getCompilerProfiles());
		}
	}

	@Override
	public JComponent getPreferredFocusedComponent()
	{
		return c3SettingsComponent == null ? null : c3SettingsComponent.getPreferredFocusedComponent();
	}

	@Override
	public @Nls String getDisplayName()
	{
		return "C3";
	}
}
