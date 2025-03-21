package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class C3Util
{
    private static final Log log = LogFactory.getLog(C3Util.class);

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

    public static void writeToFile(String moduleName, String name, File path)
    {
        InputStream inputStream = C3Util.class.getClassLoader().getResourceAsStream(name);

        if (inputStream != null) {
            try {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }

                String contentString = content.toString().formatted(moduleName);

                FileUtil.writeToFile(path, contentString);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            throw new RuntimeException("Unable to load file '%s".formatted(name));
        }
    }
}
