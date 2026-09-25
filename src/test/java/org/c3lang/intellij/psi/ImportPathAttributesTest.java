package org.c3lang.intellij.psi;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class ImportPathAttributesTest extends BasePlatformTestCase
{
	public void testImportPathModuleNameIgnoresAttributes()
	{
		myFixture.configureByText("main.c3", """
			module app;
			import std::gfx::x11 @public, foo;
			""");

		List<C3ImportPath> imports = PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3ImportPath.class)
			.stream()
			.toList();

		assertEquals(2, imports.size());
		assertEquals(new ModuleName("std::gfx::x11"), imports.get(0).getModuleName());
		assertTrue(imports.get(0).isPublicImport());
		assertEquals(new ModuleName("foo"), imports.get(1).getModuleName());
		assertFalse(imports.get(1).isPublicImport());
	}

	public void testPublicImportAllowsQualifiedPrivateConstant()
	{
		List<PsiElement> resolved = resolveConst("""
			module std::gfx::x11;
			const int SOME_CONST @private = 1;

			module app;
			import std::gfx::x11 @public;

			int abc = x11::<caret>SOME_CONST;
			""");

		assertEquals(describe(resolved), 1, resolved.size());
		assertInstanceOf(resolved.get(0), C3ConstDeclarationStmt.class);
		assertEquals("std::gfx::x11::SOME_CONST", ((C3ConstDeclarationStmt) resolved.get(0)).getFqName().getFullName());
	}

	public void testOrdinaryImportDoesNotAllowQualifiedPrivateConstant()
	{
		List<PsiElement> resolved = resolveConst("""
			module std::gfx::x11;
			const int SOME_CONST @private = 1;

			module app;
			import std::gfx::x11;

			int abc = x11::<caret>SOME_CONST;
			""");

		assertEmpty(resolved);
	}

	public void testOnlyPublicIsValidImportAttribute()
	{
		myFixture.configureByText("main.c3", """
			module app;
			import std::gfx::x11 @private;
			""");

		List<HighlightInfo> errors = myFixture.doHighlighting(HighlightSeverity.ERROR);
		assertEquals(1, errors.size());
		assertEquals("Only @public is valid on imports.", errors.get(0).getDescription());
	}

	private List<PsiElement> resolveConst(String code)
	{
		myFixture.configureByText("main.c3", code);
		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		assertTrue(reference instanceof PsiPolyVariantReference);

		return java.util.Arrays.stream(((PsiPolyVariantReference) reference).multiResolve(false))
			.map(ResolveResult::getElement)
			.filter(java.util.Objects::nonNull)
			.toList();
	}

	private static String describe(List<PsiElement> elements)
	{
		return elements.stream()
			.map(element -> element.getClass().getSimpleName() + ": " + element.getText())
			.collect(java.util.stream.Collectors.joining("\n"));
	}
}
