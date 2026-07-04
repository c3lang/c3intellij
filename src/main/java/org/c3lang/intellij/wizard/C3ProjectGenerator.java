package org.c3lang.intellij.wizard;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.ide.util.projectWizard.WebTemplateNewProjectWizard;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.GeneratorNewProjectWizard;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.GeneratorPeerImpl;
import com.intellij.platform.ProjectGeneratorPeer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3SettingsState;
import org.c3lang.intellij.C3Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class C3ProjectGenerator
{
    private C3ProjectGenerator()
    {
    }

    static void generateProject(@NotNull Path basePath, @NotNull String projectName, C3ProjectType projectType) throws IOException
    {
		String moduleName = C3Util.projectNameToModuleName(projectName);
		String targetName = C3Util.projectNameToTargetName(projectName);
        String[] dirs = new String[]{
            "build",
            "docs",
            "lib",
            "resources",
            "scripts",
            "src",
            "test"
        };

        for (String dir : dirs)
        {
            FileUtil.createDirectory(basePath.resolve(dir).toFile());
        }

        FileUtil.writeToFile(basePath.resolve("LICENSE").toFile(), "");
        FileUtil.writeToFile(basePath.resolve("README.md").toFile(), "");
		switch (projectType)
		{
			case APPLICATION:
				C3Util.INSTANCE.writeToFile(targetName, "templates/project.json", basePath.resolve("project.json").toFile());
				C3Util.INSTANCE.writeToFile(moduleName, "templates/main.c3", basePath.resolve("src/main.c3").toFile());
				break;
			case DYNAMIC_LIBRARY:
				C3Util.INSTANCE.writeToFile(targetName, "templates/project_dynamic.json", basePath.resolve("project.json").toFile());
				C3Util.INSTANCE.writeToFile(moduleName, "templates/lib.c3", basePath.resolve("src/lib.c3").toFile());
			case STATIC_LIBRARY:
				C3Util.INSTANCE.writeToFile(targetName, "templates/project_dynamic.json", basePath.resolve("project.json").toFile());
				C3Util.INSTANCE.writeToFile(moduleName, "templates/lib.c3", basePath.resolve("src/lib.c3").toFile());
		}
    }

    static void generateLibrary(@NotNull Path basePath, @NotNull String projectName) throws IOException
    {
		String moduleName = C3Util.projectNameToModuleName(projectName);
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

        for (String dir : dirs)
        {
            FileUtil.createDirectory(basePath.resolve(dir).toFile());
        }

        FileUtil.writeToFile(basePath.resolve("LICENSE").toFile(), "");
        FileUtil.writeToFile(basePath.resolve("README.md").toFile(), "");
        C3Util.INSTANCE.writeToFile(moduleName, "templates/library", basePath.resolve(moduleName + ".c3i").toFile());
        C3Util.INSTANCE.writeToFile(moduleName, "templates/manifest.json", basePath.resolve("manifest.json").toFile());
    }

    public static class ProjectNewProjectWizard implements GeneratorNewProjectWizard
    {
        private final GeneratorNewProjectWizard delegate = new WebTemplateNewProjectWizard(new ProjectDirectoryGenerator());

        @Override
        public @NotNull String getId()
        {
            return "C3_PROJECT";
        }

        @Override
        public @NotNull String getName()
        {
            return "C3 Project";
        }

        @Override
        public @NotNull Icon getIcon()
        {
            return C3Icons.LOGO;
        }

        @Override
        public String getDescription()
        {
            return "Create a C3 project";
        }

        @Override
        public int getOrdinal()
        {
            return 10;
        }

        @Override
        public @NotNull NewProjectWizardStep createStep(@NotNull WizardContext context)
        {
            return delegate.createStep(context);
        }
    }

    public static class LibraryNewProjectWizard implements GeneratorNewProjectWizard
    {
        private final GeneratorNewProjectWizard delegate = new WebTemplateNewProjectWizard(new LibraryDirectoryGenerator());

        @Override
        public @NotNull String getId()
        {
            return "C3_LIBRARY";
        }

        @Override
        public @NotNull String getName()
        {
            return "C3 Library";
        }

        @Override
        public @NotNull Icon getIcon()
        {
            return C3Icons.LIB_FILE;
        }

        @Override
        public String getDescription()
        {
            return "Create a C3 library";
        }

        @Override
        public int getOrdinal()
        {
            return 20;
        }

        @Override
        public @NotNull NewProjectWizardStep createStep(@NotNull WizardContext context)
        {
            return delegate.createStep(context);
        }
    }

    public static class ProjectDirectoryGenerator extends WebProjectTemplate<C3Settings>
    {
        private static final Log LOG = LogFactory.getLog(ProjectDirectoryGenerator.class);

        @Override
        public @NotNull String getId()
        {
            return "C3_PROJECT";
        }

        @Override
        public @NotNull String getName()
        {
            return "C3 project";
        }

        @Override
        public String getDescription()
        {
            return "Create a C3 project";
        }

        @Override
        public Icon getIcon()
        {
            return C3Icons.LOGO;
        }

        @Override
        public @NotNull ProjectGeneratorPeer<C3Settings> createPeer()
        {
            return new ProjectPeer();
        }

        @Override
        public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull C3Settings settings, @NotNull Module module)
        {
            C3ProjectType projectType = settings.projectType();

            try
            {
                C3ProjectGenerator.generateProject(Path.of(baseDir.getPath()), project.getName(), projectType);
            }
            catch (IOException e)
            {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static class LibraryDirectoryGenerator extends WebProjectTemplate<C3Settings>
    {
        private static final Log LOG = LogFactory.getLog(LibraryDirectoryGenerator.class);

        @Override
        public @NotNull String getId()
        {
            return "C3_LIBRARY";
        }

        @Override
        public @NotNull String getName()
        {
            return "C3 library";
        }

        @Override
        public String getDescription()
        {
            return "Create a C3 library";
        }

        @Override
        public Icon getIcon()
        {
            return C3Icons.LIB_FILE;
        }

        @Override
        public @NotNull ProjectGeneratorPeer<C3Settings> createPeer()
        {
            return new LibraryPeer();
        }

        @Override
        public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull C3Settings settings, @NotNull Module module)
        {
            try
            {
                C3ProjectGenerator.generateLibrary(Path.of(baseDir.getPath()), project.getName());
            }
            catch (IOException e)
            {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private static class ProjectPeer extends GeneratorPeerImpl<C3Settings>
    {
        private final C3SettingsPanel settingsPanel = new C3SettingsPanel(true);

        @Override
        public @NotNull C3Settings getSettings()
        {
            return settingsPanel.getSettings();
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep)
        {
            settingsStep.addSettingsField("Project type:", settingsPanel.getProjectType());
            settingsStep.addSettingsField("C3 compiler:", settingsPanel.getCompilerSelector());
        }

        @Override
        public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid)
        {
            return settingsPanel.getComponent();
        }

        @Override
        public ValidationInfo validate()
        {
            return null;
        }

        @Override
        public boolean isBackgroundJobRunning()
        {
            return false;
        }
    }

    private static class LibraryPeer extends GeneratorPeerImpl<C3Settings>
    {
        private final C3SettingsPanel settingsPanel = new C3SettingsPanel(false);

        @Override
        public @NotNull C3Settings getSettings()
        {
            return settingsPanel.getSettings();
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep)
        {
            settingsStep.addSettingsField("C3 compiler:", settingsPanel.getCompilerSelector());
        }

        @Override
        public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid)
        {
            return settingsPanel.getComponent();
        }

        @Override
        public ValidationInfo validate()
        {
            return null;
        }

        @Override
        public boolean isBackgroundJobRunning()
        {
            return false;
        }
    }

    private static class C3SettingsPanel
    {
        private final JComboBox<CompilerOption> compilerSelector = createCompilerSelector();
        private final JComboBox<C3ProjectType> projectType = createProjectTypeField();
        private final JPanel component;

        private C3SettingsPanel(boolean showProjectType)
        {
            component = createSettingsPanel(compilerSelector, projectType, showProjectType);
        }

        private @NotNull JComponent getComponent()
        {
            return component;
        }

        private @NotNull JComboBox<CompilerOption> getCompilerSelector()
        {
            return compilerSelector;
        }

        private @NotNull JComboBox<C3ProjectType> getProjectType()
        {
            return projectType;
        }

        private @NotNull C3Settings getSettings()
        {
            CompilerOption compilerOption = getSelectedCompilerOption(compilerSelector);
            return new C3Settings(
                compilerOption.name(),
                compilerOption.binaryPath(),
                compilerOption.stdlibPath(),
                getSelectedProjectType(projectType)
            );
        }
    }

    private static JComboBox<C3ProjectType> createProjectTypeField()
    {
        JComboBox<C3ProjectType> field = new JComboBox<>(C3ProjectType.values());
        field.setSelectedItem(C3ProjectType.APPLICATION);
        return field;
    }

    private static JComboBox<CompilerOption> createCompilerSelector()
    {
        JComboBox<CompilerOption> field = new JComboBox<>();
        List<C3SettingsState.CompilerProfile> compilerProfiles = C3SettingsState.getInstance().getCompilerProfiles();
        if (compilerProfiles.isEmpty())
        {
            field.addItem(CompilerOption.empty());
            field.setEnabled(false);
            return field;
        }

        for (C3SettingsState.CompilerProfile profile : compilerProfiles)
        {
            field.addItem(new CompilerOption(profile.name, profile.binaryPath, profile.stdlibPath));
        }
        field.setSelectedIndex(0);
        return field;
    }

    private static JPanel createSettingsPanel(JComboBox<CompilerOption> compilerSelector,
                                             JComboBox<C3ProjectType> projectType,
                                             boolean showProjectType)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (showProjectType)
        {
            panel.add(createLabeledPanel("Project type:", projectType));
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(createLabeledPanel("C3 compiler:", compilerSelector));
        return panel;
    }

    private static JPanel createLabeledPanel(@NotNull String label, @NotNull JComponent component)
    {
        JPanel panel = new JPanel(new java.awt.BorderLayout(8, 0));
        panel.add(new JLabel(label), java.awt.BorderLayout.WEST);
        panel.add(component, java.awt.BorderLayout.CENTER);
        return panel;
    }

    private static @NotNull C3ProjectType getSelectedProjectType(@NotNull JComboBox<C3ProjectType> projectType)
    {
        Object selected = projectType.getSelectedItem();
        if (selected instanceof C3ProjectType selectedProjectType)
        {
            return selectedProjectType;
        }
        return C3ProjectType.APPLICATION;
    }

    private static @NotNull CompilerOption getSelectedCompilerOption(@NotNull JComboBox<CompilerOption> compilerSelector)
    {
        Object selected = compilerSelector.getSelectedItem();
        if (selected instanceof CompilerOption option)
        {
            return option;
        }
        return CompilerOption.empty();
    }

    private record CompilerOption(@NotNull String name, @NotNull String binaryPath, @NotNull String stdlibPath)
    {
        private static @NotNull CompilerOption empty()
        {
            return new CompilerOption("No configured C3 compilers", "", "");
        }

        @Override
        public @NotNull String toString()
        {
            if (binaryPath.isBlank()) return name;
            return name + " (" + binaryPath + ")";
        }
    }

    public enum C3ProjectType
    {
        APPLICATION("Application"),
        DYNAMIC_LIBRARY("Dynamic library"),
        STATIC_LIBRARY("Static library");

        private final @NotNull String displayName;

        C3ProjectType(@NotNull String displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public @NotNull String toString()
        {
            return displayName;
        }
    }

    public static class C3Settings
    {
        private final @NotNull String compilerName;
        private final @NotNull String compilerPath;
        private final @NotNull String stdlibPath;
        private final @NotNull C3ProjectType projectType;

        private C3Settings(
                @NotNull String compilerName,
                @NotNull String compilerPath,
                @NotNull String stdlibPath,
                @NotNull C3ProjectType projectType)
        {
            this.compilerName = compilerName;
            this.compilerPath = compilerPath;
            this.stdlibPath = stdlibPath;
            this.projectType = projectType;
        }

        public @NotNull String compilerName()
        {
            return compilerName;
        }

        public @NotNull String compilerPath()
        {
            return compilerPath;
        }

        public @NotNull String stdlibPath()
        {
            return stdlibPath;
        }

        public @NotNull C3ProjectType projectType()
        {
            return projectType;
        }
    }
}
