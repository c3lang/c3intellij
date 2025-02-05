package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import org.c3lang.intellij.completion.LookupElementBuilderUtils
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.psi.*

abstract class C3CallExprMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3CallExpr {

    override fun getNameIdentifier(): PsiElement? {
        return node.findChildByType(C3Types.PATH_IDENT_EXPR)
            ?.findChildByType(C3Types.PATH_IDENT)
            ?.getChildren(TokenSet.create(C3Types.IDENT))
            ?.last()?.psi
    }

    override fun getName(): String? {
        return nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset ?: super.getTextOffset()
    }

    override fun getTextRange(): TextRange {
        return nameIdentifier?.textRange ?: super.getTextRange()
    }

    override fun getReference(): PsiReference? {
        return C3CallExprReference(this)
    }

    override fun getReferences(): Array<PsiReference> {
        return arrayOf(
            C3CallExprReference(this),
            C3CallableReference(this),
            C3ImportPathReference(this),
        )
    }

    private class C3CallExprReference(element: C3CallExprMixin) : PsiReferenceBase<C3CallExprMixin>(element) {
        override fun resolve(): PsiElement {
            return element
        }
    }

    private class C3CallableReference(element: C3CallExpr) : PsiReferenceBase<C3CallExpr>(element) {

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
                NameIndex.KEY,
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

    private class C3ImportPathReference(element: C3CallExpr) : PsiReferenceBase<C3CallExpr>(element)  {
        override fun resolve(): PsiElement? {
            val possibleModules = C3CallableReference(element).resolveVariants().mapNotNull {
                it.moduleName?.value
            }
            val moduleName = possibleModules.singleOrNull() ?: return null
            val moduleSection = element.parentOfType<C3ImportProvider>() ?: return null

            return moduleSection.importDeclarations.flatMap { it.importPaths.importPathList }.singleOrNull {
                it.text == moduleName
            }
        }

    }
}