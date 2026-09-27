package org.c3lang.intellij.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.TypeIndex;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3ArgList;
import org.c3lang.intellij.psi.C3BaseType;
import org.c3lang.intellij.psi.C3BinaryExpr;
import org.c3lang.intellij.psi.C3BinaryOp;
import org.c3lang.intellij.psi.C3BitstructBody;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3BitstructDef;
import org.c3lang.intellij.psi.C3BitstructSimpleDef;
import org.c3lang.intellij.psi.C3CallArgList;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CallInvocation;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3CompoundInitExpr;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3ConstdefDeclaration;
import org.c3lang.intellij.psi.C3CaseStmt;
import org.c3lang.intellij.psi.C3CatchUnwrap;
import org.c3lang.intellij.psi.C3CatchUnwrapMixin;
import org.c3lang.intellij.psi.C3CompoundStatement;
import org.c3lang.intellij.psi.C3Cond;
import org.c3lang.intellij.psi.C3CtIfStmt;
import org.c3lang.intellij.psi.C3DefaultStmt;
import org.c3lang.intellij.psi.C3ElsePart;
import org.c3lang.intellij.psi.C3EnumAccessExpr;
import org.c3lang.intellij.psi.C3EnumConstant;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3GroupedExpr;
import org.c3lang.intellij.psi.C3IfStmt;
import org.c3lang.intellij.psi.C3InitListExpr;
import org.c3lang.intellij.psi.C3InitializerList;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3KeywordExpr;
import org.c3lang.intellij.psi.C3Label;
import org.c3lang.intellij.psi.C3LambdaDecl;
import org.c3lang.intellij.psi.C3LambdaDeclExpr;
import org.c3lang.intellij.psi.C3LambdaDeclShortExpr;
import org.c3lang.intellij.psi.C3LiteralExpr;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ReturnStmt;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3ParameterList;
import org.c3lang.intellij.psi.C3ParenCond;
import org.c3lang.intellij.psi.C3PathAtIdentExpr;
import org.c3lang.intellij.psi.C3PathConstExpr;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Statement;
import org.c3lang.intellij.psi.C3StatementList;
import org.c3lang.intellij.psi.C3StringExpr;
import org.c3lang.intellij.psi.C3StructBody;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.C3SwitchBody;
import org.c3lang.intellij.psi.C3SwitchStmt;
import org.c3lang.intellij.psi.C3TernaryExpr;
import org.c3lang.intellij.psi.C3TryUnwrap;
import org.c3lang.intellij.psi.C3TryUnwrapChain;
import org.c3lang.intellij.psi.C3TryUnwrapMixin;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeExpr;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.C3TypedefType;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.C3UnaryExpr;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.ShortType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Expression type inference and assignability checks for C3.
 *
 * <p>Rules follow {@code docs/basic-types-and-values.md}: integer literal fits,
 * implicit literal-to-float conversion, string literal targets, {@code void*}
 * converting to any pointer. Anything that cannot be proven is
 * {@code null} (unknown) and never produces an error.
 */
public final class TypeChecker
{
    private static final int MAX_DEPTH = 8;

    /**
     * Integer type name to [bits, signed(1/0)]. Pointer-sized types assume a 64-bit target.
     */
    private static final Map<String, int[]> INT_TYPES = new HashMap<>();

    private static final Map<String, Integer> FLOAT_TYPES = new HashMap<>();

    static
    {
        INT_TYPES.put("ichar", new int[]{8, 1});
        INT_TYPES.put("char", new int[]{8, 0});
        INT_TYPES.put("short", new int[]{16, 1});
        INT_TYPES.put("ushort", new int[]{16, 0});
        INT_TYPES.put("int", new int[]{32, 1});
        INT_TYPES.put("uint", new int[]{32, 0});
        INT_TYPES.put("long", new int[]{64, 1});
        INT_TYPES.put("ulong", new int[]{64, 0});
        INT_TYPES.put("int128", new int[]{128, 1});
        INT_TYPES.put("uint128", new int[]{128, 0});
        INT_TYPES.put("iptr", new int[]{64, 1});
        INT_TYPES.put("uptr", new int[]{64, 0});
        INT_TYPES.put("sz", new int[]{64, 1});
        INT_TYPES.put("isz", new int[]{64, 1});
        INT_TYPES.put("usz", new int[]{64, 0});

        FLOAT_TYPES.put("float16", 16);
        FLOAT_TYPES.put("bfloat16", 16);
        FLOAT_TYPES.put("float", 32);
        FLOAT_TYPES.put("double", 64);
        FLOAT_TYPES.put("float128", 128);
    }

    private TypeChecker()
    {
    }

    // ------------------------------------------------------------------
    // Entry points used by the annotator
    // ------------------------------------------------------------------

