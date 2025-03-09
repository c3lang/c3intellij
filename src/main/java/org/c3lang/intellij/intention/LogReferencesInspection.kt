package org.c3lang.intellij.intention

import com.intellij.codeInspection.*
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.searches.ReferencesSearch
import org.c3lang.intellij.psi.C3Arg
import org.c3lang.intellij.psi.C3LocalDeclarationStmt
import org.c3lang.intellij.psi.C3Visitor


class LogReferencesInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : C3Visitor() {

            override fun visitElement(psi: PsiElement) {
                if (psi is C3Arg || psi is C3LocalDeclarationStmt) {
                    val references = ReferencesSearch.search(psi).findAll()

                    holder.registerProblem(
                        psi,
                        "Log references of ${psi.text}",
                        ProblemHighlightType.INFORMATION,
                        LogReferencesQuickFix
                    )
                }
            }
        }
    }

    object LogReferencesQuickFix : LocalQuickFix {
        private val log = Logger.getInstance(javaClass)

        override fun getFamilyName(): @IntentionFamilyName String = "Log references"

        override fun applyFix(
            project: Project,
            descriptor: ProblemDescriptor
        ) {
            val document: Document? =
                PsiDocumentManager.getInstance(project).getDocument(descriptor.psiElement.containingFile)
            val lineNo = document?.getLineNumber(descriptor.psiElement.textOffset)


            ReadAction.run<Nothing> {
                val refs = ReferencesSearch.search(descriptor.psiElement).findAll()
                log.debug("#2 ${refs.size}")
            }

            ApplicationManager.getApplication().runReadAction {
                val refs = ReferencesSearch.search(descriptor.psiElement).findAll()
                log.debug("#3 ${refs.size}")
            }
        }
    }

    companion object {
        private val log = Logger.getInstance(javaClass)
    }
}