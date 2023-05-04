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
  public C3BitstructDeclaration getBitstructDeclaration() {
    return findChildByClass(C3BitstructDeclaration.class);
  }

  @Override
  @Nullable
  public C3ConstDeclaration getConstDeclaration() {
    return findChildByClass(C3ConstDeclaration.class);
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
  public C3CtIncludeStmt getCtIncludeStmt() {
    return findChildByClass(C3CtIncludeStmt.class);
  }

  @Override
  @Nullable
  public C3DefDeclaration getDefDeclaration() {
    return findChildByClass(C3DefDeclaration.class);
  }

  @Override
  @Nullable
  public C3DefineDeclaration getDefineDeclaration() {
    return findChildByClass(C3DefineDeclaration.class);
  }

  @Override
  @Nullable
  public C3EnumDeclaration getEnumDeclaration() {
    return findChildByClass(C3EnumDeclaration.class);
  }

  @Override
  @Nullable
  public C3Expr getExpr() {
    return findChildByClass(C3Expr.class);
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
  public C3GlobalDeclaration getGlobalDeclaration() {
    return findChildByClass(C3GlobalDeclaration.class);
  }

  @Override
  @Nullable
  public C3ImportDecl getImportDecl() {
    return findChildByClass(C3ImportDecl.class);
  }

  @Override
  @Nullable
  public C3MacroDeclaration getMacroDeclaration() {
    return findChildByClass(C3MacroDeclaration.class);
  }

  @Override
  @Nullable
  public C3StaticDeclaration getStaticDeclaration() {
    return findChildByClass(C3StaticDeclaration.class);
  }

  @Override
  @Nullable
  public C3StructDeclaration getStructDeclaration() {
    return findChildByClass(C3StructDeclaration.class);
  }

  @Override
  @Nullable
  public C3TlCtIf getTlCtIf() {
    return findChildByClass(C3TlCtIf.class);
  }

  @Override
  @Nullable
  public C3TlCtSwitch getTlCtSwitch() {
    return findChildByClass(C3TlCtSwitch.class);
  }

  @Override
  @Nullable
  public C3TypedefDeclaration getTypedefDeclaration() {
    return findChildByClass(C3TypedefDeclaration.class);
  }

}
