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

public class C3CtCondExprImpl extends C3ExprImpl implements C3CtCondExpr {

  public C3CtCondExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitCtCondExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3CtCond getCtCond() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3CtCond.class));
  }

  @Override
  @NotNull
  public C3ExpressionList getExpressionList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3ExpressionList.class));
  }

}
