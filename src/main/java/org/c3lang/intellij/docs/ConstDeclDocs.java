package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.jetbrains.annotations.NotNull;

public final class ConstDeclDocs
{
    private ConstDeclDocs()
    {
    }

    public static @NotNull String generateConstDeclDoc(@NotNull C3ConstDeclarationStmt element)
    {
        String name = element.getName() != null ? element.getName() : "Error getting name";
        String type = element.getType() != null ? element.getType().getText() : "Error getting type";
        String value = element.getExpr() != null ? element.getExpr().getText() : "Error getting value";
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());

        return renderFullDoc(file, name, value, type, element.getProject());
    }

    private static @NotNull String renderFullDoc(
            @NotNull String file,
            @NotNull String name,
            @NotNull String value,
            @NotNull String type,
            @NotNull Project project)
    {
        StringBuilder builder = new StringBuilder();
        DocumentationUtils.appendDefinition("const " + type + " " + name + " = " + value, project, builder);
        builder.append(DocumentationMarkup.SECTIONS_START);
        DocumentationUtils.appendFileSection(file, builder);
        builder.append(DocumentationMarkup.SECTIONS_END);
        return builder.toString();
    }
}
