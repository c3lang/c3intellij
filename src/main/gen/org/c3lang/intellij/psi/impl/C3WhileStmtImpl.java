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

public class C3WhileStmtImpl extends C3PsiElementImpl implements C3WhileStmt {

  public C3WhileStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitWhileStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Label getLabel() {
    return PsiTreeUtil.getChildOfType(this, C3Label.class);
  }

  @Override
  @NotNull
  public C3ParenCond getParenCond() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3ParenCond.class));
  }

  @Override
  @NotNull
  public C3Statement getStatement() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3Statement.class));
  }

}
