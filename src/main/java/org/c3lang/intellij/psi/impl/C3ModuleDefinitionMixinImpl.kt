package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.*

abstract class C3ModuleDefinitionMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3ModuleDefinition {

    override val imports: List<ModuleName>
        get() = ModuleName.getImportList(this)
    override val importDeclarations: List<C3ImportDecl>
        get() = childrenOfType<C3TopLevel>().mapNotNull { it.importDecl }
    override val moduleName: ModuleName?
        get() = ModuleName.from(this)
    override val importPaths: List<C3ImportPath>
        get() = importDeclarations.flatMap { it.importPaths.importPathList }

    override fun containsImportOrSameModule(callable: C3FullyQualifiedNamePsiElement): Boolean {
        if (callable.moduleName == this.moduleName && callable.containingFile == this.containingFile) {
            return true
        }
        return imports.contains(callable.moduleName)
    }

    override fun contains(
        pathIdent: C3PathIdent
    ): Boolean {
        val elements = getImportOf(pathIdent)

        return elements.isNotEmpty()
    }

    override fun contains(path: C3Path): Boolean = path.parentOfType<C3PathIdent>()?.let { contains(it) } == true

    override fun getImportOf(pathIdent: C3PathIdent): List<C3ImportPath> = getImportOf(pathIdent.text)

    override fun getImportOf(pathIdentExpr: C3PathIdentExpr): List<C3ImportPath> = getImportOf(pathIdentExpr.text)

    private fun getImportOf(text: String): List<C3ImportPath> {
        val elements = NameIndexService.findByNameEndsWith(text, project).filter {
            it.fqName.suffixName == text
        }.mapNotNull { it.moduleName?.value }

        return importPaths.filter {
            elements.contains(it.moduleName?.value)
        }
    }

    override fun resolve(expr: C3PathIdentExpr): List<FullyQualifiedName> {
        val nameIdent = expr.text ?: return emptyList()
        expr.pathIdent.path ?: return listOfNotNull(FullyQualifiedName(null, nameIdent))

        val imports = getImportOf(expr)

        return imports.map {
            FullyQualifiedName(it.moduleName, nameIdent)
        }
    }

    override fun resolve(type: C3Type): List<FullyQualifiedName> {
        if (type.baseType.primitiveType) {
            return listOfNotNull(FullyQualifiedName(null, type.baseType.text))
        }

        if (type.baseType.path == null) {
            return listOfNotNull(FullyQualifiedName(moduleName, type.baseType.text))
        }

        val imports = importPaths.mapNotNull { it.moduleName }

        return NameIndexService.findByNameEndsWith(type.text, project).filter {
            it.fqName.fullName.endsWith(type.text)
        }.filter { imports.contains(it.moduleName) }.map { it.fqName }
    }

    override fun getImportPaths(moduleName: ModuleName): List<C3ImportPath> {
        return importPaths.filter { it.moduleName?.value == moduleName.value }
    }
}