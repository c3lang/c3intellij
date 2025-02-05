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
import org.c3lang.intellij.stubs.C3TypeEnum
import javax.swing.Icon

@Suppress("DuplicatedCode")
class TypeCompletionContributor : CompletionContributor() {
    private val pattern = or(
        psiElement(C3Types.CONST_IDENT),/*for searching with first upper case letter*/
        psiElement().inside(C3Type::class.java),
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
        val insertHandler = StructInsertHandler(
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
        }.filterIsInstance<C3TypeName>().forEach { typeName ->
            val icon: Icon? = when (typeName.typeEnum) {
                C3TypeEnum.FALLBACK -> null
                C3TypeEnum.STRUCT -> C3Icons.Nodes.STRUCT
                C3TypeEnum.INTERFACE -> C3Icons.Nodes.INTERFACE
                C3TypeEnum.ENUM -> C3Icons.Nodes.ENUM
                C3TypeEnum.UNION -> C3Icons.Nodes.UNION
                C3TypeEnum.BITSTRUCT -> C3Icons.Nodes.BITSTRUCT
                C3TypeEnum.FAULT -> C3Icons.Nodes.FAULT
            }

            val lookupElementBuilder = LookupElementBuilder
                .create(typeName, typeName.getFullName())
                .withLookupStrings(listOf(typeName.getFullName(), typeName.text))
                .withPsiElement(typeName)
                .withIcon(icon)
                .withPresentableText(typeName.getFullName())
                .withInsertHandler(insertHandler)

            result.addElement(lookupElementBuilder)
        }
    }

    @Suppress("DuplicatedCode")
    private class StructInsertHandler(
        private val moduleSection: C3ImportProvider,
        private val range: TextRange,
    ) : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3TypeName

            val moduleName = element.moduleName
            val moduleImported = moduleSection.imports.contains(moduleName)

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
            TypeCompletionContributor::class.java.name
        )
    }
}
