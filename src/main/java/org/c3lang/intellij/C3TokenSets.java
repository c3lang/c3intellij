package org.c3lang.intellij;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.parser.psi.C3Types;

import static org.c3lang.intellij.parser.C3ParserDefinition.*;

/**
 * Token groups
 *
 * @author Christoffer Lerno
 */
public interface C3TokenSets
{
    TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, MULTI_COMMENT, LINE_DOC_COMMENT, DOC_COMMENT);
    TokenSet STRINGS = TokenSet.create(C3Types.STRING_LIT);

    TokenSet TYPES = TokenSet.create(
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
            C3Types.TYPE_IDENT);
}
