// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3ForeachStmt extends PsiElement {

  @NotNull
  C3Expr getExpr();

  @NotNull
  C3ForeachVars getForeachVars();

  @Nullable
  C3Label getLabel();

  @NotNull
  C3Statement getStatement();

}
