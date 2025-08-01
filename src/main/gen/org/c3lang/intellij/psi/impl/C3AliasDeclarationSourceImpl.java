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

public class C3AliasDeclarationSourceImpl extends C3PsiElementImpl implements C3AliasDeclarationSource {

  public C3AliasDeclarationSourceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAliasDeclarationSource(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3GenericParameters getGenericParameters() {
    return findChildByClass(C3GenericParameters.class);
  }

  @Override
  @Nullable
  public C3Path getPath() {
    return findChildByClass(C3Path.class);
  }

  @Override
  @Nullable
  public C3PathAtIdent getPathAtIdent() {
    return findChildByClass(C3PathAtIdent.class);
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

}
