package org.c3lang.intellij.project;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class C3ProjectJsonSchemaProviderFactory implements JsonSchemaProviderFactory, DumbAware
{
	@Override
	public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project)
	{
		return List.of(new C3ProjectJsonSchemaFileProvider(project));
	}

	private static final class C3ProjectJsonSchemaFileProvider implements JsonSchemaFileProvider
	{
		private final @NotNull Project project;

		private C3ProjectJsonSchemaFileProvider(@NotNull Project project)
		{
			this.project = project;
		}

		@Override
		public boolean isAvailable(@NotNull VirtualFile file)
		{
			if (!"project.json".equals(file.getName())) return false;

			VirtualFile projectJson = C3ProjectService.getInstance(project).getProjectJsonFile();
			return projectJson != null && projectJson.equals(file);
		}

		@Override
		public @NotNull String getName()
		{
			return "C3 project.json";
		}

		@Override
		public VirtualFile getSchemaFile()
		{
			return JsonSchemaProviderFactory.getResourceFile(
				C3ProjectJsonSchemaProviderFactory.class,
				"/schemas/c3-project.schema.json"
			);
		}

		@Override
		public @NotNull SchemaType getSchemaType()
		{
			return SchemaType.schema;
		}
	}
}
