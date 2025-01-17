package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.index.C3Indexes
import org.c3lang.intellij.psi.*

class StructCompletionContributor : CompletionContributor() {
    init {
        val pattern = PlatformPatterns.or(
            PlatformPatterns.psiElement().inside(C3Statement::class.java)
        )

        extend(CompletionType.BASIC, pattern, object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: ProcessingContext,
                result: CompletionResultSet
            ) {
                val originalElement = CompletionUtil.getOriginalOrSelf(parameters.position)

                if (PsiTreeUtil.getParentOfType(originalElement, C3Statement::class.java) == null) {
                    return
                }

                val source = checkNotNull(
                    PsiTreeUtil.getParentOfType(
                        originalElement,
                        C3PathIdentExpr::class.java,
                        C3Statement::class.java
                    )
                )

                val containingFile = source.containingFile;

                val range =  originalElement.getTextRange(source)
                val text = parameters.editor.document.getText(range).trim().trim(';')
                val query = FunctionLookupElement.Query("*$text*")

                C3Indexes.findStructOrUnion(
                    query,
                    parameters.originalFile.project,
                    GlobalSearchScope.allScope(parameters.originalFile.project)
                ).forEach {
                    val bonus = if (it.result.containingFile == containingFile) {
                        0.1
                    } else {
                        0.0
                    }
                    val structDeclaration = it.result;

                    val lookupElementBuilder = LookupElementBuilder
                        .create(it.result, structDeclaration.getStructDeclarationFullName())
                        .withIcon(AllIcons.Nodes.ModelClass)
//                        .withInsertHandler(insertHandler)


                    val priority = it.degree + (1.0 + bonus)
                    result.addElement(
                        PrioritizedLookupElement.withPriority(
                            lookupElementBuilder./*DEBUG*/appendTailText(" Struct $priority", false),
                            priority
                        )
                    )
                }
            }
        })
    }

    private class StructInsertHandler() : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {

        }

    }

    companion object {
        private val log = Logger.getInstance(
            StructCompletionContributor::class.java.name
        )
    }
}
