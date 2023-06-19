// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.c3lang.intellij.psi.*;

public class C3StatementImpl extends ASTWrapperPsiElement implements C3Statement {

  public C3StatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AsmBlockStmt getAsmBlockStmt() {
    return PsiTreeUtil.getChildOfType(this, C3AsmBlockStmt.class);
  }

  @Override
  @Nullable
  public C3AssertStmt getAssertStmt() {
    return PsiTreeUtil.getChildOfType(this, C3AssertStmt.class);
  }

  @Override
  @Nullable
  public C3BreakStmt getBreakStmt() {
    return PsiTreeUtil.getChildOfType(this, C3BreakStmt.class);
  }

  @Override
  @Nullable
  public C3CompoundStatement getCompoundStatement() {
    return PsiTreeUtil.getChildOfType(this, C3CompoundStatement.class);
  }

  @Override
  @Nullable
  public C3ContinueStmt getContinueStmt() {
    return PsiTreeUtil.getChildOfType(this, C3ContinueStmt.class);
  }

  @Override
  @Nullable
  public C3CtAssertStmt getCtAssertStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtAssertStmt.class);
  }

  @Override
  @Nullable
  public C3CtEchoStmt getCtEchoStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtEchoStmt.class);
  }

  @Override
  @Nullable
  public C3CtForStmt getCtForStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtForStmt.class);
  }

  @Override
  @Nullable
  public C3CtForeachStmt getCtForeachStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtForeachStmt.class);
  }

  @Override
  @Nullable
  public C3CtIfStmt getCtIfStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtIfStmt.class);
  }

  @Override
  @Nullable
  public C3CtSwitchStmt getCtSwitchStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtSwitchStmt.class);
  }

  @Override
  @Nullable
  public C3DeclarationStmt getDeclarationStmt() {
    return PsiTreeUtil.getChildOfType(this, C3DeclarationStmt.class);
  }

  @Override
  @Nullable
  public C3DeferStmt getDeferStmt() {
    return PsiTreeUtil.getChildOfType(this, C3DeferStmt.class);
  }

  @Override
  @Nullable
  public C3DoStmt getDoStmt() {
    return PsiTreeUtil.getChildOfType(this, C3DoStmt.class);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return PsiTreeUtil.getChildOfType(this, C3Expr.class);
  }

  @Override
  @Nullable
  public C3ForStmt getForStmt() {
    return PsiTreeUtil.getChildOfType(this, C3ForStmt.class);
  }

  @Override
  @Nullable
  public C3ForeachStmt getForeachStmt() {
    return PsiTreeUtil.getChildOfType(this, C3ForeachStmt.class);
  }

  @Override
  @Nullable
  public C3IfStmt getIfStmt() {
    return PsiTreeUtil.getChildOfType(this, C3IfStmt.class);
  }

  @Override
  @Nullable
  public C3NextcaseStmt getNextcaseStmt() {
    return PsiTreeUtil.getChildOfType(this, C3NextcaseStmt.class);
  }

  @Override
  @Nullable
  public C3ReturnStmt getReturnStmt() {
    return PsiTreeUtil.getChildOfType(this, C3ReturnStmt.class);
  }

  @Override
  @Nullable
  public C3SwitchStmt getSwitchStmt() {
    return PsiTreeUtil.getChildOfType(this, C3SwitchStmt.class);
  }

  @Override
  @Nullable
  public C3VarStmt getVarStmt() {
    return PsiTreeUtil.getChildOfType(this, C3VarStmt.class);
  }

  @Override
  @Nullable
  public C3WhileStmt getWhileStmt() {
    return PsiTreeUtil.getChildOfType(this, C3WhileStmt.class);
  }

}
