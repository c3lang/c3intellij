package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.index.ModuleIndex
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.C3PsiElement

abstract class C3ImportPathMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3ImportPath {

    override fun getNameIdentifier(): PsiElement? = this

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getTextRange(): TextRange {
        return super.getTextRange()
    }

    override fun getReference(): PsiReference? {
        return C3ImportPathReference(this)
    }

    override fun getReferences(): Array<PsiReference> {
        return arrayOf(C3ImportPathReference(this), C3ModuleReference(this))
    }

    private class C3ImportPathReference(element: C3ImportPath) : PsiReferenceBase<C3ImportPath>(element) {
        override fun resolve(): PsiElement {
            return element
        }
    }

    private class C3ModuleReference(element: C3ImportPath) : PsiPolyVariantReferenceBase<C3ImportPath>(element) {

        override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
            val list = StubIndex.getInstance().getAllKeys(
                ModuleIndex.KEY,
                element.project
            ).filter { it == element.text }.flatMap {
                StubIndex.getElements(
                    ModuleIndex.KEY,
                    it,
                    element.project,
                    GlobalSearchScope.allScope(element.project),
                    C3PsiElement::class.java
                ).filterIsInstance<C3Module>()
            }.map {
                PsiElementResolveResult(it)
            }

            return list.toTypedArray()
        }

    }
}