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

        return LookupElementBuilder.create(element, element.functionOrMacroName.fullName)
            .withLookupStrings(
                listOf(
                    element.functionOrMacroName.name,
                    element.functionOrMacroName.fullName
                )
            )
            .withIcon(icon)
            .appendTailText(element.parameterTypeNames, false)
            .withTypeText(element.returnTypeName?.type?.fullName ?: "")
    }

}