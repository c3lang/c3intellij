package org.c3lang.intellij.completion

import com.intellij.codeInsight.lookup.LookupElementBuilder
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.psi.C3CallablePsiElement
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3MacroDefinition

object LookupElementBuilderUtils {

    fun createFunctionDef(element: C3CallablePsiElement): LookupElementBuilder {
        val icon = when (element) {
            is C3FuncDef -> C3Icons.Nodes.FUNCTION
            is C3MacroDefinition -> C3Icons.Nodes.MACRO
            else -> null
        }

        val parameterList = element.parameterTypes.joinToString(",") {
            listOfNotNull(
                it.type?.fullName,
                it.name
            ).joinToString(" ")
        }

        return LookupElementBuilder.create(element, element.fqName.fullName)
            .withLookupStrings(
                listOf(
                    element.fqName.name,
                    element.fqName.fullName
                )
            )
            .withIcon(icon)
            .appendTailText("(${parameterList})", false)
            .withTypeText(element.returnType?.fullName ?: "")
    }

}