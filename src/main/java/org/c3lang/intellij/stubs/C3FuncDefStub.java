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

public class C3FuncDefStub extends StubBase<C3FuncDef>
{
	private final @NotNull String sourceFileName;
	private final @Nullable ModuleName module;
	private final @Nullable ShortType type;
	private final @NotNull FullyQualifiedName fqName;
	private final @Nullable ShortType returnType;
	private final @NotNull List<ParamType> parameterTypes;
	private final @Nullable String conditionKey;
	private final boolean isPrivate;

	public C3FuncDefStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull String sourceFileName,
		@Nullable ModuleName module,
		@Nullable ShortType type,
		@NotNull FullyQualifiedName fqName,
		@Nullable ShortType returnType,
		@NotNull List<ParamType> parameterTypes,
		@Nullable String conditionKey,
		boolean isPrivate)
	{
		super(parent, elementType);
		this.sourceFileName = sourceFileName;
		this.module = module;
		this.type = type;
		this.fqName = fqName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.conditionKey = conditionKey;
		this.isPrivate = isPrivate;
	}

	public C3FuncDefStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull C3FuncDef psi)
	{
		this(
			parent,
			elementType,
			psi.getContainingFile().getName(),
			ModuleName.from(psi),
			psi.getFuncHeader().getFuncName().getType() != null
				? ShortType.from(psi.getFuncHeader().getFuncName().getType())
				: null,
			FullyQualifiedName.from(psi.getFuncHeader(), ModuleName.from(psi)),
			ShortType.fromOptionalType(psi.getFuncHeader().getOptionalType()),
			ParamType.toParamTypeList(
				psi.getFnParameterList().getParameterList() != null
					? psi.getFnParameterList().getParameterList().getParamDeclList()
					: null),
			ConditionalGating.conditionKey(psi),
			StubPrivacy.computeFlag(psi)
		);
	}

	public C3FuncDefStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(
			parent,
			elementType,
			dataStream.readUTFFast(),
			StubStreamExtensions.readModuleName(dataStream),
			readShortType(dataStream),
			FullyQualifiedName.parse(dataStream.readUTFFast()),
			readShortType(dataStream),
			ParamType.deserialize(dataStream),
			StubStreamExtensions.readNullableUTFFast(dataStream),
			dataStream.readBoolean()
		);
	}

	private static @Nullable ShortType readShortType(@NotNull StubInputStream dataStream) throws IOException
	{
		String value = StubStreamExtensions.readNullableUTFFast(dataStream);
		return value != null ? ShortType.parse(value) : null;
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

	public @Nullable String getConditionKey()
	{
		return conditionKey;
	}

	public boolean isPrivate()
	{
		return isPrivate;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		dataStream.writeUTFFast(sourceFileName);
		StubStreamExtensions.writeNullableUTFFast(dataStream, module != null ? module.getValue() : null);
		StubStreamExtensions.writeNullableUTFFast(dataStream, type != null ? type.getFullName() : null);
		dataStream.writeUTFFast(fqName.getFullName());
		StubStreamExtensions.writeNullableUTFFast(dataStream, returnType != null ? returnType.getFullName() : null);
		ParamType.serialize(dataStream, parameterTypes);
		StubStreamExtensions.writeNullableUTFFast(dataStream, conditionKey);
		dataStream.writeBoolean(isPrivate);
	}
}
