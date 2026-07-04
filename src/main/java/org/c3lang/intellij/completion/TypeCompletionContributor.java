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
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.intention.AddImportQuickFix;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeFullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.stubs.C3TypeEnum;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;
import java.util.Objects;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

@SuppressWarnings("DuplicatedCode")
public final class TypeCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final TypeCompletionContributor INSTANCE = new TypeCompletionContributor();

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getInstance(TypeCompletionContributor.class);

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3PathIdent.class),
        psiElement(C3Types.TYPE_IDENT),
        psiElement().inside(C3FnParameterList.class)
    );

    private TypeCompletionContributor()
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

        C3Type lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3Type.class);
        if (lookupTarget == null) return;

        String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
        var matcher = CompletionExtensionsKt.getMatcher(lookupString);

        C3ModuleDefinition moduleDefinition = CompletionExtensionsKt.getModuleDefinition(parameters);
        if (moduleDefinition == null) return;

        var project = parameters.getPosition().getProject();
        ModuleName moduleName = moduleDefinition.getModuleName();
        InsertHandler<LookupElement> insertHandler = new StructInsertHandler(moduleDefinition, lookupTarget);

        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (!matcher.matches(key) && !key.isBlank()) continue;

            for (C3PsiElement element : StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class))
            {
                if (!(element instanceof C3TypeFullyQualifiedNamePsiElement typeName)) continue;

                Icon icon = iconFor(typeName.getTypeEnum());
                FullyQualifiedName fqName = typeName.getFqName();
                LookupElementBuilder lookupElementBuilder = LookupElementBuilder
                    .create(typeName, fqName.getFullName())
                    .withLookupStrings(List.of(
                        typeName.getFqName().getFullName(),
                        typeName.getFqName().getSuffixName(),
                        typeName.getFqName().getName()
                    ))
                    .withPsiElement(typeName)
                    .withIcon(icon)
                    .withPresentableText(Objects.equals(fqName.getModule(), moduleName)
                        ? fqName.getName()
                        : fqName.getFullName())
                    .withInsertHandler(insertHandler);

                result.addElement(lookupElementBuilder);
            }
        }
    }

    private static Icon iconFor(@NotNull C3TypeEnum typeEnum)
    {
        return switch (typeEnum)
        {
            case FALLBACK -> null;
            case STRUCT -> C3Icons.Nodes.STRUCT;
            case INTERFACE -> C3Icons.Nodes.INTERFACE;
            case ENUM -> C3Icons.Nodes.ENUM;
            case CONSTDEF -> C3Icons.Nodes.CONSTDEF;
            case UNION -> C3Icons.Nodes.UNION;
            case BITSTRUCT -> C3Icons.Nodes.BITSTRUCT;
            case FAULT -> C3Icons.Nodes.FAULT;
        };
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class StructInsertHandler implements InsertHandler<LookupElement>
    {
        private final C3ModuleDefinition moduleDefinition;
        private final PsiElement lookupTarget;

        private StructInsertHandler(@NotNull C3ModuleDefinition moduleDefinition, @NotNull PsiElement lookupTarget)
        {
            this.moduleDefinition = moduleDefinition;
            this.lookupTarget = lookupTarget;
        }

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
        {
            PsiElement psiElement = item.getPsiElement();
            if (!(psiElement instanceof C3TypeName element)) return;

            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                AddImportQuickFix.ImportAction importAction =
                    AddImportQuickFix.addImportAsText(element, moduleDefinition);

                ModuleName importModuleName = importAction != null ? importAction.getModuleName() : null;
                String textToInsert = moduleDefinition.textToInsert(importModuleName, element);
                int endOffset = context.getEditor().getCaretModel().getOffset();

                context.getDocument().replaceString(
                    lookupTarget.getTextRange().getStartOffset(),
                    endOffset,
                    textToInsert
                );

                if (importAction != null)
                {
                    importAction.write(context.getDocument());
                }
            });
        }
    }
}
