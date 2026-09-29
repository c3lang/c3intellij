package org.c3lang.intellij.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * The statically inferred type of a C3 expression.
 *
 * <p>{@code null} is used instead of an instance to mean "unknown" — an unknown
 * type never produces an error.
 */
public final class InferredType
{
    public enum Kind
    {
        INT,
        FLOAT,
        BOOL,
        CHAR,
        STRING,
        POINTER,
        NAMED,
        INIT_LIST,
        NULL,
        VOID
    }

    private final @NotNull Kind kind;
    private final @NotNull String name;
    private final boolean literal;
    private final @Nullable BigInteger intValue;
    private final @Nullable Double floatValue;
    private final @NotNull List<InferredType> elements;
    private final boolean namedArguments;

    private InferredType(
            @NotNull Kind kind,
            @NotNull String name,
            boolean literal,
            @Nullable BigInteger intValue,
            @Nullable Double floatValue,
            @NotNull List<InferredType> elements,
            boolean namedArguments)
    {
        this.kind = kind;
        this.name = name;
        this.literal = literal;
        this.intValue = intValue;
        this.floatValue = floatValue;
        this.elements = elements;
        this.namedArguments = namedArguments;
    }

    public @NotNull Kind getKind()
    {
        return kind;
    }

    /**
     * Human-readable type name, e.g. {@code "uint"}, {@code "String"}, {@code "Baz*"}.
     */
    public @NotNull String getName()
    {
        return name;
    }

    public boolean isLiteral()
    {
        return literal;
    }

    public @Nullable BigInteger getIntValue()
    {
        return intValue;
    }

    public @Nullable Double getFloatValue()
    {
        return floatValue;
    }

    public static @NotNull InferredType voidType()
    {
        return new InferredType(Kind.VOID, "void", false, null, null, List.of(), false);
    }

    public static @NotNull InferredType boolType(boolean literal)
    {
        return new InferredType(Kind.BOOL, "bool", literal, null, null, List.of(), false);
    }

    public static @NotNull InferredType nullType()
    {
        return new InferredType(Kind.NULL, "null", false, null, null, List.of(), false);
    }

    public static @NotNull InferredType intLiteral(@NotNull BigInteger value, @NotNull String name)
    {
        return new InferredType(Kind.INT, name, true, value, null, List.of(), false);
    }

    public static @NotNull InferredType typedIntLiteral(@NotNull String name)
    {
        return new InferredType(Kind.INT, name, true, null, null, List.of(), false);
    }

    public static @NotNull InferredType floatLiteral(double value, @NotNull String name)
    {
        return new InferredType(Kind.FLOAT, name, true, null, value, List.of(), false);
    }

    public static @NotNull InferredType unparsedFloatLiteral(@NotNull String name)
    {
        return new InferredType(Kind.FLOAT, name, true, null, null, List.of(), false);
    }

    public static @NotNull InferredType charLiteral(@Nullable BigInteger value, @NotNull String name)
    {
        return new InferredType(Kind.CHAR, name, true, value, null, List.of(), false);
    }

    public static @NotNull InferredType stringLiteral()
    {
        return new InferredType(Kind.STRING, "String", true, null, null, List.of(), false);
    }

    public static @NotNull InferredType of(@NotNull Kind kind, @NotNull String name)
    {
        return new InferredType(kind, name, false, null, null, List.of(), false);
    }

    /**
     * An initializer list like {@code { 1, 2 }}. Elements may be {@code null}
     * (unknown); {@code namedArguments} is true for designated initializers.
     */
    public static @NotNull InferredType initList(@NotNull List<InferredType> elements, boolean namedArguments)
    {
        return new InferredType(Kind.INIT_LIST, "{...}", false, null, null,
            java.util.Collections.unmodifiableList(new java.util.ArrayList<>(elements)), namedArguments);
    }

    /**
     * Positional element types; entries may be {@code null} (unknown).
     */
    public @NotNull List<InferredType> getElements()
    {
        return elements;
    }

    public boolean hasNamedArguments()
    {
        return namedArguments;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof InferredType that)) return false;
        return literal == that.literal
            && namedArguments == that.namedArguments
            && kind == that.kind
            && name.equals(that.name)
            && elements.equals(that.elements)
            && Objects.equals(intValue, that.intValue)
            && Objects.equals(floatValue, that.floatValue);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(kind, name, literal, elements, namedArguments, intValue, floatValue);
    }

    @Override
    public @NotNull String toString()
    {
        return "InferredType(" + kind + " " + name + (literal ? " literal" : "") + ")";
    }
}
