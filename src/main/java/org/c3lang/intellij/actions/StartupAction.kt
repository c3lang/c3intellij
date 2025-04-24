package org.c3lang.intellij.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.io.awaitExit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.c3lang.intellij.C3SettingsState

class StartupAction : ProjectActivity
{
    override suspend fun execute(project: Project)
    {
        val settings = C3SettingsState.getInstance()
        if (settings.stdlibPath != null && settings.stdlibPath.isNotEmpty()) return

        try
        {
            val runtime = Runtime.getRuntime()
            val process = withContext(Dispatchers.IO) {
                runtime.exec(arrayOf("c3c", "compile", "--build-env"))
            }
            process.awaitExit()

            val result = process.inputStream.bufferedReader().readText()
            val path = result.split("\n").map { it.trim() }.find { it.startsWith("Stdlib") }?.substringAfter(":")?.trim()

            settings.stdlibPath = path
        } catch (_: Exception)
        {
            settings.stdlibPath = ""
        }
    }
}