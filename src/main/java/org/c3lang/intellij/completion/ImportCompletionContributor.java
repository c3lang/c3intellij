package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Util;
import org.c3lang.intellij.psi.C3ImportPath;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

@SuppressWarnings("DuplicatedCode")
public final class ImportCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final ImportCompletionContributor INSTANCE = new ImportCompletionContributor();

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3ImportPath.class)
    );

    private ImportCompletionContributor()
    {
    }

    @Override
    protected void addCompletions(
            @NotNull CompletionParameters parameters,
            @NotNull ProcessingContext context,
            @NotNull CompletionResultSet result)
    {
        if (!PATTERN.accepts(parameters.getPosition()))
        {
            return;
        }

        PsiElement packagePathElement = parameters.getPosition().getParent();
        String fullTextWithDummy = packagePathElement.getText();
        int parentStartOffset = packagePathElement.getTextRange().getStartOffset();
        int caretOffset = parameters.getOffset();
        int prefixLength = caretOffset - parentStartOffset;
        if (prefixLength < 0 || prefixLength > fullTextWithDummy.length())
        {
            return;
        }

        var project = parameters.getEditor().getProject();
        if (project == null) return;

        String prefix = fullTextWithDummy.substring(0, prefixLength);
        CompletionResultSet resultSetWithPrefix = result.withPrefixMatcher(prefix);

        for (String module : C3Util.INSTANCE.findC3ModulesStartingWith(project, prefix))
        {
            resultSetWithPrefix.addElement(LookupElementBuilder.create(module));
        }
    }
}
