package org.c3lang.intellij.docs

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import org.c3lang.intellij.psi.C3ConstDeclarationStmt
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3LocalDeclAfterType
import org.c3lang.intellij.psi.C3MacroDefinition

class C3DocumentationProvider : AbstractDocumentationProvider()
{
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String?
    {
        return when (element)
        {
            is C3FuncDef              -> generateFuncDefDoc(element)
            is C3MacroDefinition      -> generateMacroDefinitionDoc(element)
            is C3LocalDeclAfterType   -> generateVarDeclDoc(element)
            is C3ConstDeclarationStmt -> generateConstDeclDoc(element)
            else                      -> null
        }
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String?
    {
        return generateDoc(element, originalElement)
    }
}