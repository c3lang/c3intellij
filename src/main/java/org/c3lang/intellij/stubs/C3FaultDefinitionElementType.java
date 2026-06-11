package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.c3lang.intellij.index.NameIndex;
import org.c3lang.intellij.psi.C3FaultDefinition;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3FaultDefinitionImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3FaultDefinitionElementType extends C3StubElementType<C3FaultDefinitionStub, C3FaultDefinition>
{
	private static final C3FaultDefinitionElementType INSTANCE = new C3FaultDefinitionElementType();

	private C3FaultDefinitionElementType()
	{
		super(C3StubElementTypeFactory.FAULT_DEFINITION);
	}

	public static C3FaultDefinitionElementType getInstance()
	{
		return INSTANCE;
	}

	@Override
	public @NotNull C3FaultDefinition createPsi(@NotNull C3FaultDefinitionStub stub)
	{
		return new C3FaultDefinitionImpl(stub, this);
	}

	@Override
	public @NotNull C3FaultDefinitionStub createStub(@NotNull C3FaultDefinition psi, @NotNull StubElement<? extends PsiElement> stubElement)
	{
		return new C3FaultDefinitionStub(stubElement, this, psi);
	}

	@Override
	public void serialize(@NotNull C3FaultDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException
	{
		stub.serialize(dataStream);
	}

	@Override
	public @NotNull C3FaultDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement stubElement) throws IOException
	{
		return new C3FaultDefinitionStub(stubElement, this, dataStream);
	}

	@Override
	public void indexStub(@NotNull C3FaultDefinitionStub stub, @NotNull IndexSink sink)
	{
		sink.occurrence(NameIndex.KEY, stub.getName().getFullName());
	}
}
