package org.c3lang.intellij.psi

import com.intellij.psi.tree.TokenSet
import com.intellij.util.text.nullize

data class StructField(
    val name: String,
    val type: FullyQualifiedName
) {
    companion object {
        fun C3StructBody.collectFields(parentName: String?): List<StructField> {
            val module = moduleDefinition

            val map = structMemberDeclarationList.flatMap {
                val memberName = listOfNotNull(parentName, it.fieldName)
                    .joinToString(".").nullize()

                val structBody = it.structBody
                if (structBody != null) {
                    val fields = mutableListOf<StructField>()
                    if (memberName != null) {
                        val structType = it.structType ?: return emptyList()
                        fields += StructField(memberName, FullyQualifiedName(structType.module, "${structType.name}.$memberName"))
                    }

                    structBody.collectFields(memberName) + fields
                } else {
                    val type = it.type ?: return emptyList()
                    val typeFQN = module.resolve(type).singleOrNull()
                        ?: FullyQualifiedName(null, type.text)
                    memberName?.let { listOf(StructField(it, typeFQN)) } ?: emptyList()
                }
            }

            return map
        }

        val C3StructMemberDeclaration.fieldName: String?
            get() {
                return identifierList?.text
                    ?: node.getChildren(TokenSet.create(C3Types.IDENT)).firstOrNull()?.text
            }

    }
}