package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.ParamType.Companion.toParamTypeList
import org.c3lang.intellij.psi.ShortType.Companion.toShortType
import org.c3lang.intellij.stubs.C3FuncDefStub

abstract class C3FuncDefMixinImpl : C3StubBasedPsiElementBase<C3FuncDefStub>, C3FuncDef {

    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3FuncDefStub, nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3FuncDefStub, nodeType: IElementType?, node: ASTNode?
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
            return funcHeader.funcName.lastChild as? LeafPsiElement
        }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val sourceFileName: String
        get() = containingFile.name

    override val fqName: FullyQualifiedName
        get() = stub?.fqName
            ?: FullyQualifiedName.from(checkNotNull(this.funcHeader), moduleName)

    override val moduleName: ModuleName?
        get() = stub?.module
            ?: ModuleName.from(this)

    override val type: ShortType?
        get() = stub?.type
            ?: funcHeader.funcName.type?.toShortType()

    override val returnType: ShortType?
        get() = stub?.returnType
            ?: funcHeader.optionalType.type.toShortType()

    override val parameterTypes: List<ParamType>
        get() = stub?.parameterTypes
            ?: fnParameterList.parameterList?.paramDeclList.toParamTypeList()
}