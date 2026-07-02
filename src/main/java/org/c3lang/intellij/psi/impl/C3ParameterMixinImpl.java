package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3ParameterMixinImpl extends C3PsiNamedElementImpl implements C3Parameter
{
	public C3ParameterMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return getNameIdentElement();
	}

	@Override
	public @Nullable String getName()
	{
		PsiElement ident = getNameIdentifier();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable PsiElement setName(@NotNull String name)
	{
		LeafPsiElement ident = getNameIdentElement();
		if (ident != null) ident.replaceWithText(name);
		return this;
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		PsiElement last = getLastChild();
		return last instanceof LeafPsiElement ? (LeafPsiElement) last : null;
	}

	@Override
	public @Nullable FullyQualifiedName findTypeName()
	{
		C3Type type = getType();
		return type != null ? FullyQualifiedName.from(type) : null;
	}
}
