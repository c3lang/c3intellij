// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3StructDeclaration extends PsiElement {

  @Nullable
  C3Attributes getAttributes();

  @Nullable
  C3InterfaceImpl getInterfaceImpl();

  @NotNull
  C3StructBody getStructBody();

  @NotNull
  C3TypeName getTypeName();

}
