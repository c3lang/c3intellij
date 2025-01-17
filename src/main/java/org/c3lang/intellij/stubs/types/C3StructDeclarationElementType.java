package org.c3lang.intellij.stubs.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.index.C3StructDeclarationIndex;
import org.c3lang.intellij.psi.C3PsiExtensionsKt;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3StructDeclarationImpl;
import org.c3lang.intellij.stubs.C3StructDeclarationStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class C3StructDeclarationElementType extends C3StubElementType<C3StructDeclarationStub, C3StructDeclaration> {
    public static final C3StructDeclarationElementType INSTANCE = new C3StructDeclarationElementType();

    public C3StructDeclarationElementType() {
        super(C3StubElementTypeFactory.STRUCT_DECLARATION);
    }

    @Override
    public C3StructDeclaration createPsi(@NotNull C3StructDeclarationStub stub) {
        return new C3StructDeclarationImpl(stub, this);
    }

    @Override
    public @NotNull C3StructDeclarationStub createStub(@NotNull C3StructDeclaration psi, StubElement<? extends PsiElement> parentStub) {
        return new C3StructDeclarationStub(parentStub, this, StringRef.fromString(psi.getTypeName().getText()));
    }

    @Override
    public void serialize(@NotNull C3StructDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public @NotNull C3StructDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        final var name = dataStream.readName();

        return new C3StructDeclarationStub(parentStub, this, name);
    }

    @Override
    public void indexStub(@NotNull C3StructDeclarationStub stub, @NotNull IndexSink sink) {
        Optional.ofNullable(stub.getName()).ifPresent(name -> {
            sink.occurrence(C3StructDeclarationIndex.KEY, name);
        });
    }

}
