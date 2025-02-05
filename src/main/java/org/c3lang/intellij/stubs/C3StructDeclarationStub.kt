package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.C3StructDeclaration
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.StructField
import org.c3lang.intellij.psi.StructField.Companion.collectFields

class C3StructDeclarationStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val typeName: FullyQualifiedName,
    val fields: List<StructField>
) : StubBase<C3StructDeclaration>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3StructDeclaration,
    ) : this(
        parent = parent,
        elementType = elementType,
        typeName = FullyQualifiedName.from(psi.typeName),
        fields = psi.structBody?.collectFields(null) ?: emptyList()
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream,
    ) : this(
        parent = parent,
        elementType = elementType,
        typeName = FullyQualifiedName.parse(dataStream.readUTFFast()),
        fields = dataStream.deserializeStructFields()
    )

    fun serialize(stream: StubOutputStream) {
        stream.writeUTFFast(typeName.fullName)
        stream.writeUTFFast(fields.size.toString())
        fields.forEach { field ->
            stream.writeUTFFast(field.type.fullName)
            stream.writeNullableUTFFast(field.name)
        }
    }

    companion object {
        fun StubInputStream.deserializeStructFields(): List<StructField> {
            val fieldCount = readUTFFast().toInt()
            val result = mutableListOf<StructField>()

            (0 until fieldCount).forEach { i ->
                val typeFullName = readUTFFast()
                val name = readNullableUTFFast()

                StructField(
                    checkNotNull(name), FullyQualifiedName.parse(typeFullName)
                )
            }

            return result
        }
    }
}