package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import org.c3lang.intellij.C3Language
import org.c3lang.intellij.psi.C3FuncName
import org.c3lang.intellij.psi.C3ImportDecl

object PsiElementUtils {

    @JvmStatic
    fun createImport(project: Project, importPath: String): C3ImportDecl {
        val instance = PsiFileFactory.getInstance(project)
        // TODO: remove \n when after code style is implemented
        val file = instance.createFileFromText(C3Language.INSTANCE, "import $importPath;")
        return checkNotNull(PsiTreeUtil.findChildOfType(file, C3ImportDecl::class.java))
    }

    @JvmStatic
    fun createFunctionName(project: Project, functionName: String): C3FuncName {
        val instance = PsiFileFactory.getInstance(project)

        val file = instance.createFileFromText(C3Language.INSTANCE, "fn void $functionName() {}")
        return checkNotNull(PsiTreeUtil.findChildOfType(file, C3FuncName::class.java))
    }


}