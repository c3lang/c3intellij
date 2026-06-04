package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionType;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class C3CompletionContributor extends CompletionContributor
{
	public C3CompletionContributor()
	{
		var pattern = psiElement();

		extend(CompletionType.BASIC, pattern, FunctionCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, TypeCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, ImportCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, ConstCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, FaultCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, TailExprCompletionContributor.INSTANCE);
		extend(CompletionType.BASIC, pattern, InitializerListCompletionContributor.INSTANCE);
		//extend(CompletionType.BASIC, pattern, DocCommentCompletionContributor) TODO
		extend(CompletionType.BASIC, pattern, TopLevelCompletionContributor.INSTANCE);
	}

	@Override
	public void beforeCompletion(CompletionInitializationContext context)
	{
		// path
		context.setDummyIdentifier(CompletionExtensionsKt.DUMMY_IDENTIFIER);
	}
}
