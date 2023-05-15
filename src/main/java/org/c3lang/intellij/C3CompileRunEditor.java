package org.c3lang.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.JBRFileDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3CompileRunEditor extends SettingsEditor<C3CompileRunConfiguration>
{
    JPanel panel;
    TextFieldWithBrowseButton fileSource;

    @Override protected void resetEditorFrom(@NotNull C3CompileRunConfiguration c3CompileRunConfiguration)
    {
        fileSource.setText(c3CompileRunConfiguration.getSourceName());
    }

    @Override protected void applyEditorTo(@NotNull C3CompileRunConfiguration c3CompileRunConfiguration) throws
                                                                                                         ConfigurationException
    {
        System.out.println("Set source name to " + fileSource.getText());
        c3CompileRunConfiguration.setSourceName(fileSource.getText());
        System.out.println("Name is now " + c3CompileRunConfiguration.getSourceName());
    }

    @Override protected @NotNull JComponent createEditor()
    {
        return panel;
    }

    private void createUIComponents()
    {
        fileSource = new TextFieldWithBrowseButton();
        fileSource.addBrowseFolderListener("Select C3 Source File", "", null, new FileChooserDescriptor(true, false, false, false, false, false)
                {
                    @Override
                    public boolean isFileSelectable(@Nullable VirtualFile file)
                    {
                        if (file == null) return false;
                        if (!file.exists()) return false;
                        if (file.isDirectory()) return false;
                        return Objects.equals(file.getExtension(), "c3i") || Objects.equals(file.getExtension(), "c3");
                    }
                });
    }
}
