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
import org.c3lang.intellij.stubs.C3ModuleStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3ModuleImpl extends C3ModuleMixinImpl implements C3Module {

  public C3ModuleImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3ModuleImpl(@NotNull C3ModuleStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3ModuleImpl(@NotNull C3ModuleStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitModule(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public C3Attributes getAttributes() {
    return findChildByClass(C3Attributes.class);
  }

  @Override
  @Nullable
  public C3ModuleParams getModuleParams() {
    return findChildByClass(C3ModuleParams.class);
  }

  @Override
  @NotNull
  public C3ModulePath getModulePath() {
    return findNotNullChildByClass(C3ModulePath.class);
  }

}
