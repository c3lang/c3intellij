package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class C3StdLibRootsProvider extends AdditionalLibraryRootsProvider
{
	@Override public @NotNull Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project)
	{
		List<VirtualFile> stdLibRoots = new ArrayList<>();

		for (String stdLibPath : C3ProjectService.getInstance(project).getStdlibPaths())
		{
			VirtualFile stdLibRoot = LocalFileSystem.getInstance().findFileByPath(stdLibPath);
			if (stdLibRoot != null)
			{
				stdLibRoots.add(stdLibRoot);
			}
		}

		return stdLibRoots.isEmpty()
			? List.of()
			: List.of(SyntheticLibrary.newImmutableLibrary(stdLibRoots));
	}
}
