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
import org.c3lang.intellij.stubs.C3FuncDefStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3FuncDefImpl extends C3FuncDefMixinImpl implements C3FuncDef {

  public C3FuncDefImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3FuncDefImpl(@NotNull C3FuncDefStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3FuncDefImpl(@NotNull C3FuncDefStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitFuncDef(this);
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
  @NotNull
  public C3FnParameterList getFnParameterList() {
    return findNotNullChildByClass(C3FnParameterList.class);
  }

  @Override
  @NotNull
  public C3FuncHeader getFuncHeader() {
    return findNotNullChildByClass(C3FuncHeader.class);
  }

}
