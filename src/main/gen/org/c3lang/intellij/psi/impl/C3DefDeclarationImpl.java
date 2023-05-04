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

public class C3DefDeclarationImpl extends ASTWrapperPsiElement implements C3DefDeclaration {

  public C3DefDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDefDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3AnyIdent getAnyIdent() {
    return findNotNullChildByClass(C3AnyIdent.class);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return findChildByClass(C3Attributes.class);
  }

  @Override
  @NotNull
  public C3DefDeclarationSource getDefDeclarationSource() {
    return findNotNullChildByClass(C3DefDeclarationSource.class);
  }

  @Override
  @Nullable
  public C3DistinctInline getDistinctInline() {
    return findChildByClass(C3DistinctInline.class);
  }

  @Override
  @Nullable
  public C3ParameterList getParameterList() {
    return findChildByClass(C3ParameterList.class);
  }

}
