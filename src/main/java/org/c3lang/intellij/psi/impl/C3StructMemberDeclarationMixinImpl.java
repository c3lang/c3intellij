package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.stubs.C3StructMemberDeclarationStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class C3StructMemberDeclarationMixinImpl
	extends C3StubBasedPsiElementBase<C3StructMemberDeclarationStub>
	implements C3StructMemberDeclaration
{
	public C3StructMemberDeclarationMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3StructMemberDeclarationMixinImpl(
		@NotNull C3StructMemberDeclarationStub stub,
		@NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3StructMemberDeclarationMixinImpl(
		@NotNull C3StructMemberDeclarationStub stub,
		@Nullable IElementType nodeType,
		@Nullable ASTNode node)
	{
		super(stub, nodeType, node);
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
	public @Nullable String getName()
	{
		return getNameIdent();
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		if (getStructBody() != null)
		{
			ASTNode identNode = getNode().findChildByType(C3Types.IDENT);
			return identNode != null ? (LeafPsiElement) identNode.getPsi() : null;
		}
		else
		{
			C3IdentifierList identList = getIdentifierList();
			PsiElement first = identList != null ? identList.getFirstChild() : null;
			return first instanceof LeafPsiElement ? (LeafPsiElement) first : null;
		}
	}

	@Override
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable FullyQualifiedName getDeclaredIn()
	{
		C3DeclaredInProvider provider = PsiTreeUtil.getParentOfType(this, C3DeclaredInProvider.class);
		return provider != null ? provider.getDeclaredIn() : null;
	}

	@Override
	public @Nullable String getStructPath()
	{
		C3StructMemberDeclarationStub stub = getGreenStub();
		return stub != null ? stub.getStructPath() : collectStructPath();
	}

	@Override
	public @Nullable FullyQualifiedName getStructType()
	{
		C3StructMemberDeclarationStub stub = getGreenStub();
		return stub != null ? stub.getStructType() : collectStructType();
	}

	@Override
	public @Nullable FullyQualifiedName getStructPathType()
	{
		C3StructMemberDeclarationStub stub = getGreenStub();
		return stub != null ? stub.getStructPathType() : collectStructPathType();
	}

	@Override
	public @Nullable String getDeclaredInPath()
	{
		C3DeclaredInPathProvider parentProvider =
			PsiTreeUtil.getParentOfType(this, C3DeclaredInPathProvider.class);
		C3IdentifierList identList = getIdentifierList();

		List<String> parts = new ArrayList<>();
		if (parentProvider != null && parentProvider.getDeclaredInPath() != null)
		{
			parts.add(parentProvider.getDeclaredInPath());
		}
		if (identList != null)
		{
			parts.add(identList.getText());
		}
		return String.join(".", parts);
	}

	private @Nullable String collectStructPath()
	{
		String name;
		if (getStructBody() != null)
		{
			ASTNode[] children = getNode().getChildren(TokenSet.create(C3Types.IDENT));
			name = children.length > 0 ? children[0].getText() : null;
		}
		else
		{
			C3IdentifierList identList = getIdentifierList();
			name = identList != null ? identList.getText() : null;
		}

		C3StructMemberDeclaration parentMember =
			PsiTreeUtil.getParentOfType(this, C3StructMemberDeclaration.class);
		String parentPath = parentMember != null ? parentMember.getStructPath() : null;

		List<String> parts = new ArrayList<>();
		if (parentPath != null) parts.add(parentPath);
		if (name != null) parts.add(name);
		String result = String.join(".", parts);
		return result.isEmpty() ? null : result;
	}

	private @Nullable FullyQualifiedName collectStructType()
	{
		C3StructDeclaration structDecl =
			PsiTreeUtil.getParentOfType(this, C3StructDeclaration.class);
		if (structDecl == null) return null;
		String structName = structDecl.getTypeName().getText();
		return new FullyQualifiedName(getModuleDefinition().getModuleName(), structName);
	}

	private @Nullable FullyQualifiedName collectStructPathType()
	{
		if (getStructBody() != null)
		{
			ASTNode[] children = getNode().getChildren(TokenSet.create(C3Types.IDENT));
			if (children.length == 0) return null;
			String innerStructName = children[0].getText();
			FullyQualifiedName st = getStructType();
			if (st == null) return null;
			return new FullyQualifiedName(st.getModule(), st.getName() + "." + innerStructName);
		}
		else
		{
			C3Type type = getType();
			if (type == null) return null;
			return FullyQualifiedName.from(type);
		}
	}
}
