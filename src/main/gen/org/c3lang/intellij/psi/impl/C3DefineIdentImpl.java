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

public class C3DefineIdentImpl extends ASTWrapperPsiElement implements C3DefineIdent {

  public C3DefineIdentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDefineIdent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AttrAlias getAttrAlias() {
    return findChildByClass(C3AttrAlias.class);
  }

  @Override
  @Nullable
  public C3ConstAlias getConstAlias() {
    return findChildByClass(C3ConstAlias.class);
  }

  @Override
  @Nullable
  public C3GenericParameters getGenericParameters() {
    return findChildByClass(C3GenericParameters.class);
  }

  @Override
  @Nullable
  public C3IdentAlias getIdentAlias() {
    return findChildByClass(C3IdentAlias.class);
  }

}
