package org.c3lang.intellij.wizard;

import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.dsl.builder.Panel;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class C3NewProjectWizard implements LanguageGeneratorNewProjectWizard
{
    private static final Log LOG = LogFactory.getLog(C3NewProjectWizard.class);

    @Override
    public int getOrdinal()
    {
        return 10;
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
        private final TextFieldWithBrowseButton stdlibPath;

        private Step(NewProjectWizardStep parent)
        {
            super(parent);
            this.parent = parent;
            this.stdlibPath = createStdlibPathField();
        }

        @Override
        public void setupUI(@NotNull Panel builder)
        {
            builder.row("Path to C3 stdlib:", row -> {
                row.cell(stdlibPath);
                return null;
            });
        }

        @Override
        public void setupProject(@NotNull Project project)
        {
            Validate.notNull(project.getBasePath(), "Project base path cannot be null");

            WebModuleBuilder<?> builder = new WebModuleBuilder<>();
            builder.setName(parent.getContext().getProjectName());
            builder.setContentEntryPath(parent.getContext().getProjectFileDirectory());
            builder.commit(project);

            try
            {
                C3ProjectGenerator.generateProject(Path.of(project.getBasePath()), project.getName());
            }
            catch (IOException e)
            {
                LOG.error(e.getMessage(), e);
            }

			C3SettingsState.getInstance().stdlibPath = stdlibPath.getText();
        }

        private static @NotNull TextFieldWithBrowseButton createStdlibPathField()
        {
            Project project = null;
            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            if (openProjects.length > 0)
            {
                project = openProjects[0];
            }

            TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();
            field.setText(C3SettingsState.getInstance().stdlibPath);
            field.setPreferredSize(new Dimension(500, field.getPreferredSize().height));
            field.addBrowseFolderListener(project, new FileChooserDescriptor(false, true, false, false, false, false));
            return field;
        }
    }
}
