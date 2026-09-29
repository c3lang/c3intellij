package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3Util;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.intention.AddImportQuickFix;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3BaseType;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3Path;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeFullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.stubs.C3TypeEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public final class TypeCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final TypeCompletionContributor INSTANCE = new TypeCompletionContributor();

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getInstance(TypeCompletionContributor.class);

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3PathIdent.class),
        psiElement().inside(C3Path.class),
        psiElement().inside(C3Type.class),
        psiElement().inside(C3BaseType.class),
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
        if (com.intellij.openapi.project.DumbService.isDumb(parameters.getPosition().getProject())) return;
        if (!PATTERN.accepts(parameters.getPosition()) && !PATTERN.accepts(parameters.getOriginalPosition()))
        {
            return;
        }

        PsiElement pos = parameters.getPosition();
        C3Path path = findPrecedingPath(pos);

        C3ModuleDefinition moduleDefinition = CompletionExtensionsKt.getModuleDefinition(parameters);

        if (path != null)
        {
            handlePathCompletion(parameters, path, moduleDefinition, result);
            return;
        }

        handleUnqualifiedCompletion(parameters, moduleDefinition, result);
    }

    private static @Nullable C3Path findPrecedingPath(@NotNull PsiElement pos)
    {
        C3PathIdent pathIdent = PsiTreeUtil.getParentOfType(pos, C3PathIdent.class);
        if (pathIdent != null && pathIdent.getPath() != null)
        {
            return pathIdent.getPath();
        }
        C3PathIdentExpr pie = PsiTreeUtil.getParentOfType(pos, C3PathIdentExpr.class);
        if (pie != null && pie.getPathIdent() != null && pie.getPathIdent().getPath() != null)
        {
            return pie.getPathIdent().getPath();
        }
        C3Type c3Type = PsiTreeUtil.getParentOfType(pos, C3Type.class);
        if (c3Type != null && c3Type.getBaseType() != null && c3Type.getBaseType().getPath() != null)
        {
            return c3Type.getBaseType().getPath();
        }
        C3Path directPath = PsiTreeUtil.getParentOfType(pos, C3Path.class);
        if (directPath != null)
        {
            return directPath;
        }
        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(pos);
        if (prevLeaf != null && prevLeaf.getNode() != null && prevLeaf.getNode().getElementType() == C3Types.SCOPE)
        {
            return PsiTreeUtil.getParentOfType(prevLeaf, C3Path.class);
        }
        return null;
    }

    private void handlePathCompletion(
            @NotNull CompletionParameters parameters,
            @NotNull C3Path path,
            @Nullable C3ModuleDefinition moduleDefinition,
            @NotNull CompletionResultSet result)
    {
        String pathText = sanitizePathText(path.getText());
        int pathEnd = path.getTextRange().getEndOffset();
        int caretOffset = parameters.getOffset();
        String prefix = caretOffset >= pathEnd
            ? parameters.getEditor().getDocument().getText(new TextRange(pathEnd, caretOffset))
            : "";
        prefix = sanitizePrefix(prefix);

        String qualifier = pathText.endsWith("::") ? pathText.substring(0, pathText.length() - 2) : pathText;
        var project = parameters.getPosition().getProject();

        List<String> targetModules = new ArrayList<>();
        if (moduleDefinition != null)
        {
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
        }

        for (String mod : C3Util.INSTANCE.findC3ModulesStartingWith(project, qualifier))
        {
            if (mod.equals(qualifier) && !targetModules.contains(mod))
            {
                targetModules.add(mod);
            }
        }
        targetModules = new ArrayList<>(new java.util.LinkedHashSet<>(targetModules));

        CompletionResultSet scopedResult = result.withPrefixMatcher(prefix);
        // The same entity can be indexed from several files (e.g. test fixtures
        // shadowing the real stdlib): suggest each fully qualified name once.
        Set<String> addedTypeFqns = new java.util.HashSet<>();

        // Always suggest child submodules so chains like
        // std::collections::map::HashMap keep completing at every level,
        // even when the qualifier itself is an exact module.
        Set<String> submodules = new TreeSet<>();
        String qualPrefix = qualifier + "::";
        for (String mod : C3Util.INSTANCE.findC3ModulesStartingWith(project, qualPrefix))
        {
            if (mod.startsWith(qualPrefix))
            {
                String remainder = mod.substring(qualPrefix.length());
                int nextScope = remainder.indexOf("::");
                String nextSegment = nextScope >= 0 ? remainder.substring(0, nextScope) : remainder;
                if (!nextSegment.isEmpty())
                {
                    submodules.add(nextSegment);
                }
            }
        }
        for (String submod : submodules)
        {
            LookupElementBuilder builder = LookupElementBuilder.create(submod)
                .withIcon(C3Icons.Nodes.MODULE)
                .withPresentableText(submod)
                .withTypeText("module")
                .withInsertHandler(SUBMODULE_INSERT_HANDLER);
            scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 15.0));
        }

        if (!targetModules.isEmpty())
        {
            for (String modName : targetModules)
            {
                String modulePrefix = modName + "::";
                int addedBefore = addedTypeFqns.size();
                for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
                {
                    if (!key.startsWith(modulePrefix)) continue;
                    String remainder = key.substring(modulePrefix.length());
                    if (remainder.contains("::")) continue;

                    for (C3PsiElement element : StubIndex.getElements(
                            NameIndex.KEY,
                            key,
                            project,
                            C3ProjectService.getInstance(project).getSearchScope(),
                            C3PsiElement.class))
                    {
                        if (!(element instanceof C3TypeFullyQualifiedNamePsiElement typeName)) continue;
                        String name = typeName.getFqName().getName();
                        if (name == null || name.isEmpty()) continue;
                        if (!addedTypeFqns.add(typeName.getFqName().getFullName())) continue;

                        Icon icon = iconFor(typeName.getTypeEnum());
                        LookupElementBuilder builder = LookupElementBuilder.create(typeName, name)
                            .withIcon(icon)
                            .withPresentableText(name)
                            .withTypeText(modName);

                        scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 10.0));
                    }
                }
                if (addedTypeFqns.size() == addedBefore)
                {
                    // The index has no types for this module (stale or broken index):
                    // read the module file directly so qualified completion keeps working.
                    addModuleFileTypes(project, modName, modulePrefix, scopedResult, addedTypeFqns);
                }
            }
        }
        else if (!submodules.isEmpty() || !qualPrefix.isBlank())
        {
            // Qualifier is a strict prefix (e.g. "std" or "std::collections"):
            // suggest types in nested modules with their relative path.
            for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
            {
                if (!key.startsWith(qualPrefix)) continue;
                String remainder = key.substring(qualPrefix.length());

                for (C3PsiElement element : StubIndex.getElements(
                        NameIndex.KEY,
                        key,
                        project,
                        C3ProjectService.getInstance(project).getSearchScope(),
                        C3PsiElement.class))
                {
                    if (!(element instanceof C3TypeFullyQualifiedNamePsiElement typeName)) continue;
                    String name = typeName.getFqName().getName();
                    if (name == null || name.isEmpty()) continue;
                    if (!addedTypeFqns.add(typeName.getFqName().getFullName())) continue;

                    Icon icon = iconFor(typeName.getTypeEnum());
                    LookupElementBuilder builder = LookupElementBuilder.create(typeName, remainder)
                        .withIcon(icon)
                        .withPresentableText(name)
                        .withTailText(" (" + typeName.getFqName().getModule() + ")", true)
                        .withLookupStrings(List.of(name, remainder));

                    scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 8.0));
                }
            }
        }
    }

    private static void addModuleFileTypes(
            @NotNull com.intellij.openapi.project.Project project,
            @NotNull String modName,
            @NotNull String modulePrefix,
            @NotNull CompletionResultSet scopedResult,
            @NotNull Set<String> addedTypeFqns)
    {
        for (C3TypeName typeName : InterfaceService.INSTANCE.findModuleTypeDeclarations(modName, project))
        {
            FullyQualifiedName fqName = typeName.getFqName();
            if (!fqName.getFullName().startsWith(modulePrefix)) continue;
            String remainder = fqName.getFullName().substring(modulePrefix.length());
            if (remainder.contains("::")) continue;
            String name = fqName.getName();
            if (name == null || name.isEmpty()) continue;
            if (!addedTypeFqns.add(fqName.getFullName())) continue;

            Icon icon = iconFor(typeName.getTypeEnum());
            LookupElementBuilder builder = LookupElementBuilder.create(typeName, name)
                .withIcon(icon)
                .withPresentableText(name)
                .withTypeText(modName);

            scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 10.0));
        }
    }

    private static @NotNull String sanitizePathText(@NotNull String pathText)
    {
        String clean = pathText.replace(CompletionExtensionsKt.DUMMY_IDENTIFIER, "")
            .replace("dummy", "")
            .strip();
        return clean;
    }

    private static @NotNull String sanitizePrefix(@NotNull String prefix)
    {
        int dummy = prefix.indexOf("dummy");
        if (dummy >= 0) prefix = prefix.substring(0, dummy);
        return prefix.strip();
    }

    private static final InsertHandler<LookupElement> SUBMODULE_INSERT_HANDLER = (insertionContext, item) -> {
        int end = insertionContext.getTailOffset();
        CharSequence chars = insertionContext.getDocument().getCharsSequence();
        if (end + 1 < chars.length() && chars.charAt(end) == ':' && chars.charAt(end + 1) == ':')
        {
            insertionContext.getEditor().getCaretModel().moveToOffset(end + 2);
        }
        else
        {
            insertionContext.getDocument().insertString(end, "::");
            insertionContext.getEditor().getCaretModel().moveToOffset(end + 2);
        }
        com.intellij.codeInsight.AutoPopupController.getInstance(insertionContext.getProject())
            .scheduleAutoPopup(insertionContext.getEditor());
    };

    private void handleUnqualifiedCompletion(
            @NotNull CompletionParameters parameters,
            @Nullable C3ModuleDefinition moduleDefinition,
            @NotNull CompletionResultSet result)
    {
        var project = parameters.getPosition().getProject();

        for (String primType : C3KeywordCompletionContributor.PRIMITIVE_TYPES)
        {
            if (result.getPrefixMatcher().prefixMatches(primType))
            {
                result.addElement(PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(primType)
                        .bold()
                        .withTypeText("primitive type"),
                    12.0
                ));
            }
        }

        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (key.isBlank()) continue;

            for (C3PsiElement element : StubIndex.getElements(
                    NameIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class))
            {
                if (!(element instanceof C3TypeFullyQualifiedNamePsiElement typeName)) continue;

                FullyQualifiedName fqName = typeName.getFqName();
                String name = fqName.getName();
                if (name == null || name.isEmpty()) continue;

                Icon icon = iconFor(typeName.getTypeEnum());
                boolean isCurrentOrImported = false;
                if (moduleDefinition != null)
                {
                    if (moduleDefinition.isSameModule(typeName) ||
                        moduleDefinition.getVisibleModulePrefix(fqName.getModule()) != null)
                    {
                        isCurrentOrImported = true;
                    }
                }

                LookupElementBuilder builder = LookupElementBuilder.create(typeName, name)
                    .withIcon(icon)
                    .withPresentableText(name)
                    .withTailText(" (" + fqName.getModule() + ")", true)
                    .withLookupStrings(List.of(name, fqName.getFullName()));

                if (!isCurrentOrImported)
                {
                    builder = builder.withInsertHandler(TypeInsertHandler.INSTANCE);
                }

                double priority = isCurrentOrImported ? 10.0 : 7.0;
                result.addElement(PrioritizedLookupElement.withPriority(builder, priority));
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

    private static final class TypeInsertHandler implements InsertHandler<LookupElement>
    {
        public static final TypeInsertHandler INSTANCE = new TypeInsertHandler();

        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item)
        {
            PsiElement psiElement = item.getPsiElement();
            if (!(psiElement instanceof C3TypeName element)) return;

            ModuleName moduleName = element.getModuleName();
            if (moduleName == null) return;

            PsiFile file = context.getFile();
            PsiElement atOffset = file.findElementAt(context.getStartOffset());
            C3ModuleDefinition moduleSection = atOffset != null
                ? PsiTreeUtil.getParentOfType(atOffset, C3ModuleDefinition.class)
                : PsiTreeUtil.findChildOfType(file, C3ModuleDefinition.class);

            if (moduleSection == null) return;
            if (moduleSection.isSameModule(element)) return;
            if (moduleSection.getVisibleModulePrefix(moduleName) != null) return;

            AddImportQuickFix.ImportAction importAction =
                AddImportQuickFix.addImportAsText(moduleName, moduleSection);

            if (importAction != null && !(importAction instanceof AddImportQuickFix.ImportAction.Imported))
            {
                importAction.write(context.getDocument());
                PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
            }
        }
    }
}
