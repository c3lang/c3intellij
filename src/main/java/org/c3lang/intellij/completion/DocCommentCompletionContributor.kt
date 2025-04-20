package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3ParserDefinition
import org.c3lang.intellij.completion.CompletionUtil.provideCompletionsAfterSymbol
import org.c3lang.intellij.completion.CompletionUtil.provideCompletionsAfterSymbols
import org.c3lang.intellij.psi.C3FuncDefinition
import org.c3lang.intellij.psi.C3MacroDefinition
import org.eclipse.lsp4j.jsonrpc.messages.Either

object DocCommentCompletionContributor : CompletionProvider<CompletionParameters>()
{
    private val pattern = psiElement().inside(psiElement().withElementType(C3ParserDefinition.DOC_COMMENT))
    private var underlyingFunctionOrMacro: Either<C3FuncDefinition?, C3MacroDefinition?>? = null

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet)
    {
        if (!(pattern.accepts(parameters.position) && pattern.accepts(parameters.originalPosition))) return

        underlyingFunctionOrMacro = getUnderlyingFunctionOrMacro(parameters.position)

        provideCompletionsAfterSymbol(
            parameters,
            result,
            "@",
            listOf(
                "param",
                "return",
                "return?",
                "deprecated",
                "require",
                "ensure",
                "pure"
            )
        )

        provideCompletionsAfterSymbol(
            parameters,
            result,
            "@param ",
            listOf(
                "[in]",
                "[out]",
                "[inout]",
                "[&in]",
                "[&out]",
                "[&inout]"
            )
        )

        provideCompletionsAfterSymbols(
            parameters,
            result,
            listOf(
                "@param ",
                "[in] ",
                "[out] ",
                "[inout] ",
                "[&in] ",
                "[&out] ",
                "[&inout] "
            ),
            getParameters()
        )
    }

    private fun getUnderlyingFunctionOrMacro(comment: PsiElement): Either<C3FuncDefinition?, C3MacroDefinition?>?
    {
        var next = comment.nextSibling

        while (next is PsiWhiteSpace || next is PsiComment)
        {
            next = next.nextSibling
        }

        if (next == null) return null
        if (next.firstChild is C3FuncDefinition) return Either.forLeft(next.firstChild as C3FuncDefinition)
        if (next.firstChild is C3MacroDefinition) return Either.forRight(next.firstChild as C3MacroDefinition)

        return null
    }

    private fun getParameters(): List<String>
    {
        if (underlyingFunctionOrMacro == null) return arrayListOf()
        // create local variable to escape possible null error
        val funcOrMacro = underlyingFunctionOrMacro!!

        return if (funcOrMacro.isLeft)
        {
            funcOrMacro.left!!.funcDef.fnParameterList.parameterList?.paramDeclList?.map { it.parameter.name!! }!!
        } else
        {
            funcOrMacro.right!!.macroParams.parameterList?.paramDeclList?.map { it.parameter.name!! }!!
        }
    }
}