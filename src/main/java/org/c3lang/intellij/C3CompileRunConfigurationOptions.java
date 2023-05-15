package org.c3lang.intellij;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;


public class C3CompileRunConfigurationOptions extends RunConfigurationOptions
{
    private final StoredProperty<String> fileName = string("").provideDelegate(this, "filename");

    public String getSourceName()
    {
        return fileName.getValue(this);
    }

    public void setSourceName(String file)
    {
        fileName.setValue(this, file);
    }
}
