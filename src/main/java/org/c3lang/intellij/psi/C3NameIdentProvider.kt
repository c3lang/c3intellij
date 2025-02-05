package org.c3lang.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement

interface C3NameIdentProvider: C3PsiElement {
    val nameIdent: String?
    val nameIdentElement: LeafPsiElement?
}