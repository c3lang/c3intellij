package org.c3lang.intellij.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.parser.C3ParserDefinition;
import org.c3lang.intellij.parser.psi.C3Type;
import org.c3lang.intellij.parser.psi.C3Types;
import com.intellij.psi.TokenType;

%%

%class C3Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
    private int commentNesting = 0;
%}

D			= [0-9]
AN			= [a-zA-Z_0-9]
H			= [a-fA-F0-9]
UA          = [A-Z_0-9]
O           = [0-7]
B           = [0-1]
DC          = [a-z]
UC          = [A-Z]
CONST       = [_]*{UC}{UA}*
TYPE        = [_]*{UC}{UA}*{DC}{AN}*
IDENTIFIER  = [_]*{DC}{AN}*
E			= [Ee][+-]?{D}+
P           = [Pp][+-]?{D}+
B64         = [ \t\v\n\f]?[A-Za-z0-9+/][ \t\v\n\fA-Za-z0-9+/=]+
HEX         = [ \t\v\n\f]?{H}[ \t\v\n\fA-Fa-f0-9]+
SIZES       = 8|16|32|64|128
INTTYPE     = [uU] ([lL] | {SIZES}?) | [iI] {SIZES}
REALTYPE    = [fF]{SIZES}?
INT         = {D}(_*{D})*
HINT        = {H}(_*{H})*
OINT        = {O}(_*{O})*
BINT        = {B}(_*{B})*


AT_CONST_IDENT      = "@" {CONST}
HASH_CONST_IDENT    = "#" {CONST}
CT_CONST_IDENT      = "$" {CONST}
CONST_IDENT         = {CONST}

AT_TYPE_IDENT       = "@" {TYPE}
HASH_TYPE_IDENT     = "#" {TYPE}
CT_TYPE_IDENT       = "$" {TYPE}
TYPE_IDENT          = {TYPE}

BUILTIN_CONST       = "$$" {CONST}
BUILTIN             = "$$" {IDENTIFIER}

AT_IDENT            = "@" {IDENTIFIER}
HASH_IDENT          = "#" {IDENTIFIER}
CT_IDENT            = "$" {IDENTIFIER}

EOL                 = "\n"
WHITESPACE          = [ \t\x0b\f] | {EOL}

INTEGER             = ({DECIMAL_LIT} | {BINARY_LIT} | {OCTAL_LIT} | {HEX_LIT}) {INTTYPE}?
HEX_LIT             = "0" [xX] {HINT}
OCTAL_LIT           = "0" [oO] {OINT}
BINARY_LIT          = "0" [bB] {BINT}
DECIMAL_LIT         = {INT}

BYTES               = {HEXBYTES1} | {HEXBYTES2} | {HEXBYTES3} | {B64BYTES}
HEXBYTES1           = "x'" {HEX}+ "'"
HEXBYTES2           = "x\"" {HEX}+ "\""
HEXBYTES3           = "x`" {HEX}+ "`"

B64BYTES            = "b64" ({STRING_LIT} | {RAW_STR_LIT} | {CHARACTER_LIT})

REAL                = ({DEC_FLOAT_LIT} | {HEX_FLOAT_LIT}) {REALTYPE}?
DEC_FLOAT_LIT       = {INT}{E} | {INT} "." {INT} {E}?
HEX_FLOAT_LIT       = "0"[xX]{HINT}("."{HINT})?{P}

