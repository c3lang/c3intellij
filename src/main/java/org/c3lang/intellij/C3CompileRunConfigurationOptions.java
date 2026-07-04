package org.c3lang.intellij;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;


public class C3CompileRunConfigurationOptions extends RunConfigurationOptions
{
	private final StoredProperty<String> myWorkingDirectory = string("").provideDelegate(this, "workingDirectory");
	private final StoredProperty<String> myArgs = string("").provideDelegate(this, "args");
	private final StoredProperty<String> mySourceFile = string("").provideDelegate(this, "sourceFile");
	private final StoredProperty<Boolean> myRunAfterCompilation = property(true).provideDelegate(this, "runAfterCompilation");
	private final StoredProperty<String> myCompilerName = string("").provideDelegate(this, "compilerName");
	private final StoredProperty<String> myCompilerPath = string("").provideDelegate(this, "compilerPath");

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

	public String getSourceFile()
	{
		return mySourceFile.getValue(this);
	}

	public void setSourceFile(String file)
	{
		mySourceFile.setValue(this, file);
	}

	public boolean isRunAfterCompilation()
	{
		return myRunAfterCompilation.getValue(this);
	}

	public void setRunAfterCompilation(boolean runAfterCompilation)
	{
		myRunAfterCompilation.setValue(this, runAfterCompilation);
	}

	public String getCompilerName()
	{
		return myCompilerName.getValue(this);
	}

	public void setCompilerName(String compilerName)
	{
		myCompilerName.setValue(this, compilerName);
	}

	public String getCompilerPath()
	{
		return myCompilerPath.getValue(this);
	}

	public void setCompilerPath(String compilerPath)
	{
		myCompilerPath.setValue(this, compilerPath);
	}

}
