package org.c3lang.intellij.actions

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationPanel.Status
import com.intellij.ui.EditorNotificationProvider
import org.c3lang.intellij.C3SettingsState
import org.c3lang.intellij.settings.C3Configurable
import java.util.function.Function
import javax.swing.JComponent

class C3EditorNotificationProvider : EditorNotificationProvider
{
    override fun collectNotificationData(project: Project, file: VirtualFile): Function<in FileEditor, out JComponent?>?
    {
        val settings = C3SettingsState.getInstance()

        if (settings.stdlibPath != null) return null

        return Function { fileEditor: FileEditor ->
            val stdlibErrorNotification = EditorNotificationPanel(fileEditor, Status.Error)
            stdlibErrorNotification.text = "Stdlib not detected"
            stdlibErrorNotification.createActionLabel("Setup") {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, C3Configurable::class.java)
            }
            stdlibErrorNotification
        }
    }
}