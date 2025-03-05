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

public class C3DistinctDeclarationImpl extends C3PsiElementImpl implements C3DistinctDeclaration {

  public C3DistinctDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDistinctDeclaration(this);
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
  @Nullable
  public C3GenericParameters getGenericParameters() {
    return findChildByClass(C3GenericParameters.class);
  }

  @Override
  @Nullable
  public C3InterfaceImpl getInterfaceImpl() {
    return findChildByClass(C3InterfaceImpl.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return findChildByClass(C3Type.class);
  }

  @Override
  @NotNull
  public C3TypeName getTypeName() {
    return findNotNullChildByClass(C3TypeName.class);
  }

}
