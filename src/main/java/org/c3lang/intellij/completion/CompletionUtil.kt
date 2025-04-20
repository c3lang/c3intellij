package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

object CompletionUtil
{
    fun provideCompletionsAfterSymbols(parameters: CompletionParameters, result: CompletionResultSet, symbols: List<String>, completions: List<String>, additionalConditions: List<Boolean> = listOf())
    {
        val matchingSymbol = symbols.find { symbol ->
            isCursorDirectlyAfterSymbol(parameters, symbol)
        }
        if (matchingSymbol == null) return

        additionalConditions.forEach { condition ->
            if (!condition) return
        }

        completions.forEach { completion ->
            result.addElement(LookupElementBuilder.create(completion))
        }
    }

    fun provideCompletionsAfterSymbolWithInsertHandler(parameters: CompletionParameters, result: CompletionResultSet, symbol: String, completions: Map<String, InsertHandler<LookupElement>?>, additionalConditions: List<Boolean> = listOf())
    {
        if (!isCursorDirectlyAfterSymbol(parameters, symbol)) return

        additionalConditions.forEach { condition ->
            if (!condition) return
        }

        completions.forEach { completion ->
            if (completion.value == null)
            {
                result.addElement(LookupElementBuilder.create(completion.key))
            } else
            {
                result.addElement(LookupElementBuilder.create(completion.key).withInsertHandler(completion.value))
            }
        }
    }

    fun provideCompletionsAfterSymbol(parameters: CompletionParameters, result: CompletionResultSet, symbol: String, completions: List<String>, additionalConditions: List<Boolean> = listOf())
    {
        if (!isCursorDirectlyAfterSymbol(parameters, symbol)) return

        additionalConditions.forEach { condition ->
            if (!condition) return
        }

        completions.forEach { completion ->
            result.addElement(LookupElementBuilder.create(completion))
        }
    }

    private fun isCursorDirectlyAfterSymbol(parameters: CompletionParameters, symbol: String): Boolean
    {
        val offset = parameters.offset
        if (offset < symbol.length) return false

        val document = parameters.editor.document
        val chars = document.charsSequence

        for (i in symbol.indices) {
            if (chars[offset - symbol.length + i] != symbol[i]) {
                return false
            }
        }
        return true
    }
}