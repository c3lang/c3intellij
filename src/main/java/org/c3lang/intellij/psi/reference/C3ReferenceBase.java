package org.c3lang.intellij.psi.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3NameIdentProvider;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class C3ReferenceBase<T extends C3PsiElement> extends PsiPolyVariantReferenceBase<T>
	implements C3Reference
{
	public C3ReferenceBase(@NotNull T element)
	{
		super(element);
	}

	@Override
	public @Nullable C3PsiElement resolve()
	{
		PsiElement resolved = super.resolve();
		if (resolved == null)
		{
			ResolveResult[] results = multiResolve(false);
			if (results.length > 0)
			{
				resolved = results[0].getElement();
			}
		}
		return resolved instanceof C3PsiElement ? (C3PsiElement) resolved : null;
	}

	@Override
	public ResolveResult @NotNull [] multiResolve(boolean incompleteCode)
	{
		Collection<C3PsiElement> results = multiResolve();
		ResolveResult[] array = new ResolveResult[results.size()];
		int i = 0;
		for (C3PsiElement element : results)
		{
			array[i++] = new PsiElementResolveResult(element);
		}
		return array;
	}

	@Override
	public boolean isReferenceTo(@NotNull PsiElement element)
	{
		if (getElement().getManager().areElementsEquivalent(resolve(), element)) return true;
		for (ResolveResult result : multiResolve(false))
		{
			PsiElement target = result.getElement();
			if (target == null) continue;
			if (getElement().getManager().areElementsEquivalent(target, element)) return true;
			if (target instanceof C3FuncDef funcDef)
			{
				if (funcDef.getFuncHeader().getFuncName() == element) return true;
				if (funcDef.getNameIdentifier() == element) return true;
			}
			if (element instanceof C3FuncDef funcDef)
			{
				if (funcDef.getFuncHeader().getFuncName() == target) return true;
				if (funcDef.getNameIdentifier() == target) return true;
			}
			if (target instanceof C3TypeName typeName)
			{
				if (typeName.getParent() == element) return true;
				if (typeName.getNameIdentifier() == element) return true;
			}
			if (element instanceof C3TypeName typeName)
			{
				if (typeName.getParent() == target) return true;
				if (typeName.getNameIdentifier() == target) return true;
			}
		}
		return false;
	}

	@Override
	public @NotNull PsiElement handleElementRename(@NotNull String newName)
	{
		doRename(myElement, newName);
		return myElement;
	}

	@Override
	public LookupElement @NotNull [] getVariants()
	{
		return LookupElement.EMPTY_ARRAY;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof C3ReferenceBase && myElement.equals(((C3ReferenceBase<?>) other).myElement);
	}

	@Override
	public int hashCode()
	{
		return myElement.hashCode();
	}

	public static void doRename(@NotNull PsiElement element, @NotNull String newName)
	{
		if (element instanceof LeafPsiElement)
		{
			((LeafPsiElement) element).replaceWithText(newName);
		}
		else if (element instanceof C3NameIdentProvider)
		{
			LeafPsiElement ident = ((C3NameIdentProvider) element).getNameIdentElement();
			if (ident != null) ident.replaceWithText(newName);
		}
		else
		{
			throw new UnsupportedOperationException("rename for " + element + " not implemented yet");
		}
	}
}
