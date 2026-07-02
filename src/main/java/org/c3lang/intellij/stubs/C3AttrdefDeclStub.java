package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.*;
import org.c3lang.intellij.psi.C3AttrdefDecl;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3AttrdefDeclStub extends StubBase<C3AttrdefDecl>
{
	private final @NotNull FullyQualifiedName fqName;
	private final @Nullable ModuleName moduleName;

	public C3AttrdefDeclStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull FullyQualifiedName name,
		@Nullable ModuleName moduleName)
	{
		super(parent, elementType);
		this.fqName = name;
		this.moduleName = moduleName;
	}

	public C3AttrdefDeclStub(
		@NotNull StubElement<?> parent,
		@NotNull C3AttrdefDeclElementType elementType,
		@NotNull C3AttrdefDecl psi)
	{
		this(parent, elementType,
			 new FullyQualifiedName(ModuleName.from(psi), psi.getAttributeUserName().getText()),
			 ModuleName.from(psi)
		);
	}

	public C3AttrdefDeclStub(
		@NotNull StubElement<?> parent,
		@NotNull C3AttrdefDeclElementType elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(parent, elementType, FullyQualifiedName.parse(dataStream.readUTFFast()), StubStreamExtensions.readModuleName(dataStream));
	}

	public @NotNull FullyQualifiedName getFqName()
	{
		return fqName;
	}
	public @Nullable ModuleName getModuleName()
	{
		return moduleName;
	}
	public @NotNull String getName()
	{
		return fqName.getName();
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeUTFFast(fqName.getFullName());
		StubStreamExtensions.writeNullableUTFFast(dataStream, moduleName != null ? moduleName.getValue() : null);
	}
}
