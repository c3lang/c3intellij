package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3ParamPathElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class C3ParamPathElementMixinImpl extends C3PsiElementImpl implements C3ParamPathElement
{
	public C3ParamPathElementMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @NotNull List<String> findPathName(boolean includeSelf)
	{
		com.intellij.psi.PsiElement prevSib = getPrevSibling();
		C3ParamPathElement prev = prevSib instanceof C3ParamPathElement ? (C3ParamPathElement) prevSib : null;

		List<String> prevPaths = prev != null ? prev.findPathName(true) : Collections.emptyList();

		if (includeSelf)
		{
			String t = getText();
			String stripped = t.startsWith(".") ? t.substring(1) : t;
			List<String> result = new ArrayList<>(prevPaths);
			result.add(stripped);
			return result;
		}
		else
		{
			return prevPaths;
		}
	}
}
