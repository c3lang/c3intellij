package org.c3lang.intellij.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShortType
{
    private final String value;
    private final @Nullable String prefix;
    private final @NotNull  String fullName;

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
		return new ShortType(psi.getText());
    }

    /**
     * Return type of a function/macro header, preserving the Optional suffix:
     * {@code fn int? foo()} gives {@code "int?"}, not {@code "int"}.
     * Never fails: stub building must not break on malformed headers,
     * otherwise the whole file ends up without a stub tree.
     */
    public static @NotNull ShortType fromOptionalType(@NotNull C3OptionalType psi)
    {
        C3Type inner = null;
        try
        {
            inner = psi.getType();
        }
        catch (Exception ignored)
        {
        }
        String text = inner != null && inner.getText() != null ? inner.getText().strip() : psi.getText().strip();
        String full = psi.getText() != null ? psi.getText().strip() : text;
        if ((full.endsWith("?") || full.endsWith("!")) && !text.endsWith("?") && !text.endsWith("!"))
        {
            text = text + full.substring(full.length() - 1);
        }
		return new ShortType(text);
    }

    public static @NotNull ShortType toShortType(@NotNull C3Type psi)
    {
        return from(psi);
    }

    public static @NotNull ShortType parse(@NotNull String string)
    {
		return new ShortType(string);
    }

    public static @NotNull List<ShortType> parse(@NotNull List<String> strings)
    {
		List<ShortType> result = new ArrayList<>(strings.size());
		for (String string : strings)
		{
			result.add(parse(string));
		}
		return result;
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

}
