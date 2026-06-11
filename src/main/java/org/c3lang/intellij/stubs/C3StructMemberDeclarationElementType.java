package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.StructMemberDeclarationIndex;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3StructMemberDeclarationImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3StructMemberDeclarationElementType extends C3StubElementType<C3StructMemberDeclarationStub, C3StructMemberDeclaration>
{
	private static final C3StructMemberDeclarationElementType INSTANCE = new C3StructMemberDeclarationElementType();

	private C3StructMemberDeclarationElementType()
	{
		super(C3StubElementTypeFactory.STRUCT_MEMBER_DECLARATION);
	}

	public static C3StructMemberDeclarationElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3StructMemberDeclaration createPsi(@NotNull C3StructMemberDeclarationStub stub)
	{
		return new C3StructMemberDeclarationImpl(stub, this);
	}

	@Override
	public @NotNull C3StructMemberDeclarationStub createStub(@NotNull C3StructMemberDeclaration psi, @NotNull StubElement<? extends PsiElement> parentStub)
	{
		return new C3StructMemberDeclarationStub(parentStub, this, psi);
	}

	@Override
	public void serialize(@NotNull C3StructMemberDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3StructMemberDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		return new C3StructMemberDeclarationStub(parentStub, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3StructMemberDeclarationStub stub, @NotNull IndexSink sink)
	{
		String fullPath = stub.getFullPath();
		if (fullPath != null) sink.occurrence(StructMemberDeclarationIndex.KEY, fullPath);
	}
}
