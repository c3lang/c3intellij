package org.c3lang.intellij;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.execution.ExecutionException;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.jetbrains.annotations.Nullable;

public class C3LineMarkerProvider implements LineMarkerProvider
{
	@Override
	public @Nullable LineMarkerInfo<?> getLineMarkerInfo(PsiElement element)
	{
		if (!(element instanceof C3FuncDefinition function)) return null;

		String type = function.getFuncDef().getFuncHeader().getOptionalType().getType().getText();
		String name = function.getFuncDef().getFqName().getName();

		if (!name.equals("main")) return null;
		if (!type.equals("int") && !type.equals("void")) return null;

		return new LineMarkerInfo<>(
			element,
			element.getTextRange(),
			AllIcons.Actions.Execute,
			ignored -> "Run main",
			(event, elt) -> createAndRunCustomConfig(elt.getProject()),
			GutterIconRenderer.Alignment.RIGHT,
			() -> "Click to open context menu"
		);
	}

	private static void createAndRunCustomConfig(Project project)
	{
		RunManager runManager = RunManager.getInstance(project);
		C3BuildRunConfigurationType configType =
			ConfigurationTypeUtil.findConfigurationType(C3BuildRunConfigurationType.class);
		ConfigurationFactory factory = configType.getConfigurationFactories()[0];
		var settings = runManager.createConfiguration("main", factory);
		C3BuildRunConfiguration config = (C3BuildRunConfiguration) settings.getConfiguration();

		config.setWorkingDirectory(project.getBasePath());
		config.setArgs("");

		runManager.addConfiguration(settings);
		runManager.setSelectedConfiguration(settings);

		var executor = DefaultRunExecutor.getRunExecutorInstance();
		try
		{
			ExecutionEnvironment environment = ExecutionEnvironmentBuilder.create(executor, settings).build();
			environment.getRunner().execute(environment);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}
}
