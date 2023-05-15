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

    @Override public void checkConfiguration()
    {
    }

    public String getSourceName()
    {
        return getOptions().getSourceName();
    }

    public void setSourceName(String string)
    {
        getOptions().setSourceName(string);
    }

    @Override public @Nullable RunProfileState getState(@NotNull Executor executor,
                                                        @NotNull ExecutionEnvironment executionEnvironment) throws
                                                                                                            ExecutionException
    {
        return new CommandLineState(executionEnvironment) {
            @Override protected @NotNull ProcessHandler startProcess() throws ExecutionException
            {
                String sdk = C3SettingsState.getInstance().sdk;
                GeneralCommandLine commandLine = new GeneralCommandLine(sdk, "compile-run", getSourceName());
                OSProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
                ProcessTerminatedListener.attach(processHandler);
                return processHandler;
            }
        };
    }
}
