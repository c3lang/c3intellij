package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.or
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.completion.PsiDocumentUtils.commitChanges
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.intention.AddImportQuickFix
import org.c3lang.intellij.psi.*

@Suppress("DuplicatedCode")
class ConstCompletionContributor : CompletionContributor() {
    private val pattern = or(
        psiElement(C3Types.CONST_IDENT)
    )

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val originalPosition = parameters.originalPosition

        if (!pattern.accepts(originalPosition)) {
            return;
        }

        val parent = originalPosition?.parent
        if (parent is PsiErrorElement) {
            return
        }

        val completion = CompletionContext(parameters)

        val nameMatcher = NameUtil.buildMatcher(
            "*${completion.lookupString}*",
            NameUtil.MatchingCaseSensitivity.NONE
        )
        val insertHandler = ConstInsertHandler(
            moduleSection = completion.importProvider,
            range = completion.range
        )

        StubIndex.getInstance().getAllKeys(
            NameIndex.KEY,
            completion.project
        ).filter { nameMatcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                NameIndex.KEY,
                key,
                completion.project,
                GlobalSearchScope.allScope(completion.project),
                C3PsiElement::class.java,
            )
        }.filterIsInstance<C3ConstDeclarationStmt>().forEach { const ->
            val fullyQualifiedName = FullyQualifiedName.from(const)

            val lookupElementBuilder = LookupElementBuilder
                .create(const, fullyQualifiedName.fullName)
                .withLookupStrings(listOf(fullyQualifiedName.fullName, fullyQualifiedName.name))
                .withPsiElement(const)
                .withIcon(C3Icons.Nodes.CONSTANT)
                .withPresentableText(fullyQualifiedName.fullName)
                .withInsertHandler(insertHandler)

            result.addElement(lookupElementBuilder)
        }
    }

    @Suppress("DuplicatedCode")
    private class ConstInsertHandler(
        private val moduleSection: C3ImportProvider,
        private val range: TextRange,
    ) : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3TypeName

            val moduleName = element.moduleName
            val moduleImported = moduleSection.imports.contains(element.moduleName)

            val textToInsert = if (moduleImported) {
                element.typeName.suffixName
            } else {
                element.typeName.fullName
            }

            val endOffset = editor.caretModel.offset

            document.replaceString(
                range.startOffset,
                endOffset,
                textToInsert
            )
            document.commitChanges(context)
            editor.caretModel.moveToOffset(range.startOffset + textToInsert.length)

            AddImportQuickFix.addImport(
                importIntention = moduleName,
                moduleSection = moduleSection,
                project = context.project
            )
        }

    }

    companion object {
        private val log = Logger.getInstance(
            ConstCompletionContributor::class.java.name
        )
    }
}
