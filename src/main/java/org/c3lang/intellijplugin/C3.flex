package org.c3lang.intellijplugin.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellijplugin.parser.psi.C3Type;
import org.c3lang.intellijplugin.parser.psi.C3Types;
import com.intellij.psi.TokenType;

%%

%class C3Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

UC_LETTER       = [A-Z]
LC_LETTER       = [a-z]
LETTER          = {UC_LETTER} | {LC_LETTER}
DIGIT           = [0-9]
HEX_DIGIT       = [0-9a-fA-F]
BINARY_DIGIT    = [01]
OCTAL_DIGIT     = [0-7]

UC_LETTER_US    = {UC_LETTER} | "_"
ALPHANUM        = {LETTER} | {DIGIT}
ALPHANUM_US     = {ALPHANUM} | "_"
UC_ALPHANUM_US  = {UC_LETTER_US} | {DIGIT}

IDENTIFIER      = "_"* {LC_LETTER} {ALPHANUM_US}*
CONST_IDENT     = "_"* {UC_LETTER} {UC_ALPHANUM_US}*
TYPE_IDENT      = "_"* {UC_LETTER} "_"* {LC_LETTER} {ALPHANUM_US}*
CT_IDENT        = "$" {IDENTIFIER}
CT_CONST_IDENT  = "$" {CONST_IDENT}
CT_TYPE_IDENT   = "$" {TYPE_IDENT}
EOL             = "\n"
WHITESPACE      = [ \t\v\f] | {EOL}

INTEGER         =  {DECIMAL_LIT} | {BINARY_LIT} | {OCTAL_LIT} | {HEX_LIT}
DECIMAL_LIT     = "0" | [1-9] ("_"* {DECIMAL_DIGITS})?
BINARY_LIT      = "0" [bB] "_"* {BINARY_DIGITS}
OCTAL_LIT       = "0" [oO] "_"* {OCTAL_DIGITS}
HEX_LIT         = "0" [xX] "_"* {HEX_DIGITS}

DECIMAL_DIGITS  = {DIGIT} ("_"* {DIGIT})*
BINARY_DIGITS   = {BINARY_DIGIT} ("_"* {BINARY_DIGIT})*
OCTAL_DIGITS    = {OCTAL_DIGIT} ("_"* {OCTAL_DIGIT})*
HEX_DIGITS      = {HEX_DIGIT} ("_"* {HEX_DIGIT})*

%%


<YYINITIAL> {

    "as" { return C3Types.AS_KW; }
    "break" { return C3Types.BREAK_KW; }
    "case" { return C3Types.CASE_KW; }
    "cast" { return C3Types.CAST_KW; }
    "const" { return C3Types.CONST_KW; }
    "continue" { return C3Types.CONTINUE_KW; }
    "default" { return C3Types.DEFAULT_KW; }
    "defer" { return C3Types.DEFER_KW; }
    "do" { return C3Types.DO_KW; }
    "else" { return C3Types.ELSE_KW; }
    "error" { return C3Types.ELSE_KW; }
    "enum" { return C3Types.ENUM_KW; }
    "for" { return C3Types.FOR_KW; }
    "func" { return C3Types.FUNC_KW; }
    "if" { return C3Types.IF_KW; }
    "in" { return C3Types.IN_KW; }
    "import" { return C3Types.IMPORT_KW; }
    "macro" { return C3Types.MACRO_KW; }
    "module" { return C3Types.MODULE_KW; }
    "next" { return C3Types.NEXT_KW; }
    "null" { return C3Types.NULL_KW; }
    "local" { return C3Types.LOCAL_KW; }
    "public" { return C3Types.PUBLIC_KW; }
    "return" { return C3Types.PUBLIC_KW; }
    "switch" { return C3Types.SWITCH_KW; }
    "try" { return C3Types.TRY_KW; }
    "typedef" { return C3Types.TYPEDEF_KW; }
    "union" { return C3Types.UNION_KW; }
    "while" { return C3Types.WHILE_KW; }
    "$for" { return C3Types.CTFOR_KW; }
    "$if" { return C3Types.CTIF_KW; }
    "$else" { return C3Types.CTELSE_KW; }
    "$elif" { return C3Types.CTELIF_KW; }
    "$switch" { return C3Types.CTSWITCH_KW; }
    "$case" { return C3Types.CTCASE_KW; }
    "$default" { return C3Types.CTDEFAULT_KW; }

    "void" { return C3Types.VOID_KW; }
    "bool" { return C3Types.BOOL_KW; }
    "char" { return C3Types.CHAR_KW; }
    "byte" { return C3Types.BYTE_KW; }
    "short" { return C3Types.SHORT_KW; }
    "ushort" { return C3Types.USHORT_KW; }
    "int" { return C3Types.INT_KW; }
    "uint" { return C3Types.UINT_KW; }
    "long" { return C3Types.LONG_KW; }
    "ulong" { return C3Types.ULONG_KW; }
    "int" { return C3Types.INT_KW; }

    "half" { return C3Types.HALF_KW; }
    "float" { return C3Types.FLOAT_KW; }
    "double" { return C3Types.DOUBLE_KW; }
    "quad" { return C3Types.QUAD_KW; }

    "..." { return C3Types.ELLIPSIS; }
    "::" { return C3Types.SCOPE; }
    "({" { return C3Types.FN_BLOCK_START; }
    "})" { return C3Types.FN_BLOCK_END; }
    ".." { return C3Types.DOTDOT; }
    "<=" { return C3Types.LE_OP; }
    ">=" { return C3Types.GE_OP; }
    "+%" { return C3Types.ADD_MOD_OP; }
    "-%" { return C3Types.SUB_MOD_OP; }
    "++" { return C3Types.INC_OP; }
    "--" { return C3Types.DEC_OP; }
    "@" { return C3Types.AT; }
    "?:" { return C3Types.ELVIS; }
    "?" { return C3Types.QUESTION; }
    ":" { return C3Types.COLON; }
    "<" { return C3Types.LT_OP; }
    ">" { return C3Types.GT_OP; }
    "+" { return C3Types.ADD_OP; }
    "-" { return C3Types.SUB_OP; }
    "=" { return C3Types.EQ; }
    "!" { return C3Types.BANG; }
    "." { return C3Types.DOT; }
    ";" { return C3Types.EOS; }
    "(" { return C3Types.LP; }
    ")" { return C3Types.RP; }
    "," { return C3Types.COMMA; }
    "*" { return C3Types.STAR; }
    "/" { return C3Types.DIV_OP; }
    "^" { return C3Types.BIT_XOR_OP; }
    "|" { return C3Types.BIT_OR_OP; }
    "%" { return C3Types.MOD_OP; }
    "[" { return C3Types.LB; }
    "]" { return C3Types.RB; }
    "{" { return C3Types.LBR; }
    "}" { return C3Types.RBR; }




    {INTEGER} { return C3Types.INT_LITERAL; }

    {CONST_IDENT}  { return C3Types.CONST_IDENT; }
    {CT_CONST_IDENT} { return C3Types.CT_CONST_IDENT; }

    {TYPE_IDENT}  { return C3Types.TYPE_IDENT; }
    {CT_TYPE_IDENT} { return C3Types.CT_TYPE_IDENT; }

    {IDENTIFIER} { return C3Types.IDENT; }
    {CT_IDENT} { return C3Types.CT_IDENT; }

    {WHITESPACE} { return TokenType.WHITE_SPACE; }
}

[^]  { return TokenType.BAD_CHARACTER; }



