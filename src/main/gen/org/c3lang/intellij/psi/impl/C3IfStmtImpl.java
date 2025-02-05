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

public class C3IfStmtImpl extends C3PsiElementImpl implements C3IfStmt {

  public C3IfStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitIfStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3CompoundStatement getCompoundStatement() {
    return findChildByClass(C3CompoundStatement.class);
  }

  @Override
  @Nullable
  public C3ElsePart getElsePart() {
    return findChildByClass(C3ElsePart.class);
  }

  @Override
  @Nullable
  public C3Label getLabel() {
    return findChildByClass(C3Label.class);
  }

  @Override
  @Nullable
  public C3ParenCond getParenCond() {
    return findChildByClass(C3ParenCond.class);
  }

  @Override
  @Nullable
  public C3Statement getStatement() {
    return findChildByClass(C3Statement.class);
  }

  @Override
  @Nullable
  public C3SwitchBody getSwitchBody() {
    return findChildByClass(C3SwitchBody.class);
  }

}
