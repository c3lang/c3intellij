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

import java.util.Collection;

public abstract class C3PathConstMixinImpl extends C3PsiNamedElementImpl implements C3PathConst
{
	public C3PathConstMixinImpl(@NotNull ASTNode node)
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
		PsiElement first = getFirstChild();
		if (first != null && first.getNode().getElementType() == C3Types.CONST_IDENT)
		{
			return (LeafPsiElement) first;
		}
		return null;
	}

	@Override
	public @Nullable PsiReference getReference()
	{
		// One reference with an explicit priority: a bare `INFO` resolves
		// to the enum member even when a same-named const exists (verified
		// against c3c, where `INFO + 1` infers as the enum's backing `int`,
		// not the const's `char`). A plain `PsiMultiReference` cannot
		// express this: its `resolve()` does not take the first hit.
		return new C3PathConstReference(this);
	}

	private static class C3PathConstReference extends C3ReferenceBase<C3PathConst>
	{
		C3PathConstReference(@NotNull C3PathConst element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			C3ModuleDefinition moduleDefinition = myElement.getModuleDefinition();
			java.util.List<C3PsiElement> result = new java.util.ArrayList<>();
			if (moduleDefinition == null) return result;
			java.util.List<C3PsiElement> consts = new java.util.ArrayList<>();
			java.util.List<C3PsiElement> faults = new java.util.ArrayList<>();
			for (C3FullyQualifiedNamePsiElement el :
				NameIndexService.INSTANCE.findByNameEndsWith(myElement.getText(), myElement.getProject()))
			{
				if (!moduleDefinition.containsImportOrSameModule(el)) continue;
				if (el instanceof C3EnumConstant) result.add(el);
				else if (el instanceof C3ConstDeclarationStmt) consts.add(el);
				else if (el instanceof C3FaultDefinition) faults.add(el);
			}
			if (!result.isEmpty()) return result;
			if (!consts.isEmpty()) return consts;
			return faults;
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			C3Path path = myElement.getPath();
			return TextRange.create(path != null ? path.getTextLength() : 0, myElement.getTextLength());
		}
	}
}
