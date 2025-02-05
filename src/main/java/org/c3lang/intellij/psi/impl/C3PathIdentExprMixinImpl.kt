package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.psi.C3PathIdentExpr

abstract class C3PathIdentExprMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3PathIdentExpr {

//    override val nameIdentElement: LeafPsiElement?
//        get() = pathIdent.nameIdentElement
//    override val nameIdent: String?
//        get() = pathIdent.nameIdent

//    override val declaredType: FullyQualifiedName?
//        get() {
//            val localDeclAfterType = pathIdent.reference?.resolve() as? C3LocalDeclAfterType ?: return null
//            val localDeclarationStmt = localDeclAfterType.parentOfType<C3LocalDeclarationStmt>() ?: return null
//
//            return localDeclarationStmt.optionalTypeFQN
//        }


    //    class LocalDeclAfterTypeReference(element: C3PathIdentExpr) : C3ReferenceBase<C3PathIdentExpr>(element) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            element.findTopmostParentOfType<C3CompoundStatement>()?.let {
//
//                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
//                    override fun isAccepted(other: PsiElement): Boolean {
//                        val localDeclAfterType = other as? C3LocalDeclAfterType ?: return false
//                        return localDeclAfterType.nameIdent == element.nameIdent && localDeclAfterType.textOffset < element.textOffset
//                    }
//                })
//
//                return collectElements.filterIsInstance<C3PsiElement>()
//            }
//
//            return emptyList()
//        }
//    }

//    private class ImportPathReference(val pathIdentExpr: C3PathIdentExpr) :
//        PsiReferenceBase<C3PathIdentExpr>(pathIdentExpr) {
//        override fun resolve(): C3ImportPath? = getVariants().firstOrNull()
//
//        override fun getVariants(): Array<out C3ImportPath> {
//            val importPaths = pathIdentExpr.importProvider.getImportOf(pathIdentExpr)
//
//            return importPaths.toTypedArray()
//        }
//
////        override fun getRangeInElement(): TextRange {
////            return TextRange.create(0, 0)
////        }
//
//        override fun isReferenceTo(element: PsiElement): Boolean {
//            return super.isReferenceTo(element)
//        }
//    }
//
//    private class SelfPsiPolyVariantReferenceBase(val pathIdentExpr: C3PathIdentExpr) :
//        PsiReferenceBase<C3PathIdentExpr>(pathIdentExpr) {
//
//        //        override fun resolve(): PsiElement? {
////            return element.pathIdent
////        }
//        override fun resolve(): PsiElement? {
//            return pathIdentExpr
//        }
//
//        override fun getVariants(): Array<out PsiElement> {
//            val collectElements = PsiTreeUtil.collectElements(
//                pathIdentExpr.containingFile
//            ) {
//                it is C3PathIdentExpr
//            }
//
//            return collectElements
//        }
//
////        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
////            return arrayOf(PsiElementResolveResult(pathIdentExpr))
////        }
////        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
////            val result = pathIdentExpr.importProvider.getImportOf(pathIdentExpr).map {
////                PsiElementResolveResult(it)
////            }
////
////            return arrayOf(PsiElementResolveResult(pathIdentExpr))
////        }
////        override fun isSoft(): Boolean {
////            return true
////        }
//
//        override fun getRangeInElement(): TextRange {
//            return TextRange.create(0, 5)
//        }
//
//        override fun isReferenceTo(other: PsiElement): Boolean {
//            val otherPathIdent = other as? C3PathIdentExpr ?: return false
//
//            return isReferenceTo(pathIdentExpr, otherPathIdent)
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
//
////    override fun getTextRange(): TextRange? {
////        return nameIdentifier?.textRange ?: super.getTextRange()
////    }
//
////    override fun getReference(): PsiReference = SelfPsiPolyVariantReferenceBase(this)
//
//    /*class SelfPsiPolyVariantReferenceBase(val pathIdentExpr: C3PathIdentExpr) :
//        PsiPolyVariantReferenceBase<C3PathIdentExpr>(pathIdentExpr) {
//
//        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
//            val references = element.pathIdent.path?.references ?: return emptyArray()
//            val callName = element.name ?: return emptyArray()
//
//            val callables = references.mapNotNull {
//                it.resolve()
//            }.filterIsInstance<C3ImportPath>().flatMap {
//                NameIndexService.findByNameEndsWith(it, callName)
//            }.mapNotNull {
//                it.nameElement
//            }.map {
//                PsiElementResolveResult(it)
//            }
//
//            return callables.toTypedArray()
//        }
//
//        override fun getRangeInElement(): TextRange {
//            return element.nameIdentifier?.textRange ?: calculateDefaultRangeInElement()
//        }
//
//    }*/
//
////    class SelfReference(element: C3PathIdentExpr) : PsiReferenceBase<C3PathIdentExpr>(element) {
////        override fun resolve(): C3PathIdentExpr = element
////
////        override fun getRangeInElement(): TextRange {
////            return super.getRangeInElement()
////        }
////    }
////
////    class FuncDefReference(element: C3PathIdentExpr) : PsiPolyVariantReferenceBase<C3PathIdentExpr>(element) {
////        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
////            val references = element.pathIdent.path?.references ?: return emptyArray()
////            val callName = element.name ?: return emptyArray()
////
////            val callables = references.mapNotNull {
////                it.resolve()
////            }.filterIsInstance<C3ImportPath>().flatMap {
////                NameIndexService.findByNameEndsWith(it, callName)
////            }.mapNotNull {
////                it.nameElement
////            }.map {
////                PsiElementResolveResult(it)
////            }
////
////            return callables.toTypedArray()
////        }
////
////        override fun getRangeInElement(): TextRange {
////            return element.nameIdentifier?.textRange ?: calculateDefaultRangeInElement()
////        }
////    }
//
////    class LocalDeclAfterTypeReference(element: C3PathIdentExpr) : PsiReferenceBase<C3PathIdentExpr>(element) {
////        override fun resolve(): PsiElement? {
////            val path = element
////
////            element.parentOfType<C3CompoundStatement>()?.let {
////                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
////                    override fun isAccepted(other: PsiElement): Boolean {
////                        val type = other as? C3LocalDeclAfterType ?: return false
////
////                        return type.nameIdent == path.nameIdent && type.textOffset < path.textOffset
////                    }
////                })
////
////                return collectElements.firstOrNull()
////            }
////            return null
////        }
////    }
}