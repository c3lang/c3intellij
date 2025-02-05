package org.c3lang.intellij.psi

interface C3PathIdentMixin :
    C3PsiNamedElement,
    C3NameIdentProvider,
    C3FullyQualifiedTypeNameProvider {

    fun findLocalDeclAfterType(): List<C3LocalDeclAfterType>
}