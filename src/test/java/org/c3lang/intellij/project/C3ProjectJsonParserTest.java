package org.c3lang.intellij.project;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class C3ProjectJsonParserTest
{
	@Test
	public void parsesJsoncSources()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  // project sources
				  "homepage": "https://example.com//kept",
				  "sources": [
				    "src",
				    "lib/**", // trailing line comment
				    "vendor/*",
				  ],
				  "targets": {
				    "app": {
				      "type": "executable",
				    },
				  },
				  /*
				   * block comment
				   */
				}
				""");

		assertEquals(List.of("src", "lib/**", "vendor/*"), parsed.getSources());
		assertEquals("https://example.com//kept", parsed.getDocument().get("homepage").asText());
	}

	@Test
	public void requiresSources()
	{
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parse("""
				{
				  "targets": {}
				}
				"""));
	}

	@Test
	public void requiresSourcesToBeArrayOfStrings()
	{
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parse("""
				{
				  "sources": "src"
				}
				"""));

		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parse("""
				{
				  "sources": [ "src", 3 ]
				}
				"""));
	}

	@Test
	public void rejectsEmptySources()
	{
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parse("""
				{
				  "sources": [ "" ]
				}
				"""));
	}

	@Test
	public void normalizesAndDeduplicatesSources()
	{
		assertEquals(
			List.of("src/**", "lib"),
			C3ProjectJsonParser.normalizeSources(List.of(" src/** ", "", "lib", "src/**"))
		);
	}

	@Test
	public void writesSourcesAsNormalJson()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  // dropped on save
				  "sources": [
				    "src",
				  ],
				  "targets": {
				    "app": {
				      "type": "executable",
				    },
				  },
				}
				""");

		String json = C3ProjectJsonParser.toJson(
			C3ProjectJsonParser.withSources(parsed.getDocument(), List.of("src/**", "generated"))
		);

		assertEquals("""
				{
				  "sources" : [ "src/**", "generated" ],
				  "targets" : {
				    "app" : {
				      "type" : "executable"
				    }
				  }
				}
				""", json);
	}

	@Test
	public void parsesProjectMetadata()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  "version": "1.2.3",
				  "authors": [
				    "John Doe <john.doe@example.com>",
				    "Jane Doe",
				    "<jane@example.com>"
				  ],
				  "sources": [ "src/**" ]
				}
				""");

		assertEquals("1.2.3", parsed.getVersion());
		assertEquals(
			List.of("John Doe <john.doe@example.com>", "Jane Doe", "<jane@example.com>"),
			parsed.getAuthors()
		);
	}

	@Test
	public void parsesCommaSeparatedAuthors()
	{
		assertEquals(
			List.of("John Doe <john.doe@example.com>", "Jane Doe", "<jane@example.com>"),
			C3ProjectJsonParser.parseAuthorsText(
				"John Doe <john.doe@example.com>, Jane Doe, <jane@example.com>"
			)
		);
	}

	@Test
	public void rejectsInvalidAuthors()
	{
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parseAuthorsText("john.doe@example.com"));
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parseAuthorsText("John Doe <john.doe>"));
		assertThrows(IllegalArgumentException.class, () -> C3ProjectJsonParser.parseAuthorsText("John <john@example.com"));
	}

	@Test
	public void writesProjectSettingsAsNormalJson()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  "version": "0.1.0",
				  "authors": [
				    "Old Author",
				  ],
				  "sources": [ "src/**" ],
				  "targets": {
				    "app": {
				      "type": "executable",
				    },
				  },
				}
				""");

		String json = C3ProjectJsonParser.toJson(
			C3ProjectJsonParser.withProjectSettings(
				parsed.getDocument(),
				"1.0.0",
				List.of("John Doe <john.doe@example.com>", "<jane@example.com>")
			)
		);

		assertEquals("""
				{
				  "version" : "1.0.0",
				  "authors" : [ "John Doe <john.doe@example.com>", "<jane@example.com>" ],
				  "sources" : [ "src/**" ],
				  "targets" : {
				    "app" : {
				      "type" : "executable"
				    }
				  }
				}
				""", json);
	}

	@Test
	public void parsesCompilerSettings()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  "sources": [ "src/**" ],
				  "compiler": {
				    "name": "c3c-0.7",
				    "stdlib-override": "/opt/c3/lib"
				  }
				}
				""");

		assertEquals("c3c-0.7", parsed.getCompilerName());
		assertEquals("/opt/c3/lib", parsed.getStdlibOverridePath());
	}

	@Test
	public void writesCompilerSettings()
	{
		C3ProjectJsonParser.ParsedProjectJson parsed = C3ProjectJsonParser.parse("""
				{
				  "sources": [ "src/**" ],
				  "compiler": {
				    "name": "old",
				    "stdlib-override": "/old/lib"
				  }
				}
				""");

		String json = C3ProjectJsonParser.toJson(
			C3ProjectJsonParser.withCompilerSettings(parsed.getDocument(), "default", "")
		);

		assertEquals("""
				{
				  "sources" : [ "src/**" ],
				  "compiler" : {
				    "name" : "default"
				  }
				}
				""", json);
	}
}
