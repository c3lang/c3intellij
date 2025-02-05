// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3MacroDefinition extends C3MacroDefinitionMixin {

  @Nullable
  C3Attributes getAttributes();

  @NotNull
  C3MacroFuncBody getMacroFuncBody();

  @NotNull
  C3MacroHeader getMacroHeader();

  @NotNull
  C3MacroParams getMacroParams();

}
