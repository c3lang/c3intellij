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

public class C3SwitchStmtImpl extends ASTWrapperPsiElement implements C3SwitchStmt {

  public C3SwitchStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitSwitchStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Label getLabel() {
    return PsiTreeUtil.getChildOfType(this, C3Label.class);
  }

  @Override
  @Nullable
  public C3ParenCond getParenCond() {
    return PsiTreeUtil.getChildOfType(this, C3ParenCond.class);
  }

  @Override
  @Nullable
  public C3SwitchBody getSwitchBody() {
    return PsiTreeUtil.getChildOfType(this, C3SwitchBody.class);
  }

}
