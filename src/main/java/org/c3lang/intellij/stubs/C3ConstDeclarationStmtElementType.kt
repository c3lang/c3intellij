package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.psi.C3ConstDeclarationStmt
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3ConstDeclarationStmtImpl
import java.io.IOException

class C3ConstDeclarationStmtElementType :
    C3StubElementType<C3ConstDeclarationStmtStub, C3ConstDeclarationStmt>(C3StubElementTypeFactory.CONST_DECLARATION_STMT) {

    override fun createPsi(stub: C3ConstDeclarationStmtStub): C3ConstDeclarationStmt {
        return C3ConstDeclarationStmtImpl(stub, this)
    }

    override fun createStub(psi: C3ConstDeclarationStmt, stubElement: StubElement<out PsiElement?>): C3ConstDeclarationStmtStub {
        return C3ConstDeclarationStmtStub(stubElement, this, psi)
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3ConstDeclarationStmtStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3ConstDeclarationStmtStub, sink: IndexSink) {
        sink.occurrence(NameIndex.KEY, stub.name.fullName)
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3ConstDeclarationStmtStub {
        return C3ConstDeclarationStmtStub(
            stubElement,
            this,
            dataStream
        )
    }

    companion object {
        @JvmStatic
        val instance: C3ConstDeclarationStmtElementType = C3ConstDeclarationStmtElementType()
    }
}