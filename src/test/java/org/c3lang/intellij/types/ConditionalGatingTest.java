package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConditionalGatingTest extends BasePlatformTestCase
{
    public void testSectionBranchesAreNotDuplicates()
    {
        assertNoDuplicates("""
            module libcbranches @if(env::DARWIN || env::FREEBSD);
            macro usz malloc_size(void* ptr) => malloc_usable_size(ptr);
            module libcbranches @if(env::WIN32);
            macro usz malloc_size(void* ptr) => _msize(ptr);
            """);
    }

    public void testSectionBranchesFnAreNotDuplicates()
    {
        assertNoDuplicates("""
            module libcbranchesfn @if(env::DARWIN);
            fn usz malloc_size(void* ptr)
            {
                return 0;
            }
            module libcbranchesfn @if(env::WIN32);
            fn usz malloc_size(void* ptr)
            {
                return 1;
            }
            """);
    }

    public void testSameSectionConditionStillDuplicate()
    {
        List<HighlightInfo> errors = errorsWithText(check("""
            module libcsame @if(env::WIN32);
            macro usz malloc_size(void* ptr) => _msize(ptr);
            module libcsame @if(env::WIN32);
            macro usz malloc_size(void* ptr) => _msize2(ptr);
            """), "already defined");
        assertEquals("Expected one duplicate error, got: " + errors, 1, errors.size());
    }

    public void testOwnAttributeGatingIsNotDuplicate()
    {
        assertNoDuplicates("""
            module testown;
            fn void foo() @if(env::WIN32)
            {
            }
            fn void foo() @if(env::DARWIN)
            {
            }
            """);
    }

    public void testGatedAgainstUngatedIsNotDuplicate()
    {
        assertNoDuplicates("""
            module testmixed;
            fn void foo()
            {
            }
            module testmixed @if(env::WIN32);
            fn void foo()
            {
            }
            """);
    }

    private @NotNull List<HighlightInfo> check(@NotNull String code)
    {
        myFixture.configureByText("main.c3", code);
        return myFixture.doHighlighting();
    }

    private void assertNoDuplicates(@NotNull String code)
    {
        List<HighlightInfo> duplicates = errorsWithText(check(code), "already defined");
        assertTrue("Unexpected duplicate error, got: " + duplicates, duplicates.isEmpty());
    }

    private static @NotNull List<HighlightInfo> errorsWithText(@NotNull List<HighlightInfo> highlights, @NotNull String textPart)
    {
        List<HighlightInfo> result = new ArrayList<>();
        for (HighlightInfo info : highlights)
        {
            if (info.getDescription() != null && info.getDescription().contains(textPart))
            {
                result.add(info);
            }
        }
        return result;
    }
}
