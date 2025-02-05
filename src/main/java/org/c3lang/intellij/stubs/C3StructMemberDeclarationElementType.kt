package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.StructMemberDeclarationIndex
import org.c3lang.intellij.psi.C3StructMemberDeclaration
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3StructMemberDeclarationImpl
import java.io.IOException

class C3StructMemberDeclarationElementType :
    C3StubElementType<C3StructMemberDeclarationStub, C3StructMemberDeclaration>(C3StubElementTypeFactory.STRUCT_MEMBER_DECLARATION) {

    override fun createPsi(stub: C3StructMemberDeclarationStub): C3StructMemberDeclaration {
        return C3StructMemberDeclarationImpl(stub, this)
    }

    override fun createStub(
        psi: C3StructMemberDeclaration,
        parentStub: StubElement<out PsiElement?>
    ): C3StructMemberDeclarationStub {
        return C3StructMemberDeclarationStub(parentStub, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3StructMemberDeclarationStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): C3StructMemberDeclarationStub {
        return C3StructMemberDeclarationStub(parentStub, this, dataStream)
    }

    override fun indexStub(stub: C3StructMemberDeclarationStub, sink: IndexSink) {
        stub.fullPath?.let {
            sink.occurrence(StructMemberDeclarationIndex.KEY, it)
        }
    }

    companion object {
        @JvmStatic
        val instance: C3StructMemberDeclarationElementType = C3StructMemberDeclarationElementType()
    }
}
