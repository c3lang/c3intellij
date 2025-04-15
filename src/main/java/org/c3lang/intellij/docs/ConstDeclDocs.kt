package org.c3lang.intellij.docs

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.project.Project
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import org.c3lang.intellij.psi.C3ConstDeclarationStmt

internal fun generateConstDeclDoc(element: C3ConstDeclarationStmt): String
{
    val name = element.name ?: "Error getting name"
    val type = element.type?.text ?: "Error getting type"
    val value = element.expr?.text ?: "Error getting value"
    val file = SymbolPresentationUtil.getFilePathPresentation(element.containingFile)

    return renderFullDoc(file, name, value, type, element.project)
}

private fun renderFullDoc(file: String, name: String, value: String, type: String, project: Project): String
{
    val builder = StringBuilder()
    appendDefinition("const $type $name = $value", project, builder)
    builder.append(DocumentationMarkup.SECTIONS_START)
    appendFileSection(file, builder)
    builder.append(DocumentationMarkup.SECTIONS_END)

    return builder.toString()
}