package org.c3lang.intellij.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.c3lang.intellij.project.C3ProjectModel;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;

public final class C3ProjectSettingsAction extends DumbAwareAction
{
	@Override
	public void actionPerformed(@NotNull AnActionEvent e)
	{
		Project project = e.getProject();
		if (project == null) return;

		C3ProjectService service = C3ProjectService.getInstance(project);
		C3ProjectModel projectModel = service.getProjectModel();
		if (projectModel == null)
		{
			Messages.showErrorDialog(project, "Unable to load project.json.", "C3 Project Structure");
			return;
		}

		new C3ProjectStructureDialog(project, projectModel).show();
	}

	@Override
	public void update(@NotNull AnActionEvent e)
	{
		Project project = e.getProject();
		e.getPresentation().setEnabledAndVisible(
			project != null && C3ProjectService.getInstance(project).getProjectJsonFile() != null
		);
	}

	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread()
	{
		return ActionUpdateThread.BGT;
	}
}
