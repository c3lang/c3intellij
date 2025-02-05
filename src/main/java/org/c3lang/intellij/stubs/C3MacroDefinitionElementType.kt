package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.index.TypeIndex
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.impl.C3MacroDefinitionImpl
import java.io.IOException

class C3MacroDefinitionElementType :
    C3StubElementType<C3MacroDefinitionStub, C3MacroDefinition>(C3StubElementTypeFactory.MACRO_DEFINITION) {

    override fun createPsi(stub: C3MacroDefinitionStub): C3MacroDefinition {
        return C3MacroDefinitionImpl(stub, this)
    }

    override fun createStub(psi: C3MacroDefinition, stubElement: StubElement<out PsiElement?>): C3MacroDefinitionStub {
        return C3MacroDefinitionStub(stubElement, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3MacroDefinitionStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3MacroDefinitionStub, sink: IndexSink) {
        sink.occurrence(NameIndex.KEY, stub.callName.fullName)
        stub.returnType?.type?.fullName?.let {
            sink.occurrence(TypeIndex.KEY, it)
        }
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3MacroDefinitionStub {
        return C3MacroDefinitionStub(
            stubElement,
            this,
            dataStream
        )
    }

    companion object {
        @JvmStatic
        val instance: C3MacroDefinitionElementType = C3MacroDefinitionElementType()
    }
}

