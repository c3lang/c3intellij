package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.or
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3ParserDefinition

object DocCommentCompletionContributor : CompletionProvider<CompletionParameters>()
{
    private val pattern = or(
        psiElement().inside(psiElement().withElementType(C3ParserDefinition.DOC_COMMENT)),
    )

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet)
    {
        if (!pattern.accepts(parameters.position) && !pattern.accepts(parameters.originalPosition))
        {
            return
        }

        listOf(
            "@param",
            "@return",
            "@return!",
            "@deprecated",
            "@require",
            "@ensure",
            "@pure",
            "[in]",
            "[&in]",
            "[out]",
            "[&out]",
            "[inout]",
            "[&inout]"
        ).forEach {
            result.addElement(LookupElementBuilder.create(it))
        }
    }
}