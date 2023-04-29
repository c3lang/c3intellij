package org.c3lang.intellij;

import com.intellij.codeInsight.completion.*;
import org.c3lang.intellij.psi.C3Types;

public class C3CompletionContributor extends CompletionContributor
{
    public C3CompletionContributor()
    {
        /*
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(C3Types.IDENT), new CompletionProvider<>()
        {
            public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet resultSet)
            {
                resultSet.addElement(LookupElementBuilder.create("hello"));
            }
        });*/
    }

}
