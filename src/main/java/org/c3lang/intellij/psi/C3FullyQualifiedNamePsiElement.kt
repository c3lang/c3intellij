package org.c3lang.intellij.psi

interface C3FullyQualifiedNamePsiElement : C3ModuleNamePsiElement {
    val fqName: FullyQualifiedName

    override val moduleName: ModuleName?
        get() = moduleDefinition.moduleName
}