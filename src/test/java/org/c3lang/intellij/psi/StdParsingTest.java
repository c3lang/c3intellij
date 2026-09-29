package org.c3lang.intellij.psi;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class StdParsingTest extends BasePlatformTestCase
{
	public void testStdLibraryParsesWithoutSyntaxErrors() throws IOException
	{
		Path stdRoot = stdRoot();
		assertTrue("Missing std testbed: " + stdRoot, Files.isDirectory(stdRoot));

		List<Path> files;
		try (var stream = Files.walk(stdRoot))
		{
			files = stream
				.filter(path -> path.toString().endsWith(".c3"))
				.filter(path -> matchesRequestedFile(stdRoot, path))
				.sorted(Comparator.comparing(Path::toString))
				.toList();
		}

		StringBuilder failures = new StringBuilder();
		for (Path file : files)
		{
			String text = Files.readString(file);
			myFixture.configureByText(file.getFileName().toString(), text);

			List<PsiErrorElement> errors = PsiTreeUtil.collectElementsOfType(myFixture.getFile(), PsiErrorElement.class)
				.stream()
				.toList();

			if (!errors.isEmpty())
			{
				failures.append(stdRoot.relativize(file)).append('\n');
				errors.stream()
					.limit(8)
					.forEach(error -> failures
						.append("  ")
						.append(lineAndColumn(text, error.getTextOffset()))
						.append(' ')
						.append(error.getErrorDescription())
						.append(": `")
						.append(error.getText().replace("\n", "\\n"))
						.append("` near `")
						.append(context(text, error.getTextOffset()).replace("\n", "\\n"))
						.append("` parents `")
						.append(parents(error))
						.append("` parentText `")
						.append(parentText(error).replace("\n", "\\n"))
						.append("`\n"));
			}
		}

		assertTrue(failures.toString(), failures.isEmpty());
	}

	private static Path stdRoot()
	{
		String configured = System.getProperty("c3.std.root");
		if (configured != null && !configured.isBlank()) return Path.of(configured);
		return Path.of(System.getProperty("user.dir")).resolve("std");
	}

	private static boolean matchesRequestedFile(Path stdRoot, Path path)
	{
		String requested = System.getProperty("c3.std.files");
		if (requested == null || requested.isBlank()) return true;
		String relative = stdRoot.relativize(path).toString();
		for (String item : requested.split(","))
		{
			if (relative.equals(item.trim())) return true;
		}
		return false;
	}

	private static String lineAndColumn(String text, int offset)
	{
		int line = 1;
		int column = 1;
		for (int i = 0; i < offset && i < text.length(); i++)
		{
			if (text.charAt(i) == '\n')
			{
				line++;
				column = 1;
			}
			else
			{
				column++;
			}
		}
		return line + ":" + column;
	}

	private static String context(String text, int offset)
	{
		int start = Math.max(0, offset - 40);
		int end = Math.min(text.length(), offset + 40);
		return text.substring(start, end);
	}

	private static String parents(PsiErrorElement error)
	{
		StringBuilder result = new StringBuilder();
		PsiElement element = error.getParent();
		for (int i = 0; i < 6 && element != null; i++)
		{
			if (i > 0) result.append(" > ");
			result.append(element.getClass().getSimpleName());
			element = element.getParent();
		}
		return result.toString();
	}

	private static String parentText(PsiErrorElement error)
	{
		PsiElement element = error.getParent();
		if (element == null) return "";
		String text = element.getText();
		if (text.length() <= 140) return text;
		return text.substring(Math.max(0, text.length() - 140));
	}
}
