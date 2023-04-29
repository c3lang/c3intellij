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

public class C3CallExprTailImpl extends ASTWrapperPsiElement implements C3CallExprTail {

  public C3CallExprTailImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitCallExprTail(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AccessIdent getAccessIdent() {
    return findChildByClass(C3AccessIdent.class);
  }

  @Override
  @Nullable
  public C3CallInvocation getCallInvocation() {
    return findChildByClass(C3CallInvocation.class);
  }

  @Override
  @Nullable
  public C3CompoundStatement getCompoundStatement() {
    return findChildByClass(C3CompoundStatement.class);
  }

  @Override
  @Nullable
  public C3RangeExp getRangeExp() {
    return findChildByClass(C3RangeExp.class);
  }

  @Override
  @Nullable
  public C3RangeLoc getRangeLoc() {
    return findChildByClass(C3RangeLoc.class);
  }

}
