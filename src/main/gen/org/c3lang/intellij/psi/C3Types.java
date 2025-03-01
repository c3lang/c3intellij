// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.impl.*;

public interface C3Types {

  IElementType ACCESS_IDENT = new C3ElementType("ACCESS_IDENT");
  IElementType ANY_IDENT = new C3ElementType("ANY_IDENT");
  IElementType ARG = new C3ElementType("ARG");
  IElementType ARG_LIST = new C3ElementType("ARG_LIST");
  IElementType ASM_ADDR = new C3ElementType("ASM_ADDR");
  IElementType ASM_ADDR_TRAILING = new C3ElementType("ASM_ADDR_TRAILING");
  IElementType ASM_BLOCK_STMT = new C3ElementType("ASM_BLOCK_STMT");
  IElementType ASM_DECLARATION = new C3ElementType("ASM_DECLARATION");
  IElementType ASM_EXPR = new C3ElementType("ASM_EXPR");
  IElementType ASM_EXPRS = new C3ElementType("ASM_EXPRS");
  IElementType ASM_INSTR = new C3ElementType("ASM_INSTR");
  IElementType ASM_STMT = new C3ElementType("ASM_STMT");
  IElementType ASSERT_STMT = new C3ElementType("ASSERT_STMT");
  IElementType ASSIGN_TYPE_EXPR = new C3ElementType("ASSIGN_TYPE_EXPR");
  IElementType ATTRIBUTE = new C3ElementType("ATTRIBUTE");
  IElementType ATTRIBUTES = new C3ElementType("ATTRIBUTES");
  IElementType ATTRIBUTE_NAME = new C3ElementType("ATTRIBUTE_NAME");
  IElementType ATTRIBUTE_OPERATOR_EXPR = new C3ElementType("ATTRIBUTE_OPERATOR_EXPR");
  IElementType ATTRIBUTE_PARAM_LIST = new C3ElementType("ATTRIBUTE_PARAM_LIST");
  IElementType ATTR_PARAM = new C3ElementType("ATTR_PARAM");
  IElementType BASE_TYPE = new C3ElementType("BASE_TYPE");
  IElementType BINARY_EXPR = new C3ElementType("BINARY_EXPR");
  IElementType BINARY_OP = new C3ElementType("BINARY_OP");
  IElementType BITSTRUCT_BODY = new C3ElementType("BITSTRUCT_BODY");
  IElementType BITSTRUCT_DECLARATION = new C3ElementType("BITSTRUCT_DECLARATION");
  IElementType BITSTRUCT_DEF = new C3ElementType("BITSTRUCT_DEF");
  IElementType BITSTRUCT_SIMPLE_DEF = new C3ElementType("BITSTRUCT_SIMPLE_DEF");
  IElementType BREAK_STMT = new C3ElementType("BREAK_STMT");
  IElementType BUILTIN_CONST_EXPR = new C3ElementType("BUILTIN_CONST_EXPR");
  IElementType BUILTIN_EXPR = new C3ElementType("BUILTIN_EXPR");
  IElementType BYTES_EXPR = new C3ElementType("BYTES_EXPR");
  IElementType CALL_ARG_LIST = new C3ElementType("CALL_ARG_LIST");
  IElementType CALL_EXPR = new C3ElementType("CALL_EXPR");
  IElementType CALL_EXPR_TAIL = new C3ElementType("CALL_EXPR_TAIL");
  IElementType CALL_INVOCATION = new C3ElementType("CALL_INVOCATION");
  IElementType CASE_STMT = new C3ElementType("CASE_STMT");
  IElementType CATCH_UNWRAP = new C3ElementType("CATCH_UNWRAP");
  IElementType CATCH_UNWRAP_LIST = new C3ElementType("CATCH_UNWRAP_LIST");
  IElementType COMPOUND_INIT_EXPR = new C3ElementType("COMPOUND_INIT_EXPR");
  IElementType COMPOUND_STATEMENT = new C3ElementType("COMPOUND_STATEMENT");
  IElementType COND = new C3ElementType("COND");
  IElementType COND_REPEAT = new C3ElementType("COND_REPEAT");
  IElementType CONSTANT_EXPR = new C3ElementType("CONSTANT_EXPR");
  IElementType CONST_DECLARATION_STMT = new C3ElementType("CONST_DECLARATION_STMT");
  IElementType CONTINUE_STMT = new C3ElementType("CONTINUE_STMT");
  IElementType CT_ANALYZE = new C3ElementType("CT_ANALYZE");
  IElementType CT_ANALYZE_EXPR = new C3ElementType("CT_ANALYZE_EXPR");
  IElementType CT_ARG = new C3ElementType("CT_ARG");
  IElementType CT_ARG_EXPR = new C3ElementType("CT_ARG_EXPR");
  IElementType CT_ASSERT_STMT = new C3ElementType("CT_ASSERT_STMT");
  IElementType CT_CALL = new C3ElementType("CT_CALL");
  IElementType CT_CALL_EXPR = new C3ElementType("CT_CALL_EXPR");
  IElementType CT_CASE_STMT = new C3ElementType("CT_CASE_STMT");
  IElementType CT_DEFINED_CHECK_EXPR = new C3ElementType("CT_DEFINED_CHECK_EXPR");
  IElementType CT_DEFINED_CHECK_EXPR_LIST = new C3ElementType("CT_DEFINED_CHECK_EXPR_LIST");
  IElementType CT_DEFINED_EXPR = new C3ElementType("CT_DEFINED_EXPR");
  IElementType CT_ECHO_STMT = new C3ElementType("CT_ECHO_STMT");
  IElementType CT_ERROR_STMT = new C3ElementType("CT_ERROR_STMT");
  IElementType CT_FEATURE_EXPR = new C3ElementType("CT_FEATURE_EXPR");
  IElementType CT_FOREACH_STMT = new C3ElementType("CT_FOREACH_STMT");
  IElementType CT_FOR_STMT = new C3ElementType("CT_FOR_STMT");
  IElementType CT_IF_STMT = new C3ElementType("CT_IF_STMT");
  IElementType CT_INCLUDE_STMT = new C3ElementType("CT_INCLUDE_STMT");
  IElementType CT_SWITCH = new C3ElementType("CT_SWITCH");
  IElementType CT_SWITCH_BODY = new C3ElementType("CT_SWITCH_BODY");
  IElementType CT_SWITCH_STMT = new C3ElementType("CT_SWITCH_STMT");
  IElementType DECL_OR_EXPR = new C3ElementType("DECL_OR_EXPR");
  IElementType DECL_STMT_AFTER_TYPE = new C3ElementType("DECL_STMT_AFTER_TYPE");
  IElementType DEFAULT_MODULE_SECTION = new C3ElementType("DEFAULT_MODULE_SECTION");
  IElementType DEFAULT_STMT = new C3ElementType("DEFAULT_STMT");
  IElementType DEFER_STMT = new C3ElementType("DEFER_STMT");
  IElementType DEF_ATTR_VALUES = new C3ElementType("DEF_ATTR_VALUES");
  IElementType DEF_DECL = new C3ElementType("DEF_DECL");
  IElementType DEF_DECLARATION_SOURCE = new C3ElementType("DEF_DECLARATION_SOURCE");
  IElementType DISTINCT_DECLARATION = new C3ElementType("DISTINCT_DECLARATION");
  IElementType DO_STMT = new C3ElementType("DO_STMT");
  IElementType ELSE_PART = new C3ElementType("ELSE_PART");
  IElementType ENUM_CONSTANT = new C3ElementType("ENUM_CONSTANT");
  IElementType ENUM_DECLARATION = new C3ElementType("ENUM_DECLARATION");
  IElementType ENUM_LIST = new C3ElementType("ENUM_LIST");
  IElementType ENUM_PARAM_DECL = new C3ElementType("ENUM_PARAM_DECL");
  IElementType ENUM_PARAM_LIST = new C3ElementType("ENUM_PARAM_LIST");
  IElementType ENUM_SPEC = new C3ElementType("ENUM_SPEC");
  IElementType EXPR = new C3ElementType("EXPR");
  IElementType EXPRESSION_LIST = new C3ElementType("EXPRESSION_LIST");
  IElementType EXPR_BLOCK_EXPR = new C3ElementType("EXPR_BLOCK_EXPR");
  IElementType EXPR_STMT = new C3ElementType("EXPR_STMT");
  IElementType EXPR_TERMINATOR = new C3ElementType("EXPR_TERMINATOR");
  IElementType FAULT_DECLARATION = new C3ElementType("FAULT_DECLARATION");
  IElementType FLAT_PATH = new C3ElementType("FLAT_PATH");
  IElementType FLOAT_TYPE = new C3ElementType("FLOAT_TYPE");
  IElementType FN_PARAMETER_LIST = new C3ElementType("FN_PARAMETER_LIST");
  IElementType FOREACH_STMT = new C3ElementType("FOREACH_STMT");
  IElementType FOREACH_VAR = new C3ElementType("FOREACH_VAR");
  IElementType FOREACH_VARS = new C3ElementType("FOREACH_VARS");
  IElementType FOR_COND = new C3ElementType("FOR_COND");
  IElementType FOR_STMT = new C3ElementType("FOR_STMT");
  IElementType FUNC_DEF = C3StubElementTypeFactory.stubFactory("FUNC_DEF");
  IElementType FUNC_DEFINITION = new C3ElementType("FUNC_DEFINITION");
  IElementType FUNC_HEADER = new C3ElementType("FUNC_HEADER");
  IElementType FUNC_NAME = new C3ElementType("FUNC_NAME");
  IElementType FUNC_TYPEDEF = new C3ElementType("FUNC_TYPEDEF");
  IElementType GENERIC_PARAMETER = new C3ElementType("GENERIC_PARAMETER");
  IElementType GENERIC_PARAMETERS = new C3ElementType("GENERIC_PARAMETERS");
  IElementType GLOBAL_DECL = new C3ElementType("GLOBAL_DECL");
  IElementType GLOBAL_MULTI_DECLARATION = new C3ElementType("GLOBAL_MULTI_DECLARATION");
  IElementType GLOBAL_SINGLE_DECLARATION = new C3ElementType("GLOBAL_SINGLE_DECLARATION");
  IElementType GROUPED_EXPR = new C3ElementType("GROUPED_EXPR");
  IElementType GROUPED_EXPRESSION = new C3ElementType("GROUPED_EXPRESSION");
  IElementType IDENTIFIER_LIST = new C3ElementType("IDENTIFIER_LIST");
  IElementType IF_STMT = new C3ElementType("IF_STMT");
  IElementType IMPLIES_BODY = new C3ElementType("IMPLIES_BODY");
  IElementType IMPORT_DECL = new C3ElementType("IMPORT_DECL");
  IElementType IMPORT_PATH = new C3ElementType("IMPORT_PATH");
  IElementType IMPORT_PATHS = new C3ElementType("IMPORT_PATHS");
  IElementType INITIALIZER_LIST = new C3ElementType("INITIALIZER_LIST");
  IElementType INIT_LIST_EXPR = new C3ElementType("INIT_LIST_EXPR");
  IElementType INTEGER_TYPE = new C3ElementType("INTEGER_TYPE");
  IElementType INTERFACE_BODY = new C3ElementType("INTERFACE_BODY");
  IElementType INTERFACE_DEFINITION = new C3ElementType("INTERFACE_DEFINITION");
  IElementType INTERFACE_IMPL = new C3ElementType("INTERFACE_IMPL");
  IElementType KEYWORD_EXPR = new C3ElementType("KEYWORD_EXPR");
  IElementType LABEL = new C3ElementType("LABEL");
  IElementType LAMBDA_DECL = new C3ElementType("LAMBDA_DECL");
  IElementType LAMBDA_DECL_EXPR = new C3ElementType("LAMBDA_DECL_EXPR");
  IElementType LAMBDA_DECL_SHORT_EXPR = new C3ElementType("LAMBDA_DECL_SHORT_EXPR");
  IElementType LITERAL_EXPR = new C3ElementType("LITERAL_EXPR");
  IElementType LOCAL_DECLARATION_STMT = new C3ElementType("LOCAL_DECLARATION_STMT");
  IElementType LOCAL_DECL_AFTER_TYPE = new C3ElementType("LOCAL_DECL_AFTER_TYPE");
  IElementType LOCAL_DECL_AFTER_TYPE_1 = new C3ElementType("LOCAL_DECL_AFTER_TYPE_1");
  IElementType LOCAL_DECL_AFTER_TYPE_2 = new C3ElementType("LOCAL_DECL_AFTER_TYPE_2");
  IElementType LOCAL_DECL_STORAGE = new C3ElementType("LOCAL_DECL_STORAGE");
  IElementType LOCAL_IDENT_EXPR = new C3ElementType("LOCAL_IDENT_EXPR");
  IElementType MACRO_DEFINITION = new C3ElementType("MACRO_DEFINITION");
  IElementType MACRO_FUNC_BODY = new C3ElementType("MACRO_FUNC_BODY");
  IElementType MACRO_HEADER = new C3ElementType("MACRO_HEADER");
  IElementType MACRO_NAME = new C3ElementType("MACRO_NAME");
  IElementType MACRO_PARAMS = new C3ElementType("MACRO_PARAMS");
  IElementType MODULE = C3StubElementTypeFactory.stubFactory("MODULE");
  IElementType MODULE_PARAM = new C3ElementType("MODULE_PARAM");
  IElementType MODULE_PARAMS = new C3ElementType("MODULE_PARAMS");
  IElementType MODULE_PATH = new C3ElementType("MODULE_PATH");
  IElementType MODULE_SECTION = new C3ElementType("MODULE_SECTION");
  IElementType MULTI_DECLARATION = new C3ElementType("MULTI_DECLARATION");
  IElementType NAMED_IDENT = new C3ElementType("NAMED_IDENT");
  IElementType NEXTCASE_STMT = new C3ElementType("NEXTCASE_STMT");
  IElementType OPTIONAL_EXPR = new C3ElementType("OPTIONAL_EXPR");
  IElementType OPTIONAL_TYPE = new C3ElementType("OPTIONAL_TYPE");
  IElementType PARAMETER = new C3ElementType("PARAMETER");
  IElementType PARAMETER_LIST = new C3ElementType("PARAMETER_LIST");
  IElementType PARAM_DECL = new C3ElementType("PARAM_DECL");
  IElementType PARAM_PATH = new C3ElementType("PARAM_PATH");
  IElementType PARAM_PATH_ELEMENT = new C3ElementType("PARAM_PATH_ELEMENT");
  IElementType PAREN_COND = new C3ElementType("PAREN_COND");
  IElementType PATH = new C3ElementType("PATH");
  IElementType PATH_AT_IDENT = new C3ElementType("PATH_AT_IDENT");
  IElementType PATH_AT_IDENT_EXPR = new C3ElementType("PATH_AT_IDENT_EXPR");
  IElementType PATH_CONST = new C3ElementType("PATH_CONST");
  IElementType PATH_CONST_EXPR = new C3ElementType("PATH_CONST_EXPR");
  IElementType PATH_IDENT = new C3ElementType("PATH_IDENT");
  IElementType PATH_IDENT_EXPR = new C3ElementType("PATH_IDENT_EXPR");
  IElementType RANGE_EXP = new C3ElementType("RANGE_EXP");
  IElementType RANGE_LOC = new C3ElementType("RANGE_LOC");
  IElementType RETURN_STMT = new C3ElementType("RETURN_STMT");
  IElementType STATEMENT = new C3ElementType("STATEMENT");
  IElementType STATEMENT_LIST = new C3ElementType("STATEMENT_LIST");
  IElementType STRING_EXPR = new C3ElementType("STRING_EXPR");
  IElementType STRUCT_BODY = new C3ElementType("STRUCT_BODY");
  IElementType STRUCT_DECLARATION = C3StubElementTypeFactory.stubFactory("STRUCT_DECLARATION");
  IElementType STRUCT_MEMBER_DECLARATION = new C3ElementType("STRUCT_MEMBER_DECLARATION");
  IElementType SWITCH_BODY = new C3ElementType("SWITCH_BODY");
  IElementType SWITCH_STMT = new C3ElementType("SWITCH_STMT");
  IElementType TERNARY_EXPR = new C3ElementType("TERNARY_EXPR");
  IElementType TOP_LEVEL = new C3ElementType("TOP_LEVEL");
  IElementType TRAILING_BLOCK_PARAM = new C3ElementType("TRAILING_BLOCK_PARAM");
  IElementType TRY_UNWRAP = new C3ElementType("TRY_UNWRAP");
  IElementType TRY_UNWRAP_CHAIN = new C3ElementType("TRY_UNWRAP_CHAIN");
  IElementType TYPE = new C3ElementType("TYPE");
  IElementType TYPEDEF_TYPE = new C3ElementType("TYPEDEF_TYPE");
  IElementType TYPE_ACCESS_EXPR = new C3ElementType("TYPE_ACCESS_EXPR");
  IElementType TYPE_DECL = new C3ElementType("TYPE_DECL");
  IElementType TYPE_NAME = new C3ElementType("TYPE_NAME");
  IElementType TYPE_SUFFIX = new C3ElementType("TYPE_SUFFIX");
  IElementType UNARY_EXPR = new C3ElementType("UNARY_EXPR");
  IElementType UNARY_OP = new C3ElementType("UNARY_OP");
  IElementType VAR_DECL = new C3ElementType("VAR_DECL");
  IElementType VAR_STMT = new C3ElementType("VAR_STMT");
  IElementType WHILE_STMT = new C3ElementType("WHILE_STMT");

