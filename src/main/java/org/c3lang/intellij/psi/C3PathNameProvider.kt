package org.c3lang.intellij.psi

interface C3PathNameProvider : C3PsiElement {
    fun findPathName(includeSelf: Boolean): List<String>
}