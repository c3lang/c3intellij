package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.intellij.util.text.nullize
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.index.StructService
import org.c3lang.intellij.psi.*

object InitializerListCompletionContributor : CompletionProvider<CompletionParameters>() {
    private val log = Logger.getInstance(
        InitializerListCompletionContributor::class.java
    )
    private val pattern = or(
        // foo::<caret>
        psiElement().inside(C3InitializerList::class.java),
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

        val lookupTarget: C3PsiElement = parameters.siblingOf<C3AccessIdent>()
            ?: parameters.siblingOf<C3PathIdent>()
            ?: return

        val lookupString = parameters.getLookupString(lookupTarget)
        val project = parameters.position.project

        val rootType = getRootType(lookupTarget)
            ?: return
        val parentArg = lookupTarget.parentOfType<C3Arg>()

        val path = lookupTarget.parentOfType<C3PathNameProvider>()?.findPathName(false)
            ?: return

        val fieldNames = generateSequence(parentArg?.parentOfType<C3PathNameProvider>()) {
            it.parentOfType<C3PathNameProvider>()
        }.flatMap {
            it.findPathName(false)
        }.toList()

        val paths = fieldNames.reversed() + path + lookupString

        val fields = StructService.getFields(rootType, paths, project)

        fields.filter {
            it.first.name.startsWith(paths.last())
        }.forEach { (accessPath, type) ->
            result.addElement(
                LookupElementBuilder.create(accessPath.name)
                    .withPresentableText(accessPath.name)
                    .withIcon(C3Icons.Nodes.STRUCT_FIELD)
                    .withTypeText(type)
            )
        }

//        val fieldNames = generateSequence(lookupTarget.parentOfType<C3PathNameProvider>()) {
//            it.parentOfType<C3PathNameProvider>()
//        }.mapNotNull {
//            it.findPathName(false)
//        }.toList()
//
//        fieldNames

//        val last = types.lastOrNull()
//            ?: return
//
//        val query = last.fullName
//
//        val fields = StructService.findStructMemberFields(query, project).mapNotNull { smd ->
//            val structPath = smd.structPath
//
//            if (structPath == null || structPath == query || structPath.count { it == '.' } > 1) {
//                return@mapNotNull null
//            }
//
//            // TODO
//            structPath.split(".").last() to (smd.type?.text ?: "")
//        }
//
//        fields.forEach { (name, type) ->
//            result.addElement(
//                LookupElementBuilder.create(name)
//                    .withPresentableText(name)
//                    .withIcon(C3Icons.Nodes.STRUCT_FIELD)
//                    .withTypeText(type)
//            )
//        }
    }

}