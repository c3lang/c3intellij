package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import org.c3lang.intellij.project.C3ProjectModel;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

final class C3RunConfigurationUtil
{

	private C3RunConfigurationUtil()
	{
	}

	static @NotNull JComboBox<CompilerOption> createCompilerComboBox()
	{
		JComboBox<CompilerOption> comboBox = new JComboBox<>();
		List<C3SettingsState.CompilerProfile> profiles = C3SettingsState.getInstance().getCompilerProfiles();
		for (C3SettingsState.CompilerProfile profile : profiles)
		{
			comboBox.addItem(new CompilerOption(profile.name, profile.binaryPath, profile.stdlibPath));
		}
		if (comboBox.getItemCount() == 0)
		{
			comboBox.addItem(new CompilerOption(C3SettingsState.DEFAULT_COMPILER_NAME, "", ""));
		}
		return comboBox;
	}

	static public void selectCompiler(@NotNull JComboBox<CompilerOption> compilerComboBox,
	                                   @NotNull String compilerName, @NotNull String compilerPath)
	{
		if (!compilerPath.isBlank())
		{
			for (int i = 0; i < compilerComboBox.getItemCount(); i++)
			{
				if (compilerComboBox.getItemAt(i).binaryPath().equals(compilerPath))
				{
					compilerComboBox.setSelectedIndex(i);
					return;
				}
			}
		}

		for (int i = 0; i < compilerComboBox.getItemCount(); i++)
		{
			if (compilerComboBox.getItemAt(i).name().equals(compilerName))
			{
				compilerComboBox.setSelectedIndex(i);
				return;
			}
		}
		compilerComboBox.setSelectedIndex(0);
	}

	static @NotNull String findCompilerBinaryPath(@Nullable Project project, @NotNull String configuredCompilerName,
	                                              @NotNull String configuredCompilerPath)
	{
		String compilerPath = configuredCompilerPath.trim();
		if (!compilerPath.isEmpty())
		{
			return compilerPath;
		}

		C3SettingsState settings = C3SettingsState.getInstance();
		String compilerName = configuredCompilerName.trim();
		if (compilerName.isEmpty())
		{
			C3ProjectModel projectModel =
					project == null ? null : C3ProjectService.getInstance(project).getProjectModel();
			if (projectModel != null)
			{
				compilerName = projectModel.getCompilerName();
			}
		}

		if (!compilerName.isEmpty())
		{
			for (C3SettingsState.CompilerProfile profile : settings.getCompilerProfiles())
			{
				if (compilerName.equals(profile.name) && !profile.binaryPath.isBlank())
				{
					return profile.binaryPath;
				}
			}
		}

		return settings.getDefaultCompilerBinaryPath();
	}

	static @NotNull String findDefaultCompilerName(@NotNull Project project)
	{
		C3ProjectModel projectModel = C3ProjectService.getInstance(project).getProjectModel();
		if (projectModel != null && !projectModel.getCompilerName().isBlank())
		{
			return projectModel.getCompilerName();
		}
		return C3SettingsState.getInstance().getDefaultCompilerProfile().name;
	}

}
