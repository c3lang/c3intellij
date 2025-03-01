package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.completion.PsiDocumentUtils.addImport
import org.c3lang.intellij.completion.PsiDocumentUtils.commitChanges
import org.c3lang.intellij.index.C3Indexes
import org.c3lang.intellij.psi.*

class FunctionCompletionContributor : CompletionContributor() {
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
                val project = parameters.originalFile.project
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

                val moduleSection = PsiTreeUtil.getParentOfType(source, C3ModuleSection::class.java)
                    ?: checkNotNull(
                        PsiTreeUtil.getParentOfType(
                            source,
                            C3DefaultModuleSection::class.java
                        )
                    )
                val containingFile = moduleSection.containingFile;

                val range =  originalElement.getTextRange(source)
                val text = parameters.editor.document.getText(range)
                val query = FunctionLookupElement.Query("*$text*")

                C3Indexes.findFunctionsWithModuleSuffix(query, project, GlobalSearchScope.allScope(project))
                    .forEach {
                        val bonus = if (it.result.containingFile == containingFile) {
                            0.1
                        } else {
                            0.0
                        }

                        val priority = it.degree + (1.0 + bonus)
                        result.addElement(
                            PrioritizedLookupElement.withPriority(
                                FunctionLookupElement.buildFunctionLookupElement(
                                    it.result,
                                    FunctionInsertHandler(moduleSection, range)
                                ),
                                priority
                            )
                        )
                    }

                C3Indexes.findFunctionsByName(query, project, GlobalSearchScope.allScope(project)).forEach {
                    val bonus = if (PsiTreeUtil.isAncestor(moduleSection, it.result, false)) {
                        0.1
                    } else {
                        0.0
                    }

                    val priority = it.degree * (2.0 + bonus)
                    result.addElement(
                        PrioritizedLookupElement.withPriority(
                            FunctionLookupElement.buildFunctionLookupElement(
                                it.result,
                                FunctionInsertHandler(moduleSection, range)
                            ),
                            priority
                        )
                    )
                }

                C3Indexes.findFunctionsWithModuleName(query, project, GlobalSearchScope.allScope(project))
                    .forEach {
                        val priority = it.degree * 0.5
                        result.addElement(
                            PrioritizedLookupElement.withPriority(
                                FunctionLookupElement.buildFunctionLookupElement(
                                    it.result,
                                    FunctionInsertHandler(moduleSection, range)
                                ),
                                priority
                            )
                        )
                    }

            }
        })
    }

    private class FunctionInsertHandler(
        private val moduleSection: PsiElement,
        private val range: TextRange
    ) :
        InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val func = item.getObject() as C3FuncDef
            val textToInsert = func.getFunctionName(moduleSection) + "()"
            val endOffset = editor.caretModel.offset

            document.replaceString(
                range.startOffset,
                endOffset,
                textToInsert
            )
            document.commitChanges(context)
            editor.caretModel.moveToOffset(range.startOffset + textToInsert.length - 1)

            moduleSection.addImport(context, func)
        }

        override fun toString(): String {
            return "FunctionInsertHandler[" +
                    "range=" + range + ']'
        }
    }

    companion object {
        private val log = Logger.getInstance(
            FunctionCompletionContributor::class.java.name
        )
    }

}
