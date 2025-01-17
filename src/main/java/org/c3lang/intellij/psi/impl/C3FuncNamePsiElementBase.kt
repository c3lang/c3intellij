package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import org.c3lang.intellij.completion.PsiElementUtils
import org.c3lang.intellij.psi.C3FuncName
import org.c3lang.intellij.psi.C3FuncNamePsiElement

abstract class C3FuncNamePsiElementBase(node: ASTNode) : C3PsiElementImpl(node),
    C3FuncName, C3FuncNamePsiElement {

    override fun getName(): String? {
        return text
    }

    override fun setName(name: String): PsiElement {
        context?.project?.also {
            val functionName = PsiElementUtils.createFunctionName(it, name)
            ident.replace(functionName.ident)
        }
        return this;
    }

    override fun getNameIdentifier(): PsiElement? {
        return this.ident
    }

    override fun getTextOffset(): Int {
        return this.ident.textOffset
    }

    override fun getReference(): PsiReference? {
        val id = nameIdentifier ?: return null
//        val headerStart = funcHeader.textRangeInParent.startOffset
//        val nameStart = funcHeader.funcName.textRangeInParent.startOffset
//        val rangeInElement = TextRange(headerStart + nameStart, id.textLength)
        val rangeInElement = this.ident.textRangeInParent
        return object : PsiReferenceBase<PsiElement>(this, rangeInElement) {
            override fun resolve(): PsiElement {
                return this@C3FuncNamePsiElementBase
            }
        }
    }

}