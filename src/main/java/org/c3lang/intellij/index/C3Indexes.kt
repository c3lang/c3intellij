package org.c3lang.intellij.index

import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.refactoring.util.TextOccurrencesUtil
import org.c3lang.intellij.completion.FunctionLookupElement
import org.c3lang.intellij.psi.C3FuncDef
import org.c3lang.intellij.psi.C3StructDeclaration

object C3Indexes {

    fun findFunctionsWithModuleSuffix(
        query: FunctionLookupElement.Query,
        project: Project,
        scope: GlobalSearchScope
    ): Collection<QueryResult<C3FuncDef>> {
        val nameMatcher = NameUtil.buildMatcher(query.text)
            .preferringStartMatches()
            .withCaseSensitivity(NameUtil.MatchingCaseSensitivity.NONE)
            .build()

        return StubIndex.getInstance().getAllKeys(
            C3FunctionSuffixNameIndex.KEY,
            project,
        ).filter { nameMatcher.pattern.isBlank() || nameMatcher.matches(it) }.map {
            QueryResult(
                it, nameMatcher, StubIndex.getElements(
                    C3FunctionSuffixNameIndex.KEY,
                    it,
                    project,
                    scope,
                    C3FuncDef::class.java
                )
            )
        }.flatMap { match ->
            match.result.map { QueryResult(match.degree, it) }
        }
    }

    fun findFunctionsWithModuleName(
        query: FunctionLookupElement.Query,
        project: Project,
        scope: GlobalSearchScope
    ): Collection<QueryResult<C3FuncDef>> {
        val nameMatcher = NameUtil.buildMatcher(query.text, NameUtil.MatchingCaseSensitivity.NONE)

        return StubIndex.getInstance().getAllKeys(
            C3FunctionLongNameIndex.KEY,
            project,
        ).filter { nameMatcher.pattern.isBlank() || nameMatcher.matches(it) }.map {
            QueryResult(
                it, nameMatcher, StubIndex.getElements(
                    C3FunctionLongNameIndex.KEY,
                    it,
                    project,
                    scope,
                    C3FuncDef::class.java
                )
            )
        }.flatMap { match ->
            match.result.map { QueryResult(match.degree, it) }
        }
    }

    fun findFunctionsByName(
        query: FunctionLookupElement.Query,
        project: Project,
        scope: GlobalSearchScope
    ): Collection<QueryResult<C3FuncDef>> {
        val nameMatcher = NameUtil.buildMatcher(query.text, NameUtil.MatchingCaseSensitivity.NONE)

        return StubIndex.getInstance().getAllKeys(
            C3FunctionShortNameIndex.KEY,
            project,
        ).filter { nameMatcher.pattern.isBlank() || nameMatcher.matches(it) }.map {
            QueryResult(
                it,
                nameMatcher,
                StubIndex.getElements(C3FunctionShortNameIndex.KEY, it, project, scope, C3FuncDef::class.java)
            )
        }.flatMap { match ->
            match.result.map { QueryResult(match.degree, it) }
        }
    }

    fun findStructOrUnion(
        query: FunctionLookupElement.Query,
        project: Project,
        scope: GlobalSearchScope
    ) : Collection<QueryResult<C3StructDeclaration>> {
        val nameMatcher = NameUtil.buildMatcher(query.text, NameUtil.MatchingCaseSensitivity.NONE)

        return StubIndex.getInstance().getAllKeys(
            C3StructDeclarationIndex.KEY,
            project,
        ).filter { nameMatcher.pattern.isBlank() || nameMatcher.matches(it) }.map {
            QueryResult(
                it,
                nameMatcher,
                StubIndex.getElements(C3StructDeclarationIndex.KEY, it, project, scope, C3StructDeclaration::class.java)
            )
        }.flatMap { match ->
            match.result.map { QueryResult(match.degree, it) }
        }
    }

    data class QueryResult<T>(
        val degree: Int,
        val result: T
    ) {
        constructor(
            key: String,
            matcher: MinusculeMatcher,
            result: T
        ) : this(
            matcher.matchingDegree(key),
            result
        )

    }
}