package org.c3lang.intellij.annotation;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.C3SyntaxHighlighter;
import org.c3lang.intellij.annotation.fix.AddDynamicAttributeFix;
import org.c3lang.intellij.annotation.fix.AddSelfParameterFix;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class C3AnnotatorTest extends BasePlatformTestCase
{
	public void testSemanticHighlighting()
	{
		myFixture.configureByText("main.c3", """
			module std::collections::enummap;
			import std::io;

			struct EnumMap
			{
				int[] values;
			}

			fn void EnumMap.init(EnumMap* this, int init_value)
			{
				foreach(&a : this.values)
				{
					*a = init_value;
				}
				io::printn("hello");
			}

			struct MyTr
			{
				int a;
			}

			fn int MyTr.test(MyTr* self)
			{
				return self.a;
			}

			fn void caller(MyTr tr)
			{
				tr.test();
			}
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertNotNull(highlights);

		assertHasHighlight(highlights, "enummap", C3SyntaxHighlighter.MODULE_KEY);
		assertHasHighlight(highlights, "collections", C3SyntaxHighlighter.MODULE_KEY);
		assertHasHighlight(highlights, "io", C3SyntaxHighlighter.MODULE_KEY);
		assertHasHighlight(highlights, "this", C3SyntaxHighlighter.PARAMETER_KEY);
		assertHasHighlight(highlights, "init_value", C3SyntaxHighlighter.PARAMETER_KEY);
		assertHasHighlight(highlights, "a", C3SyntaxHighlighter.LOCAL_VARIABLE_KEY);
		assertHasHighlight(highlights, "values", C3SyntaxHighlighter.FIELD_KEY);
		assertHasHighlight(highlights, "printn", C3SyntaxHighlighter.FUNCTION_CALL_KEY);
		assertHasHighlight(highlights, "self", C3SyntaxHighlighter.PARAMETER_KEY);
		assertHasHighlight(highlights, "test", C3SyntaxHighlighter.METHOD_CALL_KEY);
	}

	public void testMissingDynamicAttributeIsError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}

			fn String Baz.myname(Baz* self)
			{
				return "Baz";
			}
			""");

		List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "must be marked '@dynamic'");
		assertEquals("Expected one missing-@dynamic error, got: " + errors, 1, errors.size());
	}

	public void testAddDynamicQuickFix()
	{
		myFixture.configureByText("main.c3", """
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}

			fn String Baz.my<caret>name(Baz* self)
			{
				return "Baz";
			}
			""");

		myFixture.doHighlighting();
		myFixture.launchAction(myFixture.findSingleIntention("Add '@dynamic'"));
		myFixture.checkResult("""
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}

			fn String Baz.myname(Baz* self) @dynamic
			{
				return "Baz";
			}
			""");
	}

	public void testMissingInterfaceImplIsError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}
			""");

		List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "does not implement interface method");
		assertEquals("Expected one missing-impl error, got: " + errors, 1, errors.size());
	}

	public void testEmptyStructIsError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
			}
			""");

		List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Zero sized structs are not permitted.");
		assertEquals("Expected one empty-struct error, got: " + errors, 1, errors.size());
	}

	public void testFileWithoutModuleDoesNotCrashIndexing()
	{
		myFixture.configureByText("main.c3", """
			struct Foo
			{
				int x;
			}
			fn void foo()
			{
				Foo f;
				f.x = 1;
			}
			""");

		// Must not throw (e.g. in stub building) and must not report stub errors.
		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertNotNull(highlights);
	}

	public void testCorrectInterfaceImplHasNoErrors()
	{
		myFixture.configureByText("main.c3", """
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}

			fn void Baz.test(Baz* self)
			{
				self.x = 0;
			}

			fn String Baz.myname(Baz* self) @dynamic
			{
				return "Baz";
			}
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertTrue("Unexpected @dynamic error, got: " + highlights,
			errorsWithText(highlights, "@dynamic").isEmpty());
		assertTrue("Unexpected missing-impl error, got: " + highlights,
			errorsWithText(highlights, "does not implement").isEmpty());
		assertTrue("Unexpected empty-struct error, got: " + highlights,
			errorsWithText(highlights, "Zero sized").isEmpty());
	}

	public void testMethodWithoutSelfIsError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
				int f;
			}

			fn void Test.test() {

			}
			""");

		List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "must start with an argument");
		assertEquals("Expected one missing-self error, got: " + errors, 1, errors.size());
	}

	public void testMethodWithWrongFirstParamIsError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
			}

			fn void Test.test(int x) {

			}
			""");

		List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "must start with an argument");
		assertEquals("Expected one missing-self error, got: " + errors, 1, errors.size());
	}

	public void testAddSelfParameterQuickFixEmpty()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
				int f;
			}

			fn void Test.te<caret>st() {

			}
			""");

		myFixture.doHighlighting();
		myFixture.launchAction(myFixture.findSingleIntention("Add '&self' parameter"));
		myFixture.checkResult("""
			module test;

			struct Test {
				int b;
				int f;
			}

			fn void Test.test(&self) {

			}
			""");
	}

	public void testAddSelfParameterQuickFixWithExistingParams()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
			}

			fn void Test.te<caret>st(int x) {

			}
			""");

		myFixture.doHighlighting();
		myFixture.launchAction(myFixture.findSingleIntention("Add '&self' parameter"));
		myFixture.checkResult("""
			module test;

			struct Test {
				int b;
			}

			fn void Test.test(&self, int x) {

			}
			""");
	}

	public void testMethodWithSelfHasNoError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
				int f;
			}

			fn void Test.test(Test* self) {

			}

			fn void test() {

			}
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertTrue("Unexpected missing-self error, got: " + highlights,
			errorsWithText(highlights, "must start with an argument").isEmpty());
	}

	public void testMethodWithAmpSelfHasNoError()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
				int f;
			}

			fn void Test.test(&self) {
				self.b = 1;
			}

			fn void test() {

			}
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertTrue("Unexpected missing-self error, got: " + highlights,
			errorsWithText(highlights, "must start with an argument").isEmpty());
	}

	public void testSelfParameterFixPreviewDoesNotThrow()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Test {
				int b;
			}

			fn void Test.test() {

			}
			""");

		C3FuncDef funcDef = PsiTreeUtil.findChildOfType(myFixture.getFile(), C3FuncDef.class);
		assertNotNull(funcDef);
		AddSelfParameterFix fix = new AddSelfParameterFix(funcDef, "Test", "self");
		IntentionPreviewInfo preview = fix.generatePreview(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile());
		assertTrue("Expected a diff preview, got: " + preview, preview instanceof IntentionPreviewInfo.CustomDiff);
		assertTrue(((IntentionPreviewInfo.CustomDiff) preview).modifiedText().contains("&self"));
	}

	public void testDynamicAttributeFixPreviewDoesNotThrow()
	{
		myFixture.configureByText("main.c3", """
			module test;

			interface MyName {
				fn String myname();
			}

			struct Baz (MyName)
			{
				int x;
			}

			fn String Baz.myname(Baz* self)
			{
				return "Baz";
			}
			""");

		C3FuncDef funcDef = null;
		for (C3FuncDef candidate : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3FuncDef.class))
		{
			if ("myname".equals(candidate.getNameIdent()) && candidate.getParent() instanceof C3FuncDefinition)
			{
				funcDef = candidate;
			}
		}
		assertNotNull(funcDef);
		AddDynamicAttributeFix fix = new AddDynamicAttributeFix(funcDef);
		IntentionPreviewInfo preview = fix.generatePreview(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile());
		assertTrue("Expected a diff preview, got: " + preview, preview instanceof IntentionPreviewInfo.CustomDiff);
		assertTrue(((IntentionPreviewInfo.CustomDiff) preview).modifiedText().contains("@dynamic"));
	}

	public void testSubscriptedGlobalConstFieldHasOwnStructType()
	{
		myFixture.addFileToProject("qoi.c3", """
			module qoi;

			struct OpRGBA
			{
				char alpha;
			}
			""");
		myFixture.configureByText("main.c3", """
			module ascii;

			struct GlyphInfo
			{
				bool alpha;
			}

			const GlyphInfo[4] ASCII_LOOKUP;

			macro bool is_alpha(char c) => ASCII_LOOKUP[c].alpha;
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertTrue("Field through a subscripted global const must keep its own struct type, got: "
				+ errorsWithText(highlights, "Cannot return"),
			errorsWithText(highlights, "Cannot return").isEmpty());
	}

	public void testBitstructFieldDeclarationHighlighting()
	{
		myFixture.configureByText("main.c3", """
			module test;

			bitstruct Sb : char
			{
				int alpha : 0..2;
				bool beta : 3;
			}
			""");

		List<HighlightInfo> highlights = myFixture.doHighlighting();
		assertHasHighlight(highlights, "alpha", C3SyntaxHighlighter.FIELD_KEY);
		assertHasHighlight(highlights, "beta", C3SyntaxHighlighter.FIELD_KEY);
	}

	private static @NotNull List<HighlightInfo> errorsWithText(@NotNull List<HighlightInfo> highlights, @NotNull String textPart)
	{
		List<HighlightInfo> result = new ArrayList<>();
		for (HighlightInfo info : highlights)
		{
			if (info.getSeverity() == HighlightSeverity.ERROR
				&& info.getDescription() != null
				&& info.getDescription().contains(textPart))
			{
				result.add(info);
			}
		}
		return result;
	}

	private static void assertHasHighlight(List<HighlightInfo> highlights, String text, TextAttributesKey key)
	{
		boolean found = highlights.stream().anyMatch(h ->
			text.equals(h.getText()) && key.equals(h.forcedTextAttributesKey)
		);
		assertTrue("Expected highlight with text '" + text + "' and key " + key.getExternalName(), found);
	}
}
