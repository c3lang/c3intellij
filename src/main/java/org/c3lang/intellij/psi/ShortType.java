package org.c3lang.intellij.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShortType
{
    public static final Companion Companion = new Companion();

    private final String value;
    private final String prefix;
    private final String fullName;

    public ShortType(@NotNull String value)
    {
        this(value, null);
    }

    public ShortType(@NotNull String value, @Nullable String prefix)
    {
        this.value = value;
        this.prefix = prefix;
        this.fullName = prefix != null ? prefix + "::" + value : value;
    }

    @NotNull
    public String getValue()
    {
        return value;
    }

    @Nullable
    public String getPrefix()
    {
        return prefix;
    }

    @NotNull
    public String getFullName()
    {
        return fullName;
    }

    public static @NotNull ShortType from(@NotNull C3Type psi)
    {
        return Companion.from(psi);
    }

    public static @NotNull ShortType toShortType(@NotNull C3Type psi)
    {
        return Companion.toShortType(psi);
    }

    public static @NotNull ShortType parse(@NotNull String string)
    {
        return Companion.parse(string);
    }

    public static @NotNull List<ShortType> parse(@NotNull List<String> strings)
    {
        return Companion.parse(strings);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ShortType shortType)) return false;
        return value.equals(shortType.value) && Objects.equals(prefix, shortType.prefix);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value, prefix);
    }

    @Override
    public String toString()
    {
        return "ShortType(value=" + value + ", prefix=" + prefix + ")";
    }

    public static final class Companion
    {
        private Companion()
        {
        }

        public @NotNull ShortType from(@NotNull C3Type psi)
        {
            return new ShortType(psi.getText());
        }

        public @NotNull ShortType toShortType(@NotNull C3Type psi)
        {
            return from(psi);
        }

        public @NotNull ShortType parse(@NotNull String string)
        {
            return new ShortType(string);
        }

        public @NotNull List<ShortType> parse(@NotNull List<String> strings)
        {
            List<ShortType> result = new ArrayList<>(strings.size());
            for (String string : strings)
            {
                result.add(parse(string));
            }
            return result;
        }
    }
}
