package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.patterns.PlatformPatterns.or
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiComment
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.completion.CompletionUtil.provideCompletionsAfterSymbol
import org.c3lang.intellij.completion.CompletionUtil.provideCompletionsAfterSymbolWithInsertHandler
import org.c3lang.intellij.psi.C3File
import org.c3lang.intellij.psi.C3ModuleDefinition

object TopLevelCompletionContributor : CompletionProvider<CompletionParameters>()
{
    private val pattern = or(
        psiElement().withParent(C3File::class.java).andNot(psiElement().inside(PsiComment::class.java)),
        psiElement().withParent(C3ModuleDefinition::class.java).andNot(psiElement().inside(PsiComment::class.java))
    )

    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet)
    {
        if (!pattern.accepts(parameters.position) || !pattern.accepts(parameters.originalPosition))
        {
            return
        }

        provideCompletionsAfterSymbol(
            parameters,
            result,
            "",
            listOf(
                "fn",
                "struct",
                "fault",
                "macro",
                "def",
                "module",
                "import",
                "extern",
                "const",
                "tlocal",
                "bitstruct",
                "static initialize",
                "static finalize",
                "asm",
                "\$switch"
            )
        )

        provideCompletionsAfterSymbolWithInsertHandler(
            parameters,
            result,
            "",
            mapOf(
                "main" to InsertHandler<LookupElement> { insertionContext, _ ->
                    insertionContext.document.replaceString(
                        insertionContext.startOffset,
                        insertionContext.tailOffset,
                        """
                                            fn int main()
                                            {
                                                
                                            }
                                            """.trimIndent()
                    )

                    insertionContext.editor.caretModel.moveToOffset(insertionContext.tailOffset - 2)
                },
                "maina" to InsertHandler<LookupElement> { insertionContext, _ ->
                    insertionContext.document.replaceString(
                        insertionContext.startOffset,
                        insertionContext.tailOffset,
                        """
                                            fn int main(String[] args)
                                            {
                                                
                                            }
                                            """.trimIndent()
                    )

                    insertionContext.editor.caretModel.moveToOffset(insertionContext.tailOffset - 2)
                }
            )
        )
    }
}