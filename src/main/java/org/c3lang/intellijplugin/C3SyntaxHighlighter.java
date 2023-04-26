package org.c3lang.intellijplugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellijplugin.lexer.C3LexerAdapter;
import org.c3lang.intellijplugin.parser.C3ParserDefinition;
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
        addMapping("C3_LABEL", DefaultLanguageHighlighterColors.LABEL, C3Types.LABEL);
        addMapping("C3_DEFNAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION, C3Types.FUNC_MACRO_NAME);
        addMapping("C3_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER, C3Types.IDENT);
        addMapping("C3_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT, C3Types.CONSTANT);
        addMapping("C3_NUMBER", DefaultLanguageHighlighterColors.NUMBER, C3Types.INT_LITERAL, C3Types.FLOAT_LITERAL);
        addMapping("C3_TYPE",
                   DefaultLanguageHighlighterColors.CLASS_NAME,
                   C3Types.VOID_KW,
                   C3Types.BOOL_KW,
                   C3Types.CHAR_KW,
                   C3Types.ICHAR_KW,
                   C3Types.SHORT_KW,
                   C3Types.USHORT_KW,
                   C3Types.INT_KW,
                   C3Types.UINT_KW,
                   C3Types.LONG_KW,
                   C3Types.ULONG_KW,
                   C3Types.INT128_KW,
                   C3Types.UINT128_KW,
                   C3Types.DOUBLE_KW,
                   C3Types.BFLOAT16_KW,
                   C3Types.FLOAT16_KW,
                   C3Types.FLOAT_KW,
                   C3Types.FLOAT128_KW,
                   C3Types.ANYFAULT_KW,
                   C3Types.ANY_KW,
                   C3Types.IPTR_KW,
                   C3Types.UPTR_KW,
                   C3Types.USZ_KW,
                   C3Types.ISZ_KW,
                   C3Types.CT_EVALTYPE_KW,
                   C3Types.CT_TYPE_IDENT,
                   C3Types.TYPE_IDENT
                   );
        addMapping("C3_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT, C3Types.BUILTIN_CONST, C3Types.CONST_IDENT);
        addMapping("C3_DOT", DefaultLanguageHighlighterColors.DOT, C3Types.DOT, C3Types.DOTDOT);
        addMapping("C3_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD,
                   C3Types.ASM_KW,
                   C3Types.ASSERT_KW,
                   C3Types.BREAK_KW,
                   C3Types.CASE_KW,
                   C3Types.CONST_KW,
                   C3Types.CONTINUE_KW,
                   C3Types.DEFAULT_KW,
                   C3Types.DEFER_KW,
                   C3Types.DEFINE_KW,
                   C3Types.DO_KW,
                   C3Types.TRY_KW,
                   C3Types.ELSE_KW,
                   C3Types.ENUM_KW,
                   C3Types.EXTERN_KW,
                   C3Types.FALSE_KW,
                   C3Types.FOR_KW,
                   C3Types.FAULT_KW,
                   C3Types.IF_KW,
                   C3Types.IMPORT_KW,
                   C3Types.MACRO_KW,
                   C3Types.MODULE_KW,
                   C3Types.NEXTCASE_KW,
                   C3Types.NULL_KW,
                   C3Types.RETURN_KW,
                   C3Types.STRUCT_KW,
                   C3Types.BITSTRUCT_KW,
                   C3Types.SWITCH_KW,
                   C3Types.TLOCAL_KW,
                   C3Types.TRUE_KW,
                   C3Types.TRY_KW,
                   C3Types.TYPEDEF_KW,
                   C3Types.UNION_KW,
                   C3Types.VAR_KW,
                   C3Types.WHILE_KW,
                   C3Types.CT_ASSERT_KW,
                   C3Types.CT_CASE_KW,
                   C3Types.CT_DEFAULT_KW,
                   C3Types.CT_ELSE_KW,
                   C3Types.CT_ENDIF_KW,
                   C3Types.CT_ENDFOR_KW,
                   C3Types.CT_ENDFOREACH_KW,
                   C3Types.CT_ENDSWITCH_KW,
                   C3Types.CT_FOR_KW,
                   C3Types.CT_IF_KW,
                   C3Types.CT_SWITCH_KW);
        addMapping("C3_BRACES", DefaultLanguageHighlighterColors.BRACES, C3Types.LB, C3Types.RB);
        addMapping("C3_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS, C3Types.LBT, C3Types.RBT);
        addMapping("C3_PARENS", DefaultLanguageHighlighterColors.PARENTHESES, C3Types.LP, C3Types.RP);
        addMapping("C3_DOT", DefaultLanguageHighlighterColors.DOT, C3Types.DOT);
        addMapping("C3_EOS", DefaultLanguageHighlighterColors.SEMICOLON, C3Types.EOS);
        addMapping("C3_COMMA", DefaultLanguageHighlighterColors.COMMA, C3Types.COMMA);
        addMapping("C3_STRING", DefaultLanguageHighlighterColors.STRING, C3Types.STRING_LIT);
        addMapping("C3_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT, C3ParserDefinition.LINE_COMMENT);
        addMapping("C3_MULTI_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT, C3ParserDefinition.MULTI_COMMENT);
        addMapping("C3_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT, C3ParserDefinition.DOC_COMMENT);
        addMapping("C3_LINE_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT, C3ParserDefinition.LINE_DOC_COMMENT);
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
