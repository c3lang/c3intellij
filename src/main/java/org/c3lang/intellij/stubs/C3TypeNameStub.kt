package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3TypeName
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName

class C3TypeNameStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val fqName: FullyQualifiedName,
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
        fqName = FullyQualifiedName.parse(dataStream.readUTFFast()),
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
        fqName = FullyQualifiedName.from(psi),
        moduleName = ModuleName.from(psi),
        typeEnum = C3TypeEnum.find(psi),
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(fqName.fullName)
        dataStream.writeNullableUTFFast(moduleName?.value)
        dataStream.writeUTFFast(typeEnum.name)
    }

}