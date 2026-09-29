package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3InterfaceImpl;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.reference.C3InterfaceReference;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.c3lang.intellij.stubs.C3TypeEnum;
import org.c3lang.intellij.stubs.C3TypeNameStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class C3TypeNameMixinImpl extends C3StubBasedPsiElementBase<C3TypeNameStub> implements C3TypeName
{
	public C3TypeNameMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public C3TypeNameMixinImpl(@NotNull C3TypeNameStub stub, @NotNull IStubElementType<?, ?> nodeType)
	{
		super(stub, nodeType);
	}

	public C3TypeNameMixinImpl(@NotNull C3TypeNameStub stub, @Nullable IElementType nodeType, @Nullable ASTNode node)
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
		PsiElement last = getLastChild();
		if (last != null && last.getNode().getElementType() == C3Types.TYPE_IDENT)
			return (LeafPsiElement) last;
		return null;
	}

	@Override
	public @Nullable PsiReference getReference()
	{
		// Interface names in struct contracts (struct Baz (MyName)) navigate
		// to the interface definition; other type names resolve to their
		// declaration (struct, interface, enum, typedef, alias...).
		if (getParent() instanceof C3InterfaceImpl)
		{
			return new C3InterfaceReference((C3TypeName) this);
		}
		return new C3TypeNameReference((C3TypeName) this);
	}

	private static class C3TypeNameReference extends C3ReferenceBase<C3TypeName>
	{
		C3TypeNameReference(@NotNull C3TypeName element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			String text = myElement.getText();
			if (text == null || text.isBlank()) return Collections.emptyList();
			List<C3PsiElement> result = new ArrayList<>();
			FullyQualifiedName wanted = C3InterfaceReference.interfaceNameOf(myElement);
			C3ModuleDefinition moduleDefinition = myElement.getModuleDefinition();
			for (C3TypeName declaration :
				org.c3lang.intellij.index.InterfaceService.INSTANCE.findModuleTypeDeclarations(
					wanted.getModule() != null ? wanted.getModule().getValue() : "", myElement.getProject()))
			{
				if (wanted.getName().equals(declaration.getNameIdent()) && !result.contains(declaration))
				{
					result.add(declaration);
				}
			}
			if (!result.isEmpty()) return result;
			for (C3FullyQualifiedNamePsiElement element :
				NameIndexService.INSTANCE.findByNameEndsWith(wanted.getName(), myElement.getProject()))
			{
				if (!(element instanceof C3TypeName typeName)) continue;
				if (!wanted.getName().equals(typeName.getNameIdent())) continue;
				PsiElement parent = typeName.getParent();
				if (!(parent instanceof C3StructDeclaration
					|| parent instanceof C3EnumDeclaration
					|| parent instanceof C3InterfaceDefinition
					|| parent instanceof C3TypedefDecl
					|| parent instanceof C3BitstructDeclaration
					|| parent instanceof C3AliasTypeDecl)) continue;
				if (moduleDefinition != null && !moduleDefinition.containsImportOrSameModule(element)) continue;
				if (!result.contains(element)) result.add(element);
			}
			return result;
		}
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		C3TypeNameStub s = getGreenStub();
		return s != null ? s.getModuleName() : ModuleName.from(this);
	}

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3TypeNameStub s = getGreenStub();
		return s != null ? s.getFqName() : FullyQualifiedName.from(this, getModuleName());
	}

	@Override
	public @NotNull C3TypeEnum getTypeEnum()
	{
		C3TypeNameStub s = getGreenStub();
		return s != null ? s.getTypeEnum() : C3TypeEnum.find(this);
	}
}
