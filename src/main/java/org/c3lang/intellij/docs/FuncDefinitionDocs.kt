package org.c3lang.intellij.docs

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.project.Project
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import org.c3lang.intellij.psi.C3FuncDef

internal fun generateFuncDefDoc(element: C3FuncDef): String
{
    val name = element.fqName.name
    val type = element.returnType?.fullName!!
    val docs = findDocumentationComment(element.parent)
    val file = SymbolPresentationUtil.getFilePathPresentation(element.containingFile)

    return renderFullDoc(file, name, type, element.fnParameterList.text.replace(Regex("\\s+"), " ").trim(), docs, element.project)
}

private fun renderFullDoc(file: String, name: String, type: String, args: String, docs: String, project: Project): String
{
    val builder = StringBuilder()
    appendDefinition("fn $type $name$args", project, builder)
    builder.append(DocumentationMarkup.SECTIONS_START)
    appendParamsSection(docs, builder)
    appendReturnSection(docs, builder)
    appendFileSection(file, builder)
    builder.append(DocumentationMarkup.SECTIONS_END)

    return builder.toString()
}

private fun appendParamsSection(docs: String, builder: StringBuilder)
{
    val params = extractParamsFromDoc(docs)
    if (params.isEmpty()) return

    builder.append(DocumentationMarkup.SECTION_HEADER_START)
    builder.append("Params:")
    builder.append(DocumentationMarkup.SECTION_SEPARATOR)
    builder.append(formatParamSection(params))
    builder.append(DocumentationMarkup.SECTION_END)
}

private fun appendReturnSection(docs: String, builder: StringBuilder)
{
    val returnString = extractReturnFromDoc(docs)
    if (returnString.isEmpty()) return

    builder.append(DocumentationMarkup.SECTION_HEADER_START)
    builder.append("Returns:")
    builder.append(DocumentationMarkup.SECTION_SEPARATOR)
    builder.append(formatReturnSection(returnString))
    builder.append(DocumentationMarkup.SECTION_END)
}

private fun formatReturnSection(desc: String): String
{
    return desc.replace("\"", "")
}

private fun formatParamSection(params: Map<String, String>): String
{
    if (params.isEmpty()) return ""

    val builder = StringBuilder()

    for ((name, desc) in params)
    {
        val safeDesc = if (desc.isNotBlank()) " - ${desc.replace("\"", "")}" else ""
        builder.append("<p><code>$name</code>$safeDesc</p>")
    }

    return builder.toString()
}

private fun extractParamsFromDoc(docComment: String): Map<String, String>
{
    val paramRegex = Regex("@param\\s+(\\w+)\\s+(\"[\\w\\s]+\")?")
    val result = LinkedHashMap<String, String>()

    for (match in paramRegex.findAll(docComment))
    {
        val name = match.groupValues[1]
        val desc = match.groupValues.getOrNull(2)?.trim() ?: ""

        result[name] = desc
    }

    val reversed = LinkedHashMap<String, String>()
    for ((key, value) in result.entries.reversed())
    {
        reversed[key] = value
    }

    return reversed
}

private fun extractReturnFromDoc(docComment: String): String
{
    val paramRegex = Regex("@return\\s+(\"[\\w\\s]+\")?")

    for (match in paramRegex.findAll(docComment))
    {
        val desc = match.groupValues.getOrNull(1)?.trim() ?: ""

        return desc
    }

    return ""
}