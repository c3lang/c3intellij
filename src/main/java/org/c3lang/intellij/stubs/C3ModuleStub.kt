package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.ModuleName

class C3ModuleStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val module: ModuleName?,
) : StubBase<C3Module?>(parent, elementType) {

    constructor(
        parent: StubElement<out PsiElement?>,
        elementType: C3ModuleElementType,
        psi: C3Module
    ) : this(
        parent = parent,
        elementType = elementType,
        module = ModuleName.from(psi)
    )

    constructor(
        parent: StubElement<*>,
        elementType: C3ModuleElementType,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        module = dataStream.readNullableUTFFast()?.let(::ModuleName)
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeNullableUTFFast(module?.value)
    }
}
