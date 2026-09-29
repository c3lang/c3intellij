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
import com.intellij.psi.PsiFile;
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
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3Path;
import org.c3lang.intellij.psi.C3PathAtIdentExpr;
import org.c3lang.intellij.psi.C3PathIdent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
        psiElement().inside(C3PathAtIdentExpr.class),
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
        if (com.intellij.openapi.project.DumbService.isDumb(parameters.getPosition().getProject())) return;
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

        C3PathAtIdentExpr atLookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3PathAtIdentExpr.class);
        if (atLookupTarget != null)
        {
            addAtMacroCompletions(parameters, atLookupTarget, moduleDefinition, result);
            return;
        }

        C3PathIdentExpr lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3PathIdentExpr.class);
        if (lookupTarget == null) return;

        C3PathIdent pathIdent = lookupTarget.getPathIdent();
        C3Path path = pathIdent != null ? pathIdent.getPath() : null;
        var project = parameters.getPosition().getProject();

        if (path != null)
        {
            String pathText = path.getText();
            int pathEnd = path.getTextRange().getEndOffset();
            int caretOffset = parameters.getOffset();
            String prefix = caretOffset >= pathEnd
                ? parameters.getEditor().getDocument().getText(new TextRange(pathEnd, caretOffset))
                : "";

            String qualifier = pathText.endsWith("::") ? pathText.substring(0, pathText.length() - 2) : pathText;

            List<String> targetModules = new ArrayList<>();
            for (ModuleName imported : moduleDefinition.getImports())
            {
                if (imported.getSuffix().equals(qualifier) || imported.getValue().equals(qualifier))
                {
                    targetModules.add(imported.getValue());
                }
            }
            ModuleName currentMod = moduleDefinition.getModuleName();
            if (currentMod != null && (currentMod.getSuffix().equals(qualifier) || currentMod.getValue().equals(qualifier)))
            {
                targetModules.add(currentMod.getValue());
            }
            if (targetModules.isEmpty())
            {
                targetModules.add(qualifier);
            }
            targetModules = new ArrayList<>(new java.util.LinkedHashSet<>(targetModules));

            CompletionResultSet scopedResult = result.withPrefixMatcher(prefix);

            for (String modName : targetModules)
            {
                String modulePrefix = modName + "::";
                int addedCallables = 0;
                for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
                {
                    if (!key.startsWith(modulePrefix)) continue;
                    String remainder = key.substring(modulePrefix.length());
                    if (remainder.contains("::")) continue;

                    for (C3PsiElement psiElement : StubIndex.getElements(
                            NameIndex.KEY,
                            key,
                            project,
                            C3ProjectService.getInstance(project).getSearchScope(),
                            C3PsiElement.class))
                    {
                        if (psiElement instanceof C3CallablePsiElement element)
                        {
                            String name = element.getFqName().getName();
                            if (name == null || name.isEmpty()) continue;

                            scopedResult.addElement(PrioritizedLookupElement.withPriority(
                                callableBuilder(element, name), 10.0));
                            addedCallables++;
                        }
                        else if (psiElement instanceof C3ConstDeclarationStmt constDecl)
                        {
                            // NOTE: types in qualified position are provided by
                            // TypeCompletionContributor; suggesting them here too
                            // would show every type twice.
                            String name = constDecl.getFqName().getName();
                            if (name == null || name.isEmpty()) continue;
                            LookupElementBuilder builder = LookupElementBuilder.create(constDecl, name)
                                .withIcon(C3Icons.Nodes.CONSTANT)
                                .withPresentableText(name);
                            scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 8.0));
                        }
                    }
                }
                if (addedCallables == 0)
                {
                    // The index has no callables for this module (stale or broken index):
                    // read the module file directly so qualified completion keeps working.
                    addModuleFileCallables(project, modName, modulePrefix, scopedResult);
                }
            }
            return;
        }

        String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
        var matcher = CompletionExtensionsKt.getMatcher(lookupString);
        TextRange elementRange = lookupTarget.getTextRange();
        String containingFileName = parameters.getPosition().getContainingFile().getName();
        InsertHandler<LookupElement> insertHandler = FunctionInsertHandler.INSTANCE;

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

    private void addAtMacroCompletions(
            @NotNull CompletionParameters parameters,
            @NotNull C3PathAtIdentExpr lookupTarget,
            @NotNull C3ModuleDefinition moduleDefinition,
            @NotNull CompletionResultSet result)
    {
        var project = parameters.getPosition().getProject();

        String fullText = lookupTarget.getText();
        int startOffset = lookupTarget.getTextRange().getStartOffset();
        int caretOffset = parameters.getOffset();
        String prefix = caretOffset >= startOffset && caretOffset - startOffset <= fullText.length()
            ? fullText.substring(0, caretOffset - startOffset)
            : "";
        prefix = prefix.replace(CompletionExtensionsKt.DUMMY_IDENTIFIER, "").strip();
        CompletionResultSet scopedResult = result.withPrefixMatcher(prefix);

        Set<String> added = new TreeSet<>();
        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            int at = key.indexOf('@');
            if (at < 0) continue;
            String name = key.substring(at);
            if (!name.startsWith("@")) continue;
            for (C3PsiElement psiElement : StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class))
            {
                if (!(psiElement instanceof C3MacroDefinition macro)) continue;
                if (!moduleDefinition.containsImportOrSameModule(macro)) continue;
                if (!added.add(macro.getFqName().getFullName())) continue;

                LookupElementBuilder builder = LookupElementBuilder.create(macro, name)
                    .withIcon(C3Icons.Nodes.MACRO)
                    .withPresentableText(name)
                    .withTypeText(macro.getReturnType() != null ? macro.getReturnType().getFullName() : "")
                    .withInsertHandler((insertionContext, item) -> {
                        int end = insertionContext.getTailOffset();
                        CharSequence chars = insertionContext.getDocument().getCharsSequence();
                        if (end < chars.length() && chars.charAt(end) == '(')
                        {
                            insertionContext.getEditor().getCaretModel().moveToOffset(end + 1);
                        }
                        else
                        {
                            insertionContext.getDocument().insertString(end, "()");
                            insertionContext.getEditor().getCaretModel().moveToOffset(end + 1);
                        }
                    });
                scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 10.0));
            }
        }
    }

    private static @NotNull LookupElementBuilder callableBuilder(
            @NotNull C3CallablePsiElement element,
            @NotNull String name)
    {
        Icon icon = element instanceof C3MacroDefinition ? C3Icons.Nodes.MACRO : C3Icons.Nodes.FUNCTION;
        List<String> parametersList = new ArrayList<>();
        for (ParamType pt : element.getParameterTypes())
        {
            List<String> parts = new ArrayList<>();
            ShortType t = pt.getType();
            if (t != null) parts.add(t.getFullName());
            parts.add(pt.getName());
            parametersList.add(String.join(" ", parts));
        }
        String paramStr = String.join(", ", parametersList);
        boolean noParams = parametersList.isEmpty();

        return LookupElementBuilder.create(element, name)
            .withIcon(icon)
            .withPresentableText(name)
            .appendTailText("(" + paramStr + ")", false)
            .withTypeText(element.getReturnType() != null ? element.getReturnType().getFullName() : "")
            .withInsertHandler((insertionContext, item) -> {
                int end = insertionContext.getTailOffset();
                CharSequence chars = insertionContext.getDocument().getCharsSequence();
                if (end < chars.length() && chars.charAt(end) == '(')
                {
                    insertionContext.getEditor().getCaretModel().moveToOffset(end + 1);
                }
                else
                {
                    insertionContext.getDocument().insertString(end, "()");
                    insertionContext.getEditor().getCaretModel().moveToOffset(noParams ? end + 2 : end + 1);
                }
            });
    }

    private static void addModuleFileCallables(
            @NotNull com.intellij.openapi.project.Project project,
            @NotNull String modName,
            @NotNull String modulePrefix,
            @NotNull CompletionResultSet scopedResult)
    {
        for (C3CallablePsiElement element :
            org.c3lang.intellij.index.InterfaceService.INSTANCE.findModuleCallables(modName, project))
        {
            String fullName = element.getFqName().getFullName();
            if (!fullName.startsWith(modulePrefix)) continue;
            String remainder = fullName.substring(modulePrefix.length());
            if (remainder.contains("::")) continue;
            String name = element.getFqName().getName();
            if (name == null || name.isEmpty()) continue;
            scopedResult.addElement(PrioritizedLookupElement.withPriority(callableBuilder(element, name), 10.0));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class FunctionInsertHandler implements InsertHandler<LookupElement>
    {
        public static final FunctionInsertHandler INSTANCE = new FunctionInsertHandler();

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
        {
            PsiElement psiElement = item.getPsiElement();
            if (!(psiElement instanceof C3CallablePsiElement element)) return;

            int end = context.getTailOffset();
            CharSequence chars = context.getDocument().getCharsSequence();
            boolean hasParams = !element.getParameterTypes().isEmpty();
            if (end < chars.length() && chars.charAt(end) == '(')
            {
                context.getEditor().getCaretModel().moveToOffset(end + 1);
            }
            else
            {
                context.getDocument().insertString(end, "()");
                context.getEditor().getCaretModel().moveToOffset(hasParams ? end + 1 : end + 2);
            }

            ModuleName funcModule = element.getModuleName();
            if (funcModule == null) return;

            PsiFile file = context.getFile();
            PsiElement atOffset = file.findElementAt(context.getStartOffset());
            C3ModuleDefinition moduleDef = atOffset != null
                ? PsiTreeUtil.getParentOfType(atOffset, C3ModuleDefinition.class)
                : PsiTreeUtil.findChildOfType(file, C3ModuleDefinition.class);

            if (moduleDef == null) return;
            if (moduleDef.isSameModule(element)) return;
            if (moduleDef.getVisibleModulePrefix(funcModule) != null) return;

            AddImportQuickFix.ImportAction importAction =
                AddImportQuickFix.addImportAsText(funcModule, moduleDef);

            if (importAction != null && !(importAction instanceof AddImportQuickFix.ImportAction.Imported))
            {
                importAction.write(context.getDocument());
                com.intellij.psi.PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
            }
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
