package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.index.ModuleIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class C3ImportPathMixinImpl extends C3PsiElementImpl implements C3ImportPath
{
	public C3ImportPathMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public boolean endsWith(@NotNull C3Path path)
	{
		String pathText = path.getText();
		String stripped = pathText.endsWith("::") ? pathText.substring(0, pathText.length() - 2) : pathText;
		ModuleName moduleName = getModuleName();
		return moduleName != null && moduleName.getValue().endsWith(stripped);
	}

	@Override
	public int getTextOffset()
	{
		return getFirstChild().getTextOffset();
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		return new C3ImportPathReference(this);
	}
	@Override
	public @Nullable ModuleName getModuleName()
	{
		ASTNode[] identifiers = getNode().getChildren(TokenSet.create(C3Types.IDENT));
		if (identifiers.length == 0) return null;

		StringBuilder moduleName = new StringBuilder();
		for (ASTNode identifier : identifiers)
		{
			if (!moduleName.isEmpty()) moduleName.append("::");
			moduleName.append(identifier.getText());
		}
		return new ModuleName(moduleName.toString());
	}

	@Override
	public boolean isPublicImport()
	{
		C3Attributes attributes = getAttributes();
		if (attributes == null) return false;
		for (C3Attribute attribute : attributes.getAttributeList())
		{
			if (isPublicAttribute(attribute)) return true;
		}
		return false;
	}

	@Override
	public boolean hasValidImportAttributes()
	{
		C3Attributes attributes = getAttributes();
		if (attributes == null) return true;
		for (C3Attribute attribute : attributes.getAttributeList())
		{
			if (!isPublicAttribute(attribute)) return false;
		}
		return true;
	}

	private static boolean isPublicAttribute(@NotNull C3Attribute attribute)
	{
		return "@public".equals(attribute.getAttributeName().getText());
	}

	private static class C3ImportPathReference extends C3ReferenceBase<C3ImportPath>
	{
		C3ImportPathReference(@NotNull C3ImportPath element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			ModuleName moduleName = myElement.getModuleName();
			if (moduleName == null) return java.util.List.of();

			return StubIndex.getElements(ModuleIndex.KEY,
			                             moduleName.getValue(),
			                             myElement.getProject(),
			                             C3ProjectService.getInstance(myElement.getProject()).getSearchScope(),
			                             C3PsiElement.class)
			                .stream()
			                .filter(C3Module.class::isInstance)
			                .toList();
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			ModuleName moduleName = myElement.getModuleName();
			return TextRange.from(0, moduleName != null ? moduleName.getValue().length() : myElement.getTextLength());
		}
	}

}
