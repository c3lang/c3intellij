package org.c3lang.intellij

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.rename.RenameHandler
import com.intellij.util.IncorrectOperationException

class C3RenameHandler : RenameHandler {

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext?) {
        throw IncorrectOperationException("Rename: Not yet implemented")
    }

    override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) {
        throw IncorrectOperationException("Rename: Not yet implemented")
    }

    override fun isAvailableOnDataContext(dataContext: DataContext): Boolean = true
}