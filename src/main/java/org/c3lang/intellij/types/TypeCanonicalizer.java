package org.c3lang.intellij.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.index.TypeIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3ArgList;
import org.c3lang.intellij.psi.C3CallArgList;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3ConstdefDeclaration;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3PathConstExpr;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.C3TypeExpr;
import org.c3lang.intellij.psi.C3TypeExpr;
import org.c3lang.intellij.psi.C3TypedefType;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Name resolution for type aliases and typedefs: transparent underlying
 * spellings, alias/typedef chains, {@code $typefrom} evaluation and the
 * stub-index scans behind them. Pure static helpers shared by assignability,
 * casts, layout and inference; anything unresolvable is {@code null}.
 */
public final class TypeCanonicalizer
{
    private TypeCanonicalizer()
    {
    }
    /**
     * Full chain resolution for explicit casts: unlike implicit conversions,
     * a cast may cross any mixture of {@code alias} and (inline or distinct)
     * {@code typedef} links (spec §2.5), e.g.
     * {@code Errno -> inline CInt -> $typefrom(...) -> int}.
     * Bounded and cycle-safe; returns the input when nothing resolves.
     */
    static @NotNull String resolveCastType(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String current = typeName;
        for (int depth = 0; depth < 6; depth++)
        {
            String next = resolveAlias(current, project, contextModule, 0);
            if (next == null) next = resolveTypedef(current, project, contextModule, 0);
            if (next == null) next = resolveInlineTypedef(current, project, contextModule, 0);
            if (next == null || TypeChecker.namesEqual(next, current)) return current;
            current = next;
        }
        return current;
    }
    static @Nullable PsiElement findTypeParent(@NotNull String typeName, @NotNull Project project)
    {
        String clean = TypeChecker.normalize(typeName);
        if (!isUserTypeName(clean)) return null;
        String wanted = TypeChecker.shortName(clean);
        for (C3TypeName candidate : findIndexElements(TypeIndex.KEY, C3TypeName.class, typeName, project,
            typeNameElement -> typeNameElement.getText().strip().equals(wanted)
                && (typeNameElement.getParent() instanceof C3StructDeclaration
                    || typeNameElement.getParent() instanceof C3BitstructDeclaration
                    || typeNameElement.getParent() instanceof C3EnumDeclaration
                    || typeNameElement.getParent() instanceof C3InterfaceDefinition
                    || typeNameElement.getParent() instanceof C3TypedefDecl
                    || typeNameElement.getParent() instanceof C3AliasTypeDecl)))
        {
            return candidate.getParent();
        }
        return null;
    }
    /**
     * Index lookup that tolerates stale entries for files without a stub
     * tree (e.g. indexed as plain text before C3 association): degrades to
     * empty instead of throwing into highlighting.
     */
    static @NotNull Collection<C3PsiElement> safeElements(
            @NotNull com.intellij.psi.stubs.StubIndexKey<String, C3PsiElement> key,
            @NotNull String indexKey,
            @NotNull Project project)
    {
        try
        {
            return StubIndex.getElements(
                key,
                indexKey,
                project,
                C3ProjectService.getInstance(project).getSearchScope(),
                C3PsiElement.class);
        }
        catch (Exception ignored)
        {
            return List.of();
        }
    }

    /**
     * Index scan shared by the name-based lookups (consts, type declarations,
     * bitstructs): buckets whose key equals the qualified name, or — for a
     * short name — ends with {@code ::name}, filtered by type and an extra
     * predicate. Stale entries degrade to skipped elements. Scan order is
     * the index order, so first-match callers keep their behavior.
     */
    static @NotNull <E extends C3PsiElement> List<E> findIndexElements(
            @NotNull com.intellij.psi.stubs.StubIndexKey<String, C3PsiElement> indexKey,
            @NotNull Class<E> elementType,
            @NotNull String name,
            @NotNull Project project,
            @NotNull java.util.function.Predicate<E> filter)
    {
        List<E> result = new ArrayList<>();
        String clean = TypeChecker.normalize(name).strip();
        if (clean.isEmpty()) return result;
        String wanted = TypeChecker.shortName(clean);
        boolean qualified = clean.contains("::");
        for (String key : StubIndex.getInstance().getAllKeys(indexKey, project))
        {
            if (qualified)
            {
                if (!key.equals(clean)) continue;
            }
            else if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
            for (C3PsiElement element : safeElements(indexKey, key, project))
            {
                E typed;
                try
                {
                    if (!elementType.isInstance(element)) continue;
                    typed = elementType.cast(element);
                    if (!filter.test(typed)) continue;
                }
                catch (Exception ignored)
                {
                    continue;
                }
                result.add(typed);
            }
        }
        return result;
    }

