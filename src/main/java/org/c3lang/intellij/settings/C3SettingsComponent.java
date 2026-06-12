package org.c3lang.intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class C3SettingsComponent
{
    private final JPanel mainPanel = new JPanel(new GridBagLayout());
    private final TextFieldWithBrowseButton stdlibPathField = new TextFieldWithBrowseButton();

    public C3SettingsComponent()
    {
        Project project = null;
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length > 0)
        {
            project = openProjects[0];
        }

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        stdlibPathField.addBrowseFolderListener(project, new FileChooserDescriptor(false, true, false, false, false, false));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = JBUI.insetsBottom(5);

        gbc.gridy = 0;
        mainPanel.add(new JLabel("Stdlib Path:"), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.0;
        mainPanel.add(stdlibPathField, gbc);

        gbc.gridy = 2;
        JLabel hintLabel = new JLabel("for example: .../c3c/lib/");
        hintLabel.setForeground(JBColor.GRAY);
        hintLabel.setFont(hintLabel.getFont().deriveFont((float) hintLabel.getFont().getSize() - 1.0f));
        mainPanel.add(hintLabel, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(Box.createVerticalGlue(), gbc);
    }

    public JPanel getMainPanel()
    {
        return mainPanel;
    }

    public String getStdlibPath()
    {
        return stdlibPathField.getTextField().getText();
    }

    public void setStdlibPath(String path)
    {
        stdlibPathField.getTextField().setText(path);
    }
}
