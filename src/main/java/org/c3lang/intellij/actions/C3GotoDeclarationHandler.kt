package org.c3lang.intellij.actions

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfTypes
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3Module

class C3GotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {

        val parent = sourceElement?.parentOfTypes(
            C3ImportPath::class,
            C3CallExpr::class,
        )

        if (parent is C3ImportPath) {
            return parent.references.mapNotNull(PsiReference::resolve).filterIsInstance<C3Module>().toTypedArray()
        }

        if (parent is C3CallExpr) {
            return parent.references.mapNotNull(PsiReference::resolve).filterIsInstance<C3ImportPath>().toTypedArray()
        }

        return null
    }
}