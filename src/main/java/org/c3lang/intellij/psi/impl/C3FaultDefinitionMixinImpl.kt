package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.c3lang.intellij.psi.C3FaultDefinition
import org.c3lang.intellij.psi.C3Types
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.stubs.C3FaultDefinitionStub

abstract class C3FaultDefinitionMixinImpl : C3StubBasedPsiElementBase<C3FaultDefinitionStub>, C3FaultDefinition {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3FaultDefinitionStub,
        nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3FaultDefinitionStub,
        nodeType: IElementType?,
        node: ASTNode?
    ) : super(stub, nodeType, node)

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

    override val nameIdent: String?
        get() {
            return nameIdentElement?.text
        }

    override val nameIdentElement: LeafPsiElement?
        get() {
            return node.getChildren(TokenSet.create(C3Types.CONST_IDENT)).firstOrNull() as? LeafPsiElement
        }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val fqName: FullyQualifiedName
        get() {
            return stub?.name ?: checkNotNull(FullyQualifiedName.from(this, ModuleName.from(this)))
        }

}