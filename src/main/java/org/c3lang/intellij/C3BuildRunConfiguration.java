package org.c3lang.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.c3lang.intellij.project.C3ProjectJsonParser;
import org.c3lang.intellij.project.C3ProjectModel;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3BuildRunConfiguration extends LocatableConfigurationBase<C3BuildRunConfigurationOptions>
{
	protected C3BuildRunConfiguration(Project project, ConfigurationFactory factory, String name)
	{
		super(project, factory, name);
	}

	@Override public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		return new C3BuildRunEditor();
	}

	@Override protected @NotNull C3BuildRunConfigurationOptions getOptions()
	{
		return (C3BuildRunConfigurationOptions) super.getOptions();
	}

	public String getWorkingDirectory()
	{
		return getOptions().getWorkingDirectory();
	}

	public void setWorkingDirectory(String workingDirectory)
	{
		getOptions().setWorkingDirectory(workingDirectory);
	}

	public String getArgs()
	{
		return getOptions().getArgs();
	}

	public void setArgs(String args)
	{
		getOptions().setArgs(args);
	}

	public String getTargetName()
	{
		return getOptions().getTargetName();
	}

	public void setTargetName(String targetName)
	{
		getOptions().setTargetName(targetName);
	}

	public boolean isRunAfterBuild()
	{
		return getOptions().isRunAfterBuild();
	}

	public void setRunAfterBuild(boolean runAfterBuild)
	{
		getOptions().setRunAfterBuild(runAfterBuild);
	}

	public String getCompilerName()
	{
		return getOptions().getCompilerName();
	}

	public void setCompilerName(String compilerName)
	{
		getOptions().setCompilerName(compilerName);
	}

	public String getCompilerPath()
	{
		return getOptions().getCompilerPath();
	}

	public void setCompilerPath(String compilerPath)
	{
		getOptions().setCompilerPath(compilerPath);
	}

	@Override public void checkConfiguration()
	{
	}

	@Override
	public void onNewConfigurationCreated()
	{
		super.onNewConfigurationCreated();
		setGeneratedName();
	}

	@Override
	public String suggestedName()
	{
		String targetName = getTargetNameForBuild();
		if (targetName.isBlank())
		{
			return RunManager.getInstance(getProject()).suggestUniqueName("C3 Build", getType());
		}
		return RunManager.getInstance(getProject()).suggestUniqueName(targetName, getType());
	}

	@Override public @Nullable RunProfileState getState(@NotNull Executor executor,
	                                                    @NotNull ExecutionEnvironment executionEnvironment) throws
	                                                                                                        ExecutionException
	{
		return new CommandLineState(executionEnvironment)
		{
			@Override protected @NotNull ProcessHandler startProcess() throws ExecutionException
			{
				String targetName = getTargetNameForBuild();
				if (targetName.isBlank()) throw new ExecutionException("No C3 build target is selected.");

				GeneralCommandLine commandLine = new GeneralCommandLine(C3RunConfigurationUtil.findCompilerBinaryPath(
						getProject(),
						getCompilerName(),
						getCompilerPath()), "build");
				commandLine.addParameter(targetName);
				C3RunConfigurationUtil.addProjectStdlibOverride(commandLine, getProject());

				commandLine.setWorkDirectory(getWorkingDirectory());

				ProcessHandler processHandler = new C3LinkedExecutableProcessHandler(
						commandLine,
						getWorkingDirectory(),
						getArgs() == null ? "" : getArgs(),
						isRunAfterBuild());
				ProcessTerminatedListener.attach(processHandler);
				return processHandler;
			}
		};
	}

	private @NotNull String getTargetNameForBuild()
	{
		String targetName = getTargetName().trim();
		if (!targetName.isBlank()) return targetName;

		C3ProjectModel model = C3ProjectService.getInstance(getProject()).getProjectModel();
		if (model == null) return "";
		for (C3ProjectJsonParser.TargetDefinition target : model.getTargets())
		{
			if (!target.name().isBlank()) return target.name();
		}
		return "";
	}
}
