package org.c3lang.intellij.stubs.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.index.C3ModuleIndex;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.C3PsiExtensionsKt;
import org.c3lang.intellij.psi.C3StubElementType;
import org.c3lang.intellij.psi.C3StubElementTypeFactory;
import org.c3lang.intellij.psi.impl.C3ModuleImpl;
import org.c3lang.intellij.stubs.C3ModuleStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class C3ModuleElementType extends C3StubElementType<C3ModuleStub, C3Module> {
    public static final C3ModuleElementType INSTANCE = new C3ModuleElementType();

    public C3ModuleElementType() {
        super(C3StubElementTypeFactory.MODULE);
    }

    @Override
    public C3Module createPsi(@NotNull C3ModuleStub stub) {
        return new C3ModuleImpl(stub, this);
    }

    @Override
    public @NotNull C3ModuleStub createStub(@NotNull C3Module psi, StubElement<? extends PsiElement> parentStub) {
        return new C3ModuleStub(parentStub, this, StringRef.fromString(C3PsiExtensionsKt.getModuleName(psi)));
    }

    @Override
    public void serialize(@NotNull C3ModuleStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public @NotNull C3ModuleStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        final var name = dataStream.readName();

        return new C3ModuleStub(parentStub, this, name);
    }

    @Override
    public void indexStub(@NotNull C3ModuleStub stub, @NotNull IndexSink sink) {
        final var name = stub.getName();
        if (name != null) {
            sink.occurrence(C3ModuleIndex.KEY, name);
        }
    }


}
