package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3PathIdent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class C3CompileTimeCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final C3CompileTimeCompletionContributor INSTANCE = new C3CompileTimeCompletionContributor();

	private static final List<String> CT_DIRECTIVES = List.of(
		"$assert", "$case", "$default", "$defined",
		"$echo", "$else", "$embed", "$exec",
		"$expand", "$endfor", "$endforeach", "$endif",
		"$endswitch", "$eval", "$error", "$for",
		"$foreach", "$if", "$include", "$stringify",
		"$switch", "$vaarg", "$Typefrom", "$Typeof"
	);

	private static final Set<String> CT_DIRECTIVES_WITH_ARGS = Set.of(
		"$assert", "$defined", "$echo", "$embed", "$exec",
		"$expand", "$eval", "$error", "$include", "$stringify",
		"$vaarg", "$Typefrom", "$Typeof", "$if", "$for",
		"$foreach", "$switch"
	);

	private static final List<String> BUILTIN_CONSTANTS = List.of(
		"$$BENCHMARK_FNS", "$$BENCHMARK_NAMES", "$$DATE",
		"$$FILE", "$$FILEPATH", "$$FUNC",
		"$$FUNCTION", "$$LINE", "$$LINE_RAW",
		"$$MODULE", "$$TEST_FNS", "$$TEST_NAMES",
		"$$TIME"
	);

	private C3CompileTimeCompletionContributor() {}

	@Override
	protected void addCompletions(
		@NotNull CompletionParameters parameters,
		@NotNull ProcessingContext context,
		@NotNull CompletionResultSet result)
	{
		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), PsiComment.class) != null)
		{
			return;
		}

		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), C3AccessIdent.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3CallExprTail.class) != null)
		{
			return;
		}
		C3PathIdent pathIdent = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3PathIdent.class);
		if (pathIdent != null && pathIdent.getPath() != null)
		{
			return;
		}

		int caretOffset = parameters.getOffset();
		CharSequence chars = parameters.getEditor().getDocument().getCharsSequence();
		int start = caretOffset - 1;
		while (start >= 0 && (Character.isJavaIdentifierPart(chars.charAt(start)) || chars.charAt(start) == '$'))
		{
			start--;
		}
		String word = chars.subSequence(start + 1, caretOffset).toString();
		boolean hasDollarDollar = word.startsWith("$$");
		boolean hasSingleDollar = !hasDollarDollar && word.startsWith("$");

		CompletionResultSet scopedResult = result.withPrefixMatcher(word);

		if (hasDollarDollar)
		{
			for (String c : BUILTIN_CONSTANTS)
			{
				String nameWithoutDollars = c.substring(2);
				LookupElementBuilder builder = LookupElementBuilder.create(c)
					.withLookupStrings(List.of(c, nameWithoutDollars))
					.withPresentableText(c)
					.withIcon(C3Icons.Nodes.CONSTANT)
					.withTypeText("builtin constant")
					.bold()
					.withInsertHandler((insertionContext, item) -> {
						int insStart = insertionContext.getStartOffset();
						CharSequence docChars = insertionContext.getDocument().getCharsSequence();
						while (insStart > 0 && docChars.charAt(insStart - 1) == '$')
						{
							insertionContext.getDocument().deleteString(insStart - 1, insStart);
							insStart--;
						}
					});

				scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 30.0));
			}
			return;
		}

		double directivePriority = hasSingleDollar ? 30.0 : 3.0;
		double constantPriority = hasSingleDollar ? 25.0 : 3.0;

		for (String directive : CT_DIRECTIVES)
		{
			String simpleName = directive.substring(1);
			boolean withArgs = CT_DIRECTIVES_WITH_ARGS.contains(directive);

			LookupElementBuilder builder = LookupElementBuilder.create(directive)
				.withLookupStrings(List.of(directive, simpleName))
				.withPresentableText(directive)
				.withIcon(C3Icons.Nodes.MACRO)
				.withTypeText("compile-time directive")
				.bold()
				.withInsertHandler((insertionContext, item) -> {
					int insStart = insertionContext.getStartOffset();
					CharSequence docChars = insertionContext.getDocument().getCharsSequence();
					if (insStart > 0 && docChars.charAt(insStart - 1) == '$')
					{
						insertionContext.getDocument().deleteString(insStart - 1, insStart);
					}
					if (withArgs)
					{
						int tail = insertionContext.getTailOffset();
						if (tail < docChars.length() && docChars.charAt(tail) == '(')
						{
							insertionContext.getEditor().getCaretModel().moveToOffset(tail + 1);
						}
						else
						{
							insertionContext.getDocument().insertString(tail, "()");
							insertionContext.getEditor().getCaretModel().moveToOffset(tail + 1);
						}
					}
				});

			scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, directivePriority));
		}

		for (String c : BUILTIN_CONSTANTS)
		{
			String nameWithoutDollars = c.substring(2);
			LookupElementBuilder builder = LookupElementBuilder.create(c)
				.withLookupStrings(List.of(c, nameWithoutDollars))
				.withPresentableText(c)
				.withIcon(C3Icons.Nodes.CONSTANT)
				.withTypeText("builtin constant")
				.bold()
				.withInsertHandler((insertionContext, item) -> {
					int insStart = insertionContext.getStartOffset();
					CharSequence docChars = insertionContext.getDocument().getCharsSequence();
					while (insStart > 0 && docChars.charAt(insStart - 1) == '$')
					{
						insertionContext.getDocument().deleteString(insStart - 1, insStart);
						insStart--;
					}
				});

			scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, constantPriority));
		}
	}
}
