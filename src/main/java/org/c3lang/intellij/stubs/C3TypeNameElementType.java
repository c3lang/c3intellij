package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.index.TypeIndex;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.impl.C3TypeNameImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3TypeNameElementType extends C3StubElementType<C3TypeNameStub, C3TypeName>
{
	private static final C3TypeNameElementType INSTANCE = new C3TypeNameElementType();

	private C3TypeNameElementType()
	{
		super(C3StubElementTypeFactory.TYPE_NAME);
	}

	public static C3TypeNameElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3TypeName createPsi(@NotNull C3TypeNameStub stub)
	{
		return new C3TypeNameImpl(stub, this);
	}

	@Override
	public @NotNull C3TypeNameStub createStub(@NotNull C3TypeName psi, @NotNull StubElement<? extends PsiElement> stubElement)
	{
		return new C3TypeNameStub(stubElement, this, psi);
	}

	@Override
	public void serialize(@NotNull C3TypeNameStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public void indexStub(@NotNull C3TypeNameStub stub, @NotNull IndexSink sink)
	{
		sink.occurrence(NameIndex.KEY, stub.getFqName().getFullName());
		sink.occurrence(TypeIndex.KEY, stub.getFqName().getFullName());
	}

	@Override
	public @NotNull C3TypeNameStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException
	{
		return new C3TypeNameStub(stubElement, this, dataStream);
	}
}
