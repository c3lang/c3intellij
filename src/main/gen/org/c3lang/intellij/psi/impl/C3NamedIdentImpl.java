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

public class C3NamedIdentImpl extends C3PsiElementImpl implements C3NamedIdent {

  public C3NamedIdentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitNamedIdent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
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
  public PsiElement getHashIdent() {
    return findChildByType(HASH_IDENT);
  }

  @Override
  @Nullable
  public PsiElement getIdent() {
    return findChildByType(IDENT);
  }

}
