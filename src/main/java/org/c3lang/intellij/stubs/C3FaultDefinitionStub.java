package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3FaultDefinition;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3FaultDefinitionStub extends StubBase<C3FaultDefinition>
{
	private final @NotNull FullyQualifiedName name;

	public C3FaultDefinitionStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull FullyQualifiedName name)
	{
		super(parent, elementType);
		this.name = name;
	}

	public C3FaultDefinitionStub(
		@NotNull StubElement<?> parent,
		@NotNull C3FaultDefinitionElementType elementType,
		@NotNull C3FaultDefinition psi)
	{
		this(parent, elementType, FullyQualifiedName.Companion.from(psi, ModuleName.Companion.from(psi)));
	}

	public C3FaultDefinitionStub(
		@NotNull StubElement<?> parent,
		@NotNull C3FaultDefinitionElementType elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(parent, elementType, FullyQualifiedName.Companion.parse(dataStream.readUTFFast()));
	}

	public @NotNull FullyQualifiedName getName()
	{
		return name;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeUTFFast(name.getFullName());
	}
}
