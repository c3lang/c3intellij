package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.StructDeclarationIndex;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3StructDeclarationImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3StructDeclarationElementType extends C3StubElementType<C3StructDeclarationStub, C3StructDeclaration>
{
	private static final C3StructDeclarationElementType INSTANCE = new C3StructDeclarationElementType();

	private C3StructDeclarationElementType()
	{
		super(C3StubElementTypeFactory.STRUCT_DECLARATION);
	}

	public static C3StructDeclarationElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3StructDeclaration createPsi(@NotNull C3StructDeclarationStub stub)
	{
		return new C3StructDeclarationImpl(stub, this);
	}

	@Override
	public @NotNull C3StructDeclarationStub createStub(@NotNull C3StructDeclaration psi, @NotNull StubElement<? extends PsiElement> parentStub)
	{
		return new C3StructDeclarationStub(parentStub, this, psi);
	}

	@Override
	public void serialize(@NotNull C3StructDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3StructDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		return new C3StructDeclarationStub(parentStub, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3StructDeclarationStub stub, @NotNull IndexSink sink)
	{
		sink.occurrence(StructDeclarationIndex.KEY, stub.getTypeName().getFullName());
	}
}
