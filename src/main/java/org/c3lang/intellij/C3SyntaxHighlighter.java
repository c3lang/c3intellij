package org.c3lang.intellij;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.lexer.C3LexerAdapter;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3SyntaxHighlighter extends SyntaxHighlighterBase
{
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("C3_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{ BAD_CHARACTER };
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static Map<IElementType, TextAttributesKey[]> s_mapping = new HashMap<>();

    public final static TextAttributesKey ESCAPE_SEQ_KEY = createTextAttributesKey("C3_ESCSEQ", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public final static TextAttributesKey INVALID_ESCAPE_SEQ_KEY = createTextAttributesKey("C3_INVALID_ESCSEQ", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
    public final static TextAttributesKey STRING_KEY = createTextAttributesKey("C3_STRING", DefaultLanguageHighlighterColors.STRING);
    public final static TextAttributesKey BYTES_KEY = createTextAttributesKey("C3_BYTES", DefaultLanguageHighlighterColors.NUMBER);
    public final static TextAttributesKey TYPE_KEY = createTextAttributesKey("C3_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);
    public final static TextAttributesKey CONSTANT_KEY = createTextAttributesKey("C3_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
    public final static TextAttributesKey NUMBER_KEY = createTextAttributesKey("C3_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public final static TextAttributesKey DOT_KEY = createTextAttributesKey("C3_DOT", DefaultLanguageHighlighterColors.DOT);
    public final static TextAttributesKey KEYWORD_KEY = createTextAttributesKey("C3_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public final static TextAttributesKey CT_KEYWORD_KEY = createTextAttributesKey("C3_CT_KEYWORD", DefaultLanguageHighlighterColors.METADATA);
    public final static TextAttributesKey FUNCTION_KEY = createTextAttributesKey("C3_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public final static TextAttributesKey MACRO_KEY = createTextAttributesKey("C3_MACRO", FUNCTION_KEY);
    public final static TextAttributesKey AT_MACRO_KEY = createTextAttributesKey("C3_AT_MACRO", MACRO_KEY);
    public final static TextAttributesKey METHOD_KEY = createTextAttributesKey("C3_METHOD", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public final static TextAttributesKey MACRO_METHOD_KEY = createTextAttributesKey("C3_MACRO_METHOD", METHOD_KEY);
    public final static TextAttributesKey AT_MACRO_METHOD_KEY = createTextAttributesKey("C3_AT_MACRO_METHOD", MACRO_METHOD_KEY);
    public final static TextAttributesKey TYPE_DEFINITION_KEY = createTextAttributesKey("C3_TYPE_DEFINITION", TYPE_KEY);
    public final static TextAttributesKey STRUCT_NAME_KEY = createTextAttributesKey("C3_STRUCT", TYPE_DEFINITION_KEY);
    public final static TextAttributesKey ENUM_NAME_KEY = createTextAttributesKey("C3_ENUM", TYPE_DEFINITION_KEY);
    public final static TextAttributesKey FAULT_NAME_KEY = createTextAttributesKey("C3_FAULT", ENUM_NAME_KEY);
    public final static TextAttributesKey UNION_NAME_KEY = createTextAttributesKey("C3_UNION", STRUCT_NAME_KEY);
    public final static TextAttributesKey BITSTRUCT_NAME_KEY = createTextAttributesKey("C3_BITSTRUCT", STRUCT_NAME_KEY);
    public final static TextAttributesKey ATTRIBUTE_KEY = createTextAttributesKey("C3_ATTRIBUTE", DefaultLanguageHighlighterColors.METADATA);
    public final static TextAttributesKey BRACES_KEY = createTextAttributesKey("C3_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public final static TextAttributesKey BRACKETS_KEY = createTextAttributesKey("C3_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public final static TextAttributesKey PARENTHESES_KEY = createTextAttributesKey("C3_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public final static TextAttributesKey IDENTIFIER_KEY = createTextAttributesKey("C3_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public final static TextAttributesKey COMMA_KEY = createTextAttributesKey("C3_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public final static TextAttributesKey TYPEDEF_NAME_KEY = createTextAttributesKey("C3_TYPEDEF", TYPE_DEFINITION_KEY);
    public final static TextAttributesKey ALIAS_TYPE_NAME_KEY = createTextAttributesKey("C3_ALIAS_TYPE", TYPEDEF_NAME_KEY);
    public final static TextAttributesKey ATTRDEF_ATTRIBUTE_KEY = createTextAttributesKey("C3_ATTRDEF_ATTRIBUTE", ATTRIBUTE_KEY);
    public final static TextAttributesKey ALIAS_NAME_KEY = createTextAttributesKey("C3_ALIAS_IDENTIFIER", TYPE_KEY);

    public final static TextAttributesKey CONST_IDENT_FAULT_KEY = createTextAttributesKey("C3_CONST_IDENT_FAULT", DefaultLanguageHighlighterColors.CONSTANT);
    public final static TextAttributesKey CONST_IDENT_FAULT_QUESTION_KEY = createTextAttributesKey("C3_CONST_IDENT_FAULT_QUESTION", DefaultLanguageHighlighterColors.KEYWORD);

    public final static TextAttributesKey LABEL_KEY = createTextAttributesKey("C3_LABEL", DefaultLanguageHighlighterColors.LABEL);
    public final static TextAttributesKey EOS_KEY = createTextAttributesKey("C3_EOS", DefaultLanguageHighlighterColors.SEMICOLON);
    public final static TextAttributesKey LINE_COMMENT_KEY = createTextAttributesKey("C3_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public final static TextAttributesKey BLOCK_COMMENT_KEY = createTextAttributesKey("C3_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public final static TextAttributesKey DOC_COMMENT_KEY = createTextAttributesKey("C3_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);

    private static void addMapping(String name, TextAttributesKey fallbackAttributeKey, IElementType... mapped)
    {
        TextAttributesKey key = createTextAttributesKey(name, fallbackAttributeKey);
        TextAttributesKey[] keys = new TextAttributesKey[] { key };
        for (IElementType type : mapped)
        {
            s_mapping.put(type, keys);
        }
    }

    private static void addMapping(TextAttributesKey key, IElementType... mapped)
     {
         TextAttributesKey[] keys = new TextAttributesKey[] { key };
         for (IElementType type : mapped)
         {
             s_mapping.put(type, keys);
         }
     }

    private static void addMapping(String name, TextAttributesKey fallbackAttributeKey, TokenSet set)
    {
        TextAttributesKey key = createTextAttributesKey(name, fallbackAttributeKey);
        TextAttributesKey[] keys = new TextAttributesKey[] { key };
        for (IElementType type : set.getTypes())
        {
            s_mapping.put(type, keys);
        }
    }

    private static void addMapping(TextAttributesKey key, TokenSet set)
    {
        TextAttributesKey[] keys = new TextAttributesKey[] { key };
        for (IElementType type : set.getTypes())
        {
            s_mapping.put(type, keys);
        }
    }

    static
    {
        addMapping(BYTES_KEY, C3TokenSets.BYTES);
        addMapping(STRING_KEY, C3TokenSets.STRINGS);
        addMapping(CONSTANT_KEY, C3TokenSets.CONSTANTS);
        addMapping(NUMBER_KEY, C3TokenSets.NUMBER);
        addMapping(DOT_KEY, C3TokenSets.DOTS);
        addMapping(KEYWORD_KEY, C3TokenSets.KEYWORDS);
        addMapping(CT_KEYWORD_KEY, C3TokenSets.CT_KEYWORDS);
        addMapping(BRACES_KEY, C3TokenSets.BRACES);
        addMapping(BRACKETS_KEY, C3TokenSets.BRACKETS);
        addMapping(PARENTHESES_KEY, C3TokenSets.PARENTHESES);
        addMapping(IDENTIFIER_KEY, C3TokenSets.IDENTIFIER);
        addMapping(COMMA_KEY, C3Types.COMMA);
        addMapping(EOS_KEY, C3Types.EOS);
        addMapping(TYPE_KEY, C3TokenSets.TYPES);
        addMapping(LINE_COMMENT_KEY, C3ParserDefinition.LINE_COMMENT);
        addMapping(BLOCK_COMMENT_KEY, C3ParserDefinition.BLOCK_COMMENT);
        addMapping(DOC_COMMENT_KEY, C3ParserDefinition.DOC_COMMENT);
        addMapping(CONST_IDENT_FAULT_KEY, C3SyntaxHighlighterLexer.CONST_IDENT_FAULT);
        addMapping(CONST_IDENT_FAULT_QUESTION_KEY, C3SyntaxHighlighterLexer.CONST_IDENT_FAULT_QUESTION);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer()
    {
        return new C3SyntaxHighlighterLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType)
    {
        TextAttributesKey[] mapping = s_mapping.get(tokenType);
        if (mapping != null) return mapping;

        if (tokenType.equals(TokenType.BAD_CHARACTER))
        {
            return BAD_CHAR_KEYS;
        }
        else
        {
            return EMPTY_KEYS;
        }
    }

}
