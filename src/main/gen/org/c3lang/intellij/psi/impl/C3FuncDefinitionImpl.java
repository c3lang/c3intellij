// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.c3lang.intellij.psi.*;

public class C3FuncDefinitionImpl extends ASTWrapperPsiElement implements C3FuncDefinition {

  public C3FuncDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitFuncDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3FuncDef getFuncDef() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3FuncDef.class));
  }

  @Override
  @Nullable
  public C3MacroFuncBody getMacroFuncBody() {
    return PsiTreeUtil.getChildOfType(this, C3MacroFuncBody.class);
  }

}
