package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.AdditionalLibraryRootsProvider;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.c3lang.intellij.project.C3ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class C3StdLibRootsProvider extends AdditionalLibraryRootsProvider
{
	@Override
	public @NotNull Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project)
	{
		List<VirtualFile> stdLibRoots = getRoots(project);
		if (stdLibRoots.isEmpty())
		{
			return Collections.emptyList();
		}

		return List.of(new C3SyntheticLibrary("C3 Standard Library", stdLibRoots));
	}

	@Override
	public @NotNull Collection<VirtualFile> getRootsToWatch(@NotNull Project project)
	{
		return getRoots(project);
	}

	public @NotNull List<VirtualFile> getRoots(@NotNull Project project)
	{
		List<VirtualFile> stdLibRoots = new ArrayList<>();
		LocalFileSystem lfs = LocalFileSystem.getInstance();

		for (String rawPath : C3ProjectService.getInstance(project).getStdlibPaths())
		{
			String clean = normalizePath(rawPath);
			if (clean == null) continue;

			VirtualFile root = lfs.findFileByPath(clean);
			if (root == null)
			{
				root = lfs.refreshAndFindFileByPath(clean);
			}

			if (root != null && root.isValid() && root.isDirectory())
			{
				if (!stdLibRoots.contains(root))
				{
					stdLibRoots.add(root);
				}

				VirtualFile stdChild = root.findChild("std");
				if (stdChild != null && stdChild.isValid() && stdChild.isDirectory() && !stdLibRoots.contains(stdChild))
				{
					stdLibRoots.add(stdChild);
				}
			}
		}

		return stdLibRoots;
	}

	public static @Nullable String normalizePath(@Nullable String path)
	{
		if (path == null) return null;
		String normalized = path.trim().replace('\\', '/');
		while (normalized.endsWith("/") && normalized.length() > 1)
		{
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized.isEmpty() ? null : normalized;
	}
}
