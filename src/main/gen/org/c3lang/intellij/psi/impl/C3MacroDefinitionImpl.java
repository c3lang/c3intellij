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

public class C3MacroDefinitionImpl extends C3PsiElementImpl implements C3MacroDefinition {

  public C3MacroDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitMacroDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return PsiTreeUtil.getChildOfType(this, C3Attributes.class);
  }

  @Override
  @NotNull
  public C3MacroFuncBody getMacroFuncBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3MacroFuncBody.class));
  }

  @Override
  @NotNull
  public C3MacroHeader getMacroHeader() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3MacroHeader.class));
  }

  @Override
  @NotNull
  public C3MacroParams getMacroParams() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3MacroParams.class));
  }

}
