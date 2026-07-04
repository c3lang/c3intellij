package org.c3lang.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;


public class C3BuildRunEditor extends SettingsEditor<C3BuildRunConfiguration>
{
    JPanel panel;
    TextFieldWithBrowseButton workingDirectoryField;
    JComboBox<CompilerOption> compilerComboBox;
    JCheckBox runAfterBuildCheckBox;
    JTextField argsField;

    public C3BuildRunEditor()
    {
        createUIComponents();

        panel = FormBuilder.createFormBuilder()
                           .addLabeledComponent("Working directory", workingDirectoryField)
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
        runAfterBuildCheckBox.setSelected(configuration.isRunAfterBuild());
        argsField.setText(configuration.getArgs());
        resetCompilerSettings(configuration);
    }

    @Override protected void applyEditorTo(@NotNull C3BuildRunConfiguration configuration) throws ConfigurationException
    {
        if (workingDirectoryField.getText().isEmpty())
        {
            throw new ConfigurationException("You must provide a working directory.");
        }

        configuration.setWorkingDirectory(workingDirectoryField.getText());
        configuration.setRunAfterBuild(runAfterBuildCheckBox.isSelected());
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
        compilerComboBox = C3RunConfigurationUtil.createCompilerComboBox();
        runAfterBuildCheckBox = new JCheckBox("Run after build");
        argsField = new JTextField();
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


}
