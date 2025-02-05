package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3EnumConstant
import org.c3lang.intellij.psi.C3EnumDeclaration
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName

class C3EnumConstantStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val module: ModuleName?,
    val fqName: FullyQualifiedName,
    val constIdent : String
) : StubBase<C3EnumConstant>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3EnumConstant,
        module: ModuleName? = ModuleName.from(psi),
    ) : this(
        parent = parent,
        elementType = elementType,
        module = module,
        fqName = psi.fqName,
        constIdent = psi.constIdent
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        module = dataStream.readNullableUTFFast()?.let(::ModuleName),
        fqName = FullyQualifiedName.parse(dataStream.readUTFFast()),
        constIdent = dataStream.readUTFFast()
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeNullableUTFFast(module?.value)
        dataStream.writeUTFFast(fqName.fullName)
        dataStream.writeUTFFast(constIdent)
    }

}