package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.stubs.C3EnumConstantStub
import org.c3lang.intellij.stubs.C3TypeEnum

abstract class C3EnumConstantMixinImpl : C3StubBasedPsiElementBase<C3EnumConstantStub>, C3EnumConstant {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3EnumConstantStub, nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3EnumConstantStub, nodeType: IElementType?, node: ASTNode?
    ) : super(stub, nodeType, node)

    override fun getName(): String? {
        return nameIdent
    }

    override fun setName(name: String): PsiElement {
        nameIdentElement.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentElement.textOffset
    }

    override val nameIdent: String
        get() = nameIdentElement.text

    override val nameIdentElement: LeafPsiElement
        get() = lastChild.takeIf { it.elementType == C3Types.CONST_IDENT } as LeafPsiElement

    override val fqName: FullyQualifiedName
        get() = greenStub?.fqName ?: parentFullyQualifiedName

    override val constIdent: String
        get() = greenStub?.constIdent ?: nameIdent

    override val moduleName: ModuleName?
        get() = greenStub?.module ?: moduleDefinition.moduleName

    override val typeEnum: C3TypeEnum
        get() = C3TypeEnum.ENUM

    private val parentFullyQualifiedName : FullyQualifiedName
        get() {
            return checkNotNull(
                parentOfType<C3FullyQualifiedNamePsiElement>()
            ).fqName
        }
}