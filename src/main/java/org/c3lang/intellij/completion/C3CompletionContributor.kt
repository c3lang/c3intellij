package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement

class C3CompletionContributor : CompletionContributor()
{
    init
    {
        val pattern = psiElement()/*.inside(C3ModuleDefinition::class.java)*/

        extend(CompletionType.BASIC, pattern, FunctionCompletionContributor)
        extend(CompletionType.BASIC, pattern, TypeCompletionContributor)
        extend(CompletionType.BASIC, pattern, ImportCompletionContributor)
        extend(CompletionType.BASIC, pattern, ConstCompletionContributor)
        extend(CompletionType.BASIC, pattern, FaultCompletionContributor)
        extend(CompletionType.BASIC, pattern, TailExprCompletionContributor)
        extend(CompletionType.BASIC, pattern, InitializerListCompletionContributor)
        extend(CompletionType.BASIC, pattern, DocCommentCompletionContributor)
        extend(CompletionType.BASIC, pattern, TopLevelCompletionContributor)
    }

    override fun beforeCompletion(context: CompletionInitializationContext)
    {
        // path
        context.dummyIdentifier = DUMMY_IDENTIFIER
    }
}