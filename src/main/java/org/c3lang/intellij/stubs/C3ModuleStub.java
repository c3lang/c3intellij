package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class C3ModuleStub extends StubBase<C3Module>
{
	private final @Nullable ModuleName module;

	public C3ModuleStub(
		@Nullable StubElement<?> parent,
		@Nullable IStubElementType<?, ?> elementType,
		@Nullable ModuleName module)
	{
		super(parent, elementType);
		this.module = module;
	}

	public C3ModuleStub(
		@NotNull StubElement<? extends PsiElement> parent,
		@NotNull C3ModuleElementType elementType,
		@NotNull C3Module psi)
	{
		this(parent, elementType, ModuleName.from(psi));
	}

	public C3ModuleStub(
		@NotNull StubElement<?> parent,
		@NotNull C3ModuleElementType elementType,
		@NotNull StubInputStream dataStream) throws IOException
	{
		this(parent, elementType, StubStreamExtensions.readModuleName(dataStream));
	}

	public @Nullable ModuleName getModule()
	{
		return module;
	}

	public void serialize(@NotNull StubOutputStream dataStream) throws IOException
	{
		StubStreamExtensions.writeNullableUTFFast(dataStream, module != null ? module.getValue() : null);
	}
}
