// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.c3lang.intellij.psi.C3Types.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class C3Parser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return translation_unit(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ASM_EXPR, ASSIGN_TYPE_EXPR, ATTRIBUTE_OPERATOR_EXPR, BINARY_EXPR,
      BUILTIN_CONST_EXPR, BUILTIN_EXPR, BYTES_EXPR, CALL_EXPR,
      COMPOUND_INIT_EXPR, CONSTANT_EXPR, CT_ANALYZE_EXPR, CT_ARG_EXPR,
      CT_CALL_EXPR, CT_CHECKS_EXPR, DECL_OR_EXPR, EXPR,
      EXPR_BLOCK_EXPR, GROUPED_EXPR, INIT_LIST_EXPR, KEYWORD_EXPR,
      LAMBDA_DECL_EXPR, LAMBDA_DECL_SHORT_EXPR, LITERAL_EXPR, LOCAL_IDENT_EXPR,
      OPTIONAL_EXPR, PATH_AT_IDENT_EXPR, PATH_CONST_EXPR, PATH_IDENT_EXPR,
      STRING_EXPR, TERNARY_EXPR, TYPE_ACCESS_EXPR, UNARY_EXPR),
  };

  /* ********************************************************** */
  // IDENT | AT_IDENT | HASH_IDENT | KW_CT_EVAL '(' expr ')' | KW_TYPEID
  public static boolean access_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "access_ident")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACCESS_IDENT, "<access ident>");
    r = consumeToken(b, IDENT);
    if (!r) r = consumeToken(b, AT_IDENT);
    if (!r) r = consumeToken(b, HASH_IDENT);
    if (!r) r = access_ident_3(b, l + 1);
    if (!r) r = consumeToken(b, KW_TYPEID);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_CT_EVAL '(' expr ')'
  private static boolean access_ident_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "access_ident_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_EVAL);
    r = r && consumeToken(b, "(");
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PLUS | MINUS
  public static boolean add_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_bin_op")) return false;
    if (!nextTokenIs(b, "<operator>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (param_path EQ)? (expr | type) | KW_CT_VASPLAT LP range_exp? RP | ELLIPSIS expr
  public static boolean arg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARG, "<arg>");
    r = arg_0(b, l + 1);
    if (!r) r = arg_1(b, l + 1);
    if (!r) r = arg_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (param_path EQ)? (expr | type)
  private static boolean arg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arg_0_0(b, l + 1);
    r = r && arg_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (param_path EQ)?
  private static boolean arg_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0_0")) return false;
    arg_0_0_0(b, l + 1);
    return true;
  }

  // param_path EQ
  private static boolean arg_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = param_path(b, l + 1);
    r = r && consumeToken(b, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr | type
  private static boolean arg_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0_1")) return false;
    boolean r;
    r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  // KW_CT_VASPLAT LP range_exp? RP
  private static boolean arg_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_VASPLAT, LP);
    r = r && arg_1_2(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // range_exp?
  private static boolean arg_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1_2")) return false;
    range_exp(b, l + 1);
    return true;
  }

  // ELLIPSIS expr
  private static boolean arg_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELLIPSIS);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // arg (COMMA arg)*
  public static boolean arg_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARG_LIST, "<arg list>");
    r = arg(b, l + 1);
    r = r && arg_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA arg)*
  private static boolean arg_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arg_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arg_list_1", c)) break;
    }
    return true;
  }

  // COMMA arg
  private static boolean arg_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && arg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // asm_expr (add_bin_op asm_expr asm_addr_trailing?)?
  public static boolean asm_addr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_ADDR, "<asm addr>");
    r = asm_expr(b, l + 1);
    r = r && asm_addr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (add_bin_op asm_expr asm_addr_trailing?)?
  private static boolean asm_addr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_1")) return false;
    asm_addr_1_0(b, l + 1);
    return true;
  }

  // add_bin_op asm_expr asm_addr_trailing?
  private static boolean asm_addr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = add_bin_op(b, l + 1);
    r = r && asm_expr(b, l + 1);
    r = r && asm_addr_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // asm_addr_trailing?
  private static boolean asm_addr_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_1_0_2")) return false;
    asm_addr_trailing(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // STAR INT_LITERAL (add_bin_op INT_LITERAL)? | (shift_bin_op | add_bin_op) INT_LITERAL
  public static boolean asm_addr_trailing(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_ADDR_TRAILING, "<asm addr trailing>");
    r = asm_addr_trailing_0(b, l + 1);
    if (!r) r = asm_addr_trailing_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STAR INT_LITERAL (add_bin_op INT_LITERAL)?
  private static boolean asm_addr_trailing_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, STAR, INT_LITERAL);
    r = r && asm_addr_trailing_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (add_bin_op INT_LITERAL)?
  private static boolean asm_addr_trailing_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing_0_2")) return false;
    asm_addr_trailing_0_2_0(b, l + 1);
    return true;
  }

  // add_bin_op INT_LITERAL
  private static boolean asm_addr_trailing_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = add_bin_op(b, l + 1);
    r = r && consumeToken(b, INT_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // (shift_bin_op | add_bin_op) INT_LITERAL
  private static boolean asm_addr_trailing_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_addr_trailing_1_0(b, l + 1);
    r = r && consumeToken(b, INT_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // shift_bin_op | add_bin_op
  private static boolean asm_addr_trailing_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_addr_trailing_1_0")) return false;
    boolean r;
    r = shift_bin_op(b, l + 1);
    if (!r) r = add_bin_op(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // KW_ASM (LP expr RP | LB asm_stmt* RB)
  public static boolean asm_block_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt")) return false;
    if (!nextTokenIs(b, KW_ASM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_ASM);
    r = r && asm_block_stmt_1(b, l + 1);
    exit_section_(b, m, ASM_BLOCK_STMT, r);
    return r;
  }

  // LP expr RP | LB asm_stmt* RB
  private static boolean asm_block_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_block_stmt_1_0(b, l + 1);
    if (!r) r = asm_block_stmt_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LP expr RP
  private static boolean asm_block_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // LB asm_stmt* RB
  private static boolean asm_block_stmt_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && asm_block_stmt_1_1_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, null, r);
    return r;
  }

  // asm_stmt*
  private static boolean asm_block_stmt_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!asm_stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "asm_block_stmt_1_1_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // CT_IDENT | CT_CONST_IDENT | AMP? IDENT | CONST_IDENT
  //     | FLOAT_LITERAL | INT_LITERAL | grouped_expression | LBT asm_addr RBT
  public static boolean asm_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_EXPR, "<asm expr>");
    r = consumeToken(b, CT_IDENT);
    if (!r) r = consumeToken(b, CT_CONST_IDENT);
    if (!r) r = asm_expr_2(b, l + 1);
    if (!r) r = consumeToken(b, CONST_IDENT);
    if (!r) r = consumeToken(b, FLOAT_LITERAL);
    if (!r) r = consumeToken(b, INT_LITERAL);
    if (!r) r = grouped_expression(b, l + 1);
    if (!r) r = asm_expr_7(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AMP? IDENT
  private static boolean asm_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_expr_2_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // AMP?
  private static boolean asm_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_2_0")) return false;
    consumeToken(b, AMP);
    return true;
  }

  // LBT asm_addr RBT
  private static boolean asm_expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && asm_addr(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // asm_expr (COMMA asm_expr)*
  public static boolean asm_exprs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_exprs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_EXPRS, "<asm exprs>");
    r = asm_expr(b, l + 1);
    r = r && asm_exprs_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA asm_expr)*
  private static boolean asm_exprs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_exprs_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!asm_exprs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "asm_exprs_1", c)) break;
    }
    return true;
  }

  // COMMA asm_expr
  private static boolean asm_exprs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_exprs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && asm_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (KW_INT | IDENT) (DOT IDENT)?
  public static boolean asm_instr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_instr")) return false;
    if (!nextTokenIs(b, "<asm instr>", IDENT, KW_INT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_INSTR, "<asm instr>");
    r = asm_instr_0(b, l + 1);
    r = r && asm_instr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_INT | IDENT
  private static boolean asm_instr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_instr_0")) return false;
    boolean r;
    r = consumeToken(b, KW_INT);
    if (!r) r = consumeToken(b, IDENT);
    return r;
  }

  // (DOT IDENT)?
  private static boolean asm_instr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_instr_1")) return false;
    asm_instr_1_0(b, l + 1);
    return true;
  }

  // DOT IDENT
  private static boolean asm_instr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_instr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // asm_instr asm_exprs? EOS
  public static boolean asm_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_stmt")) return false;
    if (!nextTokenIs(b, "<asm stmt>", IDENT, KW_INT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_STMT, "<asm stmt>");
    r = asm_instr(b, l + 1);
    r = r && asm_stmt_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // asm_exprs?
  private static boolean asm_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_stmt_1")) return false;
    asm_exprs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_ASSERT LP (try_unwrap_chain | expr) (COMMA expr)? RP EOS
  public static boolean assert_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt")) return false;
    if (!nextTokenIs(b, KW_ASSERT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_ASSERT, LP);
    r = r && assert_stmt_2(b, l + 1);
    r = r && assert_stmt_3(b, l + 1);
    r = r && consumeTokens(b, 0, RP, EOS);
    exit_section_(b, m, ASSERT_STMT, r);
    return r;
  }

  // try_unwrap_chain | expr
  private static boolean assert_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt_2")) return false;
    boolean r;
    r = try_unwrap_chain(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    return r;
  }

  // (COMMA expr)?
  private static boolean assert_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt_3")) return false;
    assert_stmt_3_0(b, l + 1);
    return true;
  }

  // COMMA expr
  private static boolean assert_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EQ
  //     | MULT_ASSIGN
  //     | MOD_ASSIGN
  //     | DIV_ASSIGN
  //     | PLUS_ASSIGN
  //     | MINUS_ASSIGN
  //     | SHR_ASSIGN
  //     | SHL_ASSIGN
  //     | BIT_AND_ASSIGN
  //     | BIT_XOR_ASSIGN
  //     | BIT_OR_ASSIGN
  public static boolean assign_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, MULT_ASSIGN);
    if (!r) r = consumeToken(b, MOD_ASSIGN);
    if (!r) r = consumeToken(b, DIV_ASSIGN);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, SHR_ASSIGN);
    if (!r) r = consumeToken(b, SHL_ASSIGN);
    if (!r) r = consumeToken(b, BIT_AND_ASSIGN);
    if (!r) r = consumeToken(b, BIT_XOR_ASSIGN);
    if (!r) r = consumeToken(b, BIT_OR_ASSIGN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AT_IDENT EQ path_at_ident
  public static boolean attr_alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attr_alias")) return false;
    if (!nextTokenIs(b, AT_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT_IDENT, EQ);
    r = r && path_at_ident(b, l + 1);
    exit_section_(b, m, ATTR_ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // attribute_operator_expr | constant_expr
  public static boolean attr_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attr_param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTR_PARAM, "<attr param>");
    r = attribute_operator_expr(b, l + 1);
    if (!r) r = constant_expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // attribute_name (LP attribute_param_list RP)?
  public static boolean attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE, "<attribute>");
    r = attribute_name(b, l + 1);
    r = r && attribute_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LP attribute_param_list RP)?
  private static boolean attribute_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_1")) return false;
    attribute_1_0(b, l + 1);
    return true;
  }

  // LP attribute_param_list RP
  private static boolean attribute_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && attribute_param_list(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // AT_IDENT | path? AT_TYPE_IDENT
  public static boolean attribute_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_NAME, "<attribute name>");
    r = consumeToken(b, AT_IDENT);
    if (!r) r = attribute_name_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path? AT_TYPE_IDENT
  private static boolean attribute_name_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_name_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_name_1_0(b, l + 1);
    r = r && consumeToken(b, AT_TYPE_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // path?
  private static boolean attribute_name_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_name_1_0")) return false;
    path(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBT RBT EQ | AMP? LBT RBT
  public static boolean attribute_operator_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr")) return false;
    if (!nextTokenIs(b, "<attribute operator expr>", AMP, LBT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_OPERATOR_EXPR, "<attribute operator expr>");
    r = parseTokens(b, 0, LBT, RBT, EQ);
    if (!r) r = attribute_operator_expr_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AMP? LBT RBT
  private static boolean attribute_operator_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_operator_expr_1_0(b, l + 1);
    r = r && consumeTokens(b, 0, LBT, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // AMP?
  private static boolean attribute_operator_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_1_0")) return false;
    consumeToken(b, AMP);
    return true;
  }

  /* ********************************************************** */
  // attr_param (COMMA attr_param)*
  public static boolean attribute_param_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_param_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_PARAM_LIST, "<attribute param list>");
    r = attr_param(b, l + 1);
    r = r && attribute_param_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA attr_param)*
  private static boolean attribute_param_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_param_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!attribute_param_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attribute_param_list_1", c)) break;
    }
    return true;
  }

  // COMMA attr_param
  private static boolean attribute_param_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_param_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && attr_param(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // attribute+
  public static boolean attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTES, "<attributes>");
    r = attribute(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attributes", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_VOID
  //     | KW_BOOL
  //     | integer_type
  //     | float_type
  //     | KW_IPTR
  //     | KW_UPTR
  //     | KW_ISZ
  //     | KW_USZ
  //     | KW_ANYFAULT
  //     | KW_ANY
  //     | KW_TYPEID
  //     | path? TYPE_IDENT
  //     | CT_TYPE_IDENT
  //     | KW_CT_TYPEOF grouped_expression
  //     | KW_CT_TYPEFROM const_paren_expr
  //     | KW_CT_VATYPE const_paren_expr
  //     | KW_CT_EVALTYPE const_paren_expr
  public static boolean base_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BASE_TYPE, "<base type>");
    r = consumeToken(b, KW_VOID);
    if (!r) r = consumeToken(b, KW_BOOL);
    if (!r) r = integer_type(b, l + 1);
    if (!r) r = float_type(b, l + 1);
    if (!r) r = consumeToken(b, KW_IPTR);
    if (!r) r = consumeToken(b, KW_UPTR);
    if (!r) r = consumeToken(b, KW_ISZ);
    if (!r) r = consumeToken(b, KW_USZ);
    if (!r) r = consumeToken(b, KW_ANYFAULT);
    if (!r) r = consumeToken(b, KW_ANY);
    if (!r) r = consumeToken(b, KW_TYPEID);
    if (!r) r = base_type_11(b, l + 1);
    if (!r) r = consumeToken(b, CT_TYPE_IDENT);
    if (!r) r = base_type_13(b, l + 1);
    if (!r) r = base_type_14(b, l + 1);
    if (!r) r = base_type_15(b, l + 1);
    if (!r) r = base_type_16(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path? TYPE_IDENT
  private static boolean base_type_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = base_type_11_0(b, l + 1);
    r = r && consumeToken(b, TYPE_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // path?
  private static boolean base_type_11_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_11_0")) return false;
    path(b, l + 1);
    return true;
  }

  // KW_CT_TYPEOF grouped_expression
  private static boolean base_type_13(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_13")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_TYPEOF);
    r = r && grouped_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CT_TYPEFROM const_paren_expr
  private static boolean base_type_14(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_14")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_TYPEFROM);
    r = r && const_paren_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CT_VATYPE const_paren_expr
  private static boolean base_type_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_15")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_VATYPE);
    r = r && const_paren_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CT_EVALTYPE const_paren_expr
  private static boolean base_type_16(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_16")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_EVALTYPE);
    r = r && const_paren_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // AMP | BIT_XOR | BIT_OR
  public static boolean bit_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bit_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, AMP);
    if (!r) r = consumeToken(b, BIT_XOR);
    if (!r) r = consumeToken(b, BIT_OR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LB (bitstruct_def+ | bitstruct_simple_def+)? RB
  public static boolean bitstruct_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_body")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && bitstruct_body_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, BITSTRUCT_BODY, r);
    return r;
  }

  // (bitstruct_def+ | bitstruct_simple_def+)?
  private static boolean bitstruct_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_body_1")) return false;
    bitstruct_body_1_0(b, l + 1);
    return true;
  }

  // bitstruct_def+ | bitstruct_simple_def+
  private static boolean bitstruct_body_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_body_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bitstruct_body_1_0_0(b, l + 1);
    if (!r) r = bitstruct_body_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // bitstruct_def+
  private static boolean bitstruct_body_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_body_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bitstruct_def(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!bitstruct_def(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bitstruct_body_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // bitstruct_simple_def+
  private static boolean bitstruct_body_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_body_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bitstruct_simple_def(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!bitstruct_simple_def(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bitstruct_body_1_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_BITSTRUCT type_name COLON type attributes? bitstruct_body
  public static boolean bitstruct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_declaration")) return false;
    if (!nextTokenIs(b, KW_BITSTRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_BITSTRUCT);
    r = r && type_name(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && type(b, l + 1);
    r = r && bitstruct_declaration_4(b, l + 1);
    r = r && bitstruct_body(b, l + 1);
    exit_section_(b, m, BITSTRUCT_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean bitstruct_declaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_declaration_4")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // base_type IDENT COLON constant_expr (DOTDOT constant_expr)? EOS
  public static boolean bitstruct_def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_def")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BITSTRUCT_DEF, "<bitstruct def>");
    r = base_type(b, l + 1);
    r = r && consumeTokens(b, 0, IDENT, COLON);
    r = r && constant_expr(b, l + 1);
    r = r && bitstruct_def_4(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOTDOT constant_expr)?
  private static boolean bitstruct_def_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_def_4")) return false;
    bitstruct_def_4_0(b, l + 1);
    return true;
  }

  // DOTDOT constant_expr
  private static boolean bitstruct_def_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_def_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOTDOT);
    r = r && constant_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // base_type IDENT EOS
  public static boolean bitstruct_simple_def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_simple_def")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BITSTRUCT_SIMPLE_DEF, "<bitstruct simple def>");
    r = base_type(b, l + 1);
    r = r && consumeTokens(b, 0, IDENT, EOS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_BREAK CONST_IDENT? EOS
  public static boolean break_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_stmt")) return false;
    if (!nextTokenIs(b, KW_BREAK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_BREAK);
    r = r && break_stmt_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, BREAK_STMT, r);
    return r;
  }

  // CONST_IDENT?
  private static boolean break_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_stmt_1")) return false;
    consumeToken(b, CONST_IDENT);
    return true;
  }

  /* ********************************************************** */
  // arg_list? (EOS parameter_list?)?
  public static boolean call_arg_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_arg_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CALL_ARG_LIST, "<call arg list>");
    r = call_arg_list_0(b, l + 1);
    r = r && call_arg_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // arg_list?
  private static boolean call_arg_list_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_arg_list_0")) return false;
    arg_list(b, l + 1);
    return true;
  }

  // (EOS parameter_list?)?
  private static boolean call_arg_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_arg_list_1")) return false;
    call_arg_list_1_0(b, l + 1);
    return true;
  }

  // EOS parameter_list?
  private static boolean call_arg_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_arg_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOS);
    r = r && call_arg_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter_list?
  private static boolean call_arg_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_arg_list_1_0_1")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // call_invocation compound_statement?
  //     | LBT (range_exp | range_loc) RBT
  //     | DOT access_ident
  //     | PLUSPLUS
  //     | MINUSMINUS
  //     | BANG
  //     | BANGBANG
  public static boolean call_expr_tail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CALL_EXPR_TAIL, "<call expr tail>");
    r = call_expr_tail_0(b, l + 1);
    if (!r) r = call_expr_tail_1(b, l + 1);
    if (!r) r = call_expr_tail_2(b, l + 1);
    if (!r) r = consumeToken(b, PLUSPLUS);
    if (!r) r = consumeToken(b, MINUSMINUS);
    if (!r) r = consumeToken(b, BANG);
    if (!r) r = consumeToken(b, BANGBANG);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // call_invocation compound_statement?
  private static boolean call_expr_tail_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = call_invocation(b, l + 1);
    r = r && call_expr_tail_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // compound_statement?
  private static boolean call_expr_tail_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail_0_1")) return false;
    compound_statement(b, l + 1);
    return true;
  }

  // LBT (range_exp | range_loc) RBT
  private static boolean call_expr_tail_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && call_expr_tail_1_1(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // range_exp | range_loc
  private static boolean call_expr_tail_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail_1_1")) return false;
    boolean r;
    r = range_exp(b, l + 1);
    if (!r) r = range_loc(b, l + 1);
    return r;
  }

  // DOT access_ident
  private static boolean call_expr_tail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_expr_tail_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && access_ident(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LP call_arg_list RP AT_IDENT*
  public static boolean call_invocation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_invocation")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && call_arg_list(b, l + 1);
    r = r && consumeToken(b, RP);
    r = r && call_invocation_3(b, l + 1);
    exit_section_(b, m, CALL_INVOCATION, r);
    return r;
  }

  // AT_IDENT*
  private static boolean call_invocation_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_invocation_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, AT_IDENT)) break;
      if (!empty_element_parsed_guard_(b, "call_invocation_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_CASE (expr (DOTDOT expr)? | type) COLON statement_list?
  public static boolean case_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt")) return false;
    if (!nextTokenIs(b, KW_CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CASE);
    r = r && case_stmt_1(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && case_stmt_3(b, l + 1);
    exit_section_(b, m, CASE_STMT, r);
    return r;
  }

  // expr (DOTDOT expr)? | type
  private static boolean case_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = case_stmt_1_0(b, l + 1);
    if (!r) r = type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr (DOTDOT expr)?
  private static boolean case_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1, -1);
    r = r && case_stmt_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOTDOT expr)?
  private static boolean case_stmt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt_1_0_1")) return false;
    case_stmt_1_0_1_0(b, l + 1);
    return true;
  }

  // DOTDOT expr
  private static boolean case_stmt_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOTDOT);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement_list?
  private static boolean case_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_stmt_3")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CATCH (type? IDENT EQ)? catch_unwrap_list
  public static boolean catch_unwrap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap")) return false;
    if (!nextTokenIs(b, KW_CATCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CATCH);
    r = r && catch_unwrap_1(b, l + 1);
    r = r && catch_unwrap_list(b, l + 1);
    exit_section_(b, m, CATCH_UNWRAP, r);
    return r;
  }

  // (type? IDENT EQ)?
  private static boolean catch_unwrap_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_1")) return false;
    catch_unwrap_1_0(b, l + 1);
    return true;
  }

  // type? IDENT EQ
  private static boolean catch_unwrap_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = catch_unwrap_1_0_0(b, l + 1);
    r = r && consumeTokens(b, 0, IDENT, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // type?
  private static boolean catch_unwrap_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_1_0_0")) return false;
    type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expr (COMMA expr)*
  public static boolean catch_unwrap_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CATCH_UNWRAP_LIST, "<catch unwrap list>");
    r = expr(b, l + 1, -1);
    r = r && catch_unwrap_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA expr)*
  private static boolean catch_unwrap_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!catch_unwrap_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "catch_unwrap_list_1", c)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean catch_unwrap_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "catch_unwrap_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LB statement_list? RB
  public static boolean compound_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compound_statement")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && compound_statement_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, COMPOUND_STATEMENT, r);
    return r;
  }

  // statement_list?
  private static boolean compound_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compound_statement_1")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // cond_repeat (COMMA (try_unwrap_chain | catch_unwrap))? | try_unwrap_chain | catch_unwrap
  public static boolean cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COND, "<cond>");
    r = cond_0(b, l + 1);
    if (!r) r = try_unwrap_chain(b, l + 1);
    if (!r) r = catch_unwrap(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // cond_repeat (COMMA (try_unwrap_chain | catch_unwrap))?
  private static boolean cond_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = cond_repeat(b, l + 1);
    r = r && cond_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA (try_unwrap_chain | catch_unwrap))?
  private static boolean cond_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_0_1")) return false;
    cond_0_1_0(b, l + 1);
    return true;
  }

  // COMMA (try_unwrap_chain | catch_unwrap)
  private static boolean cond_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && cond_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // try_unwrap_chain | catch_unwrap
  private static boolean cond_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_0_1_0_1")) return false;
    boolean r;
    r = try_unwrap_chain(b, l + 1);
    if (!r) r = catch_unwrap(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // decl_or_expr (COMMA decl_or_expr)*
  public static boolean cond_repeat(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_repeat")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COND_REPEAT, "<cond repeat>");
    r = decl_or_expr(b, l + 1);
    r = r && cond_repeat_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA decl_or_expr)*
  private static boolean cond_repeat_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_repeat_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!cond_repeat_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "cond_repeat_1", c)) break;
    }
    return true;
  }

  // COMMA decl_or_expr
  private static boolean cond_repeat_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_repeat_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && decl_or_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONST_IDENT EQ path_const
  public static boolean const_alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_alias")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CONST_IDENT, EQ);
    r = r && path_const(b, l + 1);
    exit_section_(b, m, CONST_ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CONST type? CONST_IDENT attributes? EQ expr EOS
  public static boolean const_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration")) return false;
    if (!nextTokenIs(b, KW_CONST)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CONST);
    r = r && const_declaration_1(b, l + 1);
    r = r && consumeToken(b, CONST_IDENT);
    r = r && const_declaration_3(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, CONST_DECLARATION, r);
    return r;
  }

  // type?
  private static boolean const_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration_1")) return false;
    type(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean const_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LP constant_expr RP
  static boolean const_paren_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_paren_expr")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && constant_expr(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr
  public static boolean constant_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, CONSTANT_EXPR, "<constant expr>");
    r = expr(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_CONTINUE CONST_IDENT? EOS
  public static boolean continue_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "continue_stmt")) return false;
    if (!nextTokenIs(b, KW_CONTINUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CONTINUE);
    r = r && continue_stmt_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, CONTINUE_STMT, r);
    return r;
  }

  // CONST_IDENT?
  private static boolean continue_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "continue_stmt_1")) return false;
    consumeToken(b, CONST_IDENT);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_EVAL | KW_CT_SIZEOF | KW_CT_STRINGIFY
  public static boolean ct_analyze(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_analyze")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ANALYZE, "<ct analyze>");
    r = consumeToken(b, KW_CT_EVAL);
    if (!r) r = consumeToken(b, KW_CT_SIZEOF);
    if (!r) r = consumeToken(b, KW_CT_STRINGIFY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_VACONST | KW_CT_VAARG | KW_CT_VAREF | KW_CT_VAEXPR
  public static boolean ct_arg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_arg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ARG, "<ct arg>");
    r = consumeToken(b, KW_CT_VACONST);
    if (!r) r = consumeToken(b, KW_CT_VAARG);
    if (!r) r = consumeToken(b, KW_CT_VAREF);
    if (!r) r = consumeToken(b, KW_CT_VAEXPR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_ASSERT LP constant_expr (COMMA constant_expr)? RP EOS
  public static boolean ct_assert_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_ASSERT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_ASSERT, LP);
    r = r && constant_expr(b, l + 1);
    r = r && ct_assert_stmt_3(b, l + 1);
    r = r && consumeTokens(b, 0, RP, EOS);
    exit_section_(b, m, CT_ASSERT_STMT, r);
    return r;
  }

  // (COMMA constant_expr)?
  private static boolean ct_assert_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt_3")) return false;
    ct_assert_stmt_3_0(b, l + 1);
    return true;
  }

  // COMMA constant_expr
  private static boolean ct_assert_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && constant_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_ALIGNOF | KW_CT_DEFINED | KW_CT_EXTNAMEOF | KW_CT_NAMEOF | KW_CT_OFFSETOF | KW_CT_QNAMEOF
  public static boolean ct_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_CALL, "<ct call>");
    r = consumeToken(b, KW_CT_ALIGNOF);
    if (!r) r = consumeToken(b, KW_CT_DEFINED);
    if (!r) r = consumeToken(b, KW_CT_EXTNAMEOF);
    if (!r) r = consumeToken(b, KW_CT_NAMEOF);
    if (!r) r = consumeToken(b, KW_CT_OFFSETOF);
    if (!r) r = consumeToken(b, KW_CT_QNAMEOF);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (KW_CT_CASE (constant_expr | type) | KW_CT_DEFAULT) COLON statement_list?
  public static boolean ct_case_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_case_stmt")) return false;
    if (!nextTokenIs(b, "<ct case stmt>", KW_CT_CASE, KW_CT_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_CASE_STMT, "<ct case stmt>");
    r = ct_case_stmt_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && ct_case_stmt_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_CT_CASE (constant_expr | type) | KW_CT_DEFAULT
  private static boolean ct_case_stmt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_case_stmt_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ct_case_stmt_0_0(b, l + 1);
    if (!r) r = consumeToken(b, KW_CT_DEFAULT);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CT_CASE (constant_expr | type)
  private static boolean ct_case_stmt_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_case_stmt_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_CASE);
    r = r && ct_case_stmt_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // constant_expr | type
  private static boolean ct_case_stmt_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_case_stmt_0_0_1")) return false;
    boolean r;
    r = constant_expr(b, l + 1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  // statement_list?
  private static boolean ct_case_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_case_stmt_2")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_ECHO const_paren_expr EOS
  public static boolean ct_echo_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_echo_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_ECHO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_ECHO);
    r = r && const_paren_expr(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, CT_ECHO_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_FOR LP for_cond RP statement_list? KW_CT_ENDFOR
  public static boolean ct_for_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_for_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_FOR, LP);
    r = r && for_cond(b, l + 1);
    r = r && consumeToken(b, RP);
    r = r && ct_for_stmt_4(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDFOR);
    exit_section_(b, m, CT_FOR_STMT, r);
    return r;
  }

  // statement_list?
  private static boolean ct_for_stmt_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_for_stmt_4")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_FOREACH LP CT_IDENT (COMMA CT_IDENT)? COLON expr RP statement_list? KW_CT_ENDFOREACH
  public static boolean ct_foreach_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_FOREACH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_FOREACH, LP, CT_IDENT);
    r = r && ct_foreach_stmt_3(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RP);
    r = r && ct_foreach_stmt_7(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDFOREACH);
    exit_section_(b, m, CT_FOREACH_STMT, r);
    return r;
  }

  // (COMMA CT_IDENT)?
  private static boolean ct_foreach_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_3")) return false;
    ct_foreach_stmt_3_0(b, l + 1);
    return true;
  }

  // COMMA CT_IDENT
  private static boolean ct_foreach_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, CT_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement_list?
  private static boolean ct_foreach_stmt_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_7")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_IF const_paren_expr statement_list? (KW_CT_ELSE statement_list?)? KW_CT_ENDIF
  public static boolean ct_if_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_IF);
    r = r && const_paren_expr(b, l + 1);
    r = r && ct_if_stmt_2(b, l + 1);
    r = r && ct_if_stmt_3(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDIF);
    exit_section_(b, m, CT_IF_STMT, r);
    return r;
  }

  // statement_list?
  private static boolean ct_if_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_2")) return false;
    statement_list(b, l + 1);
    return true;
  }

  // (KW_CT_ELSE statement_list?)?
  private static boolean ct_if_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_3")) return false;
    ct_if_stmt_3_0(b, l + 1);
    return true;
  }

  // KW_CT_ELSE statement_list?
  private static boolean ct_if_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_ELSE);
    r = r && ct_if_stmt_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement_list?
  private static boolean ct_if_stmt_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_3_0_1")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_INCLUDE LP string_expr RP EOS
  public static boolean ct_include_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_include_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_INCLUDE, LP);
    r = r && string_expr(b, l + 1);
    r = r && consumeTokens(b, 0, RP, EOS);
    exit_section_(b, m, CT_INCLUDE_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_SWITCH (LP (constant_expr | type) RP)?
  public static boolean ct_switch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch")) return false;
    if (!nextTokenIs(b, KW_CT_SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_SWITCH);
    r = r && ct_switch_1(b, l + 1);
    exit_section_(b, m, CT_SWITCH, r);
    return r;
  }

  // (LP (constant_expr | type) RP)?
  private static boolean ct_switch_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_1")) return false;
    ct_switch_1_0(b, l + 1);
    return true;
  }

  // LP (constant_expr | type) RP
  private static boolean ct_switch_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && ct_switch_1_0_1(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // constant_expr | type
  private static boolean ct_switch_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_1_0_1")) return false;
    boolean r;
    r = constant_expr(b, l + 1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ct_case_stmt+
  public static boolean ct_switch_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_body")) return false;
    if (!nextTokenIs(b, "<ct switch body>", KW_CT_CASE, KW_CT_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_SWITCH_BODY, "<ct switch body>");
    r = ct_case_stmt(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!ct_case_stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ct_switch_body", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ct_switch ct_switch_body KW_CT_ENDSWITCH
  public static boolean ct_switch_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ct_switch(b, l + 1);
    r = r && ct_switch_body(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDSWITCH);
    exit_section_(b, m, CT_SWITCH_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // var_decl | optional_type local_decl_after_type | expr
  public static boolean decl_or_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_or_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, DECL_OR_EXPR, "<decl or expr>");
    r = var_decl(b, l + 1);
    if (!r) r = decl_or_expr_1(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // optional_type local_decl_after_type
  private static boolean decl_or_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_or_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = optional_type(b, l + 1);
    r = r && local_decl_after_type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // local_decl_after_type (COMMA local_decl_after_type)*
  public static boolean decl_stmt_after_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_stmt_after_type")) return false;
    if (!nextTokenIs(b, "<decl stmt after type>", CT_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECL_STMT_AFTER_TYPE, "<decl stmt after type>");
    r = local_decl_after_type(b, l + 1);
    r = r && decl_stmt_after_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA local_decl_after_type)*
  private static boolean decl_stmt_after_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_stmt_after_type_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!decl_stmt_after_type_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "decl_stmt_after_type_1", c)) break;
    }
    return true;
  }

  // COMMA local_decl_after_type
  private static boolean decl_stmt_after_type_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "decl_stmt_after_type_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && local_decl_after_type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // const_declaration | local_decl_storage? optional_type decl_stmt_after_type EOS
  public static boolean declaration_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_stmt")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECLARATION_STMT, "<declaration stmt>");
    r = const_declaration(b, l + 1);
    if (!r) r = declaration_stmt_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // local_decl_storage? optional_type decl_stmt_after_type EOS
  private static boolean declaration_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_stmt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = declaration_stmt_1_0(b, l + 1);
    r = r && optional_type(b, l + 1);
    r = r && decl_stmt_after_type(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  // local_decl_storage?
  private static boolean declaration_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_stmt_1_0")) return false;
    local_decl_storage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // top_level+
  public static boolean default_module_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "default_module_section")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEFAULT_MODULE_SECTION, "<default module section>");
    r = top_level(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!top_level(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "default_module_section", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_DEFAULT COLON statement_list?
  public static boolean default_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "default_stmt")) return false;
    if (!nextTokenIs(b, KW_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_DEFAULT, COLON);
    r = r && default_stmt_2(b, l + 1);
    exit_section_(b, m, DEFAULT_STMT, r);
    return r;
  }

  // statement_list?
  private static boolean default_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "default_stmt_2")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_DEFER (KW_TRY | KW_CATCH)? statement
  public static boolean defer_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt")) return false;
    if (!nextTokenIs(b, KW_DEFER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_DEFER);
    r = r && defer_stmt_1(b, l + 1);
    r = r && statement(b, l + 1);
    exit_section_(b, m, DEFER_STMT, r);
    return r;
  }

  // (KW_TRY | KW_CATCH)?
  private static boolean defer_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt_1")) return false;
    defer_stmt_1_0(b, l + 1);
    return true;
  }

  // KW_TRY | KW_CATCH
  private static boolean defer_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt_1_0")) return false;
    boolean r;
    r = consumeToken(b, KW_TRY);
    if (!r) r = consumeToken(b, KW_CATCH);
    return r;
  }

  /* ********************************************************** */
  // AT_TYPE_IDENT (LP parameter_list RP)? attributes? EQ LB attributes? RB
  public static boolean define_attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_attribute")) return false;
    if (!nextTokenIs(b, AT_TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_TYPE_IDENT);
    r = r && define_attribute_1(b, l + 1);
    r = r && define_attribute_2(b, l + 1);
    r = r && consumeTokens(b, 0, EQ, LB);
    r = r && define_attribute_5(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, DEFINE_ATTRIBUTE, r);
    return r;
  }

  // (LP parameter_list RP)?
  private static boolean define_attribute_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_attribute_1")) return false;
    define_attribute_1_0(b, l + 1);
    return true;
  }

  // LP parameter_list RP
  private static boolean define_attribute_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_attribute_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && parameter_list(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean define_attribute_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_attribute_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean define_attribute_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_attribute_5")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_DEFINE (define_ident | define_attribute) EOS
  public static boolean define_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_declaration")) return false;
    if (!nextTokenIs(b, KW_DEFINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_DEFINE);
    r = r && define_declaration_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, DEFINE_DECLARATION, r);
    return r;
  }

  // define_ident | define_attribute
  private static boolean define_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_declaration_1")) return false;
    boolean r;
    r = define_ident(b, l + 1);
    if (!r) r = define_attribute(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (ident_alias | const_alias | attr_alias) generic_parameters?
  public static boolean define_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_ident")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEFINE_IDENT, "<define ident>");
    r = define_ident_0(b, l + 1);
    r = r && define_ident_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ident_alias | const_alias | attr_alias
  private static boolean define_ident_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_ident_0")) return false;
    boolean r;
    r = ident_alias(b, l + 1);
    if (!r) r = const_alias(b, l + 1);
    if (!r) r = attr_alias(b, l + 1);
    return r;
  }

  // generic_parameters?
  private static boolean define_ident_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "define_ident_1")) return false;
    generic_parameters(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_DISTINCT KW_INLINE? | KW_INLINE KW_DISTINCT?
  public static boolean distinct_inline(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "distinct_inline")) return false;
    if (!nextTokenIs(b, "<distinct inline>", KW_DISTINCT, KW_INLINE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DISTINCT_INLINE, "<distinct inline>");
    r = distinct_inline_0(b, l + 1);
    if (!r) r = distinct_inline_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_DISTINCT KW_INLINE?
  private static boolean distinct_inline_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "distinct_inline_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_DISTINCT);
    r = r && distinct_inline_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_INLINE?
  private static boolean distinct_inline_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "distinct_inline_0_1")) return false;
    consumeToken(b, KW_INLINE);
    return true;
  }

  // KW_INLINE KW_DISTINCT?
  private static boolean distinct_inline_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "distinct_inline_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_INLINE);
    r = r && distinct_inline_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_DISTINCT?
  private static boolean distinct_inline_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "distinct_inline_1_1")) return false;
    consumeToken(b, KW_DISTINCT);
    return true;
  }

  /* ********************************************************** */
  // KW_DO label? compound_statement (KW_WHILE grouped_expression)? EOS
  public static boolean do_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_stmt")) return false;
    if (!nextTokenIs(b, KW_DO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_DO);
    r = r && do_stmt_1(b, l + 1);
    r = r && compound_statement(b, l + 1);
    r = r && do_stmt_3(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, DO_STMT, r);
    return r;
  }

  // label?
  private static boolean do_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  // (KW_WHILE grouped_expression)?
  private static boolean do_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_stmt_3")) return false;
    do_stmt_3_0(b, l + 1);
    return true;
  }

  // KW_WHILE grouped_expression
  private static boolean do_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_WHILE);
    r = r && grouped_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_ELSE (if_stmt | compound_statement)
  public static boolean else_part(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_part")) return false;
    if (!nextTokenIs(b, KW_ELSE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_ELSE);
    r = r && else_part_1(b, l + 1);
    exit_section_(b, m, ELSE_PART, r);
    return r;
  }

  // if_stmt | compound_statement
  private static boolean else_part_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_part_1")) return false;
    boolean r;
    r = if_stmt(b, l + 1);
    if (!r) r = compound_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CONST_IDENT (LP arg_list COMMA? RP)?
  public static boolean enum_constant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST_IDENT);
    r = r && enum_constant_1(b, l + 1);
    exit_section_(b, m, ENUM_CONSTANT, r);
    return r;
  }

  // (LP arg_list COMMA? RP)?
  private static boolean enum_constant_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant_1")) return false;
    enum_constant_1_0(b, l + 1);
    return true;
  }

  // LP arg_list COMMA? RP
  private static boolean enum_constant_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && arg_list(b, l + 1);
    r = r && enum_constant_1_0_2(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean enum_constant_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant_1_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // KW_ENUM type_name (COLON enum_spec)? attributes? LB enum_list RB
  public static boolean enum_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration")) return false;
    if (!nextTokenIs(b, KW_ENUM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_ENUM);
    r = r && type_name(b, l + 1);
    r = r && enum_declaration_2(b, l + 1);
    r = r && enum_declaration_3(b, l + 1);
    r = r && consumeToken(b, LB);
    r = r && enum_list(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, ENUM_DECLARATION, r);
    return r;
  }

  // (COLON enum_spec)?
  private static boolean enum_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_2")) return false;
    enum_declaration_2_0(b, l + 1);
    return true;
  }

  // COLON enum_spec
  private static boolean enum_declaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && enum_spec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean enum_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // enum_constant (COMMA enum_constant)* COMMA?
  public static boolean enum_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enum_constant(b, l + 1);
    r = r && enum_list_1(b, l + 1);
    r = r && enum_list_2(b, l + 1);
    exit_section_(b, m, ENUM_LIST, r);
    return r;
  }

  // (COMMA enum_constant)*
  private static boolean enum_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enum_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enum_list_1", c)) break;
    }
    return true;
  }

  // COMMA enum_constant
  private static boolean enum_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enum_constant(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean enum_list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // type (IDENT (EQ expr)?)?
  public static boolean enum_param_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_PARAM_DECL, "<enum param decl>");
    r = type(b, l + 1);
    r = r && enum_param_decl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (IDENT (EQ expr)?)?
  private static boolean enum_param_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl_1")) return false;
    enum_param_decl_1_0(b, l + 1);
    return true;
  }

  // IDENT (EQ expr)?
  private static boolean enum_param_decl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && enum_param_decl_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ expr)?
  private static boolean enum_param_decl_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl_1_0_1")) return false;
    enum_param_decl_1_0_1_0(b, l + 1);
    return true;
  }

  // EQ expr
  private static boolean enum_param_decl_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LP enum_param_decl (COMMA enum_param_decl)* RP
  public static boolean enum_param_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_list")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && enum_param_decl(b, l + 1);
    r = r && enum_param_list_2(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, ENUM_PARAM_LIST, r);
    return r;
  }

  // (COMMA enum_param_decl)*
  private static boolean enum_param_list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_list_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enum_param_list_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enum_param_list_2", c)) break;
    }
    return true;
  }

  // COMMA enum_param_decl
  private static boolean enum_param_list_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_list_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enum_param_decl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // type enum_param_list?
  public static boolean enum_spec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_SPEC, "<enum spec>");
    r = type(b, l + 1);
    r = r && enum_spec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // enum_param_list?
  private static boolean enum_spec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_1")) return false;
    enum_param_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EOS | RP | RBT | RB | COMMA | COLON
  public static boolean expr_terminator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_terminator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_TERMINATOR, "<expr terminator>");
    r = consumeToken(b, EOS);
    if (!r) r = consumeToken(b, RP);
    if (!r) r = consumeToken(b, RBT);
    if (!r) r = consumeToken(b, RB);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, COLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // decl_or_expr (COMMA decl_or_expr)*
  public static boolean expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_LIST, "<expression list>");
    r = decl_or_expr(b, l + 1);
    r = r && expression_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA decl_or_expr)*
  private static boolean expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_list_1", c)) break;
    }
    return true;
  }

  // COMMA decl_or_expr
  private static boolean expression_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && decl_or_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_FAULT TYPE_IDENT attributes? LB CONST_IDENT (COMMA CONST_IDENT)* COMMA? RB
  public static boolean fault_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_declaration")) return false;
    if (!nextTokenIs(b, KW_FAULT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_FAULT, TYPE_IDENT);
    r = r && fault_declaration_2(b, l + 1);
    r = r && consumeTokens(b, 0, LB, CONST_IDENT);
    r = r && fault_declaration_5(b, l + 1);
    r = r && fault_declaration_6(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, FAULT_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean fault_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_declaration_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // (COMMA CONST_IDENT)*
  private static boolean fault_declaration_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_declaration_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!fault_declaration_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fault_declaration_5", c)) break;
    }
    return true;
  }

  // COMMA CONST_IDENT
  private static boolean fault_declaration_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_declaration_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, CONST_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean fault_declaration_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_declaration_6")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // primary_group param_path? | type
  public static boolean flat_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flat_path")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FLAT_PATH, "<flat path>");
    r = flat_path_0(b, l + 1);
    if (!r) r = type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // primary_group param_path?
  private static boolean flat_path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flat_path_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1, 11);
    r = r && flat_path_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // param_path?
  private static boolean flat_path_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flat_path_0_1")) return false;
    param_path(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FLOAT16 | KW_FLOAT | KW_DOUBLE | KW_FLOAT128 | KW_BFLOAT16
  public static boolean float_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "float_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FLOAT_TYPE, "<float type>");
    r = consumeToken(b, KW_FLOAT16);
    if (!r) r = consumeToken(b, KW_FLOAT);
    if (!r) r = consumeToken(b, KW_DOUBLE);
    if (!r) r = consumeToken(b, KW_FLOAT128);
    if (!r) r = consumeToken(b, KW_BFLOAT16);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LP parameter_list? RP
  public static boolean fn_parameter_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fn_parameter_list")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && fn_parameter_list_1(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, FN_PARAMETER_LIST, r);
    return r;
  }

  // parameter_list?
  private static boolean fn_parameter_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fn_parameter_list_1")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expression_list? EOS cond? EOS expression_list?
  public static boolean for_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_cond")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_COND, "<for cond>");
    r = for_cond_0(b, l + 1);
    r = r && consumeToken(b, EOS);
    r = r && for_cond_2(b, l + 1);
    r = r && consumeToken(b, EOS);
    r = r && for_cond_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression_list?
  private static boolean for_cond_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_cond_0")) return false;
    expression_list(b, l + 1);
    return true;
  }

  // cond?
  private static boolean for_cond_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_cond_2")) return false;
    cond(b, l + 1);
    return true;
  }

  // expression_list?
  private static boolean for_cond_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_cond_4")) return false;
    expression_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FOR label? LP for_cond RP statement
  public static boolean for_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_stmt")) return false;
    if (!nextTokenIs(b, KW_FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_FOR);
    r = r && for_stmt_1(b, l + 1);
    r = r && consumeToken(b, LP);
    r = r && for_cond(b, l + 1);
    r = r && consumeToken(b, RP);
    r = r && statement(b, l + 1);
    exit_section_(b, m, FOR_STMT, r);
    return r;
  }

  // label?
  private static boolean for_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (KW_FOREACH | KW_FOREACH_R) label? LP foreach_vars COLON expr RP statement
  public static boolean foreach_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_stmt")) return false;
    if (!nextTokenIs(b, "<foreach stmt>", KW_FOREACH, KW_FOREACH_R)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOREACH_STMT, "<foreach stmt>");
    r = foreach_stmt_0(b, l + 1);
    r = r && foreach_stmt_1(b, l + 1);
    r = r && consumeToken(b, LP);
    r = r && foreach_vars(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RP);
    r = r && statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_FOREACH | KW_FOREACH_R
  private static boolean foreach_stmt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_stmt_0")) return false;
    boolean r;
    r = consumeToken(b, KW_FOREACH);
    if (!r) r = consumeToken(b, KW_FOREACH_R);
    return r;
  }

  // label?
  private static boolean foreach_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // optional_type? AMP? IDENT
  public static boolean foreach_var(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_var")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOREACH_VAR, "<foreach var>");
    r = foreach_var_0(b, l + 1);
    r = r && foreach_var_1(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // optional_type?
  private static boolean foreach_var_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_var_0")) return false;
    optional_type(b, l + 1);
    return true;
  }

  // AMP?
  private static boolean foreach_var_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_var_1")) return false;
    consumeToken(b, AMP);
    return true;
  }

  /* ********************************************************** */
  // foreach_var (COMMA foreach_var)?
  public static boolean foreach_vars(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_vars")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOREACH_VARS, "<foreach vars>");
    r = foreach_var(b, l + 1);
    r = r && foreach_vars_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA foreach_var)?
  private static boolean foreach_vars_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_vars_1")) return false;
    foreach_vars_1_0(b, l + 1);
    return true;
  }

  // COMMA foreach_var
  private static boolean foreach_vars_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "foreach_vars_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && foreach_var(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_FN func_header fn_parameter_list attributes? (macro_func_body | EOS)
  public static boolean func_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_definition")) return false;
    if (!nextTokenIs(b, KW_FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_FN);
    r = r && func_header(b, l + 1);
    r = r && fn_parameter_list(b, l + 1);
    r = r && func_definition_3(b, l + 1);
    r = r && func_definition_4(b, l + 1);
    exit_section_(b, m, FUNC_DEFINITION, r);
    return r;
  }

  // attributes?
  private static boolean func_definition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_definition_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // macro_func_body | EOS
  private static boolean func_definition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_definition_4")) return false;
    boolean r;
    r = macro_func_body(b, l + 1);
    if (!r) r = consumeToken(b, EOS);
    return r;
  }

  /* ********************************************************** */
  // optional_type func_name
  public static boolean func_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_header")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNC_HEADER, "<func header>");
    r = optional_type(b, l + 1);
    r = r && func_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (type DOT)? IDENT
  public static boolean func_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNC_NAME, "<func name>");
    r = func_name_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (type DOT)?
  private static boolean func_name_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_name_0")) return false;
    func_name_0_0(b, l + 1);
    return true;
  }

  // type DOT
  private static boolean func_name_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_name_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_FN optional_type fn_parameter_list
  public static boolean func_typedef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_typedef")) return false;
    if (!nextTokenIs(b, KW_FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_FN);
    r = r && optional_type(b, l + 1);
    r = r && fn_parameter_list(b, l + 1);
    exit_section_(b, m, FUNC_TYPEDEF, r);
    return r;
  }

  /* ********************************************************** */
  // expr | type
  public static boolean generic_parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERIC_PARAMETER, "<generic parameter>");
    r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LT_OP generic_parameter (COMMA generic_parameter)* GT_OP
  public static boolean generic_parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameters")) return false;
    if (!nextTokenIs(b, LT_OP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT_OP);
    r = r && generic_parameter(b, l + 1);
    r = r && generic_parameters_2(b, l + 1);
    r = r && consumeToken(b, GT_OP);
    exit_section_(b, m, GENERIC_PARAMETERS, r);
    return r;
  }

  // (COMMA generic_parameter)*
  private static boolean generic_parameters_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameters_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!generic_parameters_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "generic_parameters_2", c)) break;
    }
    return true;
  }

  // COMMA generic_parameter
  private static boolean generic_parameters_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameters_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && generic_parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_TLOCAL? optional_type IDENT (global_multi_declaration | global_single_declaration) EOS
  public static boolean global_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_DECLARATION, "<global declaration>");
    r = global_declaration_0(b, l + 1);
    r = r && optional_type(b, l + 1);
    r = r && consumeToken(b, IDENT);
    r = r && global_declaration_3(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_TLOCAL?
  private static boolean global_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_declaration_0")) return false;
    consumeToken(b, KW_TLOCAL);
    return true;
  }

  // global_multi_declaration | global_single_declaration
  private static boolean global_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_declaration_3")) return false;
    boolean r;
    r = global_multi_declaration(b, l + 1);
    if (!r) r = global_single_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // multi_declaration attributes?
  public static boolean global_multi_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_multi_declaration")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = multi_declaration(b, l + 1);
    r = r && global_multi_declaration_1(b, l + 1);
    exit_section_(b, m, GLOBAL_MULTI_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean global_multi_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_multi_declaration_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // attributes? (EQ expr)?
  public static boolean global_single_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_single_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_SINGLE_DECLARATION, "<global single declaration>");
    r = global_single_declaration_0(b, l + 1);
    r = r && global_single_declaration_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // attributes?
  private static boolean global_single_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_single_declaration_0")) return false;
    attributes(b, l + 1);
    return true;
  }

  // (EQ expr)?
  private static boolean global_single_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_single_declaration_1")) return false;
    global_single_declaration_1_0(b, l + 1);
    return true;
  }

  // EQ expr
  private static boolean global_single_declaration_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_single_declaration_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LP expr RP
  public static boolean grouped_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grouped_expression")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, GROUPED_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // IDENT EQ path_ident
  public static boolean ident_alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ident_alias")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENT, EQ);
    r = r && path_ident(b, l + 1);
    exit_section_(b, m, IDENT_ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // IDENT (COMMA IDENT)*
  public static boolean identifier_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_list")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && identifier_list_1(b, l + 1);
    exit_section_(b, m, IDENTIFIER_LIST, r);
    return r;
  }

  // (COMMA IDENT)*
  private static boolean identifier_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!identifier_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "identifier_list_1", c)) break;
    }
    return true;
  }

  // COMMA IDENT
  private static boolean identifier_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_IF label? paren_cond (LB switch_body RB else_part? | compound_statement else_part | statement)
  public static boolean if_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt")) return false;
    if (!nextTokenIs(b, KW_IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_IF);
    r = r && if_stmt_1(b, l + 1);
    r = r && paren_cond(b, l + 1);
    r = r && if_stmt_3(b, l + 1);
    exit_section_(b, m, IF_STMT, r);
    return r;
  }

  // label?
  private static boolean if_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  // LB switch_body RB else_part? | compound_statement else_part | statement
  private static boolean if_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_stmt_3_0(b, l + 1);
    if (!r) r = if_stmt_3_1(b, l + 1);
    if (!r) r = statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LB switch_body RB else_part?
  private static boolean if_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && switch_body(b, l + 1);
    r = r && consumeToken(b, RB);
    r = r && if_stmt_3_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // else_part?
  private static boolean if_stmt_3_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3_0_3")) return false;
    else_part(b, l + 1);
    return true;
  }

  // compound_statement else_part
  private static boolean if_stmt_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = compound_statement(b, l + 1);
    r = r && else_part(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IMPLIES expr
  public static boolean implies_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "implies_body")) return false;
    if (!nextTokenIs(b, IMPLIES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IMPLIES);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, IMPLIES_BODY, r);
    return r;
  }

  /* ********************************************************** */
  // KW_IMPORT import_paths attributes? EOS
  public static boolean import_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_decl")) return false;
    if (!nextTokenIs(b, KW_IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_IMPORT);
    r = r && import_paths(b, l + 1);
    r = r && import_decl_2(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, IMPORT_DECL, r);
    return r;
  }

  // attributes?
  private static boolean import_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_decl_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // path_ident (COMMA path_ident)*
  public static boolean import_paths(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path_ident(b, l + 1);
    r = r && import_paths_1(b, l + 1);
    exit_section_(b, m, IMPORT_PATHS, r);
    return r;
  }

  // (COMMA path_ident)*
  private static boolean import_paths_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!import_paths_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "import_paths_1", c)) break;
    }
    return true;
  }

  // COMMA path_ident
  private static boolean import_paths_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && path_ident(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LB (arg_list COMMA?)? RB
  public static boolean initializer_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_list")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && initializer_list_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, INITIALIZER_LIST, r);
    return r;
  }

  // (arg_list COMMA?)?
  private static boolean initializer_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_list_1")) return false;
    initializer_list_1_0(b, l + 1);
    return true;
  }

  // arg_list COMMA?
  private static boolean initializer_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arg_list(b, l + 1);
    r = r && initializer_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean initializer_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_list_1_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // KW_CHAR | KW_ICHAR | KW_SHORT | KW_USHORT | KW_INT | KW_UINT | KW_LONG | KW_ULONG | KW_INT128 | KW_UINT128
  public static boolean integer_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "integer_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTEGER_TYPE, "<integer type>");
    r = consumeToken(b, KW_CHAR);
    if (!r) r = consumeToken(b, KW_ICHAR);
    if (!r) r = consumeToken(b, KW_SHORT);
    if (!r) r = consumeToken(b, KW_USHORT);
    if (!r) r = consumeToken(b, KW_INT);
    if (!r) r = consumeToken(b, KW_UINT);
    if (!r) r = consumeToken(b, KW_LONG);
    if (!r) r = consumeToken(b, KW_ULONG);
    if (!r) r = consumeToken(b, KW_INT128);
    if (!r) r = consumeToken(b, KW_UINT128);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CONST_IDENT COLON
  public static boolean label(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "label")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CONST_IDENT, COLON);
    exit_section_(b, m, LABEL, r);
    return r;
  }

  /* ********************************************************** */
  // KW_FN optional_type? fn_parameter_list attributes?
  public static boolean lambda_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl")) return false;
    if (!nextTokenIs(b, KW_FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_FN);
    r = r && lambda_decl_1(b, l + 1);
    r = r && fn_parameter_list(b, l + 1);
    r = r && lambda_decl_3(b, l + 1);
    exit_section_(b, m, LAMBDA_DECL, r);
    return r;
  }

  // optional_type?
  private static boolean lambda_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl_1")) return false;
    optional_type(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean lambda_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CT_IDENT (EQ constant_expr)? | IDENT attributes? (EQ expr)?
  public static boolean local_decl_after_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type")) return false;
    if (!nextTokenIs(b, "<local decl after type>", CT_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_DECL_AFTER_TYPE, "<local decl after type>");
    r = local_decl_after_type_0(b, l + 1);
    if (!r) r = local_decl_after_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CT_IDENT (EQ constant_expr)?
  private static boolean local_decl_after_type_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_IDENT);
    r = r && local_decl_after_type_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ constant_expr)?
  private static boolean local_decl_after_type_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_0_1")) return false;
    local_decl_after_type_0_1_0(b, l + 1);
    return true;
  }

  // EQ constant_expr
  private static boolean local_decl_after_type_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && constant_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT attributes? (EQ expr)?
  private static boolean local_decl_after_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && local_decl_after_type_1_1(b, l + 1);
    r = r && local_decl_after_type_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean local_decl_after_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // (EQ expr)?
  private static boolean local_decl_after_type_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1_2")) return false;
    local_decl_after_type_1_2_0(b, l + 1);
    return true;
  }

  // EQ expr
  private static boolean local_decl_after_type_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_STATIC | KW_TLOCAL
  public static boolean local_decl_storage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_storage")) return false;
    if (!nextTokenIs(b, "<local decl storage>", KW_STATIC, KW_TLOCAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_DECL_STORAGE, "<local decl storage>");
    r = consumeToken(b, KW_STATIC);
    if (!r) r = consumeToken(b, KW_TLOCAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_MACRO macro_header LP macro_params RP attributes? macro_func_body
  public static boolean macro_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_declaration")) return false;
    if (!nextTokenIs(b, KW_MACRO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_MACRO);
    r = r && macro_header(b, l + 1);
    r = r && consumeToken(b, LP);
    r = r && macro_params(b, l + 1);
    r = r && consumeToken(b, RP);
    r = r && macro_declaration_5(b, l + 1);
    r = r && macro_func_body(b, l + 1);
    exit_section_(b, m, MACRO_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean macro_declaration_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_declaration_5")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // implies_body EOS | compound_statement
  public static boolean macro_func_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_func_body")) return false;
    if (!nextTokenIs(b, "<macro func body>", IMPLIES, LB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_FUNC_BODY, "<macro func body>");
    r = macro_func_body_0(b, l + 1);
    if (!r) r = compound_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // implies_body EOS
  private static boolean macro_func_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_func_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = implies_body(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (optional_type !DOT)? macro_name
  public static boolean macro_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_header")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_HEADER, "<macro header>");
    r = macro_header_0(b, l + 1);
    r = r && macro_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (optional_type !DOT)?
  private static boolean macro_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_header_0")) return false;
    macro_header_0_0(b, l + 1);
    return true;
  }

  // optional_type !DOT
  private static boolean macro_header_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_header_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = optional_type(b, l + 1);
    r = r && macro_header_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !DOT
  private static boolean macro_header_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_header_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, DOT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (type DOT)? (AT_IDENT | IDENT)
  public static boolean macro_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_NAME, "<macro name>");
    r = macro_name_0(b, l + 1);
    r = r && macro_name_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (type DOT)?
  private static boolean macro_name_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_name_0")) return false;
    macro_name_0_0(b, l + 1);
    return true;
  }

  // type DOT
  private static boolean macro_name_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_name_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // AT_IDENT | IDENT
  private static boolean macro_name_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_name_1")) return false;
    boolean r;
    r = consumeToken(b, AT_IDENT);
    if (!r) r = consumeToken(b, IDENT);
    return r;
  }

  /* ********************************************************** */
  // parameter_list? (EOS trailing_block_param)?
  public static boolean macro_params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_params")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_PARAMS, "<macro params>");
    r = macro_params_0(b, l + 1);
    r = r && macro_params_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // parameter_list?
  private static boolean macro_params_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_params_0")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  // (EOS trailing_block_param)?
  private static boolean macro_params_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_params_1")) return false;
    macro_params_1_0(b, l + 1);
    return true;
  }

  // EOS trailing_block_param
  private static boolean macro_params_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_params_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOS);
    r = r && trailing_block_param(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_MODULE path_ident (LT_OP module_params GT_OP)? attributes? EOS {
  // }
  public static boolean module(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module")) return false;
    if (!nextTokenIs(b, KW_MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_MODULE);
    r = r && path_ident(b, l + 1);
    r = r && module_2(b, l + 1);
    r = r && module_3(b, l + 1);
    r = r && consumeToken(b, EOS);
    r = r && module_5(b, l + 1);
    exit_section_(b, m, MODULE, r);
    return r;
  }

  // (LT_OP module_params GT_OP)?
  private static boolean module_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_2")) return false;
    module_2_0(b, l + 1);
    return true;
  }

  // LT_OP module_params GT_OP
  private static boolean module_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT_OP);
    r = r && module_params(b, l + 1);
    r = r && consumeToken(b, GT_OP);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean module_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // {
  // }
  private static boolean module_5(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // TYPE_IDENT | CONST_IDENT
  public static boolean module_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_param")) return false;
    if (!nextTokenIs(b, "<module param>", CONST_IDENT, TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODULE_PARAM, "<module param>");
    r = consumeToken(b, TYPE_IDENT);
    if (!r) r = consumeToken(b, CONST_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // module_param (COMMA module_param)*
  public static boolean module_params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_params")) return false;
    if (!nextTokenIs(b, "<module params>", CONST_IDENT, TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODULE_PARAMS, "<module params>");
    r = module_param(b, l + 1);
    r = r && module_params_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA module_param)*
  private static boolean module_params_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_params_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!module_params_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "module_params_1", c)) break;
    }
    return true;
  }

  // COMMA module_param
  private static boolean module_params_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_params_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && module_param(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // module top_level*
  public static boolean module_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_section")) return false;
    if (!nextTokenIs(b, KW_MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module(b, l + 1);
    r = r && module_section_1(b, l + 1);
    exit_section_(b, m, MODULE_SECTION, r);
    return r;
  }

  // top_level*
  private static boolean module_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!top_level(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "module_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // STAR | DIV | MOD
  public static boolean mult_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mult_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, STAR);
    if (!r) r = consumeToken(b, DIV);
    if (!r) r = consumeToken(b, MOD);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (COMMA IDENT)+
  public static boolean multi_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multi_declaration")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = multi_declaration_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!multi_declaration_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multi_declaration", c)) break;
    }
    exit_section_(b, m, MULTI_DECLARATION, r);
    return r;
  }

  // COMMA IDENT
  private static boolean multi_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multi_declaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_NEXTCASE ((CONST_IDENT COLON)? (type | expr))? EOS
  public static boolean nextcase_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt")) return false;
    if (!nextTokenIs(b, KW_NEXTCASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_NEXTCASE);
    r = r && nextcase_stmt_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, NEXTCASE_STMT, r);
    return r;
  }

  // ((CONST_IDENT COLON)? (type | expr))?
  private static boolean nextcase_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1")) return false;
    nextcase_stmt_1_0(b, l + 1);
    return true;
  }

  // (CONST_IDENT COLON)? (type | expr)
  private static boolean nextcase_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nextcase_stmt_1_0_0(b, l + 1);
    r = r && nextcase_stmt_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CONST_IDENT COLON)?
  private static boolean nextcase_stmt_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1_0_0")) return false;
    nextcase_stmt_1_0_0_0(b, l + 1);
    return true;
  }

  // CONST_IDENT COLON
  private static boolean nextcase_stmt_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CONST_IDENT, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // type | expr
  private static boolean nextcase_stmt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1_0_1")) return false;
    boolean r;
    r = type(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    return r;
  }

  /* ********************************************************** */
  // type BANG?
  public static boolean optional_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTIONAL_TYPE, "<optional type>");
    r = type(b, l + 1);
    r = r && optional_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BANG?
  private static boolean optional_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_type_1")) return false;
    consumeToken(b, BANG);
    return true;
  }

  /* ********************************************************** */
  // parameter (EQ expr)?
  public static boolean param_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_DECL, "<param decl>");
    r = parameter(b, l + 1);
    r = r && param_decl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (EQ expr)?
  private static boolean param_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_decl_1")) return false;
    param_decl_1_0(b, l + 1);
    return true;
  }

  // EQ expr
  private static boolean param_decl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_decl_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // param_path_element+
  public static boolean param_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path")) return false;
    if (!nextTokenIs(b, "<param path>", DOT, LBT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_PATH, "<param path>");
    r = param_path_element(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!param_path_element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "param_path", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBT expr (DOTDOT expr)? RBT | DOT IDENT
  public static boolean param_path_element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element")) return false;
    if (!nextTokenIs(b, "<param path element>", DOT, LBT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_PATH_ELEMENT, "<param path element>");
    r = param_path_element_0(b, l + 1);
    if (!r) r = parseTokens(b, 0, DOT, IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBT expr (DOTDOT expr)? RBT
  private static boolean param_path_element_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && expr(b, l + 1, -1);
    r = r && param_path_element_0_2(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOTDOT expr)?
  private static boolean param_path_element_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element_0_2")) return false;
    param_path_element_0_2_0(b, l + 1);
    return true;
  }

  // DOTDOT expr
  private static boolean param_path_element_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOTDOT);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  //     | ELLIPSIS | AMP IDENT attributes? | HASH_IDENT attributes? | IDENT ELLIPSIS? attributes?
  //     | CT_IDENT | CT_IDENT ELLIPSIS
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = parameter_0(b, l + 1);
    if (!r) r = consumeToken(b, ELLIPSIS);
    if (!r) r = parameter_2(b, l + 1);
    if (!r) r = parameter_3(b, l + 1);
    if (!r) r = parameter_4(b, l + 1);
    if (!r) r = consumeToken(b, CT_IDENT);
    if (!r) r = parseTokens(b, 0, CT_IDENT, ELLIPSIS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  private static boolean parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type(b, l + 1);
    r = r && parameter_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?
  private static boolean parameter_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_1_0(b, l + 1);
    if (!r) r = parameter_0_1_1(b, l + 1);
    if (!r) r = parameter_0_1_2(b, l + 1);
    if (!r) r = parameter_0_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS? IDENT attributes?
  private static boolean parameter_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_1_0_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    r = r && parameter_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS?
  private static boolean parameter_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_0_0")) return false;
    consumeToken(b, ELLIPSIS);
    return true;
  }

  // attributes?
  private static boolean parameter_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_0_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // ELLIPSIS? CT_IDENT
  private static boolean parameter_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_1_1_0(b, l + 1);
    r = r && consumeToken(b, CT_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS?
  private static boolean parameter_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_1_0")) return false;
    consumeToken(b, ELLIPSIS);
    return true;
  }

  // (HASH_IDENT | AMP IDENT) attributes?
  private static boolean parameter_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_1_2_0(b, l + 1);
    r = r && parameter_0_1_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // HASH_IDENT | AMP IDENT
  private static boolean parameter_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH_IDENT);
    if (!r) r = parseTokens(b, 0, AMP, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_0_1_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_2_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean parameter_0_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_1_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // AMP IDENT attributes?
  private static boolean parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AMP, IDENT);
    r = r && parameter_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // HASH_IDENT attributes?
  private static boolean parameter_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH_IDENT);
    r = r && parameter_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // IDENT ELLIPSIS? attributes?
  private static boolean parameter_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && parameter_4_1(b, l + 1);
    r = r && parameter_4_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS?
  private static boolean parameter_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_4_1")) return false;
    consumeToken(b, ELLIPSIS);
    return true;
  }

  // attributes?
  private static boolean parameter_4_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_4_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // param_decl (COMMA param_decl)*
  public static boolean parameter_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_LIST, "<parameter list>");
    r = param_decl(b, l + 1);
    r = r && parameter_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA param_decl)*
  private static boolean parameter_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_list_1", c)) break;
    }
    return true;
  }

  // COMMA param_decl
  private static boolean parameter_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && param_decl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LP cond RP
  public static boolean paren_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_cond")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && cond(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, PAREN_COND, r);
    return r;
  }

  /* ********************************************************** */
  // (IDENT SCOPE)+
  public static boolean path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!path_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "path", c)) break;
    }
    exit_section_(b, m, PATH, r);
    return r;
  }

  // IDENT SCOPE
  private static boolean path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENT, SCOPE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // path? AT_IDENT
  public static boolean path_at_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_at_ident")) return false;
    if (!nextTokenIs(b, "<path at ident>", AT_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_AT_IDENT, "<path at ident>");
    r = path_at_ident_0(b, l + 1);
    r = r && consumeToken(b, AT_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path?
  private static boolean path_at_ident_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_at_ident_0")) return false;
    path(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // path? CONST_IDENT
  public static boolean path_const(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_const")) return false;
    if (!nextTokenIs(b, "<path const>", CONST_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_CONST, "<path const>");
    r = path_const_0(b, l + 1);
    r = r && consumeToken(b, CONST_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path?
  private static boolean path_const_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_const_0")) return false;
    path(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // path? IDENT
  public static boolean path_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_ident")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path_ident_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, m, PATH_IDENT, r);
    return r;
  }

  // path?
  private static boolean path_ident_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_ident_0")) return false;
    path(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // range_loc? (DOTDOT | COLON) range_loc?
  public static boolean range_exp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_exp")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RANGE_EXP, "<range exp>");
    r = range_exp_0(b, l + 1);
    r = r && range_exp_1(b, l + 1);
    r = r && range_exp_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // range_loc?
  private static boolean range_exp_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_exp_0")) return false;
    range_loc(b, l + 1);
    return true;
  }

  // DOTDOT | COLON
  private static boolean range_exp_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_exp_1")) return false;
    boolean r;
    r = consumeToken(b, DOTDOT);
    if (!r) r = consumeToken(b, COLON);
    return r;
  }

  // range_loc?
  private static boolean range_exp_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_exp_2")) return false;
    range_loc(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // BIT_XOR? expr
  public static boolean range_loc(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_loc")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RANGE_LOC, "<range loc>");
    r = range_loc_0(b, l + 1);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BIT_XOR?
  private static boolean range_loc_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "range_loc_0")) return false;
    consumeToken(b, BIT_XOR);
    return true;
  }

  /* ********************************************************** */
  // LT_OP | GT_OP | LE_OP | GE_OP | EQ_OP | NE_OP
  public static boolean rel_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rel_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, LT_OP);
    if (!r) r = consumeToken(b, GT_OP);
    if (!r) r = consumeToken(b, LE_OP);
    if (!r) r = consumeToken(b, GE_OP);
    if (!r) r = consumeToken(b, EQ_OP);
    if (!r) r = consumeToken(b, NE_OP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_RETURN expr? EOS
  public static boolean return_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_stmt")) return false;
    if (!nextTokenIs(b, KW_RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_RETURN);
    r = r && return_stmt_1(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, RETURN_STMT, r);
    return r;
  }

  // expr?
  private static boolean return_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_stmt_1")) return false;
    expr(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // SHL | SHR
  public static boolean shift_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "shift_bin_op")) return false;
    if (!nextTokenIs(b, "<operator >", SHL, SHR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator >");
    r = consumeToken(b, SHL);
    if (!r) r = consumeToken(b, SHR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // compound_statement
  //     | var_stmt
  //     | declaration_stmt
  //     | return_stmt
  //     | if_stmt
  //     | while_stmt
  //     | defer_stmt
  //     | switch_stmt
  //     | do_stmt
  //     | for_stmt
  //     | foreach_stmt
  //     | continue_stmt
  //     | break_stmt
  //     | nextcase_stmt
  //     | asm_block_stmt
  //     | ct_echo_stmt
  //     | ct_assert_stmt
  //     | ct_if_stmt
  //     | ct_switch_stmt
  //     | ct_foreach_stmt
  //     | ct_for_stmt
  //     | expr EOS
  //     | assert_stmt
  //     | EOS
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = compound_statement(b, l + 1);
    if (!r) r = var_stmt(b, l + 1);
    if (!r) r = declaration_stmt(b, l + 1);
    if (!r) r = return_stmt(b, l + 1);
    if (!r) r = if_stmt(b, l + 1);
    if (!r) r = while_stmt(b, l + 1);
    if (!r) r = defer_stmt(b, l + 1);
    if (!r) r = switch_stmt(b, l + 1);
    if (!r) r = do_stmt(b, l + 1);
    if (!r) r = for_stmt(b, l + 1);
    if (!r) r = foreach_stmt(b, l + 1);
    if (!r) r = continue_stmt(b, l + 1);
    if (!r) r = break_stmt(b, l + 1);
    if (!r) r = nextcase_stmt(b, l + 1);
    if (!r) r = asm_block_stmt(b, l + 1);
    if (!r) r = ct_echo_stmt(b, l + 1);
    if (!r) r = ct_assert_stmt(b, l + 1);
    if (!r) r = ct_if_stmt(b, l + 1);
    if (!r) r = ct_switch_stmt(b, l + 1);
    if (!r) r = ct_foreach_stmt(b, l + 1);
    if (!r) r = ct_for_stmt(b, l + 1);
    if (!r) r = statement_21(b, l + 1);
    if (!r) r = assert_stmt(b, l + 1);
    if (!r) r = consumeToken(b, EOS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr EOS
  private static boolean statement_21(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_21")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1, -1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // statement+
  public static boolean statement_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT_LIST, "<statement list>");
    r = statement(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "statement_list", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_STATIC IDENT attributes? compound_statement
  public static boolean static_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "static_declaration")) return false;
    if (!nextTokenIs(b, KW_STATIC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_STATIC, IDENT);
    r = r && static_declaration_2(b, l + 1);
    r = r && compound_statement(b, l + 1);
    exit_section_(b, m, STATIC_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean static_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "static_declaration_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LB struct_member_declaration* RB
  public static boolean struct_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_body")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && struct_body_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, STRUCT_BODY, r);
    return r;
  }

  // struct_member_declaration*
  private static boolean struct_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!struct_member_declaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "struct_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // struct_or_union type_name attributes? struct_body
  public static boolean struct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration")) return false;
    if (!nextTokenIs(b, "<struct declaration>", KW_STRUCT, KW_UNION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_DECLARATION, "<struct declaration>");
    r = struct_or_union(b, l + 1);
    r = r && type_name(b, l + 1);
    r = r && struct_declaration_2(b, l + 1);
    r = r && struct_body(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // attributes?
  private static boolean struct_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // type identifier_list attributes? EOS
  //     | KW_INLINE type IDENT? attributes? EOS
  //     | struct_or_union IDENT? attributes? struct_body
  //     | KW_BITSTRUCT IDENT? ':' type attributes? bitstruct_body
  public static boolean struct_member_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_MEMBER_DECLARATION, "<struct member declaration>");
    r = struct_member_declaration_0(b, l + 1);
    if (!r) r = struct_member_declaration_1(b, l + 1);
    if (!r) r = struct_member_declaration_2(b, l + 1);
    if (!r) r = struct_member_declaration_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type identifier_list attributes? EOS
  private static boolean struct_member_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type(b, l + 1);
    r = r && identifier_list(b, l + 1);
    r = r && struct_member_declaration_0_2(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean struct_member_declaration_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_0_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // KW_INLINE type IDENT? attributes? EOS
  private static boolean struct_member_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_INLINE);
    r = r && type(b, l + 1);
    r = r && struct_member_declaration_1_2(b, l + 1);
    r = r && struct_member_declaration_1_3(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT?
  private static boolean struct_member_declaration_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_1_2")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_1_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // struct_or_union IDENT? attributes? struct_body
  private static boolean struct_member_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_or_union(b, l + 1);
    r = r && struct_member_declaration_2_1(b, l + 1);
    r = r && struct_member_declaration_2_2(b, l + 1);
    r = r && struct_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT?
  private static boolean struct_member_declaration_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2_1")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // KW_BITSTRUCT IDENT? ':' type attributes? bitstruct_body
  private static boolean struct_member_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_BITSTRUCT);
    r = r && struct_member_declaration_3_1(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && type(b, l + 1);
    r = r && struct_member_declaration_3_4(b, l + 1);
    r = r && bitstruct_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT?
  private static boolean struct_member_declaration_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3_1")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_3_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3_4")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_STRUCT | KW_UNION
  static boolean struct_or_union(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_or_union")) return false;
    if (!nextTokenIs(b, "", KW_STRUCT, KW_UNION)) return false;
    boolean r;
    r = consumeToken(b, KW_STRUCT);
    if (!r) r = consumeToken(b, KW_UNION);
    return r;
  }

  /* ********************************************************** */
  // (case_stmt | default_stmt)+
  public static boolean switch_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_body")) return false;
    if (!nextTokenIs(b, "<switch body>", KW_CASE, KW_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_BODY, "<switch body>");
    r = switch_body_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!switch_body_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "switch_body", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // case_stmt | default_stmt
  private static boolean switch_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_body_0")) return false;
    boolean r;
    r = case_stmt(b, l + 1);
    if (!r) r = default_stmt(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // KW_SWITCH label? paren_cond? LB switch_body? RB
  public static boolean switch_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt")) return false;
    if (!nextTokenIs(b, KW_SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_SWITCH);
    r = r && switch_stmt_1(b, l + 1);
    r = r && switch_stmt_2(b, l + 1);
    r = r && consumeToken(b, LB);
    r = r && switch_stmt_4(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, SWITCH_STMT, r);
    return r;
  }

  // label?
  private static boolean switch_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  // paren_cond?
  private static boolean switch_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_2")) return false;
    paren_cond(b, l + 1);
    return true;
  }

  // switch_body?
  private static boolean switch_stmt_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_4")) return false;
    switch_body(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (KW_CT_DEFAULT | KW_CT_CASE (constant_expr | type)) COLON top_level*
  public static boolean tl_ct_case(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_case")) return false;
    if (!nextTokenIs(b, "<tl ct case>", KW_CT_CASE, KW_CT_DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TL_CT_CASE, "<tl ct case>");
    r = tl_ct_case_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && tl_ct_case_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_CT_DEFAULT | KW_CT_CASE (constant_expr | type)
  private static boolean tl_ct_case_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_case_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_DEFAULT);
    if (!r) r = tl_ct_case_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_CT_CASE (constant_expr | type)
  private static boolean tl_ct_case_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_case_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_CASE);
    r = r && tl_ct_case_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // constant_expr | type
  private static boolean tl_ct_case_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_case_0_1_1")) return false;
    boolean r;
    r = constant_expr(b, l + 1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  // top_level*
  private static boolean tl_ct_case_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_case_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!top_level(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tl_ct_case_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_CT_IF const_paren_expr top_level* (KW_CT_ELSE top_level*)? KW_CT_ENDIF
  public static boolean tl_ct_if(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_if")) return false;
    if (!nextTokenIs(b, KW_CT_IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_IF);
    r = r && const_paren_expr(b, l + 1);
    r = r && tl_ct_if_2(b, l + 1);
    r = r && tl_ct_if_3(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDIF);
    exit_section_(b, m, TL_CT_IF, r);
    return r;
  }

  // top_level*
  private static boolean tl_ct_if_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_if_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!top_level(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tl_ct_if_2", c)) break;
    }
    return true;
  }

  // (KW_CT_ELSE top_level*)?
  private static boolean tl_ct_if_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_if_3")) return false;
    tl_ct_if_3_0(b, l + 1);
    return true;
  }

  // KW_CT_ELSE top_level*
  private static boolean tl_ct_if_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_if_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_ELSE);
    r = r && tl_ct_if_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // top_level*
  private static boolean tl_ct_if_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_if_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!top_level(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tl_ct_if_3_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ct_switch tl_ct_case* KW_CT_ENDSWITCH
  public static boolean tl_ct_switch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_switch")) return false;
    if (!nextTokenIs(b, KW_CT_SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ct_switch(b, l + 1);
    r = r && tl_ct_switch_1(b, l + 1);
    r = r && consumeToken(b, KW_CT_ENDSWITCH);
    exit_section_(b, m, TL_CT_SWITCH, r);
    return r;
  }

  // tl_ct_case*
  private static boolean tl_ct_switch_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tl_ct_switch_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!tl_ct_case(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tl_ct_switch_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // import_decl
  //     | KW_EXTERN? (func_definition | const_declaration | global_declaration)
  //     | ct_assert_stmt
  //     | ct_echo_stmt
  //     | ct_include_stmt
  //     | tl_ct_if
  //     | tl_ct_switch
  //     | struct_declaration
  //     | fault_declaration
  //     | enum_declaration
  //     | macro_declaration
  //     | typedef_declaration
  //     | define_declaration
  //     | static_declaration
  //     | bitstruct_declaration
  //     | KW_ASM expr EOS
  public static boolean top_level(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TOP_LEVEL, "<top level>");
    r = import_decl(b, l + 1);
    if (!r) r = top_level_1(b, l + 1);
    if (!r) r = ct_assert_stmt(b, l + 1);
    if (!r) r = ct_echo_stmt(b, l + 1);
    if (!r) r = ct_include_stmt(b, l + 1);
    if (!r) r = tl_ct_if(b, l + 1);
    if (!r) r = tl_ct_switch(b, l + 1);
    if (!r) r = struct_declaration(b, l + 1);
    if (!r) r = fault_declaration(b, l + 1);
    if (!r) r = enum_declaration(b, l + 1);
    if (!r) r = macro_declaration(b, l + 1);
    if (!r) r = typedef_declaration(b, l + 1);
    if (!r) r = define_declaration(b, l + 1);
    if (!r) r = static_declaration(b, l + 1);
    if (!r) r = bitstruct_declaration(b, l + 1);
    if (!r) r = top_level_15(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_EXTERN? (func_definition | const_declaration | global_declaration)
  private static boolean top_level_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = top_level_1_0(b, l + 1);
    r = r && top_level_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_EXTERN?
  private static boolean top_level_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level_1_0")) return false;
    consumeToken(b, KW_EXTERN);
    return true;
  }

  // func_definition | const_declaration | global_declaration
  private static boolean top_level_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level_1_1")) return false;
    boolean r;
    r = func_definition(b, l + 1);
    if (!r) r = const_declaration(b, l + 1);
    if (!r) r = global_declaration(b, l + 1);
    return r;
  }

  // KW_ASM expr EOS
  private static boolean top_level_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level_15")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_ASM);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // AT_IDENT (LP parameter_list? RP)?
  public static boolean trailing_block_param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trailing_block_param")) return false;
    if (!nextTokenIs(b, AT_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_IDENT);
    r = r && trailing_block_param_1(b, l + 1);
    exit_section_(b, m, TRAILING_BLOCK_PARAM, r);
    return r;
  }

  // (LP parameter_list? RP)?
  private static boolean trailing_block_param_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trailing_block_param_1")) return false;
    trailing_block_param_1_0(b, l + 1);
    return true;
  }

  // LP parameter_list? RP
  private static boolean trailing_block_param_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trailing_block_param_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && trailing_block_param_1_0_1(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter_list?
  private static boolean trailing_block_param_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trailing_block_param_1_0_1")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // default_module_section? module_section*
  static boolean translation_unit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "translation_unit")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = translation_unit_0(b, l + 1);
    r = r && translation_unit_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // default_module_section?
  private static boolean translation_unit_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "translation_unit_0")) return false;
    default_module_section(b, l + 1);
    return true;
  }

  // module_section*
  private static boolean translation_unit_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "translation_unit_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!module_section(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "translation_unit_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_TRY (type? IDENT EQ)? expr
  public static boolean try_unwrap(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap")) return false;
    if (!nextTokenIs(b, KW_TRY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_TRY);
    r = r && try_unwrap_1(b, l + 1);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, TRY_UNWRAP, r);
    return r;
  }

  // (type? IDENT EQ)?
  private static boolean try_unwrap_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_1")) return false;
    try_unwrap_1_0(b, l + 1);
    return true;
  }

  // type? IDENT EQ
  private static boolean try_unwrap_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = try_unwrap_1_0_0(b, l + 1);
    r = r && consumeTokens(b, 0, IDENT, EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  // type?
  private static boolean try_unwrap_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_1_0_0")) return false;
    type(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // try_unwrap (AND (try_unwrap | expr))*
  public static boolean try_unwrap_chain(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_chain")) return false;
    if (!nextTokenIs(b, KW_TRY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = try_unwrap(b, l + 1);
    r = r && try_unwrap_chain_1(b, l + 1);
    exit_section_(b, m, TRY_UNWRAP_CHAIN, r);
    return r;
  }

  // (AND (try_unwrap | expr))*
  private static boolean try_unwrap_chain_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_chain_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!try_unwrap_chain_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_unwrap_chain_1", c)) break;
    }
    return true;
  }

  // AND (try_unwrap | expr)
  private static boolean try_unwrap_chain_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_chain_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && try_unwrap_chain_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // try_unwrap | expr
  private static boolean try_unwrap_chain_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_unwrap_chain_1_0_1")) return false;
    boolean r;
    r = try_unwrap(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    return r;
  }

  /* ********************************************************** */
  // base_type type_suffix*
  public static boolean type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE, "<type>");
    r = base_type(b, l + 1);
    r = r && type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type_suffix*
  private static boolean type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!type_suffix(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TYPE_IDENT
  public static boolean type_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_name")) return false;
    if (!nextTokenIs(b, TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_IDENT);
    exit_section_(b, m, TYPE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // STAR | LBT (STAR | constant_expr)? RBT | LVEC (STAR | constant_expr) RVEC
  public static boolean type_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_SUFFIX, "<type suffix>");
    r = consumeToken(b, STAR);
    if (!r) r = type_suffix_1(b, l + 1);
    if (!r) r = type_suffix_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBT (STAR | constant_expr)? RBT
  private static boolean type_suffix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && type_suffix_1_1(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (STAR | constant_expr)?
  private static boolean type_suffix_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_1_1")) return false;
    type_suffix_1_1_0(b, l + 1);
    return true;
  }

  // STAR | constant_expr
  private static boolean type_suffix_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, STAR);
    if (!r) r = constant_expr(b, l + 1);
    return r;
  }

  // LVEC (STAR | constant_expr) RVEC
  private static boolean type_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LVEC);
    r = r && type_suffix_2_1(b, l + 1);
    r = r && consumeToken(b, RVEC);
    exit_section_(b, m, null, r);
    return r;
  }

  // STAR | constant_expr
  private static boolean type_suffix_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_2_1")) return false;
    boolean r;
    r = consumeToken(b, STAR);
    if (!r) r = constant_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // KW_TYPEDEF type_name attributes? EQ distinct_inline? typedef_type EOS
  public static boolean typedef_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_declaration")) return false;
    if (!nextTokenIs(b, KW_TYPEDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_TYPEDEF);
    r = r && type_name(b, l + 1);
    r = r && typedef_declaration_2(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && typedef_declaration_4(b, l + 1);
    r = r && typedef_type(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, TYPEDEF_DECLARATION, r);
    return r;
  }

  // attributes?
  private static boolean typedef_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_declaration_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // distinct_inline?
  private static boolean typedef_declaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_declaration_4")) return false;
    distinct_inline(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // func_typedef | type generic_parameters?
  public static boolean typedef_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPEDEF_TYPE, "<typedef type>");
    r = func_typedef(b, l + 1);
    if (!r) r = typedef_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type generic_parameters?
  private static boolean typedef_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_type_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type(b, l + 1);
    r = r && typedef_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // generic_parameters?
  private static boolean typedef_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_type_1_1")) return false;
    generic_parameters(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // AMP | AND | STAR | PLUS | MINUS | BIT_NOT | BANG | PLUSPLUS | MINUSMINUS | LP type RP
  public static boolean unary_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_OP, "<operator>");
    r = consumeToken(b, AMP);
    if (!r) r = consumeToken(b, AND);
    if (!r) r = consumeToken(b, STAR);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, BIT_NOT);
    if (!r) r = consumeToken(b, BANG);
    if (!r) r = consumeToken(b, PLUSPLUS);
    if (!r) r = consumeToken(b, MINUSMINUS);
    if (!r) r = unary_op_9(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LP type RP
  private static boolean unary_op_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_op_9")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && type(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_VAR (IDENT EQ expr | CT_TYPE_IDENT (EQ type)? | CT_IDENT (EQ expr)?)
  public static boolean var_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl")) return false;
    if (!nextTokenIs(b, KW_VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_VAR);
    r = r && var_decl_1(b, l + 1);
    exit_section_(b, m, VAR_DECL, r);
    return r;
  }

  // IDENT EQ expr | CT_TYPE_IDENT (EQ type)? | CT_IDENT (EQ expr)?
  private static boolean var_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = var_decl_1_0(b, l + 1);
    if (!r) r = var_decl_1_1(b, l + 1);
    if (!r) r = var_decl_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT EQ expr
  private static boolean var_decl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENT, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CT_TYPE_IDENT (EQ type)?
  private static boolean var_decl_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_TYPE_IDENT);
    r = r && var_decl_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ type)?
  private static boolean var_decl_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_1_1")) return false;
    var_decl_1_1_1_0(b, l + 1);
    return true;
  }

  // EQ type
  private static boolean var_decl_1_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CT_IDENT (EQ expr)?
  private static boolean var_decl_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_IDENT);
    r = r && var_decl_1_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ expr)?
  private static boolean var_decl_1_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_2_1")) return false;
    var_decl_1_2_1_0(b, l + 1);
    return true;
  }

  // EQ expr
  private static boolean var_decl_1_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // var_decl EOS
  public static boolean var_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_stmt")) return false;
    if (!nextTokenIs(b, KW_VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = var_decl(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, VAR_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // KW_WHILE label? paren_cond statement
  public static boolean while_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_stmt")) return false;
    if (!nextTokenIs(b, KW_WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_WHILE);
    r = r && while_stmt_1(b, l + 1);
    r = r && paren_cond(b, l + 1);
    r = r && statement(b, l + 1);
    exit_section_(b, m, WHILE_STMT, r);
    return r;
  }

  // label?
  private static boolean while_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_bin_expr)
  // 1: ATOM(assign_type_expr)
  // 2: BINARY(elvis_bin_expr) BINARY(optelse_bin_expr) BINARY(ternary_expr) POSTFIX(optional_expr)
  // 3: BINARY(or_bin_expr)
  // 4: BINARY(and_bin_expr)
  // 5: BINARY(rel_bin_expr)
  // 6: BINARY(add_bin_expr)
  // 7: BINARY(bit_bin_expr)
  // 8: BINARY(shift_bin_expr)
  // 9: BINARY(mult_bin_expr)
  // 10: PREFIX(unary_expr)
  // 11: POSTFIX(call_expr)
  // 12: ATOM(literal_expr) ATOM(path_ident_expr) ATOM(string_expr) ATOM(bytes_expr)
  //    ATOM(keyword_expr) ATOM(builtin_const_expr) ATOM(builtin_expr) ATOM(path_const_expr)
  //    ATOM(path_at_ident_expr) ATOM(compound_init_expr) PREFIX(grouped_expr) ATOM(local_ident_expr)
  //    ATOM(type_access_expr) ATOM(expr_block_expr) ATOM(ct_call_expr) ATOM(ct_arg_expr)
  //    ATOM(ct_analyze_expr) ATOM(ct_checks_expr) ATOM(lambda_decl_expr) PREFIX(lambda_decl_short_expr)
  //    ATOM(init_list_expr)
  public static boolean expr(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr")) return false;
    addVariant(b, "<expr>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expr>");
    r = assign_type_expr(b, l + 1);
    if (!r) r = unary_expr(b, l + 1);
    if (!r) r = literal_expr(b, l + 1);
    if (!r) r = path_ident_expr(b, l + 1);
    if (!r) r = string_expr(b, l + 1);
    if (!r) r = bytes_expr(b, l + 1);
    if (!r) r = keyword_expr(b, l + 1);
    if (!r) r = builtin_const_expr(b, l + 1);
    if (!r) r = builtin_expr(b, l + 1);
    if (!r) r = path_const_expr(b, l + 1);
    if (!r) r = path_at_ident_expr(b, l + 1);
    if (!r) r = compound_init_expr(b, l + 1);
    if (!r) r = grouped_expr(b, l + 1);
    if (!r) r = local_ident_expr(b, l + 1);
    if (!r) r = type_access_expr(b, l + 1);
    if (!r) r = expr_block_expr(b, l + 1);
    if (!r) r = ct_call_expr(b, l + 1);
    if (!r) r = ct_arg_expr(b, l + 1);
    if (!r) r = ct_analyze_expr(b, l + 1);
    if (!r) r = ct_checks_expr(b, l + 1);
    if (!r) r = lambda_decl_expr(b, l + 1);
    if (!r) r = lambda_decl_short_expr(b, l + 1);
    if (!r) r = init_list_expr(b, l + 1);
    p = r;
    r = r && expr_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expr_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && assign_bin_op(b, l + 1)) {
        r = expr(b, l, -1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, ELVIS)) {
        r = expr(b, l, 1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, OPTELSE)) {
        r = expr(b, l, 1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 2 && ternary_expr_0(b, l + 1)) {
        r = report_error_(b, expr(b, l, 1));
        r = ternary_expr_1(b, l + 1) && r;
        exit_section_(b, l, m, TERNARY_EXPR, r, true, null);
      }
      else if (g < 2 && optional_expr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, OPTIONAL_EXPR, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, OR)) {
        r = expr(b, l, 3);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, AND)) {
        r = expr(b, l, 4);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 5 && rel_bin_op(b, l + 1)) {
        r = expr(b, l, 5);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 6 && add_bin_op(b, l + 1)) {
        r = expr(b, l, 6);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 7 && bit_bin_op(b, l + 1)) {
        r = expr(b, l, 7);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 8 && shift_bin_op(b, l + 1)) {
        r = expr(b, l, 8);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 9 && mult_bin_op(b, l + 1)) {
        r = expr(b, l, 9);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 11 && call_expr_tail(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, CALL_EXPR, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // CT_TYPE_IDENT EQ type
  public static boolean assign_type_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_type_expr")) return false;
    if (!nextTokenIsSmart(b, CT_TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, CT_TYPE_IDENT, EQ);
    r = r && type(b, l + 1);
    exit_section_(b, m, ASSIGN_TYPE_EXPR, r);
    return r;
  }

  // QUESTION !(BANG? expr_terminator)
  private static boolean ternary_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, QUESTION);
    r = r && ternary_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !(BANG? expr_terminator)
  private static boolean ternary_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !ternary_expr_0_1_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BANG? expr_terminator
  private static boolean ternary_expr_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ternary_expr_0_1_0_0(b, l + 1);
    r = r && expr_terminator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BANG?
  private static boolean ternary_expr_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1_0_0")) return false;
    consumeTokenSmart(b, BANG);
    return true;
  }

  // COLON expr
  private static boolean ternary_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // QUESTION &(BANG? expr_terminator)
  private static boolean optional_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, QUESTION);
    r = r && optional_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &(BANG? expr_terminator)
  private static boolean optional_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = optional_expr_0_1_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BANG? expr_terminator
  private static boolean optional_expr_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = optional_expr_0_1_0_0(b, l + 1);
    r = r && expr_terminator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BANG?
  private static boolean optional_expr_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1_0_0")) return false;
    consumeTokenSmart(b, BANG);
    return true;
  }

  public static boolean unary_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unary_op(b, l + 1);
    p = r;
    r = p && expr(b, l, 10);
    exit_section_(b, l, m, UNARY_EXPR, r, p, null);
    return r || p;
  }

  // INT_LITERAL | FLOAT_LITERAL
  public static boolean literal_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expr")) return false;
    if (!nextTokenIsSmart(b, FLOAT_LITERAL, INT_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_EXPR, "<literal expr>");
    r = consumeTokenSmart(b, INT_LITERAL);
    if (!r) r = consumeTokenSmart(b, FLOAT_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path_ident
  public static boolean path_ident_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_ident_expr")) return false;
    if (!nextTokenIsSmart(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = path_ident(b, l + 1);
    exit_section_(b, m, PATH_IDENT_EXPR, r);
    return r;
  }

  // STRING_LIT+ | CHAR_LIT
  public static boolean string_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_expr")) return false;
    if (!nextTokenIsSmart(b, CHAR_LIT, STRING_LIT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_EXPR, "<string expr>");
    r = string_expr_0(b, l + 1);
    if (!r) r = consumeTokenSmart(b, CHAR_LIT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STRING_LIT+
  private static boolean string_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, STRING_LIT);
    while (r) {
      int c = current_position_(b);
      if (!consumeTokenSmart(b, STRING_LIT)) break;
      if (!empty_element_parsed_guard_(b, "string_expr_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // BYTES
  public static boolean bytes_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bytes_expr")) return false;
    if (!nextTokenIsSmart(b, BYTES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, BYTES);
    exit_section_(b, m, BYTES_EXPR, r);
    return r;
  }

  // KW_NULL | KW_TRUE | KW_FALSE | KW_CT_VACOUNT
  public static boolean keyword_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_EXPR, "<keyword expr>");
    r = consumeTokenSmart(b, KW_NULL);
    if (!r) r = consumeTokenSmart(b, KW_TRUE);
    if (!r) r = consumeTokenSmart(b, KW_FALSE);
    if (!r) r = consumeTokenSmart(b, KW_CT_VACOUNT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // BUILTIN_CONST
  public static boolean builtin_const_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_const_expr")) return false;
    if (!nextTokenIsSmart(b, BUILTIN_CONST)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, BUILTIN_CONST);
    exit_section_(b, m, BUILTIN_CONST_EXPR, r);
    return r;
  }

  // BUILTIN
  public static boolean builtin_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtin_expr")) return false;
    if (!nextTokenIsSmart(b, BUILTIN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, BUILTIN);
    exit_section_(b, m, BUILTIN_EXPR, r);
    return r;
  }

  // path_const
  public static boolean path_const_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_const_expr")) return false;
    if (!nextTokenIsSmart(b, CONST_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_CONST_EXPR, "<path const expr>");
    r = path_const(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // path_at_ident
  public static boolean path_at_ident_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_at_ident_expr")) return false;
    if (!nextTokenIsSmart(b, AT_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_AT_IDENT_EXPR, "<path at ident expr>");
    r = path_at_ident(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type initializer_list
  public static boolean compound_init_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compound_init_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPOUND_INIT_EXPR, "<compound init expr>");
    r = type(b, l + 1);
    r = r && initializer_list(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  public static boolean grouped_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grouped_expr")) return false;
    if (!nextTokenIsSmart(b, LP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, LP);
    p = r;
    r = p && expr(b, l, -1);
    r = p && report_error_(b, consumeToken(b, RP)) && r;
    exit_section_(b, l, m, GROUPED_EXPR, r, p, null);
    return r || p;
  }

  // CT_IDENT | HASH_IDENT
  public static boolean local_ident_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_ident_expr")) return false;
    if (!nextTokenIsSmart(b, CT_IDENT, HASH_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_IDENT_EXPR, "<local ident expr>");
    r = consumeTokenSmart(b, CT_IDENT);
    if (!r) r = consumeTokenSmart(b, HASH_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type DOT (access_ident | CONST_IDENT)
  public static boolean type_access_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_access_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_ACCESS_EXPR, "<type access expr>");
    r = type(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && type_access_expr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // access_ident | CONST_IDENT
  private static boolean type_access_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_access_expr_2")) return false;
    boolean r;
    r = access_ident(b, l + 1);
    if (!r) r = consumeTokenSmart(b, CONST_IDENT);
    return r;
  }

  // LBRAPIPE statement_list? RBRAPIPE
  public static boolean expr_block_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_block_expr")) return false;
    if (!nextTokenIsSmart(b, LBRAPIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LBRAPIPE);
    r = r && expr_block_expr_1(b, l + 1);
    r = r && consumeToken(b, RBRAPIPE);
    exit_section_(b, m, EXPR_BLOCK_EXPR, r);
    return r;
  }

  // statement_list?
  private static boolean expr_block_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_block_expr_1")) return false;
    statement_list(b, l + 1);
    return true;
  }

  // ct_call LP flat_path RP
  public static boolean ct_call_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_call_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_CALL_EXPR, "<ct call expr>");
    r = ct_call(b, l + 1);
    r = r && consumeToken(b, LP);
    r = r && flat_path(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ct_arg grouped_expression
  public static boolean ct_arg_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_arg_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ARG_EXPR, "<ct arg expr>");
    r = ct_arg(b, l + 1);
    r = r && grouped_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ct_analyze grouped_expression
  public static boolean ct_analyze_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_analyze_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ANALYZE_EXPR, "<ct analyze expr>");
    r = ct_analyze(b, l + 1);
    r = r && grouped_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_CT_CHECKS LP expression_list RP
  public static boolean ct_checks_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_checks_expr")) return false;
    if (!nextTokenIsSmart(b, KW_CT_CHECKS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, KW_CT_CHECKS, LP);
    r = r && expression_list(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, CT_CHECKS_EXPR, r);
    return r;
  }

  // lambda_decl compound_statement
  public static boolean lambda_decl_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl_expr")) return false;
    if (!nextTokenIsSmart(b, KW_FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lambda_decl(b, l + 1);
    r = r && compound_statement(b, l + 1);
    exit_section_(b, m, LAMBDA_DECL_EXPR, r);
    return r;
  }

  public static boolean lambda_decl_short_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl_short_expr")) return false;
    if (!nextTokenIsSmart(b, KW_FN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = lambda_decl_short_expr_0(b, l + 1);
    p = r;
    r = p && expr(b, l, -1);
    exit_section_(b, l, m, LAMBDA_DECL_SHORT_EXPR, r, p, null);
    return r || p;
  }

  // lambda_decl IMPLIES
  private static boolean lambda_decl_short_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lambda_decl_short_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lambda_decl(b, l + 1);
    r = r && consumeToken(b, IMPLIES);
    exit_section_(b, m, null, r);
    return r;
  }

  // initializer_list
  public static boolean init_list_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "init_list_expr")) return false;
    if (!nextTokenIsSmart(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = initializer_list(b, l + 1);
    exit_section_(b, m, INIT_LIST_EXPR, r);
    return r;
  }

}
