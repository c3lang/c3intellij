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
import org.c3lang.intellij.stubs.C3MacroDefinitionStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3MacroDefinitionImpl extends C3MacroDefinitionMixinImpl implements C3MacroDefinition {

  public C3MacroDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3MacroDefinitionImpl(@NotNull C3MacroDefinitionStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3MacroDefinitionImpl(@NotNull C3MacroDefinitionStub stub, @Nullable IElementType type, @Nullable ASTNode node) {
    super(stub, type, node);
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
    return findChildByClass(C3Attributes.class);
  }

  @Override
  @NotNull
  public C3MacroFuncBody getMacroFuncBody() {
    return findNotNullChildByClass(C3MacroFuncBody.class);
  }

  @Override
  @NotNull
  public C3MacroHeader getMacroHeader() {
    return findNotNullChildByClass(C3MacroHeader.class);
  }

  @Override
  @NotNull
  public C3MacroParams getMacroParams() {
    return findNotNullChildByClass(C3MacroParams.class);
  }

}