  IElementType AMP = new C3TokenType("&");
  IElementType AND = new C3TokenType("&&");
  IElementType AT_CONST_IDENT = new C3TokenType("AT_CONST_IDENT");
  IElementType AT_IDENT = new C3TokenType("AT_IDENT");
  IElementType AT_TYPE_IDENT = new C3TokenType("AT_TYPE_IDENT");
  IElementType BAD_CHARACTER = new C3TokenType("#");
  IElementType BANG = new C3TokenType("!");
  IElementType BANGBANG = new C3TokenType("!!");
  IElementType BINARY_LIT = new C3TokenType("BINARY_LIT");
  IElementType BINT = new C3TokenType("BINT");
  IElementType BIT_AND_ASSIGN = new C3TokenType("&=");
  IElementType BIT_NOT = new C3TokenType("~");
  IElementType BIT_OR = new C3TokenType("|");
  IElementType BIT_OR_ASSIGN = new C3TokenType("|=");
  IElementType BIT_XOR = new C3TokenType("^");
  IElementType BIT_XOR_ASSIGN = new C3TokenType("^=");
  IElementType BUILTIN = new C3TokenType("BUILTIN");
  IElementType BUILTIN_CONST = new C3TokenType("BUILTIN_CONST");
  IElementType BYTES = new C3TokenType("BYTES");
  IElementType CHAR_LIT = new C3TokenType("CHAR_LIT");
  IElementType COLON = new C3TokenType(":");
  IElementType COMMA = new C3TokenType(",");
  IElementType CONST_IDENT = new C3TokenType("CONST_IDENT");
  IElementType CT_AND = new C3TokenType("&&&");
  IElementType CT_CONST_IDENT = new C3TokenType("CT_CONST_IDENT");
  IElementType CT_IDENT = new C3TokenType("CT_IDENT");
  IElementType CT_OR = new C3TokenType("|||");
  IElementType CT_PLUS = new C3TokenType("+++");
  IElementType CT_TYPE_IDENT = new C3TokenType("CT_TYPE_IDENT");
  IElementType DECIMAL_LIT = new C3TokenType("DECIMAL_LIT");
  IElementType DIV = new C3TokenType("/");
  IElementType DIV_ASSIGN = new C3TokenType("/=");
  IElementType DOT = new C3TokenType(".");
  IElementType DOTDOT = new C3TokenType("..");
  IElementType E = new C3TokenType("E");
  IElementType ELLIPSIS = new C3TokenType("...");
  IElementType ELVIS = new C3TokenType("?:");
  IElementType EOL = new C3TokenType("\\n");
  IElementType EOS = new C3TokenType(";");
  IElementType EQ = new C3TokenType("=");
  IElementType EQ_OP = new C3TokenType("==");
  IElementType FLOAT_LITERAL = new C3TokenType("FLOAT_LITERAL");
  IElementType GE_OP = new C3TokenType(">=");
  IElementType GT_OP = new C3TokenType(">");
  IElementType HASH_CONST_IDENT = new C3TokenType("HASH_CONST_IDENT");
  IElementType HASH_IDENT = new C3TokenType("HASH_IDENT");
  IElementType HASH_TYPE_IDENT = new C3TokenType("HASH_TYPE_IDENT");
  IElementType HEX_LIT = new C3TokenType("HEX_LIT");
  IElementType HINT = new C3TokenType("HINT");
  IElementType IDENT = new C3TokenType("IDENT");
  IElementType IMPLIES = new C3TokenType("=>");
  IElementType INTTYPE = new C3TokenType("INTTYPE");
  IElementType INT_LITERAL = new C3TokenType("INT_LITERAL");
  IElementType KW_ANY = new C3TokenType("any");
  IElementType KW_ANYFAULT = new C3TokenType("anyfault");
  IElementType KW_ASM = new C3TokenType("asm");
  IElementType KW_ASSERT = new C3TokenType("assert");
  IElementType KW_BFLOAT16 = new C3TokenType("bfloat16");
  IElementType KW_BITSTRUCT = new C3TokenType("bitstruct");
  IElementType KW_BOOL = new C3TokenType("bool");
  IElementType KW_BREAK = new C3TokenType("break");
  IElementType KW_CASE = new C3TokenType("case");
  IElementType KW_CATCH = new C3TokenType("catch");
  IElementType KW_CHAR = new C3TokenType("char");
  IElementType KW_CONST = new C3TokenType("const");
  IElementType KW_CONTINUE = new C3TokenType("continue");
  IElementType KW_CT_ALIGNOF = new C3TokenType("$alignof");
  IElementType KW_CT_ASSERT = new C3TokenType("$assert");
  IElementType KW_CT_CASE = new C3TokenType("$case");
  IElementType KW_CT_DEFAULT = new C3TokenType("$default");
  IElementType KW_CT_DEFINED = new C3TokenType("$defined");
  IElementType KW_CT_ECHO = new C3TokenType("$echo");
  IElementType KW_CT_ELSE = new C3TokenType("$else");
  IElementType KW_CT_ENDFOR = new C3TokenType("$endfor");
  IElementType KW_CT_ENDFOREACH = new C3TokenType("$endforeach");
  IElementType KW_CT_ENDIF = new C3TokenType("$endif");
  IElementType KW_CT_ENDSWITCH = new C3TokenType("$endswitch");
  IElementType KW_CT_ERROR = new C3TokenType("$error");
  IElementType KW_CT_EVAL = new C3TokenType("$eval");
  IElementType KW_CT_EVALTYPE = new C3TokenType("$evaltype");
  IElementType KW_CT_EXTNAMEOF = new C3TokenType("$extnameof");
  IElementType KW_CT_FEATURE = new C3TokenType("$feature");
  IElementType KW_CT_FOR = new C3TokenType("$for");
  IElementType KW_CT_FOREACH = new C3TokenType("$foreach");
  IElementType KW_CT_IF = new C3TokenType("$if");
  IElementType KW_CT_INCLUDE = new C3TokenType("$include");
  IElementType KW_CT_IS_CONST = new C3TokenType("$is_const");
  IElementType KW_CT_NAMEOF = new C3TokenType("$nameof");
  IElementType KW_CT_OFFSETOF = new C3TokenType("KW_CT_OFFSETOF");
  IElementType KW_CT_QNAMEOF = new C3TokenType("$qnameof");
  IElementType KW_CT_SIZEOF = new C3TokenType("$sizeof");
  IElementType KW_CT_STRINGIFY = new C3TokenType("$stringify");
  IElementType KW_CT_SWITCH = new C3TokenType("$switch");
  IElementType KW_CT_TYPEFROM = new C3TokenType("$typefrom");
  IElementType KW_CT_TYPEOF = new C3TokenType("$typeof");
  IElementType KW_CT_VAARG = new C3TokenType("$vaarg");
  IElementType KW_CT_VACONST = new C3TokenType("$vaconst");
  IElementType KW_CT_VACOUNT = new C3TokenType("$vacount");
  IElementType KW_CT_VAEXPR = new C3TokenType("$vaexpr");
  IElementType KW_CT_VAREF = new C3TokenType("$varef");
  IElementType KW_CT_VASPLAT = new C3TokenType("$vasplat");
  IElementType KW_CT_VATYPE = new C3TokenType("$vatype");
  IElementType KW_DEF = new C3TokenType("def");
  IElementType KW_DEFAULT = new C3TokenType("default");
  IElementType KW_DEFER = new C3TokenType("defer");
  IElementType KW_DISTINCT = new C3TokenType("distinct");
  IElementType KW_DO = new C3TokenType("do");
  IElementType KW_DOUBLE = new C3TokenType("double");
  IElementType KW_ELSE = new C3TokenType("else");
  IElementType KW_ENUM = new C3TokenType("enum");
  IElementType KW_EXTERN = new C3TokenType("extern");
  IElementType KW_FALSE = new C3TokenType("false");
  IElementType KW_FAULT = new C3TokenType("fault");
  IElementType KW_FLOAT = new C3TokenType("float");
  IElementType KW_FLOAT128 = new C3TokenType("float128");
  IElementType KW_FLOAT16 = new C3TokenType("float16");
  IElementType KW_FN = new C3TokenType("fn");
  IElementType KW_FOR = new C3TokenType("for");
  IElementType KW_FOREACH = new C3TokenType("foreach");
  IElementType KW_FOREACH_R = new C3TokenType("foreach_r");
  IElementType KW_ICHAR = new C3TokenType("ichar");
  IElementType KW_IF = new C3TokenType("if");
  IElementType KW_IMPORT = new C3TokenType("import");
  IElementType KW_INLINE = new C3TokenType("inline");
  IElementType KW_INT = new C3TokenType("int");
  IElementType KW_INT128 = new C3TokenType("int128");
  IElementType KW_INTERFACE = new C3TokenType("interface");
  IElementType KW_IPTR = new C3TokenType("iptr");
  IElementType KW_ISZ = new C3TokenType("isz");
  IElementType KW_LONG = new C3TokenType("long");
  IElementType KW_MACRO = new C3TokenType("macro");
  IElementType KW_MODULE = new C3TokenType("module");
  IElementType KW_NEXTCASE = new C3TokenType("nextcase");
  IElementType KW_NULL = new C3TokenType("null");
  IElementType KW_RETURN = new C3TokenType("return");
  IElementType KW_SHORT = new C3TokenType("short");
  IElementType KW_STATIC = new C3TokenType("static");
  IElementType KW_STRUCT = new C3TokenType("struct");
  IElementType KW_SWITCH = new C3TokenType("switch");
  IElementType KW_TLOCAL = new C3TokenType("tlocal");
  IElementType KW_TRUE = new C3TokenType("true");
  IElementType KW_TRY = new C3TokenType("try");
  IElementType KW_TYPEID = new C3TokenType("typeid");
  IElementType KW_UINT = new C3TokenType("uint");
  IElementType KW_UINT128 = new C3TokenType("uint128");
  IElementType KW_ULONG = new C3TokenType("ulong");
  IElementType KW_UNION = new C3TokenType("union");
  IElementType KW_UPTR = new C3TokenType("uptr");
  IElementType KW_USHORT = new C3TokenType("ushort");
  IElementType KW_USZ = new C3TokenType("usz");
  IElementType KW_VAR = new C3TokenType("var");
  IElementType KW_VOID = new C3TokenType("void");
  IElementType KW_WHILE = new C3TokenType("while");
  IElementType LB = new C3TokenType("{");
  IElementType LBRAPIPE = new C3TokenType("{|");
  IElementType LBT = new C3TokenType("[");
  IElementType LE_OP = new C3TokenType("<=");
  IElementType LGENPAR = new C3TokenType("(<");
  IElementType LP = new C3TokenType("(");
  IElementType LT_OP = new C3TokenType("<");
  IElementType LVEC = new C3TokenType("[<");
  IElementType MINUS = new C3TokenType("-");
  IElementType MINUSMINUS = new C3TokenType("--");
  IElementType MINUS_ASSIGN = new C3TokenType("-=");
  IElementType MOD = new C3TokenType("%");
  IElementType MOD_ASSIGN = new C3TokenType("%=");
  IElementType MULT_ASSIGN = new C3TokenType("*=");
  IElementType NE_OP = new C3TokenType("!=");
  IElementType OCTAL_LIT = new C3TokenType("OCTAL_LIT");
  IElementType OINT = new C3TokenType("OINT");
  IElementType OPTELSE = new C3TokenType("??");
  IElementType OR = new C3TokenType("||");
  IElementType P = new C3TokenType("P");
  IElementType PLUS = new C3TokenType("+");
  IElementType PLUSPLUS = new C3TokenType("++");
  IElementType PLUS_ASSIGN = new C3TokenType("+=");
  IElementType QUESTION = new C3TokenType("?");
  IElementType RB = new C3TokenType("}");
  IElementType RBRAPIPE = new C3TokenType("|}");
  IElementType RBT = new C3TokenType("]");
  IElementType REALTYPE = new C3TokenType("REALTYPE");
  IElementType RGENPAR = new C3TokenType(">)");
  IElementType RP = new C3TokenType(")");
  IElementType RVEC = new C3TokenType(">]");
  IElementType SCOPE = new C3TokenType("::");
  IElementType SHL = new C3TokenType("<<");
  IElementType SHL_ASSIGN = new C3TokenType("<<=");
  IElementType SHR = new C3TokenType(">>");
  IElementType SHR_ASSIGN = new C3TokenType(">>=");
  IElementType SIZES = new C3TokenType("SIZES");
  IElementType STAR = new C3TokenType("*");
  IElementType STRING_LIT = new C3TokenType("STRING_LIT");
  IElementType TYPE_IDENT = new C3TokenType("TYPE_IDENT");
  IElementType UNDERSCORE = new C3TokenType("_");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ACCESS_IDENT) {
        return new C3AccessIdentImpl(node);
      }
      else if (type == ANY_IDENT) {
        return new C3AnyIdentImpl(node);
      }
      else if (type == ARG) {
        return new C3ArgImpl(node);
      }
      else if (type == ARG_LIST) {
        return new C3ArgListImpl(node);
      }
      else if (type == ASM_ADDR) {
        return new C3AsmAddrImpl(node);
      }
      else if (type == ASM_ADDR_TRAILING) {
        return new C3AsmAddrTrailingImpl(node);
      }
      else if (type == ASM_BLOCK_STMT) {
        return new C3AsmBlockStmtImpl(node);
      }
      else if (type == ASM_DECLARATION) {
        return new C3AsmDeclarationImpl(node);
      }
      else if (type == ASM_EXPR) {
        return new C3AsmExprImpl(node);
      }
      else if (type == ASM_EXPRS) {
        return new C3AsmExprsImpl(node);
      }
      else if (type == ASM_INSTR) {
        return new C3AsmInstrImpl(node);
      }
      else if (type == ASM_STMT) {
        return new C3AsmStmtImpl(node);
      }
      else if (type == ASSERT_STMT) {
        return new C3AssertStmtImpl(node);
      }
      else if (type == ASSIGN_TYPE_EXPR) {
        return new C3AssignTypeExprImpl(node);
      }
      else if (type == ATTRIBUTE) {
        return new C3AttributeImpl(node);
      }
      else if (type == ATTRIBUTES) {
        return new C3AttributesImpl(node);
      }
      else if (type == ATTRIBUTE_NAME) {
        return new C3AttributeNameImpl(node);
      }
      else if (type == ATTRIBUTE_OPERATOR_EXPR) {
        return new C3AttributeOperatorExprImpl(node);
      }
      else if (type == ATTRIBUTE_PARAM_LIST) {
        return new C3AttributeParamListImpl(node);
      }
      else if (type == ATTR_PARAM) {
        return new C3AttrParamImpl(node);
      }
      else if (type == BASE_TYPE) {
        return new C3BaseTypeImpl(node);
      }
      else if (type == BINARY_EXPR) {
        return new C3BinaryExprImpl(node);
      }
      else if (type == BINARY_OP) {
        return new C3BinaryOpImpl(node);
      }
      else if (type == BITSTRUCT_BODY) {
        return new C3BitstructBodyImpl(node);
      }
      else if (type == BITSTRUCT_DECLARATION) {
        return new C3BitstructDeclarationImpl(node);
      }
      else if (type == BITSTRUCT_DEF) {
        return new C3BitstructDefImpl(node);
      }
      else if (type == BITSTRUCT_SIMPLE_DEF) {
        return new C3BitstructSimpleDefImpl(node);
      }
      else if (type == BREAK_STMT) {
        return new C3BreakStmtImpl(node);
      }
      else if (type == BUILTIN_CONST_EXPR) {
        return new C3BuiltinConstExprImpl(node);
      }
      else if (type == BUILTIN_EXPR) {
        return new C3BuiltinExprImpl(node);
      }
      else if (type == BYTES_EXPR) {
        return new C3BytesExprImpl(node);
      }
      else if (type == CALL_ARG_LIST) {
        return new C3CallArgListImpl(node);
      }
      else if (type == CALL_EXPR) {
        return new C3CallExprImpl(node);
      }
      else if (type == CALL_EXPR_TAIL) {
        return new C3CallExprTailImpl(node);
      }
      else if (type == CALL_INVOCATION) {
        return new C3CallInvocationImpl(node);
      }
      else if (type == CASE_STMT) {
        return new C3CaseStmtImpl(node);
      }
      else if (type == CATCH_UNWRAP) {
        return new C3CatchUnwrapImpl(node);
      }
      else if (type == CATCH_UNWRAP_LIST) {
        return new C3CatchUnwrapListImpl(node);
      }
      else if (type == COMPOUND_INIT_EXPR) {
        return new C3CompoundInitExprImpl(node);
      }
      else if (type == COMPOUND_STATEMENT) {
        return new C3CompoundStatementImpl(node);
      }
      else if (type == COND) {
        return new C3CondImpl(node);
      }
      else if (type == COND_REPEAT) {
        return new C3CondRepeatImpl(node);
      }
      else if (type == CONST_DECLARATION_STMT) {
        return new C3ConstDeclarationStmtImpl(node);
      }
      else if (type == CONTINUE_STMT) {
        return new C3ContinueStmtImpl(node);
      }
      else if (type == CT_ANALYZE) {
        return new C3CtAnalyzeImpl(node);
      }
      else if (type == CT_ANALYZE_EXPR) {
        return new C3CtAnalyzeExprImpl(node);
      }
      else if (type == CT_ARG) {
        return new C3CtArgImpl(node);
      }
      else if (type == CT_ARG_EXPR) {
        return new C3CtArgExprImpl(node);
      }
      else if (type == CT_ASSERT_STMT) {
        return new C3CtAssertStmtImpl(node);
      }
      else if (type == CT_CALL) {
        return new C3CtCallImpl(node);
      }
      else if (type == CT_CALL_EXPR) {
        return new C3CtCallExprImpl(node);
      }
      else if (type == CT_CASE_STMT) {
        return new C3CtCaseStmtImpl(node);
      }
      else if (type == CT_DEFINED_CHECK_EXPR) {
        return new C3CtDefinedCheckExprImpl(node);
      }
      else if (type == CT_DEFINED_CHECK_EXPR_LIST) {
        return new C3CtDefinedCheckExprListImpl(node);
      }
      else if (type == CT_DEFINED_EXPR) {
        return new C3CtDefinedExprImpl(node);
      }
      else if (type == CT_ECHO_STMT) {
        return new C3CtEchoStmtImpl(node);
      }
      else if (type == CT_ERROR_STMT) {
        return new C3CtErrorStmtImpl(node);
      }
      else if (type == CT_FEATURE_EXPR) {
        return new C3CtFeatureExprImpl(node);
      }
      else if (type == CT_FOREACH_STMT) {
        return new C3CtForeachStmtImpl(node);
      }
      else if (type == CT_FOR_STMT) {
        return new C3CtForStmtImpl(node);
      }
      else if (type == CT_IF_STMT) {
        return new C3CtIfStmtImpl(node);
      }
      else if (type == CT_INCLUDE_STMT) {
        return new C3CtIncludeStmtImpl(node);
      }
      else if (type == CT_SWITCH) {
        return new C3CtSwitchImpl(node);
      }
      else if (type == CT_SWITCH_BODY) {
        return new C3CtSwitchBodyImpl(node);
      }
      else if (type == CT_SWITCH_STMT) {
        return new C3CtSwitchStmtImpl(node);
      }
      else if (type == DECL_OR_EXPR) {
        return new C3DeclOrExprImpl(node);
      }
      else if (type == DECL_STMT_AFTER_TYPE) {
        return new C3DeclStmtAfterTypeImpl(node);
      }
      else if (type == DEFAULT_MODULE_SECTION) {
        return new C3DefaultModuleSectionImpl(node);
      }
      else if (type == DEFAULT_STMT) {
        return new C3DefaultStmtImpl(node);
      }
      else if (type == DEFER_STMT) {
        return new C3DeferStmtImpl(node);
      }
      else if (type == DEF_ATTR_VALUES) {
        return new C3DefAttrValuesImpl(node);
      }
      else if (type == DEF_DECL) {
        return new C3DefDeclImpl(node);
      }
      else if (type == DEF_DECLARATION_SOURCE) {
        return new C3DefDeclarationSourceImpl(node);
      }
      else if (type == DISTINCT_DECLARATION) {
        return new C3DistinctDeclarationImpl(node);
      }
      else if (type == DO_STMT) {
        return new C3DoStmtImpl(node);
      }
      else if (type == ELSE_PART) {
        return new C3ElsePartImpl(node);
      }
      else if (type == ENUM_CONSTANT) {
        return new C3EnumConstantImpl(node);
      }
      else if (type == ENUM_DECLARATION) {
        return new C3EnumDeclarationImpl(node);
      }
      else if (type == ENUM_LIST) {
        return new C3EnumListImpl(node);
      }
      else if (type == ENUM_PARAM_DECL) {
        return new C3EnumParamDeclImpl(node);
      }
      else if (type == ENUM_PARAM_LIST) {
        return new C3EnumParamListImpl(node);
      }
      else if (type == ENUM_SPEC) {
        return new C3EnumSpecImpl(node);
      }
      else if (type == EXPRESSION_LIST) {
        return new C3ExpressionListImpl(node);
      }
      else if (type == EXPR_BLOCK_EXPR) {
        return new C3ExprBlockExprImpl(node);
      }
      else if (type == EXPR_STMT) {
        return new C3ExprStmtImpl(node);
      }
      else if (type == EXPR_TERMINATOR) {
        return new C3ExprTerminatorImpl(node);
      }
      else if (type == FAULT_DECLARATION) {
        return new C3FaultDeclarationImpl(node);
      }
      else if (type == FLAT_PATH) {
        return new C3FlatPathImpl(node);
      }
      else if (type == FLOAT_TYPE) {
        return new C3FloatTypeImpl(node);
      }
      else if (type == FN_PARAMETER_LIST) {
        return new C3FnParameterListImpl(node);
      }
      else if (type == FOREACH_STMT) {
        return new C3ForeachStmtImpl(node);
      }
      else if (type == FOREACH_VAR) {
        return new C3ForeachVarImpl(node);
      }
      else if (type == FOREACH_VARS) {
        return new C3ForeachVarsImpl(node);
      }
      else if (type == FOR_COND) {
        return new C3ForCondImpl(node);
      }
      else if (type == FOR_STMT) {
        return new C3ForStmtImpl(node);
      }
      else if (type == FUNC_DEF) {
        return new C3FuncDefImpl(node);
      }
      else if (type == FUNC_DEFINITION) {
        return new C3FuncDefinitionImpl(node);
      }
      else if (type == FUNC_HEADER) {
        return new C3FuncHeaderImpl(node);
      }
      else if (type == FUNC_NAME) {
        return new C3FuncNameImpl(node);
      }
      else if (type == FUNC_TYPEDEF) {
        return new C3FuncTypedefImpl(node);
      }
      else if (type == GENERIC_PARAMETER) {
        return new C3GenericParameterImpl(node);
      }
      else if (type == GENERIC_PARAMETERS) {
        return new C3GenericParametersImpl(node);
      }
      else if (type == GLOBAL_DECL) {
        return new C3GlobalDeclImpl(node);
      }
      else if (type == GLOBAL_MULTI_DECLARATION) {
        return new C3GlobalMultiDeclarationImpl(node);
      }
      else if (type == GLOBAL_SINGLE_DECLARATION) {
        return new C3GlobalSingleDeclarationImpl(node);
      }
      else if (type == GROUPED_EXPR) {
        return new C3GroupedExprImpl(node);
      }
      else if (type == GROUPED_EXPRESSION) {
        return new C3GroupedExpressionImpl(node);
      }
      else if (type == IDENTIFIER_LIST) {
        return new C3IdentifierListImpl(node);
      }
      else if (type == IF_STMT) {
        return new C3IfStmtImpl(node);
      }
      else if (type == IMPLIES_BODY) {
        return new C3ImpliesBodyImpl(node);
      }
      else if (type == IMPORT_DECL) {
        return new C3ImportDeclImpl(node);
      }
      else if (type == IMPORT_PATH) {
        return new C3ImportPathImpl(node);
      }
      else if (type == IMPORT_PATHS) {
        return new C3ImportPathsImpl(node);
      }
      else if (type == INITIALIZER_LIST) {
        return new C3InitializerListImpl(node);
      }
      else if (type == INIT_LIST_EXPR) {
        return new C3InitListExprImpl(node);
      }
      else if (type == INTEGER_TYPE) {
        return new C3IntegerTypeImpl(node);
      }
      else if (type == INTERFACE_BODY) {
        return new C3InterfaceBodyImpl(node);
      }
      else if (type == INTERFACE_DEFINITION) {
        return new C3InterfaceDefinitionImpl(node);
      }
      else if (type == INTERFACE_IMPL) {
        return new C3InterfaceImplImpl(node);
      }
      else if (type == KEYWORD_EXPR) {
        return new C3KeywordExprImpl(node);
      }
      else if (type == LABEL) {
        return new C3LabelImpl(node);
      }
      else if (type == LAMBDA_DECL) {
        return new C3LambdaDeclImpl(node);
      }
      else if (type == LAMBDA_DECL_EXPR) {
        return new C3LambdaDeclExprImpl(node);
      }
      else if (type == LAMBDA_DECL_SHORT_EXPR) {
        return new C3LambdaDeclShortExprImpl(node);
      }
      else if (type == LITERAL_EXPR) {
        return new C3LiteralExprImpl(node);
      }
      else if (type == LOCAL_DECLARATION_STMT) {
        return new C3LocalDeclarationStmtImpl(node);
      }
      else if (type == LOCAL_DECL_AFTER_TYPE) {
        return new C3LocalDeclAfterTypeImpl(node);
      }
      else if (type == LOCAL_DECL_AFTER_TYPE_1) {
        return new C3LocalDeclAfterType1Impl(node);
      }
      else if (type == LOCAL_DECL_AFTER_TYPE_2) {
        return new C3LocalDeclAfterType2Impl(node);
      }
      else if (type == LOCAL_DECL_STORAGE) {
        return new C3LocalDeclStorageImpl(node);
      }
      else if (type == LOCAL_IDENT_EXPR) {
        return new C3LocalIdentExprImpl(node);
      }
      else if (type == MACRO_DEFINITION) {
        return new C3MacroDefinitionImpl(node);
      }
      else if (type == MACRO_FUNC_BODY) {
        return new C3MacroFuncBodyImpl(node);
      }
      else if (type == MACRO_HEADER) {
        return new C3MacroHeaderImpl(node);
      }
      else if (type == MACRO_NAME) {
        return new C3MacroNameImpl(node);
      }
      else if (type == MACRO_PARAMS) {
        return new C3MacroParamsImpl(node);
      }
      else if (type == MODULE) {
        return new C3ModuleImpl(node);
      }
      else if (type == MODULE_PARAM) {
        return new C3ModuleParamImpl(node);
      }
      else if (type == MODULE_PARAMS) {
        return new C3ModuleParamsImpl(node);
      }
      else if (type == MODULE_PATH) {
        return new C3ModulePathImpl(node);
      }
      else if (type == MODULE_SECTION) {
        return new C3ModuleSectionImpl(node);
      }
      else if (type == MULTI_DECLARATION) {
        return new C3MultiDeclarationImpl(node);
      }
      else if (type == NAMED_IDENT) {
        return new C3NamedIdentImpl(node);
      }
      else if (type == NEXTCASE_STMT) {
        return new C3NextcaseStmtImpl(node);
      }
      else if (type == OPTIONAL_EXPR) {
        return new C3OptionalExprImpl(node);
      }
      else if (type == OPTIONAL_TYPE) {
        return new C3OptionalTypeImpl(node);
      }
      else if (type == PARAMETER) {
        return new C3ParameterImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new C3ParameterListImpl(node);
      }
      else if (type == PARAM_DECL) {
        return new C3ParamDeclImpl(node);
      }
      else if (type == PARAM_PATH) {
        return new C3ParamPathImpl(node);
      }
      else if (type == PARAM_PATH_ELEMENT) {
        return new C3ParamPathElementImpl(node);
      }
      else if (type == PAREN_COND) {
        return new C3ParenCondImpl(node);
      }
      else if (type == PATH) {
        return new C3PathImpl(node);
      }
      else if (type == PATH_AT_IDENT) {
        return new C3PathAtIdentImpl(node);
      }
      else if (type == PATH_AT_IDENT_EXPR) {
        return new C3PathAtIdentExprImpl(node);
      }
      else if (type == PATH_CONST) {
        return new C3PathConstImpl(node);
      }
      else if (type == PATH_CONST_EXPR) {
        return new C3PathConstExprImpl(node);
      }
      else if (type == PATH_IDENT) {
        return new C3PathIdentImpl(node);
      }
      else if (type == PATH_IDENT_EXPR) {
        return new C3PathIdentExprImpl(node);
      }
      else if (type == RANGE_EXP) {
        return new C3RangeExpImpl(node);
      }
      else if (type == RANGE_LOC) {
        return new C3RangeLocImpl(node);
      }
      else if (type == RETURN_STMT) {
        return new C3ReturnStmtImpl(node);
      }
      else if (type == STATEMENT) {
        return new C3StatementImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new C3StatementListImpl(node);
      }
      else if (type == STRING_EXPR) {
        return new C3StringExprImpl(node);
      }
      else if (type == STRUCT_BODY) {
        return new C3StructBodyImpl(node);
      }
      else if (type == STRUCT_DECLARATION) {
        return new C3StructDeclarationImpl(node);
      }
      else if (type == STRUCT_MEMBER_DECLARATION) {
        return new C3StructMemberDeclarationImpl(node);
      }
      else if (type == SWITCH_BODY) {
        return new C3SwitchBodyImpl(node);
      }
      else if (type == SWITCH_STMT) {
        return new C3SwitchStmtImpl(node);
      }
      else if (type == TERNARY_EXPR) {
        return new C3TernaryExprImpl(node);
      }
      else if (type == TOP_LEVEL) {
        return new C3TopLevelImpl(node);
      }
      else if (type == TRAILING_BLOCK_PARAM) {
        return new C3TrailingBlockParamImpl(node);
      }
      else if (type == TRY_UNWRAP) {
        return new C3TryUnwrapImpl(node);
      }
      else if (type == TRY_UNWRAP_CHAIN) {
        return new C3TryUnwrapChainImpl(node);
      }
      else if (type == TYPE) {
        return new C3TypeImpl(node);
      }
      else if (type == TYPEDEF_TYPE) {
        return new C3TypedefTypeImpl(node);
      }
      else if (type == TYPE_ACCESS_EXPR) {
        return new C3TypeAccessExprImpl(node);
      }
      else if (type == TYPE_DECL) {
        return new C3TypeDeclImpl(node);
      }
      else if (type == TYPE_NAME) {
        return new C3TypeNameImpl(node);
      }
      else if (type == TYPE_SUFFIX) {
        return new C3TypeSuffixImpl(node);
      }
      else if (type == UNARY_EXPR) {
        return new C3UnaryExprImpl(node);
      }
      else if (type == UNARY_OP) {
        return new C3UnaryOpImpl(node);
      }
      else if (type == VAR_DECL) {
        return new C3VarDeclImpl(node);
      }
      else if (type == VAR_STMT) {
        return new C3VarStmtImpl(node);
      }
      else if (type == WHILE_STMT) {
        return new C3WhileStmtImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
