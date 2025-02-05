package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.C3LocalDeclAfterType
import org.c3lang.intellij.psi.C3LocalDeclarationStmt
import org.c3lang.intellij.psi.FullyQualifiedName

abstract class C3LocalDeclAfterTypeMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3LocalDeclAfterType {

    override fun getName(): String? = nameIdent

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset ?: super.getTextOffset()
    }

    override val nameIdentElement: LeafPsiElement?
        get() = firstChild as? LeafPsiElement

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override fun findTypeName(): FullyQualifiedName? {
        return parentOfType<C3LocalDeclarationStmt>()?.findTypeName()
    }
}