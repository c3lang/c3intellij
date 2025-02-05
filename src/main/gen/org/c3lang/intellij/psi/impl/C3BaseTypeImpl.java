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

public class C3BaseTypeImpl extends C3PsiElementImpl implements C3BaseType {

  public C3BaseTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitBaseType(this);
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
  public C3FloatType getFloatType() {
    return PsiTreeUtil.getChildOfType(this, C3FloatType.class);
  }

  @Override
  @Nullable
  public C3GenericParameters getGenericParameters() {
    return PsiTreeUtil.getChildOfType(this, C3GenericParameters.class);
  }

  @Override
  @Nullable
  public C3GroupedExpression getGroupedExpression() {
    return PsiTreeUtil.getChildOfType(this, C3GroupedExpression.class);
  }

  @Override
  @Nullable
  public C3IntegerType getIntegerType() {
    return PsiTreeUtil.getChildOfType(this, C3IntegerType.class);
  }

  @Override
  @Nullable
  public C3Path getPath() {
    return PsiTreeUtil.getChildOfType(this, C3Path.class);
  }

}
