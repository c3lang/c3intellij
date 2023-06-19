// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;

public interface C3ModuleSection extends PsiElement, StubBasedPsiElement<C3ModuleSectionStub> {

  @NotNull
  C3Module getModule();

  @NotNull
  List<C3TopLevel> getTopLevelList();

}
