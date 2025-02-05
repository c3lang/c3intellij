package org.c3lang.intellij;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IStubFileElementType;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.stubs.C3FileStub;
import org.jetbrains.annotations.NotNull;

public class C3FileElementType extends IStubFileElementType<C3FileStub> {
    public static final C3FileElementType INSTANCE = new C3FileElementType();

    public static final int STUB_VERSION = 1;

    public C3FileElementType() {
        super("c3.FILE", C3Language.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected @NotNull StubElement<?> createStubForFile(@NotNull PsiFile file) {
                if (file instanceof C3File) {
                    return new C3FileStub((C3File) file);
                }
                return super.createStubForFile(file);
            }
        };
    }

//    @Override
//    public @NonNls @NotNull String getExternalId() {
//        return "c3.FILE";
//    }
//    @Override
//    public @NotNull C3FileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
//        return super.deserialize(dataStream, parentStub);
//    }
//
//    @Override
//    public void serialize(@NotNull C3FileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
//        super.serialize(stub, dataStream);
//    }
}
