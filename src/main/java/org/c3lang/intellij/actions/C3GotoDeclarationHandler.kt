package org.c3lang.intellij.actions

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import org.c3lang.intellij.psi.C3Arg

class C3GotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {

        val elements: Array<PsiElement>? = when (sourceElement) {
            is C3Arg -> {
                val refs = ReferencesSearch.search(sourceElement).findAll()
                null
            }

            else -> null
        }

        return elements
    }
}