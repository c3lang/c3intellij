package org.c3lang.intellij.intention;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.C3Visitor;
import org.jetbrains.annotations.NotNull;

public class LogReferencesInspection extends LocalInspectionTool
{
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly)
    {
        return new C3Visitor()
        {
            @Override
            public void visitElement(@NotNull PsiElement psi)
            {
                if (psi instanceof C3Arg || psi instanceof C3LocalDeclarationStmt)
                {
                    holder.registerProblem(
                        psi,
                        "Log references of " + psi.getText(),
                        ProblemHighlightType.INFORMATION,
                        LogReferencesQuickFix.INSTANCE
                    );
                }
            }
        };
    }

    public static final class LogReferencesQuickFix implements LocalQuickFix
    {
        public static final LogReferencesQuickFix INSTANCE = new LogReferencesQuickFix();
        private static final Logger LOG = Logger.getInstance(LogReferencesQuickFix.class);

        private LogReferencesQuickFix()
        {
        }

        @Override
        public @NotNull String getFamilyName()
        {
            return "Log references";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
        {
            Document document = PsiDocumentManager.getInstance(project).getDocument(descriptor.getPsiElement().getContainingFile());
            if (document != null)
            {
                document.getLineNumber(descriptor.getPsiElement().getTextOffset());
            }

            ReadAction.run(() -> {
                var refs = ReferencesSearch.search(descriptor.getPsiElement()).findAll();
                LOG.debug("#2 " + refs.size());
            });

            ApplicationManager.getApplication().runReadAction(() -> {
                var refs = ReferencesSearch.search(descriptor.getPsiElement()).findAll();
                LOG.debug("#3 " + refs.size());
            });
        }
    }
}
