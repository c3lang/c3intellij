package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class C3AccessIdentMixinImpl extends C3PsiNamedElementImpl implements C3AccessIdent
{
	public C3AccessIdentMixinImpl(@NotNull ASTNode node)
	{
		super(node);
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
		PsiElement first = getFirstChild();
		return first instanceof LeafPsiElement ? (LeafPsiElement) first : null;
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @NotNull TextRange getTextRange()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextRange() : super.getTextRange();
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		return new StructMemberReference(this);
	}

	@Override
	public @Nullable FullyQualifiedName findTypeName()
	{
		C3PsiElement resolved = new StructMemberReference(this).resolve();
		if (!(resolved instanceof C3FullyQualifiedTypeNameProvider)) return null;
		return ((C3FullyQualifiedTypeNameProvider) resolved).findTypeName();
	}

	private static class StructMemberReference extends C3ReferenceBase<C3AccessIdent>
	{
		StructMemberReference(@NotNull C3AccessIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			C3CallExpr call = findAccessCallExpr();
			if (call == null) return Collections.emptyList();

			AccessIdentSequence seq = getAccessIdentSequence(call);
			if (seq == null)
			{
				return isInvocationCallee()
					? findMethodsMatchingAccessName()
					: findFieldsOrMethodsMatchingAccessName();
			}

			String query = seq.rootType.getFullName();
			List<C3StructMemberDeclaration> structMembers = Collections.emptyList();

			for (String ident : seq.idents)
			{
				structMembers = StructService.INSTANCE.getStructMembers(query + "." + ident, myElement.getProject());
				C3StructMemberDeclaration member = structMembers.size() == 1 ? structMembers.get(0) : null;
				FullyQualifiedName nextType = member != null ? member.getStructPathType() : null;
				if (nextType == null) return Collections.emptyList();
				query = nextType.getFullName();
			}

			return !structMembers.isEmpty() || isInvocationCallee()
				? new ArrayList<>(structMembers)
				: findFieldsOrMethodsMatchingAccessName();
		}

		private @NotNull Collection<C3PsiElement> findMethodsMatchingAccessName()
		{
			String name = myElement.getNameIdent();
			if (name == null) return Collections.emptyList();

			return new ArrayList<>(NameIndexService.INSTANCE.findMethodsByName(name, myElement.getProject()));
		}

		private @NotNull Collection<C3PsiElement> findFieldsOrMethodsMatchingAccessName()
		{
			String name = myElement.getNameIdent();
			if (name == null) return Collections.emptyList();

			List<C3StructMemberDeclaration> fields =
				StructService.INSTANCE.findStructMembersByName(name, myElement.getProject());
			if (!fields.isEmpty()) return new ArrayList<>(fields);

			return findMethodsMatchingAccessName();
		}

		private boolean isInvocationCallee()
		{
			C3CallExpr accessExpr = findAccessCallExpr();
			if (accessExpr == null) return false;

			PsiElement parent = accessExpr.getParent();
			if (!(parent instanceof C3CallExpr invocationExpr)) return false;

			return invocationExpr.getExpr() == accessExpr
				&& invocationExpr.getCallExprTail().getCallInvocation() != null;
		}

		private @Nullable C3CallExpr findAccessCallExpr()
		{
			PsiElement current = myElement.getParent();
			while (current != null)
			{
				if (current instanceof C3CallExpr callExpr
					&& callExpr.getCallExprTail().getAccessIdent() == myElement)
				{
					return callExpr;
				}
				current = current.getParent();
			}
			return null;
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			return super.getRangeInElement();
		}

		private static @Nullable AccessIdentSequence getAccessIdentSequence(@NotNull C3CallExpr callExpr)
		{
			List<C3PsiElement> accessSequence = new ArrayList<>();
			C3PsiElement current = callExpr;
			while (true)
			{
				accessSequence.add(current);
				if (current instanceof C3ExprStmt)
				{
					current = ((C3ExprStmt)current).getExpr();
					continue;
				}
				if (current instanceof C3CallExpr)
				{
					current = ((C3CallExpr) current).getExpr();
					continue;
				}
				break;
			}

			C3PsiElement last = accessSequence.removeLast();
			if (!(last instanceof C3PathIdentExpr)) return null;

			C3PathIdentExpr rootExpr = (C3PathIdentExpr) last;
			FullyQualifiedName rootType = rootExpr.getPathIdent().findTypeName();
			if (rootType == null) return null;

			List<String> idents = new ArrayList<>();
			for (C3PsiElement elem : accessSequence)
			{
				if (elem instanceof C3CallExpr)
				{
					String text = elem.getText();
					String[] parts = text.split("\\.");
					idents.add(parts[parts.length - 1]);
				}
			}
			Collections.reverse(idents);

			return new AccessIdentSequence(rootType, idents);
		}
	}

	private static final class AccessIdentSequence
	{
		final FullyQualifiedName rootType;
		final List<String> idents;

		AccessIdentSequence(@NotNull FullyQualifiedName rootType, @NotNull List<String> idents)
		{
			this.rootType = rootType;
			this.idents = idents;
		}
	}
}
