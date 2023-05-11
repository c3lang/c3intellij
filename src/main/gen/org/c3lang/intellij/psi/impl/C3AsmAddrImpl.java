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

public class C3AsmAddrImpl extends ASTWrapperPsiElement implements C3AsmAddr {

  public C3AsmAddrImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitAsmAddr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3AsmAddrTrailing getAsmAddrTrailing() {
    return findChildByClass(C3AsmAddrTrailing.class);
  }

  @Override
  @NotNull
  public List<C3AsmExpr> getAsmExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, C3AsmExpr.class);
  }

  @Override
  @Nullable
  public C3BinaryOp getBinaryOp() {
    return findChildByClass(C3BinaryOp.class);
  }

}
