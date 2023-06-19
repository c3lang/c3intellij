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

public class C3TopLevelImpl extends ASTWrapperPsiElement implements C3TopLevel {

  public C3TopLevelImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitTopLevel(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3ConstDecl getConstDecl() {
    return PsiTreeUtil.getChildOfType(this, C3ConstDecl.class);
  }

  @Override
  @Nullable
  public C3CtAssertStmt getCtAssertStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtAssertStmt.class);
  }

  @Override
  @Nullable
  public C3CtEchoStmt getCtEchoStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtEchoStmt.class);
  }

  @Override
  @Nullable
  public C3CtIncludeStmt getCtIncludeStmt() {
    return PsiTreeUtil.getChildOfType(this, C3CtIncludeStmt.class);
  }

  @Override
  @Nullable
  public C3DefDecl getDefDecl() {
    return PsiTreeUtil.getChildOfType(this, C3DefDecl.class);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return PsiTreeUtil.getChildOfType(this, C3Expr.class);
  }

  @Override
  @Nullable
  public C3FaultDeclaration getFaultDeclaration() {
    return PsiTreeUtil.getChildOfType(this, C3FaultDeclaration.class);
  }

  @Override
  @Nullable
  public C3FuncDefinition getFuncDefinition() {
    return PsiTreeUtil.getChildOfType(this, C3FuncDefinition.class);
  }

  @Override
  @Nullable
  public C3GlobalDecl getGlobalDecl() {
    return PsiTreeUtil.getChildOfType(this, C3GlobalDecl.class);
  }

  @Override
  @Nullable
  public C3ImportDecl getImportDecl() {
    return PsiTreeUtil.getChildOfType(this, C3ImportDecl.class);
  }

  @Override
  @Nullable
  public C3MacroDefinition getMacroDefinition() {
    return PsiTreeUtil.getChildOfType(this, C3MacroDefinition.class);
  }

  @Override
  @Nullable
  public C3StaticDecl getStaticDecl() {
    return PsiTreeUtil.getChildOfType(this, C3StaticDecl.class);
  }

  @Override
  @Nullable
  public C3TypeDecl getTypeDecl() {
    return PsiTreeUtil.getChildOfType(this, C3TypeDecl.class);
  }

}
