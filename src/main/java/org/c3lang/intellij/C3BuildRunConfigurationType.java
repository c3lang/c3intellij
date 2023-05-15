package org.c3lang.intellij;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class C3BuildRunConfigurationType implements ConfigurationType, DumbAware
{
    static final String ID = "C3BuildRunConfiguration";

    @Override public @NotNull String getDisplayName()
    {
        return "C3 Run Project";
    }

    @Override public String getConfigurationTypeDescription()
    {
        return "C3 run project";
    }

    @Override public Icon getIcon()
    {
        return AllIcons.General.Information;
    }

    @Override public @NotNull @NonNls String getId()
    {
        return ID;
    }

    @Override public ConfigurationFactory[] getConfigurationFactories()
    {
        return new ConfigurationFactory[] { new C3BuildRunConfigurationFactory(this) };
    }
}
