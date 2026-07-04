package org.c3lang.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3BuildRunConfiguration extends RunConfigurationBase<C3BuildRunConfigurationOptions>
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

	@Override public @Nullable RunProfileState getState(@NotNull Executor executor,
	                                                    @NotNull ExecutionEnvironment executionEnvironment) throws
	                                                                                                        ExecutionException
	{
		return new CommandLineState(executionEnvironment)
		{
			@Override protected @NotNull ProcessHandler startProcess() throws ExecutionException
			{
				GeneralCommandLine commandLine = new GeneralCommandLine(C3RunConfigurationUtil.findCompilerBinaryPath(
						getProject(),
						getCompilerName(),
						getCompilerPath()), isRunAfterBuild() ? "run" : "build");

				// I couldn't just add the whole args string here because the GeneralCommandLine class adds quotes
				// around parameters with spaces (so it would look like this: c3c run "--param value" which isn't valid
				// syntax).
				// Instead, I'm splitting the args string by spaces and adding that array.
				if (getArgs() != null) commandLine.addParameters(getArgs().split(" "));

				commandLine.setWorkDirectory(getWorkingDirectory());

				OSProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
				ProcessTerminatedListener.attach(processHandler);
				return processHandler;
			}
		};
	}
}
