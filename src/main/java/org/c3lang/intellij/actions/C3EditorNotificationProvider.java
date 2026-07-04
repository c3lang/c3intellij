package org.c3lang.intellij.actions;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotificationProvider;
import org.c3lang.intellij.C3SettingsState;
import org.c3lang.intellij.settings.C3Configurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.function.Function;

public class C3EditorNotificationProvider implements EditorNotificationProvider
{
	@Override
	public Function<? super FileEditor, ? extends JComponent> collectNotificationData(@NotNull Project project, @NotNull VirtualFile file)
	{
		C3SettingsState settings = C3SettingsState.getInstance();
		if (settings.hasStdlibEntries()) return null;

		return fileEditor -> {
			EditorNotificationPanel panel = new EditorNotificationPanel(fileEditor, EditorNotificationPanel.Status.Error);
			panel.setText("No C3 standard library paths are configured.");
			panel.createActionLabel("Setup", () -> ShowSettingsUtil.getInstance().showSettingsDialog(project, C3Configurable.class));
			return panel;
		};
	}
}
