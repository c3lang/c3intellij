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

public class C3AttributeImpl extends C3PsiElementImpl implements C3Attribute {

  public C3AttributeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAttribute(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3AttributeName getAttributeName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3AttributeName.class));
  }

  @Override
  @Nullable
  public C3AttributeParamList getAttributeParamList() {
    return PsiTreeUtil.getChildOfType(this, C3AttributeParamList.class);
  }

}
