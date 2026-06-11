package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3EnumConstant;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3EnumConstantImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3EnumConstantElementType extends C3StubElementType<C3EnumConstantStub, C3EnumConstant>
{
	private static final C3EnumConstantElementType INSTANCE = new C3EnumConstantElementType();

	private C3EnumConstantElementType()
	{
		super(C3StubElementTypeFactory.ENUM_CONSTANT);
	}

	public static C3EnumConstantElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3EnumConstant createPsi(@NotNull C3EnumConstantStub stub)
	{
		return new C3EnumConstantImpl(stub, this);
	}

	@Override
	public @NotNull C3EnumConstantStub createStub(@NotNull C3EnumConstant psi, @NotNull StubElement<? extends PsiElement> stubElement)
	{
		return new C3EnumConstantStub(stubElement, this, psi);
	}

	@Override
	public void serialize(@NotNull C3EnumConstantStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3EnumConstantStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException
	{
		return new C3EnumConstantStub(stubElement, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3EnumConstantStub stub, @NotNull IndexSink sink)
	{
		// Intentionally not indexed yet.
	}
}
