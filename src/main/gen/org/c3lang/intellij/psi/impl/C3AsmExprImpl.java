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

public class C3AsmExprImpl extends C3ExprImpl implements C3AsmExpr {

  public C3AsmExprImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAsmExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AsmAddr getAsmAddr() {
    return PsiTreeUtil.getChildOfType(this, C3AsmAddr.class);
  }

  @Override
  @Nullable
  public C3GroupedExpression getGroupedExpression() {
    return PsiTreeUtil.getChildOfType(this, C3GroupedExpression.class);
  }

  @Override
  @Nullable
  public PsiElement getConstIdent() {
    return findChildByType(CONST_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getCtConstIdent() {
    return findChildByType(CT_CONST_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getCtIdent() {
    return findChildByType(CT_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getFloatLiteral() {
    return findChildByType(FLOAT_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getIdent() {
    return findChildByType(IDENT);
  }

  @Override
  @Nullable
  public PsiElement getIntLiteral() {
    return findChildByType(INT_LITERAL);
  }

}