    /**
     * Same-module preference shared by the name-based lookups: the last
     * same-module candidate wins, otherwise the first candidate (mirrors
     * the historical per-call-site loops exactly).
     */
    static @Nullable <E extends C3PsiElement> E preferSameModule(
            @NotNull List<E> candidates,
            @Nullable ModuleName contextModule)
    {
        E first = null;
        E sameModule = null;
        for (E candidate : candidates)
        {
            if (first == null) first = candidate;
            if (contextModule == null) continue;
            try
            {
                if (contextModule.equals(ModuleName.from(candidate))) sameModule = candidate;
            }
            catch (Exception ignored)
            {
            }
        }
        return sameModule != null ? sameModule : first;
    }
    /**
     * Compile-time type parameters ({@code $Type}, {@code $Foo}): abstract
     * types provided at macro instantiation. Unknowable without expanding
     * the call, so any conversion involving them is allowed: it is checked
     * by the compiler per instantiation. Member access like
     * {@code $Type.min} stays unknown (and silent) through normal inference.
     */
    public static boolean isComptimeParam(@NotNull String typeName)
    {
        return COMPTIME_PARAM_PATTERN.matcher(TypeChecker.normalize(typeName)).find();
    }

    private static final java.util.regex.Pattern COMPTIME_PARAM_PATTERN =
        java.util.regex.Pattern.compile("\\$[A-Z]");
    /**
     * Whether a written type is a known type: a primitive/keyword, a compound
     * type, or a bare identifier declared as a type somewhere.
     */
    static boolean isDeclaredType(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String clean = TypeChecker.normalize(typeText);
        while (true)
        {
            if (clean.endsWith("*") || clean.endsWith("?") || clean.endsWith("!"))
            {
                clean = clean.substring(0, clean.length() - 1);
                continue;
            }
            TypeChecker.VectorInfo vector = TypeChecker.parseVector(clean);
            if (vector != null)
            {
                clean = vector.element;
                continue;
            }
            TypeChecker.VectorInfo array = TypeChecker.parseArray(clean);
            if (array != null)
            {
                clean = array.element;
                continue;
            }
            break;
        }
        String shortName = TypeChecker.shortName(clean);
        if (TypeChecker.INT_TYPES.containsKey(shortName) || TypeChecker.FLOAT_TYPES.containsKey(shortName)) return true;
        switch (shortName)
        {
            case "void", "bool", "char", "String", "ZString", "any", "typeid", "fault" -> { return true; }
            default -> {}
        }
        if (!shortName.matches("[A-Za-z_][A-Za-z_0-9]*")) return true;
        if (DumbService.isDumb(project)) return true;
        for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
        {
            if (!key.equals(shortName) && !key.endsWith("::" + shortName)) continue;
            for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
            {
                if (!(element instanceof C3TypeName typeName)) continue;
                if (!typeName.getText().strip().equals(shortName)) continue;
                PsiElement parent = typeName.getParent();
                if (parent instanceof C3StructDeclaration
                    || parent instanceof C3EnumDeclaration
                    || parent instanceof C3InterfaceDefinition
                    || parent instanceof C3TypedefDecl
                    || parent instanceof C3BitstructDeclaration
                    || parent instanceof C3AliasTypeDecl)
                {
                    return true;
                }
            }
        }
        return false;
    }
    // ------------------------------------------------------------------
    // Aliases and typedefs
    // ------------------------------------------------------------------

    static final class TargetInfo
    {
        final @NotNull String text;
        final boolean typedefOnly;

        TargetInfo(@NotNull String text, boolean typedefOnly)
        {
            this.text = text;
            this.typedefOnly = typedefOnly;
        }
    }

    private static final class NamedTypeDecl
    {
        final @NotNull C3TypeName nameElement;
        final @NotNull String underlying;
        final boolean isTypedef;
        final boolean inlineTypedef;
        final @Nullable ModuleName module;

