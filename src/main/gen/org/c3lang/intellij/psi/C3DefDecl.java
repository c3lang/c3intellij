// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3DefDecl extends PsiElement {

  @NotNull
  C3AnyIdent getAnyIdent();

  @Nullable
  C3Attributes getAttributes();

  @NotNull
  C3DefDeclarationSource getDefDeclarationSource();

  @Nullable
  C3ParameterList getParameterList();

}
