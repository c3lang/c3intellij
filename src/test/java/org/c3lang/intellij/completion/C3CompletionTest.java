package org.c3lang.intellij.completion;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class C3CompletionTest extends BasePlatformTestCase
{
	public void testCompletionAfterScope()
	{
		myFixture.configureByText("io.c3", """
			module std::io;

			fn void printn(char[] s) {}
			fn void printf(char[] fmt) {}
			""");

		myFixture.configureByText("main.c3", """
			module main;
			import std::io;

			fn void main()
			{
				io::prin<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest printn", lookupStrings.contains("printn"));
	}

	public void testMethodAndFieldCompletionOnInstance()
	{
		myFixture.configureByText("main.c3", """
			module main;

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
				tr.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest field 'a', got: " + lookupStrings, lookupStrings.contains("a"));
		assertTrue("Should suggest method 'test', got: " + lookupStrings, lookupStrings.contains("test"));
	}

	public void testReturnCompletionInFunction()
	{
		myFixture.configureByText("main.c3", """
			module main;

			struct MyTr
			{
				int a;
			}

			fn int MyTr.test()
			{
				ret<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest 'return', got: " + lookupStrings, lookupStrings.contains("return"));
		}
		else
		{
			assertTrue("Should auto-insert 'return'", myFixture.getEditor().getDocument().getText().contains("return"));
		}
	}

	public void testParameterAndVariableCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			struct EnumMap
			{
				int values;
			}

			fn void EnumMap.init(EnumMap* this, int init_value)
			{
				ini<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest parameter 'init_value', got: " + lookupStrings, lookupStrings.contains("init_value"));
		}
		else
		{
			assertTrue("Should auto-insert 'init_value'", myFixture.getEditor().getDocument().getText().contains("init_value"));
		}
	}

	public void testThisCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			struct EnumMap
			{
				int values;
			}

			fn void EnumMap.init(EnumMap* this, int init_value)
			{
				th<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest 'this', got: " + lookupStrings, lookupStrings.contains("this"));
		}
		else
		{
			assertTrue("Should auto-insert 'this'", myFixture.getEditor().getDocument().getText().contains("this"));
		}
	}

	public void testForeachVariableCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			struct EnumMap
			{
				int[] values;
			}

			fn void EnumMap.init(EnumMap* this, int init_value)
			{
				foreach(&a : this.values)
				{
					<caret>
				}
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest foreach variable 'a', got: " + lookupStrings, lookupStrings.contains("a"));
	}

	public void testBuiltinAttributeCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			@nod<caret>
			fn void foo() {}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest '@nodiscard', got: " + lookupStrings, lookupStrings.contains("@nodiscard"));
		}
		else
		{
			assertTrue("Should auto-insert '@nodiscard'", myFixture.getEditor().getDocument().getText().contains("@nodiscard"));
		}
	}

	public void testBuiltinConstantsCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			fn void foo()
			{
				$$F<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest '$$FILE', got: " + lookupStrings, lookupStrings.contains("$$FILE"));
		assertTrue("Should suggest '$$FUNC', got: " + lookupStrings, lookupStrings.contains("$$FUNC"));
		assertTrue("Should suggest '$$FUNCTION', got: " + lookupStrings, lookupStrings.contains("$$FUNCTION"));
	}

	public void testCompileTimeDirectivesCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			fn void foo()
			{
				$def<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest '$defined', got: " + lookupStrings, lookupStrings.contains("$defined"));
		}
		else
		{
			assertTrue("Should auto-insert '$defined'", myFixture.getEditor().getDocument().getText().contains("$defined"));
		}
	}

	public void testPrimitiveTypesCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			fn in<caret>
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'int', got: " + lookupStrings, lookupStrings.contains("int"));
		assertTrue("Should suggest 'int128', got: " + lookupStrings, lookupStrings.contains("int128"));
	}

	public void testExpressionLiteralsCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			fn void foo()
			{
				var x = tr<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		if (lookupStrings != null)
		{
			assertTrue("Should suggest 'true', got: " + lookupStrings, lookupStrings.contains("true"));
		}
		else
		{
			assertTrue("Should auto-insert 'true'", myFixture.getEditor().getDocument().getText().contains("true"));
		}
	}

	public void testStatementKeywordsCompletion()
	{
		myFixture.configureByText("main.c3", """
			module main;

			fn void foo()
			{
				fore<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'foreach', got: " + lookupStrings, lookupStrings.contains("foreach"));
		assertTrue("Should suggest 'foreach_r', got: " + lookupStrings, lookupStrings.contains("foreach_r"));
	}

	public void testStdCompletionDoesNotDeleteCode()
	{
		myFixture.configureByText("enummap.c3", """
			module std::collections::enummap;

			struct EnumMap
			{
				int x;
			}
			""");

		myFixture.configureByText("main.c3", """
			module testproject;
			import std::io;

			struct MyTr {
				int a;
			}

			fn int main(String[] args)
			{
				io::printn("Hello, World!");

				std::<caret>

				MyTr tr;

				tr.test();

				int a = 0;

				return 0;
			}

			fn void f()  {

			}
			""");

		myFixture.completeBasic();
		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().contains("EnumMap"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		String docText = myFixture.getEditor().getDocument().getText();
		assertTrue("Document must contain module testproject", docText.contains("module testproject;"));
		assertTrue("Document must contain fn int main", docText.contains("fn int main(String[] args)"));
		assertTrue("Document must contain MyTr tr;", docText.contains("MyTr tr;"));
		assertTrue("Document must contain std::collections::enummap::EnumMap, got:\n" + docText, docText.contains("std::collections::enummap::EnumMap"));
	}

	public void testStdModuleCompletionAfterScope()
	{
		myFixture.configureByText("enummap.c3", """
			module std::collections::enummap;

			struct EnumMap
			{
				int x;
			}
			""");

		myFixture.configureByText("main.c3", """
			module testproject;

			fn void main()
			{
				std::<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'collections' submodule, got: " + lookupStrings, lookupStrings.contains("collections"));

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("collections"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		String docText = myFixture.getEditor().getDocument().getText();
		assertTrue("Should insert collections::, got:\n" + docText, docText.contains("std::collections::"));
	}

	public void testUnqualifiedTypeCompletionAutoImport()
	{
		myFixture.configureByText("enummap.c3", """
			module std::collections::enummap;

			struct EnumMap
			{
				int x;
			}
			""");

		myFixture.configureByText("main.c3", """
			module testproject;

			fn void main()
			{
				Enum<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest EnumMap, got: " + lookupStrings, lookupStrings.contains("EnumMap"));

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("EnumMap"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		String docText = myFixture.getEditor().getDocument().getText();
		assertTrue("Should insert EnumMap at caret, got:\n" + docText, docText.contains("EnumMap"));
		assertTrue("Should add import for std::collections::enummap, got:\n" + docText, docText.contains("std::collections::enummap"));
	}

	public void testScopedTypeCompletionOnImportedModule()
	{
		myFixture.configureByText("io.c3", """
			module std::io;

			struct File
			{
				int handle;
			}
			""");

		myFixture.configureByText("main.c3", """
			module testproject;
			import std::io;

			fn void main()
			{
				io::Fi<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest File, got: " + lookupStrings, lookupStrings.contains("File"));

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("File"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		String docText = myFixture.getEditor().getDocument().getText();
		assertTrue("Should insert File after io::, got:\n" + docText, docText.contains("io::File"));
	}

	public void testChainedSubmoduleAndTypeCompletion()
	{
		myFixture.configureByText("hashmap.c3", """
			module std::collections::map;

			struct HashMap
			{
				int size;
			}
			""");

		myFixture.configureByText("main.c3", """
			module testproject;

			fn void main()
			{
				std::<caret>
			}
			""");

		// 1. At std::<caret>, select "collections"
		myFixture.completeBasic();
		List<String> items1 = myFixture.getLookupElementStrings();
		assertNotNull(items1);
		assertTrue("Should suggest collections, got: " + items1, items1.contains("collections"));
		assertEquals("Submodule 'collections' must be suggested exactly once, got: " + items1,
			1, items1.stream().filter(s -> s.equals("collections")).count());

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("collections"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		assertEquals("Should be std::collections::", "module testproject;\n\nfn void main()\n{\n\tstd::collections::\n}\n", myFixture.getEditor().getDocument().getText());

		// 2. At std::collections::<caret>, trigger completion and select "map"
		myFixture.completeBasic();
		List<String> items2 = myFixture.getLookupElementStrings();
		assertNotNull(items2);
		assertTrue("Should suggest map, got: " + items2, items2.contains("map"));
		assertEquals("Submodule 'map' must be suggested exactly once, got: " + items2,
			1, items2.stream().filter(s -> s.equals("map")).count());

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("map"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		assertEquals("Should be std::collections::map::", "module testproject;\n\nfn void main()\n{\n\tstd::collections::map::\n}\n", myFixture.getEditor().getDocument().getText());

		// 3. At std::collections::map::<caret>, trigger completion and select "HashMap"
		myFixture.completeBasic();
		List<String> items3 = myFixture.getLookupElementStrings();
		assertNotNull(items3);
		assertTrue("Should suggest HashMap, got: " + items3, items3.contains("HashMap"));
		assertEquals("Type 'HashMap' must be suggested exactly once, got: " + items3,
			1, items3.stream().filter(s -> s.equals("HashMap")).count());

		for (var item : myFixture.getLookupElements())
		{
			if (item.getLookupString().equals("HashMap"))
			{
				myFixture.getLookup().setCurrentItem(item);
				myFixture.finishLookup('\n');
				break;
			}
		}
		String finalText = myFixture.getEditor().getDocument().getText();
		assertTrue("Should contain std::collections::map::HashMap, got:\n" + finalText, finalText.contains("std::collections::map::HashMap"));
	}

	public void testInterfaceMethodCompletionOnInterfaceReceiver()
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

			fn String Baz.myname(Baz* self) @dynamic
			{
				return "Baz";
			}

			fn void caller(MyName* named)
			{
				named.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest interface method 'myname', got: " + lookupStrings, lookupStrings.contains("myname"));
	}

	public void testStructMethodCompletionPrefersImpl()
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

			fn String Baz.myname(Baz* self) @dynamic
			{
				return "Baz";
			}

			fn void caller(Baz baz)
			{
				baz.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'myname', got: " + lookupStrings, lookupStrings.contains("myname"));
		assertEquals("Should suggest 'myname' exactly once, got: " + lookupStrings,
			1, lookupStrings.stream().filter(s -> s.equals("myname")).count());
		assertTrue("Should suggest field 'x', got: " + lookupStrings, lookupStrings.contains("x"));
	}

	public void testIntVectorMethodCompletion()
	{
		myFixture.configureByText("main.c3", """
			module test;

			fn void main()
			{
				int[<2>] ivec = { 23, 11 };
				ivec.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'sum', got: " + lookupStrings, lookupStrings.contains("sum"));
		assertTrue("Should suggest 'max', got: " + lookupStrings, lookupStrings.contains("max"));
		assertTrue("Should suggest 'dot', got: " + lookupStrings, lookupStrings.contains("dot"));
		assertTrue("Should suggest 'comp_lt', got: " + lookupStrings, lookupStrings.contains("comp_lt"));
		assertTrue("Should not suggest 'length' for integer vectors, got: " + lookupStrings,
			!lookupStrings.contains("length"));
		assertTrue("Should not suggest 'normalize' for integer vectors, got: " + lookupStrings,
			!lookupStrings.contains("normalize"));
	}

	public void testFloatVectorMethodCompletion()
	{
		myFixture.configureByText("main.c3", """
			module test;

			fn void main()
			{
				double[<3>] dvec = { 1.0, 2.0, 3.0 };
				dvec.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'sum', got: " + lookupStrings, lookupStrings.contains("sum"));
		assertTrue("Should suggest 'length' for float vectors, got: " + lookupStrings,
			lookupStrings.contains("length"));
		assertTrue("Should suggest 'normalize' for float vectors, got: " + lookupStrings,
			lookupStrings.contains("normalize"));
		assertTrue("Should suggest 'lerp', got: " + lookupStrings, lookupStrings.contains("lerp"));
	}

	public void testArrayLenCompletion()
	{
		myFixture.configureByText("main.c3", """
			module test;

			fn void main()
			{
				int[4] arr = { 1, 2, 3, 4 };
				arr.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'len', got: " + lookupStrings, lookupStrings.contains("len"));
		assertTrue("Should not suggest 'ptr' for arrays, got: " + lookupStrings,
			!lookupStrings.contains("ptr"));
	}

	public void testSliceLenPtrCompletion()
	{
		myFixture.configureByText("main.c3", """
			module test;

			fn void test()
			{
				int[4] arr = { 1, 2, 3, 4 };
				int[] slice = &arr;
				slice.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest 'len', got: " + lookupStrings, lookupStrings.contains("len"));
		assertTrue("Should suggest 'ptr' for slices, got: " + lookupStrings, lookupStrings.contains("ptr"));
	}

	public void testTypingStdScopeShowsModules()
	{
		myFixture.configureByText("collections.c3", """
			module std::collections;
			""");

		myFixture.configureByText("main.c3", """
			module testproject;

			fn void main()
			{
				<caret>
			}
			""");

		myFixture.type("std::");
		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Typing 'std::' should show a lookup, got null", lookupStrings);
		assertTrue("Should suggest 'collections', got: " + lookupStrings, lookupStrings.contains("collections"));
	}
	public void testNestedImportedFieldCompletion()
	{
		myFixture.configureByText("other.c3", """
			module other;

			struct File
			{
				int handle;
			}
			""");

		myFixture.configureByText("main.c3", """
			module test;
			import other;

			struct Holder
			{
				other::File f;
			}

			fn void caller(Holder h)
			{
				h.f.<caret>
			}
			""");

		myFixture.completeBasic();
		List<String> lookupStrings = myFixture.getLookupElementStrings();
		assertNotNull("Lookup strings should not be null", lookupStrings);
		assertTrue("Should suggest imported field 'handle', got: " + lookupStrings, lookupStrings.contains("handle"));
	}
}
