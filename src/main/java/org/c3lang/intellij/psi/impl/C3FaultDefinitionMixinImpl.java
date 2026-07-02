package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.psi.C3FaultDefinition;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.stubs.C3FaultDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3FaultDefinitionMixinImpl extends C3StubBasedPsiElementBase<C3FaultDefinitionStub> implements C3FaultDefinition
{
	public C3FaultDefinitionMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3FaultDefinitionMixinImpl(@NotNull C3FaultDefinitionStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3FaultDefinitionMixinImpl(@NotNull C3FaultDefinitionStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
	{
		super(stub, nodeType, node);
	}

	@Override
	public @Nullable String getName()
	{
		return getNameIdent();
	}

	@Override
	public @NotNull PsiElement setName(@NotNull String name)
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
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		ASTNode[] children = getNode().getChildren(TokenSet.create(C3Types.CONST_IDENT));
		return children.length > 0 ? (LeafPsiElement) children[0].getPsi() : null;
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3FaultDefinitionStub s = getGreenStub();
		return s != null ? s.getName() : FullyQualifiedName.from(this, ModuleName.from(this));
	}
}
