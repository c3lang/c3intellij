package org.c3lang.intellij.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.psi.AccessPath
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.C3StructMemberDeclaration
import org.c3lang.intellij.psi.FullyQualifiedName

object StructService {
    fun getStructMembers(
        query: String,
        project: Project
    ): List<C3StructMemberDeclaration> {
        val results = StubIndex.getElements(
            StructMemberDeclarationIndex.KEY,
            query,
            project,
            GlobalSearchScope.allScope(project),
            C3PsiElement::class.java
        )
        return results.filterIsInstance<C3StructMemberDeclaration>()
    }

    fun findStructMembers(
        query: String,
        project: Project
    ): List<C3StructMemberDeclaration> {
        val results = StubIndex.getInstance().getAllKeys(
            StructMemberDeclarationIndex.KEY,
            project,
        ).filter { it.startsWith(query) }.flatMap {
            getStructMembers(it, project)
        }

        return results
    }

    @Suppress("DuplicatedCode")
    fun getFields(
        rootType: FullyQualifiedName,
        path: List<String>,
        project: Project
    ): List<Pair<AccessPath, String>> {
        var query = rootType.fullName
        var fields = listOf<Pair<AccessPath, String>>()

        for (ident in path) {
            val structMembers = getStructMembers("$query.$ident", project)

            val member = structMembers.singleOrNull()

            val structFields = findStructMembers("$query.$ident", project)

            fields = structFields.mapNotNull {
                return@mapNotNull when (val structPath = it.structPath) {
                    is String -> AccessPath(structPath) to (it.type?.text ?: "")
                    else -> null
                }
            }

            val structPathType = member?.structPathType?.fullName
            query = structPathType
                ?: break
        }

        return fields.filter { (access, _) -> access.segments.size == query.count { it == '.' } + 1 }
    }

    @Suppress("DuplicatedCode")
    fun getStructMemberDeclaration(
        rootType: FullyQualifiedName,
        path: List<String>,
        project: Project
    ): List<C3StructMemberDeclaration> {
        var query = rootType.fullName
        var structMembers = listOf<C3StructMemberDeclaration>()

        for (ident in path) {
            structMembers = getStructMembers("$query.$ident", project)

            val member = structMembers.singleOrNull()

            val structPathType = member?.structPathType?.fullName
            query = structPathType
                ?: break
        }

        return structMembers
    }

    fun findStructMemberFields(
        query: String,
        project: Project
    ): List<C3StructMemberDeclaration> {
        val results = StubIndex.getInstance().getAllKeys(
            StructMemberDeclarationIndex.KEY,
            project,
        ).filter { it.startsWith("$query.") }.flatMap {
            getStructMembers(it, project)
        }

        return results
    }

    fun getStructMembers(
        type: FullyQualifiedName,
        name: String,
        project: Project
    ): List<C3StructMemberDeclaration> {
        return getStructMembers("${type.fullName}.$name", project)
    }

}