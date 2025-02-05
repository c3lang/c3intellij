package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.completion.LookupElementBuilderUtils
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3CallablePsiElement
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3PathIdentExpr
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.C3Types

class C3CallableReference(element: C3CallExpr) : PsiReferenceBase<C3CallExpr>(element) {

    fun resolveVariants(): Collection<C3CallablePsiElement> {
        val pathIdentExpr = element.expr as? C3PathIdentExpr ?: return emptyList()
        val pathReferences = pathIdentExpr.pathIdent.path?.references ?: return emptyList()
        val callName = pathIdentExpr.pathIdent.node.findChildByType(C3Types.IDENT)?.psi ?: return emptyList()

        val expressionCall = pathReferences.map {
            it.resolve()
        }.filterIsInstance<C3ImportPath>().map {
            "${it.text}::${callName.text}"
        }.singleOrNull() ?: return emptyList()

        return StubIndex.getElements(
            NameIndex.Companion.KEY,
            expressionCall,
            element.project,
            GlobalSearchScope.allScope(element.project),
            C3PsiElement::class.java
        ).filterIsInstance<C3CallablePsiElement>()
    }

    override fun resolve(): PsiElement? {
        return resolveVariants().singleOrNull()
    }

    override fun getVariants(): Array<Any> {
        return resolveVariants().map(LookupElementBuilderUtils::createFunctionDef).toTypedArray()
    }
}