package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.PsiFileStubImpl
import org.c3lang.intellij.psi.C3File

class C3FileStub : PsiFileStubImpl<C3File> {

    constructor(file: C3File) : super(file)

}