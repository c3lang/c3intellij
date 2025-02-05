package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3EnumConstantStub

interface C3EnumConstantMixin :
    StubBasedPsiElement<C3EnumConstantStub>,
    C3PsiNamedElement,
    C3NameIdentProvider,
    C3TypeFullyQualifiedNamePsiElement {
    val constIdent: String
}
