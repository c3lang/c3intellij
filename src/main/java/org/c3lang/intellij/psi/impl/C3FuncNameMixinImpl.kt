package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import org.c3lang.intellij.psi.C3FuncName

abstract class C3FuncNameMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3FuncName {

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getName(): String {
        return text
    }

    override fun getNameIdentifier(): PsiElement {
        return this
    }

//    override fun getReference(): PsiReference = SelfReference(this)

    class SelfReference(element: C3FuncName) : PsiReferenceBase<C3FuncName>(element) {
        override fun resolve(): C3FuncName = element

        override fun getRangeInElement(): TextRange {
            return super.getRangeInElement()
        }
    }
}