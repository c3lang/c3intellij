package org.c3lang.intellij.psi.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.psi.C3NameIdentProvider
import org.c3lang.intellij.psi.C3PsiElement

abstract class C3ReferenceBase<T : C3PsiElement>(
    element: T,
    val referenceNameElement: PsiElement? = null
) : PsiPolyVariantReferenceBase<T>(element), C3Reference {

    override fun resolve(): C3PsiElement? = super.resolve() as C3PsiElement?

    override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> =
        multiResolve().map { PsiElementResolveResult(it) }.toTypedArray()

//    final override fun getRangeInElement(): TextRange = super.getRangeInElement()

    open val T.referenceAnchor: PsiElement? get() = referenceNameElement

//    final override fun calculateDefaultRangeInElement(): TextRange {
//        val anchor = element.referenceAnchor ?: return TextRange.EMPTY_RANGE
//        check(anchor.parent === element)
//        return TextRange.from(anchor.startOffsetInParent, anchor.textLength)
//    }

    override fun handleElementRename(newName: String): PsiElement {
        doRename(referenceNameElement ?: element, newName)
        return element
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun equals(other: Any?): Boolean = other is C3ReferenceBase<*> && element == other.element

    override fun hashCode(): Int = element.hashCode()

    companion object {
        fun doRename(element: PsiElement, newName: String) {
            when (element) {
                is LeafPsiElement -> {
                    element.replaceWithText(newName)
                }

                is C3NameIdentProvider -> {
                    element.nameIdentElement?.replaceWithText(newName)
                }

                else -> {
                    TODO("rename for $element for implemented yet")
                }
            }
        }
    }
}