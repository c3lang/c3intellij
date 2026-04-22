package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

import java.util.List;
import java.util.Map;

public final class CompletionUtil
{
	private CompletionUtil()
	{
	}

	public static void provideCompletionsAfterSymbols(CompletionParameters parameters, CompletionResultSet result,
	                                                  List<String> symbols, List<String> completions)
	{
		String matchingSymbol = symbols.stream()
		                               .filter(symbol -> isCursorDirectlyAfterSymbol(parameters, symbol))
		                               .findFirst()
		                               .orElse(null);

		if (matchingSymbol == null) return;
		for (String completion : completions)
		{
			result.addElement(LookupElementBuilder.create(completion));
		}
	}

	public static void provideCompletionsAfterSymbolWithInsertHandler(CompletionParameters parameters,
	                                                                  CompletionResultSet result, String symbol,
	                                                                  Map<String, InsertHandler<LookupElement>> completions)
	{
		if (!isCursorDirectlyAfterSymbol(parameters, symbol)) return;

		for (Map.Entry<String, InsertHandler<LookupElement>> completion : completions.entrySet())
		{
			if (completion.getValue() == null)
			{
				result.addElement(LookupElementBuilder.create(completion.getKey()));
			}
			else
			{
				result.addElement(LookupElementBuilder.create(completion.getKey())
				                                      .withInsertHandler(completion.getValue()));
			}
		}
	}

	public static void provideCompletionsAfterSymbol(CompletionParameters parameters, CompletionResultSet result,
	                                                 String symbol, List<String> completions)
	{
		if (!isCursorDirectlyAfterSymbol(parameters, symbol)) return;

		for (String completion : completions)
		{
			result.addElement(LookupElementBuilder.create(completion));
		}
	}

	private static boolean isCursorDirectlyAfterSymbol(CompletionParameters parameters, String symbol)
	{
		int offset = parameters.getOffset();
		if (offset < symbol.length()) return false;

		CharSequence chars = parameters.getEditor().getDocument().getCharsSequence();

		for (int i = 0; i < symbol.length(); i++)
		{
			if (chars.charAt(offset - symbol.length() + i) != symbol.charAt(i)) return false;
		}
		return true;
	}
}