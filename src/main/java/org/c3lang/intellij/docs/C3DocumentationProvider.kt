package org.c3lang.intellij.docs

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3LocalDeclAfterType

class C3DocumentationProvider : AbstractDocumentationProvider()
{
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String?
    {
        println("---")
        println(element)
        println(originalElement)
        println("---")

        if (element is C3FuncDef) return generateFuncDefDoc(element)
        if (element is C3LocalDeclAfterType) return generateVarDeclDoc(element)

        return null
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String?
    {
        return generateDoc(element, originalElement)
    }
}