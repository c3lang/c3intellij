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

public class C3DeclarationStmtImpl extends ASTWrapperPsiElement implements C3DeclarationStmt {

  public C3DeclarationStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDeclarationStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3ConstDecl getConstDecl() {
    return findChildByClass(C3ConstDecl.class);
  }

  @Override
  @Nullable
  public C3DeclStmtAfterType getDeclStmtAfterType() {
    return findChildByClass(C3DeclStmtAfterType.class);
  }

  @Override
  @Nullable
  public C3LocalDeclStorage getLocalDeclStorage() {
    return findChildByClass(C3LocalDeclStorage.class);
  }

  @Override
  @Nullable
  public C3OptionalType getOptionalType() {
    return findChildByClass(C3OptionalType.class);
  }

}
