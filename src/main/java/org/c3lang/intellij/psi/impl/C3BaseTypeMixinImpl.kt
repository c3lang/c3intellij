package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.C3BaseType
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.C3TypeName
import org.c3lang.intellij.psi.C3Types
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3BaseTypeMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3BaseType {

    override fun getName(): String? {
        return nameIdent
    }

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset
            ?: node.startOffset
    }

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override val nameIdentElement: LeafPsiElement?
        get() = lastChild.takeIf {
            it.node.elementType == C3Types.TYPE_IDENT
        } as? LeafPsiElement

    override fun getReference(): PsiReference? {
        return C3TypeNameReference(this)
    }

    private class C3TypeNameReference(element: C3BaseType) : C3ReferenceBase<C3BaseType>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val importProvider = element.moduleDefinition

            val elements = NameIndexService.findType(element, element.project)
                .filter {
                    importProvider.isSameModule(it) || importProvider.isImported(it)
                }.filterIsInstance<C3TypeName>()

            return elements
        }

        override fun getRangeInElement(): TextRange {
            return super.getRangeInElement()
        }
    }
}