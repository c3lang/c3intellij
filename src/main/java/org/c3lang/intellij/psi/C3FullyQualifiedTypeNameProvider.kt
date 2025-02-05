package org.c3lang.intellij.psi

interface C3FullyQualifiedTypeNameProvider : C3PsiElement {
    fun findTypeName() : FullyQualifiedName?
}