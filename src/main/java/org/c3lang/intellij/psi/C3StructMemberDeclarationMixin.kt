package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3StructMemberDeclarationStub

interface C3StructMemberDeclarationMixin : StubBasedPsiElement<C3StructMemberDeclarationStub>,
    C3PsiNamedElement,
    C3DeclaredInPathProvider,
    C3DeclaredInProvider,
    C3NameIdentProvider,
    C3FullyQualifiedTypeNameProvider {
    val structType: FullyQualifiedName?
    val structPath: String?
    val structPathType: FullyQualifiedName?

    override fun findTypeName(): FullyQualifiedName? {
        return structPathType
    }
}
