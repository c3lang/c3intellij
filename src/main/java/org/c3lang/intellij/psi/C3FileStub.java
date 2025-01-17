package org.c3lang.intellij.psi;

import com.intellij.psi.stubs.PsiFileStubImpl;

public class C3FileStub extends PsiFileStubImpl<C3File> {

    public C3FileStub(C3File file) {
        super(file);
    }
}
