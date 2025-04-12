package org.c3lang.intellij.docs

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.project.Project
import com.intellij.psi.presentation.java.SymbolPresentationUtil
import org.c3lang.intellij.psi.C3LocalDeclAfterType
import org.c3lang.intellij.psi.C3LocalDeclarationStmt

internal fun generateVarDeclDoc(element: C3LocalDeclAfterType): String
{
    val name = element.name ?: "Error getting name"
    val type = (element.parent.parent as C3LocalDeclarationStmt).optionalType.type.text
    val file = SymbolPresentationUtil.getFilePathPresentation(element.containingFile)

    return renderFullDoc(file, name, type, element.project)
}

private fun renderFullDoc(file: String, name: String, type: String, project: Project): String
{
    val builder = StringBuilder()
    appendDefinition("$type $name", project, builder)
    builder.append(DocumentationMarkup.SECTIONS_START)
    appendFileSection(file, builder)
    builder.append(DocumentationMarkup.SECTIONS_END)

    return builder.toString()
}