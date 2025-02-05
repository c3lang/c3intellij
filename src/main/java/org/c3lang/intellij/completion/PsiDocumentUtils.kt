package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

object PsiDocumentUtils {

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