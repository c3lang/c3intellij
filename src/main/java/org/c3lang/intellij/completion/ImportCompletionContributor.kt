package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.Project
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Util.findC3ModulesStartingWith
import org.c3lang.intellij.completion.CompletionUtil.provideCompletionsAfterSymbol
import org.c3lang.intellij.psi.C3ImportPath

@Suppress("DuplicatedCode")
object ImportCompletionContributor : CompletionProvider<CompletionParameters>()
{
    private val pattern = or(psiElement().inside(C3ImportPath::class.java))

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet)
    {
        if (!pattern.accepts(parameters.position) && !pattern.accepts(parameters.originalPosition))
        {
            return
        }

        val leaf = parameters.originalFile.findElementAt(parameters.offset)?.prevSibling

        provideCompletionsAfterSymbol(parameters, result, "::", getPossibleCompletions(parameters.editor.project!!, leaf?.text ?: "NONE"))
    }

    private fun getPossibleCompletions(project: Project, path: String): List<String>
    {
        return findC3ModulesStartingWith(project, path).toList().map { it.replace(path, "") }
    }
}
