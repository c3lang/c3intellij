// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3CaseStmt extends PsiElement {

  @NotNull
  List<C3Expr> getExprList();

  @Nullable
  C3StatementList getStatementList();

  @Nullable
  C3Type getType();

}
