package org.c3lang.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3CompileRunConfiguration extends RunConfigurationBase<C3CompileRunConfigurationOptions>
{
    protected C3CompileRunConfiguration(Project project, ConfigurationFactory factory, String name)
    {
        super(project, factory, name);
    }

    @Override public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
    {
        return new C3CompileRunEditor();
    }

    @Override protected @NotNull C3CompileRunConfigurationOptions getOptions()
    {
        return (C3CompileRunConfigurationOptions)super.getOptions();
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

    public String getSourceFile()
    {
        return getOptions().getSourceFile();
    }

    public void setSourceFile(String file)
    {
        getOptions().setSourceFile(file);
    }

    public boolean isRunAfterCompilation()
    {
        return getOptions().isRunAfterCompilation();
    }

    public void setRunAfterCompilation(boolean runAfterCompilation)
    {
        getOptions().setRunAfterCompilation(runAfterCompilation);
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
        return new CommandLineState(executionEnvironment) {
            @Override protected @NotNull ProcessHandler startProcess() throws ExecutionException
            {
                GeneralCommandLine commandLine = new GeneralCommandLine(
                    C3RunConfigurationUtil.findCompilerBinaryPath(getProject(), getCompilerName(), getCompilerPath()),
                    "compile",
                    getSourceFile()
                );
                C3RunConfigurationUtil.addProjectStdlibOverride(commandLine, getProject());

                String workingDirectory = getWorkingDirectory();
                commandLine.setWorkDirectory(workingDirectory);

                ProcessHandler processHandler = new C3LinkedExecutableProcessHandler(
                    commandLine,
                    workingDirectory,
                    getArgs() == null ? "" : getArgs(),
                    isRunAfterCompilation()
                );
                ProcessTerminatedListener.attach(processHandler);
                return processHandler;
            }
        };
    }
}
