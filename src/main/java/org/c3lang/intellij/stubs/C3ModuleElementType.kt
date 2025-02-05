package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.index.ModuleIndex
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3ModuleImpl
import java.io.IOException

class C3ModuleElementType : C3StubElementType<C3ModuleStub?, C3Module?>(C3StubElementTypeFactory.MODULE) {

    override fun createPsi(stub: C3ModuleStub): C3Module {
        return C3ModuleImpl(stub, this)
    }

    override fun createStub(psi: C3Module, parentStub: StubElement<out PsiElement?>): C3ModuleStub {
        return C3ModuleStub(parentStub, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3ModuleStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): C3ModuleStub {
        return C3ModuleStub(parentStub, this, dataStream)
    }

    override fun indexStub(stub: C3ModuleStub, sink: IndexSink) {
        stub.module?.let {
            sink.occurrence(ModuleIndex.KEY, it.value)
        }
    }


    companion object {
        @JvmStatic
        val instance: C3ModuleElementType = C3ModuleElementType()
    }
}
