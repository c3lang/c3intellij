package org.c3lang.intellij.components;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class C3PluginComponent implements ProjectManagerListener
{
    @Override
    public void projectOpened(@NotNull Project project)
    {
        FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(project);

        fileTemplateManager.addTemplate("C3 File", "c3");
    }
}