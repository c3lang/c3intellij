package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3TryUnwrap;
import org.jetbrains.annotations.NotNull;

public abstract class C3TryUnwrapMixinImpl extends C3UnwrapBindingMixinImpl implements C3TryUnwrap
{
	public C3TryUnwrapMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
