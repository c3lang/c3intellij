package org.c3lang.intellij.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.JLabel
import javax.swing.JPanel

class C3SettingsComponent
{
    var mainPanel: JPanel = JPanel(GridBagLayout())
    private var stdlibPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init
    {
        var project: Project? = null

        val openProjects = ProjectManager.getInstance().openProjects
        if (openProjects.isNotEmpty())
        {
            project = openProjects[0]
        }

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
        stdlibPathField.addBrowseFolderListener(project, FileChooserDescriptor(false, true, false, false, false, false))

        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.NORTHWEST
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        gbc.insets = JBUI.insetsBottom(5)

        gbc.gridy = 0
        mainPanel.add(JLabel("Stdlib Path:"), gbc)

        gbc.gridy = 1
        gbc.weighty = 0.0
        mainPanel.add(stdlibPathField, gbc)

        gbc.gridy = 2
        val hintLabel = JLabel("for example: .../c3c/lib/").apply {
            foreground = JBColor.GRAY
            font = font.deriveFont(font.size - 1f)
        }
        mainPanel.add(hintLabel, gbc)

        gbc.gridy = 3
        gbc.weighty = 1.0
        mainPanel.add(Box.createVerticalGlue(), gbc)
    }

    var stdlibPath: String
        get() = stdlibPathField.textField.text
        set(path)
        {
            stdlibPathField.textField.text = path
        }
}