package org.c3lang.intellij.psi

interface C3CallablePsiElement : C3FullyQualifiedNamePsiElement {
    val sourceFileName: String
    val type: ShortType?
    val returnType: ShortType?
    val parameterTypes: List<ParamType>
}