package org.c3lang.intellij;

import org.jetbrains.annotations.NotNull;

public record CompilerOption(@NotNull String name, @NotNull String binaryPath, @NotNull String stdlibPath)
{
	@Override public @NotNull String toString()
	{
		return name;
	}
}