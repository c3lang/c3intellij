package org.c3lang.intellij.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.apache.commons.lang3.Validate
import org.apache.commons.logging.LogFactory
import org.c3lang.intellij.C3Icons
import org.jetbrains.annotations.NonNls
import java.util.Properties

class C3NewFileAction : CreateFileFromTemplateAction("C3 File", "Creates a new C3 file", C3Icons.FILE)
{
    private val log = LogFactory.getLog(C3NewFileAction::class.java)

    override fun buildDialog(project: Project, psiDirectory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder)
    {
        builder.setTitle("New C3 File")
            .addKind("C3 file", C3Icons.FILE, "C3 File")
            .addKind("C3 interface", C3Icons.LIB_FILE, "C3 Interface")
    }

    override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory): PsiFile?
    {
        val project = dir.project
        val templateManager = FileTemplateManager.getInstance(project)

        val properties = Properties()
        properties.putAll(templateManager.defaultProperties)

        properties.setProperty("MODULE_NAME", getModuleName(dir))

        try
        {
            return FileTemplateUtil.createFromTemplate(template, name, properties, dir) as PsiFile
        } catch (e: Exception)
        {
            log.error(e.message, e)
            return null
        }
    }

    private fun getModuleName(file: PsiDirectory): String
    {
        Validate.notNull(file.project.basePath, "Project base path cannot be null")
        val projectRoot = LocalFileSystem.getInstance().findFileByPath(file.project.basePath!!)

        Validate.notNull(projectRoot, "Project root cannot be null")
        return projectRoot!!.name.replace(" ", "_").lowercase()
    }

    override fun getActionName(psiDirectory: PsiDirectory, newName: @NonNls String, templateName: @NonNls String?): @NlsContexts.Command String
    {
        return "Creating C3 File $newName"
    }

    override fun update(e: AnActionEvent)
    {
        super.update(e)

        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        e.presentation.isVisible = project != null && virtualFile != null && isC3Project(project)
    }

    private fun isC3Project(project: Project): Boolean
    {
        Validate.notNull(project.basePath, "Project base path cannot be null")
        val projectDir = LocalFileSystem.getInstance().findFileByPath(project.basePath!!)

        Validate.notNull(projectDir, "Project root cannot be null")
        val markerFile = projectDir!!.findChild("project.json")

        return markerFile != null && markerFile.exists()
    }
}