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

public class C3DefDeclImpl extends C3PsiElementImpl implements C3DefDecl {

  public C3DefDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDefDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3AnyIdent getAnyIdent() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3AnyIdent.class));
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return PsiTreeUtil.getChildOfType(this, C3Attributes.class);
  }

  @Override
  @Nullable
  public C3DefDeclarationSource getDefDeclarationSource() {
    return PsiTreeUtil.getChildOfType(this, C3DefDeclarationSource.class);
  }

  @Override
  @Nullable
  public C3ParameterList getParameterList() {
    return PsiTreeUtil.getChildOfType(this, C3ParameterList.class);
  }

}
