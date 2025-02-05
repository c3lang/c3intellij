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

public class C3TypeDeclImpl extends C3PsiElementImpl implements C3TypeDecl {

  public C3TypeDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitTypeDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3BitstructDeclaration getBitstructDeclaration() {
    return findChildByClass(C3BitstructDeclaration.class);
  }

  @Override
  @Nullable
  public C3EnumDeclaration getEnumDeclaration() {
    return findChildByClass(C3EnumDeclaration.class);
  }

  @Override
  @Nullable
  public C3FaultDeclaration getFaultDeclaration() {
    return findChildByClass(C3FaultDeclaration.class);
  }

  @Override
  @Nullable
  public C3StructDeclaration getStructDeclaration() {
    return findChildByClass(C3StructDeclaration.class);
  }

}
