// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3Statement extends PsiElement {

  @Nullable
  C3AsmBlockStmt getAsmBlockStmt();

  @Nullable
  C3AssertStmt getAssertStmt();

  @Nullable
  C3BreakStmt getBreakStmt();

  @Nullable
  C3CompoundStatement getCompoundStatement();

  @Nullable
  C3ContinueStmt getContinueStmt();

  @Nullable
  C3CtAssertStmt getCtAssertStmt();

  @Nullable
  C3CtEchoStmt getCtEchoStmt();

  @Nullable
  C3CtForStmt getCtForStmt();

  @Nullable
  C3CtForeachStmt getCtForeachStmt();

  @Nullable
  C3CtIfStmt getCtIfStmt();

  @Nullable
  C3CtSwitchStmt getCtSwitchStmt();

  @Nullable
  C3DeclarationStmt getDeclarationStmt();

  @Nullable
  C3DeferStmt getDeferStmt();

  @Nullable
  C3DoStmt getDoStmt();

  @Nullable
  C3Expr getExpr();

  @Nullable
  C3ForStmt getForStmt();

  @Nullable
  C3ForeachStmt getForeachStmt();

  @Nullable
  C3IfStmt getIfStmt();

  @Nullable
  C3NextcaseStmt getNextcaseStmt();

  @Nullable
  C3ReturnStmt getReturnStmt();

  @Nullable
  C3SwitchStmt getSwitchStmt();

  @Nullable
  C3VarStmt getVarStmt();

  @Nullable
  C3WhileStmt getWhileStmt();

}
