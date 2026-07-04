package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3FuncDefStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class C3FuncDefMixinImpl extends C3StubBasedPsiElementBase<C3FuncDefStub> implements C3FuncDef
{
	public C3FuncDefMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3FuncDefMixinImpl(@NotNull C3FuncDefStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3FuncDefMixinImpl(@NotNull C3FuncDefStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
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
		PsiElement last = getFuncHeader().getFuncName().getLastChild();
		return last instanceof LeafPsiElement ? (LeafPsiElement) last : null;
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @NotNull String getSourceFileName()
	{
		C3FuncDefStub s = getGreenStub();
		return s != null ? s.getSourceFileName() : getContainingFile().getName();
	}

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3FuncDefStub s = getGreenStub();
		return s != null ? s.getFqName() : FullyQualifiedName.from(getFuncHeader(), getModuleName());
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		C3FuncDefStub s = getGreenStub();
		return s != null ? s.getModule() : ModuleName.from(this);
	}

	@Override
	public @Nullable ShortType getType()
	{
		C3FuncDefStub s = getGreenStub();
		if (s != null) return s.getType();
		C3Type t = getFuncHeader().getFuncName().getType();
		return t != null ? ShortType.from(t) : null;
	}

	@Override
	public @Nullable ShortType getReturnType()
	{
		C3FuncDefStub s = getGreenStub();
		if (s != null) return s.getReturnType();
		C3Type t = getFuncHeader().getOptionalType().getType();
		return ShortType.from(t);
	}

	@Override
	public @NotNull List<ParamType> getParameterTypes()
	{
		C3FuncDefStub s = getGreenStub();
		if (s != null) return s.getParameterTypes();
		C3ParameterList paramList = getFnParameterList().getParameterList();
		List<C3ParamDecl> paramDecls = paramList != null ? paramList.getParamDeclList() : null;
		return ParamType.toParamTypeList(paramDecls);
	}

	@Override
	public @NotNull ItemPresentation getPresentation()
	{
		return new ItemPresentation()
		{
			@Override
			public @NotNull String getPresentableText()
			{
				return getFqName().getName();
			}

			@Override
			public @Nullable String getLocationString()
			{
				ModuleName moduleName = getModuleName();
				return moduleName != null ? moduleName.getValue() : null;
			}

			@Override
			public @Nullable Icon getIcon(boolean unused)
			{
				return C3Icons.Nodes.FUNCTION;
			}
		};
	}
}
