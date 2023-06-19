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

public class C3BitstructDeclarationImpl extends ASTWrapperPsiElement implements C3BitstructDeclaration {

  public C3BitstructDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitBitstructDeclaration(this);
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
  @NotNull
  public C3BitstructBody getBitstructBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3BitstructBody.class));
  }

  @Override
  @NotNull
  public C3Type getType() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3Type.class));
  }

  @Override
  @NotNull
  public C3TypeName getTypeName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3TypeName.class));
  }

}
