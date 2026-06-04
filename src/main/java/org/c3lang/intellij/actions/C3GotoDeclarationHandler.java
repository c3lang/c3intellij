package org.c3lang.intellij.actions;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.c3lang.intellij.psi.C3Arg;
import org.jetbrains.annotations.Nullable;

public final class C3GotoDeclarationHandler implements GotoDeclarationHandler
{
	@Override
	public PsiElement @Nullable [] getGotoDeclarationTargets(
		@Nullable PsiElement sourceElement,
		int offset,
		@Nullable Editor editor)
	{
		if (sourceElement instanceof C3Arg)
		{
			ReferencesSearch.search(sourceElement).findAll();
		}

		return null;
	}
}
