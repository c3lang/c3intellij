package org.c3lang.intellij.psi

interface C3ImportProvider : C3PsiElement {
    val moduleName: ModuleName?
    val imports: List<ModuleName>
    val importDeclarations: List<C3ImportDecl>
}