package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import com.intellij.util.text.nullize
import org.c3lang.intellij.psi.C3StructMemberDeclaration
import org.c3lang.intellij.psi.FullyQualifiedName

class C3StructMemberDeclarationStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val structType: FullyQualifiedName?,
    val structPath: String?,
    val structPathType: FullyQualifiedName?,
) : StubBase<C3StructMemberDeclaration>(parent, elementType) {

    val fullPath = listOfNotNull(
        structType?.fullName,
        structPath
    ).joinToString(".").nullize()

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3StructMemberDeclaration,
    ) : this(
        parent = parent,
        elementType = elementType,
        structType = psi.structType,
        structPath = psi.structPath,
        structPathType = psi.structPathType,
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream,
    ) : this(
        parent = parent,
        elementType = elementType,
        structType = dataStream.readNullableUTFFast()?.let(FullyQualifiedName::parse),
        structPath = dataStream.readNullableUTFFast(),
        structPathType = dataStream.readNullableUTFFast()?.let(FullyQualifiedName::parse),
    )

    fun serialize(stream: StubOutputStream) {
        stream.writeNullableUTFFast(structType?.fullName)
        stream.writeNullableUTFFast(structPath)
        stream.writeNullableUTFFast(structPathType?.fullName)
    }
}