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

public class C3AnyIdentImpl extends C3PsiElementImpl implements C3AnyIdent {

  public C3AnyIdentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAnyIdent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getAtIdent() {
    return findChildByType(AT_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getAtTypeIdent() {
    return findChildByType(AT_TYPE_IDENT);
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
  public PsiElement getCtTypeIdent() {
    return findChildByType(CT_TYPE_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getIdent() {
    return findChildByType(IDENT);
  }

  @Override
  @Nullable
  public PsiElement getTypeIdent() {
    return findChildByType(TYPE_IDENT);
  }

}
