package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.psi.C3FaultDefinition
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3FaultDefinitionImpl
import java.io.IOException

class C3FaultDefinitionElementType :
    C3StubElementType<C3FaultDefinitionStub, C3FaultDefinition>(C3StubElementTypeFactory.FAULT_DEFINITION) {

    override fun createPsi(stub: C3FaultDefinitionStub): C3FaultDefinition {
        return C3FaultDefinitionImpl(stub, this)
    }

    override fun createStub(psi: C3FaultDefinition, stubElement: StubElement<out PsiElement?>): C3FaultDefinitionStub {
        return C3FaultDefinitionStub(stubElement, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3FaultDefinitionStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3FaultDefinitionStub, sink: IndexSink) {
        sink.occurrence(NameIndex.KEY, stub.name.fullName)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3FaultDefinitionStub {
        return C3FaultDefinitionStub(
            stubElement,
            this,
            dataStream
        )
    }

    companion object {
        @JvmStatic
        val instance: C3FaultDefinitionElementType = C3FaultDefinitionElementType()
    }
}