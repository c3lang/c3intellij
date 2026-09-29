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

public class C3PathAtIdentImpl extends C3PsiElementImpl implements C3PathAtIdent {

  public C3PathAtIdentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitPathAtIdent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Path getPath() {
    return findChildByClass(C3Path.class);
  }

  // MANUAL PATCH (not generated): references for `@macro` names in calls.
  // A PsiReferenceContributor was attempted first, but the platform never
  // invokes it for this element; getReference() follows the pattern used by
  // all other C3 references. Guarded by InterfaceContractTest navigation tests.
  @Override
  @NotNull
  public com.intellij.psi.PsiReference getReference() {
    return new org.c3lang.intellij.psi.reference.C3AtMacroReference(this);
  }

}
