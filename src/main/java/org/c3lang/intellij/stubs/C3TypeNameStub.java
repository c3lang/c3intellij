package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3TypeNameStub extends StubBase<C3TypeName>
{
	private final @NotNull FullyQualifiedName fqName;
	private final @Nullable ModuleName moduleName;
	private final @NotNull C3TypeEnum typeEnum;

	public C3TypeNameStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull FullyQualifiedName fqName,
		@Nullable ModuleName moduleName,
		@NotNull C3TypeEnum typeEnum)
	{
		super(parent, elementType);
		this.fqName = fqName;
		this.moduleName = moduleName;
		this.typeEnum = typeEnum;
	}

	public C3TypeNameStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			FullyQualifiedName.parse(dataStream.readUTFFast()),
			StubStreamExtensions.readModuleName(dataStream),
			C3TypeEnum.valueOf(dataStream.readUTFFast())
		);
	}

	public C3TypeNameStub(
		@NotNull StubElement<?> parent,
		@NotNull C3TypeNameElementType elementType,
		@NotNull C3TypeName psi)
	{
		this(
			parent,
			elementType,
			FullyQualifiedName.from(psi, psi.getModuleName()),
			ModuleName.from(psi),
			C3TypeEnum.find(psi)
		);
	}

	public @NotNull FullyQualifiedName getFqName()
	{
		return fqName;
	}

	public @Nullable ModuleName getModuleName()
	{
		return moduleName;
	}

	public @NotNull C3TypeEnum getTypeEnum()
	{
		return typeEnum;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeUTFFast(fqName.getFullName());
		StubStreamExtensions.writeNullableUTFFast(dataStream, moduleName != null ? moduleName.getValue() : null);
		dataStream.writeUTFFast(typeEnum.name());
	}
}
