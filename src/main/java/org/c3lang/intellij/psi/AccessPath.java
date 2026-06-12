package org.c3lang.intellij.psi;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class AccessPath
{
    private final List<String> segments;
    private final String name;
    private final String path;

    public AccessPath(@NotNull String path)
    {
        this(Arrays.asList(path.split("\\.")));
    }

    public AccessPath(@NotNull List<String> segments)
    {
        if (segments.isEmpty())
        {
            throw new IllegalArgumentException("segments must not be empty");
        }
        this.segments = List.copyOf(segments);
        this.name = this.segments.get(this.segments.size() - 1);
        this.path = String.join(".", this.segments);
    }

    @NotNull
    public List<String> getSegments()
    {
        return segments;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getPath()
    {
        return path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AccessPath that)) return false;
        return segments.equals(that.segments);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(segments);
    }

    @Override
    public String toString()
    {
        return "AccessPath(segments=" + segments + ")";
    }
}
