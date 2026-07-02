package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3ConstDeclarationStmtStub extends StubBase<C3ConstDeclarationStmt>
{
	private final @NotNull FullyQualifiedName name;

	public C3ConstDeclarationStmtStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull FullyQualifiedName name)
	{
		super(parent, elementType);
		this.name = name;
	}

	public C3ConstDeclarationStmtStub(
		@NotNull StubElement<?> parent,
		@NotNull C3ConstDeclarationStmtElementType elementType,
		@NotNull C3ConstDeclarationStmt psi)
	{
		this(parent, elementType, FullyQualifiedName.from(psi, ModuleName.from(psi)));
	}

	public C3ConstDeclarationStmtStub(
		@NotNull StubElement<?> parent,
		@NotNull C3ConstDeclarationStmtElementType elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(parent, elementType, FullyQualifiedName.parse(dataStream.readUTFFast()));
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
