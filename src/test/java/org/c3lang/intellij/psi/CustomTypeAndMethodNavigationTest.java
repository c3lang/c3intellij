package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomTypeAndMethodNavigationTest extends BasePlatformTestCase
{
	private static final String STACK_CODE = """
		module stack <Type>;

		struct Stack
		{
			sz capacity;
			sz size;
			Type* elems;
		}

		fn void Stack.push(Stack* this, Type element)
		{
			if (this.capacity == this.size)
			{
				this.capacity *= 2;
				if (this.capacity < 16) this.capacity = 16;
				this.elems = realloc(this.elems, Type.sizeof * this.capacity);
			}
			this.elems[this.size++] = element;
		}

		fn Type Stack.pop(Stack* this)
		{
			assert(this.size > 0);
			return this.elems[--this.size];
		}

		fn bool Stack.empty(Stack* this)
		{
			return !this.size;
		}
		""";

	public void testCustomStructNavigationFromVariable()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test()
			{
				St<caret>ack s;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3TypeName: " + resolved, resolved instanceof C3TypeName);
		assertEquals("Stack", ((C3TypeName) resolved).getName());
	}

	public void testCustomStructNavigationFromParameter()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test(St<caret>ack* s)
			{
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3TypeName: " + resolved, resolved instanceof C3TypeName);
		assertEquals("Stack", ((C3TypeName) resolved).getName());
	}

	public void testCustomStructNavigationFromMethodHeader()
	{
		myFixture.configureByText("main.c3", """
			module test;
			struct MyTr
			{
				int a;
			}
			fn int My<caret>Tr.test()
			{
				return 0;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3TypeName: " + resolved, resolved instanceof C3TypeName);
		assertEquals("MyTr", ((C3TypeName) resolved).getName());
	}

	public void testMethodNavigationFromLocalVarCall()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test()
			{
				Stack stack;
				stack.po<caret>p();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		assertTrue(reference instanceof PsiPolyVariantReference);
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3FuncDef: " + resolved, resolved instanceof C3FuncDef);
		assertEquals("Stack.pop", ((C3FuncDef) resolved).getFqName().getName());
	}

	public void testMethodNavigationFromThisCall()
	{
		myFixture.configureByText("main.c3", """
			module stack;
			struct Stack
			{
				int size;
			}
			fn void Stack.pop(Stack* this)
			{
			}
			fn void Stack.push(Stack* this, int x)
			{
				this.po<caret>p();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3FuncDef: " + resolved, resolved instanceof C3FuncDef);
		assertEquals("Stack.pop", ((C3FuncDef) resolved).getFqName().getName());
	}

	public void testMethodNavigationFromParameterCall()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test(Stack* s)
			{
				s.po<caret>p();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve", resolved);
		assertTrue("Should resolve to C3FuncDef: " + resolved, resolved instanceof C3FuncDef);
		assertEquals("Stack.pop", ((C3FuncDef) resolved).getFqName().getName());
	}

	public void testFindUsagesOnCustomStruct()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test()
			{
				Stack s;
			}
			""");

		// Find the C3TypeName for struct Stack
		PsiElement structElement = myFixture.findElementByText("struct Stack", C3StructDeclaration.class);
		assertNotNull("Struct declaration must be found", structElement);
		C3TypeName typeName = ((C3StructDeclaration) structElement).getTypeName();
		assertNotNull("TypeName must not be null", typeName);

		Collection<UsageInfo> usages = myFixture.findUsages(typeName);
		assertFalse("Usages of struct Stack should not be empty", usages.isEmpty());
		// Verify usages include Stack.push, Stack.pop, Stack.empty, Stack s
		assertTrue("Expected at least 5 usages of Stack, got: " + usages.size(), usages.size() >= 5);
	}

	public void testFindUsagesOnMethod()
	{
		myFixture.configureByText("main.c3", """
			module stack;
			struct Stack
			{
				int size;
			}
			fn void Stack.pop(Stack* this)
			{
			}
			fn void caller1()
			{
				Stack s;
				s.pop();
			}
			fn void caller2(Stack* s)
			{
				s.pop();
			}
			""");

		PsiElement popMethod = myFixture.findElementByText("fn void Stack.pop", C3FuncDef.class);
		assertNotNull("pop method must be found", popMethod);

		Collection<UsageInfo> usages = myFixture.findUsages(popMethod);
		assertEquals("Expected 2 call usages of Stack.pop", 2, usages.size());
	}

	public void testCrossModuleStructAndMethodNavigation()
	{
		myFixture.addFileToProject("stack.c3", """
			module stack;
			struct Stack
			{
				int size;
			}
			fn void Stack.pop(Stack* this)
			{
			}
			""");

		myFixture.configureByText("main.c3", """
			module main;
			import stack;

			fn void run()
			{
				St<caret>ack s;
				s.pop();
			}
			""");

		PsiReference typeRef = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolvedType = typeRef.resolve();
		assertNotNull("Cross-module type reference should resolve", resolvedType);
		assertTrue("Should resolve to C3TypeName: " + resolvedType, resolvedType instanceof C3TypeName);
		assertEquals("Stack", ((C3TypeName) resolvedType).getName());

		// Now test method navigation
		myFixture.configureByText("main.c3", """
			module main;
			import stack;

			fn void run()
			{
				Stack s;
				s.po<caret>p();
			}
			""");

		PsiReference methodRef = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolvedMethod = methodRef.resolve();
		assertNotNull("Cross-module method reference should resolve", resolvedMethod);
		assertTrue("Should resolve to C3CallablePsiElement: " + resolvedMethod, resolvedMethod instanceof C3CallablePsiElement);
		assertEquals("Stack.pop", ((C3CallablePsiElement) resolvedMethod).getFqName().getName());
	}

	public void testCustomStructNavigationWithGenericArgs()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test()
			{
				St<caret>ack(<int>) s;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Reference should resolve to struct Stack", resolved);
		assertTrue("Should resolve to C3TypeName: " + resolved, resolved instanceof C3TypeName);
		assertEquals("Stack", ((C3TypeName) resolved).getName());
	}

	public void testGenericTypeParamNavigation()
	{
		myFixture.configureByText("main.c3", STACK_CODE);

		// Find the Type in 'Type* elems;'
		C3Type elemType = myFixture.findElementByText("Type* elems", C3Type.class);
		assertNotNull("Element type must be found", elemType);
		PsiReference reference = elemType.getBaseType().getReference();
		assertNotNull("Reference must exist", reference);
		PsiElement resolved = reference.resolve();
		assertNotNull("Generic parameter Type should resolve", resolved);
		assertTrue("Should resolve to C3ModuleParam: " + resolved, resolved instanceof C3ModuleParam);
		assertEquals("Type", resolved.getText());
	}

	public void testGenericMethodCallNavigation()
	{
		myFixture.configureByText("main.c3", STACK_CODE + """
			fn void test()
			{
				Stack(<int>) s;
				s.po<caret>p();
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Method call on generic instance should resolve", resolved);
		assertTrue("Should resolve to C3FuncDef: " + resolved, resolved instanceof C3FuncDef);
		assertEquals("Stack.pop", ((C3FuncDef) resolved).getFqName().getName());
	}
}
