package org.c3lang.intellij.psi

interface C3TypePsiElement : C3PsiElement {
    val sourceFileName: String
    val moduleName: ModuleName?
    val typeName: TypeName?
}