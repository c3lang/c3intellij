package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3ImportDecl
import org.c3lang.intellij.psi.getModule
import org.c3lang.intellij.psi.getModuleName

object PsiDocumentUtils {

    @JvmStatic
    fun PsiElement.addImport(context: InsertionContext, func: C3FuncDef) {
        val moduleSection = this
        val currentImportDeclaration = PsiTreeUtil.findChildrenOfType(moduleSection, C3ImportDecl::class.java)

        val importPaths = currentImportDeclaration.flatMap { it.importPaths.importPathList }.map { it.text }
        val sameModule = func.containingFile == moduleSection.containingFile && moduleSection == func.getModule()?.parent
        val path = func.getModuleName()

        if (path == null || importPaths.contains(path) || sameModule) {
            return
        }

        val writeCommandAction = WriteCommandAction.writeCommandAction(context.project, moduleSection.containingFile)
        val importDeclaration = PsiElementUtils.createImport(context.project, path)

        writeCommandAction.run<RuntimeException> {
            val importElement = if (importPaths.isEmpty()) {
                moduleSection.addAfter(importDeclaration, moduleSection.firstChild)
            } else {
                currentImportDeclaration.last().add(importDeclaration)
            }

            context.document.commitChanges(context)
            val textRange = importElement.textRange
            context.editor.document.replaceString(textRange.startOffset, textRange.startOffset, "\n")
        }
    }

    @JvmStatic
    fun Document.commitChanges(context: InsertionContext) {
        commitChanges(context.project)
    }

    @JvmStatic
    fun Document.commitChanges(project: Project) {
        val instance = PsiDocumentManager.getInstance(project)
        instance.doPostponedOperationsAndUnblockDocument(this)
        instance.commitDocument(this)
    }

}