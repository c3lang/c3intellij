package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.C3PathIdentExpr
import org.c3lang.intellij.psi.C3PsiElement


//class C3PathReference2(element: C3Path) : C3ReferenceBase<C3Path>(element) {
//
//    override fun isReferenceTo(other: PsiElement): Boolean {
//        return element.isReferenceTo(other)
//    }
//
//    override fun multiResolve(): Collection<C3PsiElement> {
//        val result = hashSetOf<C3PsiElement>()
//
//        element.parentOfType<C3PathIdentExpr>()?.let { pathIdentExpr ->
//            result += element.importProvider.getImportOf(pathIdentExpr)
//        }
//
//        return result
//    }
//
//    companion object {
//        private fun PsiElement.isReferenceTo(other: PsiElement): Boolean {
//            val path = this as? C3Path ?: return false
//            val pathIdentExpr = path.parentOfType<C3PathIdentExpr>()
//
//            if (pathIdentExpr != null) {
//                val otherPathIdent = other.parentOfType<C3PathIdentExpr>() ?: return false
//
//                return isReferenceTo(pathIdentExpr, otherPathIdent)
//            }
//
//            return false
//        }
//
//        private fun isReferenceTo(
//            source: C3PathIdentExpr,
//            other: C3PathIdentExpr
//        ): Boolean {
//
//            val path = source.pathIdent.path ?: return false
//
//            return path.importProvider.contains(source.pathIdent) && source.text == other.text
//        }
//    }
//}