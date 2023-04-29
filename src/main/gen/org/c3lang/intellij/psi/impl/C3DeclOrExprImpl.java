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

public class C3DeclOrExprImpl extends C3ExprImpl implements C3DeclOrExpr {

  public C3DeclOrExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDeclOrExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3LocalDeclAfterType getLocalDeclAfterType() {
    return findChildByClass(C3LocalDeclAfterType.class);
  }

  @Override
  @Nullable
  public C3OptionalType getOptionalType() {
    return findChildByClass(C3OptionalType.class);
  }

  @Override
  @Nullable
  public C3VarDecl getVarDecl() {
    return findChildByClass(C3VarDecl.class);
  }

}
