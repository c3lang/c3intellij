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

public class C3CompoundInitExprImpl extends C3ExprImpl implements C3CompoundInitExpr {

  public C3CompoundInitExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitCompoundInitExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3InitializerList getInitializerList() {
    return findNotNullChildByClass(C3InitializerList.class);
  }

  @Override
  @NotNull
  public C3Type getType() {
    return findNotNullChildByClass(C3Type.class);
  }

}
