// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3GlobalDecl extends C3PsiElement {

  @Nullable
  C3GlobalMultiDeclaration getGlobalMultiDeclaration();

  @Nullable
  C3GlobalSingleDeclaration getGlobalSingleDeclaration();

  @NotNull
  C3OptionalType getOptionalType();

  @Nullable
  PsiElement getIdent();

}
