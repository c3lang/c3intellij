package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.c3lang.intellij.types.BitstructSupport;
import org.c3lang.intellij.types.TypeCanonicalizer;
import org.c3lang.intellij.types.TypeChecker;
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

			String query = dereference(seq.rootType).getFullName();
			FullyQualifiedName currentType = dereference(seq.rootType);
			List<C3StructMemberDeclaration> structMembers = Collections.emptyList();

			for (int i = 0; i < seq.idents.size(); i++)
			{
				String ident = seq.idents.get(i);
				boolean last = i == seq.idents.size() - 1;
				if (BitstructSupport.isBitstruct(currentType.getFullName(), myElement.getProject(), ModuleName.from(myElement)))
				{
					// Bitstruct fields live outside the struct-member index:
					// an unknown name is simply unknown, never a foreign
					// struct's same-named field.
					C3PsiElement bitField = BitstructSupport.findBitstructField(
						currentType.getFullName(), ident, myElement.getProject(), ModuleName.from(myElement));
					return bitField != null && last ? List.of(bitField) : Collections.emptyList();
				}
				structMembers = StructService.INSTANCE.getStructMembers(query + "." + ident, myElement.getProject());
				C3StructMemberDeclaration member = structMembers.size() == 1 ? structMembers.get(0) : null;
				if (member != null)
				{
					FullyQualifiedName nextType = member.getStructPathType();
					if (nextType != null)
					{
						// `.` auto-dereferences pointers (`ptr.field` reads
						// through `Foo*`), so member chains stay star-free.
						currentType = dereference(nextType);
						query = currentType.getFullName();
						continue;
					}
					if (last && !isInvocationCallee())
					{
						// Leaf member (e.g. an int field): it is the answer.
						return new ArrayList<>(structMembers);
					}
				}
				if (last)
				{
					return lastIdentResults(currentType, ident, structMembers);
				}
				// An intermediate segment does not resolve: deeper segments cannot either.
				return Collections.emptyList();
			}

			return !structMembers.isEmpty()
				? new ArrayList<>(structMembers)
				: Collections.emptyList();
		}

		private @NotNull Collection<C3PsiElement> lastIdentResults(
				@NotNull FullyQualifiedName currentType,
				@NotNull String ident,
				@NotNull List<C3StructMemberDeclaration> structMembers)
		{
			if (isInvocationCallee())
			{
				Collection<C3PsiElement> methods = findMethodsForCurrentType(currentType, ident);
				if (!methods.isEmpty()) return methods;
				// A field holding a function pointer invoked as `s.cb()`.
				if (!structMembers.isEmpty()) return new ArrayList<>(structMembers);
				return Collections.emptyList();
			}
			if (!structMembers.isEmpty()) return new ArrayList<>(structMembers);
			// A method referenced as a value, e.g. `&s.method`.
			return findMethodsForCurrentType(currentType, ident);
		}

		private @NotNull Collection<C3PsiElement> findMethodsForCurrentType(
				@NotNull FullyQualifiedName currentType,
				@NotNull String ident)
		{
			// Cross-file PSI scans are intentionally NOT done here: forcing AST/stub
			// reconciliation of unrelated files from a resolver causes
			// UpToDateStubIndexMismatch. The stub index + module-file fallback below
			// cover the same cases safely.
			Collection<C3CallablePsiElement> methods =
				NameIndexService.INSTANCE.findMethodsForType(currentType, ident, myElement.getProject());
			// An interface receiver dispatches to its own methods first: they
			// are the contract the call is written against. Concrete
			// implementations (possibly with extra parameters) follow.
			List<C3CallablePsiElement> preferred = new ArrayList<>();
			List<C3CallablePsiElement> rest = new ArrayList<>();
			boolean receiverIsInterface = InterfaceService.isInterfaceType(currentType, myElement.getProject());
			for (C3CallablePsiElement method : methods)
			{
				if (receiverIsInterface && InterfaceService.getDeclaringInterface(method) != null)
				{
					preferred.add(method);
				}
				else
				{
					rest.add(method);
				}
			}
			if (!preferred.isEmpty() || !rest.isEmpty())
			{
				List<C3PsiElement> ordered = new ArrayList<>(preferred);
				ordered.addAll(rest);
				return ordered;
			}

			if (currentType.getModule() != null)
			{
				C3Module mod = C3ImportPathMixinImpl.findModuleDirectly(currentType.getModule().getValue(), myElement.getProject());
				if (mod != null && mod.getContainingFile() != null
					&& mod.getContainingFile().equals(myElement.getContainingFile()))
				{
					for (C3FuncDef funcDef : PsiTreeUtil.findChildrenOfType(mod.getContainingFile(), C3FuncDef.class))
					{
						if (funcDef.getType() != null && currentType.getSuffixName().equals(funcDef.getType().getValue()))
						{
							if (funcDef.getFqName().getName().endsWith("." + ident))
							{
								return List.of(funcDef);
							}
						}
					}
				}
			}
			return Collections.emptyList();
		}

		private @NotNull Collection<C3PsiElement> findMethodsMatchingAccessName()
		{
			String name = myElement.getNameIdent();
			if (name == null) return Collections.emptyList();

			List<C3PsiElement> result = new ArrayList<>();
			// Same-file scan is safe; cross-file lookup goes through the index below.
			for (C3FuncDef funcDef : PsiTreeUtil.findChildrenOfType(myElement.getContainingFile(), C3FuncDef.class))
			{
				if (funcDef.getFqName().getName().endsWith("." + name) && !result.contains(funcDef))
				{
					result.add(funcDef);
				}
			}

			C3ModuleDefinition moduleDefinition =
				PsiTreeUtil.getParentOfType(myElement, C3ModuleDefinition.class);

			for (C3CallablePsiElement method : NameIndexService.INSTANCE.findMethodsByName(name, myElement.getProject()))
			{
				if (moduleDefinition == null || moduleDefinition.containsImportOrSameModule(method))
				{
					if (!result.contains(method))
					{
						result.add(method);
					}
				}
			}

			return result;
		}

		private @NotNull Collection<C3PsiElement> findFieldsOrMethodsMatchingAccessName()
		{
			String name = myElement.getNameIdent();
			if (name == null) return Collections.emptyList();

			List<C3StructMemberDeclaration> fields =
				StructService.INSTANCE.findStructMembersByName(name, myElement.getProject());
			if (!fields.isEmpty()) return new ArrayList<>(visibleFields(fields));

			return findMethodsMatchingAccessName();
		}

		/**
		 * The by-name fallback fires when the receiver type is unknown, so a
		 * bare name could match an unrelated module (e.g. {@code alpha} in
		 * {@code qoi::OpRGBA} for a use in {@code ascii}). Keep only fields
		 * from visible modules; without a module context keep everything
		 * (previous behavior).
		 */
		private @NotNull List<C3StructMemberDeclaration> visibleFields(
				@NotNull List<C3StructMemberDeclaration> fields)
		{
			C3ModuleDefinition moduleDefinition =
				PsiTreeUtil.getParentOfType(myElement, C3ModuleDefinition.class);
			if (moduleDefinition == null) return fields;
			List<C3StructMemberDeclaration> visible = new ArrayList<>();
			for (C3StructMemberDeclaration field : fields)
			{
				ModuleName fieldModule = null;
				try
				{
					FullyQualifiedName structType = field.getStructType();
					fieldModule = structType != null ? structType.getModule() : null;
				}
				catch (Exception ignored)
				{
					// Stale index entry: keep the field rather than drop it.
					visible.add(field);
					continue;
				}
				if (moduleDefinition.containsImportOrSameModule(fieldModule)) visible.add(field);
			}
			return visible;
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
			if (!(last instanceof C3PathIdentExpr) && !(last instanceof C3PathConstExpr)) return null;

			C3PathIdentExpr rootExpr = last instanceof C3PathIdentExpr pathIdentExpr ? pathIdentExpr : null;
			FullyQualifiedName rootType = rootExpr != null
				? rootExpr.getPathIdent().findTypeName()
				: TypeCanonicalizer.constRootType((C3PathConstExpr) last);
			if (rootType == null) return null;

			List<String> idents = new ArrayList<>();
			for (C3PsiElement elem : accessSequence)
			{
				if (elem instanceof C3CallExpr ce)
				{
					C3CallExprTail tail = ce.getCallExprTail();
					if (tail != null && tail.getAccessIdent() != null)
					{
						String identName = tail.getAccessIdent().getNameIdent();
						if (identName != null)
						{
							idents.add(identName);
							continue;
						}
					}
					// A subscript (`LOOKUP[i]`) indexes into the receiver
					// instead of naming a member: it contributes no segment
					// (the root type is already the element type).
					if (tail != null && isSubscriptTail(tail)) continue;
					String text = elem.getText();
					String[] parts = text.split("\\.");
					idents.add(parts[parts.length - 1]);
				}
			}
			Collections.reverse(idents);

			return new AccessIdentSequence(rootType, idents);
		}

		/**
		 * Whether a call tail is a subscript ({@code [i]}, {@code [a..b]}):
		 * the only tail kind starting with {@code [} in this position.
		 */
		private static boolean isSubscriptTail(@NotNull C3CallExprTail tail)
		{
			try
			{
				String text = tail.getText();
				return text != null && text.strip().startsWith("[");
			}
			catch (Exception e)
			{
				return false;
			}
		}
	}

	/**
	 * Member lookup works on the pointed-to type: strips trailing pointer
	 * stars ({@code Foo*} to {@code Foo}), since `.` auto-dereferences.
	 */
	private static @NotNull FullyQualifiedName dereference(@NotNull FullyQualifiedName type)
	{
		String name = type.getName();
		String clean = name;
		while (clean.endsWith("*")) clean = clean.substring(0, clean.length() - 1).strip();
		if (clean.equals(name) || clean.isEmpty()) return type;
		return new FullyQualifiedName(type.getModule(), clean);
	}

	private static final class AccessIdentSequence
	{		final FullyQualifiedName rootType;
		final List<String> idents;

		AccessIdentSequence(@NotNull FullyQualifiedName rootType, @NotNull List<String> idents)
		{
			this.rootType = rootType;
			this.idents = idents;
		}
	}
}
