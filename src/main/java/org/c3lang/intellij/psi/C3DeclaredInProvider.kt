package org.c3lang.intellij.psi

interface C3DeclaredInProvider : C3PsiElement {
    val declaredIn: FullyQualifiedName?
}