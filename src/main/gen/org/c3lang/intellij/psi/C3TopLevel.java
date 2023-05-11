// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3TopLevel extends PsiElement {

  @Nullable
  C3ConstDecl getConstDecl();

  @Nullable
  C3CtAssertStmt getCtAssertStmt();

  @Nullable
  C3CtEchoStmt getCtEchoStmt();

  @Nullable
  C3CtIncludeStmt getCtIncludeStmt();

  @Nullable
  C3DefDecl getDefDecl();

  @Nullable
  C3DefineDeclaration getDefineDeclaration();

  @Nullable
  C3Expr getExpr();

  @Nullable
  C3FaultDeclaration getFaultDeclaration();

  @Nullable
  C3FuncDefinition getFuncDefinition();

  @Nullable
  C3GlobalDecl getGlobalDecl();

  @Nullable
  C3ImportDecl getImportDecl();

  @Nullable
  C3MacroDefinition getMacroDefinition();

  @Nullable
  C3StaticDecl getStaticDecl();

  @Nullable
  C3TlCtIf getTlCtIf();

  @Nullable
  C3TlCtSwitch getTlCtSwitch();

  @Nullable
  C3TypeDecl getTypeDecl();

}
