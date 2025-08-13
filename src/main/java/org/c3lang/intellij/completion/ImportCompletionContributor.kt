package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Util.findC3ModulesStartingWith
import org.c3lang.intellij.psi.C3ImportPath

@Suppress("DuplicatedCode")
object ImportCompletionContributor : CompletionProvider<CompletionParameters>()
{
    private val pattern = or(psiElement().inside(C3ImportPath::class.java))

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet)
    {
        val matches = pattern.accepts(parameters.position)
        if (!matches)
        {
            return
        }

        val position = parameters.position
        val packagePathElement = position.parent

        // Example: "std::some::dummy"
        val fullTextWithDummy = packagePathElement.text
        val parentStartOffset = packagePathElement.textRange.startOffset
        val caretOffset = parameters.offset
        val prefixLength = caretOffset - parentStartOffset
        if (prefixLength < 0 || prefixLength > fullTextWithDummy.length) {
            return
        }

        val project = parameters.editor.project!!
        val prefix = fullTextWithDummy.take(prefixLength)
        val modules = findC3ModulesStartingWith(project, prefix)

        val resultSetWithPrefix = result.withPrefixMatcher(prefix)

        modules.forEach { module ->
            resultSetWithPrefix.addElement(LookupElementBuilder.create(module))
        }
    }
}
