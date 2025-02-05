package org.c3lang.intellij.index

import com.intellij.psi.stubs.StubIndexKey
import org.c3lang.intellij.psi.C3PsiElement

class StructMemberDeclarationIndex : C3StringStubIndexExtension<C3PsiElement>() {
    override fun getKey(): StubIndexKey<String, C3PsiElement> = KEY

    companion object {
        @JvmStatic
        val KEY: StubIndexKey<String, C3PsiElement> = StubIndexKey.createIndexKey("c3.struct_member_declaration")
    }
}