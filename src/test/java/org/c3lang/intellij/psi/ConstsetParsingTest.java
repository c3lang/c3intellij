package org.c3lang.intellij.psi;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;
import java.util.stream.Collectors;

public class ConstsetParsingTest extends BasePlatformTestCase
{
	public void testStringValuedConstsetParses()
	{
		myFixture.configureByText("main.c3", """
			constset Scheme : ZString
			{
				FTP = "ftp",
				HTTP = "http",
				HTTPS = "https",
				FILE = "file"
			}
			""");

		assertNoPsiErrors();
	}

	public void testBareConstsetParses()
	{
		myFixture.configureByText("main.c3", """
			constset Scheme : ZString
			{
				FTP,
				HTTP,
				HTTPS,
				FILE,
			}
			""");

		assertNoPsiErrors();
	}

	public void testSingleStringValuedConstsetEntryParses()
	{
		myFixture.configureByText("main.c3", """
			constset Foo : ZString
			{
				ABC = "hello"
			}
			""");

		assertNoPsiErrors();
	}

	private void assertNoPsiErrors()
	{
		List<PsiErrorElement> errors =
			PsiTreeUtil.collectElementsOfType(myFixture.getFile(), PsiErrorElement.class)
				.stream()
				.toList();
		if (!errors.isEmpty())
		{
			fail(errors.stream()
				.map(error -> error.getTextRange() + ": " + error.getErrorDescription())
				.collect(Collectors.joining("\n")));
		}
	}
}
