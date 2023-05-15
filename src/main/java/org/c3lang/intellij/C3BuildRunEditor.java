package org.c3lang.intellij;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class C3BuildRunEditor extends SettingsEditor<C3BuildRunConfiguration>
{
    JPanel panel;

    @Override protected void resetEditorFrom(@NotNull C3BuildRunConfiguration configuration)
    {
    }

    @Override protected void applyEditorTo(@NotNull C3BuildRunConfiguration configuration) throws
                                                                                                         ConfigurationException
    {
    }

    @Override protected @NotNull JComponent createEditor()
    {
        return panel;
    }

    private void createUIComponents()
    {
    }
}

