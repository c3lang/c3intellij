package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.impl.C3PsiImplUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class C3Util
{
    private static Collection<VirtualFile> getAllFiles(Project project)
    {
        Collection<VirtualFile> virtualFilesInterface =
                FileTypeIndex.getFiles(C3InterfaceFileType.INSTANCE, GlobalSearchScope.allScope(project));
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(C3SourceFileType.INSTANCE, GlobalSearchScope.allScope(project));
        if (virtualFiles.isEmpty()) return virtualFilesInterface;
        if (virtualFilesInterface.isEmpty()) return virtualFiles;
        ArrayList<VirtualFile> all = new ArrayList<>(virtualFiles.size() + virtualFilesInterface.size());
        all.addAll(virtualFiles);
        all.addAll(virtualFilesInterface);
        return all;
    }

}
