package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@code foreach} iteration variables resolve to their declaration, carry
 * the declared (or iterated element) type, and support Find Usages; struct
 * contracts resolve through the module's imports (e.g. {@code Printable}
 * from {@code std::io} via {@code import std::io}); {@code if (catch)} /
 * {@code if (try)} bindings resolve to their unwrap and support Find Usages.
 */
public class LocalUsagesTest extends BasePlatformTestCase
{
    public void testDump()
    {
        myFixture.addFileToProject("fmt.c3", """
            module std::io;
            interface Printable
            {
                fn void print();
            }
            """);
        myFixture.configureByText("main.c3", """
            module test;
            import std::io;
            struct Entry
            {
                Entry* next;
                int hash;
            }
            fn void transfer(Entry*[] src)
            {
                foreach (uint j, Entry* e : src)
                {
                    if (!e) continue;
                    Entry* next = e.next;
                    e.next = next;
                }
            }
            struct Widget (Printable)
            {
                int x;
            }
            """);
        List<C3PathIdent> uses = new ArrayList<>();
        for (C3PathIdent ident : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3PathIdent.class))
        {
            if (ident.getText().equals("e")) uses.add(ident);
        }
        assertEquals(3, uses.size());
        for (C3PathIdent use : uses)
        {
            PsiReference ref = use.getReference();
            assertNotNull(ref);
            PsiElement target = ref.resolve();
            assertTrue("expected foreach-var, got: " + target, target instanceof C3ForeachVar);
            assertEquals("e", ((C3ForeachVar) target).getName());
            assertEquals("FullyQualifiedName(module=null, name=Entry*)", String.valueOf(use.findTypeName()));
        }
        PsiElement target = uses.get(0).getReference().resolve();
        assertNotNull(target);
        assertEquals(3, ReferencesSearch.search(target).findAll().size());

        List<String> errors = new ArrayList<>();
        myFixture.doHighlighting().forEach(info -> {
            if (info.getDescription() != null && (info.getDescription().contains("Cannot assign")
                || info.getDescription().contains("Unresolved interface")
                || info.getDescription().contains("is not an interface")))
            {
                errors.add(info.getDescription());
            }
        });
        assertTrue("unexpected errors: " + errors, errors.isEmpty());
    }

    public void testUnimportedContractStillUnresolved()
    {
        myFixture.addFileToProject("fmt.c3", """
            module std::io;
            interface Printable
            {
                fn void print();
            }
            """);
        myFixture.configureByText("main.c3", """
            module test;
            struct Widget (Printable)
            {
                int x;
            }
            """);
        Collection<String> errors = new ArrayList<>();
        myFixture.doHighlighting().forEach(info -> {
            if (info.getDescription() != null && info.getDescription().contains("Unresolved interface"))
            {
                errors.add(info.getDescription());
            }
        });
        assertEquals(List.of("Unresolved interface 'Printable'."), new ArrayList<>(errors));
    }

    public void testContractNavigatesToImportedInterface()
    {
        myFixture.addFileToProject("fmt.c3", """
            module std::io;
            interface Printable
            {
                fn void print();
            }
            """);
        myFixture.configureByText("main.c3", """
            module test;
            import std::io;
            struct Widget (Printable)
            {
                int x;
            }
            """);
        C3TypeName contract = null;
        for (C3TypeName typeName : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3TypeName.class))
        {
            if (typeName.getText().equals("Printable") && typeName.getParent() instanceof C3InterfaceImpl)
            {
                contract = typeName;
            }
        }
        assertNotNull(contract);
        PsiElement target = contract.getReference().resolve();
        assertTrue("expected interface, got: " + target, target instanceof C3InterfaceDefinition);
        assertEquals("std::io", ModuleName.from((C3PsiElement) target).getValue());
    }

    public void testCatchBindingResolvesAndFindsUsages()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn usz? read_it();
            fn void take_fault(fault e)
            {
            }
            fn void foo()
            {
                usz? len = read_it();
                if (catch err = len)
                {
                    take_fault(err);
                }
            }
            """);
        C3PathIdent use = null;
        for (C3PathIdent ident : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3PathIdent.class))
        {
            if (ident.getText().equals("err")) use = ident;
        }
        assertNotNull(use);
        PsiElement target = use.getReference().resolve();
        assertTrue("expected catch unwrap, got: " + target, target instanceof C3CatchUnwrap);
        assertEquals("err", ((C3CatchUnwrap) target).getName());
        assertEquals("FullyQualifiedName(module=null, name=fault)", String.valueOf(use.findTypeName()));
        assertEquals(1, ReferencesSearch.search(target).findAll().size());
    }

    public void testEnumConstantResolvesAndFindsUsages()
    {
        // Mirrors log::INFO: a bare enum constant in an argument resolves
        // to the enum constant (not to an unrelated same-named const), so
        // navigation and Find Usages land on the declaration.
        myFixture.configureByText("main.c3", """
            module test;
            enum LogPriority : int
            {
                VERBOSE,
                INFO,
            }
            const char INFO = 1;
            macro void call_log(LogPriority prio)
            {
            }
            fn void foo()
            {
                call_log(INFO);
            }
            """);
        C3PathConst use = null;
        for (C3PathConst pathConst : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3PathConst.class))
        {
            if (pathConst.getText().equals("INFO")) use = pathConst;
        }
        assertNotNull(use);
        PsiElement target = use.getReference().resolve();
        assertTrue("expected enum constant, got: " + target, target instanceof C3EnumConstant);
        assertEquals(1, ReferencesSearch.search(target).findAll().size());
    }

    public void testTryBindingResolvesWithUnwrappedType()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn usz? read_it();
            fn void take(usz x)
            {
            }
            fn void foo()
            {
                usz? len = read_it();
                if (try t = len)
                {
                    take(t);
                }
            }
            """);
        C3PathIdent use = null;
        for (C3PathIdent ident : PsiTreeUtil.findChildrenOfType(myFixture.getFile(), C3PathIdent.class))
        {
            if (ident.getText().equals("t")) use = ident;
        }
        assertNotNull(use);
        PsiElement target = use.getReference().resolve();
        assertTrue("expected try unwrap, got: " + target, target instanceof C3TryUnwrap);
        assertEquals("FullyQualifiedName(module=null, name=usz)", String.valueOf(use.findTypeName()));
    }
}
