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

public class C3VarDeclImpl extends C3PsiElementImpl implements C3VarDecl {

  public C3VarDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitVarDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return PsiTreeUtil.getChildOfType(this, C3Expr.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return PsiTreeUtil.getChildOfType(this, C3Type.class);
  }

  @Override
  @Nullable
  public PsiElement getCtIdent() {
    return findChildByType(CT_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getCtTypeIdent() {
    return findChildByType(CT_TYPE_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getIdent() {
    return findChildByType(IDENT);
  }

}
