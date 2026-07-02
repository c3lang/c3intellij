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
import org.c3lang.intellij.psi.C3FullyQualifiedTypeNameProvider;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PathIdent;
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

		return null;
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
