// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3MacroDefinitionStub;

public interface C3MacroDefinition extends C3CallablePsiElement, StubBasedPsiElement<C3MacroDefinitionStub> {

  @Nullable
  C3Attributes getAttributes();

  @NotNull
  C3MacroFuncBody getMacroFuncBody();

  @NotNull
  C3MacroHeader getMacroHeader();

  @NotNull
  C3MacroParams getMacroParams();

  @NotNull
  String getSourceFileName();

  @Nullable
  ModuleName getModuleName();

  @Nullable
  TypeName getTypeName();

  @NotNull
  FullyQualifiedName getFunctionOrMacroName();

  @Nullable
  ReturnTypeName getReturnTypeName();

  @NotNull
  String getParameterTypeNames();

}
