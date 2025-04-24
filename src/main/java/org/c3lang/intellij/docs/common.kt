package org.c3lang.intellij.docs

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import org.c3lang.intellij.C3Language
import org.c3lang.intellij.C3ParserDefinition
import org.c3lang.intellij.psi.C3DefaultModuleSection

internal fun findDocumentationComment(element: PsiElement): String
{
    // check if parent is default module or not
    var prev = if (element.parent.parent is C3DefaultModuleSection) element.parent.parent.prevSibling else element.parent.prevSibling

    while (prev is PsiWhiteSpace)
    {
        prev = prev.prevSibling
    }

    if (prev == null) return ""

    val builder = StringBuilder()
    while (prev.elementType == C3ParserDefinition.DOC_COMMENT)
    {
        builder.appendLine(prev.text)
        prev = prev.prevSibling
    }

    return builder.toString().replace("<*", "").replace("*>", "")
}

internal fun applyHtmlStyles(text: String, attributes: TextAttributes?): String
{
    if (attributes == null) return HtmlChunk.text(text).toString()

    val color = attributes.foregroundColor?.let { String.format("#%06x", it.rgb and 0xFFFFFF) }
    val style = if (color != null) "color:$color" else ""

    return "<span style=\"$style\">${HtmlChunk.text(text)}</span>"
}

internal fun appendDefinition(fmt: String, project: Project, builderIn: StringBuilder)
{
    builderIn.append(DocumentationMarkup.DEFINITION_START)

    val highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(C3Language.INSTANCE, project, null)
    val tokens = highlighter.highlightingLexer
    tokens.start(fmt)

    val scheme = EditorColorsManager.getInstance().globalScheme
    val builder = StringBuilder()

    while (tokens.tokenType != null)
    {
        val tokenText = fmt.substring(tokens.tokenStart, tokens.tokenEnd)
        val attrKeys = highlighter.getTokenHighlights(tokens.tokenType)
        val attributes = attrKeys.firstNotNullOfOrNull { scheme.getAttributes(it) }
        val styled = applyHtmlStyles(tokenText, attributes)

        builder.append(styled)
        tokens.advance()
    }

    builderIn.append(builder)
    builderIn.append(DocumentationMarkup.DEFINITION_END)
}

internal fun appendFileSection(file: String, builder: StringBuilder)
{
    builder.append(DocumentationMarkup.SECTION_HEADER_START)
    builder.append("File:")
    builder.append(DocumentationMarkup.SECTION_SEPARATOR)
    builder.append(file)
    builder.append(DocumentationMarkup.SECTION_END)
}

internal fun appendParamsSection(docs: String, builder: StringBuilder, args: List<String>)
{
    val params = extractParamsFromDoc(docs, args)
    if (params.isEmpty()) return

    builder.append(DocumentationMarkup.SECTION_HEADER_START)
    builder.append("Params:")
    builder.append(DocumentationMarkup.SECTION_SEPARATOR)
    builder.append(formatParamSection(params))
    builder.append(DocumentationMarkup.SECTION_END)
}

internal fun appendReturnSection(docs: String, builder: StringBuilder)
{
    val returnString = extractReturnFromDoc(docs)
    if (returnString.isEmpty()) return

    builder.append(DocumentationMarkup.SECTION_HEADER_START)
    builder.append("Returns:")
    builder.append(DocumentationMarkup.SECTION_SEPARATOR)
    builder.append(formatReturnSection(returnString))
    builder.append(DocumentationMarkup.SECTION_END)
}

internal fun formatReturnSection(desc: String): String
{
    return desc.replace("\"", "")
}

internal fun formatParamSection(params: Map<String, Pair<String, String>>): String
{
    if (params.isEmpty()) return ""

    val builder = StringBuilder()

    for ((name, pair) in params)
    {
        val description = pair.first
        val contract = pair.second
        val safeDescription = if (description.isNotBlank()) " - ${description.drop(1).dropLast(1)}" else ""
        val safeContract = if (contract.isNotBlank()) """<span style="color:#ffccff;"><i>$contract</i></span>""" else ""
        builder.append("<p><code>$safeContract$name</code>$safeDescription</p>")
    }

    return builder.toString()
}

internal fun extractParamsFromDoc(docComment: String, args: List<String>): Map<String, Pair<String, String>>
{
    val paramRegex = Regex("@param\\s+((\\[(in|&in|out|&out|inout|&inout)])\\s+)?(\\w+)(\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`))?")
    val result = LinkedHashMap<String, Pair<String, String>>()

    for (match in paramRegex.findAll(docComment))
    {
        val contract = match.groupValues[1]
        val name = match.groupValues[4]
        val description = match.groupValues[6]

        if (args.contains(name))
        {
            result[name] = Pair(description, contract)
        }
    }

    val reversed = LinkedHashMap<String, Pair<String, String>>()
    for ((key, value) in result.entries.reversed())
    {
        reversed[key] = value
    }

    return reversed
}

internal fun extractReturnFromDoc(docComment: String): String
{
    val paramRegex = Regex("@return\\s+(\"[\\w\\s]+\")?")

    for (match in paramRegex.findAll(docComment))
    {
        val desc = match.groupValues.getOrNull(1)?.trim() ?: ""

        return desc
    }

    return ""
}

internal fun extractDescriptionTextFromDoc(docComment: String): String
{
    val description = arrayListOf<String>()

    docComment.split("\n").map { it.trim() }.forEach {
        if (it.isEmpty()) return@forEach
        if (!it.startsWith('@'))
        {
            description.add(it)
        }
    }

    val descriptionBuilder = StringBuilder()

    description.reversed().forEach {
        descriptionBuilder.appendLine(it)
    }

    return descriptionBuilder.toString()
}