package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3Attribute;
import org.c3lang.intellij.psi.C3AttributeName;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3PathIdent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class C3AttributeCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final C3AttributeCompletionContributor INSTANCE = new C3AttributeCompletionContributor();

	private C3AttributeCompletionContributor() {}

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
		while (start >= 0 && (Character.isJavaIdentifierPart(chars.charAt(start)) || chars.charAt(start) == '@'))
		{
			start--;
		}
		String word = chars.subSequence(start + 1, caretOffset).toString();
		boolean hasAt = word.startsWith("@");
		boolean insideAttr = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3Attribute.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3Attributes.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3AttributeName.class) != null;

		String prefix = hasAt ? word : word;
		CompletionResultSet scopedResult = result.withPrefixMatcher(prefix);
		double priority = hasAt || insideAttr ? 30.0 : 2.0;

		for (Map.Entry<String, AttributeSpecs.Spec> entry : AttributeSpecs.all().entrySet())
		{
			String attr = entry.getKey();
			String fullAttr = "@" + attr;
			boolean withArgs = entry.getValue().takesArgs();

			LookupElementBuilder builder = LookupElementBuilder.create(fullAttr)
				.withLookupStrings(List.of(fullAttr, attr))
				.withPresentableText(fullAttr)
				.withTypeText("builtin attribute")
				.bold()
				.withInsertHandler((insertionContext, item) -> {
					int insStart = insertionContext.getStartOffset();
					CharSequence docChars = insertionContext.getDocument().getCharsSequence();
					if (insStart > 0 && docChars.charAt(insStart - 1) == '@')
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

			scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, priority));
		}
	}
}
