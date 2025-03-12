package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3FaultDefinition
import org.c3lang.intellij.psi.FullyQualifiedName

class C3FaultDefinitionStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val name: FullyQualifiedName
) : StubBase<C3FaultDefinition?>(parent, elementType) {

    constructor(
        parent: StubElement<out PsiElement?>,
        elementType: C3FaultDefinitionElementType,
        psi: C3FaultDefinition
    ) : this(
        parent = parent,
        elementType = elementType,
        name = FullyQualifiedName.from(psi)
    )

    constructor(
        parent: StubElement<*>,
        elementType: C3FaultDefinitionElementType,
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