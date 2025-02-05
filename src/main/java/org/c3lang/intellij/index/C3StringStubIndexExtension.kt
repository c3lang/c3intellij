package org.c3lang.intellij.index

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StringStubIndexExtension

abstract class C3StringStubIndexExtension<T: PsiElement> : StringStubIndexExtension<T>() {
    override fun getVersion(): Int = 5
}