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

public class C3BinaryExprImpl extends C3ExprImpl implements C3BinaryExpr {

  public C3BinaryExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitBinaryExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<C3Expr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, C3Expr.class);
  }

  @Override
  @NotNull
  public C3Expr getLeft() {
    List<C3Expr> p1 = getExprList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public C3Expr getRight() {
    List<C3Expr> p1 = getExprList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
