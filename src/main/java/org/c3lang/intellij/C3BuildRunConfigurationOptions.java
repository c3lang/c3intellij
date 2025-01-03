package org.c3lang.intellij;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;


public class C3BuildRunConfigurationOptions extends RunConfigurationOptions {
    private final StoredProperty<String> myWorkingDirectory =
            string("").provideDelegate(this, "workingDirectory");

    private final StoredProperty<String> myArgs =
            string("").provideDelegate(this, "args");

    public String getWorkingDirectory()
    {
        return myWorkingDirectory.getValue(this);
    }

    public void setWorkingDirectory(String workingDirectory)
    {
        myWorkingDirectory.setValue(this, workingDirectory);
    }

    public String getArgs()
    {
        return myArgs.getValue(this);
    }

    public void setArgs(String args)
    {
        myArgs.setValue(this, args);
    }
}
