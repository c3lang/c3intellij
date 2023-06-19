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

public class C3TypeAccessExprImpl extends C3ExprImpl implements C3TypeAccessExpr {

  public C3TypeAccessExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitTypeAccessExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AccessIdent getAccessIdent() {
    return PsiTreeUtil.getChildOfType(this, C3AccessIdent.class);
  }

  @Override
  @NotNull
  public C3Type getType() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3Type.class));
  }

}
