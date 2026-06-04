package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3EnumConstantStub;
import org.c3lang.intellij.stubs.C3TypeEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3EnumConstantMixinImpl extends C3StubBasedPsiElementBase<C3EnumConstantStub> implements C3EnumConstant
{
	public C3EnumConstantMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3EnumConstantMixinImpl(@NotNull C3EnumConstantStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3EnumConstantMixinImpl(@NotNull C3EnumConstantStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
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
		getNameIdentElement().replaceWithText(name);
		return this;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return getNameIdentElement();
	}

	@Override
	public int getTextOffset()
	{
		return getNameIdentElement().getTextOffset();
	}

	@Override
	public @NotNull String getNameIdent()
	{
		return getNameIdentElement().getText();
	}

	@Override
	public @NotNull LeafPsiElement getNameIdentElement()
	{
		ASTNode child = getNode().findChildByType(C3Types.CONST_IDENT);
		if (child == null) throw new IllegalStateException("CONST_IDENT not found in " + getText());
		return (LeafPsiElement) child.getPsi();
	}

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3EnumConstantStub s = getGreenStub();
		return s != null ? s.getFqName() : getParentFullyQualifiedName();
	}

	@Override
	public @NotNull String getConstIdent()
	{
		C3EnumConstantStub s = getGreenStub();
		return s != null ? s.getConstIdent() : getNameIdent();
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		C3EnumConstantStub s = getGreenStub();
		return s != null ? s.getModule() : getModuleDefinition().getModuleName();
	}

	@Override
	public @NotNull C3TypeEnum getTypeEnum()
	{
		return C3TypeEnum.ENUM;
	}

	private @NotNull FullyQualifiedName getParentFullyQualifiedName()
	{
		C3EnumDeclaration enumDeclaration = PsiTreeUtil.getParentOfType(this, C3EnumDeclaration.class);
		if (enumDeclaration == null) throw new IllegalStateException("No C3EnumDeclaration parent for " + getText());
		return enumDeclaration.getTypeName().getFqName();
	}
}
