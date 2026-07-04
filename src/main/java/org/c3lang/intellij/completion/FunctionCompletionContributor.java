package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.intention.AddImportQuickFix;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3ParamPathElement;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.ParamType;
import org.c3lang.intellij.psi.ShortType;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.and;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public final class FunctionCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final FunctionCompletionContributor INSTANCE = new FunctionCompletionContributor();

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getInstance(FunctionCompletionContributor.class);

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3CallExpr.class),
        and(
            psiElement().inside(C3PathIdentExpr.class),
            psiElement().andNot(
                psiElement().inside(C3ParamPathElement.class)
            )
        )
    );

    private FunctionCompletionContributor()
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

        if (!isValidParameterValue(parameters.getOriginalPosition()))
        {
            return;
        }

        C3ModuleDefinition moduleDefinition = CompletionExtensionsKt.getModuleDefinition(parameters);
        if (moduleDefinition == null) return;

        C3PathIdentExpr lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3PathIdentExpr.class);
        if (lookupTarget == null) return;

        String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
        var matcher = CompletionExtensionsKt.getMatcher(lookupString);
        TextRange elementRange = lookupTarget.getTextRange();
        var project = parameters.getPosition().getProject();
        String containingFileName = parameters.getPosition().getContainingFile().getName();
        InsertHandler<LookupElement> insertHandler = new FunctionInsertHandler(moduleDefinition, elementRange);

        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (!matcher.matches(key) && !key.isBlank()) continue;

            for (C3PsiElement psiElement : StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class))
            {
                if (!(psiElement instanceof C3CallablePsiElement element)) continue;

                double sameFileBonus = element.getSourceFileName().equals(containingFileName) ? 1.0 : 0.0;
                double sameModuleBonus = java.util.Objects.equals(element.getModuleName(), moduleDefinition.getModuleName()) ? 1.0 : 0.0;
                double importBonus = moduleDefinition.getVisibleModulePrefix(element.getModuleName()) != null ? 1.0 : 0.0;

                FullyQualifiedName fqName = element.getFqName();
                double nameDegree = CompletionExtensionsKt.matchingDegreeOrZero(matcher, fqName.getFullName());
                ModuleName elementModule = element.getModuleName();
                double moduleDegree = elementModule != null
                    ? CompletionExtensionsKt.matchingDegreeOrZero(matcher, elementModule.getValue())
                    : 0.0;
                double typeDegree = 0.0;

                double priority = sameFileBonus + sameModuleBonus + importBonus + moduleDegree + nameDegree + typeDegree;
                result.addElement(
                    PrioritizedLookupElement.withPriority(
                        createLookupElementBuilder(moduleDefinition, element, fqName, insertHandler),
                        priority
                    )
                );
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class FunctionInsertHandler implements InsertHandler<LookupElement>
    {
        private final C3ModuleDefinition moduleDefinition;
        private final TextRange range;

        private FunctionInsertHandler(@NotNull C3ModuleDefinition moduleDefinition, @NotNull TextRange range)
        {
            this.moduleDefinition = moduleDefinition;
            this.range = range;
        }

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
        {
            PsiElement psiElement = item.getPsiElement();
            if (!(psiElement instanceof C3CallablePsiElement element)) return;

            WriteCommandAction.runWriteCommandAction(context.getProject(), () -> {
                AddImportQuickFix.ImportAction importAction =
                    AddImportQuickFix.addImportAsText(element, moduleDefinition);

                ModuleName importModuleName = importAction != null ? importAction.getModuleName() : null;
                String textToInsert = moduleDefinition.textToInsert(importModuleName, element);
                int endOffset = context.getEditor().getCaretModel().getOffset();

                context.getDocument().replaceString(
                    range.getStartOffset(),
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

    private static LookupElementBuilder createLookupElementBuilder(
            @NotNull C3ModuleDefinition moduleDefinition,
            @NotNull C3CallablePsiElement element,
            @NotNull FullyQualifiedName fqName,
            @NotNull InsertHandler<LookupElement> insertHandler)
    {
        Icon icon = null;
        if (element instanceof C3FuncDef)
        {
            icon = C3Icons.Nodes.FUNCTION;
        }
        else if (element instanceof C3MacroDefinition)
        {
            icon = C3Icons.Nodes.MACRO;
        }

        List<String> parameters = new ArrayList<>();
        for (ParamType parameterType : element.getParameterTypes())
        {
            List<String> parts = new ArrayList<>();
            ShortType type = parameterType.getType();
            if (type != null) parts.add(type.getFullName());
            parts.add(parameterType.getName());
            parameters.add(String.join(" ", parts));
        }
        String parameterList = String.join(",", parameters);

        ModuleName moduleToUse = moduleDefinition.getVisibleModulePrefix(element.getModuleName());
        if (moduleToUse == null)
        {
            moduleToUse = element.getModuleName();
        }
        String textToInsert = moduleDefinition.textToInsert(moduleToUse, element);
        ShortType returnType = element.getReturnType();

        return LookupElementBuilder.create(element, textToInsert)
            .withLookupStrings(List.of(
                fqName.getFullName(),
                fqName.getSuffixName(),
                fqName.getName()
            ))
            .withIcon(icon)
            .withPresentableText(textToInsert)
            .appendTailText("(" + parameterList + ")", false)
            .withTypeText(returnType != null ? returnType.getFullName() : "")
            .withInsertHandler(insertHandler);
    }

    private static boolean isValidParameterValue(PsiElement element)
    {
        if (element == null) return false;

        if (PsiTreeUtil.getParentOfType(element, C3FnParameterList.class) != null)
        {
            if (PsiTreeUtil.getParentOfType(element, C3ParamDecl.class) == null)
            {
                return true;
            }
            if (element.getNode().getElementType() == C3Types.IDENT
                && PsiTreeUtil.getParentOfType(element, C3PathIdentExpr.class) == null)
            {
                return false;
            }
        }

        return true;
    }
}
