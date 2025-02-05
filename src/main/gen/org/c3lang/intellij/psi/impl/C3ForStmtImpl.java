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

public class C3ForStmtImpl extends C3PsiElementImpl implements C3ForStmt {

  public C3ForStmtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitForStmt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3ForCond getForCond() {
    return findChildByClass(C3ForCond.class);
  }

  @Override
  @Nullable
  public C3Label getLabel() {
    return findChildByClass(C3Label.class);
  }

  @Override
  @Nullable
  public C3Statement getStatement() {
    return findChildByClass(C3Statement.class);
  }

}
