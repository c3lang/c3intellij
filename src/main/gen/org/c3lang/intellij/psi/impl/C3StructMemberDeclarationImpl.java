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

public class C3StructMemberDeclarationImpl extends C3PsiElementImpl implements C3StructMemberDeclaration {

  public C3StructMemberDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitStructMemberDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return PsiTreeUtil.getChildOfType(this, C3Attributes.class);
  }

  @Override
  @Nullable
  public C3BitstructBody getBitstructBody() {
    return PsiTreeUtil.getChildOfType(this, C3BitstructBody.class);
  }

  @Override
  @Nullable
  public C3IdentifierList getIdentifierList() {
    return PsiTreeUtil.getChildOfType(this, C3IdentifierList.class);
  }

  @Override
  @Nullable
  public C3StructBody getStructBody() {
    return PsiTreeUtil.getChildOfType(this, C3StructBody.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return PsiTreeUtil.getChildOfType(this, C3Type.class);
  }

  @Override
  @Nullable
  public PsiElement getIdent() {
    return findChildByType(IDENT);
  }

}
