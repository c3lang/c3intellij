// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3ModuleStub;

public interface C3Module extends C3PsiElement, StubBasedPsiElement<C3ModuleStub> {

  @Nullable
  C3Attributes getAttributes();

  @Nullable
  C3ModuleParams getModuleParams();

  @NotNull
  C3ModulePath getModulePath();

}
