package org.c3lang.intellij.lexer;

import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3ParserDefinition;
import org.c3lang.intellij.psi.C3Types;
import com.intellij.psi.TokenType;
import com.intellij.lexer.FlexLexer;

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

REAL                = ({DEC_FLOAT_LIT} | {HEX_FLOAT_LIT}) {REALTYPE}?
DEC_FLOAT_LIT       = {INT}{E} | {INT} "." {INT} {E}?
HEX_FLOAT_LIT       = "0"[xX]{HINT}("."{HINT})?{P}

LINE_COMMENT    = "//" .*

%state IN_COMMENT, IN_RAW_STRING, IN_STRING, IN_CHAR, IN_BYTES_STRING, IN_BYTES_CHAR, IN_BYTES_RAW_STRING

%%


<YYINITIAL> {

    "any" { return C3Types.KW_ANY; }
    "anyfault" { return C3Types.KW_ANYFAULT; }
    "asm" { return C3Types.KW_ASM; }
    "assert" { return C3Types.KW_ASSERT; }
    "bitstruct" { return C3Types.KW_BITSTRUCT; }
    "break" { return C3Types.KW_BREAK; }
    "case" { return C3Types.KW_CASE; }
    "catch" { return C3Types.KW_CATCH; }
    "const" { return C3Types.KW_CONST; }
    "continue" { return C3Types.KW_CONTINUE; }
    "def" { return C3Types.KW_DEF; }
    "default" { return C3Types.KW_DEFAULT; }
    "defer" { return C3Types.KW_DEFER; }
    "distinct" { return C3Types.KW_DISTINCT; }
    "do" { return C3Types.KW_DO; }
    "else" { return C3Types.KW_ELSE; }
    "enum" { return C3Types.KW_ENUM; }
    "extern" { return C3Types.KW_EXTERN; }
    "foreach" { return C3Types.KW_FOREACH; }
    "foreach_r" { return C3Types.KW_FOREACH_R; }
    "false" { return C3Types.KW_FALSE; }
    "fault" { return C3Types.KW_FAULT; }
    "for" { return C3Types.KW_FOR; }
    "fn" { return C3Types.KW_FN; }
    "if" { return C3Types.KW_IF; }
    "inline" { return C3Types.KW_INLINE; }
    "import" { return C3Types.KW_IMPORT; }
    "macro" { return C3Types.KW_MACRO; }
    "module" { return C3Types.KW_MODULE; }
    "nextcase" { return C3Types.KW_NEXTCASE; }
    "null" { return C3Types.KW_NULL; }
    "return" { return C3Types.KW_RETURN; }
    "static" { return C3Types.KW_STATIC; }
    "struct" { return C3Types.KW_STRUCT; }
    "switch" { return C3Types.KW_SWITCH; }
    "tlocal" { return C3Types.KW_TLOCAL; }
    "true" { return C3Types.KW_TRUE; }
    "try" { return C3Types.KW_TRY; }
    "typeid" { return C3Types.KW_TYPEID; }
    "union" { return C3Types.KW_UNION; }
    "var" { return C3Types.KW_VAR; }
    "while" { return C3Types.KW_WHILE; }
    "$alignof" { return C3Types.KW_CT_ALIGNOF; }
    "$assert" { return C3Types.KW_CT_ASSERT; }
    "$case" { return C3Types.KW_CT_CASE; }
    "$checks" { return C3Types.KW_CT_CHECKS; }
    "$default" { return C3Types.KW_CT_DEFAULT; }
    "$defined" { return C3Types.KW_CT_DEFINED; }
    "$echo" { return C3Types.KW_CT_ECHO; }
    "$else" { return C3Types.KW_CT_ELSE; }
    "$endfor" { return C3Types.KW_CT_ENDFOR; }
    "$endforeach" { return C3Types.KW_CT_ENDFOREACH; }
    "$endif" { return C3Types.KW_CT_ENDIF; }
    "$endswitch" { return C3Types.KW_CT_ENDSWITCH; }
    "$error" { return C3Types.KW_CT_ERROR; }
    "$eval" { return C3Types.KW_CT_EVAL; }
    "$evaltype" { return C3Types.KW_CT_EVALTYPE; }
    "$extnameof" { return C3Types.KW_CT_EXTNAMEOF; }
    "$for" { return C3Types.KW_CT_FOR; }
    "$foreach" { return C3Types.KW_CT_FOREACH; }
    "$if" { return C3Types.KW_CT_IF; }
    "$include" { return C3Types.KW_CT_INCLUDE; }
    "$nameof" { return C3Types.KW_CT_NAMEOF; }
    "$sizeof" { return C3Types.KW_CT_SIZEOF; }
    "$stringify" { return C3Types.KW_CT_STRINGIFY; }
    "$switch" { return C3Types.KW_CT_SWITCH; }
    "$typeof" { return C3Types.KW_CT_TYPEOF; }
    "$typefrom" { return C3Types.KW_CT_TYPEFROM; }
    "$qnameof" { return C3Types.KW_CT_QNAMEOF; }
    "$vacount" { return C3Types.KW_CT_VACOUNT; }
    "$vaconst" { return C3Types.KW_CT_VACONST; }
    "$vatype" { return C3Types.KW_CT_VATYPE; }
    "$vaarg" { return C3Types.KW_CT_VAARG; }
    "$varef" { return C3Types.KW_CT_VAREF; }
    "$vaexpr" { return C3Types.KW_CT_VAEXPR; }
    "$vasplat" { return C3Types.KW_CT_VASPLAT; }

    "void" { return C3Types.KW_VOID; }
    "bool" { return C3Types.KW_BOOL; }
    "char" { return C3Types.KW_CHAR; }
    "ichar" { return C3Types.KW_ICHAR; }
    "short" { return C3Types.KW_SHORT; }
    "ushort" { return C3Types.KW_USHORT; }
    "int" { return C3Types.KW_INT; }
    "uint" { return C3Types.KW_UINT; }
    "int" { return C3Types.KW_INT; }
    "long" { return C3Types.KW_LONG; }
    "ulong" { return C3Types.KW_ULONG; }
    "uint128" { return C3Types.KW_UINT128; }
    "int128" { return C3Types.KW_INT128; }

    "bfloat16" { return C3Types.KW_BFLOAT16; }
    "double" { return C3Types.KW_DOUBLE; }
    "float" { return C3Types.KW_FLOAT; }
    "float16" { return C3Types.KW_FLOAT16; }
    "float128" { return C3Types.KW_FLOAT128; }

    "uptr" { return C3Types.KW_UPTR; }
    "iptr" { return C3Types.KW_IPTR; }
    "usz" { return C3Types.KW_USZ; }
    "isz" { return C3Types.KW_ISZ; }

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
    "(<" { return C3Types.LGENPAR; }
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
    ">)" { return C3Types.RGENPAR; }
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


    {INTEGER} { return C3Types.INT_LITERAL; }
    {REAL} { return C3Types.FLOAT_LITERAL; }

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
    {LINE_COMMENT} { return C3ParserDefinition.LINE_COMMENT; }
    "b64\"" { yybegin(IN_BYTES_STRING); }
    "x\"" { yybegin(IN_BYTES_STRING); }
    "b64\`" { yybegin(IN_BYTES_RAW_STRING); }
    "x\`" { yybegin(IN_BYTES_RAW_STRING); }
    "b64\'" { yybegin(IN_BYTES_CHAR); }
    "x\'" { yybegin(IN_BYTES_CHAR); }
    "\"" { yybegin(IN_STRING); }
    "`" { yybegin(IN_RAW_STRING); }
    "'" { yybegin(IN_CHAR); }
    "/*" { yybegin(IN_COMMENT); commentNesting = 1; return C3ParserDefinition.BLOCK_COMMENT; }
}

