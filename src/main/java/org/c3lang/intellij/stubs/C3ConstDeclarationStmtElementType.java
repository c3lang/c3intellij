package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3ConstDeclarationStmtImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3ConstDeclarationStmtElementType extends C3StubElementType<C3ConstDeclarationStmtStub, C3ConstDeclarationStmt>
{
	private static final C3ConstDeclarationStmtElementType INSTANCE = new C3ConstDeclarationStmtElementType();

	private C3ConstDeclarationStmtElementType()
	{
		super(C3StubElementTypeFactory.CONST_DECLARATION_STMT);
	}

	public static C3ConstDeclarationStmtElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3ConstDeclarationStmt createPsi(@NotNull C3ConstDeclarationStmtStub stub)
	{
		return new C3ConstDeclarationStmtImpl(stub, this);
	}

	@Override
	public @NotNull C3ConstDeclarationStmtStub createStub(@NotNull C3ConstDeclarationStmt psi, @NotNull StubElement<? extends PsiElement> stubElement)
	{
		return new C3ConstDeclarationStmtStub(stubElement, this, psi);
	}

	@Override
	public void serialize(@NotNull C3ConstDeclarationStmtStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3ConstDeclarationStmtStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException
	{
		return new C3ConstDeclarationStmtStub(stubElement, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3ConstDeclarationStmtStub stub, @NotNull IndexSink sink)
	{
		sink.occurrence(NameIndex.KEY, stub.getName().getFullName());
	}
}
