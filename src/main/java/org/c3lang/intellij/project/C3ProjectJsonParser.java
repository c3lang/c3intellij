package org.c3lang.intellij.project;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

public final class C3ProjectJsonParser
{
	private static final String EMAIL_PATTERN = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
	private static final Pattern AUTHOR_PATTERN = Pattern.compile(
		"([^<>,@]+\\s+<" + EMAIL_PATTERN + ">|<" + EMAIL_PATTERN + ">|[^<>,@]+)"
	);
	private static final ObjectMapper MAPPER = new ObjectMapper(JsonFactory.builder()
			.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
			.enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
			.build());
	private static final ObjectWriter PRETTY_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

	private C3ProjectJsonParser()
	{
	}

	public static @NotNull ParsedProjectJson parse(@NotNull String text)
	{
		JsonNode document;
		try
		{
			document = MAPPER.readTree(text);
		}
		catch (JsonProcessingException e)
		{
			throw new IllegalArgumentException("project.json must be valid JSONC", e);
		}

		if (!(document instanceof ObjectNode object))
		{
			throw new IllegalArgumentException("project.json must contain a JSON object");
		}

		JsonNode sourcesElement = object.get("sources");
		if (sourcesElement == null)
		{
			throw new IllegalArgumentException("project.json must contain a sources field");
		}
		if (!sourcesElement.isArray())
		{
			throw new IllegalArgumentException("project.json sources field must be an array");
		}

		List<String> sources = parseSources(sourcesElement);
		return new ParsedProjectJson(
			object,
			sources,
			parseVersion(object),
			parseAuthors(object),
			parseCompilerName(object),
			parseStdlibOverridePath(object)
		);
	}

	private static @NotNull List<String> parseSources(@NotNull JsonNode sourceArray)
	{
		List<String> sourceTexts = new ArrayList<>();
		for (JsonNode sourceElement : sourceArray)
		{
			if (!sourceElement.isTextual())
			{
				throw new IllegalArgumentException("project.json sources entries must be strings");
			}

			sourceTexts.add(sourceElement.asText());
		}
		return normalizeSources(sourceTexts);
	}

	private static @NotNull String parseVersion(@NotNull ObjectNode object)
	{
		JsonNode version = object.get("version");
		if (version == null) return "";
		if (!version.isTextual())
		{
			throw new IllegalArgumentException("project.json version field must be a string");
		}
		return version.asText();
	}

	private static @NotNull List<String> parseAuthors(@NotNull ObjectNode object)
	{
		JsonNode authors = object.get("authors");
		if (authors == null) return List.of();
		if (!authors.isArray())
		{
			throw new IllegalArgumentException("project.json authors field must be an array");
		}

		List<String> authorTexts = new ArrayList<>();
		for (JsonNode author : authors)
		{
			if (!author.isTextual())
			{
				throw new IllegalArgumentException("project.json authors entries must be strings");
			}
			authorTexts.add(author.asText());
		}
		return normalizeAuthors(authorTexts);
	}

	private static @NotNull String parseCompilerName(@NotNull ObjectNode object)
	{
		JsonNode compiler = object.get("compiler");
		if (compiler == null) return "";
		if (!compiler.isObject())
		{
			throw new IllegalArgumentException("project.json compiler field must be an object");
		}

		JsonNode name = compiler.get("name");
		if (name == null) return "";
		if (!name.isTextual())
		{
			throw new IllegalArgumentException("project.json compiler name field must be a string");
		}
		return name.asText().trim();
	}

	private static @NotNull String parseStdlibOverridePath(@NotNull ObjectNode object)
	{
		JsonNode compiler = object.get("compiler");
		if (compiler == null) return "";
		if (!compiler.isObject())
		{
			throw new IllegalArgumentException("project.json compiler field must be an object");
		}

		JsonNode stdlibOverride = compiler.get("stdlib-override");
		if (stdlibOverride == null) return "";
		if (!stdlibOverride.isTextual())
		{
			throw new IllegalArgumentException("project.json compiler stdlib-override field must be a string");
		}
		return stdlibOverride.asText().trim();
	}

