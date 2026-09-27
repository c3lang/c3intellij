package org.c3lang.intellij.types;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CallCheckTest extends BasePlatformTestCase
{
    public void testDuplicateMethodIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Test
            {
                int f;
            }
            fn void Test.test(&self)
            {
            }
            fn void Test.test(Test* self)
            {
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "already defined for 'Test'");
        assertEquals("Expected one duplicate error, got: " + errors, 1, errors.size());
        assertTrue("Error should point at the previous definition, got: " + errors.get(0).getDescription(),
            errors.get(0).getDescription() != null
                && errors.get(0).getDescription().contains("previous definition"));
    }

    public void testDuplicateFreeFunctionIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void foo()
            {
            }
            fn void foo()
            {
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "already defined");
        assertEquals("Expected one duplicate error, got: " + errors, 1, errors.size());
    }

    public void testDistinctMethodsOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Test
            {
                int f;
            }
            fn void Test.one(Test* self)
            {
            }
            fn void Test.two(Test* self)
            {
            }
            fn void other()
            {
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected duplicate error, got: " + highlights,
            errorsWithText(highlights, "already defined").isEmpty());
    }

    public void testLocalFunctionsAcrossFilesOk()
    {
        // Mirrors math_nolibc acos/asin: `@local` helpers with the same
        // name in different files of one module are file-confined, never
        // duplicates (verified against c3c).
        myFixture.addFileToProject("asin.c3", """
            module test;
            fn double _r(double z) @local
            {
                return z * 2;
            }
            fn double asin(double x)
            {
                return _r(x);
            }
            """);
        myFixture.configureByText("main.c3", """
            module test;
            fn double _r(double z) @local
            {
                return z;
            }
            fn double acos(double x)
            {
                return _r(x);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected duplicate error, got: " + highlights,
            errorsWithText(highlights, "already defined").isEmpty());
    }

    public void testLocalFunctionsSameFileIsError()
    {
        // Same file, same `@local` name: still a conflict (c3c reports
        // a shadowing error).
        myFixture.configureByText("main.c3", """
            module test;
            fn double _r(double z) @local
            {
                return z;
            }
            fn double _r(double w) @local
            {
                return w;
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "already defined");
        assertEquals("Expected one duplicate error, got: " + errors, 1, errors.size());
    }

    public void testUnknownNamedArgIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void test_named(int times, double data)
            {
            }
            fn void test()
            {
                test_named(times: 1, data: 3.0, bogus: 2);
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Unknown parameter 'bogus'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testNamedOverwriteIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void named_default(int times = 1, double data = 3.0)
            {
            }
            fn void test()
            {
                named_default(2, times: 3);
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "already set");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testPositionalAfterNamedIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void named_default(int times = 1, double data = 3.0)
            {
            }
            fn void test()
            {
                named_default(times: 3, 4.0);
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "may not follow named arguments");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testTooManyArgsIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void fixed(int a)
            {
            }
            fn void test()
            {
                fixed(1, 2);
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Too many arguments.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testMissingArgIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void needs_two(int a, int b)
            {
            }
            fn void test()
            {
                needs_two(1);
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Missing argument for parameter 'b'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testVaargForwardWithNamedOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Map { int x; }
            macro Map* Map.init_with_key_values(&self, int allocator, ..., uint capacity = 4, float load_factor = 0.5)
            {
                return self;
            }
            macro Map* Map.tinit(&self, ..., uint capacity = 4, float load_factor = 0.5)
            {
                return self.init_with_key_values(1, $vasplat, capacity: capacity, load_factor: load_factor);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call error, got: " + highlights,
            errorsWithText(highlights, "already set").isEmpty());
    }

    public void testGenericTypeParamArgToVoidStarOk()
    {
        // `Key` is a module generic parameter: unknowable before
        // instantiation, so passing it to `void*` must stay silent
        // (mirrors hashmap.c3 `allocator::free(map.allocator, entry.key)`).
        myFixture.configureByText("main.c3", """
            module test::map <Key, Value>;
            struct Entry
            {
                Key key;
                Entry* next;
            }
            fn void free_it(void* ptr)
            {
            }
            fn void free_entry(Entry* entry)
            {
                free_it(entry.key);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testTypedefContractArgToInterfaceOk()
    {
        // Mirrors dstring.c3: `typedef DString (OutStream) = ...` carries the
        // contract on the typedef, so `&report` converts to `OutStream`.
        myFixture.configureByText("main.c3", """
            module test;
            interface OutStream
            {
            }
            typedef DString (OutStream) = void*;
            fn void take(OutStream out)
            {
            }
            fn void foo()
            {
                DString report;
                take(&report);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testMacroSelfMemberResolvesToOwnerOk()
    {
        // Mirrors Blake3Output.chaining_value: `self.flags` in a macro with
        // typeless `&self` is a member of the macro owner, not of any other
        // struct that happens to have a `flags` field (which used to report
        // `Cannot pass 'uint' for parameter 'flags' of type 'char'`).
        myFixture.configureByText("main.c3", """
            module test;
            struct Blake3Output
            {
                char flags;
            }
            struct Darwin_segment_command_64
            {
                uint flags;
            }
            fn void take_char(char flags)
            {
            }
            macro void Blake3Output.chaining_value(&self, char* cv)
            {
                take_char(self.flags);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testInlineConstdefToBackingParamOk()
    {
        // Mirrors Blake3: `constdef Blake3Flags : inline char` constants
        // convert to the backing type implicitly.
        myFixture.configureByText("main.c3", """
            module test;
            constdef Blake3Flags : inline char
            {
                DERIVE_KEY_CONTEXT = 1 << 5,
                DERIVE_KEY_MATERIAL = 1 << 6,
            }
            fn void take_flags(char explicit_flags)
            {
            }
            fn void take_size(int size)
            {
            }
            fn void foo()
            {
                take_flags(Blake3Flags.DERIVE_KEY_CONTEXT);
                take_size(Blake3Flags.DERIVE_KEY_MATERIAL);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testWhileConditionLocalMemberOk()
    {
        // Mirrors append_xxlizer: `current` declared in the while condition
        // is visible (with its type) in the loop body, so `&current.next`
        // is `Callback**`, not some unrelated `Header**`.
        myFixture.configureByText("main.c3", """
            module test;
            struct Header
            {
                uint flags;
                Header* next;
            }
            struct Callback
            {
                uint priority;
                Callback* next;
            }
            fn Callback* get();
            fn void foo(Callback** ref)
            {
                while (Callback* current = get(), current)
                {
                    ref = &current.next;
                }
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testDifferentComputedArraySizeStillError()
    {
        // `uint[8]` does not convert to `uint[BLOCK_SIZE/uint.sizeof]`
        // (== 16): computed inequality must still report.
        myFixture.configureByText("main.c3", """
            module test;
            const BLOCK_SIZE = 64;
            fn void transform(uint[BLOCK_SIZE/uint.sizeof] input)
            {
            }
            fn void foo()
            {
                uint[8] small;
                transform(small);
            }
            """);
        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Cannot pass 'uint[8]'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testPlainConstdefToBackingParamIsError()
    {
        // Without `inline` the constdef is distinct: c3c demands an explicit
        // cast, and so do we.
        myFixture.configureByText("main.c3", """
            module test;
            constdef PlainFlags : char
            {
                VALUE = 1,
            }
            fn void take_flags(char explicit_flags)
            {
            }
            fn void foo()
            {
                take_flags(PlainFlags.VALUE);
            }
            """);
        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(),
            "Cannot pass 'PlainFlags' for parameter 'explicit_flags' of type 'char'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testComputedArraySizeParamOk()
    {
        // Mirrors RipeMd.transform: `uint[16]` converts to
        // `uint[BLOCK_SIZE/uint.sizeof]` when the sizes evaluate equal.
        myFixture.configureByText("main.c3", """
            module test;
            const BLOCK_SIZE = 64;
            struct RipeMd
            {
                uint[16] buffer;
            }
            fn void transform(uint[BLOCK_SIZE/uint.sizeof] input)
            {
            }
            fn void RipeMd.update(&self)
            {
                self.transform(self.buffer);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testUnionMembersThroughForeachPointerOk()
    {
        // Mirrors macho_runtime dl_reg_callback: `foreach (&dm : ...)` makes
        // `dm` a pointer, union members resolve through it, and a `$Type`
        // macro collection still yields the element type.
        myFixture.configureByText("main.c3", """
            module test;
            struct TypeId
            {
                char kind;
                int* dtable;
            }
            struct DynamicMethod
            {
                void* fn_ptr;
                union
                {
                    DynamicMethod* next;
                    TypeId* type;
                }
            }
            macro DynamicMethod[] find_all(DynamicMethod* start, $Type)
            {
                return {};
            }
            fn void take_dtable(int* dtable)
            {
            }
            fn void foo(DynamicMethod* start)
            {
                foreach (&dm : find_all(start, DynamicMethod))
                {
                    TypeId* type = dm.type;
                    dm.next = type.dtable;
                    take_dtable(type.dtable);
                }
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testOverloadByArityPicksMatchingCandidateOk()
    {
        // A 2-argument call must resolve to the 2-parameter function, not to
        // the 1-parameter macro with the same name.
        myFixture.configureByText("main.c3", """
            module test;
            macro int alloc($Type)
            {
                return 0;
            }
            fn int alloc(usz size, int access)
            {
                return 0;
            }
            fn void foo()
            {
                int x = alloc(1, 2);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testTypedefWithoutContractToInterfaceIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            interface OutStream
            {
            }
            typedef Plain = void*;
            fn void take(OutStream out)
            {
            }
            fn void foo()
            {
                Plain p;
                take(&p);
            }
            """);
        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(),
            "Cannot pass 'Plain*' for parameter 'out' of type 'OutStream'");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testArgTypeMismatchIsError()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void takes_uint(uint x)
            {
            }
            fn void test()
            {
                takes_uint("s");
            }
            """);

        List<HighlightInfo> errors = errorsWithText(myFixture.doHighlighting(), "Cannot pass 'String' for parameter 'x' of type 'uint'.");
        assertEquals("Expected one error, got: " + errors, 1, errors.size());
    }

    public void testCorrectCallsOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void test_named(int times, double data)
            {
            }
            fn int test_with_default(int foo = 1)
            {
                return foo;
            }
            fn void test()
            {
                test_named(times: 1, data: 3.0);
                test_named(15, data: 3.14);
                test_with_default();
                test_with_default(100);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testMethodCallReceiverSkipped()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Point
            {
                int x;
                int y;
            }
            fn void Point.add(Point* p, int x)
            {
                p.x += x;
            }
            fn void example()
            {
                Point p = { 1, 2 };
                p.add(10);
                Point.add(&p, 10);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testVaargCallOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void va_singletyped(int... args)
            {
            }
            fn void test()
            {
                va_singletyped(1, 2, 3);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testUndeclaredParamTypeSkipsTypeCheck()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void generic_like(Nope x)
            {
            }
            fn void test()
            {
                generic_like(1);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testPrintfLikeVaargCallOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            struct Test
            {
                int b;
                int f;
            }
            fn usz? printfn(String format, args...);
            fn void print_person(Test* p)
            {
                printfn("%s is %d years old.", p.b, p.f);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testExplicitAnyVaargCallOk()
    {
        myFixture.configureByText("main.c3", """
            module test;
            fn void va_explicit(any... args)
            {
            }
            fn void test()
            {
                va_explicit(1, "s");
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testSameModuleShadowsSiblingOverloadOk()
    {
        // Mirrors base32/base64 `decode_len`: both overloads are visible
        // through same-parent imports, but the caller's own module wins
        // (verified against c3c, which rejects the imported shape here).
        myFixture.addFileToProject("a.c3", """
            module pkg::a;
            fn usz decode_len(usz n, char padding)
            {
                return n;
            }
            """);
        myFixture.addFileToProject("b.c3", """
            module pkg::b;
            fn usz? decode_len(usz n, char padding)
            {
                return n;
            }
            """);
        myFixture.configureByText("main.c3", """
            module pkg::a;
            fn void foo()
            {
                usz dn = decode_len(1, 0);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
        assertTrue("Unexpected assign errors, got: " + highlights,
            errorsWithText(highlights, "Cannot assign 'usz?'").isEmpty());
    }

    public void testEllipsisDefaultArgsOk()
    {
        // Mirrors sort::binarysearch: `cmp = ...` / `context = ...` are
        // optional (unset by default), so fewer arguments are fine.
        myFixture.configureByText("main.c3", """
            module test;
            macro usz binarysearch(list, element, cmp = ..., context = ...)
            {
                return 0;
            }
            fn void foo()
            {
                usz a = binarysearch(1, 2, 3);
                usz b = binarysearch(1, 2);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testMacroEnumConstArgOk()
    {
        // Mirrors log::info -> call_log(INFO, ...): a bare enum constant
        // passed through a macro `=>` body must infer as the enum type.
        myFixture.configureByText("main.c3", """
            module test;
            typedef LogCategory = inline char;
            const LogCategory CATEGORY_APPLICATION = (LogCategory)0;
            tlocal LogCategory default_category = CATEGORY_APPLICATION;
            enum LogPriority : int
            {
                VERBOSE,
                DEBUG,
                INFO,
                WARN,
                ERROR,
                CRITICAL,
            }
            macro void call_log(LogPriority prio, LogCategory category, String fmt, args...)
            {
            }
            macro void info(String fmt, ..., LogCategory category = default_category) => call_log(INFO, category, fmt, $vasplat);
            fn void foo()
            {
                info("hello");
                call_log(INFO, CATEGORY_APPLICATION, "hello");
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testMacroEnumConstArgCrossModuleOk()
    {
        // The user's real shape: log macros live in their own module, the
        // caller imports it, and an unrelated module declares a const with
        // a colliding name (`cpu.c3`-style). The enum constant must still
        // resolve to LogPriority, not to the foreign const.
        myFixture.addFileToProject("log.c3", """
            module log;
            typedef LogCategory = inline char;
            const LogCategory CATEGORY_APPLICATION = (LogCategory)0;
            const LogCategory CATEGORY_ERROR = (LogCategory)11;
            tlocal LogCategory default_category = CATEGORY_APPLICATION;
            enum LogPriority : int
            {
                VERBOSE,
                DEBUG,
                INFO,
                WARN,
                ERROR,
                CRITICAL,
            }
            macro void call_log(LogPriority prio, LogCategory category, String fmt, args...)
            {
            }
            macro void info(String fmt, ..., LogCategory category = default_category) => call_log(INFO, category, fmt, $vasplat);
            macro void error(String fmt, ..., LogCategory category = default_category) => call_log(ERROR, category, fmt, $vasplat);
            """);
        myFixture.addFileToProject("cpu.c3", """
            module cpu;
            const char INFO = 1;
            """);
        myFixture.configureByText("main.c3", """
            module test;
            import log;
            import cpu;
            fn void foo()
            {
                log::info("hello");
                log::error("boom");
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testArrayPointerArgOk()
    {
        // Mirrors cpu.c3 native_cpu: `&nm` (int[2]*) passes for `CInt*`.
        myFixture.addFileToProject("libc.c3", """
            module libc;
            alias CInt = int;
            alias CUInt = uint;
            extern fn CInt sysctl(CInt *name, CUInt namelen, void *oldp, usz *oldlenp, void *newp, usz newlen);
            """);
        myFixture.configureByText("main.c3", """
            module test;
            import libc;
            fn uint native_cpu()
            {
                int[2] nm;
                usz len = 4;
                uint count;
                nm = { 1, 2 };
                libc::sysctl(&nm, 2, &count, &len, null, 0);
                if (count < 1) count = 1;
                return count;
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testInlineTypedefThroughAliasArgOk()
    {
        // Mirrors std::core::sanitizer::tsan: `typedef MutexFlags = inline
        // CUInt` passed where the underlying builtin is expected. Mixed
        // alias/inline chains canonicalize fully (verified against c3c).
        myFixture.configureByText("main.c3", """
            module test;
            alias CUInt = uint;
            typedef MutexFlags = inline CUInt;
            extern fn void __tsan_mutex_post_signal(void* addr, CUInt flags);
            macro void mutex_post_signal(void* addr, MutexFlags flags)
            {
                __tsan_mutex_post_signal(addr, flags);
            }
            fn void foo()
            {
                mutex_post_signal(null, 1);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertTrue("Unexpected call errors, got: " + highlights, callErrors(highlights).isEmpty());
    }

    public void testInlineTypedefTargetRejectsUnderlyingArg()
    {
        // The reverse direction needs an explicit cast: an `inline`
        // typedef target stays opaque (verified against c3c, which says
        // `Implicitly casting 'uint' to 'Flags' is not permitted...`).
        myFixture.configureByText("main.c3", """
            module test;
            alias CUInt = uint;
            typedef MutexFlags = inline CUInt;
            fn void takes_flags(MutexFlags flags)
            {
            }
            fn void foo(CUInt u)
            {
                takes_flags(u);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertEquals("Expected one error, got: " + highlights,
            1, errorsWithText(highlights, "Cannot pass").size());
    }

    public void testDistinctTypedefArgStillRejected()
    {
        // A distinct typedef is a conversion barrier: the underlying
        // builtin does not pass where it is expected (verified vs c3c).
        myFixture.configureByText("main.c3", """
            module test;
            typedef Distinct = uint;
            fn void takes_distinct(Distinct x)
            {
            }
            fn void foo(uint u)
            {
                takes_distinct(u);
            }
            """);

        List<HighlightInfo> highlights = myFixture.doHighlighting();
        assertEquals("Expected one error, got: " + highlights,
            1, errorsWithText(highlights, "Cannot pass").size());
    }

    private static @NotNull List<HighlightInfo> callErrors(@NotNull List<HighlightInfo> highlights)
    {
        List<HighlightInfo> result = new ArrayList<>();
        for (HighlightInfo info : highlights)
        {
            if (info.getSeverity() != HighlightSeverity.ERROR
                || info.getDescription() == null
                || info.getDescription().isBlank()) continue;
            String description = info.getDescription();
            if (description.contains("Unknown parameter")
                || description.contains("already set")
                || description.contains("may not follow named arguments")
                || description.contains("Too many arguments")
                || description.contains("Missing argument")
                || description.contains("Cannot pass"))
            {
                result.add(info);
            }
        }
        return result;
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
}
