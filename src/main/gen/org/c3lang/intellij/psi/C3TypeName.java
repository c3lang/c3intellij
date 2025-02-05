// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3TypeNameStub;
import org.c3lang.intellij.stubs.C3TypeEnum;

public interface C3TypeName extends C3TypePsiElement, StubBasedPsiElement<C3TypeNameStub> {

  @NotNull
  String getSourceFileName();

  @Nullable
  ModuleName getModuleName();

  @NotNull
  TypeName getTypeName();

  @NotNull
  C3TypeEnum getTypeEnum();

}
