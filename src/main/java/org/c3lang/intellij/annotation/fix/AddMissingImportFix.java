package org.c3lang.intellij.annotation.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddMissingImportFix implements LocalQuickFix
{
    @SuppressWarnings("unused")
    private final String importName;

    public AddMissingImportFix(@NotNull String importName)
    {
        this.importName = importName;
    }

    @NotNull
    @Override
    public String getFamilyName()
    {
        return "Add import";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor)
    {
//        C3ImportDecl lastImport = module.getChildrenOfType(C3ImportDecl.class).last();
    }
}
