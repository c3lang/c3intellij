package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.StructDeclarationIndex
import org.c3lang.intellij.psi.C3StructDeclaration
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3StructDeclarationImpl
import java.io.IOException

class C3StructDeclarationElementType :
    C3StubElementType<C3StructDeclarationStub, C3StructDeclaration>(C3StubElementTypeFactory.STRUCT_DECLARATION) {

    override fun createPsi(stub: C3StructDeclarationStub): C3StructDeclaration {
        return C3StructDeclarationImpl(stub, this)
    }

    override fun createStub(
        psi: C3StructDeclaration,
        parentStub: StubElement<out PsiElement?>
    ): C3StructDeclarationStub {
        return C3StructDeclarationStub(parentStub, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3StructDeclarationStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): C3StructDeclarationStub {
        return C3StructDeclarationStub(parentStub, this, dataStream)
    }

    override fun indexStub(stub: C3StructDeclarationStub, sink: IndexSink) {
        sink.occurrence(StructDeclarationIndex.KEY, stub.typeName.fullName)
//        sink.occurrence(StructDeclarationIndex.KEY, "stub-${stub.hashCode()}")

//        if (stub.fullName != null) {
//            sink.occurrence(StructDeclarationIndex.KEY, stub.fullName)
//        } else {
//            // all null
//            println("all nulls")
//        }
    }

    companion object {
        @JvmStatic
        val instance: C3StructDeclarationElementType = C3StructDeclarationElementType()
    }
}
