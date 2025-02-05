package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.C3ConstDeclarationStmt
import org.c3lang.intellij.psi.C3PathConst
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.C3Types
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3PathConstMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3PathConst {
    override fun getName(): String? {
        return nameIdent
    }

    override fun setName(name: String): PsiElement {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override val nameIdentElement: LeafPsiElement?
        get() = firstChild.takeIf { it.elementType == C3Types.CONST_IDENT } as? LeafPsiElement?

    override fun getReference(): PsiReference? {
        return C3ConstDeclarationStmtReference(this)
    }

    private class C3ConstDeclarationStmtReference(element: C3PathConst) : C3ReferenceBase<C3PathConst>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val moduleDefinition = element.moduleDefinition

            val result = NameIndexService.findByNameEndsWith(element.text, element.project).filter {
                moduleDefinition.containsImportOrSameModule(it)
            }.filterIsInstance<C3ConstDeclarationStmt>()

            return result
        }

        override fun getRangeInElement(): TextRange {
            return TextRange(
                element.path?.textLength ?: 0,
                element.textLength
            )
        }
    }
}