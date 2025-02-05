package org.c3lang.intellij.stubs

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.c3lang.intellij.index.ReturnTypeIndex
import org.c3lang.intellij.psi.C3EnumConstant
import org.c3lang.intellij.psi.C3EnumDeclaration
import org.c3lang.intellij.psi.C3StubElementType
import org.c3lang.intellij.psi.C3StubElementTypeFactory
import org.c3lang.intellij.psi.impl.C3EnumConstantImpl
import org.c3lang.intellij.psi.impl.C3EnumDeclarationImpl
import java.io.IOException

class C3EnumConstantElementType : C3StubElementType<C3EnumConstantStub, C3EnumConstant>(C3StubElementTypeFactory.ENUM_DECLARATION) {
    override fun createPsi(stub: C3EnumConstantStub): C3EnumConstant {
        return C3EnumConstantImpl(stub, this)
    }

    override fun createStub(psi: C3EnumConstant, stubElement: StubElement<out PsiElement?>): C3EnumConstantStub {
        return C3EnumConstantStub(
            parent = stubElement,
            elementType = this,
            psi = psi
        )
    }

    @Throws(IOException::class)
    override fun serialize(stub: C3EnumConstantStub, dataStream: StubOutputStream) {
        stub.serialize(dataStream)
    }

    override fun indexStub(stub: C3EnumConstantStub, sink: IndexSink) {
//        stub.entries.forEach {
//            sink.occurrence(ReturnTypeIndex.KEY, "${stub.fqName.fullName}.$it")
//        }
    }

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, stubElement: StubElement<*>): C3EnumConstantStub {
        return C3EnumConstantStub(stubElement, this, dataStream)
    }

    companion object {
        @JvmStatic
        val instance: C3EnumConstantElementType = C3EnumConstantElementType()
    }
}
