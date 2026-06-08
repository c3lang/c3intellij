package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.c3lang.intellij.index.ModuleIndex;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class C3ImportPathMixinImpl extends C3PsiElementImpl implements C3ImportPath
{
	public C3ImportPathMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public boolean endsWith(@NotNull C3Path path)
	{
		String pathText = path.getText();
		String stripped = pathText.endsWith("::") ? pathText.substring(0, pathText.length() - 2) : pathText;
		return getText().endsWith(stripped);
	}

	@Override
	public int getTextOffset()
	{
		return getFirstChild().getTextOffset();
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		return new C3ImportPathReference(this);
	}
	@Override
	public @Nullable ModuleName getModuleName()
	{
		return new ModuleName(getText());
	}

	private static class C3ImportPathReference extends C3ReferenceBase<C3ImportPath>
	{
		C3ImportPathReference(@NotNull C3ImportPath element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			return StubIndex.getElements(ModuleIndex.KEY,
			                             myElement.getText(),
			                             myElement.getProject(),
			                             GlobalSearchScope.allScope(myElement.getProject()),
			                             C3PsiElement.class)
			                .stream()
			                .filter(C3Module.class::isInstance)
			                .toList();
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			return TextRange.from(0, myElement.getTextLength());
		}
	}

}
