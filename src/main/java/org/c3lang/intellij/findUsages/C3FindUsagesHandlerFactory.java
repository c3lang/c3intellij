package org.c3lang.intellij.findUsages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO
public final class C3FindUsagesHandlerFactory extends FindUsagesHandlerFactory
{
	@Override
	public boolean canFindUsages(@NotNull PsiElement element)
	{
		throw new NotImplementedError("An operation is not implemented: Not yet implemented");
	}

	@Override
	public @Nullable FindUsagesHandler createFindUsagesHandler(
		@NotNull PsiElement element,
		boolean forHighlightUsages)
	{
		throw new NotImplementedError("An operation is not implemented: Not yet implemented");
	}
}
