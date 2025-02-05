package org.c3lang.intellij.psi

import org.c3lang.intellij.stubs.C3TypeEnum

interface C3TypeFullyQualifiedNamePsiElement : C3FullyQualifiedNamePsiElement {
    val typeEnum: C3TypeEnum
}