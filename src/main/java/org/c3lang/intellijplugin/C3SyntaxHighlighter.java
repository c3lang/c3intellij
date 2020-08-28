package org.c3lang.intellijplugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellijplugin.lexer.C3LexerAdapter;
import org.c3lang.intellijplugin.parser.psi.C3Type;
import org.c3lang.intellijplugin.parser.psi.C3Types;
import org.graalvm.compiler.lir.LIRInstruction;
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
    public static final TextAttributesKey BRACES =
            createTextAttributesKey("C3_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("C3_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{ BAD_CHARACTER };
    private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{ BRACES };
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static Map<IElementType, TextAttributesKey[]> s_mapping = new HashMap<>();

    private static void addMapping(String name, TextAttributesKey fallbackAttributeKey, IElementType... mapped)
    {
        TextAttributesKey key = createTextAttributesKey(name, fallbackAttributeKey);
        TextAttributesKey[] keys = new TextAttributesKey[] { key };
        for (IElementType type : mapped)
        {
            s_mapping.put(type, keys);
        }
    }

    static
    {
        addMapping("C3_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER, C3Types.IDENT);
        addMapping("C3_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT, C3Types.CONSTANT);
        addMapping("C3_NUMBER", DefaultLanguageHighlighterColors.NUMBER, C3Types.INT_LITERAL);
        addMapping("C3_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD,
                   C3Types.VOID_KW,
                   C3Types.BOOL_KW,
                   C3Types.CHAR_KW,
                   C3Types.BYTE_KW,
                   C3Types.SHORT_KW,
                   C3Types.USHORT_KW,
                   C3Types.INT_KW,
                   C3Types.UINT_KW,
                   C3Types.LONG_KW,
                   C3Types.ULONG_KW,
                   C3Types.DOUBLE_KW,
                   C3Types.HALF_KW,
                   C3Types.FLOAT_KW,
                   C3Types.QUAD_KW,
                   C3Types.TRY_KW,
                   C3Types.ELSE_KW,
                   C3Types.MODULE_KW,
                   C3Types.IMPORT_KW,
                   C3Types.CONST_KW,
                   C3Types.FUNC_KW,
                   C3Types.MACRO_KW,
                   C3Types.CASE_KW,
                   C3Types.DEFAULT_KW,
                   C3Types.SWITCH_KW,
                   C3Types.FOR_KW,
                   C3Types.ELSE_KW,
                   C3Types.CTELSE_KW,
                   C3Types.CTELIF_KW,
                   C3Types.NEXT_KW,
                   C3Types.BREAK_KW,
                   C3Types.CONTINUE_KW,
                   C3Types.IF_KW,
                   C3Types.CTIF_KW);
        addMapping("C3_BRACES", DefaultLanguageHighlighterColors.BRACES, C3Types.LBR, C3Types.RBR);
        addMapping("C3_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS, C3Types.LB, C3Types.RB);
        addMapping("C3_PARENS", DefaultLanguageHighlighterColors.PARENTHESES, C3Types.LP, C3Types.RP);
        addMapping("C3_DOT", DefaultLanguageHighlighterColors.DOT, C3Types.DOT);
        addMapping("C3_EOS", DefaultLanguageHighlighterColors.SEMICOLON, C3Types.EOS);
        addMapping("C3_COMMA", DefaultLanguageHighlighterColors.COMMA, C3Types.COMMA);
        addMapping("C3_STRING", DefaultLanguageHighlighterColors.STRING, C3Types.STRING_LITERAL);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer()
    {
        return new C3LexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
    {
        TextAttributesKey[] mapping = s_mapping.get(tokenType);
        if (mapping != null) return mapping;

        if (tokenType.equals(C3Types.LBR) || tokenType.equals(C3Types.RBR))
        {
            return BRACES_KEYS;
        }
        else if (tokenType.equals(TokenType.BAD_CHARACTER))
        {
            return BAD_CHAR_KEYS;
        }
        else
        {
            return EMPTY_KEYS;
        }
    }

}
