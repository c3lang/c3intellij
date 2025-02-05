// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.psi.*;

public class C3DefaultModuleSectionImpl extends C3PsiElementImpl implements C3DefaultModuleSection {

  public C3DefaultModuleSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitDefaultModuleSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<C3TopLevel> getTopLevelList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, C3TopLevel.class);
  }

  @Override
  @Nullable
  public ModuleName getModuleName() {
    return C3ParserUtils.getModuleName(this);
  }

  @Override
  @NotNull
  public List<ModuleName> getImports() {
    return C3ParserUtils.getImports(this);
  }

  @Override
  @NotNull
  public List<C3ImportDecl> getImportDeclarations() {
    return C3ParserUtils.getImportDeclarations(this);
  }

}
