package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public final class TopLevelCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final TopLevelCompletionContributor INSTANCE = new TopLevelCompletionContributor();

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().withParent(C3File.class).andNot(psiElement().inside(PsiComment.class)),
        psiElement().withParent(C3ModuleDefinition.class).andNot(psiElement().inside(PsiComment.class))
    );

    private TopLevelCompletionContributor()
    {
    }

    @Override
    protected void addCompletions(
            @NotNull CompletionParameters parameters,
            @NotNull ProcessingContext context,
            @NotNull CompletionResultSet result)
    {
        if (!PATTERN.accepts(parameters.getPosition()) || !PATTERN.accepts(parameters.getOriginalPosition()))
        {
            return;
        }

        CompletionUtil.provideCompletionsAfterSymbol(
            parameters,
            result,
            "",
            List.of(
                "fn",
                "struct",
                "faultdef",
                "macro",
                "alias",
                "typedef",
                "module",
                "import",
                "extern",
                "const",
                "tlocal",
                "bitstruct",
                "static initialize",
                "static finalize",
                "asm",
                "$switch"
            )
        );

        Map<String, InsertHandler<LookupElement>> handlers = new LinkedHashMap<>();
        handlers.put("main", (insertionContext, item) -> {
            insertionContext.getDocument().replaceString(
                insertionContext.getStartOffset(),
                insertionContext.getTailOffset(),
                "fn int main()\n" +
                    "{\n" +
                    "    \n" +
                    "}"
            );
            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset() - 2);
        });
        handlers.put("maina", (insertionContext, item) -> {
            insertionContext.getDocument().replaceString(
                insertionContext.getStartOffset(),
                insertionContext.getTailOffset(),
                "fn int main(String[] args)\n" +
                    "{\n" +
                    "    \n" +
                    "}"
            );
            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset() - 2);
        });

        CompletionUtil.provideCompletionsAfterSymbolWithInsertHandler(
            parameters,
            result,
            "",
            handlers
        );
    }
}
