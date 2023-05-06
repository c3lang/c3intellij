package org.c3lang.intellij;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.ObjectPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class C3CompletionContributor extends CompletionContributor implements DumbAware
{
    public C3CompletionContributor()
    {
        String[] topLevelKw = {
                "fn",
                "struct",
                "fault",
                "macro",
                "def",
                "module",
                "import",
                "extern",
                "const",
                "tlocal",
                "bitstruct",
                "static initialize",
                "static finalize",
                "asm",
                "$switch",
        };
        final List<LookupElement> topLevelLookups = new ArrayList<>();
        for (String s : topLevelKw)
        {
            topLevelLookups.add(LookupElementBuilder.create(s + " ").withPresentableText(s).bold());
        }
        InsertHandler<LookupElement> handler = (insertionContext, lookupElement) -> {
            insertionContext.getDocument().insertString(insertionContext.getEditor().getCaretModel().getOffset(), "()");
            insertionContext.getEditor().getCaretModel().moveCaretRelatively(1, 0, false, false, false);
        };

        for (String s : Arrays.asList("$include", "$assert", "$echo", "$if"))
        {
            topLevelLookups.add(LookupElementBuilder
                                        .create(s + " (")
                                        .bold()
                                        .withPresentableText(s));
        }
        PsiElementPattern.Capture<PsiElement> context = psiElement()
                .with(new PatternCondition<>("toplevel")
                {
                        @Override
                        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext processingContext)
                        {
                            if (!(psiElement.getParent() instanceof C3File)) return false;
                            PsiElement sib = psiElement.getPrevSibling();
                            while (sib instanceof PsiComment || sib instanceof PsiWhiteSpace)
                            {
                                sib = sib.getPrevSibling();
                            }
                            if (sib == null) return true;
                            if (sib instanceof C3DefaultModuleSection || sib instanceof C3ModuleSection) return true;
                            return false;
                        }
                    });

        extend(CompletionType.BASIC, context, new CompletionProvider<>()
        {
            public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet resultSet)
            {
                System.out.println(parameters.getPosition());
                resultSet.addAllElements(topLevelLookups);
            }
        });
    }


}
