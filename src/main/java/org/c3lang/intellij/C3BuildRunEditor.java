package org.c3lang.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.c3lang.intellij.project.C3ProjectJsonParser;
import org.c3lang.intellij.project.C3ProjectModel;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class C3BuildRunEditor extends SettingsEditor<C3BuildRunConfiguration>
{
    JPanel panel;
    TextFieldWithBrowseButton workingDirectoryField;
    JComboBox<CompilerOption> compilerComboBox;
    JComboBox<TargetOption> targetComboBox;
    JCheckBox runAfterBuildCheckBox;
    JTextField argsField;

    public C3BuildRunEditor()
    {
        createUIComponents();

        panel = FormBuilder.createFormBuilder()
                           .addLabeledComponent("Working directory", workingDirectoryField)
                           .addLabeledComponent("Target", targetComboBox)
                           .addLabeledComponent("C3 compiler", compilerComboBox)
                           .addComponent(runAfterBuildCheckBox)
                           .addLabeledComponent("Additional arguments", argsField)
                           .getPanel();
    }

    @Override protected void resetEditorFrom(@NotNull C3BuildRunConfiguration configuration)
    {
        // This function is called each time the run configuration form is shown,
        // i.e. both when its first created and when it's being edited

        if (configuration.getWorkingDirectory().isEmpty())
        {
            // By default, fill the workingDirectory field with the project's base path
            String projectDirectory = configuration.getProject().getBasePath();
            workingDirectoryField.setText(projectDirectory);
        }
        else
        {
            // Otherwise (when editing the configuration), set its value to the one that was stored
            workingDirectoryField.setText(configuration.getWorkingDirectory());
        }

        // Also set argsField to its stored value
        resetTargets(configuration);
        argsField.setText(configuration.getArgs());
        resetCompilerSettings(configuration);
        runAfterBuildCheckBox.setSelected(configuration.isRunAfterBuild());
        updateRunAfterBuildAvailability();
    }

    @Override protected void applyEditorTo(@NotNull C3BuildRunConfiguration configuration) throws ConfigurationException
    {
        if (workingDirectoryField.getText().isEmpty())
        {
            throw new ConfigurationException("You must provide a working directory.");
        }
        if (getSelectedTargetName().isBlank())
        {
            throw new ConfigurationException("You must select a C3 build target.");
        }

        configuration.setWorkingDirectory(workingDirectoryField.getText());
        configuration.setTargetName(getSelectedTargetName());
        if (configuration.isGeneratedName())
        {
            configuration.setGeneratedName();
        }
        configuration.setRunAfterBuild(runAfterBuildCheckBox.isEnabled() && runAfterBuildCheckBox.isSelected());
        configuration.setArgs(argsField.getText());
        configuration.setCompilerName(getSelectedCompilerName());
        configuration.setCompilerPath(getSelectedCompilerPath());
    }

    @Override protected @NotNull JComponent createEditor()
{
        return panel;
    }

    private void createUIComponents()
    {
        workingDirectoryField = new TextFieldWithBrowseButton();
        TextBrowseFolderListener listener = new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle("Select Working Directory"));
        workingDirectoryField.addBrowseFolderListener(listener);
        targetComboBox = new JComboBox<>();
        targetComboBox.addActionListener(event -> updateRunAfterBuildAvailability());
        compilerComboBox = C3RunConfigurationUtil.createCompilerComboBox();
        runAfterBuildCheckBox = new JCheckBox("Run after build");
        argsField = new JTextField();
    }

    private void resetTargets(@NotNull C3BuildRunConfiguration configuration)
    {
        targetComboBox.removeAllItems();

        C3ProjectModel model = C3ProjectService.getInstance(configuration.getProject()).getProjectModel();
        if (model != null)
        {
            for (C3ProjectJsonParser.TargetDefinition target : model.getTargets())
            {
                targetComboBox.addItem(new TargetOption(target.name(), target.type()));
            }
        }

        String selectedTargetName = configuration.getTargetName().trim();
        if (!selectedTargetName.isBlank())
        {
            for (int i = 0; i < targetComboBox.getItemCount(); i++)
            {
                if (targetComboBox.getItemAt(i).name().equals(selectedTargetName))
                {
                    targetComboBox.setSelectedIndex(i);
                    return;
                }
            }
            targetComboBox.addItem(new TargetOption(selectedTargetName, ""));
            targetComboBox.setSelectedIndex(targetComboBox.getItemCount() - 1);
            return;
        }
        if (targetComboBox.getItemCount() > 0)
        {
            targetComboBox.setSelectedIndex(0);
        }
    }

    private void updateRunAfterBuildAvailability()
    {
        boolean executableTarget = getSelectedTargetType().equals(C3ProjectJsonParser.DEFAULT_TARGET_TYPE);
        runAfterBuildCheckBox.setEnabled(executableTarget);
        if (!executableTarget)
        {
            runAfterBuildCheckBox.setSelected(false);
        }
    }

    private void resetCompilerSettings(@NotNull C3BuildRunConfiguration configuration)
    {
        String compilerName = configuration.getCompilerName().isBlank()
                ? C3RunConfigurationUtil.findDefaultCompilerName(configuration.getProject())
                : configuration.getCompilerName();
		C3RunConfigurationUtil.selectCompiler(compilerComboBox, compilerName, configuration.getCompilerPath());
    }

    private @NotNull String getSelectedCompilerName()
    {
        Object selected = compilerComboBox.getSelectedItem();
        return selected instanceof CompilerOption option ? option.name() : "";
    }

    private @NotNull String getSelectedCompilerPath()
    {
        Object selected = compilerComboBox.getSelectedItem();
        return selected instanceof CompilerOption option ? option.binaryPath() : "";
    }

    private @NotNull String getSelectedTargetName()
    {
        Object selected = targetComboBox.getSelectedItem();
        return selected instanceof TargetOption option ? option.name() : "";
    }

    private @NotNull String getSelectedTargetType()
    {
        Object selected = targetComboBox.getSelectedItem();
        return selected instanceof TargetOption option ? option.type() : "";
    }

    private record TargetOption(@NotNull String name, @NotNull String type)
    {
        @Override
        public String toString()
        {
            if (type.isBlank())
            {
                return name;
            }
            return name + " (" + type + ")";
        }
    }


}
