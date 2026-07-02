package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class C3MacroDefinitionStub extends StubBase<C3MacroDefinition>
{
	private final @NotNull String sourceFileName;
	private final @Nullable ModuleName module;
	private final @Nullable ShortType type;
	private final @NotNull FullyQualifiedName fqName;
	private final @Nullable ShortType returnType;
	private final @NotNull List<ParamType> parameterTypes;

	public C3MacroDefinitionStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull String sourceFileName,
		@Nullable ModuleName module,
		@Nullable ShortType type,
		@NotNull FullyQualifiedName fqName,
		@Nullable ShortType returnType,
		@NotNull List<ParamType> parameterTypes)
	{
		super(parent, elementType);
		this.sourceFileName = sourceFileName;
		this.module = module;
		this.type = type;
		this.fqName = fqName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}

	public C3MacroDefinitionStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull C3MacroDefinition psi)
	{
		this(
			parent,
			elementType,
			psi.getContainingFile().getName(),
			ModuleName.from(psi),
			psi.getMacroHeader().getMacroName().getType() != null
				? ShortType.from(psi.getMacroHeader().getMacroName().getType())
				: null,
			FullyQualifiedName.from(psi.getMacroHeader(), ModuleName.from(psi)),
			psi.getMacroHeader().getOptionalType() != null
				? ShortType.from(psi.getMacroHeader().getOptionalType().getType())
				: null,
			ParamType.toParamTypeList(
				psi.getMacroParams().getParameterList() != null
					? psi.getMacroParams().getParameterList().getParamDeclList()
					: null)
		);
	}

	public C3MacroDefinitionStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			dataStream.readUTFFast(),
			StubStreamExtensions.readModuleName(dataStream),
			StubStreamExtensions.readShortType(dataStream),
			FullyQualifiedName.parse(dataStream.readUTFFast()),
			StubStreamExtensions.readShortType(dataStream),
			ParamType.deserialize(dataStream)
		);
	}

	public @NotNull String getSourceFileName()
	{
		return sourceFileName;
	}

	public @Nullable ModuleName getModule()
	{
		return module;
	}

	public @Nullable ShortType getType()
	{
		return type;
	}

	public @NotNull FullyQualifiedName getFqName()
	{
		return fqName;
	}

	public @Nullable ShortType getReturnType()
	{
		return returnType;
	}

	public @NotNull List<ParamType> getParameterTypes()
	{
		return parameterTypes;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeUTFFast(sourceFileName);
		StubStreamExtensions.writeNullableUTFFast(dataStream, module != null ? module.getValue() : null);
		StubStreamExtensions.writeNullableUTFFast(dataStream, type != null ? type.getFullName() : null);
		dataStream.writeUTFFast(fqName.getFullName());
		StubStreamExtensions.writeNullableUTFFast(dataStream, returnType != null ? returnType.getFullName() : null);
		ParamType.serialize(dataStream, parameterTypes);
	}
}
