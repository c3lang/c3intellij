package org.c3lang.intellij

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import org.c3lang.intellij.psi.*

class C3BreadcrumbsProvider : BreadcrumbsProvider {
    override fun getLanguages(): Array<out Language> = arrayOf(C3Language.INSTANCE)

    override fun acceptElement(element: PsiElement): Boolean {
        return element is C3StructDeclaration
                || element is C3BitstructDeclaration
                || element is C3EnumDeclaration
                || element is C3MacroDefinition
                || element is C3FuncDefinition
                || element is C3StructMemberDeclaration
                || element is C3BitstructDef
                || element is C3BitstructSimpleDef
                || element is C3AttrdefDecl
                || element is C3TypedefDecl
                || element is C3AliasDecl
    }

    override fun getElementInfo(element: PsiElement): String {
        val text = when (element) {
            is C3StructDeclaration -> element.getTypeName().text
            is C3EnumDeclaration -> element.getTypeName().text
            is C3MacroDefinition -> element.getMacroHeader().getMacroName().text
            is C3TypedefDecl -> element.getTypeName().text
            is C3AttrdefDecl -> element.attributeUserName.text
            is C3AliasTypeDecl -> element.typeName.text
            is C3InterfaceDefinition -> element.getTypeName().text
            is C3FuncDefinition -> element.getFuncDef().getFuncHeader().getFuncName().text
            is C3BitstructDeclaration -> element.getTypeName().text
            is C3StructMemberDeclaration -> {
                val list: C3IdentifierList? = element.getIdentifierList()
                if (list == null) "anonymous"
                list?.text
            }

            is C3BitstructDef, is C3BitstructSimpleDef -> {
                val element: ASTNode? = element.node.findChildByType(C3Types.IDENT)
                if (element == null) "anonymous"
                element?.text
            }
            else -> ""
        }

        return text ?: ""
    }
}