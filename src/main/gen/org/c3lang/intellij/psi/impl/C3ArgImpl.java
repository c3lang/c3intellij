// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.c3lang.intellij.psi.*;

public class C3ArgImpl extends ASTWrapperPsiElement implements C3Arg {

  public C3ArgImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitArg(this);
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
  public C3NamedIdent getNamedIdent() {
    return PsiTreeUtil.getChildOfType(this, C3NamedIdent.class);
  }

  @Override
  @Nullable
  public C3ParamPath getParamPath() {
    return PsiTreeUtil.getChildOfType(this, C3ParamPath.class);
  }

  @Override
  @Nullable
  public C3RangeExp getRangeExp() {
    return PsiTreeUtil.getChildOfType(this, C3RangeExp.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return PsiTreeUtil.getChildOfType(this, C3Type.class);
  }

}
