// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3IfStmt extends C3PsiElement {

  @Nullable
  C3CompoundStatement getCompoundStatement();

  @Nullable
  C3ElsePart getElsePart();

  @Nullable
  C3Label getLabel();

  @Nullable
  C3ParenCond getParenCond();

  @Nullable
  C3Statement getStatement();

}
