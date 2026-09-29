package org.c3lang.intellij;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.roots.SyntheticLibrary;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class C3SyntheticLibrary extends SyntheticLibrary implements ItemPresentation
{
	private final String name;
	private final List<VirtualFile> sourceRoots;

	public C3SyntheticLibrary(@NotNull String name, @NotNull List<VirtualFile> sourceRoots)
	{
		this.name = name;
		this.sourceRoots = List.copyOf(sourceRoots);
	}

	@Override
	public @NotNull Collection<VirtualFile> getSourceRoots()
	{
		return sourceRoots;
	}

	@Override
	public @NotNull Collection<VirtualFile> getBinaryRoots()
	{
		return Collections.emptyList();
	}

	@Override
	public @NotNull Set<VirtualFile> getExcludedRoots()
	{
		return Collections.emptySet();
	}


	@Override
	public @Nullable String getPresentableText()
	{
		return name;
	}

	@Override
	public @Nullable String getLocationString()
	{
		if (sourceRoots.isEmpty()) return null;
		return sourceRoots.get(0).getPresentableUrl();
	}

	@Override
	public @Nullable Icon getIcon(boolean unused)
	{
		return C3Icons.FILE;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof C3SyntheticLibrary that)) return false;
		return Objects.equals(name, that.name) && Objects.equals(sourceRoots, that.sourceRoots);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, sourceRoots);
	}
}
