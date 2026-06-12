package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.intention.AddImportQuickFix;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PathConst;
import org.c3lang.intellij.psi.C3PathConstExpr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

@SuppressWarnings("DuplicatedCode")
public final class ConstCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final ConstCompletionContributor INSTANCE = new ConstCompletionContributor();

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getInstance(ConstCompletionContributor.class);

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3PathConst.class)
    );

    private ConstCompletionContributor()
    {
    }

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

        C3ModuleDefinition moduleDefinition = CompletionExtensionsKt.getModuleDefinition(parameters);
        if (moduleDefinition == null) return;

        C3PathConstExpr lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3PathConstExpr.class);
        if (lookupTarget == null) return;

        String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
        var matcher = CompletionExtensionsKt.getMatcher(lookupString);

        var moduleName = moduleDefinition.getModuleName();
        TextRange elementRange = lookupTarget.getTextRange();
        Project project = parameters.getPosition().getProject();
        InsertHandler<LookupElement> insertHandler = new ConstInsertHandler(moduleDefinition, elementRange);

        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (!matcher.matches(key) && !key.isBlank()) continue;

            for (C3PsiElement element : StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    GlobalSearchScope.allScope(project),
                    C3PsiElement.class))
            {
                if (!(element instanceof C3ConstDeclarationStmt constDeclaration)) continue;

                FullyQualifiedName fullyQualifiedName = constDeclaration.getFqName();
                LookupElementBuilder lookupElementBuilder = LookupElementBuilder
                    .create(constDeclaration, fullyQualifiedName.getFullName())
                    .withLookupStrings(List.of(
                        fullyQualifiedName.getFullName(),
                        fullyQualifiedName.getSuffixName(),
                        fullyQualifiedName.getName()
                    ))
                    .withLookupStrings(List.of(fullyQualifiedName.getFullName(), fullyQualifiedName.getName()))
                    .withIcon(C3Icons.Nodes.CONSTANT)
                    .withPresentableText(Objects.equals(fullyQualifiedName.getModule(), moduleName)
                        ? fullyQualifiedName.getName()
                        : fullyQualifiedName.getFullName())
                    .withInsertHandler(insertHandler);

                result.addElement(lookupElementBuilder);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class ConstInsertHandler implements InsertHandler<LookupElement>
    {
        private final C3ModuleDefinition moduleDefinition;
        private final TextRange range;

        private ConstInsertHandler(@NotNull C3ModuleDefinition moduleDefinition, @NotNull TextRange range)
        {
            this.moduleDefinition = moduleDefinition;
            this.range = range;
        }

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
        {
            PsiElement psiElement = item.getPsiElement();
            if (!(psiElement instanceof C3ConstDeclarationStmt element)) return;

            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                AddImportQuickFix.ImportAction imported =
                    AddImportQuickFix.Companion.addImportAsText(element, moduleDefinition);

                var importedModuleName = imported != null ? imported.getModuleName() : null;
                String textToInsert = moduleDefinition.textToInsert(importedModuleName, element);
                int endOffset = context.getEditor().getCaretModel().getOffset();

                context.getDocument().replaceString(
                    range.getStartOffset(),
                    endOffset,
                    textToInsert
                );
                context.getEditor().getCaretModel().moveToOffset(range.getStartOffset() + textToInsert.length());

                if (imported != null)
                {
                    imported.write(context.getDocument());
                }
            });
        }
    }
}
