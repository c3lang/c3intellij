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

public class C3InterfaceDefinitionImpl extends ASTWrapperPsiElement implements C3InterfaceDefinition {

  public C3InterfaceDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull C3Visitor visitor) {
    visitor.visitInterfaceDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof C3Visitor) accept((C3Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public C3InterfaceBody getInterfaceBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3InterfaceBody.class));
  }

  @Override
  @NotNull
  public List<C3Type> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, C3Type.class);
  }

  @Override
  @NotNull
  public C3TypeName getTypeName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, C3TypeName.class));
  }

}
