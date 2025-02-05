package org.c3lang.intellij.psi

interface C3DeclaredInPathProvider : C3PsiElement {
    val declaredInPath: String?
}