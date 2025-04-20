package org.c3lang.intellij.docs

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.project.Project
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import org.c3lang.intellij.psi.C3MacroDefinition

internal fun generateMacroDefinitionDoc(element: C3MacroDefinition): String
{
    val name = element.fqName.name
    val type = element.returnType?.fullName!!
    val docs = findDocumentationComment(element)
    val file = SymbolPresentationUtil.getFilePathPresentation(element.containingFile)
    val argsString = "(${element.macroParams.text.replace(Regex("\\s+"), " ").trim()})"
    val args = element.macroParams.parameterList?.paramDeclList?.map { it.parameter.name!! }!!

    return renderFullDoc(file, name, type, argsString, args, docs, element.project)
}

private fun renderFullDoc(file: String, name: String, type: String, argsString: String, args: List<String>, docs: String, project: Project): String
{
    val builder = StringBuilder()
    appendDefinition("macro $type $name$argsString", project, builder)
    builder.append(DocumentationMarkup.SECTIONS_START)
    builder.appendLine(extractDescriptionTextFromDoc(docs))
    appendParamsSection(docs, builder, args)
    appendReturnSection(docs, builder)
    appendFileSection(file, builder)
    builder.append(DocumentationMarkup.SECTIONS_END)

    return builder.toString()
}