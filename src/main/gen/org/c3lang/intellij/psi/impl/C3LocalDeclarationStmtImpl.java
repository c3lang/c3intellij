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

public class C3LocalDeclarationStmtImpl extends C3PsiElementImpl implements C3LocalDeclarationStmt {

  public C3LocalDeclarationStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitLocalDeclarationStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3DeclStmtAfterType getDeclStmtAfterType() {
    return PsiTreeUtil.getChildOfType(this, C3DeclStmtAfterType.class);
  }

  @Override
  @Nullable
  public C3LocalDeclStorage getLocalDeclStorage() {
    return PsiTreeUtil.getChildOfType(this, C3LocalDeclStorage.class);
  }

  @Override
  @NotNull
  public C3OptionalType getOptionalType() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3OptionalType.class));
  }

}