    /**
     * @return error message or {@code null} when the value fits the target.
     */
    public static @Nullable String assignmentError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @Nullable InferredType source)
    {
        return assignmentError(project, contextModule, targetText, source, null);
    }

    /**
     * @param useSite the right-hand side expression, for Optional narrowing
     *                via preceding {@code if (catch)} / {@code if (!x)} guards.
     */
    public static @Nullable String assignmentError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @Nullable InferredType source,
            @Nullable C3Expr useSite)
    {
        return assignmentError(project, contextModule, targetText, source, useSite, null);
    }

    /**
     * @param assignOp the assignment operator ({@code "="}, {@code "+="}, ...),
     *                 for pointer arithmetic ({@code void* += usz}).
     */
    public static @Nullable String assignmentError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @Nullable InferredType source,
            @Nullable C3Expr useSite,
            @Nullable String assignOp)
    {
        if (source == null) return null;
        source = narrowedSource(useSite, source);
        if (("+=".equals(assignOp) || "-=".equals(assignOp)) && isPointerArithmetic(targetText, source)) return null;
        Mismatch mismatch = check(project, contextModule, targetText, source);
        if (mismatch == null) return null;
        String implicitBitstruct = bitstructImplicitCastError(project, contextModule, targetText, source);
        if (implicitBitstruct != null) return implicitBitstruct;
        if (mismatch.intValue != null)
        {
            return "Integer value " + mismatch.intValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.floatValue != null)
        {
            return "Floating point value " + mismatch.floatValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.count >= 0)
        {
            return "Expected " + mismatch.count + " elements for type '" + mismatch.targetName
                + "' but got " + mismatch.actual + ".";
        }
        return "Cannot assign '" + mismatch.sourceName + "' to '" + mismatch.targetName + "'."
            + unwrapHint(mismatch.sourceName);
    }

    /**
     * Hint appended when an Optional value meets a plain type.
     */
    private static @NotNull String unwrapHint(@NotNull String sourceName)
    {
        if (isOptionalName(sourceName))
        {
            return " Use '!' to rethrow, '!!' to force unwrap or '??' for a default value.";
        }
        return "";
    }

    /**
     * @return error message or {@code null} when the value fits the return type.
     */
    public static @Nullable String returnError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String returnTypeText,
            @Nullable InferredType source)
    {
        return returnError(project, contextModule, returnTypeText, source, null);
    }

    /**
     * @param useSite the returned expression, for Optional narrowing via
     *                preceding {@code if (catch)} / {@code if (!x)} guards.
     */
    public static @Nullable String returnError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String returnTypeText,
            @Nullable InferredType source,
            @Nullable C3Expr useSite)
    {
        if (source == null) return null;
        source = narrowedSource(useSite, source);
        Mismatch mismatch = check(project, contextModule, returnTypeText, source);
        if (mismatch == null) return null;
        String implicitBitstruct = bitstructImplicitCastError(project, contextModule, returnTypeText, source);
        if (implicitBitstruct != null) return implicitBitstruct;
        if (mismatch.intValue != null)
        {
            return "Integer value " + mismatch.intValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.floatValue != null)
        {
            return "Floating point value " + mismatch.floatValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.count >= 0)
        {
            return "Expected " + mismatch.count + " elements for type '" + mismatch.targetName
                + "' but got " + mismatch.actual + ".";
        }
        return "Cannot return '" + mismatch.sourceName + "' from function returning '" + mismatch.targetName + "'."
            + unwrapHint(mismatch.sourceName);
    }

    // ------------------------------------------------------------------
    // Explicit casts: (Type)expr
    // ------------------------------------------------------------------

    /**
     * Diagnostic for an explicit cast, or {@code null} when the cast is fine.
     * Rules follow the C3 specification ("Cast expression"): numeric to numeric,
     * pointer to pointer, pointer to/from a pointer-sized integer, vector/array
     * with the same element type and size, alias/typedef chains, and
     * interface to/from {@code any}. Anything else is a compile error; casts
     * involving {@code any}/interfaces carry a runtime check and produce a
     * warning instead.
     */
    public static @Nullable CastDiagnostic checkCast(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @Nullable InferredType source)
    {
        return checkCast(project, contextModule, targetText, source, null);
    }

    /**
     * @param operand the cast operand, used to tell simple expressions from
     *                complex ones for the narrowing warning; may be {@code null}.
     */
    public static @Nullable CastDiagnostic checkCast(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @Nullable InferredType source,
            @Nullable C3Expr operand)
    {
        if (source == null) return null;
        // Compile-time type parameters are opaque until instantiation.
        if (isComptimeParam(targetText) || isComptimeParam(source.getName())) return null;
        String target = stripOptional(normalize(targetText));
        if (target.isEmpty()) return null;
        String sourceName = normalize(source.getName());

        // Discarding a value is always fine: (void)expr.
        if (target.equals("void")) return null;

        // Casting an Optional always lifts to an Optional result, propagating
        // any fault: (T)expr? has type T?. Whether that fits its context is
        // decided by assignment checking on the inferred T?.
        if (isOptionalName(sourceName))
        {
            String liftedTarget = isOptionalName(normalize(targetText)) ? target : target + "?";
            return checkCast(project, contextModule, liftedTarget, kindOf(stripOptional(sourceName)), operand);
        }
        // A `$typeof(...)` cast target is the operand's own type.
        if (isTypeofTarget(target)) return null;

        // Resolve alias/typedef chains on both sides first (§2.5): after
        // resolution the names may simply match.
        String resolvedTarget = resolveCastType(target, project, contextModule);
        String resolvedSource = resolveCastType(sourceName, project, contextModule);
        InferredType effectiveSource = source.getName().equals(resolvedSource) ? source : kindOf(resolvedSource);

        if (namesEqual(resolvedTarget, resolvedSource)) return null;

        // Bitstruct casts (verified against c3c): a bitstruct converts
        // explicitly to any integer/bool, but to nothing else
        // (`(float)s` is `You cannot cast 'Sb' to 'float'.`); a bitstruct is
        // built back only from its exact backing type (`(Sb)char` is fine,
        // `(Sb)uint` is not). Literals stay lenient (c3c checks the value).
        String targetBitBacking = bitstructBacking(resolvedTarget, project, contextModule);
        String sourceBitBacking = bitstructBacking(resolvedSource, project, contextModule);
        if (sourceBitBacking != null && targetBitBacking == null)
        {
            if (isIntegerName(resolvedTarget) || shortName(resolvedTarget).equals("bool")) return null;
            return CastDiagnostic.error("You cannot cast '" + sourceName + "' to '" + shortName(target) + "'.");
        }
        if (targetBitBacking != null && sourceBitBacking == null)
        {
            if (namesEqual(resolvedSource, targetBitBacking)) return null;
            if (effectiveSource.isLiteral()) return null;
            return CastDiagnostic.error("It is not possible to cast '" + sourceName + "' to '" + shortName(target) + "'.");
        }

        boolean targetNumeric = isNumericName(resolvedTarget);
        boolean sourceNumeric = isNumericKind(effectiveSource) || isNumericName(resolvedSource);
        if (targetNumeric && sourceNumeric)
        {
            return narrowingWarning(resolvedTarget, effectiveSource, operand);
        }

        boolean targetPointer = isPointerLikeName(resolvedTarget);
        boolean sourcePointer = effectiveSource.getKind() == InferredType.Kind.POINTER
            || isPointerLikeName(resolvedSource);
        if (targetPointer && sourcePointer) return null;
        if (effectiveSource.getKind() == InferredType.Kind.POINTER && isIntegerName(resolvedTarget))
        {
            if (isPointerSizedIntName(resolvedTarget)) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target)
                + "': only pointer-sized integers (iptr, uptr) can hold a pointer.");
        }
        if (targetPointer && isIntegerName(resolvedSource))
        {
            if (isPointerSizedIntName(resolvedSource)) return null;
            // A literal zero is the null pointer constant.
            if (effectiveSource.isLiteral() && BigInteger.ZERO.equals(effectiveSource.getIntValue())) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target)
                + "': only pointer-sized integers (iptr, uptr) convert to a pointer.");
        }

        // `typeid` casts like its machine-word self: explicitly to any
        // pointer, to `bool` and to pointer-sized integers; to smaller
        // integers only through a lossy `(T)(iptr)` chain; never from
        // integers/pointers and never to `any`/floats/Strings.
        if (isTypeidName(resolvedSource) || isTypeidName(resolvedTarget))
        {
            return typeidCast(resolvedTarget, resolvedSource, sourceName, target);
        }

        if (vectorCastCompatible(resolvedTarget, resolvedSource)) return null;
        if (arrayTypesEqualSize(resolvedTarget, resolvedSource, project, contextModule)) return null;

        if (effectiveSource.getKind() == InferredType.Kind.BOOL || resolvedSource.equals("bool"))
        {
            if (resolvedTarget.equals("bool")) return null;
            if (isIntegerName(resolvedTarget)) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target) + "'.");
        }
        if (resolvedTarget.equals("bool"))
        {
            if (isIntegerName(resolvedSource) || effectiveSource.getKind() == InferredType.Kind.BOOL) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to 'bool'.");
        }

        if (isStringName(resolvedTarget) || isStringKind(effectiveSource))
        {
            if (isStringCompatible(resolvedTarget, effectiveSource, resolvedSource)) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target) + "'.");
        }

        if (effectiveSource.getKind() == InferredType.Kind.NULL)
        {
            if (targetPointer || target.endsWith("?") || target.endsWith("!")) return null;
            return CastDiagnostic.error("Cannot cast 'null' to '" + shortName(target) + "'.");
        }
        if (effectiveSource.getKind() == InferredType.Kind.VOID)
        {
            return CastDiagnostic.error("Cannot cast 'void' to '" + shortName(target) + "'.");
        }

        if (resolvedTarget.equals("any") || resolvedSource.equals("any"))
        {
            return CastDiagnostic.runtimeWarning(target, sourceName);
        }

        boolean targetInterface = isInterfaceName(resolvedTarget, project);
        boolean sourceInterface = isInterfaceName(resolvedSource, project);
        if (targetInterface && sourceInterface)
        {
            return CastDiagnostic.error("Cannot cast interface '" + sourceName + "' to interface '"
                + shortName(target) + "' directly, convert through 'any' first.");
        }
        if (targetInterface || sourceInterface)
        {
            if (targetInterface && staticallyImplements(resolvedSource, resolvedTarget, project)) return null;
            return CastDiagnostic.runtimeWarning(target, sourceName);
        }

        boolean targetEnum = isEnumName(resolvedTarget, project);
        boolean sourceEnum = isEnumName(resolvedSource, project);
        boolean sourceConstdef = !sourceEnum && isConstdefName(resolvedSource, project);
        if (targetEnum || sourceEnum || sourceConstdef)
        {
            if (targetEnum && sourceEnum) return CastDiagnostic.error("Cannot cast enum '" + sourceName
                + "' to enum '" + shortName(target) + "'.");
            // Explicit casts between a constdef and integers are fine, inline
            // or not (only the implicit form requires `inline`).
            if (isIntegerName(targetEnum || sourceConstdef ? resolvedSource : resolvedTarget)
                || isNumericKind(targetEnum ? effectiveSource : kindOf(resolvedTarget))) return null;
            return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target) + "'.");
        }

        if (isStructName(resolvedTarget, project) && isStructName(resolvedSource, project))
        {
            if (isSubstructOf(resolvedSource, resolvedTarget, project)) return null;
            return CastDiagnostic.error("Cannot cast struct '" + sourceName + "' to struct '"
                + shortName(target) + "': no substruct relation, use an explicit conversion instead.");
        }
        if (arraySubstructCast(resolvedTarget, resolvedSource, project))
        {
            return CastDiagnostic.error("Cannot cast array of substruct '" + sourceName + "' to array of '"
                + shortName(target) + "': substruct arrays never convert, not even with an explicit cast.");
        }

        // One side (or both) is an unknown named type: cannot prove it is
        // forbidden, so stay silent instead of false-positive.
        if (isUnresolvableName(resolvedTarget, project) || isUnresolvableName(resolvedSource, project)) return null;

        return CastDiagnostic.error("Cannot cast '" + sourceName + "' to '" + shortName(target) + "'.");
    }

    /**
     * Severity-tagged cast diagnostic.
     */
    public static final class CastDiagnostic
    {
        /**
         * True for the runtime-checked {@code any}/interface warning, false for a hard error.
         */
        public final boolean warning;
        public final @NotNull String message;

        private CastDiagnostic(boolean warning, @NotNull String message)
        {
            this.warning = warning;
            this.message = message;
        }

        static @NotNull CastDiagnostic error(@NotNull String message)
        {
            return new CastDiagnostic(false, message);
        }

        static @NotNull CastDiagnostic runtimeWarning(@NotNull String target, @NotNull String sourceName)
        {
            return new CastDiagnostic(true, "Cast from '" + sourceName + "' to '" + shortName(target)
                + "' is checked at runtime and may fail.");
        }

        static @NotNull CastDiagnostic narrowingWarning(@NotNull String shortTarget)
        {
            return new CastDiagnostic(true, "Cast to '" + shortTarget
                + "' may silently lose precision for a non-constant expression.");
        }
    }

    private static boolean isNumericName(@NotNull String typeName)
    {
        String shortTarget = shortName(typeName);
        if (INT_TYPES.containsKey(shortTarget) || FLOAT_TYPES.containsKey(shortTarget)) return true;
        return shortTarget.equals("char") || shortTarget.equals("ichar") || shortTarget.equals("bool");
    }

    private static boolean isNumericKind(@NotNull InferredType type)
    {
        return type.getKind() == InferredType.Kind.INT
            || type.getKind() == InferredType.Kind.FLOAT
            || type.getKind() == InferredType.Kind.CHAR;
    }

    private static boolean isIntegerName(@NotNull String typeName)
    {
        String shortTarget = shortName(typeName);
        if (INT_TYPES.containsKey(shortTarget)) return true;
        return shortTarget.equals("char") || shortTarget.equals("ichar");
    }

    private static boolean isPointerName(@NotNull String typeName)
    {
        String clean = normalize(typeName);
        return clean.endsWith("*") && !clean.endsWith("**") && parseArrayPointer(clean) == null
            || clean.endsWith("**");
    }

    private static boolean isPointerLikeName(@NotNull String typeName)
    {
        String clean = normalize(typeName);
        if (clean.endsWith("*")) return true;
        if (clean.endsWith("[]")) return true;
        return parseArray(clean) != null || parseVector(clean) != null;
    }

    private static boolean isPointerSizedIntName(@NotNull String typeName)
    {
        return switch (shortName(normalize(typeName)))
        {
            case "iptr", "uptr", "sz", "isz", "usz", "long", "ulong" -> true;
            default -> false;
        };
    }

    private static boolean isStringName(@NotNull String typeName)
    {
        return switch (shortName(normalize(typeName)))
        {
            case "String", "ZString" -> true;
            default -> false;
        };
    }

    private static boolean isStringKind(@NotNull InferredType type)
    {
        if (type.getKind() == InferredType.Kind.STRING) return true;
        String name = normalize(type.getName());
        return name.equals("char[]") || name.equals("ichar[]") || name.equals("char*") || name.equals("ichar*");
    }

    private static boolean isStringCompatible(
            @NotNull String resolvedTarget, @NotNull InferredType effectiveSource, @NotNull String resolvedSource)
    {
        if (isStringKind(effectiveSource) && (isStringName(resolvedTarget) || isStringKind(kindOf(resolvedTarget)))) return true;
        if (isStringName(resolvedSource) && (isStringName(resolvedTarget) || isStringKind(kindOf(resolvedTarget)))) return true;
        return isStringName(resolvedTarget) && isPointerName(resolvedSource);
    }

    private static boolean vectorCastCompatible(@NotNull String resolvedTarget, @NotNull String resolvedSource)
    {
        VectorInfo targetVector = parseVector(resolvedTarget);
        VectorInfo sourceVector = parseVector(resolvedSource);
        if (targetVector != null || sourceVector != null)
        {
            if (targetVector == null || sourceVector == null) return false;
            if (!namesEqual(targetVector.element, sourceVector.element)) return false;
            // Equal numeric sizes, or the same symbolic size (`Real[<N>]`).
            return (targetVector.size == sourceVector.size && targetVector.size >= 0)
                || targetVector.sizeText.equals(sourceVector.sizeText);
        }
        VectorInfo targetArray = parseArray(resolvedTarget);
        VectorInfo sourceArray = parseArray(resolvedSource);
        if (targetArray == null || sourceArray == null) return false;
        if (!namesEqual(targetArray.element, sourceArray.element)) return false;
        return (targetArray.size == sourceArray.size && targetArray.size >= 0)
            || targetArray.sizeText.equals(sourceArray.sizeText);
    }

    private static @Nullable String resolveTypedefChain(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String current = typeName;
        for (int depth = 0; depth < 4; depth++)
        {
            String next = resolveTypedef(current, project, contextModule, 0);
            if (next == null) next = resolveInlineTypedef(current, project, contextModule, 0);
            if (next == null) return depth == 0 ? null : current;
            current = next;
        }
        return current;
    }

    /**
     * Full chain resolution for explicit casts: unlike implicit conversions,
     * a cast may cross any mixture of {@code alias} and (inline or distinct)
     * {@code typedef} links (spec §2.5), e.g.
     * {@code Errno -> inline CInt -> $typefrom(...) -> int}.
     * Bounded and cycle-safe; returns the input when nothing resolves.
     */
    private static @NotNull String resolveCastType(
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
            if (next == null || namesEqual(next, current)) return current;
            current = next;
        }
        return current;
    }

    private static boolean isInterfaceName(@NotNull String typeName, @NotNull Project project)
    {
        return findTypeParent(typeName, project) instanceof C3InterfaceDefinition;
    }

    private static boolean isEnumName(@NotNull String typeName, @NotNull Project project)
    {
        return findTypeParent(typeName, project) instanceof C3EnumDeclaration;
    }

    private static boolean isStructName(@NotNull String typeName, @NotNull Project project)
    {
        PsiElement parent = findTypeParent(typeName, project);
        return parent instanceof C3StructDeclaration || parent instanceof C3BitstructDeclaration;
    }

    private static boolean isUnresolvableName(@NotNull String typeName, @NotNull Project project)
    {
        String clean = normalize(typeName);
        if (!isUserTypeName(clean)) return false;
        if (DumbService.isDumb(project)) return true;
        return findTypeParent(clean, project) == null;
    }

    private static @Nullable PsiElement findTypeParent(@NotNull String typeName, @NotNull Project project)
    {
        String clean = normalize(typeName);
        if (!isUserTypeName(clean)) return null;
        String wanted = shortName(clean);
        for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
        {
            if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
            for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
            {
                if (!(element instanceof C3TypeName typeNameElement)) continue;
                if (!typeNameElement.getText().strip().equals(wanted)) continue;
                PsiElement parent = typeNameElement.getParent();
                if (parent instanceof C3StructDeclaration
                    || parent instanceof C3BitstructDeclaration
                    || parent instanceof C3EnumDeclaration
                    || parent instanceof C3InterfaceDefinition
                    || parent instanceof C3TypedefDecl
                    || parent instanceof C3AliasTypeDecl)
                {
                    return parent;
                }
            }
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

    private static boolean staticallyImplements(
            @NotNull String sourceName, @NotNull String ifaceName, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return false;
        try
        {
            FullyQualifiedName source = FullyQualifiedName.parse(sourceName);
            List<FullyQualifiedName> implemented =
                org.c3lang.intellij.index.InterfaceService.INSTANCE.getImplementedInterfaces(source, project);
            String wanted = shortName(ifaceName);
            for (FullyQualifiedName candidate : implemented)
            {
                if (candidate.getName().equals(wanted) || candidate.getFullName().equals(ifaceName)) return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
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
        return COMPTIME_PARAM_PATTERN.matcher(normalize(typeName)).find();
    }

    private static final java.util.regex.Pattern COMPTIME_PARAM_PATTERN =
        java.util.regex.Pattern.compile("\\$[A-Z]");

    /**
     * Whether the text is a {@code $typeof(...)} type (either letter case).
     */
    static boolean isTypeofTarget(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        return (clean.startsWith("$typeof(") || clean.startsWith("$Typeof(")) && clean.endsWith(")");
    }

    /**
     * {@code void*} stays a wildcard behind {@code alias}/{@code typedef}
     * links: when either side resolves to {@code void*}, pointer conversions
     * apply as if it were written directly.
     */
    private static boolean voidStarTransparent(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull InferredType source)
    {
        if (DumbService.isDumb(project)) return false;
        String cleanTarget = stripOptional(normalize(targetText));
        String sourceName = normalize(source.getName());
        if (!isUserTypeName(cleanTarget) && !isUserTypeName(sourceName)) return false;
        String resolvedTarget = resolveCastType(cleanTarget, project, contextModule);
        String resolvedSource = resolveCastType(sourceName, project, contextModule);
        boolean targetIsVoid = resolvedTarget.equals("void*");
        boolean sourceIsVoid = resolvedSource.equals("void*");
        if (!targetIsVoid && !sourceIsVoid) return false;
        if (targetIsVoid && sourceIsVoid) return true;
        if (targetIsVoid) return isVoidPointerCompatible(source);
        return cleanTarget.endsWith("*");
    }

    /**
     * Implicit conversion of a struct (or a pointer to it) to an interface
     * it implements, e.g. {@code File*} to {@code OutStream} when declared
     * as {@code struct File (InStream, OutStream)}, or {@code DString*} to
     * {@code OutStream} for {@code typedef DString (OutStream) = ...}.
     * Mirrors the compiler rule behind {@code MyName a = &b;}.
     */
    private static boolean interfaceAssignable(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull InferredType source)
    {
        if (DumbService.isDumb(project)) return false;
        String cleanTarget = stripOptional(normalize(targetText));
        if (!isUserTypeName(cleanTarget)) return false;
        String resolvedIface = resolveAlias(cleanTarget, project, contextModule, 0);
        if (resolvedIface != null) cleanTarget = stripOptional(normalize(resolvedIface));
        if (!isInterfaceName(cleanTarget, project)) return false;
        String structName = interfaceSourceStruct(source);
        if (structName == null) return false;
        // The contract may sit on the named type itself (a typedef like
        // `DString`), not only on the resolved underlying struct: check the
        // original name first, then the alias-resolved one.
        if (staticallyImplements(structName, cleanTarget, project)) return true;
        String resolved = resolveAlias(structName, project, contextModule, 0);
        if (resolved != null) structName = resolved;
        return staticallyImplements(structName, cleanTarget, project);
    }

    /**
     * Struct behind a pointer source for interface conversion, e.g.
     * {@code File} for {@code File*}. Only pointers convert implicitly: a
     * struct value would need an explicit address-of (an rvalue would
     * otherwise dangle behind the interface reference).
     */
    private static @Nullable String interfaceSourceStruct(@NotNull InferredType source)
    {
        if (source.getKind() != InferredType.Kind.POINTER) return null;
        String pointee = normalize(source.getName());
        while (pointee.endsWith("*")) pointee = pointee.substring(0, pointee.length() - 1).strip();
        if (!isUserTypeName(pointee)) return null;
        return pointee;
    }

    private static boolean isSubstructOf(
            @NotNull String childName, @NotNull String parentName, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return false;
        try
        {
            FullyQualifiedName child = FullyQualifiedName.parse(childName);
            List<C3StructDeclaration> declarations =
                org.c3lang.intellij.index.InterfaceService.INSTANCE.findStructDeclarations(child, project);
            String wanted = shortName(parentName);
            for (C3StructDeclaration declaration : declarations)
            {
                C3StructBody body = declaration.getStructBody();
                if (body == null) continue;
                for (C3StructMemberDeclaration member : body.getStructMemberDeclarationList())
                {
                    // An inline substruct member is written as a bare type (`inline Foo;`).
                    if (member.getIdentifierList() != null) continue;
                    if (member.getStructBody() != null || member.getBitstructBody() != null) continue;
                    C3Type memberType = member.getType();
                    if (memberType == null) continue;
                    if (shortName(normalize(memberType.getText())).equals(wanted)) return true;
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    private static boolean arraySubstructCast(
            @NotNull String resolvedTarget, @NotNull String resolvedSource, @NotNull Project project)
    {
        String targetElement = arrayElementType(resolvedTarget);
        String sourceElement = arrayElementType(resolvedSource);
        if (targetElement == null || sourceElement == null) return false;
        if (namesEqual(targetElement, sourceElement)) return false;
        return isStructName(targetElement, project) && isStructName(sourceElement, project)
            && (isSubstructOf(sourceElement, targetElement, project)
                || isSubstructOf(targetElement, sourceElement, project));
    }

    /**
     * Warning for {@code (narrow_type)complex_expr}: the value may silently
     * lose precision even though the compiler accepts the cast. Literals and
     * simple expressions are skipped.
     */
    private static @Nullable CastDiagnostic narrowingWarning(
            @NotNull String resolvedTarget, @NotNull InferredType effectiveSource, @Nullable C3Expr operand)
    {
        if (operand == null || isSimpleOperand(operand)) return null;
        if (effectiveSource.isLiteral()) return null;
        String shortTarget = shortName(resolvedTarget);
        if (shortTarget.equals("bool")) return null;
        int[] targetBits = INT_TYPES.get(shortTarget);
        if (targetBits == null && !shortTarget.equals("char") && !shortTarget.equals("ichar")) return null;
        int targetWidth = targetBits != null ? targetBits[0] : 8;
        Integer sourceWidth = numericWidth(effectiveSource);
        // int -> float widens implicitly and is always fine.
        if (FLOAT_TYPES.containsKey(shortTarget)) return null;
        if (effectiveSource.getKind() == InferredType.Kind.FLOAT && targetBits == null) return null;
        if (sourceWidth != null && sourceWidth <= targetWidth) return null;
        return CastDiagnostic.narrowingWarning(shortTarget);
    }

    private static @Nullable Integer numericWidth(@NotNull InferredType type)
    {
        String shortSource = shortName(type.getName());
        int[] intBits = INT_TYPES.get(shortSource);
        if (intBits != null) return intBits[0];
        if (shortSource.equals("char") || shortSource.equals("ichar")) return 8;
        Integer floatBits = FLOAT_TYPES.get(shortSource);
        if (floatBits != null) return floatBits;
        return switch (type.getKind())
        {
            case INT, CHAR -> 32;
            case FLOAT -> 64;
            default -> null;
        };
    }

    private static boolean isSimpleOperand(@NotNull C3Expr operand)
    {
        if (operand instanceof C3LiteralExpr
            || operand instanceof C3StringExpr
            || operand instanceof C3KeywordExpr
            || operand instanceof C3PathIdentExpr
            || operand instanceof C3PathConstExpr) return true;
        if (operand instanceof C3GroupedExpr grouped)
        {
            C3Expr inner = grouped.getExpr();
            return inner != null && isSimpleOperand(inner);
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Compatibility
    // ------------------------------------------------------------------

    /**
     * @return error message or {@code null} when the argument fits the parameter.
     */
    public static @Nullable String argumentError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String paramName,
            @NotNull String paramTypeText,
            @Nullable InferredType arg)
    {
        if (arg == null) return null;
        if (isOptionalName(arg.getName()) && !isOptionalName(normalize(paramTypeText)))
        {
            // Cascading: a function called with an Optional argument is only
            // executed when all Optional arguments hold results, and its own
            // result becomes Optional. Only the unwrapped types must match.
            Mismatch unwrapped = check(project, contextModule, paramTypeText, kindOf(stripOptional(arg.getName())));
            if (unwrapped == null) return null;
            // Otherwise fall through and report the Optional mismatch below.
        }
        Mismatch mismatch = check(project, contextModule, paramTypeText, arg);
        if (mismatch == null) return null;
        // Undeclared (e.g. generic) parameter types are not checked.
        if (!isDeclaredType(paramTypeText, project, contextModule)) return null;
        String implicitBitstruct = bitstructImplicitCastError(project, contextModule, paramTypeText, arg);
        if (implicitBitstruct != null) return implicitBitstruct;
        if (mismatch.intValue != null)
        {
            return "Integer value " + mismatch.intValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.floatValue != null)
        {
            return "Floating point value " + mismatch.floatValue + " does not fit in type '" + mismatch.targetName + "'.";
        }
        if (mismatch.count >= 0)
        {
            return "Expected " + mismatch.count + " elements for type '" + mismatch.targetName
                + "' but got " + mismatch.actual + ".";
        }
        return "Cannot pass '" + mismatch.sourceName + "' for parameter '" + paramName
            + "' of type '" + mismatch.targetName + "'." + unwrapHint(mismatch.sourceName);
    }

    /**
     * Whether a written type is a known type: a primitive/keyword, a compound
     * type, or a bare identifier declared as a type somewhere.
     */
    static boolean isDeclaredType(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String clean = normalize(typeText);
        while (true)
        {
            if (clean.endsWith("*") || clean.endsWith("?") || clean.endsWith("!"))
            {
                clean = clean.substring(0, clean.length() - 1);
                continue;
            }
            VectorInfo vector = parseVector(clean);
            if (vector != null)
            {
                clean = vector.element;
                continue;
            }
            VectorInfo array = parseArray(clean);
            if (array != null)
            {
                clean = array.element;
                continue;
            }
            break;
        }
        String shortName = shortName(clean);
        if (INT_TYPES.containsKey(shortName) || FLOAT_TYPES.containsKey(shortName)) return true;
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

    public static boolean isVoidType(@NotNull String typeText)
    {
        return normalize(typeText).equals("void");
    }

    /**
     * Whether the text is the {@code void?} Optional: only usable as a
     * function return type, never as a variable.
     */
    public static boolean isVoidOptionalType(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        return clean.equals("void?") || clean.equals("void!");
    }

    public static @NotNull String normalize(@NotNull String typeText)
    {
        return typeText.replaceAll("\\s+", "");
    }

    public static @NotNull String shortName(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        int separator = clean.lastIndexOf("::");
        return separator >= 0 ? clean.substring(separator + 2) : clean;
    }

    private static boolean namesEqual(@NotNull String a, @NotNull String b)
    {
        if (a.equals(b)) return true;
        String shortA = shortName(a);
        String shortB = shortName(b);
        return !shortA.isEmpty() && shortA.equals(shortB);
    }

    /**
     * @return {@code null} when assignable, otherwise the mismatch details.
     */
    static @Nullable Mismatch check(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull InferredType source)
    {
        String target = normalize(targetText);
        if (target.equals("_") || target.equals("auto")) return null;

        if (source.getKind() == InferredType.Kind.INIT_LIST)
        {
            String resolvedTarget = resolveAlias(target, project, contextModule, 0);
            return checkInitList(project, contextModule, resolvedTarget != null ? resolvedTarget : target, source);
        }

        Mismatch direct = checkOnce(target, source);
        if (direct == null) return null;
        if (isComptimeParam(target) || isComptimeParam(source.getName())) return null;
        if (voidStarTransparent(project, contextModule, target, source)) return null;
        if (interfaceAssignable(project, contextModule, target, source)) return null;
        if (isComptimeNumericLenient(target, source, project, contextModule)) return null;
        // Fixed arrays and vectors of equal length accept each other even
        // when the size is spelled differently (`uint[16]` vs
        // `uint[BLOCK_SIZE/uint.sizeof]`).
        if (arrayTypesEqualSize(target, source.getName(), project, contextModule)) return null;

        // Resolve type aliases (and typedefs for literals / inline typedef sources).
        TargetInfo resolvedTarget = resolveTargetType(project, contextModule, target);
        InferredType resolvedSource = source;
        String resolvedSourceName = resolveSourceType(project, contextModule, source.getName());
        if (resolvedSourceName != null) resolvedSource = kindOf(resolvedSourceName);
        if (resolvedSource == source)
        {
            // Values of an `inline` constdef convert through the backing
            // type, e.g. `Blake3Flags` through `char`.
            String backing = inlineConstdefBacking(source.getName(), project, contextModule);
            if (backing != null) resolvedSource = kindOf(backing);
        }

        if (resolvedTarget == null && resolvedSource == source) return direct;
        String finalTarget = resolvedTarget != null ? resolvedTarget.text : target;
        if (resolvedTarget != null && resolvedTarget.typedefOnly && !source.isLiteral()) return direct;
        Mismatch second = checkOnce(finalTarget, resolvedSource);
        if (second == null) return null;
        // Report with underlying type names when resolution helped describe the problem.
        if (resolvedTarget != null || resolvedSource != source)
        {
            return second;
        }
        return direct;
    }

    /**
     * Pure assignability check without alias resolution.
     */
    static @Nullable Mismatch checkOnce(@NotNull String targetText, @NotNull InferredType source)
    {
        String target = normalize(targetText);
        String base = stripOptional(target);
        String targetName = shortName(base);
        if (namesEqual(base, source.getName())) return null;
        // `typeid` never converts implicitly, in any direction: c3c demands
        // an explicit cast (and rejects most of those too). Plain values
        // still flow into `typeid?` through the Optional rule below.
        if (!isOptionalName(target) && (isTypeidName(source.getName()) || isTypeidName(base)))
        {
            return new Mismatch(source.getName(), targetName, null, null, -1, -1);
        }
        // `any` accepts any value (but void is not a value).
        if (base.equals("any") && source.getKind() != InferredType.Kind.VOID) return null;

        // Optional handling: `T?` holds either a `T` result or a fault.
        boolean targetOptional = isOptionalName(target);
        boolean sourceOptional = isOptionalName(source.getName());
        if (targetOptional && sourceOptional)
        {
            // `T?` accepts `U?` when the unwrapped result types are compatible.
            Mismatch inner = checkOnce(stripOptional(target), kindOf(stripOptional(source.getName())));
            if (inner == null) return null;
            return new Mismatch(source.getName(), targetName, null, null, -1, -1);
        }
        if (targetOptional)
        {
            // A plain value converts into the Optional's result type.
            return checkOnce(base, source);
        }
        if (sourceOptional)
        {
            // An Optional never converts to a plain type implicitly: use `!`
            // (rethrow), `!!` (force unwrap) or `?? default` to unwrap it.
            return new Mismatch(source.getName(), targetName, null, null, -1, -1);
        }

        // `void*` is a wildcard matching any pointer-like source: pointers,
        // array pointers and slices (which convert to pointers), strings,
        // `null` and `any`. A fixed array value does not decay into it.
        if (base.equals("void*"))
        {
            if (isVoidPointerCompatible(source)) return null;
            return new Mismatch(source.getName(), targetName, null, null, -1, -1);
        }

        if (source.getKind() == InferredType.Kind.INIT_LIST)
        {
            // Unreachable via check(); deny in the pure path.
            return new Mismatch(source.getName(), targetName, null, null, -1, -1);
        }

        if (arrayPointerCompatible(base, source)) return null;

        VectorInfo targetVector = parseVector(base);
        if (targetVector != null)
        {
            if (isVectorName(source.getName()))
            {
                return namesEqual(base, source.getName())
                    ? null
                    : new Mismatch(source.getName(), targetName, null, null, -1, -1);
            }
            // A scalar widens elementwise into the vector.
            Mismatch element = checkOnce(targetVector.element, source);
            if (element == null) return null;
            return new Mismatch(element.sourceName, targetName, element.intValue, element.floatValue, -1, -1);
        }

        switch (source.getKind())
        {
            case VOID:
            case INIT_LIST:
                break;
            case NULL:
                if (target.endsWith("*") || target.endsWith("?") || target.endsWith("!")) return null;
                break;
            case BOOL:
                break;
            case STRING:
                if (isStringTarget(base, source)) return null;
                break;
            case CHAR:
            case INT:
                if (FLOAT_TYPES.containsKey(shortName(base))) return null;
                if (intAssignable(base, source)) return null;
                if (source.isLiteral() && source.getIntValue() != null && intWidth(shortName(base)) >= 0)
                {
                    return new Mismatch(source.getName(), targetName, source.getIntValue(), null, -1, -1);
                }
                break;
            case FLOAT:
                if (floatAssignable(base, source)) return null;
                if (source.isLiteral() && source.getFloatValue() != null && FLOAT_TYPES.get(shortName(base)) != null)
                {
                    return new Mismatch(source.getName(), targetName, null, source.getFloatValue(), -1, -1);
                }
                break;
            case POINTER:
                if (isVoidPointerName(source.getName()) || isVoidPointerName(base)) return null;
                break;
            case NAMED:
                if (base.equals("any") || source.getName().equals("any")) return null;
                // A resolved `void*` member (e.g. `any.ptr`) may arrive as a
                // qualified NAMED type: it still converts to any pointer.
                if (isVoidPointerName(source.getName()) && isPlainPointerName(base)) return null;
                break;
        }
        return new Mismatch(source.getName(), targetName, null, null, -1, -1);
    }

    /**
     * Whether the (possibly module-qualified) type name denotes {@code void*}.
     */
    private static boolean isVoidPointerName(@NotNull String typeName)
    {
        String clean = normalize(typeName);
        return clean.equals("void*") || clean.endsWith("::void*");
    }

    /**
     * Whether the type name denotes {@code typeid} (never module-qualified,
     * but normalized defensively like the other builtins).
     */
    private static boolean isTypeidName(@NotNull String typeName)
    {
        return shortName(normalize(typeName)).equals("typeid");
    }

    /**
     * Explicit casts involving {@code typeid}, mirroring {@code c3c} (verified
     * by probing: the reverse direction and {@code any}/float/String targets
     * are all rejected, sub-word integers need a lossy chain).
     */
    private static @Nullable CastDiagnostic typeidCast(
            @NotNull String resolvedTarget,
            @NotNull String resolvedSource,
            @NotNull String sourceName,
            @NotNull String target)
    {
        if (isTypeidName(resolvedSource))
        {
            if (isPointerName(resolvedTarget)) return null;
            if (resolvedTarget.equals("bool")) return null;
            if (isPointerSizedIntName(resolvedTarget)) return null;
            String shortTarget = shortName(target);
            if (isIntegerName(resolvedTarget))
            {
                return CastDiagnostic.error("Casting 'typeid' to '" + shortTarget
                    + "' is not allowed because '" + shortTarget
                    + "' is smaller than a pointer. Use (" + shortTarget + ")(iptr) if you want this lossy cast.");
            }
            return CastDiagnostic.error("You cannot cast 'typeid' to '" + shortTarget + "'.");
        }
        return CastDiagnostic.error("You cannot cast '" + shortName(sourceName) + "' to 'typeid'.");
    }

    private static final class Mismatch
    {
        final @NotNull String sourceName;
        final @NotNull String targetName;
        final @Nullable BigInteger intValue;
        final @Nullable Double floatValue;
        final long count;
        final long actual;

        Mismatch(@NotNull String sourceName, @NotNull String targetName, @Nullable BigInteger intValue, @Nullable Double floatValue, long count, long actual)
        {
            this.sourceName = sourceName;
            this.targetName = targetName;
            this.intValue = intValue;
            this.floatValue = floatValue;
            this.count = count;
            this.actual = actual;
        }
    }

    // ------------------------------------------------------------------
    // Aliases and typedefs
    // ------------------------------------------------------------------

    private static final class TargetInfo
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

    private static @Nullable TargetInfo resolveTargetType(
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
        if (isOptionalName(target))
        {
            String inner = stripOptional(normalize(target));
            String suffix = normalize(target).endsWith("!") ? "!" : "?";
            String innerAlias = resolveAlias(inner, project, contextModule, 0);
            if (innerAlias != null) return new TargetInfo(innerAlias + suffix, false);
            String innerTypedef = resolveTypedef(inner, project, contextModule, 0);
            if (innerTypedef != null) return new TargetInfo(innerTypedef + suffix, true);
        }
        return null;
    }

    private static @Nullable String resolveSourceType(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String sourceName)
    {
        String transparent = transparentUnderlying(sourceName, project, contextModule);
        if (transparent != null) return transparent;
        // Same re-wrap for Optional-wrapped sources (`Alias?` -> `double?`).
        if (isOptionalName(sourceName))
        {
            String inner = stripOptional(normalize(sourceName));
            String suffix = normalize(sourceName).endsWith("!") ? "!" : "?";
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
            if (namesEqual(next, current)) return depth == 0 ? null : current;
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
        String simpleName = shortName(normalize(typeName));
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
            findNamedTypeDecls(shortName(normalize(typeName)), project), typeName, contextModule);
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
            findNamedTypeDecls(shortName(normalize(typeName)), project), typeName, contextModule);
        if (match == null || !match.isTypedef || !match.inlineTypedef) return null;
        String chained = resolveInlineTypedef(match.underlying, project, contextModule, depth + 1);
        return chained != null ? chained : match.underlying;
    }

    private static boolean isUserTypeName(@NotNull String typeName)
    {
        String clean = normalize(typeName);
        if (clean.contains("{") || clean.contains("}") || clean.contains("[")
            || clean.contains("]") || clean.contains("*") || clean.contains("?")
            || clean.contains("!") || clean.contains("(") || clean.contains(" ")) return false;
        String simpleName = shortName(clean);
        if (INT_TYPES.containsKey(simpleName) || FLOAT_TYPES.containsKey(simpleName)) return false;
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
        String clean = normalize(typeName);
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
            VectorInfo array = parseArray(clean);
            VectorInfo vector = array == null ? parseVector(clean) : null;
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
        String full = normalize(requestedText);
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
        for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
        {
            if (!key.equals(shortName) && !key.endsWith("::" + shortName)) continue;
            for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
            {
                if (!(element instanceof C3TypeName typeName)) continue;
                PsiElement parent = typeName.getParent();
                boolean isTypedef = parent instanceof C3TypedefDecl;
                if (!(parent instanceof C3AliasTypeDecl) && !isTypedef) continue;
                if (!typeName.getText().strip().equals(shortName)) continue;
                String underlying = underlyingTypeText(parent);
                if (underlying == null) continue;
                boolean inline = isTypedef && hasInlineModifier(parent);
                ModuleName module = ModuleName.from(typeName);
                result.add(new NamedTypeDecl(typeName, underlying, isTypedef, inline, module));
                if (result.size() > 25) return result;
            }
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
        return evaluateTypefromInner(normalize(arg.getText()));
    }

    /**
     * Text form of the above, for the post-keyword parse where
     * {@code $typefrom(...)} is a type node rather than a call.
     */
    private static @Nullable String evaluateComptimeAliasText(@NotNull String text)
    {
        String clean = normalize(text);
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

    /**
     * Leniency for aliases with a compile-time right-hand side the evaluator
     * could not resolve (e.g. the {@code CChar} ternary): when one side is
     * such an alias and the other side is numeric, allow the conversion. A
     * missed real error is preferable to blocking compilable code here.
     */
    private static boolean isComptimeNumericLenient(
            @NotNull String target,
            @NotNull InferredType source,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String cleanTarget = normalize(target);
        String cleanSource = normalize(source.getName());
        boolean targetOpaque = isUnresolvedComptimeAlias(cleanTarget, project, contextModule);
        boolean sourceOpaque = isUnresolvedComptimeAlias(cleanSource, project, contextModule);
        if (!targetOpaque && !sourceOpaque) return false;
        boolean targetNumeric = isIntegerName(cleanTarget) || isFloatType(cleanTarget);
        boolean sourceNumeric = isNumericKind(source)
            || isIntegerName(cleanSource) || isFloatType(cleanSource);
        return (targetOpaque && sourceNumeric) || (sourceOpaque && targetNumeric);
    }

    private static boolean isUnresolvedComptimeAlias(
            @NotNull String name,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        if (!isUserTypeName(name)) return false;
        if (resolveAlias(name, project, contextModule, 0) != null) return false;
        return hasComptimeAliasRhs(shortName(normalize(name)), project);
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
    private static @Nullable String inlineConstdefBacking(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String clean = normalize(typeName);
        if (!isUserTypeName(clean) || DumbService.isDumb(project)) return null;
        String wanted = shortName(clean);
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

    private static boolean isConstdefName(@NotNull String typeName, @NotNull Project project)
    {
        String clean = normalize(typeName);
        if (!isUserTypeName(clean) || DumbService.isDumb(project)) return false;
        String wanted = shortName(clean);
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
     * Array/slice/pointer conversions from {@code docs/arrays.md} (same element type required):
     * <ul>
     * <li>{@code T[]} accepts {@code T[]} and {@code T[N]*}</li>
     * <li>{@code T*} accepts {@code T[]}, {@code T[N]*} and {@code T*}</li>
     * <li>{@code T[N]*} accepts {@code T[M]*} with equal size</li>
     * </ul>
     * Fixed arrays never convert implicitly; anything else needs an explicit cast.
     */
    /**
     * Whether a value converts to the {@code void*} wildcard: any pointer
     * (plain or array pointer), any slice, strings, {@code null} and
     * {@code any}. Anything else, including fixed array values (which need
     * an explicit {@code &}), does not.
     */
    private static boolean isVoidPointerCompatible(@NotNull InferredType source)
    {
        switch (source.getKind())
        {
            case POINTER:
            case NULL:
            case STRING:
                return true;
            case BOOL:
            case CHAR:
            case INT:
            case FLOAT:
            case VOID:
            case INIT_LIST:
                return false;
            case NAMED:
                break;
        }
        String name = normalize(source.getName());
        if (name.equals("any") || name.equals("void*")) return true;
        if (name.endsWith("[]")) return true;
        return parseArrayPointer(name) != null;
    }

    private static boolean arrayPointerCompatible(@NotNull String base, @NotNull InferredType source)
    {
        String sourceName = source.getName();
        if (isVectorName(base) || isVectorName(sourceName)) return false;

        boolean targetSlice = isSliceName(base);
        boolean targetPtr = isPlainPointerName(base);
        VectorInfo targetArrayPtr = parseArrayPointer(base);
        if (!targetSlice && !targetPtr && targetArrayPtr == null) return false;

        boolean sourceSlice = isSliceName(sourceName);
        boolean sourcePtr = isPlainPointerName(sourceName);
        VectorInfo sourceArrayPtr = parseArrayPointer(sourceName);
        if (!sourceSlice && !sourcePtr && sourceArrayPtr == null) return false;

        String targetElement = targetSlice || targetPtr ? sliceOrPointerElement(base) : targetArrayPtr.element;
        String sourceElement = sourceSlice || sourcePtr ? sliceOrPointerElement(sourceName) : sourceArrayPtr.element;
        if (targetElement == null || sourceElement == null || !namesEqual(targetElement, sourceElement)) return false;

        if (targetSlice) return true;
        if (targetPtr) return true;
        // T[N]* <- T[M]* requires equal sizes (textual or numeric);
        // slices and plain pointers need an explicit cast here.
        if (sourceArrayPtr == null) return false;
        return targetArrayPtr.sizeText.equals(sourceArrayPtr.sizeText)
            || (targetArrayPtr.size >= 0 && targetArrayPtr.size == sourceArrayPtr.size);
    }

    private static boolean isSliceName(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        return clean.endsWith("[]");
    }

    private static boolean isPlainPointerName(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        return clean.endsWith("*") && !clean.endsWith("**") && parseArrayPointer(clean) == null;
    }

    private static @Nullable String sliceOrPointerElement(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        if (clean.endsWith("[]")) return clean.substring(0, clean.length() - 2);
        if (clean.endsWith("*") && !clean.endsWith("**"))
        {
            String element = clean.substring(0, clean.length() - 1).strip();
            return element.isEmpty() ? null : element;
        }
        return null;
    }

    private static @Nullable VectorInfo parseArrayPointer(@NotNull String typeText)
    {
        // An array pointer `T[N]*`: array with a trailing star (but not a plain `T*`).
        // The size may be symbolic (`T[BUF_SIZE]*`); only the unbounded `T[*]`
        // is not an array pointer.
        String clean = normalize(typeText);
        if (!clean.endsWith("*") || clean.endsWith("**")) return null;
        VectorInfo array = parseArray(clean.substring(0, clean.length() - 1));
        if (array == null || array.size == -1) return null;
        return array;
    }

    // ------------------------------------------------------------------
    // Vectors, arrays and initializer lists
    // ------------------------------------------------------------------

    // Array/vector sizes may be symbolic constants (`uint[BUF_SIZE]`), not
    // just literals: anything up to the closing bracket is a size, classified
    // by parseSize (numeric, `*`/empty, or symbolic).
    private static final java.util.regex.Pattern VECTOR_PATTERN =
        java.util.regex.Pattern.compile("^(.+)\\[<([^\\]]*)>\\]$");
    private static final java.util.regex.Pattern ARRAY_PATTERN =
        java.util.regex.Pattern.compile("^(.+)\\[([^\\]]*)\\]$");

    public static final class VectorInfo
    {
        public final @NotNull String element;
        public final long size;
        public final @NotNull String sizeText;

        VectorInfo(@NotNull String element, long size, @NotNull String sizeText)
        {
            this.element = element;
            this.size = size;
            this.sizeText = sizeText;
        }
    }

    private static long parseSize(@NotNull String sizeText)
    {
        if (sizeText.isEmpty() || sizeText.equals("*")) return -1;
        try
        {
            long size = Long.parseLong(sizeText);
            return size < 0 ? -1 : size;
        }
        catch (NumberFormatException e)
        {
            return -2;
        }
    }

    /**
     * Whether two array/vector types have the same element type and equal
     * lengths, e.g. {@code uint[16]} and {@code uint[BLOCK_SIZE/uint.sizeof]}.
     * Lengths compare textually, numerically, or by evaluating constant
     * expressions (literals, {@code const} values, {@code T.sizeof},
     * {@code +-* / %} with parentheses).
     */
    static boolean arrayTypesEqualSize(
            @NotNull String targetText,
            @NotNull String sourceText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String target = stripOptional(normalize(targetText));
        String source = stripOptional(normalize(sourceText));
        VectorInfo targetVector = parseVector(target);
        VectorInfo sourceVector = parseVector(source);
        if (targetVector != null || sourceVector != null)
        {
            if (targetVector == null || sourceVector == null) return false;
            if (!namesEqual(targetVector.element, sourceVector.element)) return false;
            return arraySizesEqual(targetVector.sizeText, sourceVector.sizeText, project, contextModule);
        }
        VectorInfo targetArray = parseArray(target);
        VectorInfo sourceArray = parseArray(source);
        if (targetArray == null || sourceArray == null) return false;
        if (!namesEqual(targetArray.element, sourceArray.element)) return false;
        return arraySizesEqual(targetArray.sizeText, sourceArray.sizeText, project, contextModule);
    }

    static boolean arraySizesEqual(
            @NotNull String first,
            @NotNull String second,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String cleanFirst = first.strip();
        String cleanSecond = second.strip();
        if (cleanFirst.equals(cleanSecond)) return true;
        Long valueFirst = evalSize(cleanFirst, project, contextModule, 0);
        if (valueFirst == null) return false;
        Long valueSecond = evalSize(cleanSecond, project, contextModule, 0);
        return valueSecond != null && valueFirst.equals(valueSecond);
    }

    /**
     * Evaluates a constant size expression to a number: integer literals
     * (decimal/hex/binary with underscores), {@code const} values by name,
     * {@code T.sizeof} for primitives and pointers, and {@code +-* / %}
     * arithmetic with parentheses. Anything else (unknown names, method
     * calls, overflow) yields {@code null}. Depth-bounded and dumb-safe.
     */
    private static @Nullable Long evalSize(
            @NotNull String text,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth)
    {
        if (depth > 4 || DumbService.isDumb(project)) return null;
        String clean = text.strip();
        if (clean.isEmpty() || clean.equals("*")) return null;
        // Parenthesized group.
        if (clean.startsWith("(") && matchingParen(clean, 0) == clean.length() - 1)
        {
            return evalSize(clean.substring(1, clean.length() - 1), project, contextModule, depth + 1);
        }
        // Lowest precedence first: `+` and binary `-`.
        int split = splitBinaryOp(clean, true);
        if (split >= 0)
        {
            Long left = evalSize(clean.substring(0, split), project, contextModule, depth + 1);
            Long right = evalSize(clean.substring(split + 1), project, contextModule, depth + 1);
            if (left == null || right == null) return null;
            try
            {
                return clean.charAt(split) == '+' ? Math.addExact(left, right) : Math.subtractExact(left, right);
            }
            catch (ArithmeticException e)
            {
                return null;
            }
        }
        split = splitBinaryOp(clean, false);
        if (split >= 0)
        {
            Long left = evalSize(clean.substring(0, split), project, contextModule, depth + 1);
            Long right = evalSize(clean.substring(split + 1), project, contextModule, depth + 1);
            if (left == null || right == null) return null;
            try
            {
                return switch (clean.charAt(split))
                {
                    case '*' -> Math.multiplyExact(left, right);
                    case '/' -> right == 0 ? null : left / right;
                    case '%' -> right == 0 ? null : left % right;
                    default -> null;
                };
            }
            catch (ArithmeticException e)
            {
                return null;
            }
        }
        if (clean.startsWith("-"))
        {
            Long inner = evalSize(clean.substring(1), project, contextModule, depth + 1);
            return inner == null ? null : -inner;
        }
        Long literal = parseSizeLiteral(clean);
        if (literal != null) return literal;
        if (clean.endsWith(".sizeof"))
        {
            return primitiveSizeof(clean.substring(0, clean.length() - 7).strip());
        }
        if (clean.matches("[A-Za-z_][A-Za-z_0-9.:]*"))
        {
            return constValue(clean, project, contextModule, depth);
        }
        return null;
    }

    private static int matchingParen(@NotNull String text, int open)
    {
        int depth = 0;
        for (int i = open; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '(') depth++;
            else if (c == ')')
            {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Rightmost binary operator of the given precedence group at paren depth
     * zero, or -1. A `-` directly after another operator or `(` is unary and
     * skipped; `*`/`/`/`%` are never unary here.
     */
    private static int splitBinaryOp(@NotNull String text, boolean additive)
    {
        int depth = 0;
        for (int i = text.length() - 1; i >= 0; i--)
        {
            char c = text.charAt(i);
            if (c == ')') depth++;
            else if (c == '(') depth--;
            else if (depth == 0 && (additive ? (c == '+' || c == '-') : (c == '*' || c == '/' || c == '%')))
            {
                if (c == '-' && (i == 0 || "+-*/%(".indexOf(text.charAt(i - 1)) >= 0)) continue;
                if (i == 0 || i == text.length() - 1) continue;
                return i;
            }
        }
        return -1;
    }

    private static @Nullable Long parseSizeLiteral(@NotNull String text)
    {
        String clean = text.strip().replace("_", "");
        try
        {
            if (clean.startsWith("0x") || clean.startsWith("0X")) return Long.parseLong(clean.substring(2), 16);
            if (clean.startsWith("0b") || clean.startsWith("0B")) return Long.parseLong(clean.substring(2), 2);
            if (clean.startsWith("0o") || clean.startsWith("0O")) return Long.parseLong(clean.substring(2), 8);
            if (clean.matches("0[0-7]+")) return Long.parseLong(clean.substring(1), 8);
            if (!clean.matches("[0-9]+")) return null;
            return Long.parseLong(clean);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Size of a primitive type in bytes ({@code uint.sizeof} is 4), or
     * {@code null} when not statically known here (structs, aliases, ...).
     */
    private static @Nullable Long primitiveSizeof(@NotNull String typeName)
    {
        String shortName = shortName(normalize(typeName));
        if (shortName.endsWith("*")) return 8L;
        if (shortName.equals("char") || shortName.equals("ichar") || shortName.equals("bool")) return 1L;
        int[] intBits = INT_TYPES.get(shortName);
        if (intBits != null) return (long) (intBits[0] / 8);
        Integer floatBits = FLOAT_TYPES.get(shortName);
        if (floatBits != null) return (long) (floatBits / 8);
        return null;
    }

    /**
     * Integer value of a {@code const} by (possibly qualified) name,
     * recursively evaluated. Same-module declarations win over other
     * modules on name clashes.
     */
    private static @Nullable Long constValue(
            @NotNull String name,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth)
    {
        String wanted = shortName(normalize(name));
        boolean qualified = normalize(name).contains("::");
        C3ConstDeclarationStmt best = null;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
            {
                if (qualified)
                {
                    if (!key.equals(normalize(name))) continue;
                }
                else if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
                for (C3PsiElement element : safeElements(NameIndex.KEY, key, project))
                {
                    if (!(element instanceof C3ConstDeclarationStmt constDecl)) continue;
                    if (best == null) best = constDecl;
                    if (contextModule != null && contextModule.equals(ModuleName.from(constDecl))) best = constDecl;
                }
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
        if (best == null) return null;
        C3Expr init;
        try
        {
            init = best.getExpr();
        }
        catch (Exception e)
        {
            return null;
        }
        if (init == null) return null;
        return evalSize(init.getText(), project, contextModule, depth + 1);
    }

    public static @Nullable VectorInfo parseVector(@NotNull String typeText)
    {
        java.util.regex.Matcher matcher = VECTOR_PATTERN.matcher(normalize(typeText));
        if (!matcher.matches()) return null;
        String element = matcher.group(1);
        if (element.isEmpty()) return null;
        // Symbolic sizes (`Real[<N>]`) stay unknown (size -2): callers compare
        // numerically when known, textually otherwise.
        return new VectorInfo(element, parseSize(matcher.group(2)), matcher.group(2));
    }

    public static boolean isIntegerType(@NotNull String typeText)
    {
        return INT_TYPES.containsKey(shortName(normalize(typeText)));
    }

    public static boolean isFloatType(@NotNull String typeText)
    {
        return FLOAT_TYPES.containsKey(shortName(normalize(typeText)));
    }

    /**
     * Element type of an array or slice ({@code int} for {@code int[4]},
     * {@code int[*]} and {@code int[]}), or {@code null}.
     */
    public static @Nullable String arrayElementType(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        if (clean.endsWith("[]")) return clean.substring(0, clean.length() - 2);
        VectorInfo array = parseArray(clean);
        return array != null ? array.element : null;
    }

    public static boolean isSliceType(@NotNull String typeText)
    {
        String clean = normalize(typeText);
        return clean.endsWith("[]");
    }

    private static boolean isVectorName(@NotNull String typeText)
    {
        return VECTOR_PATTERN.matcher(normalize(typeText)).matches();
    }

    public static @Nullable VectorInfo parseArray(@NotNull String typeText)
    {
        java.util.regex.Matcher matcher = ARRAY_PATTERN.matcher(normalize(typeText));
        if (!matcher.matches()) return null;
        String element = matcher.group(1);
        if (element.isEmpty()) return null;
        String sizeText = matcher.group(2);
        // Vector syntax (`Real[<4>]`) also matches the brackets: it is not an
        // array, and must not be treated as one (e.g. by parseArrayPointer).
        if (sizeText.startsWith("<") && sizeText.endsWith(">")) return null;
        return new VectorInfo(element, parseSize(sizeText), sizeText);
    }

    private static @Nullable Mismatch checkInitList(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull InferredType source)
    {
        String base = stripOptional(normalize(targetText));
        String targetName = shortName(base);
        VectorInfo vector = parseVector(base);
        VectorInfo array = vector == null ? parseArray(base) : null;
        if (vector == null && array == null) return null;
        String element = vector != null ? vector.element : array.element;
        long expected = vector != null ? vector.size : array.size;
        String sizeText = vector != null ? vector.sizeText : array.sizeText;
        if (expected < 0 && !sizeText.isEmpty() && !sizeText.equals("*"))
        {
            // Symbolic size (`uint[BUF_SIZE]`): verify the count when the
            // expression evaluates, stay lenient otherwise.
            Long evaluated = evalSize(sizeText, project, contextModule, 0);
            if (evaluated != null) expected = evaluated;
        }

        List<InferredType> elements = source.getElements();
        if (!source.hasNamedArguments() && vector != null && expected >= 0 && elements.size() != expected)
        {
            return new Mismatch(source.getName(), targetName, null, null, expected, elements.size());
        }
        if (source.hasNamedArguments()) return null;
        for (InferredType elementType : elements)
        {
            if (elementType == null) continue;
            Mismatch elementMismatch = check(project, contextModule, element, elementType);
            if (elementMismatch != null)
            {
                return new Mismatch(
                    elementMismatch.sourceName,
                    targetName,
                    elementMismatch.intValue,
                    elementMismatch.floatValue,
                    -1, -1);
            }
        }
        return null;
    }

    private static @NotNull String stripOptional(@NotNull String target)
    {
        if ((target.endsWith("?") || target.endsWith("!")) && !target.endsWith("*")) return target.substring(0, target.length() - 1);
        return target;
    }

    /**
     * Whether {@code ptr += n} / {@code ptr -= n} is valid pointer arithmetic:
     * a plain (non-void welcome) pointer target with an integer offset. The
     * compiler steps by the element size ({@code void*} steps bytes).
     */
    private static boolean isPointerArithmetic(@NotNull String targetText, @NotNull InferredType source)
    {
        String base = stripOptional(normalize(targetText));
        if (!isPlainPointerName(base)) return false;
        if (source.getKind() == InferredType.Kind.INT || source.getKind() == InferredType.Kind.CHAR) return true;
        String shortSource = shortName(stripOptional(normalize(source.getName())));
        return isIntegerName(shortSource)
            || shortSource.equals("usz") || shortSource.equals("isz")
            || shortSource.equals("uptr") || shortSource.equals("iptr");
    }

    /**
     * Unwraps an Optional-typed use when a preceding guard in the same flow
     * already excluded the fault case: {@code if (catch err = x)} separates
     * the fault branch, and {@code if (!x)} with a diverging body (return /
     * continue / break) excludes the falsy case. Mirrors the compiler's
     * flow-sensitive narrowing; anything unrecognized stays wrapped.
     * Pure PSI walk, no index access.
     */
    static @NotNull InferredType narrowedSource(@Nullable C3Expr useSite, @NotNull InferredType source)
    {
        if (useSite == null || !isOptionalName(source.getName())) return source;
        if (!(useSite instanceof C3PathIdentExpr pathExpr) || pathExpr.getPathIdent().getPath() != null) return source;
        String name = pathExpr.getPathIdent().getNameIdent();
        if (name == null || name.isEmpty()) return source;
        try
        {
            if (isNarrowedUse(useSite, name)) return kindOf(stripOptional(normalize(source.getName())));
        }
        catch (Exception ignored)
        {
        }
        return source;
    }

    private enum IfBranch
    {
        THEN, ELSE, COND
    }

    private static boolean isNarrowedUse(@NotNull C3Expr useSite, @NotNull String name)
    {
        int useOffset = useSite.getTextOffset();
        PsiElement child = useSite;
        PsiElement parent = useSite.getParent();
        int depth = 0;
        while (parent != null && depth < 14)
        {
            if (parent instanceof C3IfStmt ifStmt)
            {
                IfBranch branch = branchOf(ifStmt, child);
                Boolean decided = narrowingAtIf(ifStmt, branch, useSite, name);
                if (decided != null) return decided;
            }
            else if (parent instanceof C3CompoundStatement compound)
            {
                Boolean guarded = compoundPrecedesWithGuard(compound, useSite, useOffset, name);
                if (guarded != null) return guarded;
            }
            child = parent;
            parent = parent.getParent();
            depth++;
        }
        return false;
    }

    /**
     * Which part of the {@code if} holds the child on the path from the use:
     * the then-branch, the else-branch, the condition itself, or none of
     * them ({@code null}, e.g. the label) when the use sits outside.
     */
    private static @Nullable IfBranch branchOf(@NotNull C3IfStmt ifStmt, @NotNull PsiElement child)
    {
        if (child == ifStmt.getCompoundStatement() || child == ifStmt.getStatement()) return IfBranch.THEN;
        C3ElsePart elsePart = ifStmt.getElsePart();
        if (elsePart != null && (elsePart == child || PsiTreeUtil.isAncestor(elsePart, child, false)))
        {
            return IfBranch.ELSE;
        }
        if (ifStmt.getParenCond() != null && PsiTreeUtil.isAncestor(ifStmt.getParenCond(), child, false))
        {
            return IfBranch.COND;
        }
        return null;
    }

    /**
     * Narrowing verdict at one {@code if}: {@code TRUE}/{@code FALSE} when
     * the branch position decides it, {@code null} to keep looking outward.
     */
    private static @Nullable Boolean narrowingAtIf(
            @NotNull C3IfStmt ifStmt,
            @Nullable IfBranch branch,
            @NotNull C3Expr useSite,
            @NotNull String name)
    {
        C3ParenCond paren = ifStmt.getParenCond();
        C3Cond cond = paren != null ? paren.getCond() : null;
        if (cond == null) return null;
        if (branch == null)
        {
            // The use follows the statement: only an exhaustive `catch`
            // narrows (`try` never narrows what follows it).
            return catchNarrowsAfter(ifStmt, cond, name) ? Boolean.TRUE : null;
        }
        if (branch == IfBranch.COND) return null;
        Boolean catchVerdict = catchBranchNarrowing(cond, branch, name);
        if (catchVerdict != null) return catchVerdict;
        return tryBranchNarrowing(cond, branch, name);
    }

    private static @Nullable Boolean compoundPrecedesWithGuard(
            @NotNull C3CompoundStatement compound,
            @NotNull C3Expr useSite,
            int useOffset,
            @NotNull String name)
    {
        for (C3StatementList statementList : compound.getStatementListList())
        {
            for (C3Statement stmt : statementList.getStatementList())
            {
                if (stmt.getTextOffset() >= useOffset) break;
                if (stmt.getIfStmt() == null) continue;
                Boolean verdict = narrowingAtIf(stmt.getIfStmt(), null, useSite, name);
                if (verdict != null) return verdict;
            }
        }
        return null;
    }

    /**
     * After-statement rule: `if (catch ... = x)` narrows later uses of
     * {@code x} exactly when the fault branch always leaves its scope.
     * The presence of an `else` does not matter (it only runs for values).
     */
    private static boolean catchNarrowsAfter(
            @NotNull C3IfStmt ifStmt, @NotNull C3Cond cond, @NotNull String name)
    {
        boolean tested = false;
        for (C3CatchUnwrap unwrap : PsiTreeUtil.findChildrenOfType(cond, C3CatchUnwrap.class))
        {
            if (unwrap.getCatchUnwrapList() == null) continue;
            for (C3Expr caught : unwrap.getCatchUnwrapList().getExprList())
            {
                if (name.equals(caught.getText().strip())) tested = true;
            }
        }
        if (!tested) return false;
        return branchAlwaysDiverges(ifStmt);
    }

    private static @Nullable Boolean catchBranchNarrowing(
            @NotNull C3Cond cond, @NotNull IfBranch branch, @NotNull String name)
    {
        boolean tested = false;
        for (C3CatchUnwrap unwrap : PsiTreeUtil.findChildrenOfType(cond, C3CatchUnwrap.class))
        {
            if (unwrap.getCatchUnwrapList() == null) continue;
            for (C3Expr caught : unwrap.getCatchUnwrapList().getExprList())
            {
                if (name.equals(caught.getText().strip())) tested = true;
            }
        }
        if (!tested) return null;
        // Inside the fault branch the value is faulty; inside `else` (or
        // past an exhaustive body) only values arrive.
        return branch == IfBranch.ELSE;
    }

    /**
     * Inside `if (try t = x)`, only the bound copy narrows; the tested
     * expression itself stays Optional. Without a binding
     * (`if (try x)`), the tested name narrows instead.
     */
    private static @Nullable Boolean tryBranchNarrowing(
            @NotNull C3Cond cond, @NotNull IfBranch branch, @NotNull String name)
    {
        boolean decided = false;
        boolean narrowed = false;
        for (C3TryUnwrapChain chain : PsiTreeUtil.findChildrenOfType(cond, C3TryUnwrapChain.class))
        {
            for (C3TryUnwrap unwrap : chain.getTryUnwrapList())
            {
                String binding = unwrapBindingName(unwrap);
                String tested = unwrap.getExpr() != null ? unwrap.getExpr().getText().strip() : "";
                if (binding != null && name.equals(binding))
                {
                    if (branch != IfBranch.THEN) return null;
                    decided = true;
                    narrowed = true;
                }
                else if (binding == null && name.equals(tested))
                {
                    if (branch != IfBranch.THEN) return null;
                    decided = true;
                    narrowed = true;
                }
                else if (binding != null && name.equals(tested))
                {
                    // Tested alongside a binding: still Optional inside.
                    return Boolean.FALSE;
                }
            }
        }
        return decided ? narrowed : null;
    }

    /**
     * Binding identifier of a `catch`/`try` unwrap (`err` in
     * `catch err = x`, `t` in `try t = x`), or {@code null} for the
     * binding-less forms. Delegates to the unwrap mixins; the inline scan
     * stays as a fallback for detached trees.
     */
    static @Nullable String unwrapBindingName(@NotNull C3PsiElement unwrap)
    {
        if (unwrap instanceof C3CatchUnwrapMixin catchMixin && catchMixin.getBindingName() != null)
        {
            return catchMixin.getBindingName();
        }
        if (unwrap instanceof C3TryUnwrapMixin tryMixin && tryMixin.getBindingName() != null)
        {
            return tryMixin.getBindingName();
        }
        ASTNode eq = null;
        for (ASTNode child : unwrap.getNode().getChildren(null))
        {
            if (child.getElementType() == C3Types.EQ)
            {
                eq = child;
                break;
            }
        }
        if (eq == null) return null;
        ASTNode current = eq.getTreePrev();
        while (current != null
            && (current.getPsi() instanceof PsiWhiteSpace || current.getPsi() instanceof PsiComment))
        {
            current = current.getTreePrev();
        }
        if (current != null && current.getElementType() == C3Types.IDENT) return current.getText();
        return null;
    }

    /**
     * Whether the `if` statement's then-branch always leaves its scope, so
     * code after the statement only runs for the complementary case.
     */
    private static boolean branchAlwaysDiverges(@NotNull C3IfStmt ifStmt)
    {
        if (ifStmt.getCompoundStatement() != null)
        {
            return blockAlwaysDiverges(
                ifStmt.getCompoundStatement(), collectInnerLabels(ifStmt.getCompoundStatement()));
        }
        if (ifStmt.getStatement() != null)
        {
            PsiElement parent = ifStmt.getStatement().getParent();
            Set<String> labels = parent != null ? collectInnerLabels(parent) : Set.of();
            return statementAlwaysDiverges(ifStmt.getStatement(), labels, 0, 0);
        }
        return false;
    }

    static boolean blockAlwaysDiverges(@Nullable C3CompoundStatement body, @NotNull Set<String> labels)
    {
        return blockAlwaysDiverges(body, labels, 0, 0);
    }

    private static boolean blockAlwaysDiverges(
            @Nullable C3CompoundStatement body,
            @NotNull Set<String> labels,
            int loopDepth,
            int switchDepth)
    {
        if (body == null) return false;
        for (C3StatementList statementList : body.getStatementListList())
        {
            if (statementListAlwaysDiverges(statementList, labels, loopDepth, switchDepth)) return true;
        }
        return false;
    }

    private static boolean statementListAlwaysDiverges(
            @Nullable C3StatementList list, @NotNull Set<String> labels, int loopDepth, int switchDepth)
    {
        if (list == null) return false;
        for (C3Statement statement : list.getStatementList())
        {
            if (statementAlwaysDiverges(statement, labels, loopDepth, switchDepth)) return true;
        }
        return false;
    }

    private static boolean statementAlwaysDiverges(
            @NotNull C3Statement statement,
            @NotNull Set<String> labels,
            int loopDepth,
            int switchDepth)
    {
        if (statement.getReturnStmt() != null) return true;
        if (statement.getBreakStmt() != null)
        {
            return jumpDiverges(statement.getBreakStmt(), labels, loopDepth, switchDepth, false);
        }
        if (statement.getContinueStmt() != null)
        {
            return jumpDiverges(statement.getContinueStmt(), labels, loopDepth, 0, true);
        }
        if (statement.getIfStmt() != null) return ifAlwaysDiverges(statement.getIfStmt(), labels, loopDepth, switchDepth);
        if (statement.getSwitchStmt() != null)
        {
            return switchAlwaysDiverges(statement.getSwitchStmt(), labels, loopDepth, switchDepth);
        }
        if (statement.getCompoundStatement() != null)
        {
            return blockAlwaysDiverges(statement.getCompoundStatement(), labels, loopDepth, switchDepth);
        }
        if (statement.getDoStmt() != null && statement.getDoStmt().getCompoundStatement() != null)
        {
            // `do` runs its body at least once, but `break`/`continue` inside
            // target the loop itself.
            return blockAlwaysDiverges(statement.getDoStmt().getCompoundStatement(), labels, loopDepth + 1, switchDepth);
        }
        if (statement.getCtIfStmt() != null) return ctIfAlwaysDiverges(statement.getCtIfStmt(), labels, loopDepth, switchDepth);
        if (statement.getExprStmt() != null && statement.getExprStmt().getExpr() != null)
        {
            return isNoreturnCall(statement.getExprStmt().getExpr());
        }
        return false;
    }

    /**
     * A `break`/`continue` leaves the analyzed scope unless it targets a
     * loop, switch or label inside it. Bare `continue` never targets a
     * switch, so only the loop depth matters for it.
     */
    private static boolean jumpDiverges(
            @NotNull PsiElement jump,
            @NotNull Set<String> labels,
            int loopDepth,
            int switchDepth,
            boolean isContinue)
    {
        String label = jumpLabel(jump);
        if (label == null)
        {
            return isContinue ? loopDepth == 0 : (loopDepth == 0 && switchDepth == 0);
        }
        return !labels.contains(label);
    }

    private static @Nullable String jumpLabel(@NotNull PsiElement jump)
    {
        String text = jump.getText();
        if (text == null) return null;
        String compact = text.replaceAll("\\s+", " ").strip();
        int space = compact.indexOf(' ');
        if (space < 0) return null;
        String label = compact.substring(space + 1).strip();
        if (label.endsWith(";")) label = label.substring(0, label.length() - 1).strip();
        return label.isEmpty() ? null : label;
    }

    private static boolean ifAlwaysDiverges(
            @NotNull C3IfStmt ifStmt, @NotNull Set<String> labels, int loopDepth, int switchDepth)
    {
        boolean thenDiverges;
        if (ifStmt.getCompoundStatement() != null)
        {
            thenDiverges = blockAlwaysDiverges(ifStmt.getCompoundStatement(), labels, loopDepth, switchDepth);
        }
        else if (ifStmt.getStatement() != null)
        {
            thenDiverges = statementAlwaysDiverges(ifStmt.getStatement(), labels, loopDepth, switchDepth);
        }
        else
        {
            return false;
        }
        if (!thenDiverges) return false;
        C3ElsePart elsePart = ifStmt.getElsePart();
        if (elsePart == null) return false;
        if (elsePart.getCompoundStatement() != null)
        {
            return blockAlwaysDiverges(elsePart.getCompoundStatement(), labels, loopDepth, switchDepth);
        }
        if (elsePart.getIfStmt() != null) return ifAlwaysDiverges(elsePart.getIfStmt(), labels, loopDepth, switchDepth);
        return false;
    }

    private static boolean switchAlwaysDiverges(
            @NotNull C3SwitchStmt switchStmt,
            @NotNull Set<String> labels,
            int loopDepth,
            int switchDepth)
    {
        C3SwitchBody body = switchStmt.getSwitchBody();
        if (body == null || body.getNode() == null) return false;
        List<PsiElement> branches = new ArrayList<>();
        for (ASTNode child : body.getNode().getChildren(null))
        {
            PsiElement psi = child.getPsi();
            if (psi instanceof C3CaseStmt || psi instanceof C3DefaultStmt) branches.add(psi);
        }
        if (branches.isEmpty()) return false;
        boolean sawDefault = false;
        Boolean[] diverging = new Boolean[branches.size()];
        for (int i = branches.size() - 1; i >= 0; i--)
        {
            PsiElement branch = branches.get(i);
            if (branch instanceof C3DefaultStmt) sawDefault = true;
            C3StatementList list = branch instanceof C3CaseStmt caseStmt
                ? caseStmt.getStatementList()
                : ((C3DefaultStmt) branch).getStatementList();
            if (list != null && statementListAlwaysDiverges(list, labels, loopDepth, switchDepth + 1))
            {
                diverging[i] = Boolean.TRUE;
            }
            else if (list == null || list.getStatementList().isEmpty() || endsWithNextcase(list))
            {
                // Empty (or explicitly forwarded) cases fall into the next one.
                diverging[i] = (i + 1 < diverging.length) ? diverging[i + 1] : Boolean.FALSE;
            }
            else
            {
                diverging[i] = Boolean.FALSE;
            }
        }
        if (!sawDefault) return false;
        for (Boolean branch : diverging)
        {
            if (!Boolean.TRUE.equals(branch)) return false;
        }
        return true;
    }

    private static boolean endsWithNextcase(@NotNull C3StatementList list)
    {
        List<C3Statement> statements = list.getStatementList();
        if (statements.isEmpty()) return false;
        try
        {
            return statements.get(statements.size() - 1).getNextcaseStmt() != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean ctIfAlwaysDiverges(
            @NotNull C3CtIfStmt ctIf, @NotNull Set<String> labels, int loopDepth, int switchDepth)
    {
        List<C3StatementList> lists;
        try
        {
            lists = ctIf.getStatementListList();
        }
        catch (Exception e)
        {
            return false;
        }
        // Compile-time conditions cannot be evaluated here: both present
        // branches must diverge.
        if (lists.size() < 2) return false;
        for (C3StatementList list : lists)
        {
            if (!statementListAlwaysDiverges(list, labels, loopDepth, switchDepth)) return false;
        }
        return true;
    }

    private static @NotNull Set<String> collectInnerLabels(@NotNull PsiElement root)
    {
        Set<String> labels = new HashSet<>();
        try
        {
            for (C3Label label : PsiTreeUtil.findChildrenOfType(root, C3Label.class))
            {
                String text = label.getText();
                if (text == null) continue;
                String name = text.contains(":") ? text.substring(0, text.indexOf(':')).strip() : text.strip();
                if (!name.isEmpty()) labels.add(name);
            }
        }
        catch (Exception ignored)
        {
        }
        return labels;
    }

    /**
     * Whether the expression is a call to a `@noreturn` function or macro
     * (which includes the builtin `unreachable()` when it resolves to one).
     */
    private static boolean isNoreturnCall(@Nullable C3Expr expr)
    {
        if (!(expr instanceof C3CallExpr call)) return false;
        C3CallablePsiElement callable;
        try
        {
            callable = CallChecker.resolveTarget(call);
        }
        catch (Exception e)
        {
            return false;
        }
        if (callable == null) return false;
        try
        {
            if (callable instanceof C3FuncDef funcDef)
            {
                return AttributeSpecs.hasAttribute(funcDef.getAttributes(), "noreturn");
            }
            if (callable instanceof C3MacroDefinition macro)
            {
                return AttributeSpecs.hasAttribute(macro.getAttributes(), "noreturn");
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    /**
     * Whether a type name carries the Optional suffix (`T?`, old syntax `T!`).
     */
    public static boolean isOptionalName(@NotNull String typeName)
    {
        String clean = normalize(typeName);
        return (clean.endsWith("?") || clean.endsWith("!")) && !clean.endsWith("*") && clean.length() > 1;
    }

    private static boolean isStringTarget(@NotNull String base, @NotNull InferredType source)
    {
        if (source.isLiteral())
        {
            if (base.equals("String") || base.equals("ZString")
                || base.equals("char[]") || base.equals("ichar[]")
                || base.equals("char*") || base.equals("ichar*")) return true;
            return base.matches("(i)?char\\[(\\d*|\\*)\\]");
        }
        String name = source.getName();
        String baseSlice = base.equals("char[*]") ? "char[]" : (base.equals("ichar[*]") ? "ichar[]" : base);
        String nameSlice = name.equals("char[*]") ? "char[]" : (name.equals("ichar[*]") ? "ichar[]" : name);
        return (nameSlice.equals("String") && (baseSlice.equals("String") || baseSlice.equals("char[]")))
            || (nameSlice.equals("char[]") && (baseSlice.equals("String") || baseSlice.equals("char[]")))
            || (nameSlice.equals("ZString") && (baseSlice.equals("ZString") || baseSlice.equals("char*")))
            || (nameSlice.equals("char*") && (baseSlice.equals("ZString") || baseSlice.equals("char*")));
    }

    private static boolean intAssignable(@NotNull String base, @NotNull InferredType source)
    {
        String shortBase = shortName(base);
        int targetWidth = intWidth(shortBase);
        if (targetWidth < 0) return false;
        if (source.isLiteral())
        {
            // Typed literal without a known value (e.g. a wide char literal):
            // only the exact type is accepted, checked by the caller.
            if (source.getIntValue() == null) return false;
            int[] targetBits = INT_TYPES.get(shortBase);
            if (targetBits != null) return fits(source.getIntValue(), targetBits);
            // `char` is an 8-bit unsigned integer.
            return shortBase.equals("char") && fitsUnsigned(source.getIntValue(), 8);
        }
        // Implicit widening: a value of a narrower (or equally wide) integer
        // type converts to the target, regardless of signedness. Narrowing a
        // wider integer type needs an explicit cast.
        int sourceWidth = intWidth(shortName(source.getName()));
        if (sourceWidth < 0) return false;
        return targetWidth >= sourceWidth;
    }

    private static boolean floatAssignable(@NotNull String base, @NotNull InferredType source)
    {
        Integer targetBits = FLOAT_TYPES.get(shortName(base));
        if (targetBits == null) return false;
        if (source.isLiteral())
        {
            Double value = source.getFloatValue();
            if (value == null) return true;
            if (value.isNaN() || value.isInfinite()) return false;
            double max = maxFloat(targetBits);
            return Math.abs(value) <= max;
        }
        Integer sourceBits = FLOAT_TYPES.get(shortName(source.getName()));
        if (sourceBits != null) return sourceBits <= targetBits;
        // Any integer type converts to any float implicitly (wide integers
        // may lose precision, which the compiler accepts).
        String shortSource = shortName(source.getName());
        return intWidth(shortSource) >= 0
            || shortSource.equals("usz") || shortSource.equals("isz")
            || shortSource.equals("uptr") || shortSource.equals("iptr");
    }

    /**
     * Bit width of an integer type name ({@code char} counts as unsigned 8),
     * or {@code -1} for non-integer names.
     */
    private static int intWidth(@NotNull String shortName)
    {
        int[] bits = INT_TYPES.get(shortName);
        if (bits != null) return bits[0];
        if (shortName.equals("char")) return 8;
        return -1;
    }

    private static boolean fitsUnsigned(@NotNull BigInteger value, int bits)
    {
        return value.signum() >= 0 && value.bitLength() <= bits;
    }

    private static boolean fits(@NotNull BigInteger value, @NotNull int[] bits)
    {
        return rangeFits(value, value, rangeMin(bits), rangeMax(bits));
    }

    private static boolean rangeFits(@NotNull BigInteger sMin, @NotNull BigInteger sMax, @NotNull BigInteger tMin, @NotNull BigInteger tMax)
    {
        return sMin.compareTo(tMin) >= 0 && sMax.compareTo(tMax) <= 0;
    }

    private static @NotNull BigInteger rangeMin(@NotNull int[] bits)
    {
        if (bits[1] == 1) return BigInteger.ONE.shiftLeft(bits[0] - 1).negate();
        return BigInteger.ZERO;
    }

    private static @NotNull BigInteger rangeMax(@NotNull int[] bits)
    {
        if (bits[1] == 1) return BigInteger.ONE.shiftLeft(bits[0] - 1).subtract(BigInteger.ONE);
        return BigInteger.ONE.shiftLeft(bits[0]).subtract(BigInteger.ONE);
    }

    private static double maxFloat(int bits)
    {
        if (bits <= 16) return 65504.0;
        if (bits <= 32) return Float.MAX_VALUE;
        if (bits <= 64) return Double.MAX_VALUE;
        return Double.POSITIVE_INFINITY;
    }

    // ------------------------------------------------------------------
    // Declared type mapping
    // ------------------------------------------------------------------

    /**
     * Maps a written type text to an inferred type (never a literal).
     * Primitive names may be module-qualified (e.g. a struct member type
     * resolved to {@code mod::uint}); they are shortened to the builtin name.
     */
    public static @NotNull InferredType kindOf(@NotNull String typeText)
    {
        String text = normalize(typeText);
        String shortText = shortName(text);
        if (shortText.equals("void")) return InferredType.voidType();
        if (shortText.equals("bool")) return InferredType.boolType(false);
        if (shortText.equals("char")) return InferredType.of(InferredType.Kind.CHAR, "char");
        if (INT_TYPES.containsKey(shortText)) return InferredType.of(InferredType.Kind.INT, shortText);
        if (FLOAT_TYPES.containsKey(shortText)) return InferredType.of(InferredType.Kind.FLOAT, shortText);
        if (shortText.equals("String") || shortText.equals("ZString")) return InferredType.of(InferredType.Kind.STRING, shortText);
        if (text.endsWith("*")) return InferredType.of(InferredType.Kind.POINTER, text);
        return InferredType.of(InferredType.Kind.NAMED, text);
    }

    // ------------------------------------------------------------------
    // Inference
    // ------------------------------------------------------------------

    public static @Nullable InferredType infer(@NotNull C3Expr expr)
    {
        return infer(expr, 0);
    }

    private static @Nullable InferredType infer(@NotNull C3Expr expr, int depth)
    {
        if (depth > MAX_DEPTH) return null;
        if (expr instanceof C3LiteralExpr) return inferLiteral(expr.getText());
        if (expr instanceof C3KeywordExpr) return inferKeyword(expr.getText());
        if (expr instanceof C3StringExpr) return inferString(expr.getText());
        if (expr instanceof C3GroupedExpr grouped) return grouped.getExpr() != null ? infer(grouped.getExpr(), depth + 1) : null;
        if (expr instanceof C3UnaryExpr unary) return inferUnary(unary, depth);
        if (expr instanceof C3BinaryExpr binary) return inferBinary(binary, depth);
        if (expr instanceof C3TernaryExpr ternary) return inferTernary(ternary, depth);
        if (expr instanceof C3PathIdentExpr pathIdent) return inferPathIdent(pathIdent, depth);
        if (expr instanceof C3PathConstExpr pathConst) return inferPathConst(pathConst, depth);
        if (expr instanceof C3CallExpr call) return inferCall(call, depth);
        if (expr instanceof C3EnumAccessExpr enumAccess) return inferEnumAccess(enumAccess);
        if (expr instanceof C3CompoundInitExpr compoundInit) return kindOf(compoundInit.getType().getText());
        if (expr instanceof C3InitListExpr initList) return inferInitList(initList, depth);
        return null;
    }

    private static @Nullable InferredType inferInitList(@NotNull C3InitListExpr initList, int depth)
    {
        C3InitializerList list = initList.getInitializerList();
        if (list == null || list.getArgList() == null) return InferredType.initList(List.of(), false);
        List<InferredType> elements = new ArrayList<>();
        boolean named = false;
        for (C3Arg arg : list.getArgList().getArgList())
        {
            if (arg.getNamedIdent() != null || arg.getParamPath() != null) named = true;
            C3Expr argExpr = arg.getExpr();
            elements.add(argExpr != null ? infer(argExpr, depth + 1) : null);
        }
        return InferredType.initList(elements, named);
    }

    private static @Nullable InferredType inferLiteral(@NotNull String text)
    {
        String clean = text.replace("_", "");
        if (clean.isEmpty()) return null;
        char first = clean.charAt(0);
        if (first == '.' || (Character.isDigit(first) && (clean.contains(".") || clean.contains("e") || clean.contains("E") || clean.contains("p") || clean.contains("P"))))
        {
            return parseFloatLiteral(clean);
        }
        if (Character.isDigit(first)) return parseIntLiteral(clean);
        return null;
    }

    private static @Nullable InferredType parseIntLiteral(@NotNull String text)
    {
        int radix = 10;
        String digits = text;
        if (text.startsWith("0x") || text.startsWith("0X"))
        {
            radix = 16;
            digits = text.substring(2);
        }
        else if (text.startsWith("0o") || text.startsWith("0O"))
        {
            radix = 8;
            digits = text.substring(2);
        }
        else if (text.startsWith("0b") || text.startsWith("0B"))
        {
            radix = 2;
            digits = text.substring(2);
        }

        String suffix = "";
        for (String candidate : new String[]{"ull", "ul", "ll", "u", "l"})
        {
            if (digits.length() > candidate.length()
                && digits.regionMatches(true, digits.length() - candidate.length(), candidate, 0, candidate.length()))
            {
                char before = digits.charAt(digits.length() - candidate.length() - 1);
                if (Character.digit(before, radix) >= 0)
                {
                    suffix = candidate.toLowerCase();
                    digits = digits.substring(0, digits.length() - candidate.length());
                    break;
                }
            }
        }

        BigInteger value;
        try
        {
            value = new BigInteger(digits, radix);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        String name = switch (suffix)
        {
            case "l" -> "long";
            case "ll" -> "int128";
            case "u" -> "uint";
            case "ul" -> "ulong";
            case "ull" -> "uint128";
            default -> radix == 10 ? "int" : "uint";
        };
        return InferredType.intLiteral(value, name);
    }

    private static @Nullable InferredType parseFloatLiteral(@NotNull String text)
    {
        String name = "double";
        String digits = text;
        if (digits.endsWith("f") || digits.endsWith("F"))
        {
            name = "float";
            digits = digits.substring(0, digits.length() - 1);
        }
        else if (digits.endsWith("d") || digits.endsWith("D"))
        {
            digits = digits.substring(0, digits.length() - 1);
        }
        try
        {
            return InferredType.floatLiteral(Double.parseDouble(digits), name);
        }
        catch (NumberFormatException e)
        {
            return InferredType.unparsedFloatLiteral(name);
        }
    }

    private static @Nullable InferredType inferKeyword(@NotNull String text)
    {
        String clean = text.strip();
        if (clean.equals("true") || clean.equals("false")) return InferredType.boolType(true);
        if (clean.equals("null")) return InferredType.nullType();
        return null;
    }

    private static @Nullable InferredType inferString(@NotNull String text)
    {
        String clean = text.strip();
        if (clean.startsWith("'")) return parseCharLiteral(clean);
        if (clean.startsWith("\"") || clean.startsWith("`")) return InferredType.stringLiteral();
        return null;
    }

    private static @Nullable InferredType parseCharLiteral(@NotNull String text)
    {
        if (!text.endsWith("'") || text.length() < 3) return null;
        String inner = text.substring(1, text.length() - 1);
        BigInteger value = parseCharValue(inner);
        int width = inner.startsWith("\\") ? -1 : inner.codePointCount(0, inner.length());
        if (width == 1) return InferredType.charLiteral(value, "char");
        if (width == 2) return InferredType.charLiteral(null, "ushort");
        if (width == 4) return InferredType.charLiteral(null, "uint");
        if (width == 8) return InferredType.charLiteral(null, "ulong");
        if (value != null) return InferredType.charLiteral(value, "char");
        return null;
    }

    private static @Nullable BigInteger parseCharValue(@NotNull String inner)
    {
        try
        {
            if (inner.startsWith("\\"))
            {
                if (inner.length() == 2)
                {
                    return switch (inner.charAt(1))
                    {
                        case 'n' -> BigInteger.valueOf(10);
                        case 't' -> BigInteger.valueOf(9);
                        case 'r' -> BigInteger.valueOf(13);
                        case '0' -> BigInteger.ZERO;
                        case 'a' -> BigInteger.valueOf(7);
                        case 'b' -> BigInteger.valueOf(8);
                        case 'f' -> BigInteger.valueOf(12);
                        case 'v' -> BigInteger.valueOf(11);
                        case '\\' -> BigInteger.valueOf(92);
                        case '\'' -> BigInteger.valueOf(39);
                        case '"' -> BigInteger.valueOf(34);
                        default -> null;
                    };
                }
                if ((inner.startsWith("\\x") || inner.startsWith("\\X")) && inner.length() == 4)
                {
                    return new BigInteger(inner.substring(2), 16);
                }
                if ((inner.startsWith("\\u") || inner.startsWith("\\U")) && inner.length() > 2)
                {
                    return new BigInteger(inner.substring(2), 16);
                }
                return null;
            }
            if (inner.codePointCount(0, inner.length()) == 1) return BigInteger.valueOf(inner.codePointAt(0));
            return null;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static @Nullable InferredType inferUnary(@NotNull C3UnaryExpr unary, int depth)
    {
        if (unary.getUnaryOp().getType() != null)
        {
            // Explicit cast: (Type)expr. Casting an Optional lifts to an
            // Optional result: (T)expr? has type T?.
            String castText = unary.getUnaryOp().getType().getText();
            C3Expr castOperand = unary.getExpr();
            if (castOperand != null && !isOptionalName(castText))
            {
                InferredType innerCast = infer(castOperand, depth + 1);
                if (innerCast != null && isOptionalName(innerCast.getName()))
                {
                    return kindOf(normalize(castText) + "?");
                }
            }
            return kindOf(castText);
        }
        String op = unary.getUnaryOp().getText().strip();
        if (op.equals("&&"))
        {
            // Address of a temporary (rvalue), e.g. &&1: the operand type with no
            // addressability requirement.
            C3Expr tempOperand = unary.getExpr();
            if (tempOperand == null) return null;
            InferredType tempInner = infer(tempOperand, depth + 1);
            if (tempInner == null) return null;
            return InferredType.of(InferredType.Kind.POINTER, tempInner.getName() + "*");
        }
        C3Expr operand = unary.getExpr();
        if (operand == null) return null;
        InferredType inner = infer(operand, depth + 1);
        if (inner == null) return null;
        return switch (op)
        {
            case "-" -> negate(inner);
            case "+" -> numericOrNull(inner);
            case "!" -> InferredType.boolType(false);
            case "~" -> numericOrNull(inner);
            case "++", "--" -> numericOrNull(inner);
            case "&" -> InferredType.of(InferredType.Kind.POINTER, inner.getName() + "*");
            case "*" ->
            {
                if (inner.getKind() == InferredType.Kind.POINTER) yield kindOf(stripOnePointer(inner.getName()));
                yield null;
            }
            default -> null;
        };
    }

    private static @Nullable InferredType negate(@Nullable InferredType inner)
    {
        if (inner == null) return null;
        if (inner.getKind() == InferredType.Kind.INT && inner.getIntValue() != null)
        {
            return InferredType.intLiteral(inner.getIntValue().negate(), inner.getName());
        }
        if (inner.getKind() == InferredType.Kind.FLOAT && inner.getFloatValue() != null)
        {
            return InferredType.floatLiteral(-inner.getFloatValue(), inner.getName());
        }
        if (inner.getKind() == InferredType.Kind.CHAR && inner.getIntValue() != null)
        {
            return InferredType.charLiteral(inner.getIntValue().negate(), inner.getName());
        }
        return numericOrNull(inner);
    }

    private static @Nullable InferredType numericOrNull(@Nullable InferredType inner)
    {
        if (inner == null) return null;
        return switch (inner.getKind())
        {
            case INT, FLOAT, CHAR -> inner.isLiteral() ? inner : InferredType.of(inner.getKind(), inner.getName());
            default -> null;
        };
    }

    private static @NotNull String stripOnePointer(@NotNull String name)
    {
        String clean = normalize(name);
        if (clean.endsWith("*")) return clean.substring(0, clean.length() - 1).strip();
        return clean;
    }

    private static @Nullable InferredType inferBinary(@NotNull C3BinaryExpr binary, int depth)
    {
        String op = directOperator(binary);
        if (op == null) return null;
        C3Expr left = binary.getLeft();
        C3Expr right = binary.getRight();
        if (right == null) return null;
        if (op.equals("==") || op.equals("!=") || op.equals("<") || op.equals(">")
            || op.equals("<=") || op.equals(">=") || op.equals("&&") || op.equals("||"))
        {
            return InferredType.boolType(false);
        }
        // Assignments (including compound ones) are checked at statement level.
        if (op.endsWith("=")) return null;
        if (op.equals("?:") || op.equals("??"))
        {
            InferredType leftType = infer(left, depth + 1);
            InferredType rightType = infer(right, depth + 1);
            if (leftType != null && rightType != null && leftType.getName().equals(rightType.getName()))
            {
                return leftType.isLiteral() && rightType.isLiteral() ? leftType : InferredType.of(leftType.getKind(), leftType.getName());
            }
            if (op.equals("??") && leftType != null && rightType != null && isOptionalName(leftType.getName()))
            {
                // `opt ?? default`: the default replaces the empty case, so a
                // matching default unwraps the result to the plain value type.
                String unwrapped = stripOptional(leftType.getName());
                if (namesEqual(unwrapped, rightType.getName())) return kindOf(unwrapped);
            }
            return null;
        }
        InferredType leftType = infer(left, depth + 1);
        InferredType rightType = infer(right, depth + 1);
        if (leftType == null || rightType == null) return null;
        if ((op.equals("&") || op.equals("|") || op.equals("^"))
            && leftType.getKind() == InferredType.Kind.NAMED
            && namesEqual(leftType.getName(), rightType.getName())
            && isBitstruct(leftType.getName(), binary.getProject(), ModuleName.from(binary)))
        {
            // Bitwise ops on the same bitstruct stay in the bitstruct
            // (`BitMask c = a & b`, verified against the language docs).
            return InferredType.of(InferredType.Kind.NAMED, leftType.getName());
        }
        return arithmetic(leftType, rightType, op);
    }

    /**
     * The operator token between the operands, e.g. {@code "="}, {@code "+"}, {@code "=="}.
     * The operator may be wrapped in a {@code C3BinaryOp} element.
     */
    static @Nullable String directOperator(@NotNull C3BinaryExpr binary)
    {
        for (ASTNode child : binary.getNode().getChildren(null))
        {
            if (child.getPsi() instanceof C3BinaryOp)
            {
                String text = child.getText();
                if (text != null && !text.isBlank()) return text.strip();
            }
        }
        C3Expr left = binary.getLeft();
        C3Expr right = binary.getRight();
        int leftEnd = left.getTextRange().getEndOffset();
        int rightStart = right != null ? right.getTextRange().getStartOffset() : binary.getTextRange().getEndOffset();
        StringBuilder op = new StringBuilder();
        for (ASTNode child : binary.getNode().getChildren(null))
        {
            PsiElement psi = child.getPsi();
            if (!(psi instanceof LeafPsiElement) || psi instanceof PsiWhiteSpace) continue;
            int start = child.getTextRange().getStartOffset();
            if (start >= leftEnd && child.getTextRange().getEndOffset() <= rightStart)
            {
                op.append(child.getText());
            }
        }
        String result = op.toString().strip();
        return result.isEmpty() ? null : result;
    }

    private static @Nullable InferredType arithmetic(@NotNull InferredType left, @NotNull InferredType right, @NotNull String op)
    {
        boolean pointerOp = op.equals("+") || op.equals("-");
        if (pointerOp && left.getKind() == InferredType.Kind.POINTER && right.getKind() == InferredType.Kind.INT)
        {
            return InferredType.of(InferredType.Kind.POINTER, left.getName());
        }
        if (pointerOp && right.getKind() == InferredType.Kind.POINTER && left.getKind() == InferredType.Kind.INT)
        {
            return InferredType.of(InferredType.Kind.POINTER, right.getName());
        }
        boolean leftNum = isNumeric(left);
        boolean rightNum = isNumeric(right);
        if (!leftNum || !rightNum) return null;
        boolean floatSide = left.getKind() == InferredType.Kind.FLOAT || right.getKind() == InferredType.Kind.FLOAT;
        if (floatSide)
        {
            Double a = floatOperand(left);
            Double b = floatOperand(right);
            String name = (left.getName().equals("float") && right.getName().equals("float")) ? "float" : "double";
            if (a != null && b != null)
            {
                Double computed = computeFloat(a, b, op);
                if (computed != null) return InferredType.floatLiteral(computed, name);
            }
            return InferredType.of(InferredType.Kind.FLOAT, name);
        }
        if (left.isLiteral() && right.isLiteral() && left.getIntValue() != null && right.getIntValue() != null)
        {
            BigInteger computed = computeInt(left.getIntValue(), right.getIntValue(), op);
            if (computed != null) return InferredType.intLiteral(computed, "int");
            return null;
        }
        if (left.isLiteral() && !right.isLiteral()) return InferredType.of(right.getKind(), right.getName());
        if (right.isLiteral() && !left.isLiteral()) return InferredType.of(left.getKind(), left.getName());
        if (left.getName().equals(right.getName())) return InferredType.of(left.getKind(), left.getName());
        return null;
    }

    private static boolean isNumeric(@NotNull InferredType type)
    {
        return type.getKind() == InferredType.Kind.INT
            || type.getKind() == InferredType.Kind.FLOAT
            || type.getKind() == InferredType.Kind.CHAR;
    }

    private static @Nullable Double floatOperand(@NotNull InferredType type)
    {
        if (type.getKind() == InferredType.Kind.FLOAT) return type.getFloatValue();
        if (type.getIntValue() != null) return type.getIntValue().doubleValue();
        return null;
    }

    private static @Nullable Double computeFloat(double a, double b, @NotNull String op)
    {
        return switch (op)
        {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b == 0 ? null : a / b;
            case "%" -> b == 0 ? null : a % b;
            default -> null;
        };
    }

    private static @Nullable BigInteger computeInt(@NotNull BigInteger a, @NotNull BigInteger b, @NotNull String op)
    {
        return switch (op)
        {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            case "/" -> b.equals(BigInteger.ZERO) ? null : a.divide(b);
            case "%" -> b.equals(BigInteger.ZERO) ? null : a.remainder(b);
            case "<<", ">>" -> shift(a, b, op.equals("<<"));
            case "&" -> a.and(b);
            case "|" -> a.or(b);
            case "^" -> a.xor(b);
            default -> null;
        };
    }

    private static @Nullable BigInteger shift(@NotNull BigInteger value, @NotNull BigInteger amount, boolean left)
    {
        int shift;
        try
        {
            shift = amount.intValueExact();
        }
        catch (ArithmeticException e)
        {
            return null;
        }
        if (shift < 0 || shift > 1000000) return null;
        return left ? value.shiftLeft(shift) : value.shiftRight(shift);
    }

    private static @Nullable InferredType inferTernary(@NotNull C3TernaryExpr ternary, int depth)
    {
        List<C3Expr> branches = PsiTreeUtil.getChildrenOfTypeAsList(ternary, C3Expr.class);
        if (branches.size() < 3) return null;
        InferredType second = infer(branches.get(1), depth + 1);
        InferredType third = infer(branches.get(2), depth + 1);
        if (second != null && third != null && second.getName().equals(third.getName()))
        {
            return second.isLiteral() && third.isLiteral() ? second : InferredType.of(second.getKind(), second.getName());
        }
        return null;
    }

    private static @Nullable InferredType inferPathIdent(@NotNull C3PathIdentExpr pathIdentExpr, int depth)
    {
        C3PathIdent pathIdent = pathIdentExpr.getPathIdent();
        if (pathIdent.getPath() != null) return null;
        PsiElement resolved;
        try
        {
            resolved = pathIdent.getReference().resolve();
        }
        catch (Exception e)
        {
            return null;
        }
        if (resolved == null) return null;
        InferredType declared = inferDeclaredType(resolved, depth);
        if (declared != null) return declared;
        if (resolved instanceof C3ConstDeclarationStmt constDecl)
        {
            C3Expr init = constDecl.getExpr();
            if (init != null) return infer(init, depth + 1);
        }
        return null;
    }

    private static @Nullable InferredType inferPathConst(@NotNull C3PathConstExpr pathConst, int depth)
    {
        PsiElement resolved;
        try
        {
            resolved = pathConst.getPathConst().getReference().resolve();
        }
        catch (Exception e)
        {
            return null;
        }
        if (resolved == null) return null;
        InferredType declared = inferDeclaredType(resolved, depth);
        if (declared != null) return declared;
        if (resolved instanceof C3EnumConstant enumConstant)
        {
            return enumTypeOf(enumConstant);
        }
        if (resolved instanceof C3ConstDeclarationStmt constDecl && constDecl.getExpr() != null)
        {
            return infer(constDecl.getExpr(), depth + 1);
        }
        return null;
    }

    private static @Nullable InferredType inferEnumAccess(@NotNull C3EnumAccessExpr enumAccess)
    {
        C3BaseType baseType = enumAccess.getBaseType();
        if (baseType == null) return null;
        return InferredType.of(InferredType.Kind.NAMED, baseType.getText().strip());
    }

    /**
     * Declared type of a resolved variable as an inferred type. A
     * {@code $typeof(expr)} declaration evaluates the operand in place, so
     * {@code $typeof(*ptr) x} has the pointee type; anything else maps
     * through {@code kindOf} as before.
     */
    private static @Nullable InferredType inferDeclaredType(@NotNull PsiElement resolved, int depth)
    {
        C3Type declaredType = declaredTypeOf(resolved);
        if (declaredType == null)
        {
            String declared = assignedTypeText(resolved);
            return declared != null ? kindOf(declared) : null;
        }
        InferredType typeof = inferTypeofType(declaredType, depth);
        if (typeof != null) return typeof;
        String declared = assignedTypeText(resolved);
        return declared != null ? kindOf(declared) : null;
    }

    private static @Nullable C3Type declaredTypeOf(@NotNull PsiElement resolved)
    {
        try
        {
            if (resolved instanceof C3LocalDeclAfterType)
            {
                C3LocalDeclarationStmt stmt =
                    PsiTreeUtil.getParentOfType(resolved, C3LocalDeclarationStmt.class);
                if (stmt == null || stmt.getOptionalType() == null) return null;
                return stmt.getOptionalType().getType();
            }
            if (resolved instanceof C3Parameter parameter) return parameter.getType();
            if (resolved instanceof C3ParamDecl paramDecl)
            {
                return paramDecl.getParameter() != null ? paramDecl.getParameter().getType() : null;
            }
            if (resolved instanceof C3ConstDeclarationStmt constDecl) return constDecl.getType();
        }
        catch (Exception e)
        {
            return null;
        }
        return null;
    }

    /**
     * Resolves a {@code $typeof(expr)} type PSI to the operand's type name,
     * or {@code null} when it is not a {@code $typeof} type or the operand
     * cannot be inferred. Used to normalize declaration targets before
     * assignability checking.
     */
    public static @Nullable String resolveTypeofTarget(@Nullable C3Type type)
    {
        if (type == null) return null;
        InferredType inferred = inferTypeofType(type, 0);
        return inferred != null ? inferred.getName() : null;
    }

    private static @Nullable InferredType inferTypeofType(@NotNull C3Type type, int depth)
    {
        if (depth > MAX_DEPTH) return null;
        C3BaseType baseType;
        try
        {
            baseType = type.getBaseType();
        }
        catch (Exception e)
        {
            return null;
        }
        if (baseType == null) return null;
        boolean isTypeof;
        try
        {
            isTypeof = baseType.getNode().findChildByType(C3Types.KW_CT_TYPEOF) != null;
        }
        catch (Exception e)
        {
            return null;
        }
        if (!isTypeof) return null;
        C3Expr operand = baseType.getExpr();
        if (operand == null) return null;
        InferredType inferred = infer(operand, depth + 1);
        if (inferred == null) return null;
        return kindOf(inferred.getName());
    }

    private static @Nullable InferredType enumTypeOf(@NotNull C3EnumConstant enumConstant)
    {
        C3EnumDeclaration declaration = PsiTreeUtil.getParentOfType(enumConstant, C3EnumDeclaration.class);
        if (declaration == null || declaration.getTypeName() == null) return null;
        return InferredType.of(InferredType.Kind.NAMED, declaration.getTypeName().getText().strip());
    }

    /**
     * Expected type of an untyped lambda parameter from the surrounding
     * function-pointer type, e.g. {@code int} for {@code i} in
     * {@code apply(x, fn (i) => i * i)} with
     * {@code fn void apply(int[] arr, IntTransform t)} where
     * {@code alias IntTransform = fn int(int)}. Pure PSI walk plus alias
     * resolution; anything unrecognized yields {@code null} (unchecked).
     */
    public static @Nullable String lambdaParamType(@NotNull C3Parameter param)
    {
        if (param.getType() != null) return null;
        C3LambdaDecl lambdaDecl = PsiTreeUtil.getParentOfType(param, C3LambdaDecl.class);
        if (lambdaDecl == null) return null;
        C3ParameterList lambdaParams = lambdaDecl.getFnParameterList() != null
            ? lambdaDecl.getFnParameterList().getParameterList()
            : null;
        if (lambdaParams == null) return null;
        int paramIndex = -1;
        List<C3ParamDecl> lambdaDecls = lambdaParams.getParamDeclList();
        for (int i = 0; i < lambdaDecls.size(); i++)
        {
            if (lambdaDecls.get(i).getParameter() == param)
            {
                paramIndex = i;
                break;
            }
        }
        if (paramIndex < 0) return null;
        PsiElement lambdaExpr = lambdaDecl.getParent();
        if (lambdaExpr == null) return null;
        PsiElement context = lambdaExpr.getParent();
        String expectedFn = null;
        if (context instanceof C3Arg arg)
        {
            expectedFn = callArgFnType(arg, paramIndex);
        }
        else if (context instanceof C3LocalDeclAfterType declarator)
        {
            C3LocalDeclarationStmt stmt = PsiTreeUtil.getParentOfType(declarator, C3LocalDeclarationStmt.class);
            if (stmt != null && stmt.getOptionalType() != null && stmt.getOptionalType().getType() != null)
            {
                expectedFn = stmt.getOptionalType().getType().getText();
            }
        }
        else if (context instanceof C3ConstDeclarationStmt constDecl && constDecl.getType() != null)
        {
            expectedFn = constDecl.getType().getText();
        }
        if (expectedFn == null) return null;
        String fnText = underlyingFnType(expectedFn, param);
        if (fnText == null) return null;
        FnType fnType = parseFnType(fnText);
        if (fnType == null || paramIndex >= fnType.params.size()) return null;
        String typeText = fnType.params.get(paramIndex);
        return typeText.isBlank() ? null : typeText.strip();
    }

    record FnType(@NotNull String returns, @NotNull List<String> params)
    {
    }

    /**
     * Declared type text of the call parameter receiving the lambda's
     * argument (positional by order, named by name), or {@code null}.
     */
    private static @Nullable String callArgFnType(@NotNull C3Arg arg, int lambdaParamIndex)
    {
        C3CallExpr call = PsiTreeUtil.getParentOfType(arg, C3CallExpr.class);
        if (call == null) return null;
        C3CallablePsiElement callee;
        try
        {
            callee = CallChecker.resolveTarget(call);
        }
        catch (Exception e)
        {
            return null;
        }
        if (callee == null)
        {
            return null;
        }
        CallChecker.Signature signature;
        try
        {
            signature = CallChecker.buildSignature(callee);
        }
        catch (Exception e)
        {
            return null;
        }
        List<CallChecker.ParamInfo> params = signature.params;
        String ownerText = callee instanceof C3FuncDef funcDef
            ? InterfaceService.methodOwnerTypeName(funcDef)
            : (callee instanceof C3MacroDefinition macro
                ? InterfaceService.methodOwnerTypeName(macro)
                : null);
        int startIndex = 0;
        if (ownerText != null && !params.isEmpty())
        {
            C3Expr receiver = call.getExpr() instanceof C3CallExpr inner ? inner.getExpr() : call.getExpr();
            boolean staticReceiver = receiver instanceof C3TypeExpr;
            if (!staticReceiver && InterfaceService.firstParameterMatchesOwner(
                signature.paramTypes, signature.parameterList, ownerText))
            {
                startIndex = 1;
            }
        }
        String named = arg.getNamedIdent() != null ? arg.getNamedIdent().getText() : null;
        if (named != null)
        {
            for (int i = startIndex; i < params.size(); i++)
            {
                if (named.equals(params.get(i).name)) return params.get(i).typeText;
            }
            return null;
        }
        List<C3Arg> siblings = callArgList(call);
        int positional = 0;
        for (C3Arg sibling : siblings)
        {
            if (sibling == arg) break;
            if (sibling.getNamedIdent() == null) positional++;
        }
        boolean ownNamed = false;
        for (C3Arg sibling : siblings)
        {
            if (sibling != arg && sibling.getNamedIdent() != null) ownNamed = true;
        }
        // Positional-after-named is rejected by the compiler; bail out.
        if (ownNamed) return null;
        int slot = startIndex + positional;
        if (slot >= params.size())
        {
            for (int i = params.size() - 1; i >= startIndex; i--)
            {
                if (params.get(i).vaarg) return params.get(i).vaargElement;
            }
            return null;
        }
        return params.get(slot).typeText;
    }

    private static @NotNull List<C3Arg> callArgList(@NotNull C3CallExpr call)
    {
        try
        {
            C3CallExprTail tail = call.getCallExprTail();
            C3CallInvocation invocation = tail != null ? tail.getCallInvocation() : null;
            C3CallArgList callArgs = invocation != null ? invocation.getCallArgList() : null;
            C3ArgList args = callArgs != null ? callArgs.getArgList() : null;
            if (args != null) return args.getArgList();
        }
        catch (Exception ignored)
        {
        }
        return List.of();
    }

    private static @Nullable String underlyingFnType(@NotNull String expectedFn, @NotNull C3Parameter param)
    {
        return underlyingFnType(expectedFn, param.getProject(), ModuleName.from(param));
    }

    static @Nullable String underlyingFnTypeForCheck(
            @NotNull String expectedFn, @NotNull C3PsiElement context)
    {
        return underlyingFnType(expectedFn, context.getProject(), ModuleName.from(context));
    }

    private static @Nullable String underlyingFnType(
            @NotNull String expectedFn, @NotNull Project project, @Nullable ModuleName contextModule)
    {
        if (parseFnType(expectedFn) != null) return expectedFn;
        try
        {
            String resolved = resolveAlias(expectedFn, project, contextModule, 0);
            if (resolved != null && parseFnType(resolved) != null) return resolved;
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * Parses a function-pointer type (`fn int(int)`, `fn void()`) into its
     * return and parameter type texts. Anything else yields {@code null}.
     * Note: {@link #normalize} must not run before the prefix check, it
     * strips the space in `fn `.
     */
    static @Nullable FnType parseFnType(@NotNull String text)
    {
        String clean = text.strip();
        if (!clean.startsWith("fn ")) return null;
        String rest = clean.substring(3).strip();
        int open = rest.indexOf('(');
        int close = rest.lastIndexOf(')');
        if (open <= 0 || close <= open) return null;
        String returns = normalize(rest.substring(0, open).strip());
        if (returns.isEmpty()) return null;
        List<String> params = splitTopLevel(rest.substring(open + 1, close), ',');
        List<String> types = new ArrayList<>();
        for (String entry : params)
        {
            String item = entry.strip();
            if (item.isEmpty()) continue;
            // `type name` form degrades to the leading type.
            int space = item.indexOf(' ');
            if (space > 0 && item.substring(0, space).matches("[A-Za-z_][A-Za-z_0-9.:*\\[\\]]*")) item = item.substring(0, space);
            types.add(normalize(item));
        }
        return new FnType(returns, types);
    }

    private static @NotNull List<String> splitTopLevel(@NotNull String text, char separator)
    {
        List<String> parts = new ArrayList<>();
        int depthRound = 0;
        int depthSquare = 0;
        int depthAngle = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '(') depthRound++;
            else if (c == ')') depthRound--;
            else if (c == '[') depthSquare++;
            else if (c == ']') depthSquare--;
            else if (c == '<') depthAngle++;
            else if (c == '>') depthAngle--;
            else if (c == separator && depthRound == 0 && depthSquare == 0 && depthAngle == 0)
            {
                parts.add(text.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(text.substring(start));
        return parts;
    }
    public static @Nullable String declaredTypeText(@NotNull PsiElement resolved)
    {
        if (resolved instanceof C3LocalDeclAfterType)
        {
            C3LocalDeclarationStmt stmt =
                PsiTreeUtil.getParentOfType(resolved, C3LocalDeclarationStmt.class);
            if (stmt == null || stmt.getOptionalType() == null || stmt.getOptionalType().getType() == null) return null;
            return stmt.getOptionalType().getType().getText();
        }
        if (resolved instanceof C3Parameter parameter)
        {
            if (parameter.getType() != null) return parameter.getType().getText();
            try
            {
                return lambdaParamType(parameter);
            }
            catch (Exception ignored)
            {
                return null;
            }
        }
        if (resolved instanceof C3ParamDecl paramDecl)
        {
            C3Type type = paramDecl.getParameter() != null ? paramDecl.getParameter().getType() : null;
            return type != null ? type.getText() : null;
        }
        if (resolved instanceof C3ConstDeclarationStmt constDecl)
        {
            return constDecl.getType() != null ? constDecl.getType().getText() : null;
        }
        if (resolved instanceof C3EnumConstant enumConstant)
        {
            InferredType enumType = enumTypeOf(enumConstant);
            return enumType != null ? enumType.getName() : null;
        }
        return null;
    }

    /**
     * Declared type of a variable for assignment checking, preserving the
     * Optional suffix: {@code int?} for {@code int? x}, plain text otherwise.
     * A {@code $typeof(expr)} declaration resolves to the operand's type.
     */
    public static @Nullable String assignedTypeText(@NotNull PsiElement resolved)
    {
        C3Type declaredType = declaredTypeOf(resolved);
        if (declaredType != null)
        {
            String typeof = resolveTypeofTarget(declaredType);
            if (typeof != null) return typeof;
        }
        String base = declaredTypeText(resolved);
        if (base == null) return null;
        if (resolved instanceof C3LocalDeclAfterType)
        {
            C3LocalDeclarationStmt stmt =
                PsiTreeUtil.getParentOfType(resolved, C3LocalDeclarationStmt.class);
            if (stmt != null && stmt.getOptionalType() != null
                && stmt.getOptionalType().getNode().findChildByType(C3Types.QUESTION) != null
                && !isOptionalName(base))
            {
                return base + "?";
            }
        }
        return base;
    }

    private static @Nullable InferredType inferCall(@NotNull C3CallExpr call, int depth)
    {
        if (call.getCallExprTail() == null || call.getCallExprTail().getCallInvocation() == null)
        {
            C3CallExprTail tail = call.getCallExprTail();
            if (tail != null && depth < MAX_DEPTH)
            {
                ASTNode tailNode = tail.getNode();
                boolean rethrow = tailNode.findChildByType(C3Types.BANG) != null;
                boolean force = !rethrow && tailNode.findChildByType(C3Types.BANGBANG) != null;
                if (rethrow || force)
                {
                    // `expr!` (rethrow) and `expr!!` (force unwrap) evaluate to
                    // the Optional's result type.
                    InferredType inner = infer(call.getExpr(), depth + 1);
                    if (inner == null) return null;
                    if (isOptionalName(inner.getName())) return kindOf(stripOptional(inner.getName()));
                    return inner;
                }
                // `expr~` builds an Optional excuse: the result type comes
                // from the context, so it stays unknown here.
                if (tailNode.findChildByType(C3Types.BIT_NOT) != null) return null;
            }
            // Field access like `a.b`: resolve the member itself.
            C3AccessIdent accessIdent = call.getCallExprTail() != null ? call.getCallExprTail().getAccessIdent() : null;
            if (accessIdent == null) return null;
            InferredType builtin = builtinMemberType(call.getExpr(), accessIdent.getNameIdent(), depth);
            if (builtin != null) return builtin;
            PsiElement resolved = accessIdent.getReference().resolve();
            if (resolved instanceof C3StructMemberDeclaration member && member.getStructPathType() != null)
            {
                return kindOf(member.getStructPathType().getFullName());
            }
            String bitFieldType = bitstructFieldTypeText(resolved);
            if (bitFieldType != null) return kindOf(bitFieldType);
            return null;
        }
        C3Expr callee = call.getExpr();
        if (callee instanceof C3PathIdentExpr)
        {
            // Full overload selection (same-module shadowing, arity, types),
            // so inference agrees with checking on which declaration is called.
            C3CallablePsiElement target;
            try
            {
                target = CallChecker.resolveTarget(call);
            }
            catch (Exception e)
            {
                return null;
            }
            if (target instanceof C3FuncDef funcDef) return cascadeOptional(call, returnTypeOf(funcDef), depth);
            if (target instanceof C3MacroDefinition macro) return cascadeOptional(call, returnTypeOf(macro), depth);
            return null;
        }
        if (callee instanceof C3PathAtIdentExpr pathAtIdentExpr)
        {
            // `@macro(args)` calls.
            for (PsiReference reference : pathAtIdentExpr.getPathAtIdent().getReferences())
            {
                PsiElement resolved;
                try
                {
                    resolved = reference.resolve();
                }
                catch (Exception e)
                {
                    continue;
                }
                if (resolved instanceof C3CallablePsiElement callable) return cascadeOptional(call, returnTypeOf(callable), depth);
            }
            return null;
        }
        if (callee instanceof C3CallExpr inner && depth < MAX_DEPTH)
        {
            // Chained access like `a.b().c`: resolve `c` against the inner call's type.
            C3AccessIdent outerIdent = call.getCallExprTail().getAccessIdent();
            InferredType innerType = infer(inner, depth + 1);
            if (outerIdent == null || innerType == null) return null;
            if (innerType.getKind() != InferredType.Kind.NAMED) return null;
            PsiElement resolved = outerIdent.getReference().resolve();
            if (resolved instanceof C3FuncDef funcDef) return returnTypeOf(funcDef);
            if (resolved instanceof C3StructMemberDeclaration member && member.getStructPathType() != null)
            {
                return kindOf(member.getStructPathType().getFullName());
            }
            return null;
        }
        return null;
    }

    /**
     * Built-in member types that need no declaration lookup: the two fields
     * of {@code any} ({@code .ptr} is {@code void*}, {@code .type} is
     * {@code typeid}) and the reflection properties available on a
     * {@code typeid} value (both compile-time on a type name and run-time on
     * a {@code typeid} variable, e.g. {@code arg.type.inner}).
     * Pure inference, no index access.
     */
    private static @Nullable InferredType builtinMemberType(
            @NotNull C3Expr receiver, @Nullable String member, int depth)
    {
        if (member == null || depth >= MAX_DEPTH) return null;
        // `.sizeof`/`.alignof` on a type name (`Header.sizeof`) are
        // compile-time constants: with a computable layout they infer as
        // literals, so narrowing follows values exactly like c3c
        // (`uint pos = Header.sizeof` is fine for small structs). This runs
        // before receiver inference, which yields nothing for type receivers.
        if (member.equals("sizeof") || member.equals("alignof"))
        {
            InferredType constant = typePropertyConstant(receiver, member);
            if (constant != null) return constant;
        }
        InferredType receiverType = infer(receiver, depth + 1);
        if (receiverType == null) return null;
        String clean = stripOptional(normalize(receiverType.getName()));
        if (shortName(clean).equals("any"))
        {
            if (member.equals("ptr")) return kindOf("void*");
            if (member.equals("type")) return kindOf("typeid");
            return null;
        }
        if (!shortName(clean).equals("typeid")) return null;
        return switch (member)
        {
            // typeid-valued properties.
            case "inner", "parentof" -> kindOf("typeid");
            // Integer-valued properties (sizes, lengths, bounds).
            case "sizeof", "alignof", "len", "elements", "min", "max" -> kindOf("usz");
            // String-valued properties.
            case "nameof", "qnameof" -> kindOf("String");
            // Anything else (kindof enum, membersof, methods, ...) is a real
            // type but unmodelled here: unknown, not an error.
            default -> null;
        };
    }

    private static @Nullable InferredType returnTypeOf(@NotNull C3CallablePsiElement callable)
    {
        ShortType returnType = callable.getReturnType();
        if (returnType == null || returnType.getValue() == null) return null;
        return kindOf(returnType.getValue());
    }

    /**
     * Compile-time value of {@code .sizeof}/`.alignof`} on a type-name
     * receiver, or {@code null} when the receiver is a value (runtime
     * {@code typeid}, ordinary variables) or the layout is not computable.
     * Unknown layouts fall back to the plain {@code usz} type upstream.
     */
    private static @Nullable InferredType typePropertyConstant(
            @NotNull C3Expr receiver,
            @NotNull String member)
    {
        String typeText = typeNameOf(receiver);
        if (typeText == null) return null;
        Layout layout;
        try
        {
            layout = layoutOf(typeText, receiver.getProject(), ModuleName.from(receiver), 0, new HashSet<>());
        }
        catch (Exception e)
        {
            return null;
        }
        if (layout == null) return null;
        long value = member.equals("sizeof") ? layout.size() : layout.align();
        return InferredType.intLiteral(BigInteger.valueOf(value), "usz");
    }

    /**
     * The named type when the receiver expression denotes a type rather than
     * a value: a path resolving to a type declaration, or a primitive
     * keyword. Anything else (variables, calls, runtime {@code typeid}
     * values) is not a type context.
     */
    private static @Nullable String typeNameOf(@NotNull C3Expr receiver)
    {
        // `Header.sizeof` parses the receiver as a type expression: it is a
        // type by construction.
        if (receiver instanceof C3TypeExpr typeExpr)
        {
            String text = typeExpr.getText();
            if (text != null && !text.isBlank()) return text.strip();
            return null;
        }
        if (!(receiver instanceof C3PathIdentExpr pathExpr) || pathExpr.getPathIdent().getPath() != null) return null;
        String text = pathExpr.getPathIdent().getText();
        if (text == null || text.isBlank()) return null;
        String clean = text.strip();
        if (!clean.matches("[A-Za-z_][A-Za-z_0-9.:]*")) return null;
        // A resolved value (local, parameter, function, ...) is not a type.
        try
        {
            PsiElement resolved = pathExpr.getPathIdent().getReference().resolve();
            if (resolved instanceof C3TypeName) return clean;
            if (resolved != null) return null;
        }
        catch (Exception ignored)
        {
            return null;
        }
        // Unresolved primitives (`int`, `uint`, ...) never declare anything.
        String shortName = shortName(clean);
        if (INT_TYPES.containsKey(shortName) || FLOAT_TYPES.containsKey(shortName)
            || shortName.equals("char") || shortName.equals("ichar") || shortName.equals("bool")
            || shortName.equals("any") || shortName.equals("typeid") || shortName.equals("fault")
            || shortName.equals("String") || shortName.equals("ZString") || shortName.equals("void")) return clean;
        // Otherwise the name must declare a type somewhere.
        try
        {
            if (!org.c3lang.intellij.index.InterfaceService.INSTANCE
                .findTypeDeclarations(FullyQualifiedName.parse(clean), receiver.getProject()).isEmpty()) return clean;
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private record Layout(long size, long align)
    {
    }

    /**
     * Compile-time layout of a type with C layout rules (verified
     * against {@code c3c}: sequential members at aligned offsets padded to
     * the max alignment, unions take the max member, {@code @packed} drops
     * padding, bitstructs occupy their backing type). Anything not
     * statically modellable here (exotic attributes, unresolvable names)
     * yields {@code null}. Depth-bounded with cycle protection.
     */
    private static @Nullable Layout layoutOf(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            @NotNull Set<String> visiting)
    {
        if (depth > 8 || DumbService.isDumb(project)) return null;
        String clean = normalize(typeText).strip();
        if (clean.isEmpty()) return null;
        if (clean.endsWith("*")) return new Layout(8, 8);
        if (clean.endsWith("[]")) return new Layout(16, 8);
        String shortName = shortName(clean);
        if (shortName.equals("any") || shortName.equals("String")) return new Layout(16, 8);
        if (shortName.equals("typeid") || shortName.equals("fault") || shortName.equals("ZString"))
        {
            return new Layout(8, 8);
        }
        if (shortName.equals("char") || shortName.equals("ichar") || shortName.equals("bool"))
        {
            return new Layout(1, 1);
        }
        int[] intBits = INT_TYPES.get(shortName);
        if (intBits != null) return new Layout(intBits[0] / 8, intBits[0] / 8);
        Integer floatBits = FLOAT_TYPES.get(shortName);
        if (floatBits != null) return new Layout(floatBits / 8, floatBits / 8);
        VectorInfo array = parseArray(clean);
        if (array != null && !array.element.isEmpty())
        {
            Long count = array.size >= 0 ? array.size
                : " *".equals(array.sizeText) || array.sizeText.isEmpty() ? null
                : evalSize(array.sizeText, project, contextModule, 0);
            Layout element = layoutOf(array.element, project, contextModule, depth + 1, visiting);
            if (count == null || count < 0 || element == null) return null;
            return new Layout(count * element.size(), element.align());
        }
        VectorInfo vector = parseVector(clean);
        if (vector != null && !vector.element.isEmpty())
        {
            Long count = vector.size >= 0 ? vector.size : evalSize(vector.sizeText, project, contextModule, 0);
            Layout element = layoutOf(vector.element, project, contextModule, depth + 1, visiting);
            if (count == null || count < 0 || element == null) return null;
            return new Layout(count * element.size(), element.align());
        }
        // Aliases, typedefs and distinct types: walk the underlying spelling.
        String underlying = resolveCastType(clean, project, contextModule);
        if (underlying != null && !namesEqual(underlying, clean))
        {
            return layoutOf(underlying, project, contextModule, depth + 1, visiting);
        }
        // Constdefs and enums occupy their backing type.
        String backing = constdefOrEnumBacking(clean, project, contextModule);
        if (backing != null) return layoutOf(backing, project, contextModule, depth + 1, visiting);
        // A bitstruct occupies its backing type (`bitstruct Sb : char` is 1 byte).
        String bitBacking = bitstructBacking(clean, project, contextModule);
        if (bitBacking != null) return layoutOf(bitBacking, project, contextModule, depth + 1, visiting);
        return structLayout(clean, project, contextModule, depth, visiting);
    }

    private static @Nullable Layout structLayout(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            @NotNull Set<String> visiting)
    {
        FullyQualifiedName name = FullyQualifiedName.parse(typeText);
        List<C3StructDeclaration> declarations;
        try
        {
            declarations =
                org.c3lang.intellij.index.InterfaceService.INSTANCE.findStructDeclarations(name, project);
        }
        catch (Exception e)
        {
            return null;
        }
        C3StructDeclaration declaration = preferModule(declarations, contextModule);
        if (declaration == null || declaration.getStructBody() == null) return null;
        if (hasAnyAttribute(declaration, "compact", "overlap", "structlike")) return null;
        String key = declaration.getTypeName().getText().strip() + "@"
            + (ModuleName.from(declaration) != null ? ModuleName.from(declaration).getValue() : "");
        if (!visiting.add(key)) return null;
        try
        {
            return membersLayout(declaration.getStructBody(), isUnion(declaration), project, contextModule, depth, visiting);
        }
        finally
        {
            visiting.remove(key);
        }
    }

    private static @Nullable Layout membersLayout(
            @NotNull C3StructBody body,
            boolean union,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            @NotNull Set<String> visiting)
    {
        boolean packed = false;
        try
        {
            PsiElement owner = body.getParent();
            if (owner instanceof C3StructDeclaration structDecl && structDecl.getAttributes() != null)
            {
                packed = AttributeSpecs.hasAttribute(structDecl.getAttributes(), "packed");
            }
        }
        catch (Exception ignored)
        {
        }
        long offset = 0;
        long maxAlign = 1;
        long maxSize = 0;
        for (C3StructMemberDeclaration member : body.getStructMemberDeclarationList())
        {
            Layout memberLayout;
            try
            {
                if (member.getStructBody() != null)
                {
                    // Anonymous nested struct/union: expanded inline.
                    memberLayout = membersLayout(member.getStructBody(), isUnion(member),
                        project, contextModule, depth + 1, visiting);
                }
                else if (member.getBitstructBody() != null)
                {
                    return null;
                }
                else
                {
                    FullyQualifiedName memberType = member.getStructPathType();
                    if (memberType == null) return null;
                    memberLayout = layoutOf(memberType.getFullName(), project, contextModule, depth + 1, visiting);
                }
            }
            catch (Exception e)
            {
                return null;
            }
            if (memberLayout == null) return null;
            if (union)
            {
                maxSize = Math.max(maxSize, memberLayout.size());
                maxAlign = Math.max(maxAlign, packed ? 1 : memberLayout.align());
            }
            else
            {
                long align = packed ? 1 : memberLayout.align();
                offset = alignUp(offset, align);
                offset += memberLayout.size();
                maxAlign = Math.max(maxAlign, align);
            }
        }
        if (union) return new Layout(maxSize, maxAlign);
        return new Layout(alignUp(offset, packed ? 1 : maxAlign), packed ? 1 : maxAlign);
    }

    private static long alignUp(long offset, long align)
    {
        if (align <= 1) return offset;
        return (offset + align - 1) / align * align;
    }

    private static boolean isUnion(@NotNull PsiElement element)
    {
        try
        {
            return element.getNode() != null && element.getNode().findChildByType(C3Types.KW_UNION) != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean hasAnyAttribute(@NotNull C3StructDeclaration declaration, @NotNull String... names)
    {
        try
        {
            if (declaration.getAttributes() == null) return false;
            for (String name : names)
            {
                if (AttributeSpecs.hasAttribute(declaration.getAttributes(), name)) return true;
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    private static @Nullable C3StructDeclaration preferModule(
            @NotNull List<C3StructDeclaration> declarations,
            @Nullable ModuleName contextModule)
    {
        if (declarations.isEmpty()) return null;
        if (contextModule != null)
        {
            for (C3StructDeclaration declaration : declarations)
            {
                if (contextModule.equals(ModuleName.from(declaration))) return declaration;
            }
        }
        return declarations.get(0);
    }

    private static @Nullable String constdefOrEnumBacking(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        String clean = normalize(typeText).strip();
        if (!clean.matches("[A-Za-z_][A-Za-z_0-9.:]*")) return null;
        String wanted = shortName(clean);
        boolean qualified = clean.contains("::");
        C3ConstdefDeclaration constdefMatch = null;
        C3EnumDeclaration enumMatch = null;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (qualified)
                {
                    if (!key.equals(clean)) continue;
                }
                else if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
                for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
                {
                    if (!(element instanceof C3TypeName typeName)) continue;
                    if (!typeName.getText().strip().equals(wanted)) continue;
                    if (typeName.getParent() instanceof C3ConstdefDeclaration constdef)
                    {
                        if (constdefMatch == null) constdefMatch = constdef;
                        if (contextModule != null && contextModule.equals(ModuleName.from(constdef)))
                        {
                            constdefMatch = constdef;
                        }
                    }
                    else if (typeName.getParent() instanceof C3EnumDeclaration enumDecl)
                    {
                        if (enumMatch == null) enumMatch = enumDecl;
                        if (contextModule != null && contextModule.equals(ModuleName.from(enumDecl)))
                        {
                            enumMatch = enumDecl;
                        }
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
        if (constdefMatch != null)
        {
            try
            {
                C3Type backing = constdefMatch.getType();
                if (backing != null && backing.getText() != null && !backing.getText().isBlank())
                {
                    return backing.getText().strip();
                }
            }
            catch (Exception ignored)
            {
            }
            return null;
        }
        if (enumMatch != null)
        {
            try
            {
                C3Type backing = enumMatch.getType();
                if (backing != null && backing.getText() != null && !backing.getText().isBlank())
                {
                    return backing.getText().strip();
                }
            }
            catch (Exception ignored)
            {
            }
            // Untyped enums default to `int` (verified against c3c).
            return "int";
        }
        return null;
    }

    /**
     * Bitstruct declaration by (possibly qualified) name, or {@code null}.
     * Pure index scan; same-module declarations win on name clashes.
     */
    private static @Nullable C3BitstructDeclaration findBitstructDecl(
            @NotNull String structName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        if (DumbService.isDumb(project)) return null;
        String clean = normalize(structName).strip();
        if (!clean.matches("[A-Za-z_][A-Za-z_0-9.:]*")) return null;
        String wanted = shortName(clean);
        boolean qualified = clean.contains("::");
        C3BitstructDeclaration match = null;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (qualified)
                {
                    if (!key.equals(clean)) continue;
                }
                else if (!key.equals(wanted) && !key.endsWith("::" + wanted)) continue;
                for (C3PsiElement element : safeElements(TypeIndex.KEY, key, project))
                {
                    if (!(element instanceof C3TypeName typeName)) continue;
                    if (!typeName.getText().strip().equals(wanted)) continue;
                    if (!(typeName.getParent() instanceof C3BitstructDeclaration bitstruct)) continue;
                    if (match == null) match = bitstruct;
                    if (contextModule != null && contextModule.equals(ModuleName.from(bitstruct)))
                    {
                        match = bitstruct;
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
        return match;
    }

    /**
     * Whether the name denotes a bitstruct (verified against the type
     * index, dumb-safe).
     */
    public static boolean isBitstruct(
            @NotNull String typeName,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        return findBitstructDecl(typeName, project, contextModule) != null;
    }

    /**
     * Backing type of a bitstruct ({@code char} for
     * {@code bitstruct Sb : char}), or {@code null} when the name is not a
     * bitstruct. Pure index scan plus a guarded PSI read of the
     * declaration's type.
     */
    public static @Nullable String bitstructBacking(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        C3BitstructDeclaration match = findBitstructDecl(typeText, project, contextModule);
        if (match == null) return null;
        try
        {
            C3Type backing = match.getType();
            if (backing != null && backing.getText() != null && !backing.getText().isBlank())
            {
                return backing.getText().strip();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * Bitstruct field declaration ({@code C3BitstructDef} or
     * {@code C3BitstructSimpleDef}) by struct and field name, or
     * {@code null}. Index scan plus a guarded PSI read of the body.
     */
    public static @Nullable C3PsiElement findBitstructField(
            @NotNull String structName,
            @NotNull String field,
            @NotNull Project project,
            @Nullable ModuleName contextModule)
    {
        C3BitstructDeclaration match = findBitstructDecl(structName, project, contextModule);
        if (match == null) return null;
        try
        {
            C3BitstructBody body = match.getBitstructBody();
            if (body == null) return null;
            for (C3BitstructDef def : body.getBitstructDefList())
            {
                if (field.equals(bitFieldName(def))) return def;
            }
            for (C3BitstructSimpleDef def : body.getBitstructSimpleDefList())
            {
                if (field.equals(bitFieldName(def))) return def;
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private static @Nullable String bitFieldName(@NotNull C3PsiElement def)
    {
        try
        {
            ASTNode ident = def.getNode().findChildByType(C3Types.IDENT);
            return ident != null ? ident.getText() : null;
        }
        catch (Exception e)
        {
            return null;
        }
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

    /**
     * Declared type text of a bitstruct field ({@code int} for
     * {@code int a : 0..2}), or {@code null} for anything else.
     */
    public static @Nullable String bitstructFieldTypeText(@Nullable PsiElement field)
    {
        try
        {
            if (field instanceof C3BitstructDef def && def.getBaseType() != null)
            {
                return def.getBaseType().getText().strip();
            }
            if (field instanceof C3BitstructSimpleDef simple && simple.getBaseType() != null)
            {
                return simple.getBaseType().getText().strip();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * Error when a constant assigned to a bitstruct field does not fit the
     * field's bit range ({@code int a : 0..2} holds 0..3): c3c rejects it as
     * {@code This constant would be truncated if stored in the bitstruct...}.
     * Non-constant expressions stay unchecked (the compiler truncates them).
     */
    public static @Nullable String bitstructTruncationError(@Nullable PsiElement field, @Nullable InferredType source)
    {
        if (field == null || source == null || !source.isLiteral() || source.getIntValue() == null) return null;
        if (!(field instanceof C3BitstructDef def)) return null;
        int bits;
        try
        {
            List<C3Expr> bounds = def.getExprList();
            if (bounds.isEmpty()) return null;
            if (bounds.size() < 2)
            {
                // Single position (`bool b : 3`): exactly one bit.
                bits = 1;
            }
            else
            {
                ModuleName module = ModuleName.from(def);
                Long start = evalSize(bounds.get(0).getText(), def.getProject(), module, 0);
                Long end = evalSize(bounds.get(1).getText(), def.getProject(), module, 0);
                if (start == null || end == null || end <= start) return null;
                bits = (int) Math.min(end - start, 62);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        BigInteger value = source.getIntValue();
        if (value.signum() < 0) return null;
        if (value.bitLength() > bits)
        {
            return "This constant would be truncated if stored in the bitstruct, do you need a wider bit range?";
        }
        return null;
    }

    /**
     * c3c rejects every implicit conversion between a bitstruct and another
     * type in either direction ({@code int i = s} and {@code Sb s = 5} are
     * both `Implicitly casting ... is not permitted...`); only the explicit
     * cast (see {@link #checkCast}) converts. Initializer lists are not
     * conversions and bypass this.
     */
    static @Nullable String bitstructImplicitCastError(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull InferredType source)
    {
        if (source.getKind() == InferredType.Kind.INIT_LIST) return null;
        String sourceName = normalize(source.getName());
        if (isOptionalName(targetText) || isOptionalName(sourceName)) return null;
        String target = stripOptional(normalize(targetText));
        if (target.isEmpty() || namesEqual(target, sourceName)) return null;
        boolean targetBit = bitstructBacking(target, project, contextModule) != null;
        boolean sourceBit = bitstructBacking(sourceName, project, contextModule) != null;
        if (targetBit == sourceBit) return null;
        return "Implicitly casting '" + shortName(sourceName) + "' to '" + shortName(target)
            + "' is not permitted, but you may do an explicit cast by placing '(" + shortName(target)
            + ")' before the expression.";
    }

    /**
     * Cascading: calling a function with an Optional argument only executes
     * the function when every Optional argument holds a result, so a plain
     * result type becomes Optional. An already-Optional result stays as is.
     */
    private static @Nullable InferredType cascadeOptional(
            @NotNull C3CallExpr call, @Nullable InferredType result, int depth)
    {
        if (result == null || isOptionalName(result.getName()) || depth >= MAX_DEPTH) return result;
        // `@catch`/`@ok` consume an Optional and return a plain value.
        C3Expr callee = call.getExpr();
        if (callee instanceof C3PathAtIdentExpr atExpr && atExpr.getPathAtIdent() != null)
        {
            String name = atExpr.getPathAtIdent().getText();
            if (name != null && (name.strip().equals("@catch") || name.strip().equals("@ok"))) return result;
        }
        C3CallExprTail tail = call.getCallExprTail();
        C3CallInvocation invocation = tail != null ? tail.getCallInvocation() : null;
        C3CallArgList callArgs = invocation != null ? invocation.getCallArgList() : null;
        C3ArgList args = callArgs != null ? callArgs.getArgList() : null;
        if (args == null) return result;
        for (C3Arg arg : args.getArgList())
        {
            C3Expr argExpr = arg.getExpr();
            if (argExpr == null) continue;
            InferredType argType = infer(argExpr, depth + 1);
            if (argType != null && isOptionalName(argType.getName()))
            {
                return kindOf(result.getName() + "?");
            }
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Function structure helpers for the annotator
    // ------------------------------------------------------------------

    /**
     * Owning {@code fn} for a return statement, or {@code null} when the
     * return belongs to a lambda or macro body. Note the function body is a
     * sibling of {@code C3FuncDef} under {@code C3FuncDefinition}, not a child.
     */
    public static @Nullable C3FuncDef enclosingFunction(@NotNull C3ReturnStmt ret)
    {
        C3FuncDefinition definition = PsiTreeUtil.getParentOfType(
            ret,
            C3FuncDefinition.class,
            true,
            C3LambdaDeclExpr.class,
            C3LambdaDeclShortExpr.class,
            C3MacroDefinition.class);
        return definition != null ? definition.getFuncDef() : null;
    }

    private static final java.util.Set<String> ASSIGN_OPERATORS = java.util.Set.of(
        "=",
        "+=", "-=", "*=", "/=", "%=",
        "<<=", ">>=",
        "&=", "|=", "^=");

    /**
     * Assignment operator of a binary expression ({@code "="}, {@code "+="}, ...), or {@code null}.
     */
    public static @Nullable String assignmentOperator(@NotNull C3BinaryExpr binary)
    {
        String op = directOperator(binary);
        return op != null && ASSIGN_OPERATORS.contains(op) ? op : null;
    }
}
