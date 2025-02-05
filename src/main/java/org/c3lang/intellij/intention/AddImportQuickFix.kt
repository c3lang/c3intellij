package org.c3lang.intellij.intention

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.c3lang.intellij.completion.PsiElementUtils
import org.c3lang.intellij.psi.*

class AddImportQuickFix(
    target: C3Path,
    private val importIntention: ModuleName,
) : LocalQuickFixAndIntentionActionOnPsiElement(target) {

    override fun getFamilyName(): String = "Add import ${importIntention.value}"

    override fun getText(): String = "Add import ${importIntention.value}"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            val path = startElement as C3Path

            val moduleSection = checkNotNull(path.parentOfType<C3ModuleDefinition>())

            path.putUserData(KEY, importIntention)

            path.parentOfType<C3FullyQualifiedNamePsiElement>()?.let {
                addImport(
                    element = it,
                    moduleSection = moduleSection,
                    project = project
                )
            }

            path.shorten()
        }

//        FullyQualifiedName.from(moduleSection)
//
//        addImport(
//            importIntention = importIntention,
//            moduleSection = moduleSection,
//            project = project
//        )


//        ReferencesSearch.search(path, LocalSearchScope(moduleSection)).mapNotNull(PsiReference::resolve)
//            .filterIsInstance<C3Path>().forEach {
//                it.putUserData(KEY, importIntention)
//                it.shorten()
//            }

//            editor?.document?.let {
//                PsiDocumentManager.getInstance(project).commitDocument(it)
//            }
    }

    companion object {

        val KEY = Key.create<ModuleName>("AddImportQuickFix")

        fun addImport(
            element: C3FullyQualifiedNamePsiElement,
            moduleSection: C3ModuleDefinition,
            project: Project
        ): ModuleName? {
            val moduleName = element.moduleName ?: return null

            val moduleImported = moduleSection.isImported(element)

            if (moduleImported) {
                return element.moduleName
            }

            val imports = moduleSection.importDeclarations
            if (imports.isNotEmpty()) {
                // add import after last import in module section
                val importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, moduleName.value),
                    imports.last().parent
                )
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement)
            } else if (moduleSection is C3ModuleSection) {
                // add import after module declaration
                val importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, moduleName.value),
                    moduleSection.module
                )
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement)
            } else if (moduleSection is C3DefaultModuleSection) {
                // add import to default module in first position
                val importDeclarationElement = moduleSection.addBefore(
                    PsiElementUtils.createImport(project, moduleName.value),
                    moduleSection.firstChild
                )
                moduleSection.addAfter(PsiElementUtils.createNewLine(project), importDeclarationElement)
            }

            return moduleName
        }


        fun addImportAsText(
            element: C3FullyQualifiedNamePsiElement,
            moduleSection: C3ModuleDefinition
        ): ImportAction? {
            val moduleName = element.moduleName ?: return null

            val moduleImported = moduleSection.isImported(element)

            if (moduleImported) {
                return ImportAction.Imported(moduleName)
            }

            val imports = moduleSection.importDeclarations

            val startOffset = if (imports.isNotEmpty()) {
                // add import after last import in module section
                val parent = imports.last().parent

                parent.endOffset
            } else if (moduleSection is C3ModuleSection) {
                moduleSection.module.endOffset
            } else /*if (moduleSection is C3DefaultModuleSection)*/ {
                // add import to default module in first position
                moduleSection.firstChild.startOffset
            }

            return ImportAction.ShouldImport(startOffset, moduleName)
        }

        fun writeImport(
            document: Document,
            offset: Int,
            moduleName: ModuleName
        ) {
            document.insertString(offset, "\nimport ${moduleName.value};\n")
        }
    }

    sealed interface ImportAction {

        fun write(document: Document) {
            // noop
        }

        val moduleName: ModuleName

        class ShouldImport(val offset: Int, override val moduleName: ModuleName) : ImportAction {
            override fun write(document: Document) {
                document.insertString(offset, "\nimport ${moduleName.value};\n")
            }
        }

        class Imported(override val moduleName: ModuleName) : ImportAction
    }
}