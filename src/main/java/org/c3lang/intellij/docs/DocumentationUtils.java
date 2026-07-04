package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3ParserDefinition;
import org.c3lang.intellij.C3StringLiteralUtil;
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
        "@param\\s+((\\[(in|&in|out|&out|inout|&inout|own|&own|init|&init|drop|&drop)])\\s+)?(\\w+)(?:\\s*:\\s*(.*))?"
    );

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
        if (!returnString.isEmpty())
        {
            builder.append(DocumentationMarkup.SECTION_HEADER_START);
            builder.append("Returns:");
            builder.append(DocumentationMarkup.SECTION_SEPARATOR);
            builder.append(formatReturnSection(returnString));
            builder.append(DocumentationMarkup.SECTION_END);
        }

        List<OptionalReturnDoc> optionalReturns = extractOptionalReturnsFromDoc(docs);
        if (!optionalReturns.isEmpty())
        {
            builder.append(DocumentationMarkup.SECTION_HEADER_START);
            builder.append("Excuses:");
            builder.append(DocumentationMarkup.SECTION_SEPARATOR);
            builder.append(formatOptionalReturnSection(optionalReturns));
            builder.append(DocumentationMarkup.SECTION_END);
        }
    }

    static void appendContractSections(@NotNull String docs, @NotNull StringBuilder builder)
    {
        appendContractSection("Requires:", extractRequiresFromDoc(docs), builder);
        appendContractSection("Ensures:", extractEnsuresFromDoc(docs), builder);
    }

    private static void appendContractSection(@NotNull String title, @NotNull List<ContractDoc> contracts,
                                              @NotNull StringBuilder builder)
    {
        if (contracts.isEmpty()) return;

        builder.append(DocumentationMarkup.SECTION_HEADER_START);
        builder.append(title);
        builder.append(DocumentationMarkup.SECTION_SEPARATOR);
        builder.append(formatContractSection(contracts));
        builder.append(DocumentationMarkup.SECTION_END);
    }

    static @NotNull String formatReturnSection(@NotNull String desc)
    {
        return HtmlChunk.text(desc).toString();
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
            HtmlBuilder row = new HtmlBuilder();
            HtmlChunk contractChunk = contract.isBlank()
                ? HtmlChunk.empty()
                : DocumentationMarkup.GRAYED_ELEMENT.child(HtmlChunk.text(contract).italic());
            row.append(HtmlChunk.tag("code").children(HtmlChunk.fragment(contractChunk, HtmlChunk.text(name))));
            if (!description.isBlank()) row.append(HtmlChunk.text(" - " + description));
            builder.append(HtmlChunk.p().child(row.toFragment()));
        }
        return builder.toString();
    }

    static @NotNull String formatOptionalReturnSection(@NotNull List<OptionalReturnDoc> optionalReturns)
    {
        StringBuilder builder = new StringBuilder();
        for (OptionalReturnDoc optionalReturn : optionalReturns)
        {
            HtmlBuilder row = new HtmlBuilder();
            row.append(HtmlChunk.tag("code").child(HtmlChunk.text(String.join(", ", optionalReturn.names()))));
            if (!optionalReturn.description().isBlank())
            {
                row.append(HtmlChunk.text(" - " + optionalReturn.description()));
            }
            builder.append(HtmlChunk.p().child(row.toFragment()));
        }
        return builder.toString();
    }

    static @NotNull String formatContractSection(@NotNull List<ContractDoc> contracts)
    {
        StringBuilder builder = new StringBuilder();
        for (ContractDoc contract : contracts)
        {
            HtmlBuilder row = new HtmlBuilder();
            row.append(HtmlChunk.tag("code").child(HtmlChunk.text(contract.expression())));
            if (!contract.description().isBlank())
            {
                row.append(HtmlChunk.text(" - " + contract.description()));
            }
            builder.append(HtmlChunk.p().child(row.toFragment()));
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
            String description = C3StringLiteralUtil.unescapeStringLiteralSequence(valueOrEmpty(matcher.group(5)));

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
        for (String line : docComment.split("\n"))
        {
            String trimmed = line.trim();
            if (trimmed.equals("@return")) return "";
            if (trimmed.startsWith("@return") && Character.isWhitespace(trimmed.charAt("@return".length())))
            {
                return C3StringLiteralUtil.unescapeStringLiteralSequence(trimmed.substring("@return".length()).trim());
            }
        }
        return "";
    }

    static @NotNull List<OptionalReturnDoc> extractOptionalReturnsFromDoc(@NotNull String docComment)
    {
        List<OptionalReturnDoc> result = new ArrayList<>();
        for (String line : docComment.split("\n"))
        {
            String trimmed = line.trim();
            if (trimmed.equals("@return?")) continue;
            if (trimmed.startsWith("@return?") && Character.isWhitespace(trimmed.charAt("@return?".length())))
            {
                OptionalReturnDoc optionalReturn = parseOptionalReturn(trimmed.substring("@return?".length()).trim());
                if (!optionalReturn.names().isEmpty())
                {
                    result.add(optionalReturn);
                }
            }
        }
        return result;
    }

    static @NotNull List<ContractDoc> extractRequiresFromDoc(@NotNull String docComment)
    {
        return extractContractsFromDoc(docComment, "@require");
    }

    static @NotNull List<ContractDoc> extractEnsuresFromDoc(@NotNull String docComment)
    {
        return extractContractsFromDoc(docComment, "@ensure");
    }

    private static @NotNull List<ContractDoc> extractContractsFromDoc(@NotNull String docComment, @NotNull String tag)
    {
        List<ContractDoc> result = new ArrayList<>();
        for (String line : docComment.split("\n"))
        {
            String trimmed = line.trim();
            if (trimmed.equals(tag)) continue;
            if (trimmed.startsWith(tag) && Character.isWhitespace(trimmed.charAt(tag.length())))
            {
                ContractDoc contract = parseContract(trimmed.substring(tag.length()).trim());
                if (contract != null && !contract.expression().isEmpty())
                {
                    result.add(contract);
                }
            }
        }
        return result;
    }

    private static ContractDoc parseContract(@NotNull String text)
    {
        int descriptionStart = findContractDescriptionSeparator(text);
        if (descriptionStart >= 0)
        {
            if (!startsWithStringLiteral(text.substring(descriptionStart + 1).trim()))
            {
                return null;
            }
            String expression = text.substring(0, descriptionStart).trim();
            String description = C3StringLiteralUtil.unescapeStringLiteralSequence(text.substring(descriptionStart + 1).trim());
            return new ContractDoc(expression, description);
        }

        return new ContractDoc(text.trim(), "");
    }

    private static @NotNull OptionalReturnDoc parseOptionalReturn(@NotNull String text)
    {
        int descriptionStart = findDescriptionSeparator(text);
        String namesText = descriptionStart >= 0 ? text.substring(0, descriptionStart).trim() : text.trim();
        String descriptionText = descriptionStart >= 0 ? text.substring(descriptionStart + 1).trim() : "";

        List<String> names = new ArrayList<>();
        for (String name : namesText.split(","))
        {
            String trimmed = name.trim();
            if (!trimmed.isEmpty())
            {
                names.add(trimmed);
            }
        }

        return new OptionalReturnDoc(names, C3StringLiteralUtil.unescapeStringLiteralSequence(descriptionText));
    }

    private static int findContractDescriptionSeparator(@NotNull String text)
    {
        int parenDepth = 0;
        int bracketDepth = 0;
        int braceDepth = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '"')
            {
                i = skipQuotedString(text, i);
                continue;
            }
            if (c == '`')
            {
                i = skipRawString(text, i);
                continue;
            }

            switch (c)
            {
                case '(' -> parenDepth++;
                case ')' -> parenDepth = Math.max(0, parenDepth - 1);
                case '[' -> bracketDepth++;
                case ']' -> bracketDepth = Math.max(0, bracketDepth - 1);
                case '{' -> braceDepth++;
                case '}' -> braceDepth = Math.max(0, braceDepth - 1);
                case ':' ->
                {
                    boolean previousIsColon = i > 0 && text.charAt(i - 1) == ':';
                    boolean nextIsColon = i + 1 < text.length() && text.charAt(i + 1) == ':';
                    if (parenDepth == 0 && bracketDepth == 0 && braceDepth == 0 && !previousIsColon && !nextIsColon)
                    {
                        return i;
                    }
                }
                default ->
                {
                }
            }
        }
        return -1;
    }

    private static int skipQuotedString(@NotNull String text, int start)
    {
        for (int i = start + 1; i < text.length(); i++)
        {
            if (text.charAt(i) == '\\')
            {
                i++;
                continue;
            }
            if (text.charAt(i) == '"') return i;
        }
        return text.length() - 1;
    }

    private static int skipRawString(@NotNull String text, int start)
    {
        for (int i = start + 1; i < text.length(); i++)
        {
            if (text.charAt(i) != '`') continue;
            if (i + 1 < text.length() && text.charAt(i + 1) == '`')
            {
                i++;
                continue;
            }
            return i;
        }
        return text.length() - 1;
    }

    private static int findDescriptionSeparator(@NotNull String text)
    {
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) != ':') continue;
            boolean previousIsColon = i > 0 && text.charAt(i - 1) == ':';
            boolean nextIsColon = i + 1 < text.length() && text.charAt(i + 1) == ':';
            if (!previousIsColon && !nextIsColon)
            {
                return i;
            }
        }
        return -1;
    }

    private static boolean startsWithStringLiteral(@NotNull String text)
    {
        return !text.isEmpty() && (text.charAt(0) == '"' || text.charAt(0) == '`');
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

    record ParamDoc(@NotNull String description, @NotNull String contract)
    {
    }

    record OptionalReturnDoc(@NotNull List<String> names, @NotNull String description)
    {
    }

    record ContractDoc(@NotNull String expression, @NotNull String description)
    {
    }
}
