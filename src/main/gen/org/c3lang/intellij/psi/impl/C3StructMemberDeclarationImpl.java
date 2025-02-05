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
import org.c3lang.intellij.stubs.C3StructMemberDeclarationStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3StructMemberDeclarationImpl extends C3StructMemberDeclarationMixinImpl implements C3StructMemberDeclaration {

  public C3StructMemberDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3StructMemberDeclarationImpl(@NotNull C3StructMemberDeclarationStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3StructMemberDeclarationImpl(@NotNull C3StructMemberDeclarationStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitStructMemberDeclaration(this);
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
  public C3BitstructBody getBitstructBody() {
    return findChildByClass(C3BitstructBody.class);
  }

  @Override
  @Nullable
  public C3IdentifierList getIdentifierList() {
    return findChildByClass(C3IdentifierList.class);
  }

  @Override
  @Nullable
  public C3StructBody getStructBody() {
    return findChildByClass(C3StructBody.class);
  }

  @Override
  @Nullable
  public C3Type getType() {
    return findChildByClass(C3Type.class);
  }

}
