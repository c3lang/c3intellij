package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class C3StructMemberDeclarationStub extends StubBase<C3StructMemberDeclaration>
{
	private final @Nullable FullyQualifiedName structType;
	private final @Nullable String structPath;
	private final @Nullable FullyQualifiedName structPathType;
	private final @Nullable String fullPath;

	public C3StructMemberDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@Nullable FullyQualifiedName structType,
		@Nullable String structPath,
		@Nullable FullyQualifiedName structPathType)
	{
		super(parent, elementType);
		this.structType = structType;
		this.structPath = structPath;
		this.structPathType = structPathType;
		this.fullPath = buildFullPath(structType, structPath);
	}

	public C3StructMemberDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull C3StructMemberDeclaration psi)
	{
		this(parent, elementType, psi.getStructType(), psi.getStructPath(), psi.getStructPathType());
	}

	public C3StructMemberDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			readFullyQualifiedName(dataStream),
			StubStreamExtensions.readNullableUTFFast(dataStream),
			readFullyQualifiedName(dataStream)
		);
	}

	private static @Nullable FullyQualifiedName readFullyQualifiedName(@NotNull StubInputStream dataStream) throws IOException
	{
		String value = StubStreamExtensions.readNullableUTFFast(dataStream);
		return value != null ? FullyQualifiedName.parse(value) : null;
	}

	private static @Nullable String buildFullPath(@Nullable FullyQualifiedName structType, @Nullable String structPath)
	{
		List<String> parts = new ArrayList<>(2);
		if (structType != null) parts.add(structType.getFullName());
		if (structPath != null) parts.add(structPath);
		return parts.isEmpty() ? null : String.join(".", parts);
	}

	public @Nullable FullyQualifiedName getStructType()
	{
		return structType;
	}

	public @Nullable String getStructPath()
	{
		return structPath;
	}

	public @Nullable FullyQualifiedName getStructPathType()
	{
		return structPathType;
	}

	public @Nullable String getFullPath()
	{
		return fullPath;
	}

	public void serialize(@NotNull StubOutputStream stream) throws IOException
	{
		StubStreamExtensions.writeNullableUTFFast(stream, structType != null ? structType.getFullName() : null);
		StubStreamExtensions.writeNullableUTFFast(stream, structPath);
		StubStreamExtensions.writeNullableUTFFast(stream, structPathType != null ? structPathType.getFullName() : null);
	}
}
