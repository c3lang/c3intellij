package org.c3lang.intellij.annotation

import ai.grazie.text.range
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import org.c3lang.intellij.psi.C3FuncDefinition

internal fun annotateDocComment(element: PsiComment, holder: AnnotationHolder)
{
    annotateDocTags(element, holder)
    annotateParamTags(element, holder)
}

private fun annotateDocTags(element: PsiComment, holder: AnnotationHolder)
{
    val regex = Regex("@(param|return(!)?|deprecated|require|ensure|pure)")
    val commentText = element.text
    val commentStart = element.textRange.startOffset

    regex.findAll(commentText).forEach { match ->
        val range = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range)
            .textAttributes(DOC_COMMENT_TAG)
            .create()
    }
}

private fun getUnderlyingFunction(comment: PsiComment): C3FuncDefinition?
{
    var next = comment.nextSibling

    while (next is PsiWhiteSpace || next is PsiComment)
    {
        next = next.nextSibling
    }

    if (next.firstChild is C3FuncDefinition) return next.firstChild as C3FuncDefinition
    return null
}

private fun annotateParamTags(element: PsiComment, holder: AnnotationHolder)
{
    val function = getUnderlyingFunction(element)
    val args = arrayListOf<String>()

    if (function != null)
    {
        function.funcDef.fnParameterList.parameterList?.paramDeclList?.forEach {
            args.add(it.parameter.name!!)
        }
    }

    val regex = Regex("@param\\s+((\\[(in|&in|out|&out|inout|&inout)])\\s+)?(\\w+)(\\s+:\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`))?")
    val commentText = element.text
    val commentStart = element.textRange.startOffset

    val nonMatchingLines = commentText.lines().filter { it.trim().startsWith("@param") && !regex.matches(it.trim()) }

    nonMatchingLines.forEach {
        holder.newAnnotation(HighlightSeverity.ERROR, "Invalid syntax")
            .range(TextRange(commentStart + it.range.start, commentStart + it.range.endInclusive + 1))
            .create()
    }

    regex.findAll(commentText).forEach { match ->
        val contract = match.groups[1]
        val name = match.groups[4]!!
        val description = match.groups[6]

        if (!args.contains(name.value))
        {
            val range = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

            holder.newAnnotation(HighlightSeverity.ERROR, "Argument missing in function")
                .range(range)
                .create()

            return@forEach
        }

        if (contract != null)
        {
            val contractRange = TextRange(commentStart + contract.range.first, commentStart + contract.range.last + 1)

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(contractRange)
                .textAttributes(DefaultLanguageHighlighterColors.CONSTANT)
                .create()
        }

        if (description != null)
        {
            val descriptionRange = TextRange(commentStart + description.range.first, commentStart + description.range.last + 1)

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(descriptionRange)
                .textAttributes(DefaultLanguageHighlighterColors.STRING)
                .create()
        }
    }
}
