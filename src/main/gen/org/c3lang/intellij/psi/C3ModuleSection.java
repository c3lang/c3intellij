// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3ModuleSection extends C3ImportProvider {

  @NotNull
  C3Module getModule();

  @NotNull
  List<C3TopLevel> getTopLevelList();

  @Nullable
  ModuleName getModuleName();

  @NotNull
  List<ModuleName> getImports();

  @NotNull
  List<C3ImportDecl> getImportDeclarations();

}
