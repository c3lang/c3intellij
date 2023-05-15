package org.c3lang.intellij;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3CompileRunConfigurationFactory extends ConfigurationFactory
{
    public C3CompileRunConfigurationFactory(@NotNull ConfigurationType type)
    {
        super(type);
    }

    @Override public @NotNull @NonNls String getId()
    {
        return C3CompileRunConfigurationType.ID;
    }

    @Override public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project)
    {
        return new C3CompileRunConfiguration(project, this, "C3 Single File");
    }

    @Override public @Nullable Class<? extends BaseState> getOptionsClass()
    {
        return C3CompileRunConfigurationOptions.class;
    }
}
