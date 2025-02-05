package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3FuncDefImpl
import java.io.IOException

class C3FuncDefElementType : C3StubElementType<C3FuncDefStub?, C3FuncDef?>(C3StubElementTypeFactory.FUNC_DEF) {
    override fun createPsi(stub: C3FuncDefStub): C3FuncDef {
        return C3FuncDefImpl(stub, this)
    }

    override fun createStub(psi: C3FuncDef, stubElement: StubElement<out PsiElement?>): C3FuncDefStub {
        return C3FuncDefStub(
            parent = stubElement,
            elementType = this,
            psi = psi
        )
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3FuncDefStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3FuncDefStub, sink: IndexSink) {
        sink.occurrence(NameIndex.KEY, stub.fqName.fullName)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3FuncDefStub {
        return C3FuncDefStub(stubElement, this, dataStream)
    }

    companion object {
        @JvmStatic
        val instance: C3FuncDefElementType = C3FuncDefElementType()
    }
}
