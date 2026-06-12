package org.c3lang.intellij.psi;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class FullyQualifiedPath
{
    private final FullyQualifiedName typeName;
    private final String path;

    public FullyQualifiedPath(@NotNull FullyQualifiedName typeName, @NotNull String path)
    {
        this.typeName = typeName;
        this.path = path;
    }

    @NotNull
    public FullyQualifiedName getTypeName()
    {
        return typeName;
    }

    @NotNull
    public String getPath()
    {
        return path;
    }

    @NotNull
    public String getFullName()
    {
        return typeName.getFullName() + "." + path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FullyQualifiedPath that)) return false;
        return typeName.equals(that.typeName) && path.equals(that.path);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(typeName, path);
    }

    @Override
    public String toString()
    {
        return "FullyQualifiedPath(typeName=" + typeName + ", path=" + path + ")";
    }
}
