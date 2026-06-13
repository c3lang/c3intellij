package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3ParserDefinition;
import org.c3lang.intellij.psi.C3DefaultModuleSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DocumentationUtils
{
    private static final Pattern PARAM_PATTERN = Pattern.compile(
        "@param\\s+((\\[(in|&in|out|&out|inout|&inout)])\\s+)?(\\w+)(\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`))?"
    );
    private static final Pattern RETURN_PATTERN = Pattern.compile("@return\\s+(\"[\\w\\s]+\")?");

    private DocumentationUtils()
    {
    }

    static @NotNull String findDocumentationComment(@NotNull PsiElement element)
    {
        PsiElement prev = element.getParent() != null
            && element.getParent().getParent() instanceof C3DefaultModuleSection
            ? element.getParent().getParent().getPrevSibling()
            : element.getParent() != null ? element.getParent().getPrevSibling() : null;

        while (prev instanceof PsiWhiteSpace)
        {
            prev = prev.getPrevSibling();
        }

        if (prev == null) return "";

        StringBuilder builder = new StringBuilder();
        while (prev != null && prev.getNode().getElementType() == C3ParserDefinition.DOC_COMMENT)
        {
            builder.append(prev.getText()).append('\n');
            prev = prev.getPrevSibling();
        }

        return builder.toString().replace("<*", "").replace("*>", "");
    }

    static @NotNull String applyHtmlStyles(@NotNull String text, TextAttributes attributes)
    {
        if (attributes == null) return HtmlChunk.text(text).toString();

        String color = attributes.getForegroundColor() != null
            ? String.format("#%06x", attributes.getForegroundColor().getRGB() & 0xFFFFFF)
            : null;
        String style = color != null ? "color:" + color : "";

        return "<span style=\"" + style + "\">" + HtmlChunk.text(text) + "</span>";
    }

    static void appendDefinition(@NotNull String fmt, @NotNull Project project, @NotNull StringBuilder builderIn)
    {
        builderIn.append(DocumentationMarkup.DEFINITION_START);

        var highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(C3Language.INSTANCE, project, null);
        var tokens = highlighter.getHighlightingLexer();
        tokens.start(fmt);

        var scheme = EditorColorsManager.getInstance().getGlobalScheme();
        StringBuilder builder = new StringBuilder();

        while (tokens.getTokenType() != null)
        {
            String tokenText = fmt.substring(tokens.getTokenStart(), tokens.getTokenEnd());
            var attrKeys = highlighter.getTokenHighlights(tokens.getTokenType());
            TextAttributes attributes = null;
            for (var attrKey : attrKeys)
            {
                attributes = scheme.getAttributes(attrKey);
                if (attributes != null) break;
            }
            builder.append(applyHtmlStyles(tokenText, attributes));
            tokens.advance();
        }

        builderIn.append(builder);
        builderIn.append(DocumentationMarkup.DEFINITION_END);
    }

    static void appendFileSection(@NotNull String file, @NotNull StringBuilder builder)
    {
        builder.append(DocumentationMarkup.SECTION_HEADER_START);
        builder.append("File:");
        builder.append(DocumentationMarkup.SECTION_SEPARATOR);
        builder.append(file);
        builder.append(DocumentationMarkup.SECTION_END);
    }

    static void appendParamsSection(@NotNull String docs, @NotNull StringBuilder builder, @NotNull List<String> args)
    {
        Map<String, ParamDoc> params = extractParamsFromDoc(docs, args);
        if (params.isEmpty()) return;

        builder.append(DocumentationMarkup.SECTION_HEADER_START);
        builder.append("Params:");
        builder.append(DocumentationMarkup.SECTION_SEPARATOR);
        builder.append(formatParamSection(params));
        builder.append(DocumentationMarkup.SECTION_END);
    }

    static void appendReturnSection(@NotNull String docs, @NotNull StringBuilder builder)
    {
        String returnString = extractReturnFromDoc(docs);
        if (returnString.isEmpty()) return;

        builder.append(DocumentationMarkup.SECTION_HEADER_START);
        builder.append("Returns:");
        builder.append(DocumentationMarkup.SECTION_SEPARATOR);
        builder.append(formatReturnSection(returnString));
        builder.append(DocumentationMarkup.SECTION_END);
    }

    static @NotNull String formatReturnSection(@NotNull String desc)
    {
        return desc.replace("\"", "");
    }

    static @NotNull String formatParamSection(@NotNull Map<String, ParamDoc> params)
    {
        if (params.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ParamDoc> entry : params.entrySet())
        {
            String name = entry.getKey();
            String description = entry.getValue().description();
            String contract = entry.getValue().contract();
            String safeDescription = !description.isBlank()
                ? " - " + dropFirstAndLast(description)
                : "";
            String safeContract = !contract.isBlank()
                ? "<span style=\"color:#ffccff;\"><i>" + contract + "</i></span>"
                : "";
            builder.append("<p><code>")
                .append(safeContract)
                .append(name)
                .append("</code>")
                .append(safeDescription)
                .append("</p>");
        }

        return builder.toString();
    }

    static @NotNull Map<String, ParamDoc> extractParamsFromDoc(@NotNull String docComment, @NotNull List<String> args)
    {
        LinkedHashMap<String, ParamDoc> result = new LinkedHashMap<>();
        Matcher matcher = PARAM_PATTERN.matcher(docComment);
        while (matcher.find())
        {
            String contract = valueOrEmpty(matcher.group(1));
            String name = valueOrEmpty(matcher.group(4));
            String description = valueOrEmpty(matcher.group(6));

            if (args.contains(name))
            {
                result.put(name, new ParamDoc(description, contract));
            }
        }

        LinkedHashMap<String, ParamDoc> reversed = new LinkedHashMap<>();
        List<Map.Entry<String, ParamDoc>> entries = new ArrayList<>(result.entrySet());
        for (int i = entries.size() - 1; i >= 0; i--)
        {
            Map.Entry<String, ParamDoc> entry = entries.get(i);
            reversed.put(entry.getKey(), entry.getValue());
        }
        return reversed;
    }

    static @NotNull String extractReturnFromDoc(@NotNull String docComment)
    {
        Matcher matcher = RETURN_PATTERN.matcher(docComment);
        if (matcher.find())
        {
            String desc = matcher.group(1);
            return desc != null ? desc.trim() : "";
        }
        return "";
    }

    static @NotNull String extractDescriptionTextFromDoc(@NotNull String docComment)
    {
        List<String> description = new ArrayList<>();
        for (String line : docComment.split("\n"))
        {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            if (!trimmed.startsWith("@"))
            {
                description.add(trimmed);
            }
        }

        StringBuilder descriptionBuilder = new StringBuilder();
        for (int i = description.size() - 1; i >= 0; i--)
        {
            descriptionBuilder.append(description.get(i)).append('\n');
        }
        return descriptionBuilder.toString();
    }

    private static @NotNull String valueOrEmpty(String value)
    {
        return value != null ? value : "";
    }

    private static @NotNull String dropFirstAndLast(@NotNull String value)
    {
        if (value.length() <= 1) return "";
        return value.substring(1, value.length() - 1);
    }

    record ParamDoc(@NotNull String description, @NotNull String contract)
    {
    }
}
