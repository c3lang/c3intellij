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

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return translation_unit(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ASM_EXPR, ASSIGN_TYPE_EXPR, ATTRIBUTE_OPERATOR_EXPR, BINARY_EXPR,
      BUILTIN_CONST_EXPR, BUILTIN_EXPR, BYTES_EXPR, CALL_EXPR,
      COMPOUND_INIT_EXPR, CONSTANT_EXPR, CT_ANALYZE_EXPR, CT_ARG_EXPR,
      CT_DEFINED_CHECK_EXPR, CT_DEFINED_EXPR, CT_FEATURE_EXPR, DECL_OR_EXPR,
      ENUM_ACCESS_EXPR, EXPR, GROUPED_EXPR, INIT_LIST_EXPR,
      KEYWORD_EXPR, LAMBDA_DECL_EXPR, LAMBDA_DECL_SHORT_EXPR, LITERAL_EXPR,
      LOCAL_IDENT_EXPR, PATH_AT_IDENT_EXPR, PATH_CONST_EXPR, PATH_IDENT_EXPR,
      STRING_EXPR, TERNARY_EXPR, TYPE_ACCESS_EXPR, TYPE_EXPR,
      UNARY_EXPR),
  };

  /* ********************************************************** */
  // IDENT | AT_IDENT | HASH_IDENT | KW_CT_EVAL '(' expr ')' | KW_TYPEID | CT_IDENT
  public static boolean access_ident(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_ident")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ACCESS_IDENT, "<access ident>");
    result_ = consumeToken(builder_, IDENT);
    if (!result_) result_ = consumeToken(builder_, AT_IDENT);
    if (!result_) result_ = consumeToken(builder_, HASH_IDENT);
    if (!result_) result_ = access_ident_3(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, KW_TYPEID);
    if (!result_) result_ = consumeToken(builder_, CT_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_CT_EVAL '(' expr ')'
  private static boolean access_ident_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_ident_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_EVAL);
    result_ = result_ && consumeToken(builder_, "(");
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, ")");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // PLUS | MINUS | CT_PLUS
  public static boolean add_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "add_bin_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator>");
    result_ = consumeToken(builder_, PLUS);
    if (!result_) result_ = consumeToken(builder_, MINUS);
    if (!result_) result_ = consumeToken(builder_, CT_PLUS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_ALIAS alias_name generic_decl? attributes? EQ ((KW_MODULE module_path) | alias_declaration_source) EOS
  public static boolean alias_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_decl")) return false;
    if (!nextTokenIs(builder_, KW_ALIAS)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ALIAS_DECL, null);
    result_ = consumeToken(builder_, KW_ALIAS);
    result_ = result_ && alias_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, alias_decl_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, alias_decl_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, EQ)) && result_;
    result_ = pinned_ && report_error_(builder_, alias_decl_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // generic_decl?
  private static boolean alias_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_decl_2")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean alias_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_decl_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // (KW_MODULE module_path) | alias_declaration_source
  private static boolean alias_decl_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_decl_5")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = alias_decl_5_0(builder_, level_ + 1);
    if (!result_) result_ = alias_declaration_source(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_MODULE module_path
  private static boolean alias_decl_5_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_decl_5_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_MODULE);
    result_ = result_ && module_path(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (path_const | path_ident | path_at_ident) generic_parameters?
  public static boolean alias_declaration_source(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_declaration_source")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ALIAS_DECLARATION_SOURCE, "<alias declaration source>");
    result_ = alias_declaration_source_0(builder_, level_ + 1);
    result_ = result_ && alias_declaration_source_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path_const | path_ident | path_at_ident
  private static boolean alias_declaration_source_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_declaration_source_0")) return false;
    boolean result_;
    result_ = path_const(builder_, level_ + 1);
    if (!result_) result_ = path_ident(builder_, level_ + 1);
    if (!result_) result_ = path_at_ident(builder_, level_ + 1);
    return result_;
  }

  // generic_parameters?
  private static boolean alias_declaration_source_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_declaration_source_1")) return false;
    generic_parameters(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // CONST_IDENT | AT_IDENT | IDENT
  public static boolean alias_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_name")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ALIAS_NAME, "<alias name>");
    result_ = consumeToken(builder_, CONST_IDENT);
    if (!result_) result_ = consumeToken(builder_, AT_IDENT);
    if (!result_) result_ = consumeToken(builder_, IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_ALIAS type_name generic_decl? attributes? EQ typedef_type EOS
  public static boolean alias_type_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_type_decl")) return false;
    if (!nextTokenIs(builder_, KW_ALIAS)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ALIAS_TYPE_DECL, null);
    result_ = consumeToken(builder_, KW_ALIAS);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, alias_type_decl_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, alias_type_decl_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, EQ)) && result_;
    result_ = pinned_ && report_error_(builder_, typedef_type(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // generic_decl?
  private static boolean alias_type_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_type_decl_2")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean alias_type_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_type_decl_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (named_ident COLON (ELLIPSIS? expr)) | param_path (EQ (expr))? | expr | ELLIPSIS expr
  public static boolean arg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ARG, "<arg>");
    result_ = arg_0(builder_, level_ + 1);
    if (!result_) result_ = arg_1(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    if (!result_) result_ = arg_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // named_ident COLON (ELLIPSIS? expr)
  private static boolean arg_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = named_ident(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, COLON);
    result_ = result_ && arg_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS? expr
  private static boolean arg_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_0_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = arg_0_2_0(builder_, level_ + 1);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS?
  private static boolean arg_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_0_2_0")) return false;
    consumeToken(builder_, ELLIPSIS);
    return true;
  }

  // param_path (EQ (expr))?
  private static boolean arg_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = param_path(builder_, level_ + 1);
    result_ = result_ && arg_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (EQ (expr))?
  private static boolean arg_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_1_1")) return false;
    arg_1_1_0(builder_, level_ + 1);
    return true;
  }

  // EQ (expr)
  private static boolean arg_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_1_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EQ);
    result_ = result_ && arg_1_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (expr)
  private static boolean arg_1_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_1_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS expr
  private static boolean arg_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ELLIPSIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // arg (COMMA arg)* COMMA?
  public static boolean arg_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ARG_LIST, "<arg list>");
    result_ = arg(builder_, level_ + 1);
    result_ = result_ && arg_list_1(builder_, level_ + 1);
    result_ = result_ && arg_list_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA arg)*
  private static boolean arg_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!arg_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "arg_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA arg
  private static boolean arg_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && arg(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean arg_list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_2")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // asm_expr (add_bin_op asm_expr asm_addr_trailing?)?
  public static boolean asm_addr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_ADDR, "<asm addr>");
    result_ = asm_expr(builder_, level_ + 1);
    result_ = result_ && asm_addr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (add_bin_op asm_expr asm_addr_trailing?)?
  private static boolean asm_addr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_1")) return false;
    asm_addr_1_0(builder_, level_ + 1);
    return true;
  }

  // add_bin_op asm_expr asm_addr_trailing?
  private static boolean asm_addr_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = add_bin_op(builder_, level_ + 1);
    result_ = result_ && asm_expr(builder_, level_ + 1);
    result_ = result_ && asm_addr_1_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // asm_addr_trailing?
  private static boolean asm_addr_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_1_0_2")) return false;
    asm_addr_trailing(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // STAR INT_LITERAL (add_bin_op INT_LITERAL)? | (shift_bin_op | add_bin_op) INT_LITERAL
  public static boolean asm_addr_trailing(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_ADDR_TRAILING, "<asm addr trailing>");
    result_ = asm_addr_trailing_0(builder_, level_ + 1);
    if (!result_) result_ = asm_addr_trailing_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // STAR INT_LITERAL (add_bin_op INT_LITERAL)?
  private static boolean asm_addr_trailing_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, STAR, INT_LITERAL);
    result_ = result_ && asm_addr_trailing_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (add_bin_op INT_LITERAL)?
  private static boolean asm_addr_trailing_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing_0_2")) return false;
    asm_addr_trailing_0_2_0(builder_, level_ + 1);
    return true;
  }

  // add_bin_op INT_LITERAL
  private static boolean asm_addr_trailing_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing_0_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = add_bin_op(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, INT_LITERAL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (shift_bin_op | add_bin_op) INT_LITERAL
  private static boolean asm_addr_trailing_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_addr_trailing_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, INT_LITERAL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // shift_bin_op | add_bin_op
  private static boolean asm_addr_trailing_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_addr_trailing_1_0")) return false;
    boolean result_;
    result_ = shift_bin_op(builder_, level_ + 1);
    if (!result_) result_ = add_bin_op(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // KW_ASM (LP expr RP attributes? | attributes? LB asm_stmt* RB)
  public static boolean asm_block_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt")) return false;
    if (!nextTokenIs(builder_, KW_ASM)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_BLOCK_STMT, null);
    result_ = consumeToken(builder_, KW_ASM);
    pinned_ = result_; // pin = 1
    result_ = result_ && asm_block_stmt_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LP expr RP attributes? | attributes? LB asm_stmt* RB
  private static boolean asm_block_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_block_stmt_1_0(builder_, level_ + 1);
    if (!result_) result_ = asm_block_stmt_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LP expr RP attributes?
  private static boolean asm_block_stmt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, RP);
    result_ = result_ && asm_block_stmt_1_0_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean asm_block_stmt_1_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1_0_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // attributes? LB asm_stmt* RB
  private static boolean asm_block_stmt_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_block_stmt_1_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, LB);
    result_ = result_ && asm_block_stmt_1_1_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean asm_block_stmt_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1_1_0")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // asm_stmt*
  private static boolean asm_block_stmt_1_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_block_stmt_1_1_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!asm_stmt(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "asm_block_stmt_1_1_2", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_ASM expr attributes? EOS
  public static boolean asm_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_declaration")) return false;
    if (!nextTokenIs(builder_, KW_ASM)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_DECLARATION, null);
    result_ = consumeToken(builder_, KW_ASM);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, asm_declaration_2(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // attributes?
  private static boolean asm_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_declaration_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // CT_IDENT | CT_CONST_IDENT | AMP? IDENT | CONST_IDENT
  //     | MINUS? FLOAT_LITERAL | MINUS? INT_LITERAL | grouped_expr | LBT asm_addr RBT
  public static boolean asm_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, ASM_EXPR, "<asm expr>");
    result_ = consumeToken(builder_, CT_IDENT);
    if (!result_) result_ = consumeToken(builder_, CT_CONST_IDENT);
    if (!result_) result_ = asm_expr_2(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, CONST_IDENT);
    if (!result_) result_ = asm_expr_4(builder_, level_ + 1);
    if (!result_) result_ = asm_expr_5(builder_, level_ + 1);
    if (!result_) result_ = grouped_expr(builder_, level_ + 1);
    if (!result_) result_ = asm_expr_7(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // AMP? IDENT
  private static boolean asm_expr_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_expr_2_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // AMP?
  private static boolean asm_expr_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_2_0")) return false;
    consumeToken(builder_, AMP);
    return true;
  }

  // MINUS? FLOAT_LITERAL
  private static boolean asm_expr_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_expr_4_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLOAT_LITERAL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // MINUS?
  private static boolean asm_expr_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_4_0")) return false;
    consumeToken(builder_, MINUS);
    return true;
  }

  // MINUS? INT_LITERAL
  private static boolean asm_expr_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_5")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = asm_expr_5_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, INT_LITERAL);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // MINUS?
  private static boolean asm_expr_5_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_5_0")) return false;
    consumeToken(builder_, MINUS);
    return true;
  }

  // LBT asm_addr RBT
  private static boolean asm_expr_7(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_expr_7")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LBT);
    result_ = result_ && asm_addr(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RBT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // asm_expr (COMMA asm_expr)*
  public static boolean asm_exprs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_exprs")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_EXPRS, "<asm exprs>");
    result_ = asm_expr(builder_, level_ + 1);
    result_ = result_ && asm_exprs_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA asm_expr)*
  private static boolean asm_exprs_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_exprs_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!asm_exprs_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "asm_exprs_1", pos_)) break;
    }
    return true;
  }

  // COMMA asm_expr
  private static boolean asm_exprs_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_exprs_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && asm_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (KW_INT | IDENT) (DOT IDENT)?
  public static boolean asm_instr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_instr")) return false;
    if (!nextTokenIs(builder_, "<asm instr>", IDENT, KW_INT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_INSTR, "<asm instr>");
    result_ = asm_instr_0(builder_, level_ + 1);
    result_ = result_ && asm_instr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_INT | IDENT
  private static boolean asm_instr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_instr_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, KW_INT);
    if (!result_) result_ = consumeToken(builder_, IDENT);
    return result_;
  }

  // (DOT IDENT)?
  private static boolean asm_instr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_instr_1")) return false;
    asm_instr_1_0(builder_, level_ + 1);
    return true;
  }

  // DOT IDENT
  private static boolean asm_instr_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_instr_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, DOT, IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // asm_instr asm_exprs? EOS
  public static boolean asm_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_stmt")) return false;
    if (!nextTokenIs(builder_, "<asm stmt>", IDENT, KW_INT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASM_STMT, "<asm stmt>");
    result_ = asm_instr(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, asm_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // asm_exprs?
  private static boolean asm_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asm_stmt_1")) return false;
    asm_exprs(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_ASSERT LP expr (COMMA expr)* RP EOS
  public static boolean assert_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assert_stmt")) return false;
    if (!nextTokenIs(builder_, KW_ASSERT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ASSERT_STMT, null);
    result_ = consumeTokens(builder_, 1, KW_ASSERT, LP);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1, -1));
    result_ = pinned_ && report_error_(builder_, assert_stmt_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeTokens(builder_, -1, RP, EOS)) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (COMMA expr)*
  private static boolean assert_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assert_stmt_3")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!assert_stmt_3_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "assert_stmt_3", pos_)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean assert_stmt_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assert_stmt_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // EQ
  //     | CT_PLUS EQ
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
  public static boolean assign_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assign_bin_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator>");
    result_ = consumeToken(builder_, EQ);
    if (!result_) result_ = parseTokens(builder_, 0, CT_PLUS, EQ);
    if (!result_) result_ = consumeToken(builder_, MULT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MOD_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, DIV_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, PLUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MINUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHL_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, BIT_AND_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, BIT_XOR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, BIT_OR_ASSIGN);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // attribute_operator_expr | constant_expr
  public static boolean attr_param(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_param")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTR_PARAM, "<attr param>");
    result_ = attribute_operator_expr(builder_, level_ + 1);
    if (!result_) result_ = constant_expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_ATTRDEF attribute_user_name (LP parameter_list RP)? generic_decl? attributes? (EQ def_attr_values)? EOS
  public static boolean attrdef_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl")) return false;
    if (!nextTokenIs(builder_, KW_ATTRDEF)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRDEF_DECL, null);
    result_ = consumeToken(builder_, KW_ATTRDEF);
    result_ = result_ && attribute_user_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, attrdef_decl_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, attrdef_decl_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, attrdef_decl_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, attrdef_decl_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (LP parameter_list RP)?
  private static boolean attrdef_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_2")) return false;
    attrdef_decl_2_0(builder_, level_ + 1);
    return true;
  }

  // LP parameter_list RP
  private static boolean attrdef_decl_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && parameter_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // generic_decl?
  private static boolean attrdef_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_3")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean attrdef_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // (EQ def_attr_values)?
  private static boolean attrdef_decl_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_5")) return false;
    attrdef_decl_5_0(builder_, level_ + 1);
    return true;
  }

  // EQ def_attr_values
  private static boolean attrdef_decl_5_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrdef_decl_5_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EQ);
    result_ = result_ && def_attr_values(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // attribute_name (LP attribute_param_list RP)?
  public static boolean attribute(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRIBUTE, "<attribute>");
    result_ = attribute_name(builder_, level_ + 1);
    result_ = result_ && attribute_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (LP attribute_param_list RP)?
  private static boolean attribute_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_1")) return false;
    attribute_1_0(builder_, level_ + 1);
    return true;
  }

  // LP attribute_param_list RP
  private static boolean attribute_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && attribute_param_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // AT_IDENT | path? AT_TYPE_IDENT
  public static boolean attribute_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_name")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRIBUTE_NAME, "<attribute name>");
    result_ = consumeToken(builder_, AT_IDENT);
    if (!result_) result_ = attribute_name_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path? AT_TYPE_IDENT
  private static boolean attribute_name_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_name_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attribute_name_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, AT_TYPE_IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // path?
  private static boolean attribute_name_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_name_1_0")) return false;
    path(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // BIT_AND_ASSIGN | BIT_OR_ASSIGN | BIT_XOR_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | MOD_ASSIGN | SHL_ASSIGN | SHR_ASSIGN | &RP | PLUS &RP | MINUS &RP | DIV | LT_OP | STAR &RP | MOD | EQ_OP | BIT_XOR | BIT_NOT | BIT_OR | AMP &RP | SHL | SHR | LBT RBT EQ | AMP? LBT RBT
  public static boolean attribute_operator_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRIBUTE_OPERATOR_EXPR, "<attribute operator expr>");
    result_ = consumeToken(builder_, BIT_AND_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, BIT_OR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, BIT_XOR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, PLUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MINUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MULT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, DIV_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MOD_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHL_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHR_ASSIGN);
    if (!result_) result_ = attribute_operator_expr_10(builder_, level_ + 1);
    if (!result_) result_ = attribute_operator_expr_11(builder_, level_ + 1);
    if (!result_) result_ = attribute_operator_expr_12(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, DIV);
    if (!result_) result_ = consumeToken(builder_, LT_OP);
    if (!result_) result_ = attribute_operator_expr_15(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, MOD);
    if (!result_) result_ = consumeToken(builder_, EQ_OP);
    if (!result_) result_ = consumeToken(builder_, BIT_XOR);
    if (!result_) result_ = consumeToken(builder_, BIT_NOT);
    if (!result_) result_ = consumeToken(builder_, BIT_OR);
    if (!result_) result_ = attribute_operator_expr_21(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, SHL);
    if (!result_) result_ = consumeToken(builder_, SHR);
    if (!result_) result_ = parseTokens(builder_, 0, LBT, RBT, EQ);
    if (!result_) result_ = attribute_operator_expr_25(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // &RP
  private static boolean attribute_operator_expr_10(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_10")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // PLUS &RP
  private static boolean attribute_operator_expr_11(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_11")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, PLUS);
    result_ = result_ && attribute_operator_expr_11_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &RP
  private static boolean attribute_operator_expr_11_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_11_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // MINUS &RP
  private static boolean attribute_operator_expr_12(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_12")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, MINUS);
    result_ = result_ && attribute_operator_expr_12_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &RP
  private static boolean attribute_operator_expr_12_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_12_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // STAR &RP
  private static boolean attribute_operator_expr_15(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_15")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, STAR);
    result_ = result_ && attribute_operator_expr_15_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &RP
  private static boolean attribute_operator_expr_15_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_15_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // AMP &RP
  private static boolean attribute_operator_expr_21(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_21")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AMP);
    result_ = result_ && attribute_operator_expr_21_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &RP
  private static boolean attribute_operator_expr_21_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_21_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // AMP? LBT RBT
  private static boolean attribute_operator_expr_25(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_25")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attribute_operator_expr_25_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, LBT, RBT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // AMP?
  private static boolean attribute_operator_expr_25_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_operator_expr_25_0")) return false;
    consumeToken(builder_, AMP);
    return true;
  }

  /* ********************************************************** */
  // attr_param (COMMA attr_param)*
  public static boolean attribute_param_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_param_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRIBUTE_PARAM_LIST, "<attribute param list>");
    result_ = attr_param(builder_, level_ + 1);
    result_ = result_ && attribute_param_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA attr_param)*
  private static boolean attribute_param_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_param_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!attribute_param_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "attribute_param_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA attr_param
  private static boolean attribute_param_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_param_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && attr_param(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // AT_TYPE_IDENT
  public static boolean attribute_user_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attribute_user_name")) return false;
    if (!nextTokenIs(builder_, AT_TYPE_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AT_TYPE_IDENT);
    exit_section_(builder_, marker_, ATTRIBUTE_USER_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // attribute+
  public static boolean attributes(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attributes")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ATTRIBUTES, "<attributes>");
    result_ = attribute(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!attribute(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "attributes", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_VOID
  //     | KW_BOOL
  //     | integer_type
  //     | float_type
  //     | KW_IPTR
  //     | KW_UPTR
  //     | KW_SZ
  //     | KW_UNTYPEDLIST
  //     | KW_USZ
  //     | KW_FAULT
  //     | KW_ANY
  //     | KW_TYPEID
  //     | struct_type
  //     | CT_TYPE_IDENT
  //     | KW_CT_REFLECT grouped_expr
  //     | KW_CT_TYPEOF grouped_expr
  //     | KW_CT_TYPEFROM const_paren_expr
  public static boolean base_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "base_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BASE_TYPE, "<base type>");
    result_ = consumeToken(builder_, KW_VOID);
    if (!result_) result_ = consumeToken(builder_, KW_BOOL);
    if (!result_) result_ = integer_type(builder_, level_ + 1);
    if (!result_) result_ = float_type(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, KW_IPTR);
    if (!result_) result_ = consumeToken(builder_, KW_UPTR);
    if (!result_) result_ = consumeToken(builder_, KW_SZ);
    if (!result_) result_ = consumeToken(builder_, KW_UNTYPEDLIST);
    if (!result_) result_ = consumeToken(builder_, KW_USZ);
    if (!result_) result_ = consumeToken(builder_, KW_FAULT);
    if (!result_) result_ = consumeToken(builder_, KW_ANY);
    if (!result_) result_ = consumeToken(builder_, KW_TYPEID);
    if (!result_) result_ = struct_type(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, CT_TYPE_IDENT);
    if (!result_) result_ = base_type_14(builder_, level_ + 1);
    if (!result_) result_ = base_type_15(builder_, level_ + 1);
    if (!result_) result_ = base_type_16(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_CT_REFLECT grouped_expr
  private static boolean base_type_14(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "base_type_14")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_REFLECT);
    result_ = result_ && grouped_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_CT_TYPEOF grouped_expr
  private static boolean base_type_15(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "base_type_15")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_TYPEOF);
    result_ = result_ && grouped_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_CT_TYPEFROM const_paren_expr
  private static boolean base_type_16(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "base_type_16")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_TYPEFROM);
    result_ = result_ && const_paren_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // AMP | BIT_XOR | BIT_OR
  public static boolean bit_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bit_bin_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator>");
    result_ = consumeToken(builder_, AMP);
    if (!result_) result_ = consumeToken(builder_, BIT_XOR);
    if (!result_) result_ = consumeToken(builder_, BIT_OR);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LB (bitstruct_def+ | bitstruct_simple_def+)? RB
  public static boolean bitstruct_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_body")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && bitstruct_body_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, marker_, BITSTRUCT_BODY, result_);
    return result_;
  }

  // (bitstruct_def+ | bitstruct_simple_def+)?
  private static boolean bitstruct_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_body_1")) return false;
    bitstruct_body_1_0(builder_, level_ + 1);
    return true;
  }

  // bitstruct_def+ | bitstruct_simple_def+
  private static boolean bitstruct_body_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_body_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = bitstruct_body_1_0_0(builder_, level_ + 1);
    if (!result_) result_ = bitstruct_body_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // bitstruct_def+
  private static boolean bitstruct_body_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_body_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = bitstruct_def(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!bitstruct_def(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "bitstruct_body_1_0_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // bitstruct_simple_def+
  private static boolean bitstruct_body_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_body_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = bitstruct_simple_def(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!bitstruct_simple_def(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "bitstruct_body_1_0_1", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_BITSTRUCT type_name interface_impl? COLON type generic_decl? attributes? bitstruct_body
  public static boolean bitstruct_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_declaration")) return false;
    if (!nextTokenIs(builder_, KW_BITSTRUCT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BITSTRUCT_DECLARATION, null);
    result_ = consumeToken(builder_, KW_BITSTRUCT);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, bitstruct_declaration_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, type(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, bitstruct_declaration_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, bitstruct_declaration_6(builder_, level_ + 1)) && result_;
    result_ = pinned_ && bitstruct_body(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // interface_impl?
  private static boolean bitstruct_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_declaration_2")) return false;
    interface_impl(builder_, level_ + 1);
    return true;
  }

  // generic_decl?
  private static boolean bitstruct_declaration_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_declaration_5")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean bitstruct_declaration_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_declaration_6")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // base_type IDENT COLON constant_expr (DOTDOT constant_expr)? EOS
  public static boolean bitstruct_def(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_def")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BITSTRUCT_DEF, "<bitstruct def>");
    result_ = base_type(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENT, COLON);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    result_ = result_ && bitstruct_def_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (DOTDOT constant_expr)?
  private static boolean bitstruct_def_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_def_4")) return false;
    bitstruct_def_4_0(builder_, level_ + 1);
    return true;
  }

  // DOTDOT constant_expr
  private static boolean bitstruct_def_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_def_4_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DOTDOT);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // base_type IDENT EOS
  public static boolean bitstruct_simple_def(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bitstruct_simple_def")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BITSTRUCT_SIMPLE_DEF, "<bitstruct simple def>");
    result_ = base_type(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENT, EOS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_BREAK CONST_IDENT? EOS
  public static boolean break_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "break_stmt")) return false;
    if (!nextTokenIs(builder_, KW_BREAK)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BREAK_STMT, null);
    result_ = consumeToken(builder_, KW_BREAK);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, break_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // CONST_IDENT?
  private static boolean break_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "break_stmt_1")) return false;
    consumeToken(builder_, CONST_IDENT);
    return true;
  }

  /* ********************************************************** */
  // arg_list? (EOS parameter_list?)?
  public static boolean call_arg_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_arg_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CALL_ARG_LIST, "<call arg list>");
    result_ = call_arg_list_0(builder_, level_ + 1);
    result_ = result_ && call_arg_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // arg_list?
  private static boolean call_arg_list_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_arg_list_0")) return false;
    arg_list(builder_, level_ + 1);
    return true;
  }

  // (EOS parameter_list?)?
  private static boolean call_arg_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_arg_list_1")) return false;
    call_arg_list_1_0(builder_, level_ + 1);
    return true;
  }

  // EOS parameter_list?
  private static boolean call_arg_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_arg_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOS);
    result_ = result_ && call_arg_list_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // parameter_list?
  private static boolean call_arg_list_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_arg_list_1_0_1")) return false;
    parameter_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // call_invocation (compound_statement | (IMPLIES expr))?
  //     | DOT? LBT (range_exp | range_loc) RBT
  //     | generic_parameters
  //     | dot_access_ident
  //     | BIT_NOT
  //     | PLUSPLUS
  //     | MINUSMINUS
  //     | BANG
  //     | BANGBANG
  public static boolean call_expr_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CALL_EXPR_TAIL, "<call expr tail>");
    result_ = call_expr_tail_0(builder_, level_ + 1);
    if (!result_) result_ = call_expr_tail_1(builder_, level_ + 1);
    if (!result_) result_ = generic_parameters(builder_, level_ + 1);
    if (!result_) result_ = dot_access_ident(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BIT_NOT);
    if (!result_) result_ = consumeToken(builder_, PLUSPLUS);
    if (!result_) result_ = consumeToken(builder_, MINUSMINUS);
    if (!result_) result_ = consumeToken(builder_, BANG);
    if (!result_) result_ = consumeToken(builder_, BANGBANG);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // call_invocation (compound_statement | (IMPLIES expr))?
  private static boolean call_expr_tail_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = call_invocation(builder_, level_ + 1);
    result_ = result_ && call_expr_tail_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (compound_statement | (IMPLIES expr))?
  private static boolean call_expr_tail_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_0_1")) return false;
    call_expr_tail_0_1_0(builder_, level_ + 1);
    return true;
  }

  // compound_statement | (IMPLIES expr)
  private static boolean call_expr_tail_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = compound_statement(builder_, level_ + 1);
    if (!result_) result_ = call_expr_tail_0_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IMPLIES expr
  private static boolean call_expr_tail_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_0_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IMPLIES);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // DOT? LBT (range_exp | range_loc) RBT
  private static boolean call_expr_tail_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = call_expr_tail_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, LBT);
    result_ = result_ && call_expr_tail_1_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RBT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // DOT?
  private static boolean call_expr_tail_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_1_0")) return false;
    consumeToken(builder_, DOT);
    return true;
  }

  // range_exp | range_loc
  private static boolean call_expr_tail_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expr_tail_1_2")) return false;
    boolean result_;
    result_ = range_exp(builder_, level_ + 1);
    if (!result_) result_ = range_loc(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // LP call_arg_list RP AT_IDENT*
  public static boolean call_invocation(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_invocation")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && call_arg_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    result_ = result_ && call_invocation_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, CALL_INVOCATION, result_);
    return result_;
  }

  // AT_IDENT*
  private static boolean call_invocation_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_invocation_3")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, AT_IDENT)) break;
      if (!empty_element_parsed_guard_(builder_, "call_invocation_3", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_CASE expr (DOTDOT expr)? COLON statement_list?
  public static boolean case_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CASE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CASE_STMT, null);
    result_ = consumeToken(builder_, KW_CASE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1, -1));
    result_ = pinned_ && report_error_(builder_, case_stmt_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && case_stmt_4(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (DOTDOT expr)?
  private static boolean case_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_stmt_2")) return false;
    case_stmt_2_0(builder_, level_ + 1);
    return true;
  }

  // DOTDOT expr
  private static boolean case_stmt_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_stmt_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DOTDOT);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // statement_list?
  private static boolean case_stmt_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_stmt_4")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CATCH (type? IDENT EQ)? catch_unwrap_list
  public static boolean catch_unwrap(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap")) return false;
    if (!nextTokenIs(builder_, KW_CATCH)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CATCH);
    result_ = result_ && catch_unwrap_1(builder_, level_ + 1);
    result_ = result_ && catch_unwrap_list(builder_, level_ + 1);
    exit_section_(builder_, marker_, CATCH_UNWRAP, result_);
    return result_;
  }

  // (type? IDENT EQ)?
  private static boolean catch_unwrap_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_1")) return false;
    catch_unwrap_1_0(builder_, level_ + 1);
    return true;
  }

  // type? IDENT EQ
  private static boolean catch_unwrap_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = catch_unwrap_1_0_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENT, EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // type?
  private static boolean catch_unwrap_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_1_0_0")) return false;
    type(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // expr (COMMA expr)*
  public static boolean catch_unwrap_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CATCH_UNWRAP_LIST, "<catch unwrap list>");
    result_ = expr(builder_, level_ + 1, -1);
    result_ = result_ && catch_unwrap_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA expr)*
  private static boolean catch_unwrap_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!catch_unwrap_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "catch_unwrap_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA expr
  private static boolean catch_unwrap_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "catch_unwrap_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LB statement_list* RB
  public static boolean compound_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compound_statement")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, COMPOUND_STATEMENT, null);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && compound_statement_1(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // statement_list*
  private static boolean compound_statement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compound_statement_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement_list(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "compound_statement_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // cond_repeat (COMMA (try_unwrap_chain | catch_unwrap))? | try_unwrap_chain | catch_unwrap
  public static boolean cond(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, COND, "<cond>");
    result_ = cond_0(builder_, level_ + 1);
    if (!result_) result_ = try_unwrap_chain(builder_, level_ + 1);
    if (!result_) result_ = catch_unwrap(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // cond_repeat (COMMA (try_unwrap_chain | catch_unwrap))?
  private static boolean cond_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = cond_repeat(builder_, level_ + 1);
    result_ = result_ && cond_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA (try_unwrap_chain | catch_unwrap))?
  private static boolean cond_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_0_1")) return false;
    cond_0_1_0(builder_, level_ + 1);
    return true;
  }

  // COMMA (try_unwrap_chain | catch_unwrap)
  private static boolean cond_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && cond_0_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // try_unwrap_chain | catch_unwrap
  private static boolean cond_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_0_1_0_1")) return false;
    boolean result_;
    result_ = try_unwrap_chain(builder_, level_ + 1);
    if (!result_) result_ = catch_unwrap(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // decl_or_expr (COMMA decl_or_expr)*
  public static boolean cond_repeat(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_repeat")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, COND_REPEAT, "<cond repeat>");
    result_ = decl_or_expr(builder_, level_ + 1);
    result_ = result_ && cond_repeat_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA decl_or_expr)*
  private static boolean cond_repeat_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_repeat_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!cond_repeat_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "cond_repeat_1", pos_)) break;
    }
    return true;
  }

  // COMMA decl_or_expr
  private static boolean cond_repeat_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cond_repeat_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && decl_or_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_CONST type? CONST_IDENT attributes? eq_expr_pin EOS
  public static boolean const_declaration_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "const_declaration_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CONST)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CONST_DECLARATION_STMT, null);
    result_ = consumeToken(builder_, KW_CONST);
    result_ = result_ && const_declaration_stmt_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CONST_IDENT);
    pinned_ = result_; // pin = 3
    result_ = result_ && report_error_(builder_, const_declaration_stmt_3(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, eq_expr_pin(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // type?
  private static boolean const_declaration_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "const_declaration_stmt_1")) return false;
    type(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean const_declaration_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "const_declaration_stmt_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // LP constant_expr RP
  static boolean const_paren_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "const_paren_expr")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr
  public static boolean constant_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constant_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, CONSTANT_EXPR, "<constant expr>");
    result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CONST_IDENT attributes? [eq_expr_pin]
  public static boolean constdef_constant(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_constant")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CONST_IDENT);
    result_ = result_ && constdef_constant_1(builder_, level_ + 1);
    result_ = result_ && constdef_constant_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, CONSTDEF_CONSTANT, result_);
    return result_;
  }

  // attributes?
  private static boolean constdef_constant_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_constant_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // [eq_expr_pin]
  private static boolean constdef_constant_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_constant_2")) return false;
    eq_expr_pin(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CONSTDEF type_name interface_impl? (COLON KW_INLINE? type?)? attributes? LB constdef_list RB
  public static boolean constdef_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration")) return false;
    if (!nextTokenIs(builder_, KW_CONSTDEF)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CONSTDEF_DECLARATION, null);
    result_ = consumeToken(builder_, KW_CONSTDEF);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, constdef_declaration_2(builder_, level_ + 1));
    result_ = pinned_ && constdef_declaration_3(builder_, level_ + 1) && result_;
    result_ = pinned_ && constdef_declaration_4(builder_, level_ + 1) && result_;
    result_ = pinned_ && consumeToken(builder_, LB) && result_;
    if (pinned_) {
      constdef_list(builder_, level_ + 1);
      consumeToken(builder_, RB);
      result_ = true;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // interface_impl?
  private static boolean constdef_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_2")) return false;
    interface_impl(builder_, level_ + 1);
    return true;
  }

  // (COLON KW_INLINE? type?)?
  private static boolean constdef_declaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_3")) return false;
    constdef_declaration_3_0(builder_, level_ + 1);
    return true;
  }

  // COLON KW_INLINE? type?
  private static boolean constdef_declaration_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && constdef_declaration_3_0_1(builder_, level_ + 1);
    result_ = result_ && constdef_declaration_3_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_INLINE?
  private static boolean constdef_declaration_3_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_3_0_1")) return false;
    consumeToken(builder_, KW_INLINE);
    return true;
  }

  // type?
  private static boolean constdef_declaration_3_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_3_0_2")) return false;
    type(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean constdef_declaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_declaration_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (constdef_constant COMMA?)+
  public static boolean constdef_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_list")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = constdef_list_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!nextTokenIs(builder_, CONST_IDENT)) break;
      if (!constdef_list_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "constdef_list", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, CONSTDEF_LIST, result_);
    return result_;
  }

  // constdef_constant COMMA?
  private static boolean constdef_list_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_list_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = constdef_constant(builder_, level_ + 1);
    result_ = result_ && constdef_list_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean constdef_list_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constdef_list_0_1")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // KW_CONTINUE CONST_IDENT? EOS
  public static boolean continue_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "continue_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CONTINUE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CONTINUE_STMT, null);
    result_ = consumeToken(builder_, KW_CONTINUE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, continue_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // CONST_IDENT?
  private static boolean continue_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "continue_stmt_1")) return false;
    consumeToken(builder_, CONST_IDENT);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_EVAL | KW_CT_REFLECT | KW_CT_STRINGIFY | KW_CT_EXPAND
  public static boolean ct_analyze(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_analyze")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_ANALYZE, "<ct analyze>");
    result_ = consumeToken(builder_, KW_CT_EVAL);
    if (!result_) result_ = consumeToken(builder_, KW_CT_REFLECT);
    if (!result_) result_ = consumeToken(builder_, KW_CT_STRINGIFY);
    if (!result_) result_ = consumeToken(builder_, KW_CT_EXPAND);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_CT_ASSERT constant_expr (COLON constant_expr (COMMA constant_expr)*)? EOS
  public static boolean ct_assert_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_assert_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_ASSERT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_ASSERT_STMT, null);
    result_ = consumeToken(builder_, KW_CT_ASSERT);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, constant_expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, ct_assert_stmt_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (COLON constant_expr (COMMA constant_expr)*)?
  private static boolean ct_assert_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_assert_stmt_2")) return false;
    ct_assert_stmt_2_0(builder_, level_ + 1);
    return true;
  }

  // COLON constant_expr (COMMA constant_expr)*
  private static boolean ct_assert_stmt_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_assert_stmt_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    result_ = result_ && ct_assert_stmt_2_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA constant_expr)*
  private static boolean ct_assert_stmt_2_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_assert_stmt_2_0_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!ct_assert_stmt_2_0_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ct_assert_stmt_2_0_2", pos_)) break;
    }
    return true;
  }

  // COMMA constant_expr
  private static boolean ct_assert_stmt_2_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_assert_stmt_2_0_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (KW_CT_CASE constant_expr | KW_CT_DEFAULT) COLON statement_list?
  public static boolean ct_case_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_case_stmt")) return false;
    if (!nextTokenIs(builder_, "<ct case stmt>", KW_CT_CASE, KW_CT_DEFAULT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_CASE_STMT, "<ct case stmt>");
    result_ = ct_case_stmt_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, consumeToken(builder_, COLON));
    result_ = pinned_ && ct_case_stmt_2(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_CT_CASE constant_expr | KW_CT_DEFAULT
  private static boolean ct_case_stmt_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_case_stmt_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = ct_case_stmt_0_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, KW_CT_DEFAULT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_CT_CASE constant_expr
  private static boolean ct_case_stmt_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_case_stmt_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_CASE);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // statement_list?
  private static boolean ct_case_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_case_stmt_2")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // decl_or_expr
  public static boolean ct_defined_check_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_defined_check_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, CT_DEFINED_CHECK_EXPR, "<ct defined check expr>");
    result_ = decl_or_expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // ct_defined_check_expr (COMMA ct_defined_check_expr)*
  public static boolean ct_defined_check_expr_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_defined_check_expr_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_DEFINED_CHECK_EXPR_LIST, "<ct defined check expr list>");
    result_ = ct_defined_check_expr(builder_, level_ + 1);
    result_ = result_ && ct_defined_check_expr_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA ct_defined_check_expr)*
  private static boolean ct_defined_check_expr_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_defined_check_expr_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!ct_defined_check_expr_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ct_defined_check_expr_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA ct_defined_check_expr
  private static boolean ct_defined_check_expr_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_defined_check_expr_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && ct_defined_check_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_CT_ECHO constant_expr EOS
  public static boolean ct_echo_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_echo_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_ECHO)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_ECHO_STMT, null);
    result_ = consumeToken(builder_, KW_CT_ECHO);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, constant_expr(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // KW_CT_ERROR constant_expr (COMMA constant_expr)* EOS
  public static boolean ct_error_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_error_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_ERROR)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_ERROR_STMT, null);
    result_ = consumeToken(builder_, KW_CT_ERROR);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, constant_expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, ct_error_stmt_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (COMMA constant_expr)*
  private static boolean ct_error_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_error_stmt_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!ct_error_stmt_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ct_error_stmt_2", pos_)) break;
    }
    return true;
  }

  // COMMA constant_expr
  private static boolean ct_error_stmt_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_error_stmt_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_CT_FOR for_cond COLON statement_list? KW_CT_ENDFOR
  public static boolean ct_for_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_for_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_FOR)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_FOR_STMT, null);
    result_ = consumeToken(builder_, KW_CT_FOR);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, for_cond(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, ct_for_stmt_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, KW_CT_ENDFOR) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // statement_list?
  private static boolean ct_for_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_for_stmt_3")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_FOREACH CT_IDENT (COMMA CT_IDENT)? COLON expr COLON statement_list? KW_CT_ENDFOREACH
  public static boolean ct_foreach_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_foreach_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_FOREACH)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_FOREACH_STMT, null);
    result_ = consumeTokens(builder_, 1, KW_CT_FOREACH, CT_IDENT);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, ct_foreach_stmt_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, expr(builder_, level_ + 1, -1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, ct_foreach_stmt_6(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, KW_CT_ENDFOREACH) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (COMMA CT_IDENT)?
  private static boolean ct_foreach_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_foreach_stmt_2")) return false;
    ct_foreach_stmt_2_0(builder_, level_ + 1);
    return true;
  }

  // COMMA CT_IDENT
  private static boolean ct_foreach_stmt_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_foreach_stmt_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, COMMA, CT_IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // statement_list?
  private static boolean ct_foreach_stmt_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_foreach_stmt_6")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_IF constant_expr COLON statement_list? (KW_CT_ELSE statement_list?)? KW_CT_ENDIF
  public static boolean ct_if_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_if_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_IF)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_IF_STMT, null);
    result_ = consumeToken(builder_, KW_CT_IF);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, constant_expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, ct_if_stmt_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, ct_if_stmt_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, KW_CT_ENDIF) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // statement_list?
  private static boolean ct_if_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_if_stmt_3")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  // (KW_CT_ELSE statement_list?)?
  private static boolean ct_if_stmt_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_if_stmt_4")) return false;
    ct_if_stmt_4_0(builder_, level_ + 1);
    return true;
  }

  // KW_CT_ELSE statement_list?
  private static boolean ct_if_stmt_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_if_stmt_4_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_ELSE);
    result_ = result_ && ct_if_stmt_4_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // statement_list?
  private static boolean ct_if_stmt_4_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_if_stmt_4_0_1")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_CT_INCLUDE string_expr attributes? EOS
  public static boolean ct_include_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_include_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_INCLUDE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_INCLUDE_STMT, null);
    result_ = consumeToken(builder_, KW_CT_INCLUDE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, string_expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, ct_include_stmt_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attributes?
  private static boolean ct_include_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_include_stmt_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // ct_case_stmt+
  public static boolean ct_switch_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_switch_body")) return false;
    if (!nextTokenIs(builder_, "<ct switch body>", KW_CT_CASE, KW_CT_DEFAULT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_SWITCH_BODY, "<ct switch body>");
    result_ = ct_case_stmt(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!ct_case_stmt(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ct_switch_body", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_CT_SWITCH constant_expr? COLON ct_switch_body KW_CT_ENDSWITCH
  public static boolean ct_switch_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_switch_stmt")) return false;
    if (!nextTokenIs(builder_, KW_CT_SWITCH)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CT_SWITCH_STMT, null);
    result_ = consumeToken(builder_, KW_CT_SWITCH);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, ct_switch_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, ct_switch_body(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, KW_CT_ENDSWITCH) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // constant_expr?
  private static boolean ct_switch_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_switch_stmt_1")) return false;
    constant_expr(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // var_decl | optional_type local_decl_after_type | expr
  public static boolean decl_or_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "decl_or_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, DECL_OR_EXPR, "<decl or expr>");
    result_ = var_decl(builder_, level_ + 1);
    if (!result_) result_ = decl_or_expr_1(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // optional_type local_decl_after_type
  private static boolean decl_or_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "decl_or_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = optional_type(builder_, level_ + 1);
    result_ = result_ && local_decl_after_type(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // local_decl_after_type (COMMA local_decl_after_type)*
  public static boolean decl_stmt_after_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "decl_stmt_after_type")) return false;
    if (!nextTokenIs(builder_, "<decl stmt after type>", CT_IDENT, IDENT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DECL_STMT_AFTER_TYPE, "<decl stmt after type>");
    result_ = local_decl_after_type(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && decl_stmt_after_type_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (COMMA local_decl_after_type)*
  private static boolean decl_stmt_after_type_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "decl_stmt_after_type_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!decl_stmt_after_type_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "decl_stmt_after_type_1", pos_)) break;
    }
    return true;
  }

  // COMMA local_decl_after_type
  private static boolean decl_stmt_after_type_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "decl_stmt_after_type_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && local_decl_after_type(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // attribute (COMMA attribute)* COMMA?
  public static boolean def_attr_values(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "def_attr_values")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DEF_ATTR_VALUES, "<def attr values>");
    result_ = attribute(builder_, level_ + 1);
    result_ = result_ && def_attr_values_1(builder_, level_ + 1);
    result_ = result_ && def_attr_values_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA attribute)*
  private static boolean def_attr_values_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "def_attr_values_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!def_attr_values_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "def_attr_values_1", pos_)) break;
    }
    return true;
  }

  // COMMA attribute
  private static boolean def_attr_values_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "def_attr_values_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && attribute(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean def_attr_values_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "def_attr_values_2")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // top_level+
  public static boolean default_module_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_module_section")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DEFAULT_MODULE_SECTION, "<default module section>");
    result_ = top_level(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!top_level(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "default_module_section", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_DEFAULT COLON statement_list?
  public static boolean default_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_stmt")) return false;
    if (!nextTokenIs(builder_, KW_DEFAULT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DEFAULT_STMT, null);
    result_ = consumeTokens(builder_, 1, KW_DEFAULT, COLON);
    pinned_ = result_; // pin = 1
    result_ = result_ && default_stmt_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // statement_list?
  private static boolean default_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_stmt_2")) return false;
    statement_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_DEFER (KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP))? statement
  public static boolean defer_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defer_stmt")) return false;
    if (!nextTokenIs(builder_, KW_DEFER)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DEFER_STMT, null);
    result_ = consumeToken(builder_, KW_DEFER);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, defer_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && statement(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP))?
  private static boolean defer_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defer_stmt_1")) return false;
    defer_stmt_1_0(builder_, level_ + 1);
    return true;
  }

  // KW_TRY | KW_CATCH | (LP KW_CATCH IDENT RP)
  private static boolean defer_stmt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defer_stmt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_TRY);
    if (!result_) result_ = consumeToken(builder_, KW_CATCH);
    if (!result_) result_ = defer_stmt_1_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LP KW_CATCH IDENT RP
  private static boolean defer_stmt_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "defer_stmt_1_0_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, LP, KW_CATCH, IDENT, RP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_DO label? compound_statement (KW_WHILE grouped_expr)? EOS
  public static boolean do_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "do_stmt")) return false;
    if (!nextTokenIs(builder_, KW_DO)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, DO_STMT, null);
    result_ = consumeToken(builder_, KW_DO);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, do_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, compound_statement(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, do_stmt_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // label?
  private static boolean do_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "do_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  // (KW_WHILE grouped_expr)?
  private static boolean do_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "do_stmt_3")) return false;
    do_stmt_3_0(builder_, level_ + 1);
    return true;
  }

  // KW_WHILE grouped_expr
  private static boolean do_stmt_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "do_stmt_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_WHILE);
    result_ = result_ && grouped_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // DOT access_ident
  static boolean dot_access_ident(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dot_access_ident")) return false;
    if (!nextTokenIs(builder_, DOT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, DOT);
    pinned_ = result_; // pin = 1
    result_ = result_ && access_ident(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // KW_ELSE (if_stmt | compound_statement)
  public static boolean else_part(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "else_part")) return false;
    if (!nextTokenIs(builder_, KW_ELSE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_ELSE);
    result_ = result_ && else_part_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, ELSE_PART, result_);
    return result_;
  }

  // if_stmt | compound_statement
  private static boolean else_part_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "else_part_1")) return false;
    boolean result_;
    result_ = if_stmt(builder_, level_ + 1);
    if (!result_) result_ = compound_statement(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // CONST_IDENT attributes? initializer_list?
  public static boolean enum_constant(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_constant")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CONST_IDENT);
    result_ = result_ && enum_constant_1(builder_, level_ + 1);
    result_ = result_ && enum_constant_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, ENUM_CONSTANT, result_);
    return result_;
  }

  // attributes?
  private static boolean enum_constant_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_constant_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // initializer_list?
  private static boolean enum_constant_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_constant_2")) return false;
    initializer_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_ENUM type_name interface_impl? (COLON KW_INLINE? type? enum_param_list?)? generic_decl? attributes? LB enum_list RB
  public static boolean enum_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration")) return false;
    if (!nextTokenIs(builder_, KW_ENUM)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ENUM_DECLARATION, null);
    result_ = consumeToken(builder_, KW_ENUM);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, enum_declaration_2(builder_, level_ + 1));
    result_ = pinned_ && enum_declaration_3(builder_, level_ + 1) && result_;
    result_ = pinned_ && enum_declaration_4(builder_, level_ + 1) && result_;
    result_ = pinned_ && enum_declaration_5(builder_, level_ + 1) && result_;
    result_ = pinned_ && consumeToken(builder_, LB) && result_;
    if (pinned_) {
      enum_list(builder_, level_ + 1);
      consumeToken(builder_, RB);
      result_ = true;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // interface_impl?
  private static boolean enum_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_2")) return false;
    interface_impl(builder_, level_ + 1);
    return true;
  }

  // (COLON KW_INLINE? type? enum_param_list?)?
  private static boolean enum_declaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_3")) return false;
    enum_declaration_3_0(builder_, level_ + 1);
    return true;
  }

  // COLON KW_INLINE? type? enum_param_list?
  private static boolean enum_declaration_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && enum_declaration_3_0_1(builder_, level_ + 1);
    result_ = result_ && enum_declaration_3_0_2(builder_, level_ + 1);
    result_ = result_ && enum_declaration_3_0_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_INLINE?
  private static boolean enum_declaration_3_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_3_0_1")) return false;
    consumeToken(builder_, KW_INLINE);
    return true;
  }

  // type?
  private static boolean enum_declaration_3_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_3_0_2")) return false;
    type(builder_, level_ + 1);
    return true;
  }

  // enum_param_list?
  private static boolean enum_declaration_3_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_3_0_3")) return false;
    enum_param_list(builder_, level_ + 1);
    return true;
  }

  // generic_decl?
  private static boolean enum_declaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_4")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean enum_declaration_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_declaration_5")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (enum_constant COMMA?)+
  public static boolean enum_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_list")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = enum_list_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!nextTokenIs(builder_, CONST_IDENT)) break;
      if (!enum_list_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "enum_list", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, ENUM_LIST, result_);
    return result_;
  }

  // enum_constant COMMA?
  private static boolean enum_list_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_list_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = enum_constant(builder_, level_ + 1);
    result_ = result_ && enum_list_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean enum_list_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_list_0_1")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // type IDENT
  public static boolean enum_param_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_param_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ENUM_PARAM_DECL, "<enum param decl>");
    result_ = type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LP enum_param_decl (COMMA enum_param_decl)* RP
  public static boolean enum_param_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_param_list")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && enum_param_decl(builder_, level_ + 1);
    result_ = result_ && enum_param_list_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, ENUM_PARAM_LIST, result_);
    return result_;
  }

  // (COMMA enum_param_decl)*
  private static boolean enum_param_list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_param_list_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!enum_param_list_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "enum_param_list_2", pos_)) break;
    }
    return true;
  }

  // COMMA enum_param_decl
  private static boolean enum_param_list_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_param_list_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && enum_param_decl(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // EQ expr
  static boolean eq_expr_pin(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "eq_expr_pin")) return false;
    if (!nextTokenIs(builder_, EQ)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, EQ);
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // expr EOS
  public static boolean expr_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr_stmt")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, EXPR_STMT, "<expr stmt>");
    result_ = expr(builder_, level_ + 1, -1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // decl_or_expr (COMMA decl_or_expr)*
  public static boolean expression_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, EXPRESSION_LIST, "<expression list>");
    result_ = decl_or_expr(builder_, level_ + 1);
    result_ = result_ && expression_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA decl_or_expr)*
  private static boolean expression_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!expression_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "expression_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA decl_or_expr
  private static boolean expression_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && decl_or_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // CONST_IDENT attributes?
  public static boolean fault_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fault_definition")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CONST_IDENT);
    result_ = result_ && fault_definition_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAULT_DEFINITION, result_);
    return result_;
  }

  // attributes?
  private static boolean fault_definition_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fault_definition_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FAULTDEF ((fault_definition (COMMA fault_definition)* COMMA? EOS) | (LB fault_definition (COMMA fault_definition)* COMMA? RB))
  public static boolean faultdef_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl")) return false;
    if (!nextTokenIs(builder_, KW_FAULTDEF)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_FAULTDEF);
    result_ = result_ && faultdef_decl_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAULTDEF_DECL, result_);
    return result_;
  }

  // (fault_definition (COMMA fault_definition)* COMMA? EOS) | (LB fault_definition (COMMA fault_definition)* COMMA? RB)
  private static boolean faultdef_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = faultdef_decl_1_0(builder_, level_ + 1);
    if (!result_) result_ = faultdef_decl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // fault_definition (COMMA fault_definition)* COMMA? EOS
  private static boolean faultdef_decl_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = fault_definition(builder_, level_ + 1);
    result_ = result_ && faultdef_decl_1_0_1(builder_, level_ + 1);
    result_ = result_ && faultdef_decl_1_0_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA fault_definition)*
  private static boolean faultdef_decl_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_0_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!faultdef_decl_1_0_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "faultdef_decl_1_0_1", pos_)) break;
    }
    return true;
  }

  // COMMA fault_definition
  private static boolean faultdef_decl_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && fault_definition(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean faultdef_decl_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_0_2")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  // LB fault_definition (COMMA fault_definition)* COMMA? RB
  private static boolean faultdef_decl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && fault_definition(builder_, level_ + 1);
    result_ = result_ && faultdef_decl_1_1_2(builder_, level_ + 1);
    result_ = result_ && faultdef_decl_1_1_3(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA fault_definition)*
  private static boolean faultdef_decl_1_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_1_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!faultdef_decl_1_1_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "faultdef_decl_1_1_2", pos_)) break;
    }
    return true;
  }

  // COMMA fault_definition
  private static boolean faultdef_decl_1_1_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_1_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && fault_definition(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean faultdef_decl_1_1_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "faultdef_decl_1_1_3")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // KW_FLOAT16 | KW_FLOAT | KW_DOUBLE | KW_FLOAT128 | KW_BFLOAT16
  public static boolean float_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "float_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FLOAT_TYPE, "<float type>");
    result_ = consumeToken(builder_, KW_FLOAT16);
    if (!result_) result_ = consumeToken(builder_, KW_FLOAT);
    if (!result_) result_ = consumeToken(builder_, KW_DOUBLE);
    if (!result_) result_ = consumeToken(builder_, KW_FLOAT128);
    if (!result_) result_ = consumeToken(builder_, KW_BFLOAT16);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LP parameter_list? COMMA? RP
  public static boolean fn_parameter_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fn_parameter_list")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && fn_parameter_list_1(builder_, level_ + 1);
    result_ = result_ && fn_parameter_list_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, FN_PARAMETER_LIST, result_);
    return result_;
  }

  // parameter_list?
  private static boolean fn_parameter_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fn_parameter_list_1")) return false;
    parameter_list(builder_, level_ + 1);
    return true;
  }

  // COMMA?
  private static boolean fn_parameter_list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fn_parameter_list_2")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // expression_list? EOS cond? EOS expression_list?
  public static boolean for_cond(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_cond")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOR_COND, "<for cond>");
    result_ = for_cond_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOS);
    result_ = result_ && for_cond_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOS);
    result_ = result_ && for_cond_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // expression_list?
  private static boolean for_cond_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_cond_0")) return false;
    expression_list(builder_, level_ + 1);
    return true;
  }

  // cond?
  private static boolean for_cond_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_cond_2")) return false;
    cond(builder_, level_ + 1);
    return true;
  }

  // expression_list?
  private static boolean for_cond_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_cond_4")) return false;
    expression_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FOR label? LP for_cond RP statement
  public static boolean for_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_stmt")) return false;
    if (!nextTokenIs(builder_, KW_FOR)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOR_STMT, null);
    result_ = consumeToken(builder_, KW_FOR);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, for_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, LP)) && result_;
    result_ = pinned_ && report_error_(builder_, for_cond(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, RP)) && result_;
    result_ = pinned_ && statement(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // label?
  private static boolean for_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (KW_FOREACH | KW_FOREACH_R) label? LP foreach_vars COLON expr RP statement
  public static boolean foreach_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_stmt")) return false;
    if (!nextTokenIs(builder_, "<foreach stmt>", KW_FOREACH, KW_FOREACH_R)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOREACH_STMT, "<foreach stmt>");
    result_ = foreach_stmt_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, foreach_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, LP)) && result_;
    result_ = pinned_ && report_error_(builder_, foreach_vars(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, COLON)) && result_;
    result_ = pinned_ && report_error_(builder_, expr(builder_, level_ + 1, -1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, RP)) && result_;
    result_ = pinned_ && statement(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_FOREACH | KW_FOREACH_R
  private static boolean foreach_stmt_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_stmt_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, KW_FOREACH);
    if (!result_) result_ = consumeToken(builder_, KW_FOREACH_R);
    return result_;
  }

  // label?
  private static boolean foreach_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // optional_type? AMP? IDENT
  public static boolean foreach_var(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_var")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOREACH_VAR, "<foreach var>");
    result_ = foreach_var_0(builder_, level_ + 1);
    result_ = result_ && foreach_var_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // optional_type?
  private static boolean foreach_var_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_var_0")) return false;
    optional_type(builder_, level_ + 1);
    return true;
  }

  // AMP?
  private static boolean foreach_var_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_var_1")) return false;
    consumeToken(builder_, AMP);
    return true;
  }

  /* ********************************************************** */
  // foreach_var (COMMA foreach_var)?
  public static boolean foreach_vars(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_vars")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOREACH_VARS, "<foreach vars>");
    result_ = foreach_var(builder_, level_ + 1);
    result_ = result_ && foreach_vars_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA foreach_var)?
  private static boolean foreach_vars_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_vars_1")) return false;
    foreach_vars_1_0(builder_, level_ + 1);
    return true;
  }

  // COMMA foreach_var
  private static boolean foreach_vars_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_vars_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && foreach_var(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_FN func_header fn_parameter_list generic_decl? attributes?
  public static boolean func_def(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_def")) return false;
    if (!nextTokenIs(builder_, KW_FN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_FN);
    result_ = result_ && func_header(builder_, level_ + 1);
    result_ = result_ && fn_parameter_list(builder_, level_ + 1);
    result_ = result_ && func_def_3(builder_, level_ + 1);
    result_ = result_ && func_def_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, FUNC_DEF, result_);
    return result_;
  }

  // generic_decl?
  private static boolean func_def_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_def_3")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean func_def_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_def_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // func_def (macro_func_body | EOS)
  public static boolean func_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_definition")) return false;
    if (!nextTokenIs(builder_, KW_FN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNC_DEFINITION, null);
    result_ = func_def(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && func_definition_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // macro_func_body | EOS
  private static boolean func_definition_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_definition_1")) return false;
    boolean result_;
    result_ = macro_func_body(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, EOS);
    return result_;
  }

  /* ********************************************************** */
  // optional_type func_name
  public static boolean func_header(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_header")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNC_HEADER, "<func header>");
    result_ = optional_type(builder_, level_ + 1);
    result_ = result_ && func_name(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (type DOT)? IDENT
  public static boolean func_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_name")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNC_NAME, "<func name>");
    result_ = func_name_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (type DOT)?
  private static boolean func_name_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_name_0")) return false;
    func_name_0_0(builder_, level_ + 1);
    return true;
  }

  // type DOT
  private static boolean func_name_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "func_name_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LT_OP module_params GT_OP
  public static boolean generic_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "generic_decl")) return false;
    if (!nextTokenIs(builder_, LT_OP)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, GENERIC_DECL, null);
    result_ = consumeToken(builder_, LT_OP);
    result_ = result_ && module_params(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, GT_OP);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // expr | type
  public static boolean generic_parameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "generic_parameter")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, GENERIC_PARAMETER, "<generic parameter>");
    result_ = expr(builder_, level_ + 1, -1);
    if (!result_) result_ = type(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LB generic_parameter (COMMA generic_parameter)* RB
  public static boolean generic_parameters(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "generic_parameters")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && generic_parameter(builder_, level_ + 1);
    result_ = result_ && generic_parameters_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, marker_, GENERIC_PARAMETERS, result_);
    return result_;
  }

  // (COMMA generic_parameter)*
  private static boolean generic_parameters_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "generic_parameters_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!generic_parameters_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "generic_parameters_2", pos_)) break;
    }
    return true;
  }

  // COMMA generic_parameter
  private static boolean generic_parameters_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "generic_parameters_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && generic_parameter(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_TLOCAL? optional_type IDENT ((COMMA IDENT)+ generic_decl? attributes? | generic_decl? attributes? eq_expr_pin?) EOS
  public static boolean global_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, GLOBAL_DECL, "<global decl>");
    result_ = global_decl_0(builder_, level_ + 1);
    result_ = result_ && optional_type(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, consumeToken(builder_, IDENT));
    result_ = pinned_ && report_error_(builder_, global_decl_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_TLOCAL?
  private static boolean global_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_0")) return false;
    consumeToken(builder_, KW_TLOCAL);
    return true;
  }

  // (COMMA IDENT)+ generic_decl? attributes? | generic_decl? attributes? eq_expr_pin?
  private static boolean global_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = global_decl_3_0(builder_, level_ + 1);
    if (!result_) result_ = global_decl_3_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA IDENT)+ generic_decl? attributes?
  private static boolean global_decl_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = global_decl_3_0_0(builder_, level_ + 1);
    result_ = result_ && global_decl_3_0_1(builder_, level_ + 1);
    result_ = result_ && global_decl_3_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA IDENT)+
  private static boolean global_decl_3_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = global_decl_3_0_0_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!global_decl_3_0_0_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "global_decl_3_0_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA IDENT
  private static boolean global_decl_3_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_0_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, COMMA, IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // generic_decl?
  private static boolean global_decl_3_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_0_1")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean global_decl_3_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_0_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // generic_decl? attributes? eq_expr_pin?
  private static boolean global_decl_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = global_decl_3_1_0(builder_, level_ + 1);
    result_ = result_ && global_decl_3_1_1(builder_, level_ + 1);
    result_ = result_ && global_decl_3_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // generic_decl?
  private static boolean global_decl_3_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_1_0")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean global_decl_3_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_1_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // eq_expr_pin?
  private static boolean global_decl_3_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_decl_3_1_2")) return false;
    eq_expr_pin(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENT | TYPE_IDENT | CT_TYPE_IDENT | CT_IDENT | CONST_IDENT | AT_IDENT
  static boolean ident_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ident_list")) return false;
    boolean result_;
    result_ = consumeToken(builder_, IDENT);
    if (!result_) result_ = consumeToken(builder_, TYPE_IDENT);
    if (!result_) result_ = consumeToken(builder_, CT_TYPE_IDENT);
    if (!result_) result_ = consumeToken(builder_, CT_IDENT);
    if (!result_) result_ = consumeToken(builder_, CONST_IDENT);
    if (!result_) result_ = consumeToken(builder_, AT_IDENT);
    return result_;
  }

  /* ********************************************************** */
  // IDENT (COMMA IDENT)*
  public static boolean identifier_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier_list")) return false;
    if (!nextTokenIs(builder_, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENT);
    result_ = result_ && identifier_list_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, IDENTIFIER_LIST, result_);
    return result_;
  }

  // (COMMA IDENT)*
  private static boolean identifier_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!identifier_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "identifier_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA IDENT
  private static boolean identifier_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, COMMA, IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_IF label? paren_cond (compound_statement else_part | statement)
  public static boolean if_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_stmt")) return false;
    if (!nextTokenIs(builder_, KW_IF)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, IF_STMT, null);
    result_ = consumeToken(builder_, KW_IF);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, if_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, paren_cond(builder_, level_ + 1)) && result_;
    result_ = pinned_ && if_stmt_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // label?
  private static boolean if_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  // compound_statement else_part | statement
  private static boolean if_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_stmt_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = if_stmt_3_0(builder_, level_ + 1);
    if (!result_) result_ = statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // compound_statement else_part
  private static boolean if_stmt_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_stmt_3_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = compound_statement(builder_, level_ + 1);
    result_ = result_ && else_part(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_IMPORT import_paths EOS
  public static boolean import_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_decl")) return false;
    if (!nextTokenIs(builder_, KW_IMPORT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, IMPORT_DECL, null);
    result_ = consumeToken(builder_, KW_IMPORT);
    result_ = result_ && import_paths(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // IDENT (SCOPE IDENT)* attributes?
  public static boolean import_path(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_path")) return false;
    if (!nextTokenIs(builder_, IDENT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, IMPORT_PATH, null);
    result_ = consumeToken(builder_, IDENT);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, import_path_1(builder_, level_ + 1));
    result_ = pinned_ && import_path_2(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (SCOPE IDENT)*
  private static boolean import_path_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_path_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!import_path_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "import_path_1", pos_)) break;
    }
    return true;
  }

  // SCOPE IDENT
  private static boolean import_path_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_path_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeTokens(builder_, 1, SCOPE, IDENT);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attributes?
  private static boolean import_path_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_path_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // import_path (COMMA import_path)*
  public static boolean import_paths(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_paths")) return false;
    if (!nextTokenIs(builder_, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = import_path(builder_, level_ + 1);
    result_ = result_ && import_paths_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, IMPORT_PATHS, result_);
    return result_;
  }

  // (COMMA import_path)*
  private static boolean import_paths_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_paths_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!import_paths_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "import_paths_1", pos_)) break;
    }
    return true;
  }

  // COMMA import_path
  private static boolean import_paths_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_paths_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && import_path(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LB (arg_list COMMA?)? RB
  public static boolean initializer_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializer_list")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INITIALIZER_LIST, null);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && initializer_list_1(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (arg_list COMMA?)?
  private static boolean initializer_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializer_list_1")) return false;
    initializer_list_1_0(builder_, level_ + 1);
    return true;
  }

  // arg_list COMMA?
  private static boolean initializer_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializer_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = arg_list(builder_, level_ + 1);
    result_ = result_ && initializer_list_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // COMMA?
  private static boolean initializer_list_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializer_list_1_0_1")) return false;
    consumeToken(builder_, COMMA);
    return true;
  }

  /* ********************************************************** */
  // KW_CHAR | KW_ICHAR | KW_SHORT | KW_USHORT | KW_INT | KW_UINT | KW_LONG | KW_ULONG | KW_INT128 | KW_UINT128
  public static boolean integer_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "integer_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INTEGER_TYPE, "<integer type>");
    result_ = consumeToken(builder_, KW_CHAR);
    if (!result_) result_ = consumeToken(builder_, KW_ICHAR);
    if (!result_) result_ = consumeToken(builder_, KW_SHORT);
    if (!result_) result_ = consumeToken(builder_, KW_USHORT);
    if (!result_) result_ = consumeToken(builder_, KW_INT);
    if (!result_) result_ = consumeToken(builder_, KW_UINT);
    if (!result_) result_ = consumeToken(builder_, KW_LONG);
    if (!result_) result_ = consumeToken(builder_, KW_ULONG);
    if (!result_) result_ = consumeToken(builder_, KW_INT128);
    if (!result_) result_ = consumeToken(builder_, KW_UINT128);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LB (func_def EOS)* RB
  public static boolean interface_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_body")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && interface_body_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, marker_, INTERFACE_BODY, result_);
    return result_;
  }

  // (func_def EOS)*
  private static boolean interface_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!interface_body_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interface_body_1", pos_)) break;
    }
    return true;
  }

  // func_def EOS
  private static boolean interface_body_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_body_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = func_def(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_INTERFACE type_name (COLON type (COMMA type)*)? interface_body
  public static boolean interface_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_definition")) return false;
    if (!nextTokenIs(builder_, KW_INTERFACE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_INTERFACE);
    result_ = result_ && type_name(builder_, level_ + 1);
    result_ = result_ && interface_definition_2(builder_, level_ + 1);
    result_ = result_ && interface_body(builder_, level_ + 1);
    exit_section_(builder_, marker_, INTERFACE_DEFINITION, result_);
    return result_;
  }

  // (COLON type (COMMA type)*)?
  private static boolean interface_definition_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_definition_2")) return false;
    interface_definition_2_0(builder_, level_ + 1);
    return true;
  }

  // COLON type (COMMA type)*
  private static boolean interface_definition_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_definition_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && interface_definition_2_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMMA type)*
  private static boolean interface_definition_2_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_definition_2_0_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!interface_definition_2_0_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interface_definition_2_0_2", pos_)) break;
    }
    return true;
  }

  // COMMA type
  private static boolean interface_definition_2_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_definition_2_0_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && type(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LP type_name (COMMA type)* RP
  public static boolean interface_impl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_impl")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && type_name(builder_, level_ + 1);
    result_ = result_ && interface_impl_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, INTERFACE_IMPL, result_);
    return result_;
  }

  // (COMMA type)*
  private static boolean interface_impl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_impl_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!interface_impl_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "interface_impl_2", pos_)) break;
    }
    return true;
  }

  // COMMA type
  private static boolean interface_impl_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_impl_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && type(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_ALIAS | KW_ANY | KW_ATTRDEF | KW_ASM | KW_ASSERT
  //     | KW_BITSTRUCT | KW_BREAK | KW_CASE | KW_CATCH | KW_CONST | KW_CONTINUE
  //     | KW_DEFAULT | KW_DEFER | KW_DO | KW_ELSE | KW_ENUM | KW_EXTERN | KW_FOREACH
  //     | KW_FOREACH_R | KW_FALSE | KW_FAULT | KW_FAULTDEF | KW_FOR | KW_FN | KW_IF
  //     | KW_INLINE | KW_INTERFACE | KW_IMPORT | KW_MACRO | KW_MODULE | KW_NEXTCASE
  //     | KW_NULL | KW_RETURN | KW_STATIC | KW_STRUCT | KW_SWITCH | KW_TLOCAL | KW_TRUE
  //     | KW_TRY | KW_TYPEDEF | KW_TYPEID | KW_UNION | KW_VAR | KW_WHILE
  //     | KW_CT_ASSERT | KW_CT_CASE | KW_CT_DEFAULT | KW_CT_DEFINED | KW_CT_ECHO
  //     | KW_CT_ELSE | KW_CT_ENDFOR | KW_CT_ENDFOREACH | KW_CT_ENDIF | KW_CT_ENDSWITCH
  //     | KW_CT_ERROR | KW_CT_EVAL | KW_CT_EVALTYPE | KW_CT_FEATURE | KW_CT_EXEC
  //     | KW_CT_FOR | KW_CT_FOREACH | KW_CT_IF | KW_CT_INCLUDE | KW_CT_EXPAND
  //     | KW_CT_REFLECT
  //     | KW_CT_STRINGIFY | KW_CT_SWITCH | KW_CT_TYPEOF
  //     | KW_CT_TYPEFROM
  //     | KW_CT_VAARG | KW_VOID | KW_BOOL
  //     | KW_CHAR | KW_ICHAR | KW_SHORT | KW_USHORT | KW_INT | KW_UINT | KW_LONG
  //     | KW_ULONG | KW_UINT128 | KW_INT128 | KW_BFLOAT16 | KW_DOUBLE | KW_FLOAT | KW_FLOAT16
  //     | KW_FLOAT128 | KW_UPTR | KW_IPTR | KW_USZ | KW_SZ | KW_UNTYPEDLIST
  static boolean keyword_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keyword_list")) return false;
    boolean result_;
    result_ = consumeToken(builder_, KW_ALIAS);
    if (!result_) result_ = consumeToken(builder_, KW_ANY);
    if (!result_) result_ = consumeToken(builder_, KW_ATTRDEF);
    if (!result_) result_ = consumeToken(builder_, KW_ASM);
    if (!result_) result_ = consumeToken(builder_, KW_ASSERT);
    if (!result_) result_ = consumeToken(builder_, KW_BITSTRUCT);
    if (!result_) result_ = consumeToken(builder_, KW_BREAK);
    if (!result_) result_ = consumeToken(builder_, KW_CASE);
    if (!result_) result_ = consumeToken(builder_, KW_CATCH);
    if (!result_) result_ = consumeToken(builder_, KW_CONST);
    if (!result_) result_ = consumeToken(builder_, KW_CONTINUE);
    if (!result_) result_ = consumeToken(builder_, KW_DEFAULT);
    if (!result_) result_ = consumeToken(builder_, KW_DEFER);
    if (!result_) result_ = consumeToken(builder_, KW_DO);
    if (!result_) result_ = consumeToken(builder_, KW_ELSE);
    if (!result_) result_ = consumeToken(builder_, KW_ENUM);
    if (!result_) result_ = consumeToken(builder_, KW_EXTERN);
    if (!result_) result_ = consumeToken(builder_, KW_FOREACH);
    if (!result_) result_ = consumeToken(builder_, KW_FOREACH_R);
    if (!result_) result_ = consumeToken(builder_, KW_FALSE);
    if (!result_) result_ = consumeToken(builder_, KW_FAULT);
    if (!result_) result_ = consumeToken(builder_, KW_FAULTDEF);
    if (!result_) result_ = consumeToken(builder_, KW_FOR);
    if (!result_) result_ = consumeToken(builder_, KW_FN);
    if (!result_) result_ = consumeToken(builder_, KW_IF);
    if (!result_) result_ = consumeToken(builder_, KW_INLINE);
    if (!result_) result_ = consumeToken(builder_, KW_INTERFACE);
    if (!result_) result_ = consumeToken(builder_, KW_IMPORT);
    if (!result_) result_ = consumeToken(builder_, KW_MACRO);
    if (!result_) result_ = consumeToken(builder_, KW_MODULE);
    if (!result_) result_ = consumeToken(builder_, KW_NEXTCASE);
    if (!result_) result_ = consumeToken(builder_, KW_NULL);
    if (!result_) result_ = consumeToken(builder_, KW_RETURN);
    if (!result_) result_ = consumeToken(builder_, KW_STATIC);
    if (!result_) result_ = consumeToken(builder_, KW_STRUCT);
    if (!result_) result_ = consumeToken(builder_, KW_SWITCH);
    if (!result_) result_ = consumeToken(builder_, KW_TLOCAL);
    if (!result_) result_ = consumeToken(builder_, KW_TRUE);
    if (!result_) result_ = consumeToken(builder_, KW_TRY);
    if (!result_) result_ = consumeToken(builder_, KW_TYPEDEF);
    if (!result_) result_ = consumeToken(builder_, KW_TYPEID);
    if (!result_) result_ = consumeToken(builder_, KW_UNION);
    if (!result_) result_ = consumeToken(builder_, KW_VAR);
    if (!result_) result_ = consumeToken(builder_, KW_WHILE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ASSERT);
    if (!result_) result_ = consumeToken(builder_, KW_CT_CASE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_DEFAULT);
    if (!result_) result_ = consumeToken(builder_, KW_CT_DEFINED);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ECHO);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ELSE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ENDFOR);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ENDFOREACH);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ENDIF);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ENDSWITCH);
    if (!result_) result_ = consumeToken(builder_, KW_CT_ERROR);
    if (!result_) result_ = consumeToken(builder_, KW_CT_EVAL);
    if (!result_) result_ = consumeToken(builder_, KW_CT_EVALTYPE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_FEATURE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_EXEC);
    if (!result_) result_ = consumeToken(builder_, KW_CT_FOR);
    if (!result_) result_ = consumeToken(builder_, KW_CT_FOREACH);
    if (!result_) result_ = consumeToken(builder_, KW_CT_IF);
    if (!result_) result_ = consumeToken(builder_, KW_CT_INCLUDE);
    if (!result_) result_ = consumeToken(builder_, KW_CT_EXPAND);
    if (!result_) result_ = consumeToken(builder_, KW_CT_REFLECT);
    if (!result_) result_ = consumeToken(builder_, KW_CT_STRINGIFY);
    if (!result_) result_ = consumeToken(builder_, KW_CT_SWITCH);
    if (!result_) result_ = consumeToken(builder_, KW_CT_TYPEOF);
    if (!result_) result_ = consumeToken(builder_, KW_CT_TYPEFROM);
    if (!result_) result_ = consumeToken(builder_, KW_CT_VAARG);
    if (!result_) result_ = consumeToken(builder_, KW_VOID);
    if (!result_) result_ = consumeToken(builder_, KW_BOOL);
    if (!result_) result_ = consumeToken(builder_, KW_CHAR);
    if (!result_) result_ = consumeToken(builder_, KW_ICHAR);
    if (!result_) result_ = consumeToken(builder_, KW_SHORT);
    if (!result_) result_ = consumeToken(builder_, KW_USHORT);
    if (!result_) result_ = consumeToken(builder_, KW_INT);
    if (!result_) result_ = consumeToken(builder_, KW_UINT);
    if (!result_) result_ = consumeToken(builder_, KW_LONG);
    if (!result_) result_ = consumeToken(builder_, KW_ULONG);
    if (!result_) result_ = consumeToken(builder_, KW_UINT128);
    if (!result_) result_ = consumeToken(builder_, KW_INT128);
    if (!result_) result_ = consumeToken(builder_, KW_BFLOAT16);
    if (!result_) result_ = consumeToken(builder_, KW_DOUBLE);
    if (!result_) result_ = consumeToken(builder_, KW_FLOAT);
    if (!result_) result_ = consumeToken(builder_, KW_FLOAT16);
    if (!result_) result_ = consumeToken(builder_, KW_FLOAT128);
    if (!result_) result_ = consumeToken(builder_, KW_UPTR);
    if (!result_) result_ = consumeToken(builder_, KW_IPTR);
    if (!result_) result_ = consumeToken(builder_, KW_USZ);
    if (!result_) result_ = consumeToken(builder_, KW_SZ);
    if (!result_) result_ = consumeToken(builder_, KW_UNTYPEDLIST);
    return result_;
  }

  /* ********************************************************** */
  // CONST_IDENT COLON
  public static boolean label(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "label")) return false;
    if (!nextTokenIs(builder_, CONST_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CONST_IDENT, COLON);
    exit_section_(builder_, marker_, LABEL, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_FN optional_type? fn_parameter_list attributes?
  public static boolean lambda_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl")) return false;
    if (!nextTokenIs(builder_, KW_FN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LAMBDA_DECL, null);
    result_ = consumeToken(builder_, KW_FN);
    result_ = result_ && lambda_decl_1(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, fn_parameter_list(builder_, level_ + 1));
    result_ = pinned_ && lambda_decl_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // optional_type?
  private static boolean lambda_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl_1")) return false;
    optional_type(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean lambda_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // CT_IDENT (EQ constant_expr)? | IDENT attributes? (eq_expr_pin)?
  public static boolean local_decl_after_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type")) return false;
    if (!nextTokenIs(builder_, "<local decl after type>", CT_IDENT, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LOCAL_DECL_AFTER_TYPE, "<local decl after type>");
    result_ = local_decl_after_type_0(builder_, level_ + 1);
    if (!result_) result_ = local_decl_after_type_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // CT_IDENT (EQ constant_expr)?
  private static boolean local_decl_after_type_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CT_IDENT);
    result_ = result_ && local_decl_after_type_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (EQ constant_expr)?
  private static boolean local_decl_after_type_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_0_1")) return false;
    local_decl_after_type_0_1_0(builder_, level_ + 1);
    return true;
  }

  // EQ constant_expr
  private static boolean local_decl_after_type_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EQ);
    result_ = result_ && constant_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IDENT attributes? (eq_expr_pin)?
  private static boolean local_decl_after_type_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENT);
    result_ = result_ && local_decl_after_type_1_1(builder_, level_ + 1);
    result_ = result_ && local_decl_after_type_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean local_decl_after_type_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_1_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // (eq_expr_pin)?
  private static boolean local_decl_after_type_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_1_2")) return false;
    local_decl_after_type_1_2_0(builder_, level_ + 1);
    return true;
  }

  // (eq_expr_pin)
  private static boolean local_decl_after_type_1_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_after_type_1_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = eq_expr_pin(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_STATIC | KW_TLOCAL
  public static boolean local_decl_storage(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_decl_storage")) return false;
    if (!nextTokenIs(builder_, "<local decl storage>", KW_STATIC, KW_TLOCAL)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LOCAL_DECL_STORAGE, "<local decl storage>");
    result_ = consumeToken(builder_, KW_STATIC);
    if (!result_) result_ = consumeToken(builder_, KW_TLOCAL);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // local_decl_storage? optional_type decl_stmt_after_type EOS
  public static boolean local_declaration_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_declaration_stmt")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LOCAL_DECLARATION_STMT, "<local declaration stmt>");
    result_ = local_declaration_stmt_0(builder_, level_ + 1);
    result_ = result_ && optional_type(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, decl_stmt_after_type(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // local_decl_storage?
  private static boolean local_declaration_stmt_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_declaration_stmt_0")) return false;
    local_decl_storage(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_MACRO macro_header LP macro_params RP generic_decl? attributes? macro_func_body
  public static boolean macro_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition")) return false;
    if (!nextTokenIs(builder_, KW_MACRO)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_MACRO);
    result_ = result_ && macro_header(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, LP);
    result_ = result_ && macro_params(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    result_ = result_ && macro_definition_5(builder_, level_ + 1);
    result_ = result_ && macro_definition_6(builder_, level_ + 1);
    result_ = result_ && macro_func_body(builder_, level_ + 1);
    exit_section_(builder_, marker_, MACRO_DEFINITION, result_);
    return result_;
  }

  // generic_decl?
  private static boolean macro_definition_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition_5")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean macro_definition_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition_6")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // macro_implies_body | IMPLIES expr EOS? | compound_statement
  public static boolean macro_func_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_func_body")) return false;
    if (!nextTokenIs(builder_, "<macro func body>", IMPLIES, LB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_FUNC_BODY, "<macro func body>");
    result_ = macro_implies_body(builder_, level_ + 1);
    if (!result_) result_ = macro_func_body_1(builder_, level_ + 1);
    if (!result_) result_ = compound_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // IMPLIES expr EOS?
  private static boolean macro_func_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_func_body_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IMPLIES);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && macro_func_body_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EOS?
  private static boolean macro_func_body_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_func_body_1_2")) return false;
    consumeToken(builder_, EOS);
    return true;
  }

  /* ********************************************************** */
  // (optional_type !DOT)? macro_name
  public static boolean macro_header(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_header")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_HEADER, "<macro header>");
    result_ = macro_header_0(builder_, level_ + 1);
    result_ = result_ && macro_name(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (optional_type !DOT)?
  private static boolean macro_header_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_header_0")) return false;
    macro_header_0_0(builder_, level_ + 1);
    return true;
  }

  // optional_type !DOT
  private static boolean macro_header_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_header_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = optional_type(builder_, level_ + 1);
    result_ = result_ && macro_header_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !DOT
  private static boolean macro_header_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_header_0_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, DOT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // IMPLIES AT_IDENT call_invocation (compound_statement | IMPLIES expr EOS)
  public static boolean macro_implies_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_implies_body")) return false;
    if (!nextTokenIs(builder_, IMPLIES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, IMPLIES, AT_IDENT);
    result_ = result_ && call_invocation(builder_, level_ + 1);
    result_ = result_ && macro_implies_body_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, MACRO_IMPLIES_BODY, result_);
    return result_;
  }

  // compound_statement | IMPLIES expr EOS
  private static boolean macro_implies_body_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_implies_body_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = compound_statement(builder_, level_ + 1);
    if (!result_) result_ = macro_implies_body_3_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IMPLIES expr EOS
  private static boolean macro_implies_body_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_implies_body_3_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IMPLIES);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (type DOT)? (AT_IDENT | IDENT)
  public static boolean macro_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_name")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_NAME, "<macro name>");
    result_ = macro_name_0(builder_, level_ + 1);
    result_ = result_ && macro_name_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (type DOT)?
  private static boolean macro_name_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_name_0")) return false;
    macro_name_0_0(builder_, level_ + 1);
    return true;
  }

  // type DOT
  private static boolean macro_name_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_name_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DOT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // AT_IDENT | IDENT
  private static boolean macro_name_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_name_1")) return false;
    boolean result_;
    result_ = consumeToken(builder_, AT_IDENT);
    if (!result_) result_ = consumeToken(builder_, IDENT);
    return result_;
  }

  /* ********************************************************** */
  // parameter_list? (EOS trailing_block_param)?
  public static boolean macro_params(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_params")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_PARAMS, "<macro params>");
    result_ = macro_params_0(builder_, level_ + 1);
    result_ = result_ && macro_params_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // parameter_list?
  private static boolean macro_params_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_params_0")) return false;
    parameter_list(builder_, level_ + 1);
    return true;
  }

  // (EOS trailing_block_param)?
  private static boolean macro_params_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_params_1")) return false;
    macro_params_1_0(builder_, level_ + 1);
    return true;
  }

  // EOS trailing_block_param
  private static boolean macro_params_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_params_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EOS);
    result_ = result_ && trailing_block_param(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // KW_MODULE module_path (generic_decl)? attributes? EOS
  public static boolean module(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module")) return false;
    if (!nextTokenIs(builder_, KW_MODULE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MODULE, null);
    result_ = consumeToken(builder_, KW_MODULE);
    result_ = result_ && module_path(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, module_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, module_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (generic_decl)?
  private static boolean module_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_2")) return false;
    module_2_0(builder_, level_ + 1);
    return true;
  }

  // (generic_decl)
  private static boolean module_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = generic_decl(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean module_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // TYPE_IDENT | CONST_IDENT
  public static boolean module_param(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_param")) return false;
    if (!nextTokenIs(builder_, "<module param>", CONST_IDENT, TYPE_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MODULE_PARAM, "<module param>");
    result_ = consumeToken(builder_, TYPE_IDENT);
    if (!result_) result_ = consumeToken(builder_, CONST_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // module_param (COMMA module_param)*
  public static boolean module_params(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_params")) return false;
    if (!nextTokenIs(builder_, "<module params>", CONST_IDENT, TYPE_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MODULE_PARAMS, "<module params>");
    result_ = module_param(builder_, level_ + 1);
    result_ = result_ && module_params_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA module_param)*
  private static boolean module_params_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_params_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!module_params_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "module_params_1", pos_)) break;
    }
    return true;
  }

  // COMMA module_param
  private static boolean module_params_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_params_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && module_param(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [(IDENT SCOPE)+] IDENT
  public static boolean module_path(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_path")) return false;
    if (!nextTokenIs(builder_, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = module_path_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    exit_section_(builder_, marker_, MODULE_PATH, result_);
    return result_;
  }

  // [(IDENT SCOPE)+]
  private static boolean module_path_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_path_0")) return false;
    module_path_0_0(builder_, level_ + 1);
    return true;
  }

  // (IDENT SCOPE)+
  private static boolean module_path_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_path_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = module_path_0_0_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!module_path_0_0_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "module_path_0_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IDENT SCOPE
  private static boolean module_path_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_path_0_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, IDENT, SCOPE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // module top_level*
  public static boolean module_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_section")) return false;
    if (!nextTokenIs(builder_, KW_MODULE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = module(builder_, level_ + 1);
    result_ = result_ && module_section_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, MODULE_SECTION, result_);
    return result_;
  }

  // top_level*
  private static boolean module_section_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "module_section_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!top_level(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "module_section_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // STAR | DIV | MOD
  public static boolean mult_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mult_bin_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator>");
    result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = consumeToken(builder_, DIV);
    if (!result_) result_ = consumeToken(builder_, MOD);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CT_IDENT | HASH_IDENT | IDENT | CT_TYPE_IDENT
  public static boolean named_ident(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "named_ident")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NAMED_IDENT, "<named ident>");
    result_ = consumeToken(builder_, CT_IDENT);
    if (!result_) result_ = consumeToken(builder_, HASH_IDENT);
    if (!result_) result_ = consumeToken(builder_, IDENT);
    if (!result_) result_ = consumeToken(builder_, CT_TYPE_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_NEXTCASE ((CONST_IDENT COLON)? (expr | KW_DEFAULT))? EOS
  public static boolean nextcase_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt")) return false;
    if (!nextTokenIs(builder_, KW_NEXTCASE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NEXTCASE_STMT, null);
    result_ = consumeToken(builder_, KW_NEXTCASE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, nextcase_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ((CONST_IDENT COLON)? (expr | KW_DEFAULT))?
  private static boolean nextcase_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt_1")) return false;
    nextcase_stmt_1_0(builder_, level_ + 1);
    return true;
  }

  // (CONST_IDENT COLON)? (expr | KW_DEFAULT)
  private static boolean nextcase_stmt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = nextcase_stmt_1_0_0(builder_, level_ + 1);
    result_ = result_ && nextcase_stmt_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (CONST_IDENT COLON)?
  private static boolean nextcase_stmt_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt_1_0_0")) return false;
    nextcase_stmt_1_0_0_0(builder_, level_ + 1);
    return true;
  }

  // CONST_IDENT COLON
  private static boolean nextcase_stmt_1_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt_1_0_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CONST_IDENT, COLON);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // expr | KW_DEFAULT
  private static boolean nextcase_stmt_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nextcase_stmt_1_0_1")) return false;
    boolean result_;
    result_ = expr(builder_, level_ + 1, -1);
    if (!result_) result_ = consumeToken(builder_, KW_DEFAULT);
    return result_;
  }

  /* ********************************************************** */
  // type QUESTION?
  public static boolean optional_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "optional_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, OPTIONAL_TYPE, "<optional type>");
    result_ = type(builder_, level_ + 1);
    result_ = result_ && optional_type_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // QUESTION?
  private static boolean optional_type_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "optional_type_1")) return false;
    consumeToken(builder_, QUESTION);
    return true;
  }

  /* ********************************************************** */
  // parameter ((EQ ELLIPSIS) | [eq_expr_pin])
  public static boolean param_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAM_DECL, "<param decl>");
    result_ = parameter(builder_, level_ + 1);
    result_ = result_ && param_decl_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (EQ ELLIPSIS) | [eq_expr_pin]
  private static boolean param_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_decl_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = param_decl_1_0(builder_, level_ + 1);
    if (!result_) result_ = param_decl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // EQ ELLIPSIS
  private static boolean param_decl_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_decl_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, EQ, ELLIPSIS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [eq_expr_pin]
  private static boolean param_decl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_decl_1_1")) return false;
    eq_expr_pin(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // param_path_element+
  public static boolean param_path(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path")) return false;
    if (!nextTokenIs(builder_, "<param path>", DOT, LBT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAM_PATH, "<param path>");
    result_ = param_path_element(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!param_path_element(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "param_path", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LBT expr (DOTDOT expr)? RBT | DOT primary_group | DOT CT_TYPE_IDENT
  public static boolean param_path_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path_element")) return false;
    if (!nextTokenIs(builder_, "<param path element>", DOT, LBT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAM_PATH_ELEMENT, "<param path element>");
    result_ = param_path_element_0(builder_, level_ + 1);
    if (!result_) result_ = param_path_element_1(builder_, level_ + 1);
    if (!result_) result_ = parseTokens(builder_, 1, DOT, CT_TYPE_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LBT expr (DOTDOT expr)? RBT
  private static boolean param_path_element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path_element_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, LBT);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1, -1));
    result_ = pinned_ && report_error_(builder_, param_path_element_0_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, RBT) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (DOTDOT expr)?
  private static boolean param_path_element_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path_element_0_2")) return false;
    param_path_element_0_2_0(builder_, level_ + 1);
    return true;
  }

  // DOTDOT expr
  private static boolean param_path_element_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path_element_0_2_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, DOTDOT);
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // DOT primary_group
  private static boolean param_path_element_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_path_element_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, DOT);
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(builder_, level_ + 1, 13);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // KW_INLINE? type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  //     | ELLIPSIS | HASH_IDENT attributes?| AMP IDENT attributes?  | IDENT ELLIPSIS? attributes?
  //     | CT_IDENT | CT_IDENT ELLIPSIS
  public static boolean parameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAMETER, "<parameter>");
    result_ = parameter_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, ELLIPSIS);
    if (!result_) result_ = parameter_2(builder_, level_ + 1);
    if (!result_) result_ = parameter_3(builder_, level_ + 1);
    if (!result_) result_ = parameter_4(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, CT_IDENT);
    if (!result_) result_ = parseTokens(builder_, 0, CT_IDENT, ELLIPSIS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_INLINE? type (ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?)
  private static boolean parameter_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter_0_0(builder_, level_ + 1);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && parameter_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_INLINE?
  private static boolean parameter_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_0")) return false;
    consumeToken(builder_, KW_INLINE);
    return true;
  }

  // ELLIPSIS? IDENT attributes? | ELLIPSIS? CT_IDENT | (HASH_IDENT | AMP IDENT) attributes? | attributes?
  private static boolean parameter_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter_0_2_0(builder_, level_ + 1);
    if (!result_) result_ = parameter_0_2_1(builder_, level_ + 1);
    if (!result_) result_ = parameter_0_2_2(builder_, level_ + 1);
    if (!result_) result_ = parameter_0_2_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS? IDENT attributes?
  private static boolean parameter_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter_0_2_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IDENT);
    result_ = result_ && parameter_0_2_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS?
  private static boolean parameter_0_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_0_0")) return false;
    consumeToken(builder_, ELLIPSIS);
    return true;
  }

  // attributes?
  private static boolean parameter_0_2_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_0_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // ELLIPSIS? CT_IDENT
  private static boolean parameter_0_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter_0_2_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CT_IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS?
  private static boolean parameter_0_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_1_0")) return false;
    consumeToken(builder_, ELLIPSIS);
    return true;
  }

  // (HASH_IDENT | AMP IDENT) attributes?
  private static boolean parameter_0_2_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter_0_2_2_0(builder_, level_ + 1);
    result_ = result_ && parameter_0_2_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // HASH_IDENT | AMP IDENT
  private static boolean parameter_0_2_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, HASH_IDENT);
    if (!result_) result_ = parseTokens(builder_, 0, AMP, IDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean parameter_0_2_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_2_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean parameter_0_2_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_0_2_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // HASH_IDENT attributes?
  private static boolean parameter_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, HASH_IDENT);
    result_ = result_ && parameter_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean parameter_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // AMP IDENT attributes?
  private static boolean parameter_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, AMP, IDENT);
    result_ = result_ && parameter_3_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean parameter_3_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_3_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // IDENT ELLIPSIS? attributes?
  private static boolean parameter_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENT);
    result_ = result_ && parameter_4_1(builder_, level_ + 1);
    result_ = result_ && parameter_4_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ELLIPSIS?
  private static boolean parameter_4_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_4_1")) return false;
    consumeToken(builder_, ELLIPSIS);
    return true;
  }

  // attributes?
  private static boolean parameter_4_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_4_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // param_decl (COMMA param_decl)*
  public static boolean parameter_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAMETER_LIST, "<parameter list>");
    result_ = param_decl(builder_, level_ + 1);
    result_ = result_ && parameter_list_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (COMMA param_decl)*
  private static boolean parameter_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!parameter_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "parameter_list_1", pos_)) break;
    }
    return true;
  }

  // COMMA param_decl
  private static boolean parameter_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    result_ = result_ && param_decl(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LP cond RP
  public static boolean paren_cond(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_cond")) return false;
    if (!nextTokenIs(builder_, LP)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PAREN_COND, null);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && cond(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // (IDENT SCOPE)+
  public static boolean path(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path")) return false;
    if (!nextTokenIs(builder_, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = path_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!path_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "path", pos_)) break;
    }
    exit_section_(builder_, marker_, PATH, result_);
    return result_;
  }

  // IDENT SCOPE
  private static boolean path_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 2, IDENT, SCOPE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // path? AT_IDENT
  public static boolean path_at_ident(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_at_ident")) return false;
    if (!nextTokenIs(builder_, "<path at ident>", AT_IDENT, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_AT_IDENT, "<path at ident>");
    result_ = path_at_ident_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, AT_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path?
  private static boolean path_at_ident_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_at_ident_0")) return false;
    path(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // path? CONST_IDENT
  public static boolean path_const(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_const")) return false;
    if (!nextTokenIs(builder_, "<path const>", CONST_IDENT, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_CONST, "<path const>");
    result_ = path_const_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CONST_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path?
  private static boolean path_const_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_const_0")) return false;
    path(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // <<parsePathIdent>>
  public static boolean path_ident(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_ident")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_IDENT, "<path ident>");
    result_ = parsePathIdent(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // range_loc? (DOTDOT | COLON) range_loc?
  public static boolean range_exp(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_exp")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RANGE_EXP, "<range exp>");
    result_ = range_exp_0(builder_, level_ + 1);
    result_ = result_ && range_exp_1(builder_, level_ + 1);
    result_ = result_ && range_exp_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // range_loc?
  private static boolean range_exp_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_exp_0")) return false;
    range_loc(builder_, level_ + 1);
    return true;
  }

  // DOTDOT | COLON
  private static boolean range_exp_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_exp_1")) return false;
    boolean result_;
    result_ = consumeToken(builder_, DOTDOT);
    if (!result_) result_ = consumeToken(builder_, COLON);
    return result_;
  }

  // range_loc?
  private static boolean range_exp_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_exp_2")) return false;
    range_loc(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // BIT_XOR? expr
  public static boolean range_loc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_loc")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RANGE_LOC, "<range loc>");
    result_ = range_loc_0(builder_, level_ + 1);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // BIT_XOR?
  private static boolean range_loc_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "range_loc_0")) return false;
    consumeToken(builder_, BIT_XOR);
    return true;
  }

  /* ********************************************************** */
  // !(statement|keyword_list|ident_list|RB|EOS)
  static boolean recover_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recover_statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !recover_statement_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // statement|keyword_list|ident_list|RB|EOS
  private static boolean recover_statement_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recover_statement_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = statement(builder_, level_ + 1);
    if (!result_) result_ = keyword_list(builder_, level_ + 1);
    if (!result_) result_ = ident_list(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, RB);
    if (!result_) result_ = consumeToken(builder_, EOS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LT_OP !LBT | GT_OP | LE_OP | GE_OP | EQ_OP | NE_OP
  public static boolean rel_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rel_bin_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator>");
    result_ = rel_bin_op_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, GT_OP);
    if (!result_) result_ = consumeToken(builder_, LE_OP);
    if (!result_) result_ = consumeToken(builder_, GE_OP);
    if (!result_) result_ = consumeToken(builder_, EQ_OP);
    if (!result_) result_ = consumeToken(builder_, NE_OP);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LT_OP !LBT
  private static boolean rel_bin_op_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rel_bin_op_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LT_OP);
    result_ = result_ && rel_bin_op_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !LBT
  private static boolean rel_bin_op_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rel_bin_op_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, LBT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_RETURN expr? EOS
  public static boolean return_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "return_stmt")) return false;
    if (!nextTokenIs(builder_, KW_RETURN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RETURN_STMT, null);
    result_ = consumeToken(builder_, KW_RETURN);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, return_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // expr?
  private static boolean return_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "return_stmt_1")) return false;
    expr(builder_, level_ + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // SHL | SHR
  public static boolean shift_bin_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "shift_bin_op")) return false;
    if (!nextTokenIs(builder_, "<operator >", SHL, SHR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY_OP, "<operator >");
    result_ = consumeToken(builder_, SHL);
    if (!result_) result_ = consumeToken(builder_, SHR);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
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
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = compound_statement(builder_, level_ + 1);
    if (!result_) result_ = var_stmt(builder_, level_ + 1);
    if (!result_) result_ = const_declaration_stmt(builder_, level_ + 1);
    if (!result_) result_ = expr_stmt(builder_, level_ + 1);
    if (!result_) result_ = local_declaration_stmt(builder_, level_ + 1);
    if (!result_) result_ = return_stmt(builder_, level_ + 1);
    if (!result_) result_ = if_stmt(builder_, level_ + 1);
    if (!result_) result_ = while_stmt(builder_, level_ + 1);
    if (!result_) result_ = defer_stmt(builder_, level_ + 1);
    if (!result_) result_ = switch_stmt(builder_, level_ + 1);
    if (!result_) result_ = do_stmt(builder_, level_ + 1);
    if (!result_) result_ = for_stmt(builder_, level_ + 1);
    if (!result_) result_ = foreach_stmt(builder_, level_ + 1);
    if (!result_) result_ = continue_stmt(builder_, level_ + 1);
    if (!result_) result_ = break_stmt(builder_, level_ + 1);
    if (!result_) result_ = nextcase_stmt(builder_, level_ + 1);
    if (!result_) result_ = asm_block_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_echo_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_assert_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_error_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_if_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_switch_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_foreach_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_for_stmt(builder_, level_ + 1);
    if (!result_) result_ = assert_stmt(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, EOS);
    exit_section_(builder_, level_, marker_, result_, false, C3Parser::recover_statement);
    return result_;
  }

  /* ********************************************************** */
  // statement+
  public static boolean statement_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STATEMENT_LIST, "<statement list>");
    result_ = statement(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "statement_list", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LB struct_member_declaration* RB
  public static boolean struct_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_body")) return false;
    if (!nextTokenIs(builder_, LB)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_BODY, null);
    result_ = consumeToken(builder_, LB);
    result_ = result_ && struct_body_1(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, RB);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // struct_member_declaration*
  private static boolean struct_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!struct_member_declaration(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "struct_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (KW_STRUCT | KW_UNION) type_name interface_impl? generic_decl? attributes? struct_body
  public static boolean struct_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_declaration")) return false;
    if (!nextTokenIs(builder_, "<struct declaration>", KW_STRUCT, KW_UNION)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_DECLARATION, "<struct declaration>");
    result_ = struct_declaration_0(builder_, level_ + 1);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, struct_declaration_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, struct_declaration_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, struct_declaration_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && struct_body(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_STRUCT | KW_UNION
  private static boolean struct_declaration_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_declaration_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, KW_STRUCT);
    if (!result_) result_ = consumeToken(builder_, KW_UNION);
    return result_;
  }

  // interface_impl?
  private static boolean struct_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_declaration_2")) return false;
    interface_impl(builder_, level_ + 1);
    return true;
  }

  // generic_decl?
  private static boolean struct_declaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_declaration_3")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean struct_declaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_declaration_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // struct_member_declaration_1
  //                               | struct_member_declaration_2
  //                               | struct_member_declaration_3
  //                               | struct_member_declaration_4
  public static boolean struct_member_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_MEMBER_DECLARATION, "<struct member declaration>");
    result_ = struct_member_declaration_1(builder_, level_ + 1);
    if (!result_) result_ = struct_member_declaration_2(builder_, level_ + 1);
    if (!result_) result_ = struct_member_declaration_3(builder_, level_ + 1);
    if (!result_) result_ = struct_member_declaration_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // type identifier_list attributes? EOS
  static boolean struct_member_declaration_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = type(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, identifier_list(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, struct_member_declaration_1_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attributes?
  private static boolean struct_member_declaration_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_1_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_INLINE type IDENT? attributes? EOS
  static boolean struct_member_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_2")) return false;
    if (!nextTokenIs(builder_, KW_INLINE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, KW_INLINE);
    result_ = result_ && type(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, struct_member_declaration_2_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, struct_member_declaration_2_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // IDENT?
  private static boolean struct_member_declaration_2_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_2_2")) return false;
    consumeToken(builder_, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_2_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_2_3")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (KW_STRUCT | KW_UNION) IDENT? attributes? struct_body
  static boolean struct_member_declaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_3")) return false;
    if (!nextTokenIs(builder_, "", KW_STRUCT, KW_UNION)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = struct_member_declaration_3_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, struct_member_declaration_3_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, struct_member_declaration_3_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && struct_body(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_STRUCT | KW_UNION
  private static boolean struct_member_declaration_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_3_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, KW_STRUCT);
    if (!result_) result_ = consumeToken(builder_, KW_UNION);
    return result_;
  }

  // IDENT?
  private static boolean struct_member_declaration_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_3_1")) return false;
    consumeToken(builder_, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_3_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_3_2")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_BITSTRUCT IDENT? ':' type attributes? bitstruct_body
  static boolean struct_member_declaration_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_4")) return false;
    if (!nextTokenIs(builder_, KW_BITSTRUCT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, KW_BITSTRUCT);
    result_ = result_ && struct_member_declaration_4_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ":");
    result_ = result_ && type(builder_, level_ + 1);
    pinned_ = result_; // pin = 4
    result_ = result_ && report_error_(builder_, struct_member_declaration_4_4(builder_, level_ + 1));
    result_ = pinned_ && bitstruct_body(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // IDENT?
  private static boolean struct_member_declaration_4_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_4_1")) return false;
    consumeToken(builder_, IDENT);
    return true;
  }

  // attributes?
  private static boolean struct_member_declaration_4_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member_declaration_4_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // path? TYPE_IDENT generic_parameters?
  static boolean struct_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_type")) return false;
    if (!nextTokenIs(builder_, "", IDENT, TYPE_IDENT)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = struct_type_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TYPE_IDENT);
    pinned_ = result_; // pin = 2
    result_ = result_ && struct_type_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // path?
  private static boolean struct_type_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_type_0")) return false;
    path(builder_, level_ + 1);
    return true;
  }

  // generic_parameters?
  private static boolean struct_type_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_type_2")) return false;
    generic_parameters(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (case_stmt | default_stmt)+
  public static boolean switch_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_body")) return false;
    if (!nextTokenIs(builder_, "<switch body>", KW_CASE, KW_DEFAULT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SWITCH_BODY, "<switch body>");
    result_ = switch_body_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!switch_body_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "switch_body", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // case_stmt | default_stmt
  private static boolean switch_body_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_body_0")) return false;
    boolean result_;
    result_ = case_stmt(builder_, level_ + 1);
    if (!result_) result_ = default_stmt(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // KW_SWITCH label? paren_cond? AT_IDENT? LB switch_body? RB
  public static boolean switch_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_stmt")) return false;
    if (!nextTokenIs(builder_, KW_SWITCH)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SWITCH_STMT, null);
    result_ = consumeToken(builder_, KW_SWITCH);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, switch_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, switch_stmt_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, switch_stmt_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, LB)) && result_;
    result_ = pinned_ && report_error_(builder_, switch_stmt_5(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, RB) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // label?
  private static boolean switch_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  // paren_cond?
  private static boolean switch_stmt_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_stmt_2")) return false;
    paren_cond(builder_, level_ + 1);
    return true;
  }

  // AT_IDENT?
  private static boolean switch_stmt_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_stmt_3")) return false;
    consumeToken(builder_, AT_IDENT);
    return true;
  }

  // switch_body?
  private static boolean switch_stmt_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_stmt_5")) return false;
    switch_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // import_decl
  //     | KW_EXTERN? (func_definition | const_declaration_stmt | global_decl)
  //     | ct_assert_stmt
  //     | ct_error_stmt
  //     | ct_echo_stmt
  //     | ct_include_stmt
  //     | KW_CT_EXPAND grouped_expr
  //     | type_decl
  //     | alias_decl
  //     | faultdef_decl
  //     | typedef_decl
  //     | attrdef_decl
  //     | alias_type_decl
  //     | macro_definition
  //     | asm_declaration
  //     | interface_definition
  public static boolean top_level(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "top_level")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TOP_LEVEL, "<top level>");
    result_ = import_decl(builder_, level_ + 1);
    if (!result_) result_ = top_level_1(builder_, level_ + 1);
    if (!result_) result_ = ct_assert_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_error_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_echo_stmt(builder_, level_ + 1);
    if (!result_) result_ = ct_include_stmt(builder_, level_ + 1);
    if (!result_) result_ = top_level_6(builder_, level_ + 1);
    if (!result_) result_ = type_decl(builder_, level_ + 1);
    if (!result_) result_ = alias_decl(builder_, level_ + 1);
    if (!result_) result_ = faultdef_decl(builder_, level_ + 1);
    if (!result_) result_ = typedef_decl(builder_, level_ + 1);
    if (!result_) result_ = attrdef_decl(builder_, level_ + 1);
    if (!result_) result_ = alias_type_decl(builder_, level_ + 1);
    if (!result_) result_ = macro_definition(builder_, level_ + 1);
    if (!result_) result_ = asm_declaration(builder_, level_ + 1);
    if (!result_) result_ = interface_definition(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_EXTERN? (func_definition | const_declaration_stmt | global_decl)
  private static boolean top_level_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "top_level_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = top_level_1_0(builder_, level_ + 1);
    result_ = result_ && top_level_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // KW_EXTERN?
  private static boolean top_level_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "top_level_1_0")) return false;
    consumeToken(builder_, KW_EXTERN);
    return true;
  }

  // func_definition | const_declaration_stmt | global_decl
  private static boolean top_level_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "top_level_1_1")) return false;
    boolean result_;
    result_ = func_definition(builder_, level_ + 1);
    if (!result_) result_ = const_declaration_stmt(builder_, level_ + 1);
    if (!result_) result_ = global_decl(builder_, level_ + 1);
    return result_;
  }

  // KW_CT_EXPAND grouped_expr
  private static boolean top_level_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "top_level_6")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_CT_EXPAND);
    result_ = result_ && grouped_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // AT_IDENT (LP parameter_list? RP)?
  public static boolean trailing_block_param(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "trailing_block_param")) return false;
    if (!nextTokenIs(builder_, AT_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AT_IDENT);
    result_ = result_ && trailing_block_param_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, TRAILING_BLOCK_PARAM, result_);
    return result_;
  }

  // (LP parameter_list? RP)?
  private static boolean trailing_block_param_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "trailing_block_param_1")) return false;
    trailing_block_param_1_0(builder_, level_ + 1);
    return true;
  }

  // LP parameter_list? RP
  private static boolean trailing_block_param_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "trailing_block_param_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && trailing_block_param_1_0_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // parameter_list?
  private static boolean trailing_block_param_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "trailing_block_param_1_0_1")) return false;
    parameter_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // default_module_section? module_section*
  static boolean translation_unit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "translation_unit")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = translation_unit_0(builder_, level_ + 1);
    result_ = result_ && translation_unit_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // default_module_section?
  private static boolean translation_unit_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "translation_unit_0")) return false;
    default_module_section(builder_, level_ + 1);
    return true;
  }

  // module_section*
  private static boolean translation_unit_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "translation_unit_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!module_section(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "translation_unit_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // KW_TRY (type? IDENT EQ)? <<expr 4>>
  public static boolean try_unwrap(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap")) return false;
    if (!nextTokenIs(builder_, KW_TRY)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_TRY);
    result_ = result_ && try_unwrap_1(builder_, level_ + 1);
    result_ = result_ && expr(builder_, level_ + 1, 4);
    exit_section_(builder_, marker_, TRY_UNWRAP, result_);
    return result_;
  }

  // (type? IDENT EQ)?
  private static boolean try_unwrap_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_1")) return false;
    try_unwrap_1_0(builder_, level_ + 1);
    return true;
  }

  // type? IDENT EQ
  private static boolean try_unwrap_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = try_unwrap_1_0_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENT, EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // type?
  private static boolean try_unwrap_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_1_0_0")) return false;
    type(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // try_unwrap (AND (try_unwrap | expr))*
  public static boolean try_unwrap_chain(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_chain")) return false;
    if (!nextTokenIs(builder_, KW_TRY)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = try_unwrap(builder_, level_ + 1);
    result_ = result_ && try_unwrap_chain_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, TRY_UNWRAP_CHAIN, result_);
    return result_;
  }

  // (AND (try_unwrap | expr))*
  private static boolean try_unwrap_chain_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_chain_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!try_unwrap_chain_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "try_unwrap_chain_1", pos_)) break;
    }
    return true;
  }

  // AND (try_unwrap | expr)
  private static boolean try_unwrap_chain_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_chain_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AND);
    result_ = result_ && try_unwrap_chain_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // try_unwrap | expr
  private static boolean try_unwrap_chain_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "try_unwrap_chain_1_0_1")) return false;
    boolean result_;
    result_ = try_unwrap(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    return result_;
  }

  /* ********************************************************** */
  // base_type type_suffix*
  public static boolean type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPE, "<type>");
    result_ = base_type(builder_, level_ + 1);
    result_ = result_ && type_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // type_suffix*
  private static boolean type_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!type_suffix(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "type_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // struct_declaration
  //     | enum_declaration
  //     | constdef_declaration
  //     | bitstruct_declaration
  public static boolean type_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPE_DECL, "<type decl>");
    result_ = struct_declaration(builder_, level_ + 1);
    if (!result_) result_ = enum_declaration(builder_, level_ + 1);
    if (!result_) result_ = constdef_declaration(builder_, level_ + 1);
    if (!result_) result_ = bitstruct_declaration(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // TYPE_IDENT
  public static boolean type_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_name")) return false;
    if (!nextTokenIs(builder_, TYPE_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TYPE_IDENT);
    exit_section_(builder_, marker_, TYPE_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // STAR | LBT PLUS RBT | LBT (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)? RBT | LVEC (STAR | constant_expr) RVEC
  public static boolean type_suffix(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPE_SUFFIX, "<type suffix>");
    result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = parseTokens(builder_, 0, LBT, PLUS, RBT);
    if (!result_) result_ = type_suffix_2(builder_, level_ + 1);
    if (!result_) result_ = type_suffix_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LBT (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)? RBT
  private static boolean type_suffix_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LBT);
    result_ = result_ && type_suffix_2_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RBT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (STAR | QUESTION | DIV | UNDERSCORE | constant_expr)?
  private static boolean type_suffix_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix_2_1")) return false;
    type_suffix_2_1_0(builder_, level_ + 1);
    return true;
  }

  // STAR | QUESTION | DIV | UNDERSCORE | constant_expr
  private static boolean type_suffix_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix_2_1_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = consumeToken(builder_, QUESTION);
    if (!result_) result_ = consumeToken(builder_, DIV);
    if (!result_) result_ = consumeToken(builder_, UNDERSCORE);
    if (!result_) result_ = constant_expr(builder_, level_ + 1);
    return result_;
  }

  // LVEC (STAR | constant_expr) RVEC
  private static boolean type_suffix_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LVEC);
    result_ = result_ && type_suffix_3_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RVEC);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // STAR | constant_expr
  private static boolean type_suffix_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_suffix_3_1")) return false;
    boolean result_;
    result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = constant_expr(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // KW_TYPEDEF type_name interface_impl? generic_decl? attributes? EQ KW_INLINE? typedef_type attributes? EOS
  public static boolean typedef_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl")) return false;
    if (!nextTokenIs(builder_, KW_TYPEDEF)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPEDEF_DECL, null);
    result_ = consumeToken(builder_, KW_TYPEDEF);
    result_ = result_ && type_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, typedef_decl_2(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, typedef_decl_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, typedef_decl_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, EQ)) && result_;
    result_ = pinned_ && report_error_(builder_, typedef_decl_6(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, typedef_type(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, typedef_decl_8(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, EOS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // interface_impl?
  private static boolean typedef_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl_2")) return false;
    interface_impl(builder_, level_ + 1);
    return true;
  }

  // generic_decl?
  private static boolean typedef_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl_3")) return false;
    generic_decl(builder_, level_ + 1);
    return true;
  }

  // attributes?
  private static boolean typedef_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl_4")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // KW_INLINE?
  private static boolean typedef_decl_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl_6")) return false;
    consumeToken(builder_, KW_INLINE);
    return true;
  }

  // attributes?
  private static boolean typedef_decl_8(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_decl_8")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // KW_FN optional_type fn_parameter_list | expr | type generic_parameters?
  public static boolean typedef_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPEDEF_TYPE, "<typedef type>");
    result_ = typedef_type_0(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    if (!result_) result_ = typedef_type_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_FN optional_type fn_parameter_list
  private static boolean typedef_type_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_type_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_FN);
    result_ = result_ && optional_type(builder_, level_ + 1);
    result_ = result_ && fn_parameter_list(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // type generic_parameters?
  private static boolean typedef_type_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_type_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = type(builder_, level_ + 1);
    result_ = result_ && typedef_type_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // generic_parameters?
  private static boolean typedef_type_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typedef_type_1_1")) return false;
    generic_parameters(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // AMP | AND | BIT_XOR | STAR | PLUS | MINUS | BIT_NOT | BANG | BANGBANG | PLUSPLUS | MINUSMINUS | LP type RP !LB
  public static boolean unary_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, UNARY_OP, "<operator>");
    result_ = consumeToken(builder_, AMP);
    if (!result_) result_ = consumeToken(builder_, AND);
    if (!result_) result_ = consumeToken(builder_, BIT_XOR);
    if (!result_) result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = consumeToken(builder_, PLUS);
    if (!result_) result_ = consumeToken(builder_, MINUS);
    if (!result_) result_ = consumeToken(builder_, BIT_NOT);
    if (!result_) result_ = consumeToken(builder_, BANG);
    if (!result_) result_ = consumeToken(builder_, BANGBANG);
    if (!result_) result_ = consumeToken(builder_, PLUSPLUS);
    if (!result_) result_ = consumeToken(builder_, MINUSMINUS);
    if (!result_) result_ = unary_op_11(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LP type RP !LB
  private static boolean unary_op_11(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_op_11")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LP);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    result_ = result_ && unary_op_11_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !LB
  private static boolean unary_op_11_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_op_11_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, LB);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // KW_VAR (IDENT attributes? eq_expr_pin | CT_TYPE_IDENT (EQ expr)? | CT_IDENT eq_expr_pin?)
  public static boolean var_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl")) return false;
    if (!nextTokenIs(builder_, KW_VAR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, KW_VAR);
    result_ = result_ && var_decl_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, VAR_DECL, result_);
    return result_;
  }

  // IDENT attributes? eq_expr_pin | CT_TYPE_IDENT (EQ expr)? | CT_IDENT eq_expr_pin?
  private static boolean var_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = var_decl_1_0(builder_, level_ + 1);
    if (!result_) result_ = var_decl_1_1(builder_, level_ + 1);
    if (!result_) result_ = var_decl_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IDENT attributes? eq_expr_pin
  private static boolean var_decl_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENT);
    result_ = result_ && var_decl_1_0_1(builder_, level_ + 1);
    result_ = result_ && eq_expr_pin(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attributes?
  private static boolean var_decl_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_0_1")) return false;
    attributes(builder_, level_ + 1);
    return true;
  }

  // CT_TYPE_IDENT (EQ expr)?
  private static boolean var_decl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CT_TYPE_IDENT);
    result_ = result_ && var_decl_1_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (EQ expr)?
  private static boolean var_decl_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_1_1")) return false;
    var_decl_1_1_1_0(builder_, level_ + 1);
    return true;
  }

  // EQ expr
  private static boolean var_decl_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_1_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EQ);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // CT_IDENT eq_expr_pin?
  private static boolean var_decl_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CT_IDENT);
    result_ = result_ && var_decl_1_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // eq_expr_pin?
  private static boolean var_decl_1_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_decl_1_2_1")) return false;
    eq_expr_pin(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // var_decl EOS
  public static boolean var_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "var_stmt")) return false;
    if (!nextTokenIs(builder_, KW_VAR)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, VAR_STMT, null);
    result_ = var_decl(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, EOS);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // KW_WHILE label? paren_cond statement
  public static boolean while_stmt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "while_stmt")) return false;
    if (!nextTokenIs(builder_, KW_WHILE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, WHILE_STMT, null);
    result_ = consumeToken(builder_, KW_WHILE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, while_stmt_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, paren_cond(builder_, level_ + 1)) && result_;
    result_ = pinned_ && statement(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // label?
  private static boolean while_stmt_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "while_stmt_1")) return false;
    label(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(ternary_expr)
  // 1: ATOM(assign_type_expr)
  // 2: BINARY(assign_bin_expr)
  // 3: BINARY(or_bin_expr)
  // 4: BINARY(and_bin_expr)
  // 5: BINARY(rel_bin_expr)
  // 6: BINARY(add_bin_expr)
  // 7: BINARY(elvis_bin_expr)
  // 8: BINARY(optelse_bin_expr)
  // 9: BINARY(bit_bin_expr)
  // 10: BINARY(shift_bin_expr)
  // 11: BINARY(mult_bin_expr)
  // 12: PREFIX(unary_expr)
  // 13: POSTFIX(call_expr)
  // 14: ATOM(literal_expr) ATOM(path_ident_expr) ATOM(string_expr) ATOM(bytes_expr)
  //    ATOM(keyword_expr) ATOM(builtin_const_expr) ATOM(builtin_expr) ATOM(path_const_expr)
  //    ATOM(path_at_ident_expr) ATOM(compound_init_expr) PREFIX(grouped_expr) ATOM(local_ident_expr)
  //    ATOM(type_access_expr) ATOM(enum_access_expr) ATOM(type_expr) ATOM(ct_feature_expr)
  //    ATOM(ct_arg_expr) PREFIX(ct_analyze_expr) ATOM(ct_defined_expr) ATOM(lambda_decl_expr)
  //    PREFIX(lambda_decl_short_expr) ATOM(init_list_expr)
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    addVariant(builder_, "<expr>");
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expr>");
    result_ = assign_type_expr(builder_, level_ + 1);
    if (!result_) result_ = unary_expr(builder_, level_ + 1);
    if (!result_) result_ = literal_expr(builder_, level_ + 1);
    if (!result_) result_ = path_ident_expr(builder_, level_ + 1);
    if (!result_) result_ = string_expr(builder_, level_ + 1);
    if (!result_) result_ = bytes_expr(builder_, level_ + 1);
    if (!result_) result_ = keyword_expr(builder_, level_ + 1);
    if (!result_) result_ = builtin_const_expr(builder_, level_ + 1);
    if (!result_) result_ = builtin_expr(builder_, level_ + 1);
    if (!result_) result_ = path_const_expr(builder_, level_ + 1);
    if (!result_) result_ = path_at_ident_expr(builder_, level_ + 1);
    if (!result_) result_ = compound_init_expr(builder_, level_ + 1);
    if (!result_) result_ = grouped_expr(builder_, level_ + 1);
    if (!result_) result_ = local_ident_expr(builder_, level_ + 1);
    if (!result_) result_ = type_access_expr(builder_, level_ + 1);
    if (!result_) result_ = enum_access_expr(builder_, level_ + 1);
    if (!result_) result_ = type_expr(builder_, level_ + 1);
    if (!result_) result_ = ct_feature_expr(builder_, level_ + 1);
    if (!result_) result_ = ct_arg_expr(builder_, level_ + 1);
    if (!result_) result_ = ct_analyze_expr(builder_, level_ + 1);
    if (!result_) result_ = ct_defined_expr(builder_, level_ + 1);
    if (!result_) result_ = lambda_decl_expr(builder_, level_ + 1);
    if (!result_) result_ = lambda_decl_short_expr(builder_, level_ + 1);
    if (!result_) result_ = init_list_expr(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expr_0(builder_, level_ + 1, priority_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker marker_ = enter_section_(builder_, level_, _LEFT_, null);
      if (priority_ < 0 && ternary_expr_0(builder_, level_ + 1)) {
        result_ = report_error_(builder_, expr(builder_, level_, -1));
        result_ = ternary_expr_1(builder_, level_ + 1) && result_;
        exit_section_(builder_, level_, marker_, TERNARY_EXPR, result_, true, null);
      }
      else if (priority_ < 2 && assign_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 1);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 3 && or_bin_expr_0(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 3);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 4 && and_bin_expr_0(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 4);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 5 && rel_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 5);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 6 && add_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 6);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 7 && consumeTokenSmart(builder_, ELVIS)) {
        result_ = expr(builder_, level_, 6);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 8 && consumeTokenSmart(builder_, OPTELSE)) {
        result_ = expr(builder_, level_, 7);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 9 && bit_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 9);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 10 && shift_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 10);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 11 && mult_bin_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 11);
        exit_section_(builder_, level_, marker_, BINARY_EXPR, result_, true, null);
      }
      else if (priority_ < 13 && call_expr_tail(builder_, level_ + 1)) {
        result_ = true;
        exit_section_(builder_, level_, marker_, CALL_EXPR, result_, true, null);
      }
      else {
        exit_section_(builder_, level_, marker_, null, false, false, null);
        break;
      }
    }
    return result_;
  }

  // QUESTION | CT_TERNARY
  private static boolean ternary_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ternary_expr_0")) return false;
    boolean result_;
    result_ = consumeTokenSmart(builder_, QUESTION);
    if (!result_) result_ = consumeTokenSmart(builder_, CT_TERNARY);
    return result_;
  }

  // COLON expr
  private static boolean ternary_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ternary_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // CT_TYPE_IDENT /*EQ type*/eq_expr_pin
  public static boolean assign_type_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assign_type_expr")) return false;
    if (!nextTokenIsSmart(builder_, CT_TYPE_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, CT_TYPE_IDENT);
    result_ = result_ && eq_expr_pin(builder_, level_ + 1);
    exit_section_(builder_, marker_, ASSIGN_TYPE_EXPR, result_);
    return result_;
  }

  // OR | CT_OR
  private static boolean or_bin_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "or_bin_expr_0")) return false;
    boolean result_;
    result_ = consumeTokenSmart(builder_, OR);
    if (!result_) result_ = consumeTokenSmart(builder_, CT_OR);
    return result_;
  }

  // AND | CT_AND
  private static boolean and_bin_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "and_bin_expr_0")) return false;
    boolean result_;
    result_ = consumeTokenSmart(builder_, AND);
    if (!result_) result_ = consumeTokenSmart(builder_, CT_AND);
    return result_;
  }

  public static boolean unary_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = unary_op(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 12);
    exit_section_(builder_, level_, marker_, UNARY_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // INT_LITERAL | FLOAT_LITERAL
  public static boolean literal_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expr")) return false;
    if (!nextTokenIsSmart(builder_, FLOAT_LITERAL, INT_LITERAL)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LITERAL_EXPR, "<literal expr>");
    result_ = consumeTokenSmart(builder_, INT_LITERAL);
    if (!result_) result_ = consumeTokenSmart(builder_, FLOAT_LITERAL);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path_ident
  public static boolean path_ident_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_ident_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_IDENT_EXPR, "<path ident expr>");
    result_ = path_ident(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // STRING_LIT+ | CHAR_LIT
  public static boolean string_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_expr")) return false;
    if (!nextTokenIsSmart(builder_, CHAR_LIT, STRING_LIT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRING_EXPR, "<string expr>");
    result_ = string_expr_0(builder_, level_ + 1);
    if (!result_) result_ = consumeTokenSmart(builder_, CHAR_LIT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // STRING_LIT+
  private static boolean string_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, STRING_LIT);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!consumeTokenSmart(builder_, STRING_LIT)) break;
      if (!empty_element_parsed_guard_(builder_, "string_expr_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // BYTES+
  public static boolean bytes_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bytes_expr")) return false;
    if (!nextTokenIsSmart(builder_, BYTES)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, BYTES);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!consumeTokenSmart(builder_, BYTES)) break;
      if (!empty_element_parsed_guard_(builder_, "bytes_expr", pos_)) break;
    }
    exit_section_(builder_, marker_, BYTES_EXPR, result_);
    return result_;
  }

  // KW_NULL | KW_TRUE | KW_FALSE
  public static boolean keyword_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "keyword_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, KEYWORD_EXPR, "<keyword expr>");
    result_ = consumeTokenSmart(builder_, KW_NULL);
    if (!result_) result_ = consumeTokenSmart(builder_, KW_TRUE);
    if (!result_) result_ = consumeTokenSmart(builder_, KW_FALSE);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // BUILTIN_CONST
  public static boolean builtin_const_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "builtin_const_expr")) return false;
    if (!nextTokenIsSmart(builder_, BUILTIN_CONST)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, BUILTIN_CONST);
    exit_section_(builder_, marker_, BUILTIN_CONST_EXPR, result_);
    return result_;
  }

  // BUILTIN
  public static boolean builtin_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "builtin_expr")) return false;
    if (!nextTokenIsSmart(builder_, BUILTIN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, BUILTIN);
    exit_section_(builder_, marker_, BUILTIN_EXPR, result_);
    return result_;
  }

  // path_const
  public static boolean path_const_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_const_expr")) return false;
    if (!nextTokenIsSmart(builder_, CONST_IDENT, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_CONST_EXPR, "<path const expr>");
    result_ = path_const(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // path_at_ident
  public static boolean path_at_ident_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "path_at_ident_expr")) return false;
    if (!nextTokenIsSmart(builder_, AT_IDENT, IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PATH_AT_IDENT_EXPR, "<path at ident expr>");
    result_ = path_at_ident(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // LP type RP initializer_list
  public static boolean compound_init_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "compound_init_expr")) return false;
    if (!nextTokenIsSmart(builder_, LP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, LP);
    result_ = result_ && type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    result_ = result_ && initializer_list(builder_, level_ + 1);
    exit_section_(builder_, marker_, COMPOUND_INIT_EXPR, result_);
    return result_;
  }

  public static boolean grouped_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grouped_expr")) return false;
    if (!nextTokenIsSmart(builder_, LP)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeTokenSmart(builder_, LP);
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, -1);
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, RP)) && result_;
    exit_section_(builder_, level_, marker_, GROUPED_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // CT_IDENT | HASH_IDENT
  public static boolean local_ident_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "local_ident_expr")) return false;
    if (!nextTokenIsSmart(builder_, CT_IDENT, HASH_IDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LOCAL_IDENT_EXPR, "<local ident expr>");
    result_ = consumeTokenSmart(builder_, CT_IDENT);
    if (!result_) result_ = consumeTokenSmart(builder_, HASH_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // type SCOPE access_ident
  public static boolean type_access_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_access_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPE_ACCESS_EXPR, "<type access expr>");
    result_ = type(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SCOPE);
    result_ = result_ && access_ident(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // base_type DOT CONST_IDENT
  public static boolean enum_access_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_access_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ENUM_ACCESS_EXPR, "<enum access expr>");
    result_ = base_type(builder_, level_ + 1);
    result_ = result_ && consumeTokensSmart(builder_, 0, DOT, CONST_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // type !IDENT !CT_IDENT !KW_CT_EVAL !QUESTION
  public static boolean type_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPE_EXPR, "<type expr>");
    result_ = type(builder_, level_ + 1);
    result_ = result_ && type_expr_1(builder_, level_ + 1);
    result_ = result_ && type_expr_2(builder_, level_ + 1);
    result_ = result_ && type_expr_3(builder_, level_ + 1);
    result_ = result_ && type_expr_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // !IDENT
  private static boolean type_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeTokenSmart(builder_, IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // !CT_IDENT
  private static boolean type_expr_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_expr_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeTokenSmart(builder_, CT_IDENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // !KW_CT_EVAL
  private static boolean type_expr_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_expr_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeTokenSmart(builder_, KW_CT_EVAL);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // !QUESTION
  private static boolean type_expr_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type_expr_4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeTokenSmart(builder_, QUESTION);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // KW_CT_FEATURE LP CONST_IDENT RP
  public static boolean ct_feature_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_feature_expr")) return false;
    if (!nextTokenIsSmart(builder_, KW_CT_FEATURE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokensSmart(builder_, 0, KW_CT_FEATURE, LP, CONST_IDENT, RP);
    exit_section_(builder_, marker_, CT_FEATURE_EXPR, result_);
    return result_;
  }

  // KW_CT_VAARG (LBT (range_exp | range_loc) RBT)?
  public static boolean ct_arg_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_arg_expr")) return false;
    if (!nextTokenIsSmart(builder_, KW_CT_VAARG)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, KW_CT_VAARG);
    result_ = result_ && ct_arg_expr_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, CT_ARG_EXPR, result_);
    return result_;
  }

  // (LBT (range_exp | range_loc) RBT)?
  private static boolean ct_arg_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_arg_expr_1")) return false;
    ct_arg_expr_1_0(builder_, level_ + 1);
    return true;
  }

  // LBT (range_exp | range_loc) RBT
  private static boolean ct_arg_expr_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_arg_expr_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, LBT);
    result_ = result_ && ct_arg_expr_1_0_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RBT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // range_exp | range_loc
  private static boolean ct_arg_expr_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_arg_expr_1_0_1")) return false;
    boolean result_;
    result_ = range_exp(builder_, level_ + 1);
    if (!result_) result_ = range_loc(builder_, level_ + 1);
    return result_;
  }

  public static boolean ct_analyze_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_analyze_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = ct_analyze(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 13);
    exit_section_(builder_, level_, marker_, CT_ANALYZE_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // KW_CT_DEFINED LP ct_defined_check_expr_list RP
  public static boolean ct_defined_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ct_defined_expr")) return false;
    if (!nextTokenIsSmart(builder_, KW_CT_DEFINED)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokensSmart(builder_, 0, KW_CT_DEFINED, LP);
    result_ = result_ && ct_defined_check_expr_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RP);
    exit_section_(builder_, marker_, CT_DEFINED_EXPR, result_);
    return result_;
  }

  // lambda_decl compound_statement
  public static boolean lambda_decl_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl_expr")) return false;
    if (!nextTokenIsSmart(builder_, KW_FN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = lambda_decl(builder_, level_ + 1);
    result_ = result_ && compound_statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, LAMBDA_DECL_EXPR, result_);
    return result_;
  }

  public static boolean lambda_decl_short_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl_short_expr")) return false;
    if (!nextTokenIsSmart(builder_, KW_FN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = lambda_decl_short_expr_0(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, -1);
    exit_section_(builder_, level_, marker_, LAMBDA_DECL_SHORT_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // lambda_decl IMPLIES
  private static boolean lambda_decl_short_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lambda_decl_short_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = lambda_decl(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, IMPLIES);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // initializer_list
  public static boolean init_list_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "init_list_expr")) return false;
    if (!nextTokenIsSmart(builder_, LB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = initializer_list(builder_, level_ + 1);
    exit_section_(builder_, marker_, INIT_LIST_EXPR, result_);
    return result_;
  }

}
