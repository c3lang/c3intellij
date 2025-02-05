package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3TypeName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.psi.TypeName

class C3TypeNameStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val typeName: TypeName,
    val moduleName: ModuleName?,
    val typeEnum: C3TypeEnum
) : StubBase<C3TypeName>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        typeName = TypeName.deserialize(dataStream.readUTFFast()),
        moduleName = dataStream.readNullableUTFFast()?.let(::ModuleName),
        typeEnum = C3TypeEnum.valueOf(dataStream.readUTFFast()),
    )

    constructor(
        parent: StubElement<out PsiElement?>,
        elementType: C3TypeNameElementType,
        psi: C3TypeName
    ) : this(
        parent = parent,
        elementType = elementType,
        typeName = TypeName.from(psi),
        moduleName = ModuleName.from(psi),
        typeEnum = C3TypeEnum.find(psi),
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(typeName.fullName)
        dataStream.writeNullableUTFFast(moduleName?.value)
        dataStream.writeUTFFast(typeEnum.name)
    }

}