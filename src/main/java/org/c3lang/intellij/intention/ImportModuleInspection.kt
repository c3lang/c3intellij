package org.c3lang.intellij.intention

import ai.grazie.utils.dropPostfix
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.removeUserData
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.*


class ImportModuleInspection : LocalInspectionTool() {

    override fun getDisplayName(): String = "Import module"

    override fun getGroupDisplayName(): String = "Import"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : C3Visitor() {
            override fun visitPath(path: C3Path) {
                val scopes = path.node.getChildren(TokenSet.create(C3Types.SCOPE))
                val pathExists = scopes.isNotEmpty()
                val importProvider = checkNotNull(path.parentOfType<C3ImportProvider>())
                val importIntention = ModuleName(path.text.dropPostfix("::"))
                val applied = path.removeUserData(AddImportQuickFix.KEY)

                if (pathExists && !importProvider.imports.contains(importIntention) && applied == null) {

                    holder.registerProblem(
                        path,
                        "Import ${importIntention.value}",
                        ProblemHighlightType.WEAK_WARNING,
                        AddImportQuickFix(
                            path = path,
                            importIntention = importIntention,
                        )
                    )
                }
            }
        }
    }

}