	public static @NotNull List<String> normalizeSources(@NotNull Collection<String> sourceTexts)
	{
		LinkedHashSet<String> sources = new LinkedHashSet<>();
		for (String sourceText : sourceTexts)
		{
			String source = sourceText.trim();
			if (source.isEmpty()) continue;
			if (!C3ProjectModel.isValidSourcePattern(source))
			{
				throw new IllegalArgumentException("Invalid source path: " + source);
			}
			sources.add(source);
		}
		if (sources.isEmpty())
		{
			throw new IllegalArgumentException("project.json sources field must contain at least one source path");
		}
		return List.copyOf(sources);
	}

	public static @NotNull ObjectNode withSources(@NotNull ObjectNode document, @NotNull List<String> sources)
	{
		ObjectNode updated = document.deepCopy();
		ArrayNode sourceArray = updated.putArray("sources");
		for (String source : normalizeSources(sources))
		{
			sourceArray.add(source);
		}
		return updated;
	}

	public static @NotNull ObjectNode withProjectSettings(
			@NotNull ObjectNode document,
			@NotNull String version,
			@NotNull List<String> authors)
	{
		ObjectNode updated = document.deepCopy();
		updated.put("version", version);

		ArrayNode authorArray = updated.putArray("authors");
		for (String author : normalizeAuthors(authors))
		{
			authorArray.add(author);
		}
		return updated;
	}

	public static @NotNull ObjectNode withCompilerSettings(
			@NotNull ObjectNode document,
			@NotNull String compilerName,
			@NotNull String stdlibOverridePath)
	{
		ObjectNode updated = document.deepCopy();
		ObjectNode compiler = updated.withObject("/compiler");
		String normalizedName = compilerName.trim();
		String normalizedStdlibOverride = stdlibOverridePath.trim();

		if (normalizedName.isEmpty())
		{
			compiler.remove("name");
		}
		else
		{
			compiler.put("name", normalizedName);
		}

		if (normalizedStdlibOverride.isEmpty())
		{
			compiler.remove("stdlib-override");
		}
		else
		{
			compiler.put("stdlib-override", normalizedStdlibOverride);
		}

		if (compiler.isEmpty())
		{
			updated.remove("compiler");
		}
		return updated;
	}

	public static @NotNull List<String> parseAuthorsText(@NotNull String text)
	{
		return normalizeAuthors(List.of(text.split(",")));
	}

	public static @NotNull String formatAuthorsText(@NotNull List<String> authors)
	{
		return String.join(", ", authors);
	}

	public static @NotNull List<String> normalizeAuthors(@NotNull Collection<String> authorTexts)
	{
		List<String> authors = new ArrayList<>();
		for (String authorText : authorTexts)
		{
			String author = authorText.trim();
			if (author.isEmpty()) continue;
			if (!isValidAuthor(author))
			{
				throw new IllegalArgumentException("Invalid author: " + author);
			}
			authors.add(author);
		}
		return List.copyOf(authors);
	}

	public static boolean isValidAuthor(@NotNull String author)
	{
		return AUTHOR_PATTERN.matcher(author.trim()).matches();
	}

	public static @NotNull String toJson(@NotNull ObjectNode document)
	{
		try
		{
			return PRETTY_WRITER.writeValueAsString(document) + "\n";
		}
		catch (JsonProcessingException e)
		{
			throw new IllegalArgumentException("Unable to write project.json", e);
		}
	}

	public static final class ParsedProjectJson
	{
		private final @NotNull ObjectNode document;
		private final @NotNull List<String> sources;
		private final @NotNull String version;
		private final @NotNull List<String> authors;
		private final @NotNull String compilerName;
		private final @NotNull String stdlibOverridePath;

		private ParsedProjectJson(
				@NotNull ObjectNode document,
				@NotNull List<String> sources,
				@NotNull String version,
				@NotNull List<String> authors,
				@NotNull String compilerName,
				@NotNull String stdlibOverridePath)
		{
			this.document = document;
			this.sources = List.copyOf(sources);
			this.version = version;
			this.authors = List.copyOf(authors);
			this.compilerName = compilerName;
			this.stdlibOverridePath = stdlibOverridePath;
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
	}
}
