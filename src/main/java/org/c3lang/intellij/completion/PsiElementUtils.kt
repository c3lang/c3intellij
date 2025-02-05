package org.c3lang.intellij.completion

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import org.c3lang.intellij.C3Language
import org.c3lang.intellij.psi.C3Arg
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3FuncName
import org.c3lang.intellij.psi.C3ImportDecl
import org.c3lang.intellij.psi.C3PathIdentExpr
import org.c3lang.intellij.psi.C3Statement

object PsiElementUtils {

    @JvmStatic
    fun createImport(project: Project, importPath: String): C3ImportDecl {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "import $importPath;")

        return checkNotNull(PsiTreeUtil.findChildOfType(file, C3ImportDecl::class.java))
    }

    @JvmStatic
    fun createNewLine(project: Project): PsiElement {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "\n")

        return file.firstChild
    }



    @JvmStatic
    fun createFunctionName(project: Project, functionName: String): C3FuncName {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "fn void $functionName() {}")

        return checkNotNull(PsiTreeUtil.findChildOfType(file, C3FuncName::class.java))
    }

    @JvmStatic
    fun createPathIdentExpr(project: Project, expression: String): C3PathIdentExpr {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "fn void dummy() { $expression(); }")

        return checkNotNull(PsiTreeUtil.findChildOfType(file, C3PathIdentExpr::class.java))
    }

    @JvmStatic
    fun createArg(project: Project, name: String): C3Arg {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "int i = dummy($name);")

        return requireNotNull(PsiTreeUtil.findChildOfType(file, C3Arg::class.java))
    }

    @JvmStatic
    fun createStatement(project: Project, name: String): C3Statement {
        val instance = PsiFileFactory.getInstance(project)
        val file = instance.createFileFromText(C3Language.INSTANCE, "fn void dummy() { int $name = 0; }")

        return requireNotNull(PsiTreeUtil.findChildOfType(file, C3Statement::class.java))
    }

}