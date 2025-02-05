package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.and
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.index.NameIndex
import org.c3lang.intellij.intention.AddImportQuickFix
import org.c3lang.intellij.psi.*

object FunctionCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(
        FunctionCompletionContributor::class.java
    )
    private val pattern = or(
        // foo::<caret>
        psiElement().inside(C3CallExpr::class.java),
//        psiElement().inside(C3PathIdentExpr::class.java),

        // struct fields
        and(
            psiElement().inside(C3PathIdentExpr::class.java),
            psiElement().andNot(
                psiElement().inside(C3ParamPathElement::class.java),
            )
        )
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val originalPosition = parameters.originalPosition

        if (!pattern.accepts(parameters.position) && !pattern.accepts(parameters.originalPosition)) {
            return;
        }

        if (!originalPosition.isValidParameterValue()) {
            return
        }

        val moduleDefinition = parameters.moduleDefinition
            ?: return
        val lookupTarget = parameters.siblingOf<C3PathIdentExpr>()
            ?: return
        val lookupString = parameters.getLookupString(lookupTarget)
        val matcher = getMatcher(lookupString)

        val moduleName = moduleDefinition.moduleName
        val elementRange = lookupTarget.textRange
        val project = parameters.position.project
        val containingFileName = parameters.position.containingFile.name

        val insertHandler = FunctionInsertHandler(
            moduleDefinition = moduleDefinition,
            range = elementRange
        )

        StubIndex.getInstance().getAllKeys(
            NameIndex.KEY,
            project
        ).filter { matcher.matches(it) || it.isBlank() }.flatMap { key ->
            StubIndex.getElements(
                NameIndex.KEY,
                key,
                project,
                GlobalSearchScope.allScope(project),
                C3PsiElement::class.java,
            )
        }.filterIsInstance<C3CallablePsiElement>().forEach { element ->
            val sameFileBonus = if (element.sourceFileName == containingFileName) 1.0 else 0.0
            val sameModuleBonus = if (element.moduleName == moduleDefinition.moduleName) 1.0 else 0.0
            val importBonus = if (moduleDefinition.imports.contains(element.moduleName)) 1.0 else 0.0

            val fqName = element.fqName
            val nameDegree = matcher.matchingDegreeOrZero(fqName.fullName)
            val moduleDegree = element.moduleName?.value?.let(matcher::matchingDegreeOrZero) ?: 0
            val typeDegree = 0 // TODO match against type of declaration

            val priority = listOf(
                sameFileBonus,
                sameModuleBonus,
                importBonus,
                moduleDegree,
                nameDegree,
                typeDegree,
            ).sumOf {
                it.toDouble()
            }

            result.addElement(
                PrioritizedLookupElement.withPriority(
                    createLookupElementBuilder(moduleName, element, fqName, insertHandler),
                    priority
                )
            )
        }
    }

    @Suppress("DuplicatedCode")
    private class FunctionInsertHandler(
        private val moduleDefinition: C3ModuleDefinition,
        private val range: TextRange,
    ) :
        InsertHandler<LookupElement> {
        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val editor = context.editor
            val document = editor.document
            val element = item.psiElement as C3CallablePsiElement

            WriteCommandAction.runWriteCommandAction(context.project) {
                val importAction = AddImportQuickFix.addImportAsText(
                    element = element,
                    moduleSection = moduleDefinition,
                )

                val textToInsert = moduleDefinition.textToInsert(importAction?.moduleName, element)
                val endOffset = editor.caretModel.offset

                document.replaceString(
                    range.startOffset,
                    endOffset,
                    textToInsert
                )

                importAction?.write(document)
            }
        }

    }

    private fun createLookupElementBuilder(
        moduleName: ModuleName?,
        element: C3CallablePsiElement,
        fqName: FullyQualifiedName,
        insertHandler: InsertHandler<LookupElement>,
    ): LookupElementBuilder {
        val icon = when (element) {
            is C3FuncDef -> C3Icons.Nodes.FUNCTION
            is C3MacroDefinition -> C3Icons.Nodes.MACRO
            else -> null
        }

        val parameterList = element.parameterTypes.joinToString(",") {
            listOfNotNull(
                it.type?.fullName,
                it.name
            ).joinToString(" ")
        }

        val lookupElementBuilder =
            LookupElementBuilder.create(element, fqName.fullName)
                .withLookupStrings(
                    listOf(
                        fqName.fullName,
                        fqName.suffixName,
                        fqName.name
                    )
                )
                .withIcon(icon)
                .withPresentableText(if (fqName.module == moduleName) fqName.name else fqName.fullName)
                .appendTailText("($parameterList)", false)
                .withTypeText(element.returnType?.fullName ?: "")
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

}
