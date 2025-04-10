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

internal fun findDocumentationComment(element: PsiElement): String
{
    var prev = element.parent.prevSibling

    while (prev is PsiWhiteSpace)
    {
        prev = prev.prevSibling
    }

    println(prev)

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