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

public class C3DoStmtImpl extends C3PsiElementImpl implements C3DoStmt {

  public C3DoStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDoStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3CompoundStatement getCompoundStatement() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3CompoundStatement.class));
  }

  @Override
  @Nullable
  public C3GroupedExpression getGroupedExpression() {
    return PsiTreeUtil.getChildOfType(this, C3GroupedExpression.class);
  }

  @Override
  @Nullable
  public C3Label getLabel() {
    return PsiTreeUtil.getChildOfType(this, C3Label.class);
  }

}
