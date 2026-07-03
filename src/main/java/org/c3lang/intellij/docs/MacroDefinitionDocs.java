package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3ParameterList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MacroDefinitionDocs
{
    private MacroDefinitionDocs()
    {
    }

    public static @NotNull String generateMacroDefinitionDoc(@NotNull C3MacroDefinition element)
    {
        String name = element.getFqName().getName();
        String type = element.getReturnType() != null ? element.getReturnType().getFullName() : "";
        String docs = DocumentationUtils.findDocumentationComment(element);
        String file = SymbolPresentationUtil.getFilePathPresentation(element.getContainingFile());
        String argsString = "(" + element.getMacroParams().getText().replaceAll("\\s+", " ").trim() + ")";
		C3ParameterList parameterList = element.getMacroParams().getParameterList();

        List<String> args = new ArrayList<>();
		if (parameterList != null)
		{
			for (C3ParamDecl decl : parameterList.getParamDeclList())
			{
				String s = decl.getParameter().getName();
				if (s != null && !s.isBlank()) args.add(s);
			}
		}
        return renderFullDoc(file, name, type, argsString, args, docs, element.getProject());
    }

    private static @NotNull String renderFullDoc(
            @NotNull String file,
            @NotNull String name,
            @NotNull String type,
            @NotNull String argsString,
            @NotNull List<String> args,
            @NotNull String docs,
            @NotNull Project project)
    {
        StringBuilder builder = new StringBuilder();
        if (type.isEmpty())
        {
            DocumentationUtils.appendDefinition("macro " + name + argsString, project, builder);
        }
        else
        {
            DocumentationUtils.appendDefinition("macro " + type + " " + name + argsString, project, builder);
        }
        builder.append(DocumentationMarkup.SECTIONS_START);
        builder.append(DocumentationUtils.extractDescriptionTextFromDoc(docs)).append('\n');
        DocumentationUtils.appendParamsSection(docs, builder, args);
        DocumentationUtils.appendReturnSection(docs, builder);
        DocumentationUtils.appendFileSection(file, builder);
        builder.append(DocumentationMarkup.SECTIONS_END);
        return builder.toString();
    }
}
