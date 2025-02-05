package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.index.TypeIndex
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.impl.C3TypeNameImpl
import java.io.IOException

class C3TypeNameElementType : C3StubElementType<C3TypeNameStub, C3TypeName>(C3StubElementTypeFactory.TYPE_NAME) {

    override fun createPsi(stub: C3TypeNameStub): C3TypeName {
        return C3TypeNameImpl(stub, this)
    }

    override fun createStub(psi: C3TypeName, stubElement: StubElement<out PsiElement?>): C3TypeNameStub {
        return C3TypeNameStub(stubElement, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3TypeNameStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3TypeNameStub, sink: IndexSink) {
        sink.occurrence(NameIndex.KEY, stub.fqName.fullName)
        sink.occurrence(TypeIndex.KEY, stub.fqName.fullName)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3TypeNameStub {
        return C3TypeNameStub(
            stubElement,
            this,
            dataStream
        )
    }

    companion object {
        @JvmStatic
        val instance: C3TypeNameElementType = C3TypeNameElementType()
    }
}

