package org.c3lang.intellij;

import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;


public class C3BuildRunConfigurationOptions extends LocatableRunConfigurationOptions
{
	private final StoredProperty<String> myWorkingDirectory = string("").provideDelegate(this, "workingDirectory");
	private final StoredProperty<String> myArgs = string("").provideDelegate(this, "args");
	private final StoredProperty<String> myTargetName = string("").provideDelegate(this, "targetName");
	private final StoredProperty<Boolean> myRunAfterBuild = property(true).provideDelegate(this, "runAfterBuild");
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

	public String getTargetName()
	{
		return myTargetName.getValue(this);
	}

	public void setTargetName(String targetName)
	{
		myTargetName.setValue(this, targetName);
	}

	public boolean isRunAfterBuild()
	{
		return myRunAfterBuild.getValue(this);
	}

	public void setRunAfterBuild(boolean runAfterBuild)
	{
		myRunAfterBuild.setValue(this, runAfterBuild);
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
