package org.c3lang.intellij.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Properties;

abstract public class C3NewFileActionBase extends CreateFileFromTemplateAction
{
	protected static final Log LOG = LogFactory.getLog(C3NewFileActionBase.class);
	
	public C3NewFileActionBase(@NlsActions.ActionText String text, @NlsActions.ActionDescription String description, @Nullable Icon icon) 
	{
		super(text, description, icon);
 	}
	
	@Override protected @Nullable PsiFile createFileFromTemplate(@NotNull String name, @NotNull FileTemplate template,
	                                                             @NotNull PsiDirectory dir)
	{
		Project project = dir.getProject();
		FileTemplateManager templateManager = FileTemplateManager.getInstance(project);

		Properties properties = new Properties();
		properties.putAll(templateManager.getDefaultProperties());
		properties.setProperty("MODULE_NAME", getModuleName(project));

		try
		{
			return (PsiFile) FileTemplateUtil.createFromTemplate(template, name, properties, dir);
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	private static @NotNull String getModuleName(@NotNull Project project)
	{
		String basePath = Validate.notNull(project.getBasePath(), "Project base path cannot be null");
		VirtualFile projectRoot =
				Validate.notNull(LocalFileSystem.getInstance().findFileByPath(basePath), "Project root cannot be null");

		return projectRoot.getName().replace(' ', '_').toLowerCase();
	}

	@Override public @NotNull String getActionName(@NotNull PsiDirectory psiDirectory, @NonNls @NotNull String newName,
	                                               @NonNls String templateName)
	{
		return "Creating C3 File " + newName;
	}

	@Override public void update(@NotNull AnActionEvent e)
	{
		super.update(e);

		Project project = e.getProject();
		VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

		e.getPresentation().setVisible(project != null && virtualFile != null && isC3Project(project));
	}

	private static boolean isC3Project(@NotNull Project project)
	{
		return C3ProjectService.getInstance(project).isC3Project();
	}
}
