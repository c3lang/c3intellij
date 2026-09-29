package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CompoundStatement;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.ShortType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class C3StatementCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final C3StatementCompletionContributor INSTANCE = new C3StatementCompletionContributor();

	private static final ElementPattern<PsiElement> PATTERN =
		psiElement().inside(C3CompoundStatement.class).andNot(psiElement().inside(PsiComment.class));

	private static final List<String> STATEMENTS = List.of(
		"var", "if", "else", "while", "for", "foreach", "switch", "case", "default", "break", "continue", "defer", "assert"
	);

	private C3StatementCompletionContributor() {}

	@Override
	protected void addCompletions(
		@NotNull CompletionParameters parameters,
		@NotNull ProcessingContext context,
		@NotNull CompletionResultSet result)
	{
		if (!PATTERN.accepts(parameters.getPosition()) && !PATTERN.accepts(parameters.getOriginalPosition()))
		{
			return;
		}

		// Don't suggest statement keywords after :: (path) or . (dot)
		C3PathIdent pathIdent = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3PathIdent.class);
		if (pathIdent != null && pathIdent.getPath() != null)
		{
			return;
		}
		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), C3AccessIdent.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3CallExprTail.class) != null)
		{
			return;
		}

		C3FuncDef enclosingFunc = null;
		C3FuncDefinition funcDefinition = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3FuncDefinition.class);
		if (funcDefinition != null)
		{
			enclosingFunc = funcDefinition.getFuncDef();
		}
		else
		{
			enclosingFunc = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3FuncDef.class);
		}
		ShortType returnType = enclosingFunc != null ? enclosingFunc.getReturnType() : null;
		boolean hasReturnValue = returnType != null && !returnType.getValue().equals("void");

		LookupElementBuilder returnBuilder = LookupElementBuilder.create("return")
			.bold()
			.withTypeText(returnType != null ? returnType.getFullName() : "");

		if (hasReturnValue)
		{
			returnBuilder = returnBuilder.withInsertHandler((insertionContext, item) -> {
				int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
				insertionContext.getDocument().insertString(caretOffset, " ;");
				insertionContext.getEditor().getCaretModel().moveToOffset(caretOffset + 1);
			});
			result.addElement(PrioritizedLookupElement.withPriority(returnBuilder, 20.0));
		}
		else
		{
			returnBuilder = returnBuilder.withInsertHandler((insertionContext, item) -> {
				int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
				insertionContext.getDocument().insertString(caretOffset, ";");
				insertionContext.getEditor().getCaretModel().moveToOffset(caretOffset + 1);
			});
			result.addElement(PrioritizedLookupElement.withPriority(returnBuilder, 10.0));
		}

		for (String kw : STATEMENTS)
		{
			result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(kw).bold(), 5.0));
		}
	}
}
