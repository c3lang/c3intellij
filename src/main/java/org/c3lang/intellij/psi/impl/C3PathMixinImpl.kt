package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.text.nullize
import org.c3lang.intellij.psi.C3ImportProvider
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.C3Types

abstract class C3PathMixinImpl(node: ASTNode) : C3Path, C3PsiNamedElementImpl(node) {

    override fun getNameIdentifier(): PsiElement = this

    override fun getName(): String = text.dropPostfix("::")

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getReference(): PsiReference = C3PathReference(this)

    override fun getReferences(): Array<PsiReference> {
        val importProvider = this.parentOfType<C3ImportProvider>() ?: return emptyArray()

        return arrayOf(C3PathReference(this), C3ImportPathReference(importProvider, this))
    }

    override fun shorten() {
        val idents = node.getChildren(
            TokenSet.create(C3Types.IDENT, C3Types.SCOPE)
        ).toMutableList()

        if (idents.size <= 2) {
            // bar::
            return
        }

        // tokens: std::io::
        // remove ::
        idents.removeLast()
        // remove io
        idents.removeLast()
        // remove std::
        deleteChildRange(
            idents.first().psi,
            idents.last().psi
        )
    }

    private class C3PathReference(private val element: C3Path) : PsiReferenceBase<C3Path>(element) {
        override fun resolve(): PsiElement = element

        override fun isReferenceTo(psi: PsiElement): Boolean {
            return psi is C3Path && psi.text == element.text
        }
    }

    private class C3ImportPathReference(
        private val importProvider: C3ImportProvider,
        private val element: C3Path
    ) : PsiReferenceBase<C3Path>(element) {
        override fun resolve(): PsiElement? {
            val importPaths = importProvider.importDeclarations.flatMap { it.importPaths.importPathList }

            val elementPrefix = element.text.split("::")
                .mapNotNull { it.nullize() }.singleOrNull() ?: return null

            val res = importPaths.singleOrNull { it.text == element.text } ?: importPaths.singleOrNull {
                it.text.endsWith(elementPrefix)
            }

            return res
        }

//        override fun isReferenceTo(element: PsiElement): Boolean {
//            return element is C3ImportPath && resolve() == element
//        }
    }
}