STR_ELEMENT         = [^\\\"\r\n]
CHAR_ELEMENT        = [^\\\'\r\n]
CHAR_LIT_BYTE       = {CHAR_ELEMENT} | \x5C {CHAR_ESCAPE}
STR_LIT_BYTE        = {STR_ELEMENT} | \x5C {CHAR_ESCAPE}
CHAR_ESCAPE         = [abefnrtv\'\"\\0] | "x" {H}{2} | "u" {H}{4} | "U" {H}{8}

CHARACTER_LIT       = \x27 {CHAR_LIT_BYTE}+ \x27

STRING_LIT          = \x22 {STR_LIT_BYTE}* \x22

RAW_ESCAPE          = "``"
RAW_STR_LIT         = "`" ([^`]|{RAW_ESCAPE})* "`"

LINE_COMMENT    = "//" .*
LINE_DOC_COMMENT= "///" .*

%state IN_COMMENT, RAW_STRING

%%


<YYINITIAL> {


    "any" { return C3Types.ANY_KW; }
    "anyfault" { return C3Types.ANYFAULT_KW; }
    "asm" { return C3Types.ASM_KW; }
    "assert" { return C3Types.ASSERT_KW; }
    "bitstruct" { return C3Types.BITSTRUCT_KW; }
    "break" { return C3Types.BREAK_KW; }
    "case" { return C3Types.CASE_KW; }
    "catch" { return C3Types.CATCH_KW; }
    "const" { return C3Types.CONST_KW; }
    "continue" { return C3Types.CONTINUE_KW; }
    "define" { return C3Types.DEFINE_KW; }
    "default" { return C3Types.DEFAULT_KW; }
    "defer" { return C3Types.DEFER_KW; }
    "distinct" { return C3Types.DISTINCT_KW; }
    "do" { return C3Types.DO_KW; }
    "else" { return C3Types.ELSE_KW; }
    "enum" { return C3Types.ENUM_KW; }
    "extern" { return C3Types.EXTERN_KW; }
    "foreach" { return C3Types.FOREACH_KW; }
    "foreach_r" { return C3Types.FOREACH_R_KW; }
    "false" { return C3Types.FALSE_KW; }
    "fault" { return C3Types.FAULT_KW; }
    "for" { return C3Types.FOR_KW; }
    "fn" { return C3Types.FN_KW; }
    "if" { return C3Types.IF_KW; }
    "inline" { return C3Types.INLINE_KW; }
    "import" { return C3Types.IMPORT_KW; }
    "macro" { return C3Types.MACRO_KW; }
    "module" { return C3Types.MODULE_KW; }
    "nextcase" { return C3Types.NEXTCASE_KW; }
    "null" { return C3Types.NULL_KW; }
    "return" { return C3Types.RETURN_KW; }
    "static" { return C3Types.STATIC_KW; }
    "struct" { return C3Types.STRUCT_KW; }
    "switch" { return C3Types.SWITCH_KW; }
    "tlocal" { return C3Types.TLOCAL_KW; }
    "true" { return C3Types.TRUE_KW; }
    "try" { return C3Types.TRY_KW; }
    "typedef" { return C3Types.TYPEDEF_KW; }
    "typeid" { return C3Types.TYPEID_KW; }
    "union" { return C3Types.UNION_KW; }
    "var" { return C3Types.VAR_KW; }
    "while" { return C3Types.WHILE_KW; }
    "$alignof" { return C3Types.CT_ALIGNOF_KW; }
    "$assert" { return C3Types.CT_ASSERT_KW; }
    "$case" { return C3Types.CT_CASE_KW; }
    "$checks" { return C3Types.CT_CHECKS_KW; }
    "$default" { return C3Types.CT_DEFAULT_KW; }
    "$defined" { return C3Types.CT_DEFINED_KW; }
    "$echo" { return C3Types.CT_ECHO_KW; }
    "$else" { return C3Types.CT_ELSE_KW; }
    "$endfor" { return C3Types.CT_ENDFOR_KW; }
    "$endforeach" { return C3Types.CT_ENDFOREACH_KW; }
    "$endif" { return C3Types.CT_ENDIF_KW; }
    "$endswitch" { return C3Types.CT_ENDSWITCH_KW; }
    "$eval" { return C3Types.CT_EVAL_KW; }
    "$evaltype" { return C3Types.CT_EVALTYPE_KW; }
    "$extnameof" { return C3Types.CT_EXTNAMEOF_KW; }
    "$for" { return C3Types.CT_FOR_KW; }
    "$foreach" { return C3Types.CT_FOREACH_KW; }
    "$if" { return C3Types.CT_IF_KW; }
    "$include" { return C3Types.CT_INCLUDE_KW; }
    "$nameof" { return C3Types.CT_NAMEOF_KW; }
    "$sizeof" { return C3Types.CT_SIZEOF_KW; }
    "$stringify" { return C3Types.CT_STRINGIFY_KW; }
    "$switch" { return C3Types.CT_SWITCH_KW; }
    "$typeof" { return C3Types.CT_TYPEOF_KW; }
    "$typefrom" { return C3Types.CT_TYPEFROM_KW; }
    "$qnameof" { return C3Types.CT_QNAMEOF_KW; }
    "$vacount" { return C3Types.CT_VACOUNT_KW; }
    "$vaconst" { return C3Types.CT_VACONST_KW; }
    "$vatype" { return C3Types.CT_VATYPE_KW; }
    "$vaarg" { return C3Types.CT_VAARG_KW; }
    "$varef" { return C3Types.CT_VAREF_KW; }
    "$vaexpr" { return C3Types.CT_VAEXPR_KW; }
    "$vasplat" { return C3Types.CT_VASPLAT_KW; }

    "void" { return C3Types.VOID_KW; }
    "bool" { return C3Types.BOOL_KW; }
    "char" { return C3Types.CHAR_KW; }
    "ichar" { return C3Types.ICHAR_KW; }
    "short" { return C3Types.SHORT_KW; }
    "ushort" { return C3Types.USHORT_KW; }
    "int" { return C3Types.INT_KW; }
    "uint" { return C3Types.UINT_KW; }
    "int" { return C3Types.INT_KW; }
    "long" { return C3Types.LONG_KW; }
    "ulong" { return C3Types.ULONG_KW; }
    "uint128" { return C3Types.UINT128_KW; }
    "int128" { return C3Types.INT128_KW; }

    "bfloat16" { return C3Types.BFLOAT16_KW; }
    "double" { return C3Types.DOUBLE_KW; }
    "float" { return C3Types.FLOAT_KW; }
    "float16" { return C3Types.FLOAT16_KW; }
    "float128" { return C3Types.FLOAT128_KW; }

    "uptr" { return C3Types.UPTR_KW; }
    "iptr" { return C3Types.IPTR_KW; }
    "usz" { return C3Types.USZ_KW; }
    "isz" { return C3Types.ISZ_KW; }

    "..." { return C3Types.ELLIPSIS; }
    "<<=" { return C3Types.SHL_ASSIGN; }
    ">>=" { return C3Types.SHR_ASSIGN; }

    "&&" { return C3Types.AND; }
    "->" { return TokenType.BAD_CHARACTER; } // Arrow
    "&=" { return C3Types.BIT_AND_ASSIGN; }
    "|=" { return C3Types.BIT_OR_ASSIGN; }
    "^=" { return C3Types.BIT_XOR_ASSIGN; }
    "/=" { return C3Types.DIV_ASSIGN; }
    ".." { return C3Types.DOTDOT; }
    "?:" { return C3Types.ELVIS; }
    "==" { return C3Types.EQ_OP; }
    ">=" { return C3Types.GE_OP; }
    "=>" { return C3Types.IMPLIES; }
    "<=" { return C3Types.LE_OP; }
    "{|" { return C3Types.LBRAPIPE; }
    "[<" { return C3Types.LVEC; }
    "-=" { return C3Types.MINUS_ASSIGN; }
    "--" { return C3Types.MINUSMINUS; }
    "%=" { return C3Types.MOD_ASSIGN; }
    "*=" { return C3Types.MULT_ASSIGN; }
    "!=" { return C3Types.NE_OP; }
    "||" { return C3Types.OR; }
    "+=" { return C3Types.PLUS_ASSIGN; }
    "++" { return C3Types.PLUSPLUS; }
    "|}" { return C3Types.RBRAPIPE; }
    ">]" { return C3Types.RVEC; }
    "::" { return C3Types.SCOPE; }
    ">>" { return C3Types.SHR; }
    "<<" { return C3Types.SHL; }
    "!!" { return C3Types.BANGBANG; }
    "??" { return C3Types.OPTELSE; }

    "&" { return C3Types.AMP; }
    "@" { return TokenType.BAD_CHARACTER; }
    "!" { return C3Types.BANG; }
    "~" { return C3Types.BIT_NOT; }
    "|" { return C3Types.BIT_OR; }
    "^" { return C3Types.BIT_XOR; }
    ":" { return C3Types.COLON; }
    "," { return C3Types.COMMA; }
    ";" { return C3Types.EOS; }
    "=" { return C3Types.EQ; }
    ">" { return C3Types.GT_OP; }
    "/" { return C3Types.DIV; }
    "$" { return TokenType.BAD_CHARACTER; } // DOLLAR
    "." { return C3Types.DOT; }
    "#" { return TokenType.BAD_CHARACTER; } // HASH
    "<" { return C3Types.LT_OP; }
    "{" { return C3Types.LB; }
    "[" { return C3Types.LBT; }
    "(" { return C3Types.LP; }
    "-" { return C3Types.MINUS; }
    "%" { return C3Types.MOD; }
    "+" { return C3Types.PLUS; }
    "?" { return C3Types.QUESTION; }
    "]" { return C3Types.RBT; }
    "}" { return C3Types.RB; }
    ")" { return C3Types.RP; }
    "*" { return C3Types.STAR; }


    {STRING_LIT} { return C3Types.STRING_LIT; }
    {RAW_STR_LIT} { return C3Types.STRING_LIT; }
    {CHARACTER_LIT} { return C3Types.CHAR_LIT; }
    {INTEGER} { return C3Types.INT_LITERAL; }
    {REAL} { return C3Types.FLOAT_LITERAL; }
    {BYTES} { return C3Types.BYTES; }

    {HASH_CONST_IDENT} { return TokenType.BAD_CHARACTER; }
    {AT_CONST_IDENT} { return TokenType.BAD_CHARACTER; }
    {CONST_IDENT}  { return C3Types.CONST_IDENT; }
    {CT_CONST_IDENT} { return C3Types.CT_CONST_IDENT; }

    {HASH_TYPE_IDENT} { return TokenType.BAD_CHARACTER; }
    {AT_TYPE_IDENT} { return C3Types.AT_TYPE_IDENT; }
    {TYPE_IDENT}  { return C3Types.TYPE_IDENT; }
    {CT_TYPE_IDENT} { return C3Types.CT_TYPE_IDENT; }

    {HASH_IDENT} { return C3Types.HASH_IDENT; }
    {AT_IDENT} { return C3Types.AT_IDENT; }
    {IDENTIFIER} { return C3Types.IDENT; }
    {CT_IDENT} { return C3Types.CT_IDENT; }

    {BUILTIN} { return C3Types.BUILTIN; }
    {BUILTIN_CONST} { return C3Types.BUILTIN_CONST; }

    {WHITESPACE}+ { return TokenType.WHITE_SPACE; }
    {LINE_DOC_COMMENT} { return C3ParserDefinition.LINE_DOC_COMMENT; }
    {LINE_COMMENT} { return C3ParserDefinition.LINE_COMMENT; }
    "/*" { yybegin(IN_COMMENT); commentNesting = 1; return C3ParserDefinition.MULTI_COMMENT; }
}

<IN_COMMENT> {
    "*/" { if (--commentNesting == 0) { yybegin(YYINITIAL); return C3ParserDefinition.MULTI_COMMENT; } }
    "/*" { commentNesting++; return C3ParserDefinition.MULTI_COMMENT; }
     [^*/\n]+ { return C3ParserDefinition.MULTI_COMMENT; }
     "*"|[/] { return C3ParserDefinition.MULTI_COMMENT; }
     [\n] { return C3ParserDefinition.MULTI_COMMENT; }
}

[^]  { return TokenType.BAD_CHARACTER; }



