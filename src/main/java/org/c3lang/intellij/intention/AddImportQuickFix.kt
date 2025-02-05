package org.c3lang.intellij.intention

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.completion.PsiElementUtils
import org.c3lang.intellij.psi.*

class AddImportQuickFix(
    path: C3Path,
    private val importIntention: ModuleName,
) : LocalQuickFixAndIntentionActionOnPsiElement(path) {

    override fun getFamilyName(): String = "Add import ${importIntention.value}"

    override fun getText(): String = "Add import ${importIntention.value}"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        ApplicationManager.getApplication().invokeLater {
            val path = startElement as C3Path

            val moduleSection = checkNotNull(path.parentOfType<C3ImportProvider>())

            WriteCommandAction.runWriteCommandAction(project) {

                addImport(
                    importIntention = importIntention,
                    moduleSection = moduleSection,
                    project = project
                )

                ReferencesSearch.search(path, LocalSearchScope(moduleSection)).mapNotNull(PsiReference::resolve)
                    .filterIsInstance<C3Path>().forEach {
                        it.putUserData(KEY, importIntention)
                        it.shorten()
                    }

                editor?.document?.let {
                    PsiDocumentManager.getInstance(project).commitDocument(it)
                }
            }
        }
    }

    companion object {

        val KEY = Key.create<ModuleName>("AddImportQuickFix")

        fun addImport(
            importIntention: ModuleName?,
            moduleSection: C3ImportProvider,
            project: Project
        ) {
            val moduleImported = moduleSection.imports.contains(importIntention)

            if (moduleImported || importIntention == null) {
                return
            }

            val imports = moduleSection.importDeclarations
            if (imports.isNotEmpty()) {
                // add import after last import in module section
                val importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, importIntention.value),
                    imports.last().parent
                )
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement)
            } else if (moduleSection is C3ModuleSection) {
                // add import after module declaration
                val importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, importIntention.value),
                    moduleSection.module
                )
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement)
            } else if (moduleSection is C3DefaultModuleSection) {
                // add import to default module in first position
                val importDeclarationElement = moduleSection.addBefore(
                    PsiElementUtils.createImport(project, importIntention.value),
                    moduleSection.firstChild
                )
                moduleSection.addAfter(PsiElementUtils.createNewLine(project), importDeclarationElement)
            }
        }
    }

}