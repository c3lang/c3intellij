package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3ImportPathMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3ImportPath {

    override fun endsWith(path: C3Path): Boolean {
        return text.endsWith(path.text.dropPostfix("::"))
    }

    override fun getTextOffset(): Int {
        return firstChild.textOffset
    }

//    override fun getTextRange(): TextRange? {
//        return TextRange.create(
//            firstChild.textOffset,
//            textOffset + name.length
//        )
//    }

    override val moduleName: ModuleName?
        get() = ModuleName(text)

//    override fun getReference(): PsiReference? {
//        return PsiMultiReference(
//            arrayOf(
//                C3PathIdentExprReference(this)
//            ),
//            this
//        )
//    }

    private class C3PathIdentExprReference(element: C3ImportPath) : C3ReferenceBase<C3ImportPath>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val importProvider = element.moduleDefinition
            val collectElements = PsiTreeUtil.collectElements(importProvider) {
                it as? C3CallExpr ?: return@collectElements false
                val expr = it.expr as? C3PathIdentExpr ?: return@collectElements false
                val path = expr.pathIdent.path ?: return@collectElements false

                element.text.endsWith(path.text) && importProvider.contains(expr.pathIdent)
            }.filterIsInstance<C3PsiElement>()

            return collectElements.toList()
        }
    }
}

