// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.stubs.C3TypeNameStub;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3TypeEnum;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3TypeNameImpl extends C3StubBasedPsiElementBase<C3TypeNameStub> implements C3TypeName {

  public C3TypeNameImpl(@NotNull C3TypeNameStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3TypeNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3TypeNameImpl(@NotNull C3TypeNameStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitTypeName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public String getSourceFileName() {
    return C3ParserUtils.getSourceFileName(this);
  }

  @Override
  @Nullable
  public ModuleName getModuleName() {
    return C3ParserUtils.getModuleName(this);
  }

  @Override
  @NotNull
  public TypeName getTypeName() {
    return C3ParserUtils.getTypeName(this);
  }

  @Override
  @NotNull
  public C3TypeEnum getTypeEnum() {
    return C3ParserUtils.getTypeEnum(this);
  }

}
