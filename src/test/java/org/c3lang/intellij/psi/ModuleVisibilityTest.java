package org.c3lang.intellij.psi;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.intention.ImportModuleInspection;

import java.util.List;

public class ModuleVisibilityTest extends BasePlatformTestCase
{
	public void testSubmoduleCanReferenceParentModuleFunctionWithoutImport()
	{
		myFixture.configureByText("main.c3", """
			module gfx;
			fn void x()
			{
			}

			module gfx::foo;
			fn void test()
			{
				gfx::<caret>x();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		assertTrue(reference instanceof PsiPolyVariantReference);

		ResolveResult[] resolved = ((PsiPolyVariantReference) reference).multiResolve(false);
		assertEquals(1, resolved.length);
		PsiElement element = resolved[0].getElement();
		assertInstanceOf(element, C3CallablePsiElement.class);
		assertEquals("gfx::x", ((C3CallablePsiElement) element).getFqName().getFullName());
	}

	public void testSubmoduleParentReferenceDoesNotRequestImport()
	{
		myFixture.configureByText("main.c3", """
			module gfx;
			fn void x()
			{
			}

			module gfx::foo;
			fn void test()
			{
				gfx::x();
			}
			""");
		myFixture.enableInspections(new LocalInspectionTool[]{new ImportModuleInspection()});

		List<HighlightInfo> errors = myFixture.doHighlighting(HighlightSeverity.ERROR);
		assertEmpty(errors);
	}

	public void testSubmoduleCanUseQualifiedParentVariableWithoutImportProblem()
	{
		myFixture.configureByText("main.c3", """
			module gfx;
			int x;

			module gfx::foo;
			fn void test()
			{
				int y = gfx::x;
			}
			""");
		myFixture.enableInspections(new LocalInspectionTool[]{new ImportModuleInspection()});

		List<HighlightInfo> errors = myFixture.doHighlighting(HighlightSeverity.ERROR);
		assertEmpty(errors);
	}
}