        NamedTypeDecl(
                @NotNull C3TypeName nameElement,
                @NotNull String underlying,
                boolean isTypedef,
                boolean inlineTypedef,
                @Nullable ModuleName module)
        {
            this.nameElement = nameElement;
            this.underlying = underlying;
            this.isTypedef = isTypedef;
            this.inlineTypedef = inlineTypedef;
            this.module = module;
        }
    }

    static @Nullable TargetInfo resolveTargetType(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String target)
    {
        String underlying = resolveAlias(target, project, contextModule, 0);
        if (underlying != null) return new TargetInfo(underlying, false);
        // Inline typedef targets stay opaque for values (the reverse needs
        // an explicit cast) but accept fitting literals, like distinct
        // typedefs (both verified against c3c).
        String inlineUnderlying = transparentUnderlying(target, project, contextModule);
        if (inlineUnderlying != null) return new TargetInfo(inlineUnderlying, true);
        String typedefTarget = resolveTypedef(target, project, contextModule, 0);
        if (typedefTarget != null) return new TargetInfo(typedefTarget, true);
        // Optional-wrapped alias (`FloatType?`): resolve the inner type and
        // re-wrap, so `return *(int*)arg` sees `double?`, not a dead end.
        if (TypeChecker.isOptionalName(target))
        {
            String inner = TypeChecker.stripOptional(TypeChecker.normalize(target));
            String suffix = TypeChecker.normalize(target).endsWith("!") ? "!" : "?";
            String innerAlias = resolveAlias(inner, project, contextModule, 0);
            if (innerAlias != null) return new TargetInfo(innerAlias + suffix, false);
            String innerTypedef = resolveTypedef(inner, project, contextModule, 0);
            if (innerTypedef != null) return new TargetInfo(innerTypedef + suffix, true);
        }
        return null;
    }

    static @Nullable String resolveSourceType(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String sourceName)
    {
        String transparent = transparentUnderlying(sourceName, project, contextModule);
        if (transparent != null) return transparent;
        // Same re-wrap for Optional-wrapped sources (`Alias?` -> `double?`).
        if (TypeChecker.isOptionalName(sourceName))
        {
            String inner = TypeChecker.stripOptional(TypeChecker.normalize(sourceName));
            String suffix = TypeChecker.normalize(sourceName).endsWith("!") ? "!" : "?";
            String innerTransparent = transparentUnderlying(inner, project, contextModule);
            if (innerTransparent != null) return innerTransparent + suffix;
        }
        return null;
    }

    /**
     * Fully transparent spelling of a type: follows alias and
     * {@code inline} typedef links to a fixpoint
     * ({@code MutexFlags -> CUInt -> uint}). Stops at distinct typedefs,
     * structs and builtins (those are conversion barriers), returning
     * {@code null} when the input itself is already opaque. Bounded and
     * cycle-safe.
     */
    private static @Nullable String transparentUnderlying(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String current = typeName;
        for (int depth = 0; depth < 6; depth++)
        {
            String next = resolveAlias(current, project, contextModule, 0);
            if (next == null) next = resolveInlineTypedef(current, project, contextModule, 0);
            if (next == null) return depth == 0 ? null : current;
            if (TypeChecker.namesEqual(next, current)) return depth == 0 ? null : current;
            current = next;
        }
        return current;
    }

    /**
     * Underlying text of a type alias like {@code alias CharPtr = char*;}, or {@code null}.
     * Composite spellings resolve through their base: {@code CInt*} via
     * {@code CInt}, {@code Alias[4]} via {@code Alias} (c3c accepts
     * {@code &nm} for a {@code CInt*} parameter, so the check must see
     * through the alias).
     */
    static @Nullable String resolveAlias(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth)
    {
        if (depth > 4 || DumbService.isDumb(project)) return null;
        String composite = resolveCompositeBase(typeName, project, contextModule, depth, false);
        if (composite != null) return composite;
        if (!isUserTypeName(typeName)) return null;
        String simpleName = TypeChecker.shortName(TypeChecker.normalize(typeName));
        NamedTypeDecl match = pickDeclaration(findNamedTypeDecls(simpleName, project), typeName, contextModule);
        if (match == null || match.isTypedef) return null;
        String chained = resolveAlias(match.underlying, project, contextModule, depth + 1);
        return chained != null ? chained : match.underlying;
    }

