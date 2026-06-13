package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.jetbrains.annotations.NotNull;

public final class VarDeclDocs
{
    private VarDeclDocs()
    {
    }

    public static @NotNull String generateVarDeclDoc(@NotNull C3LocalDeclAfterType element)
    {
        String name = element.getName() != null ? element.getName() : "Error getting name";
        C3LocalDeclarationStmt declaration = (C3LocalDeclarationStmt) element.getParent().getParent();
        String type = declaration.getOptionalType().getType().getText();
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());

        return renderFullDoc(file, name, type, element.getProject());
    }

    private static @NotNull String renderFullDoc(
            @NotNull String file,
            @NotNull String name,
            @NotNull String type,
            @NotNull Project project)
    {
        StringBuilder builder = new StringBuilder();
        DocumentationUtils.appendDefinition(type + " " + name, project, builder);
        builder.append(DocumentationMarkup.SECTIONS_START);
        DocumentationUtils.appendFileSection(file, builder);
        builder.append(DocumentationMarkup.SECTIONS_END);
        return builder.toString();
    }
}
