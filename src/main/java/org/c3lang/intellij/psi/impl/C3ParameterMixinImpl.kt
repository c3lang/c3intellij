package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.psi.C3Parameter
import org.c3lang.intellij.psi.FullyQualifiedName

abstract class C3ParameterMixinImpl(node: ASTNode) : C3Parameter, C3PsiNamedElementImpl(node) {

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getName(): String? {
        return nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override val nameIdentElement: LeafPsiElement?
        get() = lastChild as? LeafPsiElement

    override fun findTypeName(): FullyQualifiedName? {
        return type?.let(FullyQualifiedName::from)
    }
}