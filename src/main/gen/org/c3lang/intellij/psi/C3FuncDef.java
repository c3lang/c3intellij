// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3FuncDefStub;

public interface C3FuncDef extends C3PsiElement, StubBasedPsiElement<C3FuncDefStub> {

  @Nullable
  C3Attributes getAttributes();

  @NotNull
  C3FnParameterList getFnParameterList();

  @NotNull
  C3FuncHeader getFuncHeader();

}
