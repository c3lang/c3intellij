package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3ConstDeclarationStmt
import org.c3lang.intellij.psi.FullyQualifiedName

class C3ConstDeclarationStmtStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val name: FullyQualifiedName
) : StubBase<C3ConstDeclarationStmt?>(parent, elementType) {

    constructor(
        parent: StubElement<out PsiElement?>,
        elementType: C3ConstDeclarationStmtElementType,
        psi: C3ConstDeclarationStmt
    ) : this(
        parent = parent,
        elementType = elementType,
        name = FullyQualifiedName.from(psi)
    )

    constructor(
        parent: StubElement<*>,
        elementType: C3ConstDeclarationStmtElementType,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        name = FullyQualifiedName.parse(dataStream.readUTFFast())
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(name.fullName)
    }

}