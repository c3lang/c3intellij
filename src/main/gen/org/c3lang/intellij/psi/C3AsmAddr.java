// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3AsmAddr extends PsiElement {

  @Nullable
  C3AsmAddrTrailing getAsmAddrTrailing();

  @NotNull
  List<C3AsmExpr> getAsmExprList();

  @Nullable
  C3BinaryOp getBinaryOp();

}
