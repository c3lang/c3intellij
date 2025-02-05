// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.psi.*;

public class C3StatementImpl extends C3PsiElementImpl implements C3Statement {

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
    return findChildByClass(C3AsmBlockStmt.class);
  }

  @Override
  @Nullable
  public C3AssertStmt getAssertStmt() {
    return findChildByClass(C3AssertStmt.class);
  }

  @Override
  @Nullable
  public C3BreakStmt getBreakStmt() {
    return findChildByClass(C3BreakStmt.class);
  }

  @Override
  @Nullable
  public C3CompoundStatement getCompoundStatement() {
    return findChildByClass(C3CompoundStatement.class);
  }

  @Override
  @Nullable
  public C3ConstDeclarationStmt getConstDeclarationStmt() {
    return findChildByClass(C3ConstDeclarationStmt.class);
  }

  @Override
  @Nullable
  public C3ContinueStmt getContinueStmt() {
    return findChildByClass(C3ContinueStmt.class);
  }

  @Override
  @Nullable
  public C3CtAssertStmt getCtAssertStmt() {
    return findChildByClass(C3CtAssertStmt.class);
  }

  @Override
  @Nullable
  public C3CtEchoStmt getCtEchoStmt() {
    return findChildByClass(C3CtEchoStmt.class);
  }

  @Override
  @Nullable
  public C3CtErrorStmt getCtErrorStmt() {
    return findChildByClass(C3CtErrorStmt.class);
  }

  @Override
  @Nullable
  public C3CtForStmt getCtForStmt() {
    return findChildByClass(C3CtForStmt.class);
  }

  @Override
  @Nullable
  public C3CtForeachStmt getCtForeachStmt() {
    return findChildByClass(C3CtForeachStmt.class);
  }

  @Override
  @Nullable
  public C3CtIfStmt getCtIfStmt() {
    return findChildByClass(C3CtIfStmt.class);
  }

  @Override
  @Nullable
  public C3CtSwitchStmt getCtSwitchStmt() {
    return findChildByClass(C3CtSwitchStmt.class);
  }

  @Override
  @Nullable
  public C3DeferStmt getDeferStmt() {
    return findChildByClass(C3DeferStmt.class);
  }

  @Override
  @Nullable
  public C3DoStmt getDoStmt() {
    return findChildByClass(C3DoStmt.class);
  }

  @Override
  @Nullable
  public C3ExprStmt getExprStmt() {
    return findChildByClass(C3ExprStmt.class);
  }

  @Override
  @Nullable
  public C3ForStmt getForStmt() {
    return findChildByClass(C3ForStmt.class);
  }

  @Override
  @Nullable
  public C3ForeachStmt getForeachStmt() {
    return findChildByClass(C3ForeachStmt.class);
  }

  @Override
  @Nullable
  public C3IfStmt getIfStmt() {
    return findChildByClass(C3IfStmt.class);
  }

  @Override
  @Nullable
  public C3LocalDeclarationStmt getLocalDeclarationStmt() {
    return findChildByClass(C3LocalDeclarationStmt.class);
  }

  @Override
  @Nullable
  public C3NextcaseStmt getNextcaseStmt() {
    return findChildByClass(C3NextcaseStmt.class);
  }

  @Override
  @Nullable
  public C3ReturnStmt getReturnStmt() {
    return findChildByClass(C3ReturnStmt.class);
  }

  @Override
  @Nullable
  public C3SwitchStmt getSwitchStmt() {
    return findChildByClass(C3SwitchStmt.class);
  }

  @Override
  @Nullable
  public C3VarStmt getVarStmt() {
    return findChildByClass(C3VarStmt.class);
  }

  @Override
  @Nullable
  public C3WhileStmt getWhileStmt() {
    return findChildByClass(C3WhileStmt.class);
  }

}
