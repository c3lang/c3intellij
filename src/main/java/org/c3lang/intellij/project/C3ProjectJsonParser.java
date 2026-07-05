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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class C3ProjectJsonParser
{
	public static final @NotNull String DEFAULT_TARGET_TYPE = "executable";
	public static final @NotNull List<String> TARGET_TYPES = List.of(
		DEFAULT_TARGET_TYPE,
		"static-lib",
		"dynamic-lib",
		"benchmark",
		"test",
		"object-files",
		"prepare"
	);
	public static final @NotNull List<OptimizationLevel> OPTIMIZATION_LEVELS = List.of(
		new OptimizationLevel("", "Project default"),
		new OptimizationLevel("O0", "No optimizations"),
		new OptimizationLevel("O1", "Safe, optimized"),
		new OptimizationLevel("O2", "Unsafe, optimized"),
		new OptimizationLevel("O3", "O2 + single module"),
		new OptimizationLevel("O4", "O3 + relaxed math"),
		new OptimizationLevel("O5", "O4 + unsafe math"),
		new OptimizationLevel("Os", "O3 + small code"),
		new OptimizationLevel("Oz", "O3 + tiny code")
	);
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
			parseTargets(object)
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

	private static @NotNull List<TargetDefinition> parseTargets(@NotNull ObjectNode object)
	{
		JsonNode targets = object.get("targets");
		if (targets == null) return List.of();
		if (!targets.isObject())
		{
			throw new IllegalArgumentException("project.json targets field must be an object");
		}

		List<TargetDefinition> targetDefinitions = new ArrayList<>();
		Iterator<String> names = targets.fieldNames();
		while (names.hasNext())
		{
			String name = names.next().trim();
			if (name.isEmpty()) continue;

			JsonNode target = targets.get(name);
			if (!target.isObject())
			{
				throw new IllegalArgumentException("project.json target " + name + " must be an object");
			}
			String type = parseTargetType(name, target);
			String optimization = parseTargetOptimization(name, target);
			targetDefinitions.add(new TargetDefinition(name, type, optimization, name));
		}
		return List.copyOf(targetDefinitions);
	}

	private static @NotNull String parseTargetType(@NotNull String name, @NotNull JsonNode target)
	{
		JsonNode type = target.get("type");
		if (type == null) return DEFAULT_TARGET_TYPE;
		if (!type.isTextual())
		{
			throw new IllegalArgumentException("project.json target " + name + " type field must be a string");
		}

		String typeText = type.asText().trim();
		if (!isValidTargetType(typeText))
		{
			throw new IllegalArgumentException("Invalid target type for " + name + ": " + typeText);
		}
		return typeText;
	}

	private static @NotNull String parseTargetOptimization(@NotNull String name, @NotNull JsonNode target)
	{
		JsonNode optimization = target.get("opt");
		if (optimization == null) return "";
		if (!optimization.isTextual())
		{
			throw new IllegalArgumentException("project.json target " + name + " opt field must be a string");
		}

		String optimizationText = optimization.asText().trim();
		if (!isValidOptimizationLevel(optimizationText))
		{
			throw new IllegalArgumentException("Invalid optimization level for " + name + ": " + optimizationText);
		}
		return optimizationText;
	}

	public static boolean isValidTargetType(@NotNull String type)
	{
		return TARGET_TYPES.contains(type.trim());
	}

	public static boolean isValidOptimizationLevel(@NotNull String optimization)
	{
		String normalizedOptimization = optimization.trim();
		for (OptimizationLevel level : OPTIMIZATION_LEVELS)
		{
			if (level.key().equals(normalizedOptimization)) return true;
		}
		return false;
	}

	public static @NotNull List<TargetDefinition> normalizeTargets(@NotNull Collection<TargetDefinition> targets)
	{
		List<TargetDefinition> normalizedTargets = new ArrayList<>();
		Set<String> names = new LinkedHashSet<>();
		for (TargetDefinition target : targets)
		{
			String name = target.name().trim();
			if (name.isEmpty()) throw new IllegalArgumentException("Target name must not be empty");
			if (!names.add(name)) throw new IllegalArgumentException("Duplicate target name: " + name);

			String type = target.type().trim();
			if (type.isEmpty()) type = DEFAULT_TARGET_TYPE;
			if (!isValidTargetType(type)) throw new IllegalArgumentException("Invalid target type for " + name + ": " + type);

			String optimization = target.optimization().trim();
			if (!isValidOptimizationLevel(optimization)) throw new IllegalArgumentException("Invalid optimization level for " + name + ": " + optimization);
			normalizedTargets.add(new TargetDefinition(name, type, optimization, target.originalName().trim()));
		}
		return List.copyOf(normalizedTargets);
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
		ObjectNode updated = withoutIdeSettings(document);
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
		ObjectNode updated = withoutIdeSettings(document);
		updated.put("version", version);

		ArrayNode authorArray = updated.putArray("authors");
		for (String author : normalizeAuthors(authors))
		{
			authorArray.add(author);
		}
		return updated;
	}

	public static @NotNull ObjectNode withTargets(
			@NotNull ObjectNode document,
			@NotNull List<TargetDefinition> targets)
	{
		ObjectNode updated = withoutIdeSettings(document);
		JsonNode existingTargets = updated.get("targets");
		ObjectNode existingTargetObjects = existingTargets instanceof ObjectNode object
				? object : MAPPER.createObjectNode();
		ObjectNode targetObject = MAPPER.createObjectNode();

		for (TargetDefinition target : normalizeTargets(targets))
		{
			JsonNode existingTarget = null;
			if (!target.originalName().isBlank())
			{
				existingTarget = existingTargetObjects.get(target.originalName());
			}
			if (existingTarget == null) existingTarget = existingTargetObjects.get(target.name());

			ObjectNode targetDocument = existingTarget instanceof ObjectNode object
					? object.deepCopy() : MAPPER.createObjectNode();
			targetDocument.put("type", target.type());
			if (target.optimization().isBlank())
			{
				targetDocument.remove("opt");
			}
			else
			{
				targetDocument.put("opt", target.optimization());
			}
			targetObject.set(target.name(), targetDocument);
		}

		updated.set("targets", targetObject);
		return updated;
	}

	public static @NotNull ObjectNode withoutIdeSettings(@NotNull ObjectNode document)
	{
		ObjectNode updated = document.deepCopy();
		updated.remove("compiler");
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
		private final @NotNull List<TargetDefinition> targets;

		private ParsedProjectJson(
				@NotNull ObjectNode document,
				@NotNull List<String> sources,
				@NotNull String version,
				@NotNull List<String> authors,
				@NotNull List<TargetDefinition> targets)
		{
			this.document = document;
			this.sources = List.copyOf(sources);
			this.version = version;
			this.authors = List.copyOf(authors);
			this.targets = List.copyOf(targets);
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

		public @NotNull List<String> getTargetNames()
		{
			return targets.stream().map(TargetDefinition::name).toList();
		}

		public @NotNull List<TargetDefinition> getTargets()
		{
			return targets;
		}

	}

	public record TargetDefinition(
			@NotNull String name,
			@NotNull String type,
			@NotNull String optimization,
			@NotNull String originalName)
	{
		public TargetDefinition(@NotNull String name, @NotNull String type, @NotNull String originalName)
		{
			this(name, type, "", originalName);
		}
	}

	public record OptimizationLevel(@NotNull String key, @NotNull String description)
	{
		@Override
		public String toString()
		{
			return key.isBlank() ? description : key + " - " + description;
		}
	}
}
