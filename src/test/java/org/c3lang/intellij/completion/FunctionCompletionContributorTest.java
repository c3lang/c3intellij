package org.c3lang.intellij.completion;

import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.ModuleName;

import java.util.Arrays;

public class FunctionCompletionContributorTest extends BasePlatformTestCase
{
	public void testModuleSectionReportsOwnModuleName()
	{
		myFixture.configureByText("foo.c3", """
			module foo;

			fn void test()
			{
			}
			""");

		C3ModuleDefinition moduleDefinition =
			PsiTreeUtil.findChildOfType(myFixture.getFile(), C3ModuleDefinition.class);

		assertNotNull(moduleDefinition);
		assertEquals(new ModuleName("foo"), moduleDefinition.getModuleName());
	}

	public void testSameModuleFunctionCompletionFromDifferentFileDoesNotQualifyOrImportSelf()
	{
		myFixture.addFileToProject("foo.c3", """
			module foo;

			fn void test()
			{
			}
			""");

		myFixture.configureByText("foo2.c3", """
			module foo;

			fn void test2()
			{
				te<caret>
			}
			""");

		myFixture.completeBasic();
		LookupElement item = findLookupElement("test");
		assertNotNull(item);

		LookupEx lookup = myFixture.getLookup();
		assertNotNull(lookup);
		lookup.setCurrentItem(item);
		myFixture.type('\n');

		myFixture.checkResult("""
			module foo;

			fn void test2()
			{
				test<caret>
			}
			""");
	}

	public void testParentModuleFunctionCompletionDoesNotImportParent()
	{
		myFixture.configureByText("main.c3", """
			module gfx;

			fn void test()
			{
			}

			module gfx::foo;

			fn void test2()
			{
				te<caret>
			}
			""");

		myFixture.completeBasic();
		LookupElement item = findLookupElement("test");
		assertNotNull(item);

		LookupEx lookup = myFixture.getLookup();
		assertNotNull(lookup);
		lookup.setCurrentItem(item);
		myFixture.type('\n');

		myFixture.checkResult("""
			module gfx;

			fn void test()
			{
			}

			module gfx::foo;

			fn void test2()
			{
				gfx::test<caret>
			}
			""");
	}

	private LookupElement findLookupElement(String lookupString)
	{
		LookupElement[] elements = myFixture.getLookupElements();
		assertNotNull(elements);
		return Arrays.stream(elements)
			.filter(element -> element.getAllLookupStrings().contains(lookupString))
			.findFirst()
			.orElse(null);
	}
}
