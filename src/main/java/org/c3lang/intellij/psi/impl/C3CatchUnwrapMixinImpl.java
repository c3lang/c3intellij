package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3CatchUnwrap;
import org.jetbrains.annotations.NotNull;

public abstract class C3CatchUnwrapMixinImpl extends C3UnwrapBindingMixinImpl implements C3CatchUnwrap
{
	public C3CatchUnwrapMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
