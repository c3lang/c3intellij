package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.C3StructDeclaration
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.StructField
import org.c3lang.intellij.psi.StructField.Companion.collectFields
import org.c3lang.intellij.stubs.C3StructDeclarationStub

abstract class C3StructDeclarationMixinImpl : C3StubBasedPsiElementBase<C3StructDeclarationStub>, C3StructDeclaration {

    constructor(node: ASTNode) : super(node)

    constructor(stub: C3StructDeclarationStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(stub: C3StructDeclarationStub, nodeType: IElementType?, node: ASTNode?) : super(stub, nodeType, node)

    override val fields: List<StructField>
        get() = greenStub?.fields ?: structBody?.collectFields(null) ?: emptyList()

    override val declaredIn: FullyQualifiedName?
        get() = this.typeName?.let {
            FullyQualifiedName.from(it)
        }
}