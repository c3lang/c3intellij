package org.c3lang.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3CompileRunEditor extends SettingsEditor<C3CompileRunConfiguration>
{
    JPanel panel;
    TextFieldWithBrowseButton sourceFileField;
    TextFieldWithBrowseButton workingDirectoryField;
    JTextField argsField;

    public C3CompileRunEditor()
    {
        createUIComponents();

        panel = FormBuilder.createFormBuilder()
                           .addLabeledComponent("C3 Source file", sourceFileField)
                           .addLabeledComponent("Working directory", workingDirectoryField)
                           .addTooltip("This is the directory where the output executable will be produced.")
                           .addLabeledComponent("Additional arguments", argsField)
                           .getPanel();
    }

    @Override protected void resetEditorFrom(@NotNull C3CompileRunConfiguration configuration)
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

        // Also set argsField and sourceFileField to their stored values
        argsField.setText(configuration.getArgs());
        sourceFileField.setText(configuration.getSourceFile());
    }

    @Override protected void applyEditorTo(@NotNull C3CompileRunConfiguration configuration) throws
                                                                                             ConfigurationException
    {
        if (sourceFileField.getText().isEmpty())
        {
            throw new ConfigurationException("You must provide a source file.");
        }

        if (workingDirectoryField.getText().isEmpty())
        {
            throw new ConfigurationException("You must provide a working directory.");
        }

        configuration.setWorkingDirectory(workingDirectoryField.getText());
        configuration.setArgs(argsField.getText());
        configuration.setSourceFile(sourceFileField.getText());
    }

    @Override protected @NotNull JComponent createEditor()
    {
        return panel;
    }

    private void createUIComponents()
    {
        workingDirectoryField = new TextFieldWithBrowseButton();
        TextBrowseFolderListener listener =
                new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()
                                                                         .withTitle("Select Working Directory"));
        workingDirectoryField.addBrowseFolderListener(listener);

        argsField = new JTextField();

        sourceFileField = new TextFieldWithBrowseButton();
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter((VirtualFile file) ->
                                        Comparing.equal(file.getExtension(), "c3i", true)
                                        || Comparing.equal(file.getExtension(), "c3", true));
        sourceFileField.addBrowseFolderListener(new TextBrowseFolderListener(descriptor.withTitle(
                "Select C3 Source File")));
    }
}
