// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3AsmExpr extends C3Expr {

  @Nullable
  C3AsmAddr getAsmAddr();

  @Nullable
  C3GroupedExpression getGroupedExpression();

  @Nullable
  PsiElement getConstIdent();

  @Nullable
  PsiElement getCtConstIdent();

  @Nullable
  PsiElement getCtIdent();

  @Nullable
  PsiElement getFloatLiteral();

  @Nullable
  PsiElement getIdent();

  @Nullable
  PsiElement getIntLiteral();

}
