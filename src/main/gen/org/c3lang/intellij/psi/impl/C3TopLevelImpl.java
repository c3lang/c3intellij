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

public class C3TopLevelImpl extends C3PsiElementImpl implements C3TopLevel {

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
  public C3AsmDeclaration getAsmDeclaration() {
    return findChildByClass(C3AsmDeclaration.class);
  }

  @Override
  @Nullable
  public C3ConstDeclarationStmt getConstDeclarationStmt() {
    return findChildByClass(C3ConstDeclarationStmt.class);
  }

  @Override
  @Nullable
  public C3CtAssertStmt getCtAssertStmt() {
    return findChildByClass(C3CtAssertStmt.class);
  }

  @Override
  @Nullable
  public C3CtEchoStmt getCtEchoStmt() {
    return findChildByClass(C3CtEchoStmt.class);
  }

  @Override
  @Nullable
  public C3CtErrorStmt getCtErrorStmt() {
    return findChildByClass(C3CtErrorStmt.class);
  }

  @Override
  @Nullable
  public C3CtIncludeStmt getCtIncludeStmt() {
    return findChildByClass(C3CtIncludeStmt.class);
  }

  @Override
  @Nullable
  public C3DefDecl getDefDecl() {
    return findChildByClass(C3DefDecl.class);
  }

  @Override
  @Nullable
  public C3DistinctDeclaration getDistinctDeclaration() {
    return findChildByClass(C3DistinctDeclaration.class);
  }

  @Override
  @Nullable
  public C3FaultDeclaration getFaultDeclaration() {
    return findChildByClass(C3FaultDeclaration.class);
  }

  @Override
  @Nullable
  public C3FuncDefinition getFuncDefinition() {
    return findChildByClass(C3FuncDefinition.class);
  }

  @Override
  @Nullable
  public C3GlobalDecl getGlobalDecl() {
    return findChildByClass(C3GlobalDecl.class);
  }

  @Override
  @Nullable
  public C3ImportDecl getImportDecl() {
    return findChildByClass(C3ImportDecl.class);
  }

  @Override
  @Nullable
  public C3InterfaceDefinition getInterfaceDefinition() {
    return findChildByClass(C3InterfaceDefinition.class);
  }

  @Override
  @Nullable
  public C3MacroDefinition getMacroDefinition() {
    return findChildByClass(C3MacroDefinition.class);
  }

  @Override
  @Nullable
  public C3TypeDecl getTypeDecl() {
    return findChildByClass(C3TypeDecl.class);
  }

}
