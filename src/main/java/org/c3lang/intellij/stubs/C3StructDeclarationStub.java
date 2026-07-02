package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3StructBody;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.StructField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class C3StructDeclarationStub extends StubBase<C3StructDeclaration>
{
	private final @NotNull FullyQualifiedName typeName;
	private final @NotNull List<StructField> fields;

	public C3StructDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull FullyQualifiedName typeName,
		@NotNull List<StructField> fields)
	{
		super(parent, elementType);
		this.typeName = typeName;
		this.fields = fields;
	}

	public C3StructDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull C3StructDeclaration psi)
	{
		this(
			parent,
			elementType,
			FullyQualifiedName.from(psi.getTypeName(), psi.getTypeName().getModuleName()),
			collectFields(psi)
		);
	}

	public C3StructDeclarationStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			FullyQualifiedName.parse(dataStream.readUTFFast()),
			deserializeStructFields(dataStream)
		);
	}

	private static @NotNull List<StructField> collectFields(@NotNull C3StructDeclaration psi)
	{
		C3StructBody structBody = psi.getStructBody();
		return structBody != null ? StructField.collectFields(structBody, null) : Collections.emptyList();
	}

	private static @NotNull List<StructField> deserializeStructFields(@NotNull StubInputStream dataStream) throws IOException
	{
		int fieldCount = Integer.parseInt(dataStream.readUTFFast());
		List<StructField> result = new ArrayList<>(fieldCount);

		for (int i = 0; i < fieldCount; i++)
		{
			String typeFullName = dataStream.readUTFFast();
			String name = StubStreamExtensions.readNullableUTFFast(dataStream);
			if (name == null) throw new IllegalStateException("Struct field name missing");
			result.add(new StructField(name, FullyQualifiedName.parse(typeFullName)));
		}

		return result;
	}

	public @NotNull FullyQualifiedName getTypeName()
	{
		return typeName;
	}

	public @NotNull List<StructField> getFields()
	{
		return fields;
	}

	public void serialize(@NotNull StubOutputStream stream) throws IOException
	{
		stream.writeUTFFast(typeName.getFullName());
		stream.writeUTFFast(Integer.toString(fields.size()));
		for (StructField field : fields)
		{
			stream.writeUTFFast(field.getType().getFullName());
			StubStreamExtensions.writeNullableUTFFast(stream, field.getName());
		}
	}
}