    /**
     * Underlying text of a plain (non-inline) typedef, used for literals only.
     */
    private static @Nullable String resolveTypedef(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth)
    {
        if (depth > 4 || DumbService.isDumb(project)) return null;
        String composite = resolveCompositeBase(typeName, project, contextModule, depth, true);
        if (composite != null) return composite;
        if (!isUserTypeName(typeName)) return null;
        NamedTypeDecl match = pickDeclaration(
            findNamedTypeDecls(TypeChecker.shortName(TypeChecker.normalize(typeName)), project), typeName, contextModule);
        if (match == null || !match.isTypedef || match.inlineTypedef) return null;
        String chained = resolveTypedef(match.underlying, project, contextModule, depth + 1);
        return chained != null ? chained : match.underlying;
    }

    /**
     * Underlying text of an {@code inline} typedef, convertible both ways.
     */
    private static @Nullable String resolveInlineTypedef(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth)
    {
        if (depth > 4 || DumbService.isDumb(project)) return null;
        String composite = resolveCompositeBase(typeName, project, contextModule, depth, true);
        if (composite != null) return composite;
        if (!isUserTypeName(typeName)) return null;
        NamedTypeDecl match = pickDeclaration(
            findNamedTypeDecls(TypeChecker.shortName(TypeChecker.normalize(typeName)), project), typeName, contextModule);
        if (match == null || !match.isTypedef || !match.inlineTypedef) return null;
        String chained = resolveInlineTypedef(match.underlying, project, contextModule, depth + 1);
        return chained != null ? chained : match.underlying;
    }

    static boolean isUserTypeName(@NotNull String typeName)
    {
        String clean = TypeChecker.normalize(typeName);
        if (clean.contains("{") || clean.contains("}") || clean.contains("[")
            || clean.contains("]") || clean.contains("*") || clean.contains("?")
            || clean.contains("!") || clean.contains("(") || clean.contains(" ")) return false;
        String simpleName = TypeChecker.shortName(clean);
        if (TypeChecker.INT_TYPES.containsKey(simpleName) || TypeChecker.FLOAT_TYPES.containsKey(simpleName)) return false;
        return switch (simpleName)
        {
            case "void", "bool", "char", "String", "ZString", "any", "typeid", "fault" -> false;
            default -> simpleName.matches("[A-Za-z_][A-Za-z_0-9]*");
        };
    }

    /**
     * Alias/typedef resolution for composite spellings: strip one outer
     * suffix (`*`, `[]`, `[N]`, `[<N>]`), resolve the base, re-attach.
     * Only the alias path applies to every composite; typedefs resolve
     * through the base as well (their conversions are one-directional but
     * spelled through the same sugar). Returns {@code null} when the base
     * is not a resolvable user type, so plain callers keep their behavior.
     */
    private static @Nullable String resolveCompositeBase(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            boolean includeTypedefs)
    {
        String clean = TypeChecker.normalize(typeName);
        String base;
        String suffix;
        if (clean.endsWith("*") && !clean.endsWith("**"))
        {
            base = clean.substring(0, clean.length() - 1).strip();
            suffix = "*";
        }
        else if (clean.endsWith("[]"))
        {
            base = clean.substring(0, clean.length() - 2).strip();
            suffix = "[]";
        }
        else
        {
            TypeChecker.VectorInfo array = TypeChecker.parseArray(clean);
            TypeChecker.VectorInfo vector = array == null ? TypeChecker.parseVector(clean) : null;
            if (array == null && vector == null) return null;
            base = array != null ? array.element : vector.element;
            suffix = clean.substring(base.length());
        }
        if (!isUserTypeName(base)) return null;
        String resolved = resolveAlias(base, project, contextModule, depth + 1);
        if (resolved == null && includeTypedefs)
        {
            resolved = resolveInlineTypedef(base, project, contextModule, depth + 1);
        }
        if (resolved == null) return null;
        return resolved + suffix;
    }