<IN_RAW_STRING> {
    "``" { }
    "`"  { yybegin(YYINITIAL); return C3Types.STRING_LIT; }
    [\n\r] { }
    . {}

}

<IN_BYTES_RAW_STRING> {
    "`" {WHITESPACE}* ([\r\n] {WHITESPACE}*)* "`" { } // Wrapping
    "`"  { yybegin(YYINITIAL); return C3Types.BYTES; }
    [\n\r] { }
    . {}
}


<IN_CHAR> {
    "\'" { yybegin(YYINITIAL); return C3Types.CHAR_LIT; }
    [^\x00-\x1f\\\']+ { }
    [\r\n] { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
    "\\" [^\x00-\x1f] {  }
    . { return TokenType.BAD_CHARACTER; }
}

<IN_BYTES_CHAR> {
    "\'" {WHITESPACE}* ([\r\n] {WHITESPACE}*)* "\'" { } // Wrapping
    "\'" { yybegin(YYINITIAL); return C3Types.BYTES; }
    [^\x00-\x1f\']+ { }
    [\r\n] { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
    . { return TokenType.BAD_CHARACTER; }
}

<IN_STRING> {
    "\"" { yybegin(YYINITIAL); return C3Types.STRING_LIT; }
    [^\x00-\x1f\\\"]+ { }
    [\r\n] { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
    "\\" [^\x00-\x1f] {  }
    . { return TokenType.BAD_CHARACTER; }
}

<IN_BYTES_STRING> {
    "\"" {WHITESPACE}* ([\r\n] {WHITESPACE}*)* "\"" { } // Wrapping
    "\"" { yybegin(YYINITIAL); return C3Types.BYTES; }
    [^\x00-\x1f\"]+ { }
    [\r\n] { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
    . { return TokenType.BAD_CHARACTER; }
}

<IN_COMMENT> {
    "*/" { if (--commentNesting == 0) { yybegin(YYINITIAL); return C3ParserDefinition.BLOCK_COMMENT; } }
    "/*" { commentNesting++; return C3ParserDefinition.BLOCK_COMMENT; }
     [^*/\n]+ { return C3ParserDefinition.BLOCK_COMMENT; }
     "*"|[/] { return C3ParserDefinition.BLOCK_COMMENT; }
     [\n] { return C3ParserDefinition.BLOCK_COMMENT; }
}

[^]  { return TokenType.BAD_CHARACTER; }



