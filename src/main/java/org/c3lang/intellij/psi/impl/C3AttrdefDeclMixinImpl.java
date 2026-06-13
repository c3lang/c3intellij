package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3AttrdefDeclStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3AttrdefDeclMixinImpl extends C3StubBasedPsiElementBase<C3AttrdefDeclStub>
	implements C3AttrdefDecl
{

	public C3AttrdefDeclMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3AttrdefDeclMixinImpl(@NotNull C3AttrdefDeclStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3AttrdefDeclMixinImpl(@NotNull C3AttrdefDeclStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
	{
		super(stub, nodeType, node);
	}

	@Override public @Nullable PsiElement getNameIdentifier()
	{
		return getNameIdentElement();
	}

	@Override public @NotNull PsiElement setName(@NotNull String name)
	{
		LeafPsiElement ident = getNameIdentElement();
		if (ident != null) ident.replaceWithText(name);
		return this;
	}

	@Override public @Nullable String getName()
	{
		C3AttrdefDeclStub s = getGreenStub();
		if (s != null) return s.getName();

		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override public @NotNull FullyQualifiedName getFqName()
	{
		C3AttrdefDeclStub s = getGreenStub();
		return s != null
				? s.getFqName()
				: new FullyQualifiedName(ModuleName.from(this), getAttributeUserName().getText());
	}

	@Override public @Nullable ModuleName getModuleName()
	{
		C3AttrdefDeclStub s = getGreenStub();
		return s != null ? s.getModuleName() : ModuleName.from(this);
	}

	private @Nullable LeafPsiElement getNameIdentElement()
	{
		PsiElement child = getAttributeUserName().getFirstChild();
		return child instanceof LeafPsiElement leaf ? leaf : null;
	}
}
