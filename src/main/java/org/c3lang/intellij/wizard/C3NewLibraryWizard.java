package org.c3lang.intellij.wizard;

import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class C3NewLibraryWizard implements LanguageGeneratorNewProjectWizard
{
    private static final Log log = LogFactory.getLog(C3NewLibraryWizard.class);

    @Override
    public int getOrdinal()
    {
        return 0;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "C3 Library";
    }

    @Override
    public @NotNull Icon getIcon()
    {
        return C3Icons.LIB_FILE;
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
            Validate.notNull(project.getBasePath(), "Project base path cannot be null");

            String[] dirs = new String[]{
                    "freebsd-x64",
                    "linux-aarch64",
                    "linux-riscv32",
                    "linux-riscv64",
                    "linux-x64",
                    "linux-x86",
                    "macos-aarch64",
                    "macos-x64",
                    "netbsd-x64",
                    "openbsd-x64",
                    "wasm32",
                    "wasm64",
                    "windows-aarch64",
                    "windows-x64",
                    "scripts"
            };

            String moduleName = project.getName().replaceAll(" ", "_").toLowerCase();
            File licenseFilePath = Path.of(project.getBasePath(), "LICENSE").toFile();
            File readmeFilePath = Path.of(project.getBasePath(), "README.md").toFile();
            File manifestJsonFilePath = Path.of(project.getBasePath(), "manifest.json").toFile();
            File moduleFilePath = Path.of(project.getBasePath(), moduleName + ".c3i").toFile();

            WebModuleBuilder<?> builder = new WebModuleBuilder<>();
            builder.setName(parent.getContext().getProjectName());
            builder.setContentEntryPath(parent.getContext().getProjectFileDirectory());
            builder.commit(project);

            for (String dir : dirs)
            {
                FileUtil.createDirectory(Path.of(project.getBasePath(), dir).toFile());
            }

            try
            {
                FileUtil.writeToFile(licenseFilePath, "");
                FileUtil.writeToFile(readmeFilePath, "");
                C3Util.writeToFile(moduleName, "templates/library", moduleFilePath);
                C3Util.writeToFile(moduleName, "templates/manifest.json", manifestJsonFilePath);
            } catch (IOException e)
            {
                log.error(e.getMessage(), e);
            }
        }
    }
}