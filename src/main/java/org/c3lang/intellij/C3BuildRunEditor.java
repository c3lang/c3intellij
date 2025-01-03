package org.c3lang.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class C3BuildRunEditor extends SettingsEditor<C3BuildRunConfiguration>
{
    JPanel panel;
    TextFieldWithBrowseButton workingDirectoryField;
    JTextField argsField;

    public C3BuildRunEditor() {
        createUIComponents();

        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Working directory", workingDirectoryField)
                .addLabeledComponent("Additional arguments", argsField)
                .getPanel();
    }

    @Override protected void resetEditorFrom(@NotNull C3BuildRunConfiguration configuration)
    {
        // This function is called each time the run configuration form is shown,
        // i.e. both when its first created and when it's being edited

        if (configuration.getWorkingDirectory().isEmpty()) {
            // By default, fill the workingDirectory field with the project's base path
            String projectDirectory = configuration.getProject().getBasePath();
            workingDirectoryField.setText(projectDirectory);
        } else {
            // Otherwise (when editing the configuration), set its value to the one that was stored
            workingDirectoryField.setText(configuration.getWorkingDirectory());
        }

        // Also set argsField to its stored value
        argsField.setText(configuration.getArgs());
    }

    @Override protected void applyEditorTo(@NotNull C3BuildRunConfiguration configuration) throws
                                                                                           ConfigurationException
    {
        if (workingDirectoryField.getText().isEmpty()) {
            throw new ConfigurationException("You must provide a working directory.");
        }

        configuration.setWorkingDirectory(workingDirectoryField.getText());
        configuration.setArgs(argsField.getText());
    }

    @Override protected @NotNull JComponent createEditor()
    {
        return panel;
    }

    private void createUIComponents() {
        workingDirectoryField = new TextFieldWithBrowseButton();
        workingDirectoryField.addBrowseFolderListener(
                "Select Working Directory",
                null,
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
        );

        argsField = new JTextField();
    }
}

