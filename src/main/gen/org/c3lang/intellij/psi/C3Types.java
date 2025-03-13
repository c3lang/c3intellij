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
  IElementType CONST_DECLARATION_STMT = C3StubElementTypeFactory.stubFactory("CONST_DECLARATION_STMT");
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
  IElementType ENUM_CONSTANT = C3StubElementTypeFactory.stubFactory("ENUM_CONSTANT");
  IElementType ENUM_DECLARATION = new C3ElementType("ENUM_DECLARATION");
  IElementType ENUM_LIST = new C3ElementType("ENUM_LIST");
  IElementType ENUM_PARAM_DECL = new C3ElementType("ENUM_PARAM_DECL");
  IElementType ENUM_PARAM_LIST = new C3ElementType("ENUM_PARAM_LIST");
  IElementType ENUM_SPEC = new C3ElementType("ENUM_SPEC");
  IElementType EXPR = new C3ElementType("EXPR");
  IElementType EXPRESSION_LIST = new C3ElementType("EXPRESSION_LIST");
  IElementType EXPR_STMT = new C3ElementType("EXPR_STMT");
  IElementType EXPR_TERMINATOR = new C3ElementType("EXPR_TERMINATOR");
  IElementType FAULT_DECLARATION = new C3ElementType("FAULT_DECLARATION");
  IElementType FAULT_DEFINITION = new C3ElementType("FAULT_DEFINITION");
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
  IElementType LOCAL_DECL_STORAGE = new C3ElementType("LOCAL_DECL_STORAGE");
  IElementType LOCAL_IDENT_EXPR = new C3ElementType("LOCAL_IDENT_EXPR");
  IElementType MACRO_DEFINITION = C3StubElementTypeFactory.stubFactory("MACRO_DEFINITION");
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
  IElementType STRUCT_MEMBER_DECLARATION = C3StubElementTypeFactory.stubFactory("STRUCT_MEMBER_DECLARATION");
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
  IElementType TYPE_NAME = C3StubElementTypeFactory.stubFactory("TYPE_NAME");
  IElementType TYPE_SUFFIX = new C3ElementType("TYPE_SUFFIX");
  IElementType UNARY_EXPR = new C3ElementType("UNARY_EXPR");
  IElementType UNARY_OP = new C3ElementType("UNARY_OP");
  IElementType VAR_DECL = new C3ElementType("VAR_DECL");
  IElementType VAR_STMT = new C3ElementType("VAR_STMT");
  IElementType WHILE_STMT = new C3ElementType("WHILE_STMT");

  IElementType AMP = new C3TokenType("AMP");
  IElementType AND = new C3TokenType("AND");
  IElementType AT_IDENT = new C3TokenType("AT_IDENT");
  IElementType AT_TYPE_IDENT = new C3TokenType("AT_TYPE_IDENT");
  IElementType BANG = new C3TokenType("BANG");
  IElementType BANGBANG = new C3TokenType("BANGBANG");
  IElementType BIT_AND_ASSIGN = new C3TokenType("BIT_AND_ASSIGN");
  IElementType BIT_NOT = new C3TokenType("BIT_NOT");
  IElementType BIT_OR = new C3TokenType("BIT_OR");
  IElementType BIT_OR_ASSIGN = new C3TokenType("BIT_OR_ASSIGN");
  IElementType BIT_XOR = new C3TokenType("BIT_XOR");
  IElementType BIT_XOR_ASSIGN = new C3TokenType("BIT_XOR_ASSIGN");
  IElementType BUILTIN = new C3TokenType("BUILTIN");
  IElementType BUILTIN_CONST = new C3TokenType("BUILTIN_CONST");
  IElementType BYTES = new C3TokenType("BYTES");
  IElementType CHAR_LIT = new C3TokenType("CHAR_LIT");
  IElementType COLON = new C3TokenType("COLON");
  IElementType COMMA = new C3TokenType("COMMA");
  IElementType CONST_IDENT = new C3TokenType("CONST_IDENT");
  IElementType CT_AND = new C3TokenType("CT_AND");
  IElementType CT_CONST_IDENT = new C3TokenType("CT_CONST_IDENT");
  IElementType CT_IDENT = new C3TokenType("CT_IDENT");
  IElementType CT_OR = new C3TokenType("CT_OR");
  IElementType CT_PLUS = new C3TokenType("CT_PLUS");
  IElementType CT_TYPE_IDENT = new C3TokenType("CT_TYPE_IDENT");
  IElementType DIV = new C3TokenType("DIV");
  IElementType DIV_ASSIGN = new C3TokenType("DIV_ASSIGN");
  IElementType DOT = new C3TokenType("DOT");
  IElementType DOTDOT = new C3TokenType("DOTDOT");
  IElementType ELLIPSIS = new C3TokenType("ELLIPSIS");
  IElementType ELVIS = new C3TokenType("ELVIS");
  IElementType EOS = new C3TokenType("EOS");
  IElementType EQ = new C3TokenType("EQ");
  IElementType EQ_OP = new C3TokenType("EQ_OP");
  IElementType FLOAT_LITERAL = new C3TokenType("FLOAT_LITERAL");
  IElementType GE_OP = new C3TokenType("GE_OP");
  IElementType GT_OP = new C3TokenType("GT_OP");
  IElementType HASH_IDENT = new C3TokenType("HASH_IDENT");
  IElementType IDENT = new C3TokenType("IDENT");
  IElementType IMPLIES = new C3TokenType("IMPLIES");
  IElementType INT_LITERAL = new C3TokenType("INT_LITERAL");
  IElementType KW_ALIAS = new C3TokenType("KW_ALIAS");
  IElementType KW_ANY = new C3TokenType("KW_ANY");
  IElementType KW_ANYFAULT = new C3TokenType("KW_ANYFAULT");
  IElementType KW_ASM = new C3TokenType("KW_ASM");
  IElementType KW_ASSERT = new C3TokenType("KW_ASSERT");
  IElementType KW_BFLOAT16 = new C3TokenType("KW_BFLOAT16");
  IElementType KW_BITSTRUCT = new C3TokenType("KW_BITSTRUCT");
  IElementType KW_BOOL = new C3TokenType("KW_BOOL");
  IElementType KW_BREAK = new C3TokenType("KW_BREAK");
  IElementType KW_CASE = new C3TokenType("KW_CASE");
  IElementType KW_CATCH = new C3TokenType("KW_CATCH");
  IElementType KW_CHAR = new C3TokenType("KW_CHAR");
  IElementType KW_CONST = new C3TokenType("KW_CONST");
  IElementType KW_CONTINUE = new C3TokenType("KW_CONTINUE");
  IElementType KW_CT_ALIGNOF = new C3TokenType("KW_CT_ALIGNOF");
  IElementType KW_CT_ASSERT = new C3TokenType("KW_CT_ASSERT");
  IElementType KW_CT_CASE = new C3TokenType("KW_CT_CASE");
  IElementType KW_CT_DEFAULT = new C3TokenType("KW_CT_DEFAULT");
  IElementType KW_CT_DEFINED = new C3TokenType("KW_CT_DEFINED");
  IElementType KW_CT_ECHO = new C3TokenType("KW_CT_ECHO");
  IElementType KW_CT_ELSE = new C3TokenType("KW_CT_ELSE");
  IElementType KW_CT_ENDFOR = new C3TokenType("KW_CT_ENDFOR");
  IElementType KW_CT_ENDFOREACH = new C3TokenType("KW_CT_ENDFOREACH");
  IElementType KW_CT_ENDIF = new C3TokenType("KW_CT_ENDIF");
  IElementType KW_CT_ENDSWITCH = new C3TokenType("KW_CT_ENDSWITCH");
  IElementType KW_CT_ERROR = new C3TokenType("KW_CT_ERROR");
  IElementType KW_CT_EVAL = new C3TokenType("KW_CT_EVAL");
  IElementType KW_CT_EVALTYPE = new C3TokenType("KW_CT_EVALTYPE");
  IElementType KW_CT_EXTNAMEOF = new C3TokenType("KW_CT_EXTNAMEOF");
  IElementType KW_CT_FEATURE = new C3TokenType("KW_CT_FEATURE");
  IElementType KW_CT_FOR = new C3TokenType("KW_CT_FOR");
  IElementType KW_CT_FOREACH = new C3TokenType("KW_CT_FOREACH");
  IElementType KW_CT_IF = new C3TokenType("KW_CT_IF");
  IElementType KW_CT_INCLUDE = new C3TokenType("KW_CT_INCLUDE");
  IElementType KW_CT_IS_CONST = new C3TokenType("KW_CT_IS_CONST");
  IElementType KW_CT_NAMEOF = new C3TokenType("KW_CT_NAMEOF");
  IElementType KW_CT_OFFSETOF = new C3TokenType("KW_CT_OFFSETOF");
  IElementType KW_CT_QNAMEOF = new C3TokenType("KW_CT_QNAMEOF");
  IElementType KW_CT_SIZEOF = new C3TokenType("KW_CT_SIZEOF");
  IElementType KW_CT_STRINGIFY = new C3TokenType("KW_CT_STRINGIFY");
  IElementType KW_CT_SWITCH = new C3TokenType("KW_CT_SWITCH");
  IElementType KW_CT_TYPEFROM = new C3TokenType("KW_CT_TYPEFROM");
  IElementType KW_CT_TYPEOF = new C3TokenType("KW_CT_TYPEOF");
  IElementType KW_CT_VAARG = new C3TokenType("KW_CT_VAARG");
  IElementType KW_CT_VACONST = new C3TokenType("KW_CT_VACONST");
  IElementType KW_CT_VACOUNT = new C3TokenType("KW_CT_VACOUNT");
  IElementType KW_CT_VAEXPR = new C3TokenType("KW_CT_VAEXPR");
  IElementType KW_CT_VAREF = new C3TokenType("KW_CT_VAREF");
  IElementType KW_CT_VASPLAT = new C3TokenType("KW_CT_VASPLAT");
  IElementType KW_CT_VATYPE = new C3TokenType("KW_CT_VATYPE");
  IElementType KW_DEFAULT = new C3TokenType("KW_DEFAULT");
  IElementType KW_DEFER = new C3TokenType("KW_DEFER");
  IElementType KW_DISTINCT = new C3TokenType("KW_DISTINCT");
  IElementType KW_DO = new C3TokenType("KW_DO");
  IElementType KW_DOUBLE = new C3TokenType("KW_DOUBLE");
  IElementType KW_ELSE = new C3TokenType("KW_ELSE");
  IElementType KW_ENUM = new C3TokenType("KW_ENUM");
  IElementType KW_EXTERN = new C3TokenType("KW_EXTERN");
  IElementType KW_FALSE = new C3TokenType("KW_FALSE");
  IElementType KW_FAULT = new C3TokenType("KW_FAULT");
  IElementType KW_FLOAT = new C3TokenType("KW_FLOAT");
  IElementType KW_FLOAT128 = new C3TokenType("KW_FLOAT128");
  IElementType KW_FLOAT16 = new C3TokenType("KW_FLOAT16");
  IElementType KW_FN = new C3TokenType("KW_FN");
  IElementType KW_FOR = new C3TokenType("KW_FOR");
  IElementType KW_FOREACH = new C3TokenType("KW_FOREACH");
  IElementType KW_FOREACH_R = new C3TokenType("KW_FOREACH_R");
  IElementType KW_ICHAR = new C3TokenType("KW_ICHAR");
  IElementType KW_IF = new C3TokenType("KW_IF");
  IElementType KW_IMPORT = new C3TokenType("KW_IMPORT");
  IElementType KW_INLINE = new C3TokenType("KW_INLINE");
  IElementType KW_INT = new C3TokenType("KW_INT");
  IElementType KW_INT128 = new C3TokenType("KW_INT128");
  IElementType KW_INTERFACE = new C3TokenType("KW_INTERFACE");
  IElementType KW_IPTR = new C3TokenType("KW_IPTR");
  IElementType KW_ISZ = new C3TokenType("KW_ISZ");
  IElementType KW_LONG = new C3TokenType("KW_LONG");
  IElementType KW_MACRO = new C3TokenType("KW_MACRO");
  IElementType KW_MODULE = new C3TokenType("KW_MODULE");
  IElementType KW_NEXTCASE = new C3TokenType("KW_NEXTCASE");
  IElementType KW_NULL = new C3TokenType("KW_NULL");
  IElementType KW_RETURN = new C3TokenType("KW_RETURN");
  IElementType KW_SHORT = new C3TokenType("KW_SHORT");
  IElementType KW_STATIC = new C3TokenType("KW_STATIC");
  IElementType KW_STRUCT = new C3TokenType("KW_STRUCT");
  IElementType KW_SWITCH = new C3TokenType("KW_SWITCH");
  IElementType KW_TLOCAL = new C3TokenType("KW_TLOCAL");
  IElementType KW_TRUE = new C3TokenType("KW_TRUE");
  IElementType KW_TRY = new C3TokenType("KW_TRY");
  IElementType KW_TYPEID = new C3TokenType("KW_TYPEID");
  IElementType KW_UINT = new C3TokenType("KW_UINT");
  IElementType KW_UINT128 = new C3TokenType("KW_UINT128");
  IElementType KW_ULONG = new C3TokenType("KW_ULONG");
  IElementType KW_UNION = new C3TokenType("KW_UNION");
  IElementType KW_UPTR = new C3TokenType("KW_UPTR");
  IElementType KW_USHORT = new C3TokenType("KW_USHORT");
  IElementType KW_USZ = new C3TokenType("KW_USZ");
  IElementType KW_VAR = new C3TokenType("KW_VAR");
  IElementType KW_VOID = new C3TokenType("KW_VOID");
  IElementType KW_WHILE = new C3TokenType("KW_WHILE");
  IElementType LB = new C3TokenType("LB");
  IElementType LBT = new C3TokenType("LBT");
  IElementType LE_OP = new C3TokenType("LE_OP");
  IElementType LP = new C3TokenType("LP");
  IElementType LT_OP = new C3TokenType("LT_OP");
  IElementType LVEC = new C3TokenType("LVEC");
  IElementType MINUS = new C3TokenType("MINUS");
  IElementType MINUSMINUS = new C3TokenType("MINUSMINUS");
  IElementType MINUS_ASSIGN = new C3TokenType("MINUS_ASSIGN");
  IElementType MOD = new C3TokenType("MOD");
  IElementType MOD_ASSIGN = new C3TokenType("MOD_ASSIGN");
  IElementType MULT_ASSIGN = new C3TokenType("MULT_ASSIGN");
  IElementType NE_OP = new C3TokenType("NE_OP");
  IElementType OPTELSE = new C3TokenType("OPTELSE");
  IElementType OR = new C3TokenType("OR");
  IElementType PLUS = new C3TokenType("PLUS");
  IElementType PLUSPLUS = new C3TokenType("PLUSPLUS");
  IElementType PLUS_ASSIGN = new C3TokenType("PLUS_ASSIGN");
  IElementType QUESTION = new C3TokenType("QUESTION");
  IElementType RB = new C3TokenType("RB");
  IElementType RBT = new C3TokenType("RBT");
  IElementType RP = new C3TokenType("RP");
  IElementType RVEC = new C3TokenType("RVEC");
  IElementType SCOPE = new C3TokenType("SCOPE");
  IElementType SHL = new C3TokenType("SHL");
  IElementType SHL_ASSIGN = new C3TokenType("SHL_ASSIGN");
  IElementType SHR = new C3TokenType("SHR");
  IElementType SHR_ASSIGN = new C3TokenType("SHR_ASSIGN");
  IElementType STAR = new C3TokenType("STAR");
  IElementType STRING_LIT = new C3TokenType("STRING_LIT");
  IElementType TYPE_IDENT = new C3TokenType("TYPE_IDENT");
  IElementType UNDERSCORE = new C3TokenType("UNDERSCORE");

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
      else if (type == EXPR_STMT) {
        return new C3ExprStmtImpl(node);
      }
      else if (type == EXPR_TERMINATOR) {
        return new C3ExprTerminatorImpl(node);
      }
      else if (type == FAULT_DECLARATION) {
        return new C3FaultDeclarationImpl(node);
      }
      else if (type == FAULT_DEFINITION) {
        return new C3FaultDefinitionImpl(node);
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
