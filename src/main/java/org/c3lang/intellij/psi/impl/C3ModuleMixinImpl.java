package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.stubs.C3ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class C3ModuleMixinImpl extends C3StubBasedPsiElementBase<C3ModuleStub> implements C3Module
{
	public C3ModuleMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3ModuleMixinImpl(@NotNull C3ModuleStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3ModuleMixinImpl(@NotNull C3ModuleStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
	{
		super(stub, nodeType, node);
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		C3ModuleStub s = getGreenStub();
		return s != null ? s.getModule() : ModuleName.from(this);
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return new ItemPresentation()
		{
			@Override
			public String getPresentableText()
			{
				return String.format("%-100s %s", getText(), getContainingFile().getVirtualFile().getPath());
			}

			@Override
			public @NotNull Icon getIcon(boolean unused)
			{
				return C3Icons.Nodes.MODULE;
			}
		};
	}
}
