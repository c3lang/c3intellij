package org.c3lang.intellij.intention;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.completion.ImportCompletionContributor;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3Path;
import org.c3lang.intellij.psi.C3Visitor;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImportModuleInspection extends LocalInspectionTool
{
    private static final Logger LOG = Logger.getInstance(ImportCompletionContributor.class);

    @Override
    public @NotNull String getDisplayName()
    {
        return "Import module";
    }

    @Override
    public @NotNull String getGroupDisplayName()
    {
        return "Import";
    }

    @Override
    public @NotNull PsiElementVisitor buildVisitor(
            @NotNull ProblemsHolder holder,
            boolean isOnTheFly,
            @NotNull LocalInspectionToolSession session)
    {
        if (!(session.getFile() instanceof C3File))
        {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new C3Visitor()
        {
            @Override
            public void visitPath(@NotNull C3Path o)
            {
                registerPathProblem(o);
            }

            private void registerPathProblem(@NotNull C3Path psi)
            {
                PsiElement problemElement = psi.getParent();
                Collection<C3FullyQualifiedNamePsiElement> callables =
                    NameIndexService.INSTANCE.findByNameEndsWith(problemElement.getText(), psi.getProject());
                List<C3FullyQualifiedNamePsiElement> callableList = new ArrayList<>(callables);
                C3FullyQualifiedNamePsiElement element = callableList.size() == 1 ? callableList.get(0) : null;

                if (element == null)
                {
                    String details = String.join(", ", callableList.stream()
                        .map(callable -> callable.getFqName().getFullName())
                        .toList());
                    LOG.debug("Ignoring QuickFix, callables found: " + details);
                    return;
                }

                C3ModuleDefinition importProvider = java.util.Objects.requireNonNull(
                    PsiTreeUtil.getParentOfType(psi, C3ModuleDefinition.class)
                );

                ModuleName importIntention = element.getFqName().getModule();
                if (importIntention == null) return;

                if (importIntention.getValue().equals("std::core") || importIntention.getValue().startsWith("std::core::"))
                {
                    return;
                }

                if (importProvider.containsImportOrSameModule(element)) return;

                ModuleName applied = psi.getUserData(AddImportQuickFix.KEY);
                psi.putUserData(AddImportQuickFix.KEY, null);
                if (applied != null) return;

                holder.registerProblem(
                    problemElement,
                    "Import " + importIntention.getValue(),
                    ProblemHighlightType.GENERIC_ERROR,
                    new AddImportQuickFix(problemElement, importIntention)
                );
            }
        };
    }
}
