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

public class C3TypedefDeclImpl extends C3PsiElementImpl implements C3TypedefDecl
{

	public C3TypedefDeclImpl(ASTNode node)
	{
		super(node);
	}

	public void accept(@NotNull C3Visitor visitor)
	{
		visitor.visitTypedefDecl(this);
	}

	@Override public void accept(@NotNull PsiElementVisitor visitor)
	{
		if (visitor instanceof C3Visitor)
		{
			accept((C3Visitor) visitor);
			return;
		}
		super.accept(visitor);
	}

	@Override @NotNull public List<C3Attributes> getAttributesList()
	{
		return PsiTreeUtil.getChildrenOfTypeAsList(this, C3Attributes.class);
	}

	@Override @Nullable public C3GenericDecl getGenericDecl()
	{
		return findChildByClass(C3GenericDecl.class);
	}

	@Override @Nullable public C3InterfaceImpl getInterfaceImpl()
	{
		return findChildByClass(C3InterfaceImpl.class);
	}

	@Override @NotNull public C3TypeName getTypeName()
	{
		return findNotNullChildByClass(C3TypeName.class);
	}

	@Override @Nullable public C3TypedefType getTypedefType()
	{
		return findChildByClass(C3TypedefType.class);
	}

}
