package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3DeclOrExpr;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3LocalDeclAfterTypeMixinImpl extends C3PsiNamedElementImpl implements C3LocalDeclAfterType
{
	public C3LocalDeclAfterTypeMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @Nullable String getName()
	{
		return getNameIdent();
	}

	@Override
	public @Nullable PsiElement setName(@NotNull String name)
	{
		LeafPsiElement ident = getNameIdentElement();
		if (ident != null) ident.replaceWithText(name);
		return this;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return getNameIdentElement();
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
		PsiElement first = getFirstChild();
		return first instanceof LeafPsiElement ? (LeafPsiElement) first : null;
	}

	@Override
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable FullyQualifiedName findTypeName()
	{
		C3LocalDeclarationStmt parent = PsiTreeUtil.getParentOfType(this, C3LocalDeclarationStmt.class);
		if (parent != null) return parent.findTypeName();
		// Declaration inside a condition (`while (T x = ..., x)`,
		// `if (T x = ...)`): the type sits in the sibling optional_type of
		// the enclosing decl_or_expr.
		C3DeclOrExpr condDecl = PsiTreeUtil.getParentOfType(this, C3DeclOrExpr.class);
		if (condDecl != null && condDecl.getOptionalType() != null)
		{
			try
			{
				return FullyQualifiedName.from(condDecl.getOptionalType());
			}
			catch (Exception ignored)
			{
			}
		}
		return null;
	}
}
