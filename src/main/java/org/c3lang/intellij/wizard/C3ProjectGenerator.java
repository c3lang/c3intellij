package org.c3lang.intellij.wizard;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.ide.util.projectWizard.WebTemplateNewProjectWizard;
import com.intellij.ide.wizard.GeneratorNewProjectWizardBuilderAdapter;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
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

public final class C3ProjectGenerator
{
    private C3ProjectGenerator()
    {
    }

    static void generateProject(@NotNull Path basePath, @NotNull String projectName) throws IOException
    {
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
        C3Util.INSTANCE.writeToFile(projectName, "templates/project.json", basePath.resolve("project.json").toFile());
        C3Util.INSTANCE.writeToFile(projectName, "templates/main", basePath.resolve("src/main.c3").toFile());
    }

    static void generateLibrary(@NotNull Path basePath, @NotNull String projectName) throws IOException
    {
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

        String moduleName = projectName.replace(" ", "_").toLowerCase();

        for (String dir : dirs)
        {
            FileUtil.createDirectory(basePath.resolve(dir).toFile());
        }

        FileUtil.writeToFile(basePath.resolve("LICENSE").toFile(), "");
        FileUtil.writeToFile(basePath.resolve("README.md").toFile(), "");
        C3Util.INSTANCE.writeToFile(moduleName, "templates/library", basePath.resolve(moduleName + ".c3i").toFile());
        C3Util.INSTANCE.writeToFile(moduleName, "templates/manifest.json", basePath.resolve("manifest.json").toFile());
    }

    public static class ProjectModuleBuilder extends GeneratorNewProjectWizardBuilderAdapter
    {
        public ProjectModuleBuilder()
        {
            super(new WebTemplateNewProjectWizard(new ProjectDirectoryGenerator()));
        }

        @Override
        public @NotNull String getGroupName()
        {
            return "Other";
        }

        @Override
        public @NotNull String getDescription()
        {
            return "Create a C3 project";
        }
    }

    public static class LibraryModuleBuilder extends GeneratorNewProjectWizardBuilderAdapter
    {
        public LibraryModuleBuilder()
        {
            super(new WebTemplateNewProjectWizard(new LibraryDirectoryGenerator()));
        }

        @Override
        public @NotNull String getGroupName()
        {
            return "Other";
        }

        @Override
        public @NotNull String getDescription()
        {
            return "Create a C3 library";
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
            return new Peer();
        }

        @Override
        public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull C3Settings settings, @NotNull Module module)
        {
            try
            {
                C3ProjectGenerator.generateProject(Path.of(baseDir.getPath()), project.getName());
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
            return new Peer();
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

    private static class Peer extends GeneratorPeerImpl<C3Settings>
    {
        private final C3SettingsPanel settingsPanel = new C3SettingsPanel();

        @Override
        public @NotNull C3Settings getSettings()
        {
            return settingsPanel.getSettings();
        }

        @Override
        public void buildUI(@NotNull SettingsStep settingsStep)
        {
            settingsStep.addSettingsField("Path to C3 stdlib:", settingsPanel.getStdlibPath());
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
        private final TextFieldWithBrowseButton stdlibPath = createStdlibPathField();
        private final JPanel component = createStdlibPathPanel(stdlibPath);

        private C3SettingsPanel()
        {
        }

        private @NotNull JComponent getComponent()
        {
            return component;
        }

        private @NotNull TextFieldWithBrowseButton getStdlibPath()
        {
            return stdlibPath;
        }

        private @NotNull C3Settings getSettings()
        {
            return new C3Settings(stdlibPath.getText());
        }
    }

    private static TextFieldWithBrowseButton createStdlibPathField()
    {
        TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();
        field.setText(C3SettingsState.getInstance().stdlibPath);
        field.addBrowseFolderListener(null, new FileChooserDescriptor(false, true, false, false, false, false));
        return field;
    }

    private static JPanel createStdlibPathPanel(TextFieldWithBrowseButton stdlibPath)
    {
        JPanel panel = new JPanel(new java.awt.BorderLayout(8, 0));
        panel.add(new JLabel("C3 stdlib path:"), java.awt.BorderLayout.WEST);
        panel.add(stdlibPath, java.awt.BorderLayout.CENTER);
        return panel;
    }

    public static class C3Settings
    {
        private final @NotNull String stdlibPath;

        private C3Settings(@NotNull String stdlibPath)
        {
            this.stdlibPath = stdlibPath;
        }

        public @NotNull String stdlibPath()
        {
            return stdlibPath;
        }
    }
}
