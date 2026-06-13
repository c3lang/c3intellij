package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.DataInputOutputUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StubStreamExtensions
{
	private StubStreamExtensions() {}

	public static void writeNullableUTFFast(@NotNull StubOutputStream s, @Nullable String arg) throws IOException
	{
		DataInputOutputUtil.writeNullable(s, arg, s::writeUTFFast);
	}

	public static @Nullable String readNullableUTFFast(@NotNull StubInputStream s) throws IOException
	{
		return DataInputOutputUtil.readNullable(s, s::readUTFFast);
	}

	public static void writeList(@NotNull StubOutputStream s, @NotNull List<String> list) throws IOException
	{
		s.writeVarInt(list.size());
		for (String item : list)
		{
			s.writeUTFFast(item);
		}
	}

	public static @NotNull List<String> readList(@NotNull StubInputStream s) throws IOException
	{
		int size = s.readVarInt();
		List<String> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			result.add(s.readUTFFast());
		}
		return result;
	}
}
