package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class TypeCompletionContributor extends CompletionContributor {
    private final static Logger log = Logger.getInstance(TypeCompletionContributor.class.getName());

    public TypeCompletionContributor() {
        final var psiElementCapture = psiElement().andOr(
//                psiElement().inside(C3ImportDecl.class)
        );

        extend(CompletionType.BASIC, psiElementCapture, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
//                final Project project = parameters.getOriginalFile().getProject();
//                final PsiElement originalElement = CompletionUtil.getOriginalOrSelf(parameters.getPosition());
//                final C3ImportDecl importDeclaration = PsiTreeUtil.getParentOfType(originalElement, C3ImportDecl.class);
//
//                if (importDeclaration == null) {
//                    return;
//                }
//
//                final TextRange range = TextRange.create(
//                        importDeclaration.getFirstChild().getTextRange().getEndOffset(),
//                        parameters.getEditor().getCaretModel().getOffset()
//                );
//                final String query = parameters.getEditor().getDocument().getText(range).trim();
//
//                final MinusculeMatcher nameMatcher = NameUtil.buildMatcher(query, NameUtil.MatchingCaseSensitivity.NONE);
//
//                StubIndex.getInstance()
//                        .getAllKeys(C3ModuleIndex.KEY, project).stream()
//                        .filter(module -> query.isBlank() || nameMatcher.matches(module))
//                        .flatMap(key -> StubIndex.getElements(C3ModuleIndex.KEY, key, project, GlobalSearchScope.allScope(project), C3Module.class).stream())
//                        .map(module -> {
//                            final C3ModuleParams params = module.getModuleParams();
//
//                            final StringBuilder tail = new StringBuilder();
//                            if (params != null) {
//                                tail.append("(<").append(params.getText()).append(">)");
//                            }
//                            final var attributes = module.getAttributes();
//                            if (attributes != null) {
//                                tail.append(" ").append(attributes.getText());
//                            }
//
//                            return LookupElementBuilder.create(module.getName())
//                                    .withTailText(tail.toString(), true)
//                                    .withInsertHandler(new FunctionInsertHandler(importDeclaration));
//                        }).distinct().forEach(result::addElement);
            }
        });
    }

//    private static class FunctionInsertHandler implements InsertHandler<LookupElement> {
//
//        private final C3ImportDecl importDeclaration;
//
//        public FunctionInsertHandler(C3ImportDecl importDeclaration) {
//            this.importDeclaration = importDeclaration;
//        }
//
//        @Override
//        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
//            final Editor editor = context.getEditor();
//            final Document document = editor.getDocument();
//
//            final String endOfStatement = (importDeclaration.getLastChild() instanceof LeafPsiElement) ? "" : ";";
//            final String textToInsert = item.getLookupString() + endOfStatement;
//
//            final int startOffset = importDeclaration.getFirstChild().getTextRange().getEndOffset() + 1/*space*/;
//            final int endOffset = editor.getCaretModel().getOffset();
//
//            document.replaceString(startOffset, endOffset, textToInsert);
//
//            editor.getCaretModel().moveToOffset(startOffset + textToInsert.length());
//        }
//    }
}
