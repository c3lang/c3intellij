package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
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
