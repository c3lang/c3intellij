package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.text.nullize
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.stubs.C3StructMemberDeclarationStub

abstract class C3StructMemberDeclarationMixinImpl : C3StubBasedPsiElementBase<C3StructMemberDeclarationStub>,
    C3StructMemberDeclaration {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3StructMemberDeclarationStub, nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3StructMemberDeclarationStub, nodeType: IElementType?, node: ASTNode?
    ) : super(stub, nodeType, node)

    override fun setName(name: String): PsiElement {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getName(): String? {
        return nameIdent
    }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val nameIdentElement: LeafPsiElement?
        get() {
            return if (structBody != null) {
                node.findChildByType(C3Types.IDENT)?.psi as? LeafPsiElement
            } else {
                identifierList?.firstChild as? LeafPsiElement
            }
        }

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override val declaredIn: FullyQualifiedName?
        get() = parentOfType<C3DeclaredInProvider>()?.declaredIn

    override val structPath: String?
        get() = greenStub?.structPath ?: collectStructPath()

    override val structType: FullyQualifiedName?
        get() = greenStub?.structType ?: collectStructType()

    override val structPathType: FullyQualifiedName?
        get() = greenStub?.structPathType ?: collectStructPathType()

    override val declaredInPath: String?
        get() {
            return listOfNotNull(
                parentOfType<C3DeclaredInPathProvider>()?.declaredInPath,
                identifierList?.text
            ).joinToString(".")
        }

    private fun collectStructPath(): String? {
        val name = if (structBody != null) {
            node.getChildren(TokenSet.create(C3Types.IDENT)).firstOrNull()?.text
        } else {
            identifierList?.text
        }
        return listOfNotNull(
            parentOfType<C3StructMemberDeclaration>()?.structPath,
            name
        ).joinToString(".").nullize()
    }

    private fun collectStructType(): FullyQualifiedName? {
        val structName = parentOfType<C3StructDeclaration>()?.typeName?.text ?: return null

        return FullyQualifiedName(
            moduleDefinition.moduleName,
            structName
        )
    }

    private fun collectStructPathType(): FullyQualifiedName? {

        val structPathType = if (structBody != null) {
            val innerStructName = node.getChildren(TokenSet.create(C3Types.IDENT)).firstOrNull()?.text
                ?: return null
            val structType = structType
                ?: return null

            FullyQualifiedName(structType.module, "${structType.name}.$innerStructName")
        } else {
            FullyQualifiedName.from(checkNotNull(type))
        }

        return structPathType
    }
}