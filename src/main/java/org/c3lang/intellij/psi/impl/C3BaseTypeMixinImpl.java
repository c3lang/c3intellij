package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class C3BaseTypeMixinImpl extends C3PsiNamedElementImpl implements C3BaseType
{
	public C3BaseTypeMixinImpl(@NotNull ASTNode node)
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
		return ident != null ? ident.getTextOffset() : getNode().getStartOffset();
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
		if (last != null && last.getNode().getElementType() == C3Types.TYPE_IDENT)
		{
			return (LeafPsiElement) last;
		}
		return null;
	}

	@Override
	public @Nullable PsiReference getReference()
	{
		return new C3TypeNameReference(this);
	}

	private static class C3TypeNameReference extends C3ReferenceBase<C3BaseType>
	{
		C3TypeNameReference(@NotNull C3BaseType element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			C3ModuleDefinition importProvider = myElement.getModuleDefinition();
			List<C3PsiElement> result = new ArrayList<>();
			for (C3FullyQualifiedNamePsiElement el :
				NameIndexService.INSTANCE.findType(myElement, myElement.getProject()))
			{
				if (el instanceof C3TypeName && importProvider.containsImportOrSameModule(el))
				{
					result.add(el);
				}
			}
			return result;
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			return super.getRangeInElement();
		}
	}
}
