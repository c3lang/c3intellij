package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.index.C3ModuleIndex;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ImportCompletionContributor extends CompletionContributor {
    private final static Logger log = Logger.getInstance(ImportCompletionContributor.class.getName());

    public ImportCompletionContributor() {
        final var psiElementCapture = PlatformPatterns.or(
                psiElement().inside(C3ImportPath.class)
        );

        extend(CompletionType.BASIC, psiElementCapture, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                final Project project = parameters.getOriginalFile().getProject();
                final C3ImportPath importPath = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3ImportPath.class);
                if (importPath == null) {
                    return;
                }

                final TextRange range = TextRange.create(
                        importPath.getTextRange().getStartOffset(),
                        parameters.getEditor().getCaretModel().getOffset()
                );
                final String query = parameters.getEditor().getDocument().getText(range).trim();

                final MinusculeMatcher nameMatcher = NameUtil.buildMatcher(query, NameUtil.MatchingCaseSensitivity.NONE);

                StubIndex.getInstance()
                        .getAllKeys(C3ModuleIndex.KEY, project).stream()
                        .filter(module -> query.isBlank() || nameMatcher.matches(module))
                        .flatMap(key -> StubIndex.getElements(C3ModuleIndex.KEY, key, project, GlobalSearchScope.allScope(project), C3Module.class).stream())
                        .map(module -> {
                            final C3ModuleParams params = module.getModuleParams();

                            final StringBuilder tail = new StringBuilder();
                            if (params != null) {
                                tail.append("(<").append(params.getText()).append(">)");
                            }
                            final var attributes = module.getAttributes();
                            if (attributes != null) {
                                tail.append(" ").append(attributes.getText());
                            }

                            return LookupElementBuilder.create(C3PsiExtensionsKt.getModuleName(module))
                                    .withTailText(tail.toString(), true)
                                    .withInsertHandler(new FunctionInsertHandler(range));

                        }).distinct().forEach(result::addElement);
            }
        });
    }

    private record FunctionInsertHandler(TextRange range) implements InsertHandler<LookupElement> {
        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
            final Editor editor = context.getEditor();
            final Document document = editor.getDocument();
            final String textToInsert = item.getLookupString();

            document.replaceString(
                    range.getStartOffset(),
                    editor.getCaretModel().getOffset(),
                    textToInsert
            );
        }
    }
}
