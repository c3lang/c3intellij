// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface C3TypeDecl extends C3NamedElement {

  @Nullable
  C3BitstructDeclaration getBitstructDeclaration();

  @Nullable
  C3EnumDeclaration getEnumDeclaration();

  @Nullable
  C3FaultDeclaration getFaultDeclaration();

  @Nullable
  C3StructDeclaration getStructDeclaration();

  @Nullable
  C3TypedefDeclaration getTypedefDeclaration();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

}
