package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class C3StdLibRootsProvider extends AdditionalLibraryRootsProvider {
    @Override
    public @NotNull Collection<SyntheticLibrary> getAdditionalProjectLibraries(Project project) {

        var settings = C3SettingsState.getInstance();
        var stdLibPath = settings.stdlibPath;

        VirtualFile stdLibRoot = LocalFileSystem.getInstance().findFileByPath(stdLibPath);
        if (stdLibRoot != null) {
            return Collections.singletonList(
                    SyntheticLibrary.newImmutableLibrary(Collections.singletonList(stdLibRoot))
            );
        }
        return Collections.emptyList();
    }
}
