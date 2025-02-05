package org.c3lang.intellij.psi

import com.intellij.psi.util.parentOfType

interface C3ModuleNamePsiElement : C3PsiElement {
    val moduleName: ModuleName?

    fun isSameModule(other: C3FullyQualifiedNamePsiElement): Boolean {
        return this.containingFile.name == other.containingFile.name && moduleName == other.moduleName
    }

    fun isImported(other: C3FullyQualifiedNamePsiElement): Boolean {
        val moduleDefinition = checkNotNull(parentOfType<C3ModuleDefinition>(true))

        return other.moduleDefinition == moduleDefinition || moduleDefinition.imports.contains(other.moduleName)
    }

    fun textToInsert(imported: ModuleName?, element: C3FullyQualifiedNamePsiElement): String = when {
        isSameModule(element) || imported == null -> element.fqName.name
        isImported(element) || imported == element.moduleName -> element.fqName.suffixName
        else -> element.fqName.fullName
    }
}