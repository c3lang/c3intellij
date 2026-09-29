package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3StructDeclarationStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class C3StructDeclarationMixinImpl extends C3StubBasedPsiElementBase<C3StructDeclarationStub> implements C3StructDeclaration
{
	public C3StructDeclarationMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3StructDeclarationMixinImpl(@NotNull C3StructDeclarationStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3StructDeclarationMixinImpl(@NotNull C3StructDeclarationStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
	{
		super(stub, nodeType, node);
	}

	@Override
	public @Nullable String getName()
	{
		return getTypeName().getName();
	}

	@Override
	public @NotNull PsiElement setName(@NotNull String name)
	{
		getTypeName().setName(name);
		return this;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return getTypeName().getNameIdentifier();
	}

	@Override
	public @Nullable String getNameIdent()
	{
		return getTypeName().getNameIdent();
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		return getTypeName().getNameIdentElement();
	}

	@Override
	public int getTextOffset()
	{
		return getTypeName().getTextOffset();
	}

	@Override
	public @NotNull List<StructField> getFields()
	{
		C3StructDeclarationStub s = getGreenStub();
		if (s != null) return s.getFields();
		C3StructBody structBody = getStructBody();
		if (structBody == null) return Collections.emptyList();
		return StructField.collectFields(structBody, null);
	}

	@Override
	public @NotNull FullyQualifiedName getDeclaredIn()
	{
		C3TypeName typeName = getTypeName();
		return FullyQualifiedName.from(typeName, typeName.getModuleName());
	}
}