    private static @Nullable NamedTypeDecl pickDeclaration(
            @NotNull List<NamedTypeDecl> candidates,
            @NotNull String requestedText,
            @Nullable ModuleName contextModule)
    {
        if (candidates.isEmpty()) return null;
        String full = TypeChecker.normalize(requestedText);
        for (NamedTypeDecl candidate : candidates)
        {
            if (candidate.module != null && full.equals(candidate.module.getValue() + "::" + candidate.nameElement.getText().strip()))
            {
                return candidate;
            }
        }
        if (contextModule != null)
        {
            for (NamedTypeDecl candidate : candidates)
            {
                if (contextModule.equals(candidate.module)) return candidate;
            }
        }
        return candidates.get(0);
    }

    private static @NotNull List<NamedTypeDecl> findNamedTypeDecls(@NotNull String shortName, @NotNull Project project)
    {
        List<NamedTypeDecl> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        List<C3TypeName> typeNames;
        try
        {
            typeNames = findIndexElements(TypeIndex.KEY, C3TypeName.class, shortName, project,
                typeName -> typeName.getText().strip().equals(shortName)
                    && (typeName.getParent() instanceof C3AliasTypeDecl
                        || typeName.getParent() instanceof C3TypedefDecl));
        }
        catch (Exception ignored)
        {
            return result;
        }
        for (C3TypeName typeName : typeNames)
        {
            PsiElement parent = typeName.getParent();
            boolean isTypedef = parent instanceof C3TypedefDecl;
            String underlying = underlyingTypeText(parent);
            if (underlying == null) continue;
            boolean inline = isTypedef && hasInlineModifier(parent);
            ModuleName module = ModuleName.from(typeName);
            result.add(new NamedTypeDecl(typeName, underlying, isTypedef, inline, module));
            if (result.size() > 25) return result;
        }
        return result;
    }

    private static @Nullable String underlyingTypeText(@NotNull PsiElement declaration)
    {
        C3TypedefType typedefType = null;
        if (declaration instanceof C3AliasTypeDecl aliasDecl)
        {
            if (aliasDecl.getGenericDecl() != null) return null;
            typedefType = aliasDecl.getTypedefType();
        }
        else if (declaration instanceof C3TypedefDecl typedefDecl)
        {
            if (typedefDecl.getGenericDecl() != null) return null;
            typedefType = typedefDecl.getTypedefType();
        }
        if (typedefType == null) return null;
        if (typedefType.getGenericParameters() != null) return null;
        C3Type type = typedefType.getType();
        if (type == null && typedefType.getExpr() instanceof C3TypeExpr typeExpr)
        {
            // Since 0.2.11 `typedef_type` prefers `expr`: a plain type RHS
            // parses as `type_expr` wrapping the type (`alias CharPtr = char*`).
            try
            {
                type = typeExpr.getType();
            }
            catch (Exception ignored)
            {
                type = null;
            }
        }
        if (type == null)
        {
            // `alias F = fn int(int);`: the right-hand side is a function
            // type, not an expression — expose it raw so fn-type aliases
            // resolve (used by lambda inference and, elsewhere, as a name
            // that is at least declared).
            String raw = typedefType.getText();
            if (raw != null && raw.strip().startsWith("fn ")) return raw.strip();
            // Compile-time computed right-hand side, e.g.
            // `alias CInt = $typefrom(signed_int_from_bitsize($$C_INT_SIZE));`.
            return evaluateComptimeAlias(typedefType.getExpr());
        }
        String text = type.getText();
        if (text == null || text.isBlank()) return null;
        String clean = text.strip();
        // Since `$typefrom` became a keyword, `$typefrom(...)` parses as a
        // type rather than a call: an evaluatable form resolves to the
        // builtin, anything else stays unresolved (lenient downstream).
        if (clean.startsWith("$typefrom(") || clean.startsWith("$Typefrom("))
        {
            return evaluateComptimeAliasText(clean);
        }
        return clean;
    }

    /**
     * Evaluates a compile-time alias right-hand side to a concrete builtin
     * type name. Handles the standard {@code std::core::cinterop} pattern
     * {@code $typefrom(signed_int_from_bitsize($$C_X_SIZE))} (and the
     * unsigned/legacy-capitalized variants), {@code $typefrom(X.typeid)}
     * and {@code $typefrom("name")}. Anything else returns {@code null}.
     * Pure text matching on the already-located declaration: no index access.
     */
    private static @Nullable String evaluateComptimeAlias(@Nullable C3Expr expr)
    {
        if (!(expr instanceof C3CallExpr call)) return null;
        C3CallExprTail tail = call.getCallExprTail();
        if (tail == null || tail.getCallInvocation() == null) return null;
        String callee = call.getExpr().getText().strip();
        if (!callee.equals("$typefrom") && !callee.equals("$Typefrom")) return null;
        C3CallArgList callArgs = tail.getCallInvocation().getCallArgList();
        C3ArgList args = callArgs != null ? callArgs.getArgList() : null;
        if (args == null || args.getArgList().size() != 1) return null;
        C3Expr arg = args.getArgList().get(0).getExpr();
        if (arg == null) return null;
        return evaluateTypefromInner(TypeChecker.normalize(arg.getText()));
    }

