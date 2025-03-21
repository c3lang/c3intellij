package org.c3lang.intellij.wizard;

import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

import static com.intellij.ide.wizard.UIWizardUtil.setupProjectFromBuilder;

public class C3NewProjectWizard implements LanguageGeneratorNewProjectWizard
{
    private static final Log log = LogFactory.getLog(C3NewProjectWizard.class);

    @Override
    public int getOrdinal()
    {
        return 0;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "C3 Project";
    }

    @Override
    public @NotNull Icon getIcon()
    {
        return C3Icons.LOGO;
    }

    @Override
    public @NotNull NewProjectWizardStep createStep(@NotNull NewProjectWizardStep newProjectWizardStep)
    {
        return new Step(newProjectWizardStep);
    }

    static class Step extends AbstractNewProjectWizardStep
    {
        private final NewProjectWizardStep parent;

        public Step(NewProjectWizardStep parent)
        {
            super(parent);

            this.parent = parent;
        }

        @Override
        public void setupProject(@NotNull Project project)
        {
            String[] dirs = new String[] {
                    "build",
                    "docs",
                    "lib",
                    "resources",
                    "scripts",
                    "src",
                    "test"
            };

            File licenseFilePath = Path.of(Objects.requireNonNull(project.getBasePath()), "LICENSE").toFile();
            File readmeFilePath = Path.of(Objects.requireNonNull(project.getBasePath()), "README.md").toFile();
            File projectJsonFilePath = Path.of(Objects.requireNonNull(project.getBasePath()), "project.json").toFile();
            File mainFilePath = Path.of(Objects.requireNonNull(project.getBasePath()), "src/main.c3").toFile();

            WebModuleBuilder<?> builder = new WebModuleBuilder<>();
            builder.setName(parent.getContext().getProjectName());
            builder.setContentEntryPath(parent.getContext().getProjectFileDirectory());
            setupProjectFromBuilder(this, project, builder);

            StartupManager startupManager = StartupManager.getInstance(project);
            startupManager.runAfterOpened(() -> {
                String contentEntryPath = builder.getContentEntryPath();
                if (contentEntryPath == null) {
                    return;
                }
                VirtualFile root = LocalFileSystem.getInstance().findFileByPath(contentEntryPath);
                if (root == null) {
                    return;
                }

                WriteCommandAction.runWriteCommandAction(project, null, null, () -> {
                    PsiDirectory directory = PsiManager.getInstance(project).findDirectory(root);
                    if (directory != null) {
                        for (String dir : dirs) {
                            FileUtil.createDirectory(Path.of(Objects.requireNonNull(project.getBasePath()), dir).toFile());
                        }

                        try {
                            FileUtil.writeToFile(licenseFilePath, "");
                            FileUtil.writeToFile(readmeFilePath, "");
                            C3Util.writeToFile(project.getName(), "templates/project.json", projectJsonFilePath);
                            C3Util.writeToFile(project.getName(), "templates/main", mainFilePath);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
            });
        }
    }
}