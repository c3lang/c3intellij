// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import org.c3lang.intellij.stubs.C3FuncDefStub;
import org.c3lang.intellij.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3FuncDefImpl extends C3StubBasedPsiElementBase<C3FuncDefStub> implements C3FuncDef {

  public C3FuncDefImpl(@NotNull C3FuncDefStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3FuncDefImpl(@NotNull ASTNode node) {
    super(node);
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
    return PsiTreeUtil.getChildOfType(this, C3Attributes.class);
  }

  @Override
  @NotNull
  public C3FnParameterList getFnParameterList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3FnParameterList.class));
  }

  @Override
  @NotNull
  public C3FuncHeader getFuncHeader() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3FuncHeader.class));
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
  @Nullable
  public TypeName getTypeName() {
    return C3ParserUtils.getTypeName(this);
  }

  @Override
  @NotNull
  public FullyQualifiedName getFunctionOrMacroName() {
    return C3ParserUtils.getFunctionOrMacroName(this);
  }

  @Override
  @NotNull
  public ReturnTypeName getReturnTypeName() {
    return C3ParserUtils.getReturnTypeName(this);
  }

  @Override
  @NotNull
  public String getParameterTypeNames() {
    return C3ParserUtils.getParameterTypeNames(this);
  }

}
