package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import kotlin.Pair;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.AccessPath;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3InitializerList;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PathNameProvider;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public final class InitializerListCompletionContributor extends CompletionProvider<CompletionParameters>
{
    public static final InitializerListCompletionContributor INSTANCE = new InitializerListCompletionContributor();

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getInstance(InitializerListCompletionContributor.class);

    private static final ElementPattern<PsiElement> PATTERN = or(
        psiElement().inside(C3InitializerList.class)
    );

    private InitializerListCompletionContributor()
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

        C3PsiElement lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3AccessIdent.class);
        if (lookupTarget == null)
        {
            lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3PathIdent.class);
        }
        if (lookupTarget == null) return;

        String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
        FullyQualifiedName rootType = CompletionExtensionsKt.getRootType(lookupTarget);
        if (rootType == null) return;

        C3Arg parentArg = PsiTreeUtil.getParentOfType(lookupTarget, C3Arg.class);
        C3PathNameProvider pathProvider = PsiTreeUtil.getParentOfType(lookupTarget, C3PathNameProvider.class);
        if (pathProvider == null) return;

        List<String> path = pathProvider.findPathName(false);
        List<String> fieldNames = new ArrayList<>();
        C3PathNameProvider currentProvider = parentArg != null
            ? PsiTreeUtil.getParentOfType(parentArg, C3PathNameProvider.class)
            : null;
        while (currentProvider != null)
        {
            fieldNames.addAll(currentProvider.findPathName(false));
            currentProvider = PsiTreeUtil.getParentOfType(currentProvider, C3PathNameProvider.class);
        }

        List<String> paths = new ArrayList<>();
        Collections.reverse(fieldNames);
        paths.addAll(fieldNames);
        paths.addAll(path);
        paths.add(lookupString);

        List<Pair<AccessPath, String>> fields =
            StructService.INSTANCE.getFields(rootType, paths, parameters.getPosition().getProject());
        String lastPath = paths.get(paths.size() - 1);

        for (Pair<AccessPath, String> field : fields)
        {
            AccessPath accessPath = field.getFirst();
            if (!accessPath.getName().startsWith(lastPath)) continue;

            result.addElement(
                LookupElementBuilder.create(accessPath.getName())
                    .withPresentableText(accessPath.getName())
                    .withIcon(C3Icons.Nodes.STRUCT_FIELD)
                    .withTypeText(field.getSecond())
            );
        }
    }
}
