package org.c3lang.intellij.project;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class C3ProjectService
{
	private static final Logger LOG = Logger.getInstance(C3ProjectService.class);

	private final @NotNull Project project;
	private @Nullable C3ProjectModel model;
	private @Nullable String projectJsonPath;
	private long projectJsonModificationStamp = -1;

	public C3ProjectService(@NotNull Project project)
	{
		this.project = project;
	}

	public static @NotNull C3ProjectService getInstance(@NotNull Project project)
	{
		return project.getService(C3ProjectService.class);
	}

	public boolean isC3Project()
	{
		return getProjectModel() != null;
	}

	public @Nullable C3ProjectModel getProjectModel()
	{
		VirtualFile projectRoot = getProjectRoot();
		if (projectRoot == null)
		{
			clearCache();
			return null;
		}

		VirtualFile projectJson = getProjectJsonFile(projectRoot);
		if (projectJson == null || !projectJson.isValid() || projectJson.isDirectory())
		{
			clearCache();
			return null;
		}

		String path = projectJson.getPath();
		long modificationStamp = projectJson.getModificationStamp();
		if (model != null && path.equals(projectJsonPath) && modificationStamp == projectJsonModificationStamp)
		{
			return model;
		}

		try
		{
			String text = new String(projectJson.contentsToByteArray(), StandardCharsets.UTF_8);
			C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse(text);
			model = new C3ProjectModel(
				projectRoot,
				projectJson,
				parsed.getDocument(),
				parsed.getSources(),
				parsed.getVersion(),
				parsed.getAuthors(),
				parsed.getCompilerName(),
				parsed.getStdlibOverridePath()
			);
			projectJsonPath = path;
			projectJsonModificationStamp = modificationStamp;
			return model;
		}
		catch (IOException | RuntimeException e)
		{
			LOG.debug("Unable to parse C3 project.json", e);
			clearCache();
			return null;
		}
	}

	public @Nullable VirtualFile getProjectJsonFile()
	{
		VirtualFile projectRoot = getProjectRoot();
		if (projectRoot == null) return null;
		return getProjectJsonFile(projectRoot);
	}

	public void saveProjectSettings(@NotNull String version, @NotNull List<String> authors) throws IOException
	{
		VirtualFile projectJson = findProjectJsonForWrite();
		String text = new String(projectJson.contentsToByteArray(), StandardCharsets.UTF_8);
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse(text);
		String updatedText = C3ProjectJsonParser.toJson(
			C3ProjectJsonParser.withProjectSettings(parsed.getDocument(), version, authors)
		);
		writeProjectJson(projectJson, updatedText);
	}

	public void saveSources(@NotNull List<String> sources) throws IOException
	{
		VirtualFile projectJson = findProjectJsonForWrite();
		String text = new String(projectJson.contentsToByteArray(), StandardCharsets.UTF_8);
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse(text);
		String updatedText = C3ProjectJsonParser.toJson(C3ProjectJsonParser.withSources(parsed.getDocument(), sources));
		writeProjectJson(projectJson, updatedText);
	}

	public void saveCompilerSettings(@NotNull String compilerName, @NotNull String stdlibOverridePath) throws IOException
	{
		VirtualFile projectJson = findProjectJsonForWrite();
		String text = new String(projectJson.contentsToByteArray(), StandardCharsets.UTF_8);
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse(text);
		String updatedText = C3ProjectJsonParser.toJson(
			C3ProjectJsonParser.withCompilerSettings(parsed.getDocument(), compilerName, stdlibOverridePath)
		);
		writeProjectJson(projectJson, updatedText);
	}

	public @NotNull List<String> getStdlibPaths()
	{
		C3ProjectModel projectModel = getProjectModel();
		if (projectModel != null && projectModel.hasStdlibOverride())
		{
			return List.of(projectModel.getStdlibOverridePath());
		}

		C3SettingsState settings = C3SettingsState.getInstance();
		if (projectModel != null && !projectModel.getCompilerName().isBlank())
		{
			for (C3SettingsState.CompilerProfile profile : settings.getCompilerProfiles())
			{
				if (projectModel.getCompilerName().equals(profile.name) && !profile.stdlibPath.isBlank())
				{
					return List.of(profile.stdlibPath);
				}
			}
		}

		String defaultStdlibPath = settings.getDefaultStdlibPath();
		if (!defaultStdlibPath.isBlank())
		{
			return List.of(defaultStdlibPath);
		}

		ArrayList<String> paths = new ArrayList<>();
		for (String path : settings.getStdlibPaths())
		{
			if (!path.isBlank()) paths.add(path);
		}
		return List.copyOf(paths);
	}

	private @NotNull VirtualFile findProjectJsonForWrite() throws IOException
	{
		VirtualFile projectRoot = getProjectRoot();
		if (projectRoot == null)
		{
			throw new IOException("Project root is not available");
		}

		VirtualFile projectJson = getProjectJsonFile(projectRoot);
		if (projectJson == null)
		{
			throw new IOException("project.json was not found");
		}
		return projectJson;
	}

	private void writeProjectJson(@NotNull VirtualFile projectJson, @NotNull String updatedText) throws IOException
	{
		byte[] updatedBytes = updatedText.getBytes(StandardCharsets.UTF_8);
		IOException[] error = new IOException[1];
		WriteCommandAction.runWriteCommandAction(project, "Update C3 Project Structure", null, () -> {
			try
			{
				projectJson.setBinaryContent(updatedBytes);
				clearCache();
			}
			catch (IOException e)
			{
				error[0] = e;
			}
		});
		if (error[0] != null)
		{
			throw error[0];
		}
	}

	public @NotNull List<VirtualFile> getSourceFiles()
	{
		C3ProjectModel projectModel = getProjectModel();
		if (projectModel == null) return Collections.emptyList();
		return projectModel.collectSourceFiles();
	}

	public boolean isProjectSourceFile(@NotNull VirtualFile file)
	{
		C3ProjectModel projectModel = getProjectModel();
		return projectModel != null && projectModel.isSourceFile(file);
	}

	public boolean acceptsIndexedFile(@NotNull VirtualFile file)
	{
		C3ProjectModel projectModel = getProjectModel();
		return projectModel == null || !projectModel.isUnderProjectRoot(file) || projectModel.isSourceFile(file);
	}

	public @NotNull GlobalSearchScope getSearchScope()
	{
		GlobalSearchScope allScope = GlobalSearchScope.allScope(project);
		return new GlobalSearchScope(project)
		{
			@Override
			public boolean contains(@NotNull VirtualFile file)
			{
				return allScope.contains(file) && acceptsIndexedFile(file);
			}

			@Override
			public boolean isSearchInModuleContent(@NotNull Module aModule)
			{
				return allScope.isSearchInModuleContent(aModule);
			}

			@Override
			public boolean isSearchInLibraries()
			{
				return allScope.isSearchInLibraries();
			}
		};
	}

	private @Nullable VirtualFile getProjectRoot()
	{
		String basePath = project.getBasePath();
		if (basePath == null) return null;
		return LocalFileSystem.getInstance().findFileByPath(basePath);
	}

	private static @Nullable VirtualFile getProjectJsonFile(@NotNull VirtualFile projectRoot)
	{
		VirtualFile projectJson = projectRoot.findChild("project.json");
		if (projectJson == null || !projectJson.isValid() || projectJson.isDirectory()) return null;
		return projectJson;
	}

	private void clearCache()
	{
		model = null;
		projectJsonPath = null;
		projectJsonModificationStamp = -1;
	}
}
