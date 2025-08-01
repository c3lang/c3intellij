// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.c3lang.intellij.psi.C3Types.*;
import static org.c3lang.intellij.psi.impl.C3ParserUtil.*;
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
      CT_CALL_EXPR, CT_DEFINED_CHECK_EXPR, CT_DEFINED_EXPR, CT_FEATURE_EXPR,
      DECL_OR_EXPR, EXPR, GROUPED_EXPR, INIT_LIST_EXPR,
      KEYWORD_EXPR, LAMBDA_DECL_EXPR, LAMBDA_DECL_SHORT_EXPR, LITERAL_EXPR,
      LOCAL_IDENT_EXPR, OPTIONAL_EXPR, PATH_AT_IDENT_EXPR, PATH_CONST_EXPR,
      PATH_IDENT_EXPR, STRING_EXPR, TERNARY_EXPR, TYPE_ACCESS_EXPR,
      UNARY_EXPR),
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
  // PLUS | MINUS | CT_PLUS
  public static boolean add_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, CT_PLUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_ALIAS alias_name attributes? EQ alias_declaration_source EOS
  public static boolean alias_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_decl")) return false;
    if (!nextTokenIs(b, KW_ALIAS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ALIAS_DECL, null);
    r = consumeToken(b, KW_ALIAS);
    r = r && alias_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, alias_decl_2(b, l + 1));
    r = p && report_error_(b, consumeToken(b, EQ)) && r;
    r = p && report_error_(b, alias_declaration_source(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean alias_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_decl_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (KW_MODULE path) | ((path_const | path_ident | path_at_ident) generic_parameters?)
  public static boolean alias_declaration_source(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_declaration_source")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIAS_DECLARATION_SOURCE, "<alias declaration source>");
    r = alias_declaration_source_0(b, l + 1);
    if (!r) r = alias_declaration_source_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_MODULE path
  private static boolean alias_declaration_source_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_declaration_source_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_MODULE);
    r = r && path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (path_const | path_ident | path_at_ident) generic_parameters?
  private static boolean alias_declaration_source_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_declaration_source_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = alias_declaration_source_1_0(b, l + 1);
    r = r && alias_declaration_source_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // path_const | path_ident | path_at_ident
  private static boolean alias_declaration_source_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_declaration_source_1_0")) return false;
    boolean r;
    r = path_const(b, l + 1);
    if (!r) r = path_ident(b, l + 1);
    if (!r) r = path_at_ident(b, l + 1);
    return r;
  }

  // generic_parameters?
  private static boolean alias_declaration_source_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_declaration_source_1_1")) return false;
    generic_parameters(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CONST_IDENT | AT_IDENT | IDENT
  public static boolean alias_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIAS_NAME, "<alias name>");
    r = consumeToken(b, CONST_IDENT);
    if (!r) r = consumeToken(b, AT_IDENT);
    if (!r) r = consumeToken(b, IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_ALIAS type_name attributes? EQ typedef_type EOS
  public static boolean alias_type_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_type_decl")) return false;
    if (!nextTokenIs(b, KW_ALIAS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ALIAS_TYPE_DECL, null);
    r = consumeToken(b, KW_ALIAS);
    r = r && type_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, alias_type_decl_2(b, l + 1));
    r = p && report_error_(b, consumeToken(b, EQ)) && r;
    r = p && report_error_(b, typedef_type(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean alias_type_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alias_type_decl_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (named_ident COLON (expr | type)) | param_path (EQ (expr | type))? | expr | type | KW_CT_VASPLAT (LBT range_exp RBT)? | ELLIPSIS expr
  public static boolean arg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARG, "<arg>");
    r = arg_0(b, l + 1);
    if (!r) r = arg_1(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    if (!r) r = arg_4(b, l + 1);
    if (!r) r = arg_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // named_ident COLON (expr | type)
  private static boolean arg_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = named_ident(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && arg_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr | type
  private static boolean arg_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_0_2")) return false;
    boolean r;
    r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  // param_path (EQ (expr | type))?
  private static boolean arg_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = param_path(b, l + 1);
    r = r && arg_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ (expr | type))?
  private static boolean arg_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1_1")) return false;
    arg_1_1_0(b, l + 1);
    return true;
  }

  // EQ (expr | type)
  private static boolean arg_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && arg_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr | type
  private static boolean arg_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_1_1_0_1")) return false;
    boolean r;
    r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    return r;
  }

  // KW_CT_VASPLAT (LBT range_exp RBT)?
  private static boolean arg_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_VASPLAT);
    r = r && arg_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LBT range_exp RBT)?
  private static boolean arg_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_4_1")) return false;
    arg_4_1_0(b, l + 1);
    return true;
  }

  // LBT range_exp RBT
  private static boolean arg_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && range_exp(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS expr
  private static boolean arg_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELLIPSIS);
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // arg (COMMA arg)* COMMA?
  public static boolean arg_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARG_LIST, "<arg list>");
    r = arg(b, l + 1);
    r = r && arg_list_1(b, l + 1);
    r = r && arg_list_2(b, l + 1);
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

  // COMMA?
  private static boolean arg_list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_2")) return false;
    consumeToken(b, COMMA);
    return true;
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
  // KW_ASM (LP expr RP attributes? | attributes? LB asm_stmt* RB)
  public static boolean asm_block_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt")) return false;
    if (!nextTokenIs(b, KW_ASM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASM_BLOCK_STMT, null);
    r = consumeToken(b, KW_ASM);
    p = r; // pin = 1
    r = r && asm_block_stmt_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // LP expr RP attributes? | attributes? LB asm_stmt* RB
  private static boolean asm_block_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_block_stmt_1_0(b, l + 1);
    if (!r) r = asm_block_stmt_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LP expr RP attributes?
  private static boolean asm_block_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RP);
    r = r && asm_block_stmt_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean asm_block_stmt_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_0_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // attributes? LB asm_stmt* RB
  private static boolean asm_block_stmt_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_block_stmt_1_1_0(b, l + 1);
    r = r && consumeToken(b, LB);
    r = r && asm_block_stmt_1_1_2(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean asm_block_stmt_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_1_0")) return false;
    attributes(b, l + 1);
    return true;
  }

  // asm_stmt*
  private static boolean asm_block_stmt_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_block_stmt_1_1_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!asm_stmt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "asm_block_stmt_1_1_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_ASM expr attributes? EOS
  public static boolean asm_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_declaration")) return false;
    if (!nextTokenIs(b, KW_ASM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASM_DECLARATION, null);
    r = consumeToken(b, KW_ASM);
    r = r && expr(b, l + 1, -1);
    p = r; // pin = 2
    r = r && report_error_(b, asm_declaration_2(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean asm_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_declaration_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CT_IDENT | CT_CONST_IDENT | AMP? IDENT | CONST_IDENT
  //     | MINUS? FLOAT_LITERAL | MINUS? INT_LITERAL | grouped_expression | LBT asm_addr RBT
  public static boolean asm_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASM_EXPR, "<asm expr>");
    r = consumeToken(b, CT_IDENT);
    if (!r) r = consumeToken(b, CT_CONST_IDENT);
    if (!r) r = asm_expr_2(b, l + 1);
    if (!r) r = consumeToken(b, CONST_IDENT);
    if (!r) r = asm_expr_4(b, l + 1);
    if (!r) r = asm_expr_5(b, l + 1);
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

  // MINUS? FLOAT_LITERAL
  private static boolean asm_expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_expr_4_0(b, l + 1);
    r = r && consumeToken(b, FLOAT_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUS?
  private static boolean asm_expr_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_4_0")) return false;
    consumeToken(b, MINUS);
    return true;
  }

  // MINUS? INT_LITERAL
  private static boolean asm_expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = asm_expr_5_0(b, l + 1);
    r = r && consumeToken(b, INT_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUS?
  private static boolean asm_expr_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_expr_5_0")) return false;
    consumeToken(b, MINUS);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASM_STMT, "<asm stmt>");
    r = asm_instr(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, asm_stmt_1(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // asm_exprs?
  private static boolean asm_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asm_stmt_1")) return false;
    asm_exprs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_ASSERT LP expr (COMMA expr)* RP EOS
  public static boolean assert_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt")) return false;
    if (!nextTokenIs(b, KW_ASSERT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSERT_STMT, null);
    r = consumeTokens(b, 1, KW_ASSERT, LP);
    p = r; // pin = 1
    r = r && report_error_(b, expr(b, l + 1, -1));
    r = p && report_error_(b, assert_stmt_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeTokens(b, -1, RP, EOS)) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA expr)*
  private static boolean assert_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assert_stmt_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!assert_stmt_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "assert_stmt_3", c)) break;
    }
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
  // KW_ATTRDEF attribute_user_name (LP parameter_list RP)? attributes? (EQ def_attr_values)? EOS
  public static boolean attrdef_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl")) return false;
    if (!nextTokenIs(b, KW_ATTRDEF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRDEF_DECL, null);
    r = consumeToken(b, KW_ATTRDEF);
    r = r && attribute_user_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, attrdef_decl_2(b, l + 1));
    r = p && report_error_(b, attrdef_decl_3(b, l + 1)) && r;
    r = p && report_error_(b, attrdef_decl_4(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (LP parameter_list RP)?
  private static boolean attrdef_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl_2")) return false;
    attrdef_decl_2_0(b, l + 1);
    return true;
  }

  // LP parameter_list RP
  private static boolean attrdef_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && parameter_list(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean attrdef_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // (EQ def_attr_values)?
  private static boolean attrdef_decl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl_4")) return false;
    attrdef_decl_4_0(b, l + 1);
    return true;
  }

  // EQ def_attr_values
  private static boolean attrdef_decl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attrdef_decl_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && def_attr_values(b, l + 1);
    exit_section_(b, m, null, r);
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
  // BIT_AND_ASSIGN | BIT_OR_ASSIGN | BIT_XOR_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | SHL_ASSIGN | SHR_ASSIGN | &RP | PLUS &RP | MINUS &RP | DIV | STAR &RP | MOD | EQ_OP | NE_OP | BIT_XOR | BIT_NOT | BIT_OR | AMP &RP | SHL | SHR | LBT RBT EQ | AMP? LBT RBT
  public static boolean attribute_operator_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_OPERATOR_EXPR, "<attribute operator expr>");
    r = consumeToken(b, BIT_AND_ASSIGN);
    if (!r) r = consumeToken(b, BIT_OR_ASSIGN);
    if (!r) r = consumeToken(b, BIT_XOR_ASSIGN);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, MULT_ASSIGN);
    if (!r) r = consumeToken(b, DIV_ASSIGN);
    if (!r) r = consumeToken(b, MOD_ASSIGN);
    if (!r) r = consumeToken(b, SHL_ASSIGN);
    if (!r) r = consumeToken(b, SHR_ASSIGN);
    if (!r) r = attribute_operator_expr_10(b, l + 1);
    if (!r) r = attribute_operator_expr_11(b, l + 1);
    if (!r) r = attribute_operator_expr_12(b, l + 1);
    if (!r) r = consumeToken(b, DIV);
    if (!r) r = attribute_operator_expr_14(b, l + 1);
    if (!r) r = consumeToken(b, MOD);
    if (!r) r = consumeToken(b, EQ_OP);
    if (!r) r = consumeToken(b, NE_OP);
    if (!r) r = consumeToken(b, BIT_XOR);
    if (!r) r = consumeToken(b, BIT_NOT);
    if (!r) r = consumeToken(b, BIT_OR);
    if (!r) r = attribute_operator_expr_21(b, l + 1);
    if (!r) r = consumeToken(b, SHL);
    if (!r) r = consumeToken(b, SHR);
    if (!r) r = parseTokens(b, 0, LBT, RBT, EQ);
    if (!r) r = attribute_operator_expr_25(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // &RP
  private static boolean attribute_operator_expr_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_10")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PLUS &RP
  private static boolean attribute_operator_expr_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    r = r && attribute_operator_expr_11_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &RP
  private static boolean attribute_operator_expr_11_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_11_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // MINUS &RP
  private static boolean attribute_operator_expr_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_12")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUS);
    r = r && attribute_operator_expr_12_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &RP
  private static boolean attribute_operator_expr_12_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_12_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STAR &RP
  private static boolean attribute_operator_expr_14(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_14")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    r = r && attribute_operator_expr_14_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &RP
  private static boolean attribute_operator_expr_14_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_14_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AMP &RP
  private static boolean attribute_operator_expr_21(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_21")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AMP);
    r = r && attribute_operator_expr_21_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &RP
  private static boolean attribute_operator_expr_21_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_21_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = consumeToken(b, RP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // AMP? LBT RBT
  private static boolean attribute_operator_expr_25(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_25")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_operator_expr_25_0(b, l + 1);
    r = r && consumeTokens(b, 0, LBT, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // AMP?
  private static boolean attribute_operator_expr_25_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_operator_expr_25_0")) return false;
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
  // AT_TYPE_IDENT
  public static boolean attribute_user_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_user_name")) return false;
    if (!nextTokenIs(b, AT_TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT_TYPE_IDENT);
    exit_section_(b, m, ATTRIBUTE_USER_NAME, r);
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
  //     | KW_FAULT
  //     | KW_ANY
  //     | KW_TYPEID
  //     | struct_type
  //     | CT_TYPE_IDENT
  //     | KW_CT_TYPEOF grouped_expression
  //     | KW_CT_TYPEFROM const_paren_expr
  //     | KW_CT_VATYPE LBT expr RBT
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
    if (!r) r = consumeToken(b, KW_FAULT);
    if (!r) r = consumeToken(b, KW_ANY);
    if (!r) r = consumeToken(b, KW_TYPEID);
    if (!r) r = struct_type(b, l + 1);
    if (!r) r = consumeToken(b, CT_TYPE_IDENT);
    if (!r) r = base_type_13(b, l + 1);
    if (!r) r = base_type_14(b, l + 1);
    if (!r) r = base_type_15(b, l + 1);
    if (!r) r = base_type_16(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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

  // KW_CT_VATYPE LBT expr RBT
  private static boolean base_type_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_type_15")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, KW_CT_VATYPE, LBT);
    r = r && expr(b, l + 1, -1);
    r = r && consumeToken(b, RBT);
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
  // KW_BITSTRUCT type_name interface_impl? COLON type attributes? bitstruct_body
  public static boolean bitstruct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_declaration")) return false;
    if (!nextTokenIs(b, KW_BITSTRUCT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BITSTRUCT_DECLARATION, null);
    r = consumeToken(b, KW_BITSTRUCT);
    r = r && type_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, bitstruct_declaration_2(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, type(b, l + 1)) && r;
    r = p && report_error_(b, bitstruct_declaration_5(b, l + 1)) && r;
    r = p && bitstruct_body(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interface_impl?
  private static boolean bitstruct_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_declaration_2")) return false;
    interface_impl(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean bitstruct_declaration_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitstruct_declaration_5")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BREAK_STMT, null);
    r = consumeToken(b, KW_BREAK);
    p = r; // pin = 1
    r = r && report_error_(b, break_stmt_1(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  //     | generic_parameters
  //     | dot_access_ident
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
    if (!r) r = generic_parameters(b, l + 1);
    if (!r) r = dot_access_ident(b, l + 1);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CASE_STMT, null);
    r = consumeToken(b, KW_CASE);
    p = r; // pin = 1
    r = r && report_error_(b, case_stmt_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && case_stmt_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // LB statement_list* RB
  public static boolean compound_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compound_statement")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPOUND_STATEMENT, null);
    r = consumeToken(b, LB);
    r = r && compound_statement_1(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, RB);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // statement_list*
  private static boolean compound_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compound_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!statement_list(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "compound_statement_1", c)) break;
    }
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
  // KW_CONST type? CONST_IDENT attributes? eq_expr_pin EOS
  public static boolean const_declaration_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration_stmt")) return false;
    if (!nextTokenIs(b, KW_CONST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONST_DECLARATION_STMT, null);
    r = consumeToken(b, KW_CONST);
    r = r && const_declaration_stmt_1(b, l + 1);
    r = r && consumeToken(b, CONST_IDENT);
    p = r; // pin = 3
    r = r && report_error_(b, const_declaration_stmt_3(b, l + 1));
    r = p && report_error_(b, eq_expr_pin(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // type?
  private static boolean const_declaration_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration_stmt_1")) return false;
    type(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean const_declaration_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_declaration_stmt_3")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONTINUE_STMT, null);
    r = consumeToken(b, KW_CONTINUE);
    p = r; // pin = 1
    r = r && report_error_(b, continue_stmt_1(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // CONST_IDENT?
  private static boolean continue_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "continue_stmt_1")) return false;
    consumeToken(b, CONST_IDENT);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_EVAL | KW_CT_SIZEOF | KW_CT_STRINGIFY | KW_CT_IS_CONST
  public static boolean ct_analyze(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_analyze")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ANALYZE, "<ct analyze>");
    r = consumeToken(b, KW_CT_EVAL);
    if (!r) r = consumeToken(b, KW_CT_SIZEOF);
    if (!r) r = consumeToken(b, KW_CT_STRINGIFY);
    if (!r) r = consumeToken(b, KW_CT_IS_CONST);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_VACONST | KW_CT_VAARG | KW_CT_VAEXPR
  public static boolean ct_arg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_arg")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_ARG, "<ct arg>");
    r = consumeToken(b, KW_CT_VACONST);
    if (!r) r = consumeToken(b, KW_CT_VAARG);
    if (!r) r = consumeToken(b, KW_CT_VAEXPR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_ASSERT constant_expr (COLON constant_expr)? EOS
  public static boolean ct_assert_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_ASSERT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_ASSERT_STMT, null);
    r = consumeToken(b, KW_CT_ASSERT);
    p = r; // pin = 1
    r = r && report_error_(b, constant_expr(b, l + 1));
    r = p && report_error_(b, ct_assert_stmt_2(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COLON constant_expr)?
  private static boolean ct_assert_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt_2")) return false;
    ct_assert_stmt_2_0(b, l + 1);
    return true;
  }

  // COLON constant_expr
  private static boolean ct_assert_stmt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_assert_stmt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && constant_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_ALIGNOF | KW_CT_EXTNAMEOF | KW_CT_NAMEOF | KW_CT_OFFSETOF | KW_CT_QNAMEOF
  public static boolean ct_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_CALL, "<ct call>");
    r = consumeToken(b, KW_CT_ALIGNOF);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_CASE_STMT, "<ct case stmt>");
    r = ct_case_stmt_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && ct_case_stmt_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // expr | type
  public static boolean ct_defined_check_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_defined_check_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, CT_DEFINED_CHECK_EXPR, "<ct defined check expr>");
    r = expr(b, l + 1, -1);
    if (!r) r = type(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ct_defined_check_expr (COMMA ct_defined_check_expr)*
  public static boolean ct_defined_check_expr_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_defined_check_expr_list")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CT_DEFINED_CHECK_EXPR_LIST, "<ct defined check expr list>");
    r = ct_defined_check_expr(b, l + 1);
    r = r && ct_defined_check_expr_list_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA ct_defined_check_expr)*
  private static boolean ct_defined_check_expr_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_defined_check_expr_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ct_defined_check_expr_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ct_defined_check_expr_list_1", c)) break;
    }
    return true;
  }

  // COMMA ct_defined_check_expr
  private static boolean ct_defined_check_expr_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_defined_check_expr_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ct_defined_check_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_CT_ECHO constant_expr EOS
  public static boolean ct_echo_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_echo_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_ECHO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_ECHO_STMT, null);
    r = consumeToken(b, KW_CT_ECHO);
    p = r; // pin = 1
    r = r && report_error_(b, constant_expr(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KW_CT_ERROR constant_expr EOS
  public static boolean ct_error_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_error_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_ERROR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_ERROR_STMT, null);
    r = consumeToken(b, KW_CT_ERROR);
    p = r; // pin = 1
    r = r && report_error_(b, constant_expr(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KW_CT_FOR for_cond COLON statement_list? KW_CT_ENDFOR
  public static boolean ct_for_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_for_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_FOR_STMT, null);
    r = consumeToken(b, KW_CT_FOR);
    p = r; // pin = 1
    r = r && report_error_(b, for_cond(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, ct_for_stmt_3(b, l + 1)) && r;
    r = p && consumeToken(b, KW_CT_ENDFOR) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // statement_list?
  private static boolean ct_for_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_for_stmt_3")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_FOREACH CT_IDENT (COMMA CT_IDENT)? COLON expr COLON statement_list? KW_CT_ENDFOREACH
  public static boolean ct_foreach_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_FOREACH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_FOREACH_STMT, null);
    r = consumeTokens(b, 1, KW_CT_FOREACH, CT_IDENT);
    p = r; // pin = 1
    r = r && report_error_(b, ct_foreach_stmt_2(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, expr(b, l + 1, -1)) && r;
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, ct_foreach_stmt_6(b, l + 1)) && r;
    r = p && consumeToken(b, KW_CT_ENDFOREACH) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA CT_IDENT)?
  private static boolean ct_foreach_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_2")) return false;
    ct_foreach_stmt_2_0(b, l + 1);
    return true;
  }

  // COMMA CT_IDENT
  private static boolean ct_foreach_stmt_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, CT_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement_list?
  private static boolean ct_foreach_stmt_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_foreach_stmt_6")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_IF constant_expr COLON statement_list? (KW_CT_ELSE statement_list?)? KW_CT_ENDIF
  public static boolean ct_if_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_IF_STMT, null);
    r = consumeToken(b, KW_CT_IF);
    p = r; // pin = 1
    r = r && report_error_(b, constant_expr(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, ct_if_stmt_3(b, l + 1)) && r;
    r = p && report_error_(b, ct_if_stmt_4(b, l + 1)) && r;
    r = p && consumeToken(b, KW_CT_ENDIF) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // statement_list?
  private static boolean ct_if_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_3")) return false;
    statement_list(b, l + 1);
    return true;
  }

  // (KW_CT_ELSE statement_list?)?
  private static boolean ct_if_stmt_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_4")) return false;
    ct_if_stmt_4_0(b, l + 1);
    return true;
  }

  // KW_CT_ELSE statement_list?
  private static boolean ct_if_stmt_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_ELSE);
    r = r && ct_if_stmt_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement_list?
  private static boolean ct_if_stmt_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_if_stmt_4_0_1")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_INCLUDE string_expr attributes? EOS
  public static boolean ct_include_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_include_stmt")) return false;
    if (!nextTokenIs(b, KW_CT_INCLUDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_INCLUDE_STMT, null);
    r = consumeToken(b, KW_CT_INCLUDE);
    p = r; // pin = 1
    r = r && report_error_(b, string_expr(b, l + 1));
    r = p && report_error_(b, ct_include_stmt_2(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean ct_include_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_include_stmt_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_SWITCH (constant_expr | type)? COLON
  public static boolean ct_switch(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch")) return false;
    if (!nextTokenIs(b, KW_CT_SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CT_SWITCH);
    r = r && ct_switch_1(b, l + 1);
    r = r && consumeToken(b, COLON);
    exit_section_(b, m, CT_SWITCH, r);
    return r;
  }

  // (constant_expr | type)?
  private static boolean ct_switch_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_1")) return false;
    ct_switch_1_0(b, l + 1);
    return true;
  }

  // constant_expr | type
  private static boolean ct_switch_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_switch_1_0")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CT_SWITCH_STMT, null);
    r = ct_switch(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, ct_switch_body(b, l + 1));
    r = p && consumeToken(b, KW_CT_ENDSWITCH) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DECL_STMT_AFTER_TYPE, "<decl stmt after type>");
    r = local_decl_after_type(b, l + 1);
    p = r; // pin = 1
    r = r && decl_stmt_after_type_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // attribute (COMMA attribute)* COMMA?
  public static boolean def_attr_values(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def_attr_values")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEF_ATTR_VALUES, "<def attr values>");
    r = attribute(b, l + 1);
    r = r && def_attr_values_1(b, l + 1);
    r = r && def_attr_values_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA attribute)*
  private static boolean def_attr_values_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def_attr_values_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!def_attr_values_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "def_attr_values_1", c)) break;
    }
    return true;
  }

  // COMMA attribute
  private static boolean def_attr_values_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def_attr_values_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && attribute(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean def_attr_values_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "def_attr_values_2")) return false;
    consumeToken(b, COMMA);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DEFAULT_STMT, null);
    r = consumeTokens(b, 1, KW_DEFAULT, COLON);
    p = r; // pin = 1
    r = r && default_stmt_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // statement_list?
  private static boolean default_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "default_stmt_2")) return false;
    statement_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_DEFER (KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP))? statement
  public static boolean defer_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt")) return false;
    if (!nextTokenIs(b, KW_DEFER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DEFER_STMT, null);
    r = consumeToken(b, KW_DEFER);
    p = r; // pin = 1
    r = r && report_error_(b, defer_stmt_1(b, l + 1));
    r = p && statement(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP))?
  private static boolean defer_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt_1")) return false;
    defer_stmt_1_0(b, l + 1);
    return true;
  }

  // KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP)
  private static boolean defer_stmt_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_TRY);
    if (!r) r = consumeToken(b, KW_CATCH);
    if (!r) r = defer_stmt_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LP KW_CATCH IDENT RP
  private static boolean defer_stmt_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defer_stmt_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LP, KW_CATCH, IDENT, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_DO label? compound_statement (KW_WHILE grouped_expression)? EOS
  public static boolean do_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "do_stmt")) return false;
    if (!nextTokenIs(b, KW_DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_STMT, null);
    r = consumeToken(b, KW_DO);
    p = r; // pin = 1
    r = r && report_error_(b, do_stmt_1(b, l + 1));
    r = p && report_error_(b, compound_statement(b, l + 1)) && r;
    r = p && report_error_(b, do_stmt_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // DOT access_ident
  static boolean dot_access_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dot_access_ident")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && access_ident(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // CONST_IDENT attributes? [eq_expr_pin]
  public static boolean enum_constant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST_IDENT);
    r = r && enum_constant_1(b, l + 1);
    r = r && enum_constant_2(b, l + 1);
    exit_section_(b, m, ENUM_CONSTANT, r);
    return r;
  }

  // attributes?
  private static boolean enum_constant_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // [eq_expr_pin]
  private static boolean enum_constant_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_constant_2")) return false;
    eq_expr_pin(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_ENUM type_name interface_impl? (COLON enum_spec)? attributes? LB enum_list RB
  public static boolean enum_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration")) return false;
    if (!nextTokenIs(b, KW_ENUM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ENUM_DECLARATION, null);
    r = consumeToken(b, KW_ENUM);
    r = r && type_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, enum_declaration_2(b, l + 1));
    r = p && report_error_(b, enum_declaration_3(b, l + 1)) && r;
    r = p && report_error_(b, enum_declaration_4(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LB)) && r;
    r = p && report_error_(b, enum_list(b, l + 1)) && r;
    r = p && consumeToken(b, RB) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interface_impl?
  private static boolean enum_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_2")) return false;
    interface_impl(b, l + 1);
    return true;
  }

  // (COLON enum_spec)?
  private static boolean enum_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_3")) return false;
    enum_declaration_3_0(b, l + 1);
    return true;
  }

  // COLON enum_spec
  private static boolean enum_declaration_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && enum_spec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean enum_declaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_4")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // enum_constant (COMMA enum_constant ?)* COMMA?
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

  // (COMMA enum_constant ?)*
  private static boolean enum_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enum_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enum_list_1", c)) break;
    }
    return true;
  }

  // COMMA enum_constant ?
  private static boolean enum_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enum_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // enum_constant ?
  private static boolean enum_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_1_0_1")) return false;
    enum_constant(b, l + 1);
    return true;
  }

  // COMMA?
  private static boolean enum_list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_list_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // type IDENT
  public static boolean enum_param_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_param_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_PARAM_DECL, "<enum param decl>");
    r = type(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, l, m, r, false, null);
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
  // (KW_CONST KW_INLINE? type?) | enum_param_list | KW_INLINE? type enum_param_list?
  public static boolean enum_spec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_SPEC, "<enum spec>");
    r = enum_spec_0(b, l + 1);
    if (!r) r = enum_param_list(b, l + 1);
    if (!r) r = enum_spec_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_CONST KW_INLINE? type?
  private static boolean enum_spec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_CONST);
    r = r && enum_spec_0_1(b, l + 1);
    r = r && enum_spec_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_INLINE?
  private static boolean enum_spec_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_0_1")) return false;
    consumeToken(b, KW_INLINE);
    return true;
  }

  // type?
  private static boolean enum_spec_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_0_2")) return false;
    type(b, l + 1);
    return true;
  }

  // KW_INLINE? type enum_param_list?
  private static boolean enum_spec_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enum_spec_2_0(b, l + 1);
    r = r && type(b, l + 1);
    r = r && enum_spec_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_INLINE?
  private static boolean enum_spec_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_2_0")) return false;
    consumeToken(b, KW_INLINE);
    return true;
  }

  // enum_param_list?
  private static boolean enum_spec_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_spec_2_2")) return false;
    enum_param_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EQ expr
  static boolean eq_expr_pin(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eq_expr_pin")) return false;
    if (!nextTokenIs(b, EQ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EQ);
    p = r; // pin = 1
    r = r && expr(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // expr EOS
  public static boolean expr_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_stmt")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPR_STMT, "<expr stmt>");
    r = expr(b, l + 1, -1);
    p = r; // pin = 1
    r = r && consumeToken(b, EOS);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // CONST_IDENT attributes?
  public static boolean fault_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_definition")) return false;
    if (!nextTokenIs(b, CONST_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST_IDENT);
    r = r && fault_definition_1(b, l + 1);
    exit_section_(b, m, FAULT_DEFINITION, r);
    return r;
  }

  // attributes?
  private static boolean fault_definition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fault_definition_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FAULTDEF fault_definition (COMMA fault_definition)* COMMA? EOS
  public static boolean faultdef_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "faultdef_decl")) return false;
    if (!nextTokenIs(b, KW_FAULTDEF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FAULTDEF_DECL, null);
    r = consumeToken(b, KW_FAULTDEF);
    r = r && fault_definition(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, faultdef_decl_2(b, l + 1));
    r = p && report_error_(b, faultdef_decl_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COMMA fault_definition)*
  private static boolean faultdef_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "faultdef_decl_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!faultdef_decl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "faultdef_decl_2", c)) break;
    }
    return true;
  }

  // COMMA fault_definition
  private static boolean faultdef_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "faultdef_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && fault_definition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean faultdef_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "faultdef_decl_3")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_STMT, null);
    r = consumeToken(b, KW_FOR);
    p = r; // pin = 1
    r = r && report_error_(b, for_stmt_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LP)) && r;
    r = p && report_error_(b, for_cond(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, RP)) && r;
    r = p && statement(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOREACH_STMT, "<foreach stmt>");
    r = foreach_stmt_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, foreach_stmt_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LP)) && r;
    r = p && report_error_(b, foreach_vars(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && report_error_(b, expr(b, l + 1, -1)) && r;
    r = p && report_error_(b, consumeToken(b, RP)) && r;
    r = p && statement(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // KW_FN func_header fn_parameter_list attributes?
  public static boolean func_def(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_def")) return false;
    if (!nextTokenIs(b, KW_FN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_FN);
    r = r && func_header(b, l + 1);
    r = r && fn_parameter_list(b, l + 1);
    r = r && func_def_3(b, l + 1);
    exit_section_(b, m, FUNC_DEF, r);
    return r;
  }

  // attributes?
  private static boolean func_def_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_def_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // func_def (macro_func_body | EOS)
  public static boolean func_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_definition")) return false;
    if (!nextTokenIs(b, KW_FN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FUNC_DEFINITION, null);
    r = func_def(b, l + 1);
    p = r; // pin = 1
    r = r && func_definition_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // macro_func_body | EOS
  private static boolean func_definition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "func_definition_1")) return false;
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
  // LB generic_parameter (COMMA generic_parameter)* RB
  public static boolean generic_parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameters")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && generic_parameter(b, l + 1);
    r = r && generic_parameters_2(b, l + 1);
    r = r && consumeToken(b, RB);
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
  public static boolean global_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_decl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_DECL, "<global decl>");
    r = global_decl_0(b, l + 1);
    r = r && optional_type(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, consumeToken(b, IDENT));
    r = p && report_error_(b, global_decl_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // KW_TLOCAL?
  private static boolean global_decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_decl_0")) return false;
    consumeToken(b, KW_TLOCAL);
    return true;
  }

  // global_multi_declaration | global_single_declaration
  private static boolean global_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_decl_3")) return false;
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
  // attributes? [eq_expr_pin]
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

  // [eq_expr_pin]
  private static boolean global_single_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_single_declaration_1")) return false;
    eq_expr_pin(b, l + 1);
    return true;
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
  // IDENT | TYPE_IDENT | CT_TYPE_IDENT | CT_IDENT | CONST_IDENT | AT_IDENT
  static boolean ident_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ident_list")) return false;
    boolean r;
    r = consumeToken(b, IDENT);
    if (!r) r = consumeToken(b, TYPE_IDENT);
    if (!r) r = consumeToken(b, CT_TYPE_IDENT);
    if (!r) r = consumeToken(b, CT_IDENT);
    if (!r) r = consumeToken(b, CONST_IDENT);
    if (!r) r = consumeToken(b, AT_IDENT);
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
  // KW_IF label? paren_cond (compound_statement else_part | statement)
  public static boolean if_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt")) return false;
    if (!nextTokenIs(b, KW_IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_STMT, null);
    r = consumeToken(b, KW_IF);
    p = r; // pin = 1
    r = r && report_error_(b, if_stmt_1(b, l + 1));
    r = p && report_error_(b, paren_cond(b, l + 1)) && r;
    r = p && if_stmt_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // label?
  private static boolean if_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_1")) return false;
    label(b, l + 1);
    return true;
  }

  // compound_statement else_part | statement
  private static boolean if_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_stmt_3_0(b, l + 1);
    if (!r) r = statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // compound_statement else_part
  private static boolean if_stmt_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_stmt_3_0")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_DECL, null);
    r = consumeToken(b, KW_IMPORT);
    r = r && import_paths(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, import_decl_2(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean import_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_decl_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENT (SCOPE IDENT)*
  public static boolean import_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_path")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_PATH, null);
    r = consumeToken(b, IDENT);
    p = r; // pin = 1
    r = r && import_path_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (SCOPE IDENT)*
  private static boolean import_path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_path_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!import_path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "import_path_1", c)) break;
    }
    return true;
  }

  // SCOPE IDENT
  private static boolean import_path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_path_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, SCOPE, IDENT);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // import_path (COMMA import_path)*
  public static boolean import_paths(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = import_path(b, l + 1);
    r = r && import_paths_1(b, l + 1);
    exit_section_(b, m, IMPORT_PATHS, r);
    return r;
  }

  // (COMMA import_path)*
  private static boolean import_paths_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!import_paths_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "import_paths_1", c)) break;
    }
    return true;
  }

  // COMMA import_path
  private static boolean import_paths_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_paths_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && import_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LB (arg_list COMMA?)? RB
  public static boolean initializer_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_list")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INITIALIZER_LIST, null);
    r = consumeToken(b, LB);
    r = r && initializer_list_1(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, RB);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // LB (func_def EOS)* RB
  public static boolean interface_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_body")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && interface_body_1(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, INTERFACE_BODY, r);
    return r;
  }

  // (func_def EOS)*
  private static boolean interface_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interface_body_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interface_body_1", c)) break;
    }
    return true;
  }

  // func_def EOS
  private static boolean interface_body_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_body_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = func_def(b, l + 1);
    r = r && consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_INTERFACE type_name (COLON type (COMMA type)*)? interface_body
  public static boolean interface_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_definition")) return false;
    if (!nextTokenIs(b, KW_INTERFACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_INTERFACE);
    r = r && type_name(b, l + 1);
    r = r && interface_definition_2(b, l + 1);
    r = r && interface_body(b, l + 1);
    exit_section_(b, m, INTERFACE_DEFINITION, r);
    return r;
  }

  // (COLON type (COMMA type)*)?
  private static boolean interface_definition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_definition_2")) return false;
    interface_definition_2_0(b, l + 1);
    return true;
  }

  // COLON type (COMMA type)*
  private static boolean interface_definition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_definition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type(b, l + 1);
    r = r && interface_definition_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA type)*
  private static boolean interface_definition_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_definition_2_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interface_definition_2_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interface_definition_2_0_2", c)) break;
    }
    return true;
  }

  // COMMA type
  private static boolean interface_definition_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_definition_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LP type_name (COMMA type)* RP
  public static boolean interface_impl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_impl")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && type_name(b, l + 1);
    r = r && interface_impl_2(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, INTERFACE_IMPL, r);
    return r;
  }

  // (COMMA type)*
  private static boolean interface_impl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_impl_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!interface_impl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "interface_impl_2", c)) break;
    }
    return true;
  }

  // COMMA type
  private static boolean interface_impl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "interface_impl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KW_ALIAS | KW_ANY | KW_ATTRDEF | KW_ASM | KW_ASSERT
  //     | KW_BITSTRUCT | KW_BREAK | KW_CASE | KW_CATCH | KW_CONST | KW_CONTINUE
  //     | KW_DEFAULT | KW_DEFER | KW_DO | KW_ELSE | KW_ENUM | KW_EXTERN | KW_FOREACH
  //     | KW_FOREACH_R | KW_FALSE | KW_FAULT | KW_FAULTDEF | KW_FOR | KW_FN | KW_IF
  //     | KW_INLINE | KW_INTERFACE | KW_IMPORT | KW_MACRO | KW_MODULE | KW_NEXTCASE
  //     | KW_NULL | KW_RETURN | KW_STATIC | KW_STRUCT | KW_SWITCH | KW_TLOCAL | KW_TRUE
  //     | KW_TRY | KW_TYPEDEF | KW_TYPEID | KW_UNION | KW_VAR | KW_WHILE | KW_CT_ALIGNOF
  //     | KW_CT_ASSERT | KW_CT_CASE | KW_CT_DEFAULT | KW_CT_DEFINED | KW_CT_ECHO
  //     | KW_CT_ELSE | KW_CT_ENDFOR | KW_CT_ENDFOREACH | KW_CT_ENDIF | KW_CT_ENDSWITCH
  //     | KW_CT_ERROR | KW_CT_EVAL | KW_CT_EVALTYPE | KW_CT_EXTNAMEOF | KW_CT_FEATURE
  //     | KW_CT_FOR | KW_CT_FOREACH | KW_CT_IF | KW_CT_IS_CONST | KW_CT_INCLUDE
  //     | KW_CT_NAMEOF | KW_CT_SIZEOF | KW_CT_STRINGIFY | KW_CT_SWITCH | KW_CT_TYPEOF
  //     | KW_CT_TYPEFROM | KW_CT_QNAMEOF | KW_CT_VACOUNT | KW_CT_VACONST | KW_CT_VATYPE
  //     | KW_CT_VAARG | KW_CT_VAREF | KW_CT_VAEXPR | KW_CT_VASPLAT | KW_VOID | KW_BOOL
  //     | KW_CHAR | KW_ICHAR | KW_SHORT | KW_USHORT | KW_INT | KW_UINT | KW_LONG
  //     | KW_ULONG | KW_UINT128 | KW_INT128 | KW_BFLOAT16 | KW_DOUBLE | KW_FLOAT | KW_FLOAT16
  //     | KW_FLOAT128 | KW_UPTR | KW_IPTR | KW_USZ | KW_ISZ
  static boolean keyword_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_list")) return false;
    boolean r;
    r = consumeToken(b, KW_ALIAS);
    if (!r) r = consumeToken(b, KW_ANY);
    if (!r) r = consumeToken(b, KW_ATTRDEF);
    if (!r) r = consumeToken(b, KW_ASM);
    if (!r) r = consumeToken(b, KW_ASSERT);
    if (!r) r = consumeToken(b, KW_BITSTRUCT);
    if (!r) r = consumeToken(b, KW_BREAK);
    if (!r) r = consumeToken(b, KW_CASE);
    if (!r) r = consumeToken(b, KW_CATCH);
    if (!r) r = consumeToken(b, KW_CONST);
    if (!r) r = consumeToken(b, KW_CONTINUE);
    if (!r) r = consumeToken(b, KW_DEFAULT);
    if (!r) r = consumeToken(b, KW_DEFER);
    if (!r) r = consumeToken(b, KW_DO);
    if (!r) r = consumeToken(b, KW_ELSE);
    if (!r) r = consumeToken(b, KW_ENUM);
    if (!r) r = consumeToken(b, KW_EXTERN);
    if (!r) r = consumeToken(b, KW_FOREACH);
    if (!r) r = consumeToken(b, KW_FOREACH_R);
    if (!r) r = consumeToken(b, KW_FALSE);
    if (!r) r = consumeToken(b, KW_FAULT);
    if (!r) r = consumeToken(b, KW_FAULTDEF);
    if (!r) r = consumeToken(b, KW_FOR);
    if (!r) r = consumeToken(b, KW_FN);
    if (!r) r = consumeToken(b, KW_IF);
    if (!r) r = consumeToken(b, KW_INLINE);
    if (!r) r = consumeToken(b, KW_INTERFACE);
    if (!r) r = consumeToken(b, KW_IMPORT);
    if (!r) r = consumeToken(b, KW_MACRO);
    if (!r) r = consumeToken(b, KW_MODULE);
    if (!r) r = consumeToken(b, KW_NEXTCASE);
    if (!r) r = consumeToken(b, KW_NULL);
    if (!r) r = consumeToken(b, KW_RETURN);
    if (!r) r = consumeToken(b, KW_STATIC);
    if (!r) r = consumeToken(b, KW_STRUCT);
    if (!r) r = consumeToken(b, KW_SWITCH);
    if (!r) r = consumeToken(b, KW_TLOCAL);
    if (!r) r = consumeToken(b, KW_TRUE);
    if (!r) r = consumeToken(b, KW_TRY);
    if (!r) r = consumeToken(b, KW_TYPEDEF);
    if (!r) r = consumeToken(b, KW_TYPEID);
    if (!r) r = consumeToken(b, KW_UNION);
    if (!r) r = consumeToken(b, KW_VAR);
    if (!r) r = consumeToken(b, KW_WHILE);
    if (!r) r = consumeToken(b, KW_CT_ALIGNOF);
    if (!r) r = consumeToken(b, KW_CT_ASSERT);
    if (!r) r = consumeToken(b, KW_CT_CASE);
    if (!r) r = consumeToken(b, KW_CT_DEFAULT);
    if (!r) r = consumeToken(b, KW_CT_DEFINED);
    if (!r) r = consumeToken(b, KW_CT_ECHO);
    if (!r) r = consumeToken(b, KW_CT_ELSE);
    if (!r) r = consumeToken(b, KW_CT_ENDFOR);
    if (!r) r = consumeToken(b, KW_CT_ENDFOREACH);
    if (!r) r = consumeToken(b, KW_CT_ENDIF);
    if (!r) r = consumeToken(b, KW_CT_ENDSWITCH);
    if (!r) r = consumeToken(b, KW_CT_ERROR);
    if (!r) r = consumeToken(b, KW_CT_EVAL);
    if (!r) r = consumeToken(b, KW_CT_EVALTYPE);
    if (!r) r = consumeToken(b, KW_CT_EXTNAMEOF);
    if (!r) r = consumeToken(b, KW_CT_FEATURE);
    if (!r) r = consumeToken(b, KW_CT_FOR);
    if (!r) r = consumeToken(b, KW_CT_FOREACH);
    if (!r) r = consumeToken(b, KW_CT_IF);
    if (!r) r = consumeToken(b, KW_CT_IS_CONST);
    if (!r) r = consumeToken(b, KW_CT_INCLUDE);
    if (!r) r = consumeToken(b, KW_CT_NAMEOF);
    if (!r) r = consumeToken(b, KW_CT_SIZEOF);
    if (!r) r = consumeToken(b, KW_CT_STRINGIFY);
    if (!r) r = consumeToken(b, KW_CT_SWITCH);
    if (!r) r = consumeToken(b, KW_CT_TYPEOF);
    if (!r) r = consumeToken(b, KW_CT_TYPEFROM);
    if (!r) r = consumeToken(b, KW_CT_QNAMEOF);
    if (!r) r = consumeToken(b, KW_CT_VACOUNT);
    if (!r) r = consumeToken(b, KW_CT_VACONST);
    if (!r) r = consumeToken(b, KW_CT_VATYPE);
    if (!r) r = consumeToken(b, KW_CT_VAARG);
    if (!r) r = consumeToken(b, KW_CT_VAREF);
    if (!r) r = consumeToken(b, KW_CT_VAEXPR);
    if (!r) r = consumeToken(b, KW_CT_VASPLAT);
    if (!r) r = consumeToken(b, KW_VOID);
    if (!r) r = consumeToken(b, KW_BOOL);
    if (!r) r = consumeToken(b, KW_CHAR);
    if (!r) r = consumeToken(b, KW_ICHAR);
    if (!r) r = consumeToken(b, KW_SHORT);
    if (!r) r = consumeToken(b, KW_USHORT);
    if (!r) r = consumeToken(b, KW_INT);
    if (!r) r = consumeToken(b, KW_UINT);
    if (!r) r = consumeToken(b, KW_LONG);
    if (!r) r = consumeToken(b, KW_ULONG);
    if (!r) r = consumeToken(b, KW_UINT128);
    if (!r) r = consumeToken(b, KW_INT128);
    if (!r) r = consumeToken(b, KW_BFLOAT16);
    if (!r) r = consumeToken(b, KW_DOUBLE);
    if (!r) r = consumeToken(b, KW_FLOAT);
    if (!r) r = consumeToken(b, KW_FLOAT16);
    if (!r) r = consumeToken(b, KW_FLOAT128);
    if (!r) r = consumeToken(b, KW_UPTR);
    if (!r) r = consumeToken(b, KW_IPTR);
    if (!r) r = consumeToken(b, KW_USZ);
    if (!r) r = consumeToken(b, KW_ISZ);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LAMBDA_DECL, null);
    r = consumeToken(b, KW_FN);
    r = r && lambda_decl_1(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, fn_parameter_list(b, l + 1));
    r = p && lambda_decl_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // local_decl_after_type_1 | local_decl_after_type_2
  public static boolean local_decl_after_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type")) return false;
    if (!nextTokenIs(b, "<local decl after type>", CT_IDENT, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_DECL_AFTER_TYPE, "<local decl after type>");
    r = local_decl_after_type_1(b, l + 1);
    if (!r) r = local_decl_after_type_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CT_IDENT (EQ constant_expr)?
  static boolean local_decl_after_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1")) return false;
    if (!nextTokenIs(b, CT_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_IDENT);
    r = r && local_decl_after_type_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ constant_expr)?
  private static boolean local_decl_after_type_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1_1")) return false;
    local_decl_after_type_1_1_0(b, l + 1);
    return true;
  }

  // EQ constant_expr
  private static boolean local_decl_after_type_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && constant_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENT attributes? (eq_expr_pin)?
  static boolean local_decl_after_type_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_2")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && local_decl_after_type_2_1(b, l + 1);
    r = r && local_decl_after_type_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean local_decl_after_type_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_2_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // (eq_expr_pin)?
  private static boolean local_decl_after_type_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_2_2")) return false;
    local_decl_after_type_2_2_0(b, l + 1);
    return true;
  }

  // (eq_expr_pin)
  private static boolean local_decl_after_type_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_decl_after_type_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = eq_expr_pin(b, l + 1);
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
  // local_decl_storage? optional_type decl_stmt_after_type EOS
  public static boolean local_declaration_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_declaration_stmt")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_DECLARATION_STMT, "<local declaration stmt>");
    r = local_declaration_stmt_0(b, l + 1);
    r = r && optional_type(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, decl_stmt_after_type(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // local_decl_storage?
  private static boolean local_declaration_stmt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_declaration_stmt_0")) return false;
    local_decl_storage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_MACRO macro_header LP macro_params RP attributes? macro_func_body
  public static boolean macro_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_definition")) return false;
    if (!nextTokenIs(b, KW_MACRO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KW_MACRO);
    r = r && macro_header(b, l + 1);
    r = r && consumeToken(b, LP);
    r = r && macro_params(b, l + 1);
    r = r && consumeToken(b, RP);
    r = r && macro_definition_5(b, l + 1);
    r = r && macro_func_body(b, l + 1);
    exit_section_(b, m, MACRO_DEFINITION, r);
    return r;
  }

  // attributes?
  private static boolean macro_definition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_definition_5")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // macro_implies_body | implies_body EOS | compound_statement
  public static boolean macro_func_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_func_body")) return false;
    if (!nextTokenIs(b, "<macro func body>", IMPLIES, LB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_FUNC_BODY, "<macro func body>");
    r = macro_implies_body(b, l + 1);
    if (!r) r = macro_func_body_1(b, l + 1);
    if (!r) r = compound_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // implies_body EOS
  private static boolean macro_func_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_func_body_1")) return false;
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
  // IMPLIES AT_IDENT call_invocation compound_statement
  public static boolean macro_implies_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_implies_body")) return false;
    if (!nextTokenIs(b, IMPLIES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IMPLIES, AT_IDENT);
    r = r && call_invocation(b, l + 1);
    r = r && compound_statement(b, l + 1);
    exit_section_(b, m, MACRO_IMPLIES_BODY, r);
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
  // KW_MODULE module_path (LB module_params RB)? attributes? EOS
  public static boolean module(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module")) return false;
    if (!nextTokenIs(b, KW_MODULE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MODULE, null);
    r = consumeToken(b, KW_MODULE);
    r = r && module_path(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, module_2(b, l + 1));
    r = p && report_error_(b, module_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (LB module_params RB)?
  private static boolean module_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_2")) return false;
    module_2_0(b, l + 1);
    return true;
  }

  // LB module_params RB
  private static boolean module_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && module_params(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean module_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_3")) return false;
    attributes(b, l + 1);
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
  // [(IDENT SCOPE)+] IDENT
  public static boolean module_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path")) return false;
    if (!nextTokenIs(b, IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    exit_section_(b, m, MODULE_PATH, r);
    return r;
  }

  // [(IDENT SCOPE)+]
  private static boolean module_path_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path_0")) return false;
    module_path_0_0(b, l + 1);
    return true;
  }

  // (IDENT SCOPE)+
  private static boolean module_path_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path_0_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!module_path_0_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "module_path_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENT SCOPE
  private static boolean module_path_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENT, SCOPE);
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
  // CT_IDENT | HASH_IDENT | IDENT | CT_TYPE_IDENT
  public static boolean named_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "named_ident")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMED_IDENT, "<named ident>");
    r = consumeToken(b, CT_IDENT);
    if (!r) r = consumeToken(b, HASH_IDENT);
    if (!r) r = consumeToken(b, IDENT);
    if (!r) r = consumeToken(b, CT_TYPE_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_NEXTCASE ((CONST_IDENT COLON)? (type | expr | KW_DEFAULT))? EOS
  public static boolean nextcase_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt")) return false;
    if (!nextTokenIs(b, KW_NEXTCASE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEXTCASE_STMT, null);
    r = consumeToken(b, KW_NEXTCASE);
    p = r; // pin = 1
    r = r && report_error_(b, nextcase_stmt_1(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ((CONST_IDENT COLON)? (type | expr | KW_DEFAULT))?
  private static boolean nextcase_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1")) return false;
    nextcase_stmt_1_0(b, l + 1);
    return true;
  }

  // (CONST_IDENT COLON)? (type | expr | KW_DEFAULT)
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

  // type | expr | KW_DEFAULT
  private static boolean nextcase_stmt_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nextcase_stmt_1_0_1")) return false;
    boolean r;
    r = type(b, l + 1);
    if (!r) r = expr(b, l + 1, -1);
    if (!r) r = consumeToken(b, KW_DEFAULT);
    return r;
  }

  /* ********************************************************** */
  // type QUESTION?
  public static boolean optional_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTIONAL_TYPE, "<optional type>");
    r = type(b, l + 1);
    r = r && optional_type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // QUESTION?
  private static boolean optional_type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_type_1")) return false;
    consumeToken(b, QUESTION);
    return true;
  }

  /* ********************************************************** */
  // parameter [eq_expr_pin]
  public static boolean param_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_DECL, "<param decl>");
    r = parameter(b, l + 1);
    r = r && param_decl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [eq_expr_pin]
  private static boolean param_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_decl_1")) return false;
    eq_expr_pin(b, l + 1);
    return true;
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
  // LBT expr (DOTDOT expr)? RBT | DOT primary_group | DOT CT_TYPE_IDENT
  public static boolean param_path_element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element")) return false;
    if (!nextTokenIs(b, "<param path element>", DOT, LBT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAM_PATH_ELEMENT, "<param path element>");
    r = param_path_element_0(b, l + 1);
    if (!r) r = param_path_element_1(b, l + 1);
    if (!r) r = parseTokens(b, 1, DOT, CT_TYPE_IDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBT expr (DOTDOT expr)? RBT
  private static boolean param_path_element_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBT);
    p = r; // pin = 1
    r = r && report_error_(b, expr(b, l + 1, -1));
    r = p && report_error_(b, param_path_element_0_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBT) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, DOTDOT);
    p = r; // pin = 1
    r = r && expr(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // DOT primary_group
  private static boolean param_path_element_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param_path_element_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, DOT);
    p = r; // pin = 1
    r = r && expr(b, l + 1, 11);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KW_INLINE? type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  //     | ELLIPSIS | HASH_IDENT attributes?| AMP IDENT attributes?  | IDENT ELLIPSIS? attributes?
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

  // KW_INLINE? type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  private static boolean parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_0(b, l + 1);
    r = r && type(b, l + 1);
    r = r && parameter_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // KW_INLINE?
  private static boolean parameter_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_0")) return false;
    consumeToken(b, KW_INLINE);
    return true;
  }

  // ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?
  private static boolean parameter_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_2_0(b, l + 1);
    if (!r) r = parameter_0_2_1(b, l + 1);
    if (!r) r = parameter_0_2_2(b, l + 1);
    if (!r) r = parameter_0_2_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS? IDENT attributes?
  private static boolean parameter_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_2_0_0(b, l + 1);
    r = r && consumeToken(b, IDENT);
    r = r && parameter_0_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS?
  private static boolean parameter_0_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_0_0")) return false;
    consumeToken(b, ELLIPSIS);
    return true;
  }

  // attributes?
  private static boolean parameter_0_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_0_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  // ELLIPSIS? CT_IDENT
  private static boolean parameter_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_2_1_0(b, l + 1);
    r = r && consumeToken(b, CT_IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ELLIPSIS?
  private static boolean parameter_0_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_1_0")) return false;
    consumeToken(b, ELLIPSIS);
    return true;
  }

  // (HASH_IDENT | AMP IDENT) attributes?
  private static boolean parameter_0_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_2_2_0(b, l + 1);
    r = r && parameter_0_2_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // HASH_IDENT | AMP IDENT
  private static boolean parameter_0_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH_IDENT);
    if (!r) r = parseTokens(b, 0, AMP, IDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_0_2_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_2_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean parameter_0_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // HASH_IDENT attributes?
  private static boolean parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH_IDENT);
    r = r && parameter_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_1")) return false;
    attributes(b, l + 1);
    return true;
  }

  // AMP IDENT attributes?
  private static boolean parameter_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AMP, IDENT);
    r = r && parameter_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attributes?
  private static boolean parameter_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_2")) return false;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_LIST, "<parameter list>");
    r = param_decl(b, l + 1);
    p = r; // pin = 1
    r = r && parameter_list_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && param_decl(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LP cond RP
  public static boolean paren_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_cond")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PAREN_COND, null);
    r = consumeToken(b, LP);
    r = r && cond(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, RP);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    r = consumeTokens(b, 2, IDENT, SCOPE);
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
  // <<parsePathIdent>>
  public static boolean path_ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "path_ident")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_IDENT, "<path ident>");
    r = parsePathIdent(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // !(statement|keyword_list|ident_list|RB|EOS)
  static boolean recover_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_statement_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // statement|keyword_list|ident_list|RB|EOS
  private static boolean recover_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = statement(b, l + 1);
    if (!r) r = keyword_list(b, l + 1);
    if (!r) r = ident_list(b, l + 1);
    if (!r) r = consumeToken(b, RB);
    if (!r) r = consumeToken(b, EOS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LT_OP !LBT | GT_OP | LE_OP | GE_OP | EQ_OP | NE_OP
  public static boolean rel_bin_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rel_bin_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BINARY_OP, "<operator>");
    r = rel_bin_op_0(b, l + 1);
    if (!r) r = consumeToken(b, GT_OP);
    if (!r) r = consumeToken(b, LE_OP);
    if (!r) r = consumeToken(b, GE_OP);
    if (!r) r = consumeToken(b, EQ_OP);
    if (!r) r = consumeToken(b, NE_OP);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LT_OP !LBT
  private static boolean rel_bin_op_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rel_bin_op_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT_OP);
    r = r && rel_bin_op_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !LBT
  private static boolean rel_bin_op_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rel_bin_op_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, LBT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KW_RETURN expr? EOS
  public static boolean return_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_stmt")) return false;
    if (!nextTokenIs(b, KW_RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RETURN_STMT, null);
    r = consumeToken(b, KW_RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, return_stmt_1(b, l + 1));
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  //     | const_declaration_stmt
  //     | expr_stmt
  //     | local_declaration_stmt
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
  //     | ct_error_stmt
  //     | ct_if_stmt
  //     | ct_switch_stmt
  //     | ct_foreach_stmt
  //     | ct_for_stmt
  //     | assert_stmt
  //     | EOS
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = compound_statement(b, l + 1);
    if (!r) r = var_stmt(b, l + 1);
    if (!r) r = const_declaration_stmt(b, l + 1);
    if (!r) r = expr_stmt(b, l + 1);
    if (!r) r = local_declaration_stmt(b, l + 1);
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
    if (!r) r = ct_error_stmt(b, l + 1);
    if (!r) r = ct_if_stmt(b, l + 1);
    if (!r) r = ct_switch_stmt(b, l + 1);
    if (!r) r = ct_foreach_stmt(b, l + 1);
    if (!r) r = ct_for_stmt(b, l + 1);
    if (!r) r = assert_stmt(b, l + 1);
    if (!r) r = consumeToken(b, EOS);
    exit_section_(b, l, m, r, false, C3Parser::recover_statement);
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
  // LB struct_member_declaration* RB
  public static boolean struct_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_body")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_BODY, null);
    r = consumeToken(b, LB);
    r = r && struct_body_1(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, RB);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // struct_or_union type_name interface_impl? attributes? struct_body
  public static boolean struct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration")) return false;
    if (!nextTokenIs(b, "<struct declaration>", KW_STRUCT, KW_UNION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_DECLARATION, "<struct declaration>");
    r = struct_or_union(b, l + 1);
    r = r && type_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, struct_declaration_2(b, l + 1));
    r = p && report_error_(b, struct_declaration_3(b, l + 1)) && r;
    r = p && struct_body(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interface_impl?
  private static boolean struct_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_2")) return false;
    interface_impl(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean struct_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // struct_member_declaration_1
  //                               | struct_member_declaration_2
  //                               | struct_member_declaration_3
  //                               | struct_member_declaration_4
  public static boolean struct_member_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_MEMBER_DECLARATION, "<struct member declaration>");
    r = struct_member_declaration_1(b, l + 1);
    if (!r) r = struct_member_declaration_2(b, l + 1);
    if (!r) r = struct_member_declaration_3(b, l + 1);
    if (!r) r = struct_member_declaration_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // type identifier_list attributes? EOS
  static boolean struct_member_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = type(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, identifier_list(b, l + 1));
    r = p && report_error_(b, struct_member_declaration_1_2(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // attributes?
  private static boolean struct_member_declaration_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_1_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_INLINE type IDENT? attributes? EOS
  static boolean struct_member_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2")) return false;
    if (!nextTokenIs(b, KW_INLINE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, KW_INLINE);
    r = r && type(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, struct_member_declaration_2_2(b, l + 1));
    r = p && report_error_(b, struct_member_declaration_2_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // IDENT?
  private static boolean struct_member_declaration_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2_2")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_2_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // struct_or_union IDENT? attributes? struct_body
  static boolean struct_member_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3")) return false;
    if (!nextTokenIs(b, "", KW_STRUCT, KW_UNION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = struct_or_union(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, struct_member_declaration_3_1(b, l + 1));
    r = p && report_error_(b, struct_member_declaration_3_2(b, l + 1)) && r;
    r = p && struct_body(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // IDENT?
  private static boolean struct_member_declaration_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3_1")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_3_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_BITSTRUCT IDENT? ':' type attributes? bitstruct_body
  static boolean struct_member_declaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_4")) return false;
    if (!nextTokenIs(b, KW_BITSTRUCT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, KW_BITSTRUCT);
    r = r && struct_member_declaration_4_1(b, l + 1);
    r = r && consumeToken(b, ":");
    r = r && type(b, l + 1);
    p = r; // pin = 4
    r = r && report_error_(b, struct_member_declaration_4_4(b, l + 1));
    r = p && bitstruct_body(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // IDENT?
  private static boolean struct_member_declaration_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_4_1")) return false;
    consumeToken(b, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_4_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_member_declaration_4_4")) return false;
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
  // path? TYPE_IDENT generic_parameters?
  static boolean struct_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_type")) return false;
    if (!nextTokenIs(b, "", IDENT, TYPE_IDENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = struct_type_0(b, l + 1);
    r = r && consumeToken(b, TYPE_IDENT);
    p = r; // pin = 2
    r = r && struct_type_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // path?
  private static boolean struct_type_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_type_0")) return false;
    path(b, l + 1);
    return true;
  }

  // generic_parameters?
  private static boolean struct_type_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_type_2")) return false;
    generic_parameters(b, l + 1);
    return true;
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
  // KW_SWITCH label? paren_cond? AT_IDENT? LB switch_body? RB
  public static boolean switch_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt")) return false;
    if (!nextTokenIs(b, KW_SWITCH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_STMT, null);
    r = consumeToken(b, KW_SWITCH);
    p = r; // pin = 1
    r = r && report_error_(b, switch_stmt_1(b, l + 1));
    r = p && report_error_(b, switch_stmt_2(b, l + 1)) && r;
    r = p && report_error_(b, switch_stmt_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, LB)) && r;
    r = p && report_error_(b, switch_stmt_5(b, l + 1)) && r;
    r = p && consumeToken(b, RB) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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

  // AT_IDENT?
  private static boolean switch_stmt_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_3")) return false;
    consumeToken(b, AT_IDENT);
    return true;
  }

  // switch_body?
  private static boolean switch_stmt_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_5")) return false;
    switch_body(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // import_decl
  //     | KW_EXTERN? (func_definition | const_declaration_stmt | global_decl)
  //     | ct_assert_stmt
  //     | ct_error_stmt
  //     | ct_echo_stmt
  //     | ct_include_stmt
  //     | type_decl
  //     | alias_decl
  //     | faultdef_decl
  //     | typedef_decl
  //     | attrdef_decl
  //     | alias_type_decl
  //     | macro_definition
  //     | asm_declaration
  //     | interface_definition
  public static boolean top_level(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TOP_LEVEL, "<top level>");
    r = import_decl(b, l + 1);
    if (!r) r = top_level_1(b, l + 1);
    if (!r) r = ct_assert_stmt(b, l + 1);
    if (!r) r = ct_error_stmt(b, l + 1);
    if (!r) r = ct_echo_stmt(b, l + 1);
    if (!r) r = ct_include_stmt(b, l + 1);
    if (!r) r = type_decl(b, l + 1);
    if (!r) r = alias_decl(b, l + 1);
    if (!r) r = faultdef_decl(b, l + 1);
    if (!r) r = typedef_decl(b, l + 1);
    if (!r) r = attrdef_decl(b, l + 1);
    if (!r) r = alias_type_decl(b, l + 1);
    if (!r) r = macro_definition(b, l + 1);
    if (!r) r = asm_declaration(b, l + 1);
    if (!r) r = interface_definition(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // KW_EXTERN? (func_definition | const_declaration_stmt | global_decl)
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

  // func_definition | const_declaration_stmt | global_decl
  private static boolean top_level_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "top_level_1_1")) return false;
    boolean r;
    r = func_definition(b, l + 1);
    if (!r) r = const_declaration_stmt(b, l + 1);
    if (!r) r = global_decl(b, l + 1);
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
  // struct_declaration
  //     | enum_declaration
  //     | bitstruct_declaration
  public static boolean type_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_DECL, "<type decl>");
    r = struct_declaration(b, l + 1);
    if (!r) r = enum_declaration(b, l + 1);
    if (!r) r = bitstruct_declaration(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // STAR | LBT PLUS RBT | LBT (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)? RBT | LVEC (STAR | constant_expr) RVEC
  public static boolean type_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_SUFFIX, "<type suffix>");
    r = consumeToken(b, STAR);
    if (!r) r = parseTokens(b, 0, LBT, PLUS, RBT);
    if (!r) r = type_suffix_2(b, l + 1);
    if (!r) r = type_suffix_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBT (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)? RBT
  private static boolean type_suffix_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBT);
    r = r && type_suffix_2_1(b, l + 1);
    r = r && consumeToken(b, RBT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)?
  private static boolean type_suffix_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_2_1")) return false;
    type_suffix_2_1_0(b, l + 1);
    return true;
  }

  // STAR | QUESTION | DIV | UNDERSCORE | constant_expr
  private static boolean type_suffix_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_2_1_0")) return false;
    boolean r;
    r = consumeToken(b, STAR);
    if (!r) r = consumeToken(b, QUESTION);
    if (!r) r = consumeToken(b, DIV);
    if (!r) r = consumeToken(b, UNDERSCORE);
    if (!r) r = constant_expr(b, l + 1);
    return r;
  }

  // LVEC (STAR | constant_expr) RVEC
  private static boolean type_suffix_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LVEC);
    r = r && type_suffix_3_1(b, l + 1);
    r = r && consumeToken(b, RVEC);
    exit_section_(b, m, null, r);
    return r;
  }

  // STAR | constant_expr
  private static boolean type_suffix_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_suffix_3_1")) return false;
    boolean r;
    r = consumeToken(b, STAR);
    if (!r) r = constant_expr(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // KW_TYPEDEF type_name interface_impl? attributes? EQ KW_INLINE? typedef_type EOS
  public static boolean typedef_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_decl")) return false;
    if (!nextTokenIs(b, KW_TYPEDEF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPEDEF_DECL, null);
    r = consumeToken(b, KW_TYPEDEF);
    r = r && type_name(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, typedef_decl_2(b, l + 1));
    r = p && report_error_(b, typedef_decl_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, EQ)) && r;
    r = p && report_error_(b, typedef_decl_5(b, l + 1)) && r;
    r = p && report_error_(b, typedef_type(b, l + 1)) && r;
    r = p && consumeToken(b, EOS) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // interface_impl?
  private static boolean typedef_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_decl_2")) return false;
    interface_impl(b, l + 1);
    return true;
  }

  // attributes?
  private static boolean typedef_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_decl_3")) return false;
    attributes(b, l + 1);
    return true;
  }

  // KW_INLINE?
  private static boolean typedef_decl_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typedef_decl_5")) return false;
    consumeToken(b, KW_INLINE);
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
  // KW_VAR (IDENT eq_expr_pin | CT_TYPE_IDENT eq_expr_pin? | CT_IDENT eq_expr_pin?)
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

  // IDENT eq_expr_pin | CT_TYPE_IDENT eq_expr_pin? | CT_IDENT eq_expr_pin?
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

  // IDENT eq_expr_pin
  private static boolean var_decl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENT);
    r = r && eq_expr_pin(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CT_TYPE_IDENT eq_expr_pin?
  private static boolean var_decl_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_TYPE_IDENT);
    r = r && var_decl_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // eq_expr_pin?
  private static boolean var_decl_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_1_1")) return false;
    eq_expr_pin(b, l + 1);
    return true;
  }

  // CT_IDENT eq_expr_pin?
  private static boolean var_decl_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CT_IDENT);
    r = r && var_decl_1_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // eq_expr_pin?
  private static boolean var_decl_1_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_decl_1_2_1")) return false;
    eq_expr_pin(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // var_decl EOS
  public static boolean var_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_stmt")) return false;
    if (!nextTokenIs(b, KW_VAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VAR_STMT, null);
    r = var_decl(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, EOS);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // KW_WHILE label? paren_cond statement
  public static boolean while_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_stmt")) return false;
    if (!nextTokenIs(b, KW_WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_STMT, null);
    r = consumeToken(b, KW_WHILE);
    p = r; // pin = 1
    r = r && report_error_(b, while_stmt_1(b, l + 1));
    r = p && report_error_(b, paren_cond(b, l + 1)) && r;
    r = p && statement(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // 0: BINARY(elvis_bin_expr) BINARY(optelse_bin_expr) BINARY(ternary_expr) POSTFIX(optional_expr)
  // 1: ATOM(assign_type_expr)
  // 2: BINARY(assign_bin_expr)
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
  //    ATOM(type_access_expr) ATOM(ct_call_expr) ATOM(ct_feature_expr) PREFIX(ct_arg_expr)
  //    ATOM(ct_analyze_expr) ATOM(ct_defined_expr) ATOM(lambda_decl_expr) PREFIX(lambda_decl_short_expr)
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
    if (!r) r = ct_call_expr(b, l + 1);
    if (!r) r = ct_feature_expr(b, l + 1);
    if (!r) r = ct_arg_expr(b, l + 1);
    if (!r) r = ct_analyze_expr(b, l + 1);
    if (!r) r = ct_defined_expr(b, l + 1);
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
      if (g < 0 && consumeTokenSmart(b, ELVIS)) {
        r = expr(b, l, -1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 0 && consumeTokenSmart(b, OPTELSE)) {
        r = expr(b, l, -1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 0 && ternary_expr_0(b, l + 1)) {
        r = report_error_(b, expr(b, l, -1));
        r = ternary_expr_1(b, l + 1) && r;
        exit_section_(b, l, m, TERNARY_EXPR, r, true, null);
      }
      else if (g < 0 && optional_expr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, OPTIONAL_EXPR, r, true, null);
      }
      else if (g < 2 && assign_bin_op(b, l + 1)) {
        r = expr(b, l, 1);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 3 && or_bin_expr_0(b, l + 1)) {
        r = expr(b, l, 3);
        exit_section_(b, l, m, BINARY_EXPR, r, true, null);
      }
      else if (g < 4 && and_bin_expr_0(b, l + 1)) {
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

  // QUESTION !((BANGBANG | BANG)? expr_terminator)
  private static boolean ternary_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, QUESTION);
    r = r && ternary_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !((BANGBANG | BANG)? expr_terminator)
  private static boolean ternary_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !ternary_expr_0_1_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (BANGBANG | BANG)? expr_terminator
  private static boolean ternary_expr_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ternary_expr_0_1_0_0(b, l + 1);
    r = r && expr_terminator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BANGBANG | BANG)?
  private static boolean ternary_expr_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1_0_0")) return false;
    ternary_expr_0_1_0_0_0(b, l + 1);
    return true;
  }

  // BANGBANG | BANG
  private static boolean ternary_expr_0_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ternary_expr_0_1_0_0_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, BANGBANG);
    if (!r) r = consumeTokenSmart(b, BANG);
    return r;
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

  // QUESTION &((BANGBANG | BANG)? expr_terminator)
  private static boolean optional_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, QUESTION);
    r = r && optional_expr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &((BANGBANG | BANG)? expr_terminator)
  private static boolean optional_expr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = optional_expr_0_1_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (BANGBANG | BANG)? expr_terminator
  private static boolean optional_expr_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = optional_expr_0_1_0_0(b, l + 1);
    r = r && expr_terminator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BANGBANG | BANG)?
  private static boolean optional_expr_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1_0_0")) return false;
    optional_expr_0_1_0_0_0(b, l + 1);
    return true;
  }

  // BANGBANG | BANG
  private static boolean optional_expr_0_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optional_expr_0_1_0_0_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, BANGBANG);
    if (!r) r = consumeTokenSmart(b, BANG);
    return r;
  }

  // CT_TYPE_IDENT /*EQ type*/eq_expr_pin
  public static boolean assign_type_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_type_expr")) return false;
    if (!nextTokenIsSmart(b, CT_TYPE_IDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, CT_TYPE_IDENT);
    r = r && eq_expr_pin(b, l + 1);
    exit_section_(b, m, ASSIGN_TYPE_EXPR, r);
    return r;
  }

  // OR | CT_OR
  private static boolean or_bin_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "or_bin_expr_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, OR);
    if (!r) r = consumeTokenSmart(b, CT_OR);
    return r;
  }

  // AND | CT_AND
  private static boolean and_bin_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "and_bin_expr_0")) return false;
    boolean r;
    r = consumeTokenSmart(b, AND);
    if (!r) r = consumeTokenSmart(b, CT_AND);
    return r;
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
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATH_IDENT_EXPR, "<path ident expr>");
    r = path_ident(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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

  // BYTES+
  public static boolean bytes_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bytes_expr")) return false;
    if (!nextTokenIsSmart(b, BYTES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, BYTES);
    while (r) {
      int c = current_position_(b);
      if (!consumeTokenSmart(b, BYTES)) break;
      if (!empty_element_parsed_guard_(b, "bytes_expr", c)) break;
    }
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

  // KW_CT_FEATURE LP CONST_IDENT RP
  public static boolean ct_feature_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_feature_expr")) return false;
    if (!nextTokenIsSmart(b, KW_CT_FEATURE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, KW_CT_FEATURE, LP, CONST_IDENT, RP);
    exit_section_(b, m, CT_FEATURE_EXPR, r);
    return r;
  }

  public static boolean ct_arg_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_arg_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ct_arg_expr_0(b, l + 1);
    p = r;
    r = p && expr(b, l, -1);
    r = p && report_error_(b, consumeToken(b, RBT)) && r;
    exit_section_(b, l, m, CT_ARG_EXPR, r, p, null);
    return r || p;
  }

  // ct_arg LBT
  private static boolean ct_arg_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_arg_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ct_arg(b, l + 1);
    r = r && consumeToken(b, LBT);
    exit_section_(b, m, null, r);
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

  // KW_CT_DEFINED LP ct_defined_check_expr_list RP
  public static boolean ct_defined_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ct_defined_expr")) return false;
    if (!nextTokenIsSmart(b, KW_CT_DEFINED)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, KW_CT_DEFINED, LP);
    r = r && ct_defined_check_expr_list(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, CT_DEFINED_EXPR, r);
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
