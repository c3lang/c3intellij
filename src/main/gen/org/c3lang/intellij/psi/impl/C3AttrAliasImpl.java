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

public class C3AttrAliasImpl extends ASTWrapperPsiElement implements C3AttrAlias {

  public C3AttrAliasImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAttrAlias(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3PathAtIdent getPathAtIdent() {
    return findNotNullChildByClass(C3PathAtIdent.class);
  }

}
