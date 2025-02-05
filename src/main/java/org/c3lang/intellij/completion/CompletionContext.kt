package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.psi.*

class CompletionContext(
    parameters: CompletionParameters
) {
    val project = parameters.originalFile.project
    val originalElement = CompletionUtil.getOriginalOrSelf(parameters.originalPosition ?: parameters.position)

    val importProvider = checkNotNull(
        originalElement.parentOfType<C3ImportProvider>()
    )

    val containingFile = importProvider.containingFile
    val range = originalElement.getTextRangeForCompletion(parameters.editor.caretModel)
    val lookupString = parameters.editor.document.getText(range)
}