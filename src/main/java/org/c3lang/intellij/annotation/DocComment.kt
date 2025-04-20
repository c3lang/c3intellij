package org.c3lang.intellij.annotation

import ai.grazie.text.range
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import org.c3lang.intellij.psi.C3DefaultModuleSection
import org.c3lang.intellij.psi.C3FuncDefinition
import org.c3lang.intellij.psi.C3MacroDefinition
import org.eclipse.lsp4j.jsonrpc.messages.Either

internal fun annotateDocComment(element: PsiComment, holder: AnnotationHolder)
{
    annotateDocTags(element, holder)
    annotateParamTags(element, holder)
//    annotateReturnTags(element, holder)
//    annotateDeprecatedTags(element, holder)
    annotateStrings(element, holder)
}

fun annotateStrings(element: PsiComment, holder: AnnotationHolder)
{
    val regex = Regex("(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)")
    val commentText = element.text
    val commentStart = element.textRange.startOffset

    regex.findAll(commentText).forEach { match ->
        val descriptionRange = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(descriptionRange)
            .textAttributes(DefaultLanguageHighlighterColors.STRING)
            .create()
    }
}

/* removed for now TODO: add back later
fun annotateDeprecatedTags(element: PsiComment, holder: AnnotationHolder)
{
    val regex = Regex("@deprecated\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)?")
    val commentText  = element.text
    val commentStart = element.textRange.startOffset

    regex.findAll(commentText).forEach { match ->
        val description = match.groups[1]

        if (description != null)
        {
            val descriptionRange = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(descriptionRange)
                .textAttributes(DefaultLanguageHighlighterColors.STRING)
                .create()
        }
    }
}

fun annotateReturnTags(element: PsiComment, holder: AnnotationHolder)
{
    val regex        = Regex("@return\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)?")
    val commentText  = element.text
    val commentStart = element.textRange.startOffset

    regex.findAll(commentText).forEach { match ->
        val description = match.groups[1]

        if (description != null)
        {
            val descriptionRange = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(descriptionRange)
                .textAttributes(DefaultLanguageHighlighterColors.STRING)
                .create()
        }
    }
}
*/

private fun annotateDocTags(element: PsiComment, holder: AnnotationHolder)
{
    val regex = Regex("@(param|return(!)?|deprecated|require|ensure|pure)")
    val commentText = element.text
    val commentStart = element.textRange.startOffset

    regex.findAll(commentText).forEach { match ->
        val range = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range)
            .textAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
            .create()
    }
}

private fun getUnderlyingFunction(comment: PsiComment): Either<C3FuncDefinition?, C3MacroDefinition?>?
{
    var next = comment.nextSibling

    while (next is PsiWhiteSpace || next is PsiComment)
    {
        next = next.nextSibling
    }

    if (next is C3DefaultModuleSection)
    {
        next = next.firstChild
    }

    if (next == null) return null

    if (next.firstChild is C3FuncDefinition) return Either.forLeft(next.firstChild as C3FuncDefinition)
    if (next.firstChild is C3MacroDefinition) return Either.forRight(next.firstChild as C3MacroDefinition)
    return null
}

private fun annotateParamTags(element: PsiComment, holder: AnnotationHolder)
{
    val functionOrMacro = getUnderlyingFunction(element)
    val function = functionOrMacro?.left
    val macro = functionOrMacro?.right
    val args = arrayListOf<String>()

    if (function != null)
    {
        function.funcDef.fnParameterList.parameterList?.paramDeclList?.forEach {
            args.add(it.parameter.name!!)
        }
    } else if (macro != null)
    {
        macro.macroParams.parameterList?.paramDeclList?.forEach {
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
        val name = match.groups[4]
        val description = match.groups[6]

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

        if (name != null)
        {
            if (!args.contains(name.value))
            {
                val range = TextRange(commentStart + match.range.first, commentStart + match.range.last + 1)

                holder.newAnnotation(HighlightSeverity.ERROR, "Argument missing in function")
                    .range(range)
                    .create()
            }

            val nameRange = TextRange(commentStart + name.range.first, commentStart + name.range.last + 1)

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(nameRange)
                .textAttributes(DefaultLanguageHighlighterColors.NUMBER)
                .create()
        }
    }
}
