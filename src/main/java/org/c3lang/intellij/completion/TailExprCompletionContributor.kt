package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.index.StructService
import org.c3lang.intellij.psi.C3AccessIdent
import org.c3lang.intellij.psi.C3CallExprTail
import org.c3lang.intellij.psi.C3ExprStmt
import org.c3lang.intellij.psi.C3Types

object TailExprCompletionContributor : CompletionProvider<CompletionParameters>() {

    private val pattern = or(
        // foo.bar.baz<caret>
        psiElement(C3Types.IDENT).inside(C3AccessIdent::class.java),
        // foo.bar.<caret>
        psiElement(PsiWhiteSpace::class.java).inside(C3CallExprTail::class.java),
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (!pattern.accepts(parameters.position) && !pattern.accepts(parameters.originalPosition)) {
            return;
        }
        val lookupTarget = parameters.siblingOf<C3ExprStmt>()
            ?: return
        val lookupString = parameters.getLookupString(lookupTarget)

        val project = parameters.position.project

        val rootType = getRootType(lookupTarget)
            ?: return

        val idents = lookupString.substringAfter(".").split(".")
        val fields = StructService.getFields(rootType, idents, project)

        fields.filter { (accessPath, _) ->
            accessPath.segments.size == 1
        }.forEach { (accessPath, type) ->
            result.addElement(
                LookupElementBuilder.create(accessPath.name)
                    .withPresentableText(accessPath.name)
                    .withIcon(C3Icons.Nodes.STRUCT_FIELD)
                    .withTypeText(type)
            )
        }

    }
}