package org.c3lang.intellij;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class C3CompileRunConfigurationType implements ConfigurationType, DumbAware
{
    static final String ID = "C3RunConfiguration";

    @Override public @NotNull String getDisplayName()
    {
        return "C3 Single File";
    }

    @Override public String getConfigurationTypeDescription()
    {
        return "C3 run configuration type";
    }

    @Override public Icon getIcon()
    {
        return C3Icons.FILE;
    }

    @Override public @NotNull @NonNls String getId()
    {
        return ID;
    }

    @Override public ConfigurationFactory[] getConfigurationFactories()
    {
        return new ConfigurationFactory[] { new C3CompileRunConfigurationFactory(this) };
    }
}
