package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.completion.PsiDocumentUtils.commitChanges
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.intention.AddImportQuickFix
import org.c3lang.intellij.psi.*

class FunctionCompletionContributor : CompletionContributor() {
    private val pattern = PlatformPatterns.or(
        PlatformPatterns.psiElement().inside(C3FnParameterList::class.java),
        PlatformPatterns.psiElement().inside(C3ExprStmt::class.java),
        PlatformPatterns.psiElement().inside(C3PathIdentExpr::class.java),
    )

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val originalPosition = parameters.originalPosition

        if (!pattern.accepts(originalPosition)) {
            return;
        }

        if (originalPosition?.parent is PsiErrorElement) {
            return
        }

        if (!originalPosition.isValidParameterValue()) {
            return
        }

        val completion = CompletionContext(parameters)

        val matcher = NameUtil.buildMatcher(
            "*${completion.lookupString}*",
            NameUtil.MatchingCaseSensitivity.NONE
        )
        val insertHandler = FunctionInsertHandler(
            moduleSection = completion.importProvider,
            range = completion.range
        )

        StubIndex.getInstance().getAllKeys(
            NameIndex.KEY,
            completion.project
        ).filter { matcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                NameIndex.KEY,
                key,
                completion.project,
                GlobalSearchScope.allScope(completion.project),
                C3PsiElement::class.java,
            )
        }.filterIsInstance<C3CallablePsiElement>().forEach { element ->
            val sameFileBonus = if (element.sourceFileName == completion.containingFile.name) 1.0 else 0.0
            val sameModuleBonus = if (element.moduleName == completion.importProvider.moduleName) 1.0 else 0.0
            val importBonus = if (completion.importProvider.imports.contains(element.moduleName)) 1.0 else 0.0

            val nameDegree = matcher.matchingDegree(element.functionOrMacroName.fullName)
            val moduleDegree = element.moduleName?.value?.let(matcher::matchingDegree) ?: 0
            val typeDegree = 0 /*TODO match against type of declaration*/

            val priority = listOf(
                sameFileBonus,
                sameModuleBonus,
                importBonus,
                moduleDegree,
                nameDegree,
                typeDegree,
            ).sumOf { it.toDouble() }

            result.addElement(
                PrioritizedLookupElement.withPriority(
                    createLookupElementBuilder(element, completion, insertHandler),
                    priority
                )
            )
        }
    }

    @Suppress("DuplicatedCode")
    private class FunctionInsertHandler(
        private val moduleSection: C3ImportProvider,
        private val range: TextRange,
    ) :
        InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3CallablePsiElement

            val moduleName = element.moduleName
            val moduleImported = moduleSection.imports.contains(moduleName)

            val textToInsert = if (moduleImported) {
                "${element.functionOrMacroName.suffixName}()"
            } else {
                "${element.functionOrMacroName.fullName}()"
            }

            val endOffset = editor.caretModel.offset

            document.replaceString(
                range.startOffset,
                endOffset,
                textToInsert
            )
            document.commitChanges(context)
            editor.caretModel.moveToOffset(range.startOffset + textToInsert.length - 1)

            AddImportQuickFix.addImport(
                importIntention = moduleName,
                moduleSection = moduleSection,
                project = context.project
            )
        }

        override fun toString(): String {
            return "FunctionInsertHandler[" +
                    "range=" + range + ']'
        }
    }

    private fun createLookupElementBuilder(
        element: C3CallablePsiElement,
        completion: CompletionContext,
        insertHandler: InsertHandler<LookupElement>,
    ): LookupElementBuilder {
        val icon = when (element) {
            is C3FuncDef -> C3Icons.Nodes.FUNCTION
            is C3MacroDefinition -> C3Icons.Nodes.MACRO
            else -> null
        }

        val lookupElementBuilder =
            LookupElementBuilder.create(element, element.functionOrMacroName.fullName)
                .withLookupStrings(
                    listOf(
                        element.functionOrMacroName.name,
                        element.functionOrMacroName.fullName
                    )
                )
                .withIcon(icon)
                .appendTailText(element.parameterTypeNames, false)
                .withTypeText(element.returnTypeName?.type?.fullName ?: "")
                .withInsertHandler(insertHandler)

        return lookupElementBuilder

    }

    private fun PsiElement?.isValidParameterValue(): Boolean {
        if (this == null) return false

        // we are in parameters
        if (parentOfType<C3FnParameterList>() is C3FnParameterList) {
            // function(std::io::File file = <here>)
            if (parentOfType<C3ParamDecl>() !is C3ParamDecl) {
                return true
            }
            // function(std::io::File fi<here>)
            // this is IDENT but if its parent is path_ident_expr that means we are inside expression
            if (this.node.elementType == C3Types.IDENT && parentOfType<C3PathIdentExpr>() == null) {
                return false
            }
        }

        return true
    }

    companion object {
        private val log = Logger.getInstance(
            FunctionCompletionContributor::class.java.name
        )
    }

}
