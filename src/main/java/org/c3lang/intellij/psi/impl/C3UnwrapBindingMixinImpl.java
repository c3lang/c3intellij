package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.c3lang.intellij.psi.C3NameIdentProvider;
import org.c3lang.intellij.psi.C3PsiNamedElement;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared logic for `catch`/`try` unwrap bindings (`err` in
 * `catch err = x`): the name is the `IDENT` directly before `=`, which the
 * grammar leaves as a bare token. See {@code C3ForeachVarMixinImpl} for the
 * sibling pattern on iteration variables.
 */
public abstract class C3UnwrapBindingMixinImpl extends C3PsiNamedElementImpl
	implements C3PsiNamedElement, C3NameIdentProvider
{
	public C3UnwrapBindingMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public @Nullable String getBindingName()
	{
		LeafPsiElement ident = getBindingIdentElement();
		return ident != null ? ident.getText() : null;
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
		PsiElement ident = getNameIdentifier();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		return getBindingIdentElement();
	}

	@Override
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	private @Nullable LeafPsiElement getBindingIdentElement()
	{
		ASTNode eq = null;
		for (ASTNode child : getNode().getChildren(null))
		{
			if (child.getElementType() == C3Types.EQ)
			{
				eq = child;
				break;
			}
		}
		if (eq == null) return null;
		ASTNode current = eq.getTreePrev();
		while (current != null
			&& (current.getPsi() instanceof com.intellij.psi.PsiWhiteSpace
				|| current.getPsi() instanceof com.intellij.psi.PsiComment))
		{
			current = current.getTreePrev();
		}
		if (current != null && current.getElementType() == C3Types.IDENT
			&& current.getPsi() instanceof LeafPsiElement leaf)
		{
			return leaf;
		}
		return null;
	}
}
