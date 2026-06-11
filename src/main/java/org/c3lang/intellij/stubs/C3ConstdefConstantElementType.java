package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.psi.C3ConstdefConstant;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3ConstdefConstantImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3ConstdefConstantElementType extends C3StubElementType<C3ConstdefConstantStub, C3ConstdefConstant>
{
	private static final C3ConstdefConstantElementType INSTANCE = new C3ConstdefConstantElementType();

	private C3ConstdefConstantElementType()
	{
		super(C3StubElementTypeFactory.CONSTDEF_CONSTANT);
	}

	public static C3ConstdefConstantElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3ConstdefConstant createPsi(@NotNull C3ConstdefConstantStub stub)
	{
		return new C3ConstdefConstantImpl(stub, this);
	}

	@Override
	public @NotNull C3ConstdefConstantStub createStub(@NotNull C3ConstdefConstant psi, @NotNull StubElement<? extends PsiElement> stubElement)
	{
		return new C3ConstdefConstantStub(stubElement, this, psi);
	}

	@Override
	public void serialize(@NotNull C3ConstdefConstantStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3ConstdefConstantStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException
	{
		return new C3ConstdefConstantStub(stubElement, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3ConstdefConstantStub stub, @NotNull IndexSink sink)
	{
		// Intentionally not indexed yet.
	}
}
