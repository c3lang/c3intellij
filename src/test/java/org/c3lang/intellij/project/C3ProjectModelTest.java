package org.c3lang.intellij.project;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class C3ProjectModelTest
{
	@Test
	public void bareDirectoryMatchesNonRecursiveC3Files()
	{
		assertTrue(C3ProjectModel.matchesSource("src", "src/main.c3"));
		assertTrue(C3ProjectModel.matchesSource("src", "src/types.c3i"));
		assertFalse(C3ProjectModel.matchesSource("src", "src/nested/main.c3"));
		assertFalse(C3ProjectModel.matchesSource("src", "test/main.c3"));
	}

	@Test
	public void singleStarDirectoryMatchesNonRecursiveC3Files()
	{
		assertTrue(C3ProjectModel.matchesSource("src/*", "src/main.c3"));
		assertTrue(C3ProjectModel.matchesSource("src/*", "src/types.c3i"));
		assertFalse(C3ProjectModel.matchesSource("src/*", "src/nested/main.c3"));
		assertFalse(C3ProjectModel.matchesSource("src/*", "test/main.c3"));
	}

	@Test
	public void doubleStarDirectoryMatchesRecursiveC3Files()
	{
		assertTrue(C3ProjectModel.matchesSource("src/**", "src/main.c3"));
		assertTrue(C3ProjectModel.matchesSource("src/**", "src/nested/main.c3"));
		assertFalse(C3ProjectModel.matchesSource("src/**", "test/main.c3"));
	}

	@Test
	public void fileSourceMatchesOnlyExactFile()
	{
		assertTrue(C3ProjectModel.matchesSource("src/foo.c3", "src/foo.c3"));
		assertFalse(C3ProjectModel.matchesSource("src/foo.c3", "src/bar.c3"));
		assertFalse(C3ProjectModel.matchesSource("src/foo.c3", "src/foo.c3i"));
	}

	@Test
	public void normalizesRelativeSyntax()
	{
		assertTrue(C3ProjectModel.matchesSource("./src\\**", "src/nested/main.c3"));
		assertTrue(C3ProjectModel.matchesSource("/src/*", "./src/main.c3"));
	}

	@Test
	public void validatesSourcePatterns()
	{
		assertTrue(C3ProjectModel.isValidSourcePattern("src"));
		assertTrue(C3ProjectModel.isValidSourcePattern("src/*"));
		assertTrue(C3ProjectModel.isValidSourcePattern("src/**"));
		assertTrue(C3ProjectModel.isValidSourcePattern("src/foo.c3"));

		assertFalse(C3ProjectModel.isValidSourcePattern(""));
		assertFalse(C3ProjectModel.isValidSourcePattern("*"));
		assertFalse(C3ProjectModel.isValidSourcePattern("src*"));
		assertFalse(C3ProjectModel.isValidSourcePattern("src/**/foo.c3"));
	}
}
