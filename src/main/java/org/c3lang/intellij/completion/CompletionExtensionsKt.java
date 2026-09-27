package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3BinaryExpr;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CompoundInitExpr;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3FullyQualifiedTypeNameProvider;
import org.c3lang.intellij.psi.C3GlobalDecl;
import org.c3lang.intellij.psi.C3InitListExpr;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeSuffix;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public final class CompletionExtensionsKt
{
	public static final String DUMMY_IDENTIFIER = "dummy;";

	private CompletionExtensionsKt() {}

	public static @Nullable C3ModuleDefinition getModuleDefinition(@NotNull CompletionParameters parameters)
	{
		return siblingOf(parameters, C3ModuleDefinition.class);
	}

	public static <T extends PsiElement> @Nullable T siblingOf(
		@NotNull CompletionParameters parameters,
		@NotNull Class<T> type)
	{
		PsiElement originalPosition = parameters.getOriginalPosition();
		T originalParent = originalPosition != null ? PsiTreeUtil.getParentOfType(originalPosition, type) : null;
		return originalParent != null ? originalParent : PsiTreeUtil.getParentOfType(parameters.getPosition(), type);
	}

	public static @NotNull String getLookupString(
		@NotNull CompletionParameters parameters,
		@NotNull PsiElement lookupTarget)
	{
		return parameters.getEditor().getDocument().getText(
			TextRange.create(
				lookupTarget.getTextRange().getStartOffset(),
				parameters.getEditor().getCaretModel().getOffset()
			)
		);
	}

	public static @Nullable FullyQualifiedName getRootType(@NotNull PsiElement lookupTarget)
	{
		C3CompoundInitExpr compoundInitExpr =
			PsiTreeUtil.getParentOfType(lookupTarget, C3CompoundInitExpr.class);
		if (compoundInitExpr != null)
		{
			return FullyQualifiedName.from(compoundInitExpr.getType());
		}

		C3BinaryExpr binaryExpr = PsiTreeUtil.getParentOfType(lookupTarget, C3BinaryExpr.class);
		C3PathIdent pathIdent = PsiTreeUtil.findChildOfType(binaryExpr, C3PathIdent.class);
		C3LocalDeclAfterType localDeclAfterType = null;
		if (pathIdent != null)
		{
			var declarations = pathIdent.findLocalDeclAfterType();
			if (declarations.size() == 1) localDeclAfterType = declarations.get(0);
		}

		if (localDeclAfterType != null)
		{
			FullyQualifiedName fqn = localDeclAfterType.findTypeName();
			if (fqn != null)
			{
				C3CallExpr callExpr = PsiTreeUtil.getParentOfType(pathIdent, C3CallExpr.class);
				var tails = PsiTreeUtil.findChildrenOfType(callExpr, C3CallExprTail.class);
				String accessPath = tails.stream()
					.map(PsiElement::getText)
					.collect(Collectors.joining(", "));

				return new FullyQualifiedName(fqn.getModule(), fqn.getName() + accessPath);
			}
		}

		C3FullyQualifiedTypeNameProvider provider =
			PsiTreeUtil.getParentOfType(lookupTarget, C3FullyQualifiedTypeNameProvider.class);
		if (provider != null)
		{
			FullyQualifiedName fqn = provider.findTypeName();
			if (fqn != null) return fqn;
		}

		PsiElement[] typeProviders = PsiTreeUtil.collectElements(lookupTarget, element ->
			element instanceof C3FullyQualifiedTypeNameProvider);
		for (PsiElement element : typeProviders)
		{
			FullyQualifiedName fqn = ((C3FullyQualifiedTypeNameProvider) element).findTypeName();
			if (fqn != null) return fqn;
		}

		return initListTargetType(lookupTarget);
	}

	/**
	 * Target type of a designated initializer list, e.g.
	 * {@code ascii::CharType[256]} for the outer list in
	 * {@code const CharType[256] ASCII_LOOKUP = { [0..31] = { .control } }}.
	 * Array suffixes are preserved textually so member walks can unwrap
	 * one level per subscript-nested list. Locals are already covered by
	 * the provider branch above; consts and globals are not (their type
	 * is a sibling, not an ancestor, of the list).
	 */
	private static @Nullable FullyQualifiedName initListTargetType(@NotNull PsiElement lookupTarget)
	{
		try
		{
			C3InitListExpr outer = outermostInitList(lookupTarget);
			if (outer == null) return null;
			C3ConstDeclarationStmt constDecl = PsiTreeUtil.getParentOfType(outer, C3ConstDeclarationStmt.class);
			if (constDecl != null && constDecl.getType() != null)
			{
				return qualifiedTargetText(constDecl.getType());
			}
			C3GlobalDecl globalDecl = PsiTreeUtil.getParentOfType(outer, C3GlobalDecl.class);
			if (globalDecl != null && globalDecl.getOptionalType() != null
				&& globalDecl.getOptionalType().getType() != null)
			{
				return qualifiedTargetText(globalDecl.getOptionalType().getType());
			}
		}
		catch (Exception ignored)
		{
		}
		return null;
	}

	private static @Nullable C3InitListExpr outermostInitList(@NotNull PsiElement lookupTarget)
	{
		C3InitListExpr inner = PsiTreeUtil.getParentOfType(lookupTarget, C3InitListExpr.class);
		if (inner == null) return null;
		C3InitListExpr outer = inner;
		int guard = 0;
		while (guard++ < 8)
		{
			C3Arg arg = PsiTreeUtil.getParentOfType(outer, C3Arg.class);
			C3InitListExpr parent = arg != null ? PsiTreeUtil.getParentOfType(arg, C3InitListExpr.class) : null;
			if (parent == null) return outer;
			outer = parent;
		}
		return outer;
	}

	/**
	 * Base-type module plus the written suffixes
	 * ({@code CharType[256]} in {@code ascii} becomes
	 * {@code ascii::CharType[256]}).
	 */
	private static @Nullable FullyQualifiedName qualifiedTargetText(@NotNull C3Type type)
	{
		try
		{
			FullyQualifiedName base = FullyQualifiedName.from(type);
			if (base == null) return null;
			StringBuilder qualified = new StringBuilder(base.getFullName());
			for (C3TypeSuffix suffix : type.getTypeSuffixList())
			{
				if (suffix.getText() != null) qualified.append(suffix.getText());
			}
			return FullyQualifiedName.parse(qualified.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static @NotNull MinusculeMatcher getMatcher(@NotNull String lookupString)
	{
		return NameUtil.buildMatcher(
			"*" + lookupString + "*",
			NameUtil.MatchingCaseSensitivity.NONE
		);
	}

	public static int matchingDegreeOrZero(@NotNull MinusculeMatcher matcher, @NotNull String name)
	{
		return Math.max(matcher.matchingDegree(name), 0);
	}
}
