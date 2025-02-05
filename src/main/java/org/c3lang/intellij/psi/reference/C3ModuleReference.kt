package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.index.ModuleIndex
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.C3PsiElement

class C3ModuleReference(element: C3ImportPath) : PsiPolyVariantReferenceBase<C3ImportPath>(element) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val list = StubIndex.getInstance().getAllKeys(
            ModuleIndex.Companion.KEY,
            element.project
        ).filter { it == element.text }.flatMap {
            StubIndex.getElements(
                ModuleIndex.Companion.KEY,
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