    /**
     * Text form of the above, for the post-keyword parse where
     * {@code $typefrom(...)} is a type node rather than a call.
     */
    private static @Nullable String evaluateComptimeAliasText(@NotNull String text)
    {
        String clean = TypeChecker.normalize(text);
        if ((!clean.startsWith("$typefrom(") && !clean.startsWith("$Typefrom(")) || !clean.endsWith(")")) return null;
        int open = clean.indexOf('(');
        return evaluateTypefromInner(clean.substring(open + 1, clean.length() - 1));
    }

    private static @Nullable String evaluateTypefromInner(@NotNull String inner)
    {
        java.util.regex.Matcher bitsize = BITSIZE_PATTERN.matcher(inner);
        if (bitsize.matches())
        {
            boolean signed = bitsize.group(1).equals("signed");
            int bits = cAbiBitsize(bitsize.group(2));
            if (bits < 0) return null;
            return signed ? SIGNED_BY_BITS.get(bits) : UNSIGNED_BY_BITS.get(bits);
        }
        java.util.regex.Matcher typeidAccess = TYPEID_PATTERN.matcher(inner);
        if (typeidAccess.matches()) return typeidAccess.group(1);
        java.util.regex.Matcher stringName = QUOTED_NAME_PATTERN.matcher(inner);
        if (stringName.matches()) return stringName.group(1);
        return null;
    }

    private static final java.util.regex.Pattern BITSIZE_PATTERN =
        java.util.regex.Pattern.compile("(?:[A-Za-z_][A-Za-z_0-9]*::)*(signed|unsigned)_int_from_bitsize\\(\\$\\$C_([A-Z_]+)_SIZE\\)");
    private static final java.util.regex.Pattern TYPEID_PATTERN =
        java.util.regex.Pattern.compile("([A-Za-z_][A-Za-z_0-9]*(?:::[A-Za-z_][A-Za-z_0-9]*)*)\\.typeid");
    private static final java.util.regex.Pattern QUOTED_NAME_PATTERN =
        java.util.regex.Pattern.compile("\"([A-Za-z_][A-Za-z_0-9]*(?:::[A-Za-z_][A-Za-z_0-9]*)*)\"");

    private static final java.util.Map<Integer, String> SIGNED_BY_BITS = java.util.Map.of(
        8, "ichar", 16, "short", 32, "int", 64, "long", 128, "int128");
    private static final java.util.Map<Integer, String> UNSIGNED_BY_BITS = java.util.Map.of(
        8, "char", 16, "ushort", 32, "uint", 64, "ulong", 128, "uint128");

    /**
     * Bit width of a C ABI type for the compilation target. Only
     * {@code long} differs between the data models (LP64 vs LLP64); without
     * a project target setting the host OS decides, which matches the
     * build host in the common case.
     */
    private static int cAbiBitsize(@NotNull String name)
    {
        return switch (name)
        {
            case "SHORT" -> 16;
            case "INT" -> 32;
            case "LONG_LONG" -> 64;
            case "LONG" -> isWindowsHost() ? 32 : 64;
            default -> -1;
        };
    }

    private static boolean isWindowsHost()
    {
        String os = System.getProperty("os.name", "");
        return os.toLowerCase(java.util.Locale.ROOT).contains("win");
    }
    static boolean isUnresolvedComptimeAlias(
            @NotNull String name,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        if (!isUserTypeName(name)) return false;
        if (resolveAlias(name, project, contextModule, 0) != null) return false;
        return hasComptimeAliasRhs(TypeChecker.shortName(TypeChecker.normalize(name)), project);
    }

