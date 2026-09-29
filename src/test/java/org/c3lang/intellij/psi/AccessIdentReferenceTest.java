package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccessIdentReferenceTest extends BasePlatformTestCase
{
	private static final String DECLARATIONS = """
		module test;

		struct Stream
		{
			int read_byte;
		}

		struct Other
		{
			int read_byte;
		}

		fn int Stream.read_byte(Stream* stream)
		{
			return 1;
		}

		fn int Other.read_byte(Other* other)
		{
			return 2;
		}

		fn int read_byte()
		{
			return 3;
		}
		""";

	public void testUnknownReceiverInvocationResolvesMethodsByName()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			%s

			fn void caller(stream)
			{
				stream.read_<caret>byte();
			}
			""".formatted(DECLARATIONS));

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 2, methods.size());
		assertTrue(methods.stream().allMatch(method -> method.getType() != null));
		assertTrue(methods.stream().allMatch(method -> method.getFqName().getName().endsWith(".read_byte")));
		assertEquals(
			List.of("Other.read_byte", "Stream.read_byte"),
			methods.stream()
				.map(method -> method.getPresentation().getPresentableText())
				.sorted()
				.collect(Collectors.toList())
		);
		assertEquals(0, instancesOf(resolved, C3StructMemberDeclaration.class).size());
	}

	public void testUnknownReceiverAccessResolvesFieldsBeforeMethods()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			%s

			fn void caller(stream)
			{
				stream.read_<caret>byte;
			}
			""".formatted(DECLARATIONS));

		List<C3StructMemberDeclaration> fields = instancesOf(resolved, C3StructMemberDeclaration.class);
		assertEquals(describe(resolved), 2, fields.size());
		assertTrue(fields.stream().allMatch(field -> "read_byte".equals(field.getNameIdent())));
		assertEquals(0, instancesOf(resolved, C3CallablePsiElement.class).size());
	}

	public void testTypedReceiverMethodResolvesExactMethod()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct MyTr
			{
				int a;
			}

			fn int MyTr.test()
			{
				return 0;
			}

			fn void caller(MyTr tr)
			{
				tr.te<caret>st();
			}
			""");

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 1, methods.size());
		assertEquals("MyTr.test", methods.get(0).getFqName().getName());
	}

	public void testPointerReceiverMethodResolvesExactMethod()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct MyTr
			{
				int a;
			}

			fn int MyTr.test(MyTr* self)
			{
				return 0;
			}

			fn void caller(MyTr* tr)
			{
				tr.te<caret>st();
			}
			""");

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 1, methods.size());
		assertEquals("MyTr.test", methods.get(0).getFqName().getName());
	}

	public void testThisReceiverMethodResolvesExactMethod()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct MyTr
			{
				int a;
			}

			fn int MyTr.test(MyTr* this)
			{
				return 0;
			}

			fn void MyTr.other(MyTr* this)
			{
				this.te<caret>st();
			}
			""");

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 1, methods.size());
		assertEquals("MyTr.test", methods.get(0).getFqName().getName());
	}

	public void testInterfaceReceiverMethodResolvesInterfaceMethod()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			interface MyName {
				fn String myname();
			}

			fn void caller(MyName* named)
			{
				named.my<caret>name();
			}
			""");

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 1, methods.size());
		assertTrue(methods.get(0) instanceof C3FuncDef);
		assertEquals("myname", methods.get(0).getFqName().getName());
		assertTrue(methods.get(0).getParent() instanceof C3InterfaceBody);
	}

	public void testStructMethodImplementingInterfaceResolvesImpl()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
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

			fn void caller(Baz baz)
			{
				baz.my<caret>name();
			}
			""");

		List<C3CallablePsiElement> methods = instancesOf(resolved, C3CallablePsiElement.class);
		assertEquals(describe(resolved), 1, methods.size());
		assertEquals("Baz.myname", methods.get(0).getFqName().getName());
	}

	public void testSelfFieldAccessInInterfaceImpl()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
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
				self.<caret>x = 0;
			}
			""");

		List<C3StructMemberDeclaration> fields = instancesOf(resolved, C3StructMemberDeclaration.class);
		assertEquals(describe(resolved), 1, fields.size());
		assertEquals("x", fields.get(0).getNameIdent());
	}

	public void testSelfFieldAccessWithAmpSelf()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct Baz
			{
				int x;
			}

			fn void Baz.test(&self)
			{
				self.<caret>x = 0;
			}
			""");

		List<C3StructMemberDeclaration> fields = instancesOf(resolved, C3StructMemberDeclaration.class);
		assertEquals(describe(resolved), 1, fields.size());
		assertEquals("x", fields.get(0).getNameIdent());
	}

	public void testVectorSwizzleDoesNotResolveToUnrelatedStructField()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct Baz
			{
				int x;
			}

			struct Test
			{
				int b;
				int f;
			}

			fn int main(String[] args)
			{
				int[<8>] v1 = { 1, 2, 3, 4, 5, 6, 7, 8 };
				int[<8>] sum = v1;
				int first = sum.<caret>x;
				return 0;
			}
			""");

		assertTrue("Vector swizzle must not resolve to an unrelated struct field, got: " + describe(resolved),
			resolved.isEmpty());
	}

	public void testMissingStructFieldDoesNotResolveToUnrelatedStructField()
	{
		List<PsiElement> resolved = resolveAccessIdent("""
			module test;

			struct Baz
			{
				int x;
			}

			struct Test
			{
				int b;
				int f;
			}

			fn void caller(Test t)
			{
				t.non<caret>existent();
			}
			""");

		assertTrue("Missing member must not resolve to an unrelated struct field, got: " + describe(resolved),
			resolved.isEmpty());
	}

	public void testSubscriptedGlobalConstFieldResolvesOwnStruct()
	{
		myFixture.addFileToProject("qoi.c3", """
			module qoi;

			struct OpRGBA
			{
				char alpha;
			}
			""");
		List<PsiElement> resolved = resolveAccessIdent("""
			module ascii;

			bitstruct GlyphInfo : char
			{
				bool alpha : 0;
			}

			const GlyphInfo[4] ASCII_LOOKUP;

			macro bool is_alpha(char c) => ASCII_LOOKUP[c].alp<caret>ha;
			""");

		assertEquals("Field through a subscripted global const must resolve, got: " + describe(resolved),
			1, resolved.size());
		assertTrue("Expected a bitstruct field, got: " + describe(resolved),
			resolved.get(0) instanceof C3BitstructDef);
	}

	public void testBitstructFieldNavigatesFromAtMacroBody()
	{
		myFixture.configureByText("main.c3", """
			module ascii;

			bitstruct AsciiProps : char
			{
				bool lower : 0;
				bool upper : 1;
			}

			const AsciiProps[4] ASCII_LOOKUP;

			macro bool @is_lower(c) => ASCII_LOOKUP[c].low<caret>er;
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement target = reference.resolve();
		assertTrue("Member access must navigate to the bitstruct field, got: " + target,
			target instanceof C3BitstructDef);
		assertEquals("lower", target.getText().contains("lower") ? "lower" : target.getText());
	}

	public void testBitstructFieldNavigatesAcrossFiles()
	{
		myFixture.addFileToProject("props.c3", """
			module ascii;

			bitstruct AsciiProps : char
			{
				bool lower : 0;
			}
			""");
		myFixture.configureByText("main.c3", """
			module ascii;

			const AsciiProps[4] ASCII_LOOKUP;

			fn bool is_lower(char c)
			{
				return ASCII_LOOKUP[c].low<caret>er;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement target = reference.resolve();
		assertTrue("Member access must navigate to the bitstruct field in another file, got: " + target,
			target instanceof C3BitstructDef);
	}

	public void testBitstructFieldHasFindUsages()
	{
		myFixture.configureByText("main.c3", """
			module ascii;

			bitstruct AsciiProps : char
			{
				bool lower : 0;
			}

			const AsciiProps[4] ASCII_LOOKUP;

			fn bool is_lower(char c)
			{
				return ASCII_LOOKUP[c].lower;
			}
			""");

		C3BitstructDef field = PsiTreeUtil.findChildOfType(myFixture.getFile(), C3BitstructDef.class);
		assertNotNull(field);
		assertTrue("Find Usages must be available on bitstruct fields",
			new org.c3lang.intellij.findUsages.C3FindUsagesProvider().canFindUsagesFor(field));
		java.util.Collection<?> usages = myFixture.findUsages(field);
		assertEquals("Expected one usage, got: " + usages, 1, usages.size());
	}

	public void testBitstructFieldUsagesIncludeRangeInit()
	{
		myFixture.configureByText("main.c3", """
			module ascii;

			bitstruct CharType : char
			{
				bool control : 0;
				bool space : 1;
			}

			const CharType[256] ASCII_LOOKUP @private = {
				[0..31] = { .control },
				[9..13] = { .control, .space },
			};
			""");

		C3BitstructDef field = PsiTreeUtil.findChildOfType(myFixture.getFile(), C3BitstructDef.class);
		assertNotNull(field);
		assertEquals("control", field.getName());
		java.util.Collection<?> usages = myFixture.findUsages(field);
		assertEquals("Expected two usages, got: " + usages, 2, usages.size());
	}

	public void testBitstructFieldNavigatesFromRangeInit()
	{
		myFixture.configureByText("main.c3", """
			module ascii;

			bitstruct CharType : char
			{
				bool control : 0;
				bool space : 1;
			}

			const CharType[256] ASCII_LOOKUP @private = {
				[0..31] = { .cont<caret>rol },
				[9..13] = { .control, .space },
			};
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement target = reference.resolve();
		assertTrue("Designated init must navigate to the bitstruct field, got: " + target,
			target instanceof C3BitstructDef);
	}

	public void testStructFieldNavigatesFromInit()
	{
		myFixture.configureByText("main.c3", """
			module test;

			struct Point
			{
				int x;
				int y;
			}

			fn void foo()
			{
				Point p = { .<caret>x = 1 };
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement target = reference.resolve();
		assertTrue("Designated init must navigate to the struct field, got: " + target,
			target instanceof C3StructMemberDeclaration);
	}

	private @NotNull List<PsiElement> resolveAccessIdent(@NotNull String code)
	{
		myFixture.configureByText("main.c3", code);
		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		assertTrue(reference instanceof PsiPolyVariantReference);

		List<PsiElement> resolved = new ArrayList<>();
		for (ResolveResult result : ((PsiPolyVariantReference) reference).multiResolve(false))
		{
			PsiElement element = result.getElement();
			if (element != null)
			{
				resolved.add(element);
			}
		}
		return resolved;
	}

	private static <T> @NotNull List<T> instancesOf(@NotNull List<PsiElement> elements, @NotNull Class<T> type)
	{
		List<T> result = new ArrayList<>();
		for (PsiElement element : elements)
		{
			if (type.isInstance(element))
			{
				result.add(type.cast(element));
			}
		}
		return result;
	}

	private static @NotNull String describe(@NotNull List<PsiElement> elements)
	{
		List<String> descriptions = new ArrayList<>();
		for (PsiElement element : elements)
		{
			descriptions.add(element.getClass().getSimpleName() + ": " + element.getText());
		}
		return String.join("\n", descriptions);
	}
}
