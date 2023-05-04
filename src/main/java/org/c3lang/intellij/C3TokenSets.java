package org.c3lang.intellij;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.psi.C3Types;



/**
 * Token groups
 *
 * @author Christoffer Lerno
 */
public interface C3TokenSets
{
    TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    TokenSet COMMENTS = TokenSet.create(C3ParserDefinition.LINE_COMMENT, C3ParserDefinition.BLOCK_COMMENT, C3ParserDefinition.DOC_COMMENT);
    TokenSet STRINGS = TokenSet.create(C3Types.STRING_LIT);

    TokenSet IDENTIFIERS = TokenSet.create(C3Types.IDENT);
    TokenSet INTEGER = TokenSet.create(C3Types.INT_LITERAL);
    TokenSet FLOAT = TokenSet.create(C3Types.FLOAT_LITERAL);
    TokenSet NUMBER = TokenSet.orSet(INTEGER, FLOAT);
    TokenSet LOGICAL_OPS = TokenSet.create(C3Types.AND, C3Types.OR);
    TokenSet RELATIONAL_OPS = TokenSet.create(C3Types.LT_OP, C3Types.LE_OP, C3Types.GT_OP, C3Types.GE_OP);
    TokenSet EQUALITY_OPS = TokenSet.create(C3Types.EQ_OP, C3Types.NE_OP);
    TokenSet BITWISE_OPS = TokenSet.create(C3Types.BIT_OR, C3Types.BIT_XOR, C3Types.AMP);
    TokenSet ADDITIVE_OPS = TokenSet.create(C3Types.PLUS, C3Types.MINUS);
    TokenSet MULTIPLICATIVE_OPS = TokenSet.create(C3Types.STAR, C3Types.DIV, C3Types.MOD);
    TokenSet SHIFT_OPS = TokenSet.create(C3Types.SHL, C3Types.SHR);
    TokenSet INC_DEC = TokenSet.create(C3Types.PLUSPLUS, C3Types.MINUSMINUS);
    TokenSet DOTS = TokenSet.create(C3Types.DOT, C3Types.DOTDOT, C3Types.ELLIPSIS);
    TokenSet BANGQUEST = TokenSet.create(C3Types.BANG, C3Types.ELVIS, C3Types.OPTELSE, C3Types.BANGBANG, C3Types.QUESTION, C3Types.COLON);
    TokenSet PUNCTUATION = TokenSet.create(C3Types.COMMA, C3Types.EOS);
    TokenSet BINARY_OPS = TokenSet.orSet(
            LOGICAL_OPS, RELATIONAL_OPS, EQUALITY_OPS, BITWISE_OPS, ADDITIVE_OPS, MULTIPLICATIVE_OPS, SHIFT_OPS
    );
    TokenSet BRACES = TokenSet.create(C3Types.LB, C3Types.RB);
    TokenSet BRACKETS = TokenSet.create(C3Types.LBT, C3Types.RBT);
    TokenSet PARENTHESES = TokenSet.create(C3Types.LP, C3Types.RP);
    TokenSet OTHER_OPERATORS = TokenSet.create(C3Types.BIT_NOT);
    TokenSet IDENTIFIER = TokenSet.create(C3Types.IDENT);
    TokenSet OPERATOR_ASSIGN_OPS = TokenSet.create(
            C3Types.BIT_AND_ASSIGN,
            C3Types.BIT_OR_ASSIGN,
            C3Types.BIT_XOR_ASSIGN,
            C3Types.MULT_ASSIGN,
            C3Types.DIV_ASSIGN,
            C3Types.MOD_ASSIGN,
            C3Types.PLUS_ASSIGN,
            C3Types.MINUS_ASSIGN,
            C3Types.SHL_ASSIGN,
            C3Types.SHR_ASSIGN
    );
    TokenSet ASSIGNMENT = TokenSet.orSet(TokenSet.create(C3Types.EQ), OPERATOR_ASSIGN_OPS);
    TokenSet TYPES = TokenSet.create(C3Types.KW_ANY,
                                     C3Types.TYPE_IDENT,
                                     C3Types.CT_TYPE_IDENT,
                                     C3Types.KW_ANYFAULT,
                                     C3Types.KW_BFLOAT16,
                                     C3Types.KW_BOOL,
                                     C3Types.KW_CHAR,
                                     C3Types.KW_CT_EVALTYPE,
                                     C3Types.KW_CT_TYPEFROM,
                                     C3Types.KW_CT_TYPEOF,
                                     C3Types.KW_CT_VATYPE,
                                     C3Types.KW_DOUBLE,
                                     C3Types.KW_FLOAT,
                                     C3Types.KW_FLOAT128,
                                     C3Types.KW_FLOAT16,
                                     C3Types.KW_ICHAR,
                                     C3Types.KW_INT,
                                     C3Types.KW_INT128,
                                     C3Types.KW_IPTR,
                                     C3Types.KW_ISZ,
                                     C3Types.KW_LONG,
                                     C3Types.KW_SHORT,
                                     C3Types.KW_TYPEID,
                                     C3Types.KW_UINT,
                                     C3Types.KW_UINT128,
                                     C3Types.KW_ULONG,
                                     C3Types.KW_UPTR,
                                     C3Types.KW_USHORT,
                                     C3Types.KW_USZ,
                                     C3Types.KW_VOID);
    TokenSet CT_KEYWORDS = TokenSet.create(C3Types.KW_CT_ALIGNOF,
                                           C3Types.KW_CT_ASSERT,
                                           C3Types.KW_CT_CASE,
                                           C3Types.KW_CT_CHECKS,
                                           C3Types.KW_CT_DEFAULT,
                                           C3Types.KW_CT_DEFINED,
                                           C3Types.KW_CT_ECHO,
                                           C3Types.KW_CT_ELSE,
                                           C3Types.KW_CT_ENDFOR,
                                           C3Types.KW_CT_ENDFOREACH,
                                           C3Types.KW_CT_ENDIF,
                                           C3Types.KW_CT_ENDSWITCH,
                                           C3Types.KW_CT_EVAL,
                                           C3Types.KW_CT_EXTNAMEOF,
                                           C3Types.KW_CT_FOR,
                                           C3Types.KW_CT_FOREACH,
                                           C3Types.KW_CT_IF,
                                           C3Types.KW_CT_INCLUDE,
                                           C3Types.KW_CT_NAMEOF,
                                           C3Types.KW_CT_OFFSETOF,
                                           C3Types.KW_CT_QNAMEOF,
                                           C3Types.KW_CT_SIZEOF,
                                           C3Types.KW_CT_STRINGIFY,
                                           C3Types.KW_CT_SWITCH,
                                           C3Types.KW_CT_VAARG,
                                           C3Types.KW_CT_VACONST,
                                           C3Types.KW_CT_VACOUNT,
                                           C3Types.KW_CT_VAEXPR,
                                           C3Types.KW_CT_VAREF,
                                           C3Types.KW_CT_VASPLAT);
    TokenSet CONSTANTS = TokenSet.create(C3Types.CONST_IDENT, C3Types.BUILTIN_CONST);
    TokenSet KEYWORDS = TokenSet.create(C3Types.KW_ASM,
                                        C3Types.KW_ASSERT,
                                        C3Types.KW_BITSTRUCT,
                                        C3Types.KW_BREAK,
                                        C3Types.KW_CASE,
                                        C3Types.KW_CATCH,
                                        C3Types.KW_CONST,
                                        C3Types.KW_CONTINUE,
                                        C3Types.KW_DEF,
                                        C3Types.KW_DEFAULT,
                                        C3Types.KW_DEFER,
                                        C3Types.KW_DEFINE,
                                        C3Types.KW_DISTINCT,
                                        C3Types.KW_DO,
                                        C3Types.KW_ELSE,
                                        C3Types.KW_ENUM,
                                        C3Types.KW_EXTERN,
                                        C3Types.KW_FALSE,
                                        C3Types.KW_FAULT,
                                        C3Types.KW_FN,
                                        C3Types.KW_FOR,
                                        C3Types.KW_FOREACH,
                                        C3Types.KW_FOREACH_R,
                                        C3Types.KW_IF,
                                        C3Types.KW_IMPORT,
                                        C3Types.KW_INLINE,
                                        C3Types.KW_MACRO,
                                        C3Types.KW_MODULE,
                                        C3Types.KW_NEXTCASE,
                                        C3Types.KW_NULL,
                                        C3Types.KW_RETURN,
                                        C3Types.KW_STATIC,
                                        C3Types.KW_STRUCT,
                                        C3Types.KW_SWITCH,
                                        C3Types.KW_TLOCAL,
                                        C3Types.KW_TRUE,
                                        C3Types.KW_TRY,
                                        C3Types.KW_TYPEDEF,
                                        C3Types.KW_UNION,
                                        C3Types.KW_VAR,
                                        C3Types.KW_WHILE);

    
}
