package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import org.c3lang.intellij.psi.*

object FunctionLookupElement {
    fun buildFunctionLookupElement(
        funcDef: C3FuncDef,
        insertHandler: InsertHandler<LookupElement>
    ): LookupElementBuilder {
        var lookupElementBuilder = LookupElementBuilder.create(funcDef, funcDef.getFunctionFullName())
            .withIcon(AllIcons.Nodes.Function)
            .appendTailText(funcDef.getFunctionParameters(), false)
            .withTypeText(funcDef.getReturnType())
            .withInsertHandler(insertHandler)

        funcDef.getModuleName()?.let {
            lookupElementBuilder = lookupElementBuilder
                .appendTailText(" ", true)
                .appendTailText(it, true)
        }

        return lookupElementBuilder
    }

    data class Query(
        val text: String
    )
}
