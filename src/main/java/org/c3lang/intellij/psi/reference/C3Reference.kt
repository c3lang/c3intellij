package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiPolyVariantReference
import org.c3lang.intellij.psi.C3PsiElement

interface C3Reference : PsiPolyVariantReference {

    override fun resolve(): C3PsiElement?

    override fun getElement(): C3PsiElement

    fun multiResolve(): Collection<C3PsiElement>
}