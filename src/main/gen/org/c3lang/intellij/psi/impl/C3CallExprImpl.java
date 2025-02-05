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

public class C3CallExprImpl extends C3CallExprMixinImpl implements C3CallExpr {

  public C3CallExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitCallExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3CallExprTail getCallExprTail() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3CallExprTail.class));
  }

  @Override
  @NotNull
  public C3Expr getExpr() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3Expr.class));
  }

}
