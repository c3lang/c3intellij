package org.c3lang.intellij.psi

import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.util.elementType

sealed interface StructDeclarationFields {
    val declaredIn: FullyQualifiedPath

    //    val fields: Map<String, ShortType>
    fun serialize(stream: StubOutputStream)

    data class Complex(
        override val declaredIn: FullyQualifiedPath,
//        override val fields: Map<String, ShortType>
    ) : StructDeclarationFields {
        override fun serialize(stream: StubOutputStream) {
            stream.writeUTFFast(declaredIn.typeName.fullName)
            stream.writeUTFFast(declaredIn.path)
//            stream.writeVarInt(fields.size)
//            fields.forEach { (name, value) ->
//                stream.writeUTFFast(name)
//                stream.writeUTFFast(value.fullName)
//            }
        }
    }

    data class Simple(
        override val declaredIn: FullyQualifiedPath,
    ) : StructDeclarationFields {
        //        override val fields: Map<String, ShortType> = emptyMap()
        override fun serialize(stream: StubOutputStream) {
            stream.writeUTFFast(declaredIn.typeName.fullName)
            stream.writeUTFFast(declaredIn.path)
//            stream.writeVarInt(0)
        }
    }

    companion object {
        fun build(source: C3StructMemberDeclaration): StructDeclarationFields? {
            val baseType = source.type?.baseType ?: return null
            val declaredIn = source.declaredIn ?: return null
            val declaredInPathPath = source.declaredInPath

            return when (baseType.firstChild.elementType) {
                C3Types.TYPE_IDENT -> {
                    baseType.moduleDefinition
                    Complex(
                        declaredIn = FullyQualifiedPath(
                            typeName = declaredIn,
                            path = declaredInPathPath ?: "<empty>"
                        ),
//                        fields = emptyMap()
                    )
                }

                else -> Simple(
                    FullyQualifiedPath(declaredIn, "-build-")
                )
            }
        }

        fun deserialize(stream: StubInputStream): StructDeclarationFields {
            val typeName = FullyQualifiedName.parse(stream.readUTFFast())
            val path = stream.readUTFFast()
//            val fields = stream.readVarInt()

            val declaredIn = FullyQualifiedPath(typeName, path)

            return Complex(
                declaredIn = declaredIn,
            )
        }
    }
}