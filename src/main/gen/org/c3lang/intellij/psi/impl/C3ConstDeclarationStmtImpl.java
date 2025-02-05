// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.stubs.C3ConstDeclarationStmtStub;
import org.c3lang.intellij.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3ConstDeclarationStmtImpl extends C3StubBasedPsiElementBase<C3ConstDeclarationStmtStub> implements C3ConstDeclarationStmt {

  public C3ConstDeclarationStmtImpl(@NotNull C3ConstDeclarationStmtStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3ConstDeclarationStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3ConstDeclarationStmtImpl(@NotNull C3ConstDeclarationStmtStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitConstDeclarationStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return PsiTreeUtil.getChildOfType(this, C3Attributes.class);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return PsiTreeUtil.getChildOfType(this, C3Expr.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return PsiTreeUtil.getChildOfType(this, C3Type.class);
  }

}
