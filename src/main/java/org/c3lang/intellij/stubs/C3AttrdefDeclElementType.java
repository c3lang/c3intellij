package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.AttributeIndex;
import org.c3lang.intellij.psi.C3AttrdefDecl;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3AttrdefDeclImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3AttrdefDeclElementType extends C3StubElementType<C3AttrdefDeclStub, C3AttrdefDecl>
{
	public static final C3AttrdefDeclElementType INSTANCE = new C3AttrdefDeclElementType();

	private C3AttrdefDeclElementType()
	{
		super(C3StubElementTypeFactory.ATTRDEF_DECL);
	}

	public static C3AttrdefDeclElementType getInstance()
	{
		return INSTANCE;
	}

	@Override public @NotNull C3AttrdefDecl createPsi(@NotNull C3AttrdefDeclStub stub)
	{
		return new C3AttrdefDeclImpl(stub, this);
	}

	@Override public @NotNull C3AttrdefDeclStub createStub(
		@NotNull C3AttrdefDecl psi,
		@NotNull StubElement<? extends PsiElement> parentStub)
	{
		return new C3AttrdefDeclStub(parentStub, this, psi);
	}

	@Override public void serialize(@NotNull C3AttrdefDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override public @NotNull C3AttrdefDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		return new C3AttrdefDeclStub(parentStub, this, dataStream);
	}

	@Override public void indexStub(@NotNull C3AttrdefDeclStub stub, @NotNull IndexSink sink)
	{
		sink.occurrence(AttributeIndex.KEY, stub.getFqName().getFullName());
		sink.occurrence(AttributeIndex.KEY, stub.getName());
	}
}
