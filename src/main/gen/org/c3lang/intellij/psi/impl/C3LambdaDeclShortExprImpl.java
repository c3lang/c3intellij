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

public class C3LambdaDeclShortExprImpl extends C3ExprImpl implements C3LambdaDeclShortExpr {

  public C3LambdaDeclShortExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitLambdaDeclShortExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return findChildByClass(C3Expr.class);
  }

  @Override
  @NotNull
  public C3LambdaDecl getLambdaDecl() {
    return findNotNullChildByClass(C3LambdaDecl.class);
  }

}
