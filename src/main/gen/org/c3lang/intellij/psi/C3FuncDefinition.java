// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3FuncDefinition extends PsiElement {

  @Nullable
  C3Attributes getAttributes();

  @NotNull
  C3FnParameterList getFnParameterList();

  @NotNull
  C3FuncHeader getFuncHeader();

  @Nullable
  C3MacroFuncBody getMacroFuncBody();

}
