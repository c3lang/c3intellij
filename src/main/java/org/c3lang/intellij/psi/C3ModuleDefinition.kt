package org.c3lang.intellij.psi

interface C3ModuleDefinition : C3ModuleNamePsiElement {
    val imports: List<ModuleName>
    val importDeclarations: List<C3ImportDecl>
    val importPaths: List<C3ImportPath>

    fun containsImportOrSameModule(callable: C3FullyQualifiedNamePsiElement): Boolean
    fun contains(pathIdent: C3PathIdent): Boolean
    fun contains(path: C3Path): Boolean
    fun getImportOf(pathIdent: C3PathIdent): List<C3ImportPath>
    fun getImportOf(pathIdentExpr: C3PathIdentExpr): List<C3ImportPath>
    fun resolve(pathIdent: C3PathIdentExpr): List<FullyQualifiedName>
    fun resolve(type: C3Type): List<FullyQualifiedName>
    fun getImportPaths(moduleName: ModuleName): List<C3ImportPath>
}