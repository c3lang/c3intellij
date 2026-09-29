package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.ModuleIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class C3PathMixinImpl extends C3PsiNamedElementImpl implements C3Path
{
	public C3PathMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return this;
	}

	@Override
	public @Nullable String getName()
	{
		return getText();
	}

	@Override
	public @NotNull PsiElement setName(@NotNull String name)
	{
		// TODO
		return this;
	}

	@Override
	public int getTextOffset()
	{
		return getNode().getStartOffset();
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		return new C3PathReference(this);
	}

	@Override
	public void shorten()
	{
		List<ASTNode> idents = new ArrayList<>(Arrays.asList(
			getNode().getChildren(TokenSet.create(C3Types.IDENT, C3Types.SCOPE))));

		if (idents.size() <= 2)
		{
			// bar::
			return;
		}

		idents.remove(idents.size() - 1); // ::
		idents.remove(idents.size() - 1); // IDENT

		// remove std::
		deleteChildRange(
			idents.get(0).getPsi(),
			idents.get(idents.size() - 1).getPsi()
		);
	}

	private static class C3PathReference extends C3ReferenceBase<C3Path>
	{
		C3PathReference(@NotNull C3Path element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			String text = myElement.getText();
			String moduleName = text.endsWith("::") ? text.substring(0, text.length() - 2) : text;
			if (moduleName.isEmpty()) return Collections.emptyList();
			if (com.intellij.openapi.project.DumbService.isDumb(myElement.getProject())) return Collections.emptyList();

			Collection<C3PsiElement> elements;
			try
			{
				elements = StubIndex.getElements(
					ModuleIndex.KEY,
					moduleName,
					myElement.getProject(),
					C3ProjectService.getInstance(myElement.getProject()).getSearchScope(),
					C3PsiElement.class)
					.stream()
					.filter(C3Module.class::isInstance)
					.toList();
			}
			catch (Exception ignored)
			{
				elements = List.of();
			}

			if (!elements.isEmpty()) return elements;

			C3Module direct = C3ImportPathMixinImpl.findModuleDirectly(moduleName, myElement.getProject());
			if (direct != null) return List.of(direct);

			C3ModuleDefinition moduleDef = PsiTreeUtil.getParentOfType(myElement, C3ModuleDefinition.class);
			if (moduleDef != null)
			{
				for (ModuleName imported : moduleDef.getImports())
				{
					if (imported.getValue().endsWith("::" + moduleName) || imported.getValue().equals(moduleName))
					{
						direct = C3ImportPathMixinImpl.findModuleDirectly(imported.getValue(), myElement.getProject());
						if (direct != null) return List.of(direct);
					}
				}
			}

			return Collections.emptyList();
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			String text = myElement.getText();
			int len = text.endsWith("::") ? text.length() - 2 : text.length();
			return new TextRange(0, Math.max(0, len));
		}
	}
}
