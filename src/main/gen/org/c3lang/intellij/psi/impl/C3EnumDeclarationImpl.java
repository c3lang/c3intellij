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

public class C3EnumDeclarationImpl extends ASTWrapperPsiElement implements C3EnumDeclaration {

  public C3EnumDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitEnumDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return findChildByClass(C3Attributes.class);
  }

  @Override
  @NotNull
  public C3EnumList getEnumList() {
    return findNotNullChildByClass(C3EnumList.class);
  }

  @Override
  @Nullable
  public C3EnumSpec getEnumSpec() {
    return findChildByClass(C3EnumSpec.class);
  }

  @Override
  @NotNull
  public C3TypeName getTypeName() {
    return findNotNullChildByClass(C3TypeName.class);
  }

}
