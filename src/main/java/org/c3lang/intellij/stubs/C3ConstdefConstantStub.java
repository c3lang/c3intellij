package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3ConstdefConstant;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3ConstdefConstantStub extends StubBase<C3ConstdefConstant>
{
	private final @Nullable ModuleName module;
	private final @NotNull FullyQualifiedName fqName;
	private final @NotNull String constIdent;

	public C3ConstdefConstantStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@Nullable ModuleName module,
		@NotNull FullyQualifiedName fqName,
		@NotNull String constIdent)
	{
		super(parent, elementType);
		this.module = module;
		this.fqName = fqName;
		this.constIdent = constIdent;
	}

	public C3ConstdefConstantStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull C3ConstdefConstant psi)
	{
		this(parent, elementType, ModuleName.from(psi), psi.getFqName(), psi.getConstIdent());
	}

	public C3ConstdefConstantStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			StubStreamExtensions.readModuleName(dataStream),
			FullyQualifiedName.parse(dataStream.readUTFFast()),
			dataStream.readUTFFast()
		);
	}

	public @Nullable ModuleName getModule()
	{
		return module;
	}

	public @NotNull FullyQualifiedName getFqName()
	{
		return fqName;
	}

	public @NotNull String getConstIdent()
	{
		return constIdent;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		StubStreamExtensions.writeNullableUTFFast(dataStream, module != null ? module.getValue() : null);
		dataStream.writeUTFFast(fqName.getFullName());
		dataStream.writeUTFFast(constIdent);
	}
}
