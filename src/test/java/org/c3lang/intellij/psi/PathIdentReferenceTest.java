package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class PathIdentReferenceTest extends BasePlatformTestCase
{
	public void testCompileTimeMacroParameterWithDefaultResolvesInMacroBody()
	{
		myFixture.configureByText("main.c3", """
			module test;

			macro void @assert_leak($report = true; @body()) @builtin
			{
				$if $rep<caret>ort:
				$endif
				@body();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();

		assertInstanceOf(resolved, C3Parameter.class);
		assertEquals("$report", ((C3Parameter) resolved).getNameIdent());
	}
}
