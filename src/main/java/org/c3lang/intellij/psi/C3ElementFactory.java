package org.c3lang.intellij.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3SourceFileType;

public class C3ElementFactory
{
    public static C3TypeName createTypeName(Project project, String name)
    {
        return (C3TypeName)createFile(project, "struct " + name + "{int a}").getNode().findChildByType(C3Types.TYPE_NAME);
    }

    public static C3File createFile(Project project, String text)
    {
        String name = "dummy.c3";
        return (C3File) PsiFileFactory.getInstance(project).createFileFromText(name, C3SourceFileType.INSTANCE, text);
    }

    public static IElementType stubFactory(String name)
    {
        if ("MODULE_SECTION".equals(name)) return new C3ModuleSectionStubElementType();
        throw new RuntimeException();
    }

}