    /**
     * Whether an alias/typedef with this short name has a call-expression
     * right-hand side that looks typeid-producing ({@code $typefrom},
     * {@code typeid}, {@code bitsize}). Pure index scan, no resolution.
     */
    private static boolean hasComptimeAliasRhs(@NotNull String shortName, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return false;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(shortName) && !key.endsWith("::" + shortName)) continue;
                for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
                {
                    if (!(element instanceof C3TypeName typeName)) continue;
                    if (!typeName.getText().strip().equals(shortName)) continue;
                    PsiElement parent = typeName.getParent();
                    C3TypedefType typedefType = null;
                    if (parent instanceof C3AliasTypeDecl aliasDecl)
                    {
                        if (aliasDecl.getGenericDecl() != null) continue;
                        typedefType = aliasDecl.getTypedefType();
                    }
                    else if (parent instanceof C3TypedefDecl typedefDecl)
                    {
                        if (typedefDecl.getGenericDecl() != null) continue;
                        typedefType = typedefDecl.getTypedefType();
                    }
                    if (typedefType == null || typedefType.getGenericParameters() != null) continue;
                    String rhsText = typedefType.getType() != null
                        ? typedefType.getType().getText()
                        : (typedefType.getExpr() != null ? typedefType.getExpr().getText() : null);
                    if (rhsText == null) continue;
                    String callText = rhsText.toLowerCase(java.util.Locale.ROOT);
                    if (callText.contains("typefrom") || callText.contains("typeid") || callText.contains("bitsize"))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    private static boolean hasInlineModifier(@NotNull PsiElement declaration)
    {
        ASTNode inline = declaration.getNode().findChildByType(C3Types.KW_INLINE);
        return inline != null;
    }

    /**
     * Backing type of an {@code inline} constdef, e.g. {@code char} for
     * {@code constdef Blake3Flags : inline char}. Values of an inline
     * constdef convert to the backing type implicitly (verified against
     * {@code c3c}); without {@code inline} (or without a backing type) the
     * constdef is distinct and needs an explicit cast.
     * Pure index scan with same-module preference, no resolution.
     */
    static @Nullable String inlineConstdefBacking(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String clean = TypeChecker.normalize(typeName);
        if (!isUserTypeName(clean) || DumbService.isDumb(project)) return null;
        String wanted = TypeChecker.shortName(clean);
        C3ConstdefDeclaration best = null;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
                for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
                {
                    if (!(element instanceof C3TypeName typeNameElement)) continue;
                    if (!typeNameElement.getText().strip().equals(wanted)) continue;
                    if (!(typeNameElement.getParent() instanceof C3ConstdefDeclaration constdef)) continue;
                    if (best == null) best = constdef;
                    if (contextModule != null && contextModule.equals(ModuleName.from(constdef))) best = constdef;
                }
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
        if (best == null || !hasInlineModifier(best)) return null;
        C3Type backing;
        try
        {
            backing = best.getType();
        }
        catch (Exception e)
        {
            return null;
        }
        if (backing == null) return null;
        String text = backing.getText();
        return text == null || text.isBlank() ? null : text.strip();
    }

    static boolean isConstdefName(@NotNull String typeName, @NotNull Project project)
    {
        String clean = TypeChecker.normalize(typeName);
        if (!isUserTypeName(clean) || DumbService.isDumb(project)) return false;
        String wanted = TypeChecker.shortName(clean);
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
                for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
                {
                    if (!(element instanceof C3TypeName typeNameElement)) continue;
                    if (!typeNameElement.getText().strip().equals(wanted)) continue;
                    if (typeNameElement.getParent() instanceof C3ConstdefDeclaration) return true;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    /**
     * Element type of a top-level {@code const} used as a member-access
     * root ({@code ASCII_LOOKUP} in {@code ASCII_LOOKUP[c].lower}):
     * ALL_CAPS globals parse as path consts, not path idents, so they
     * never reach {@code C3PathIdent.findTypeName}. Only the base type is
     * resolved (array suffixes are dropped), so subscripted uses chain
     * onto the element type.
     */
    public static @Nullable FullyQualifiedName constRootType(@NotNull C3PathConstExpr rootConstExpr)
    {
        try
        {
            PsiElement resolved = rootConstExpr.getPathConst().getReference().resolve();
            if (!(resolved instanceof C3ConstDeclarationStmt constDecl) || constDecl.getType() == null) return null;
            return FullyQualifiedName.from(constDecl.getType());
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
