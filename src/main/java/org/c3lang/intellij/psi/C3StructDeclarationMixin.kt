package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3StructDeclarationStub

interface C3StructDeclarationMixin : C3PsiElement, C3DeclaredInProvider, StubBasedPsiElement<C3StructDeclarationStub> {
    val fields: List<StructField>
}