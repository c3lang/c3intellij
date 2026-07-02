package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3ParserDefinition;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DocCommentCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final DocCommentCompletionContributor INSTANCE = new DocCommentCompletionContributor();

	private static final ElementPattern<?> PATTERN =
		PlatformPatterns.psiElement().inside(
			PlatformPatterns.psiElement().withElementType(C3ParserDefinition.DOC_COMMENT)
		);

	private @Nullable List<C3ParamDecl> paramDeclList = null;

	private DocCommentCompletionContributor() {}

	@Override
	protected void addCompletions(
		@NotNull CompletionParameters parameters,
		@NotNull ProcessingContext context,
		@NotNull CompletionResultSet result)
	{
		if (!(PATTERN.accepts(parameters.getPosition()) && PATTERN.accepts(parameters.getOriginalPosition())))
		{
			return;
		}

		paramDeclList = getParamDeclList(parameters.getPosition());

		CompletionUtil.provideCompletionsAfterSymbol(
			parameters,
			result,
			"@",
			Arrays.asList(
				"param",
				"return",
				"return?",
				"deprecated",
				"require",
				"ensure",
				"pure"
			)
		);

		CompletionUtil.provideCompletionsAfterSymbol(
			parameters,
			result,
			"@param ",
			Arrays.asList(
				"[in]",
				"[out]",
				"[inout]",
				"[own]",
				"[init]",
				"[drop]",
				"[&in]",
				"[&out]",
				"[&inout]",
				"[&own]",
				"[&init]",
				"[&drop]"
			)
		);

		CompletionUtil.provideCompletionsAfterSymbols(
			parameters,
			result,
			Arrays.asList(
				"@param ",
				"[in] ",
				"[out] ",
				"[inout] ",
				"[&in] ",
				"[&out] ",
				"[&inout] "
			),
			getParameters()
		);
	}

	private static @Nullable List<C3ParamDecl> getParamDeclList(@NotNull PsiElement comment)
	{
		PsiElement next = comment.getNextSibling();

		while (next instanceof PsiWhiteSpace || next instanceof PsiComment)
		{
			next = next.getNextSibling();
		}

		if (next == null)
		{
			return null;
		}

		PsiElement firstChild = next.getFirstChild();
		if (firstChild instanceof C3FuncDefinition)
		{
			C3FuncDefinition funcDef = (C3FuncDefinition) firstChild;
			var paramList = funcDef.getFuncDef().getFnParameterList().getParameterList();
			return paramList != null ? paramList.getParamDeclList() : null;
		}
		if (firstChild instanceof C3MacroDefinition)
		{
			C3MacroDefinition macroDef = (C3MacroDefinition) firstChild;
			var paramList = macroDef.getMacroParams().getParameterList();
			return paramList != null ? paramList.getParamDeclList() : null;
		}

		return null;
	}

	private @NotNull List<String> getParameters()
	{
		if (paramDeclList == null)
		{
			return new ArrayList<>();
		}
		List<String> names = new ArrayList<>();
		for (C3ParamDecl decl : paramDeclList)
		{
			String name = decl.getParameter().getName();
			if (name != null)
			{
				names.add(name);
			}
		}
		return names;
	}
}
