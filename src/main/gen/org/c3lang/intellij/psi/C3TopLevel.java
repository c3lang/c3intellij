// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3TopLevel extends PsiElement {

  @Nullable
  C3BitstructDeclaration getBitstructDeclaration();

  @Nullable
  C3ConstDeclaration getConstDeclaration();

  @Nullable
  C3CtAssertStmt getCtAssertStmt();

  @Nullable
  C3CtEchoStmt getCtEchoStmt();

  @Nullable
  C3CtIncludeStmt getCtIncludeStmt();

  @Nullable
  C3DefDeclaration getDefDeclaration();

  @Nullable
  C3DefineDeclaration getDefineDeclaration();

  @Nullable
  C3EnumDeclaration getEnumDeclaration();

  @Nullable
  C3Expr getExpr();

  @Nullable
  C3FaultDeclaration getFaultDeclaration();

  @Nullable
  C3FuncDefinition getFuncDefinition();

  @Nullable
  C3GlobalDeclaration getGlobalDeclaration();

  @Nullable
  C3ImportDecl getImportDecl();

  @Nullable
  C3MacroDeclaration getMacroDeclaration();

  @Nullable
  C3StaticDeclaration getStaticDeclaration();

  @Nullable
  C3StructDeclaration getStructDeclaration();

  @Nullable
  C3TlCtIf getTlCtIf();

  @Nullable
  C3TlCtSwitch getTlCtSwitch();

  @Nullable
  C3TypedefDeclaration getTypedefDeclaration();

}
