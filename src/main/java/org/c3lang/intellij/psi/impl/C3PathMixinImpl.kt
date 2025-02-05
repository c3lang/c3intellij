package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findTopmostParentOfType
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.C3BaseType
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3CompoundStatement
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.C3PathIdentExpr
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.C3Types
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3PathMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3Path {

    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    override fun getName(): String? {
        return text
    }

    override fun setName(name: String): PsiElement {
        // TODO
        return this
    }

    override fun getTextOffset(): Int {
        return node.startOffset
    }

//    override fun getReference(): PsiReference? {
//        return C3PathReference(this)
//    }

    override fun shorten() {
        val idents = node.getChildren(
            TokenSet.create(C3Types.IDENT, C3Types.SCOPE)
        ).toMutableList()

        if (idents.size <= 2) {
            // bar::
            return
        }

        idents.removeLast() /*::*/
        idents.removeLast() /*IDENT*/

        // remove std::
        deleteChildRange(
            idents.first().psi,
            idents.last().psi
        )
    }

//    class C3PathReference(element: C3Path) : C3ReferenceBase<C3Path>(element) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            val paths = PsiTreeUtil.collectElements(
//                element.findTopmostParentOfType<C3CompoundStatement>()
//            ) { it is C3Path && it != element }.filterIsInstance<C3PsiElement>()
//
//            return paths.toList()
//        }
//
//        override fun isReferenceTo(other: PsiElement): Boolean {
//            val sourceRef = element.parentOfType<C3FullyQualifiedNamePsiElement>()?.reference?.resolve()
//            val targetRef = other.parentOfType<C3FullyQualifiedNamePsiElement>()?.reference?.resolve()
//
//            return sourceRef == targetRef
//        }
//    }

//    class SelfPsiPolyVariantReferenceBase(val path: C3Path) :
//        PsiPolyVariantReferenceBase<C3Path>(path) {
//
//        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
//            val result = mutableListOf<PsiElement>(path)
//
//            element.parentOfType<C3PathIdentExpr>()?.let { pathIdentExpr ->
//                pathIdentExpr.importProvider.getImportOf(pathIdentExpr).forEach { importPath ->
//                    result.add(importPath)
//                }
//            }
//
//            return result.map {
//                PsiElementResolveResult(it)
//            }.toTypedArray()
//        }
//
//        override fun isReferenceTo(other: PsiElement): Boolean {
//            val pathIdentExpr = path.parentOfType<C3PathIdentExpr>()
//
//            if (pathIdentExpr != null) {
//                val otherPathIdent = other.parentOfType<C3PathIdentExpr>() ?: return false
//
//                return isReferenceTo(pathIdentExpr, otherPathIdent)
//            }
//
//            return false
//
//        }
//
//        private fun isReferenceTo(
//            source: C3PathIdentExpr,
//            other: C3PathIdentExpr
//        ): Boolean {
//            val path = source.pathIdent.path ?: return false
//
//            return path.importProvider.contains(source.pathIdent) && source.text == other.text
//        }
//    }

    /*class SelfReference(element: C3Path) : PsiReferenceBase<C3Path>(element) {
        override fun resolve(): C3Path? {
            return element
        }

        override fun getRangeInElement(): TextRange {
            return element.textRangeInParent
        }
    }*/

    /*class SelfPolyReference(val path: C3Path) : PsiPolyVariantReferenceBase<C3Path>(path) {
        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
            element.parentOfType<C3CompoundStatement>()?.let {
                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
                    override fun isAccepted(other: PsiElement): Boolean {
                        other as? C3Path ?: return false

                        val source = path.parentOfType<C3PathIdentExpr>() ?: return false
                        val target = other.parentOfType<C3PathIdentExpr>() ?: return false

                        return source.text == target.text && path.importProvider.contains(source.pathIdent)
                    }
                })

                return collectElements.map { PsiElementResolveResult(it) }.toTypedArray()
            }
            return emptyArray()
        }

        override fun isSoft(): Boolean = true

    }*/

}


