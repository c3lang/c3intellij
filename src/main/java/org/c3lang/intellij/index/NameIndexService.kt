package org.c3lang.intellij.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.psi.*

object NameIndexService {
    fun findByNameEndsWith(name: String, project: Project): Collection<C3FullyQualifiedNamePsiElement> {
        return StubIndex.getInstance().getAllKeys(NameIndex.KEY, project).filter { it.endsWith(name) }.flatMap {
            getElementsByName(it, project)
        }.filterIsInstance<C3FullyQualifiedNamePsiElement>()
    }

    fun findType(type: C3BaseType, project: Project): Collection<C3FullyQualifiedNamePsiElement> {
        val name = if (type.path == null) {
            listOfNotNull(
                type.moduleDefinition.moduleName?.value,
                type.text
            ).joinToString("::")
        } else {
            type.text
        }

        return StubIndex.getInstance().getAllKeys(NameIndex.KEY, project).filter { it.endsWith(name) }.flatMap {
            getElementsByName(it, project)
        }.filterIsInstance<C3FullyQualifiedNamePsiElement>()
    }

    private fun getElementsByName(
        string: String,
        project: Project
    ): Collection<C3PsiElement?> = StubIndex.getElements(
        NameIndex.KEY,
        string,
        project,
        GlobalSearchScope.allScope(project),
        C3PsiElement::class.java
    )
}