// This is a generated file. Not intended for manual editing.
package org.c3lang.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.c3lang.intellij.psi.C3Types.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import org.c3lang.intellij.psi.C3ModuleSectionStub;
import org.c3lang.intellij.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class C3ModuleSectionImpl extends StubBasedPsiElementBase<C3ModuleSectionStub> implements C3ModuleSection {

  public C3ModuleSectionImpl(@NotNull C3ModuleSectionStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public C3ModuleSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public C3ModuleSectionImpl(C3ModuleSectionStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitModuleSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3Module getModule() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3Module.class));
  }

  @Override
  @NotNull
  public List<C3TopLevel> getTopLevelList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, C3TopLevel.class);
  }

}
