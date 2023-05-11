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

public class C3DefDeclarationSourceImpl extends ASTWrapperPsiElement implements C3DefDeclarationSource {

  public C3DefDeclarationSourceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDefDeclarationSource(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AnyIdent getAnyIdent() {
    return findChildByClass(C3AnyIdent.class);
  }

  @Override
  @Nullable
  public C3DefAttrValues getDefAttrValues() {
    return findChildByClass(C3DefAttrValues.class);
  }

  @Override
  @Nullable
  public C3GenericParameter getGenericParameter() {
    return findChildByClass(C3GenericParameter.class);
  }

  @Override
  @Nullable
  public C3PathConst getPathConst() {
    return findChildByClass(C3PathConst.class);
  }

  @Override
  @Nullable
  public C3PathIdent getPathIdent() {
    return findChildByClass(C3PathIdent.class);
  }

  @Override
  @Nullable
  public C3TypedefType getTypedefType() {
    return findChildByClass(C3TypedefType.class);
  }

}
