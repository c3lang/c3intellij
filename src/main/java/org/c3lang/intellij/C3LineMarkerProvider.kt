package org.c3lang.intellij

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.c3lang.intellij.psi.C3FuncDefinition

class C3LineMarkerProvider : LineMarkerProvider
{
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>?
    {
        if (element !is C3FuncDefinition) return null

        val type = element.funcDef.funcHeader.optionalType.type.text
        val name = element.funcDef.fqName.name

        if (name != "main") return null
        if (type != "int") return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.Actions.Execute,
            { "Run main" },
            { _, elt -> createAndRunCustomConfig(elt.project) },
            GutterIconRenderer.Alignment.RIGHT,
            { "Click to open context menu" }
        )
    }

    private fun createAndRunCustomConfig(project: Project)
    {
        val runManager = RunManager.getInstance(project)
        val configType = ConfigurationTypeUtil.findConfigurationType(C3BuildRunConfigurationType::class.java)
        val factory = configType.configurationFactories.first()
        val uniqueName = "main"
        val settings = runManager.createConfiguration(uniqueName, factory)
        val config = settings.configuration as C3BuildRunConfiguration

        config.workingDirectory = project.basePath
        config.args = ""

        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings

        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val environment = ExecutionEnvironmentBuilder.create(executor, settings).build()

        environment.runner.execute(environment)
    }
}