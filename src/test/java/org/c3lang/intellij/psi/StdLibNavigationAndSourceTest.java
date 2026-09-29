package org.c3lang.intellij.psi;

import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.c3lang.intellij.C3CompilerDetector;
import org.c3lang.intellij.C3StdLibRootsProvider;
import org.c3lang.intellij.C3SyntheticLibrary;
import org.c3lang.intellij.C3Util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StdLibNavigationAndSourceTest extends BasePlatformTestCase
{
	private static final String ENUMMAP_C3 = """
		module std::collections::enummap <Enum, ValueType>;

		struct EnumMap
		{
			int dummy;
		}

		fn void EnumMap.init(&self, ValueType init_value)
		{
		}

		fn int EnumMap.len(&self)
		{
			return 0;
		}
		""";

	public void testC3SyntheticLibraryPresentation() throws Exception
	{
		VirtualFile dummyDir = myFixture.getTempDirFixture().findOrCreateDir("fake_lib");
		C3SyntheticLibrary library = new C3SyntheticLibrary("C3 Standard Library", List.of(dummyDir));

		assertEquals("C3 Standard Library", library.getPresentableText());
		assertNotNull(library.getLocationString());
		assertNotNull(library.getIcon(false));
		assertEquals(1, library.getSourceRoots().size());
		assertTrue(library.getSourceRoots().contains(dummyDir));
		assertTrue(library.getBinaryRoots().isEmpty());
		assertTrue(library.getExcludedRoots().isEmpty());

		C3SyntheticLibrary same = new C3SyntheticLibrary("C3 Standard Library", List.of(dummyDir));
		assertEquals(library, same);
		assertEquals(library.hashCode(), same.hashCode());
	}

	public void testCompilerDetectorStdlibPathNormalization()
	{
		String parsed = C3CompilerDetector.parseStdlibPath("Stdlib            : C:/Program Files/c3/lib/\\");
		assertEquals("C:/Program Files/c3/lib", parsed);

		String executable = C3CompilerDetector.findCompilerExecutable("c3c");
		assertNotNull(executable);
		assertFalse(executable.isBlank());
	}

	public void testStdLibRootsProviderNormalization()
	{
		assertEquals("C:/Program Files/c3/lib", C3StdLibRootsProvider.normalizePath("C:\\Program Files\\c3\\lib\\\\"));
		assertEquals("C:/Program Files/c3/lib", C3StdLibRootsProvider.normalizePath("C:/Program Files/c3/lib/"));
		assertNull(C3StdLibRootsProvider.normalizePath("   "));
		assertNull(C3StdLibRootsProvider.normalizePath(null));
	}

	public void testImportPathNavigationToStdLibModule()
	{
		myFixture.addFileToProject("std/collections/enummap.c3", ENUMMAP_C3);

		myFixture.configureByText("main.c3", """
			module main;
			import std::collections::en<caret>ummap;

			fn void run() {}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Import path should resolve to module", resolved);
		assertTrue("Should resolve to C3Module: " + resolved, resolved instanceof C3Module);
		assertEquals("std::collections::enummap", ((C3Module) resolved).getModuleName().getValue());
	}

	public void testTypeNavigationToStdLibStruct()
	{
		myFixture.addFileToProject("std/collections/enummap.c3", ENUMMAP_C3);

		myFixture.configureByText("main.c3", """
			module main;
			import std::collections::enummap;

			fn void run()
			{
				Enum<caret>Map map;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Type reference should resolve to struct in stdlib", resolved);
		assertTrue("Should resolve to C3TypeName: " + resolved, resolved instanceof C3TypeName);
		assertEquals("EnumMap", ((C3TypeName) resolved).getName());
	}

	public void testMethodNavigationToStdLibMethod()
	{
		myFixture.addFileToProject("std/collections/enummap.c3", ENUMMAP_C3);

		myFixture.configureByText("main.c3", """
			module main;
			import std::collections::enummap;

			fn void run()
			{
				EnumMap map;
				map.in<caret>it(42);
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Method call should resolve to method in stdlib", resolved);
		assertTrue("Should resolve to C3FuncDef: " + resolved, resolved instanceof C3FuncDef);
		assertEquals("EnumMap.init", ((C3FuncDef) resolved).getFqName().getName());
	}

	public void testFullyQualifiedPathNavigation()
	{
		myFixture.addFileToProject("std/collections/enummap.c3", ENUMMAP_C3);

		myFixture.configureByText("main.c3", """
			module main;

			fn void run()
			{
				std::collections::enum<caret>map::EnumMap map;
			}
			""");

		PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
		PsiElement resolved = reference.resolve();
		assertNotNull("Fully qualified path should resolve to module", resolved);
		assertTrue("Should resolve to C3Module: " + resolved, resolved instanceof C3Module);
		assertEquals("std::collections::enummap", ((C3Module) resolved).getModuleName().getValue());
	}

	public void testFindC3ModulesStartingWithCompletesStdLib()
	{
		myFixture.addFileToProject("std/collections/enummap.c3", ENUMMAP_C3);

		Set<String> modules = C3Util.INSTANCE.findC3ModulesStartingWith(getProject(), "std::collections");
		assertTrue("Should find std::collections::enummap, found: " + modules,
			modules.contains("std::collections::enummap"));
	}
}
