package org.c3lang.intellij.project;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class C3ProjectModel
{
	private final @NotNull VirtualFile projectRoot;
	private final @NotNull VirtualFile projectJson;
	private final @NotNull ObjectNode document;
	private final @NotNull List<String> sources;
	private final @NotNull String version;
	private final @NotNull List<String> authors;
	private final @NotNull String compilerName;
	private final @NotNull String stdlibOverridePath;

	C3ProjectModel(
			@NotNull VirtualFile projectRoot,
			@NotNull VirtualFile projectJson,
			@NotNull ObjectNode document,
			@NotNull List<String> sources,
			@NotNull String version,
			@NotNull List<String> authors,
			@NotNull String compilerName,
			@NotNull String stdlibOverridePath)
	{
		this.projectRoot = projectRoot;
		this.projectJson = projectJson;
		this.document = document;
		this.sources = List.copyOf(sources);
		this.version = version;
		this.authors = List.copyOf(authors);
		this.compilerName = compilerName;
		this.stdlibOverridePath = stdlibOverridePath;
	}

	public @NotNull VirtualFile getProjectRoot()
	{
		return projectRoot;
	}

	public @NotNull VirtualFile getProjectJson()
	{
		return projectJson;
	}

	public @NotNull ObjectNode getDocument()
	{
		return document;
	}

	public @NotNull List<String> getSources()
	{
		return sources;
	}

	public @NotNull String getVersion()
	{
		return version;
	}

	public @NotNull List<String> getAuthors()
	{
		return authors;
	}

	public @NotNull String getCompilerName()
	{
		return compilerName;
	}

	public @NotNull String getStdlibOverridePath()
	{
		return stdlibOverridePath;
	}

	public boolean hasStdlibOverride()
	{
		return !stdlibOverridePath.isBlank();
	}

	public boolean isSourceFile(@NotNull VirtualFile file)
	{
		if (file.isDirectory()) return false;
		String extension = file.getExtension();
		if (!"c3".equals(extension) && !"c3i".equals(extension)) return false;

		String relativePath = relativePath(file);
		if (relativePath == null) return false;

		for (String source : sources)
		{
			if (matchesSource(source, relativePath))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isUnderProjectRoot(@NotNull VirtualFile file)
	{
		String rootPath = projectRoot.getPath();
		String filePath = file.getPath();
		return filePath.equals(rootPath) || filePath.startsWith(rootPath + "/");
	}

	public @NotNull List<VirtualFile> collectSourceFiles()
	{
		LinkedHashSet<VirtualFile> result = new LinkedHashSet<>();
		for (String source : sources)
		{
			collectSourceFiles(source, result);
		}
		return new ArrayList<>(result);
	}

	private void collectSourceFiles(@NotNull String source, @NotNull LinkedHashSet<VirtualFile> result)
	{
		String normalized = normalizeSource(source);
		if (normalized.endsWith("/**"))
		{
			VirtualFile directory = projectRoot.findFileByRelativePath(normalized.substring(0, normalized.length() - 3));
			if (directory != null && directory.isDirectory())
			{
				collectDirectory(directory, true, result);
			}
			return;
		}
		if (normalized.endsWith("/*"))
		{
			VirtualFile directory = projectRoot.findFileByRelativePath(normalized.substring(0, normalized.length() - 2));
			if (directory != null && directory.isDirectory())
			{
				collectDirectory(directory, false, result);
			}
			return;
		}

		VirtualFile fileOrDirectory = projectRoot.findFileByRelativePath(normalized);
		if (fileOrDirectory == null) return;
		if (fileOrDirectory.isDirectory())
		{
			collectDirectory(fileOrDirectory, false, result);
		}
		else if (isC3File(fileOrDirectory))
		{
			result.add(fileOrDirectory);
		}
	}

	private static void collectDirectory(
			@NotNull VirtualFile directory,
			boolean recursive,
			@NotNull LinkedHashSet<VirtualFile> result)
	{
		for (VirtualFile child : directory.getChildren())
		{
			if (child.isDirectory())
			{
				if (recursive)
				{
					collectDirectory(child, true, result);
				}
			}
			else if (isC3File(child))
			{
				result.add(child);
			}
		}
	}

	private static boolean isC3File(@NotNull VirtualFile file)
	{
		String extension = file.getExtension();
		return "c3".equals(extension) || "c3i".equals(extension);
	}

	static boolean matchesSource(@NotNull String source, @NotNull String relativePath)
	{
		String normalizedSource = normalizeSource(source);
		String normalizedPath = normalizeRelativePath(relativePath);

		if (normalizedSource.endsWith("/**"))
		{
			String directory = normalizedSource.substring(0, normalizedSource.length() - 3);
			return normalizedPath.startsWith(directory + "/");
		}
		if (normalizedSource.endsWith("/*"))
		{
			String directory = normalizedSource.substring(0, normalizedSource.length() - 2);
			if (!normalizedPath.startsWith(directory + "/")) return false;
			return normalizedPath.indexOf('/', directory.length() + 1) < 0;
		}
		if (normalizedSource.endsWith(".c3") || normalizedSource.endsWith(".c3i"))
		{
			return normalizedPath.equals(normalizedSource);
		}
		return normalizedPath.startsWith(normalizedSource + "/")
			&& normalizedPath.indexOf('/', normalizedSource.length() + 1) < 0;
	}

	public static boolean isValidSourcePattern(@NotNull String source)
	{
		String normalized = normalizeSource(source);
		if (normalized.isEmpty()) return false;
		if (normalized.contains("//")) return false;
		int wildcard = normalized.indexOf('*');
		if (wildcard < 0) return true;
		int slashBeforeWildcard = wildcard - 1;
		if (slashBeforeWildcard <= 0 || normalized.charAt(slashBeforeWildcard) != '/') return false;
		if (normalized.endsWith("/*"))
		{
			return wildcard == normalized.length() - 1;
		}
		if (normalized.endsWith("/**"))
		{
			return wildcard == normalized.length() - 2
				&& normalized.indexOf('*', wildcard + 1) == normalized.length() - 1;
		}
		return false;
	}

	private static @NotNull String normalizeSource(@NotNull String source)
	{
		String normalized = normalizeRelativePath(source);
		while (normalized.endsWith("/") && normalized.length() > 1)
		{
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}

	private static @NotNull String normalizeRelativePath(@NotNull String path)
	{
		String normalized = path.replace('\\', '/').trim();
		while (normalized.startsWith("./"))
		{
			normalized = normalized.substring(2);
		}
		while (normalized.startsWith("/"))
		{
			normalized = normalized.substring(1);
		}
		return normalized;
	}

	private String relativePath(@NotNull VirtualFile file)
	{
		String rootPath = projectRoot.getPath();
		String filePath = file.getPath();
		if (!filePath.startsWith(rootPath + "/")) return null;
		return filePath.substring(rootPath.length() + 1);
	}
}
