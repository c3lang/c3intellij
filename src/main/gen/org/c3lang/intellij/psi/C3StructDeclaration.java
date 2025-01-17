// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3StructDeclarationStub;

public interface C3StructDeclaration extends C3PsiElement, StubBasedPsiElement<C3StructDeclarationStub> {

  @Nullable
  C3Attributes getAttributes();

  @Nullable
  C3InterfaceImpl getInterfaceImpl();

  @NotNull
  C3StructBody getStructBody();

  @NotNull
  C3TypeName getTypeName();

}
