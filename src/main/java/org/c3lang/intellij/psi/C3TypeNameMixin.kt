package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3TypeNameStub

interface C3TypeNameMixin :
    StubBasedPsiElement<C3TypeNameStub>,
    C3PsiNamedElement,
    C3NameIdentProvider,
    C3TypeFullyQualifiedNamePsiElement