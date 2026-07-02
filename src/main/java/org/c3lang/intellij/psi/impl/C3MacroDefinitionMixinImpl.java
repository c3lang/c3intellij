package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3MacroDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public abstract class C3MacroDefinitionMixinImpl extends C3StubBasedPsiElementBase<C3MacroDefinitionStub> implements C3MacroDefinition
{
	public C3MacroDefinitionMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3MacroDefinitionMixinImpl(@NotNull C3MacroDefinitionStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3MacroDefinitionMixinImpl(@NotNull C3MacroDefinitionStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
	{
		super(stub, nodeType, node);
	}

	@Override
	public @Nullable String getName()
	{
		return getNameIdent();
	}

	@Override
	public @Nullable PsiElement setName(@NotNull String name)
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
		PsiElement last = getMacroHeader().getMacroName().getLastChild();
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
		C3MacroDefinitionStub s = getGreenStub();
		return s != null ? s.getSourceFileName() : getContainingFile().getName();
	}

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3MacroDefinitionStub s = getGreenStub();
		return s != null ? s.getFqName() : FullyQualifiedName.from(getMacroHeader(), getModuleName());
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		C3MacroDefinitionStub s = getGreenStub();
		return s != null ? s.getModule() : ModuleName.from(this);
	}

	@Override
	public @Nullable ShortType getType()
	{
		C3MacroDefinitionStub s = getGreenStub();
		if (s != null) return s.getType();
		C3Type t = getMacroHeader().getMacroName().getType();
		return t != null ? ShortType.from(t) : null;
	}

	@Override
	public @Nullable ShortType getReturnType()
	{
		C3MacroDefinitionStub s = getGreenStub();
		if (s != null) return s.getReturnType();
		C3OptionalType optType = getMacroHeader().getOptionalType();
		if (optType == null) return null;
		return ShortType.from(optType.getType());
	}

	@Override
	public @NotNull List<ParamType> getParameterTypes()
	{
		C3MacroDefinitionStub s = getGreenStub();
		if (s != null) return s.getParameterTypes();
		C3ParameterList paramList = getMacroParams().getParameterList();
		List<C3ParamDecl> paramDecls = paramList != null ? paramList.getParamDeclList() : null;
		return ParamType.toParamTypeList(paramDecls);
	}
}
