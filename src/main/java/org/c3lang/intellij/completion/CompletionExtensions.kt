package org.c3lang.intellij.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.siblings
import com.intellij.refactoring.suggested.startOffset
import org.c3lang.intellij.psi.*
import kotlin.math.max

private val log = Logger.getInstance("org.c3lang.intellij.completion.CompletionExtensionsKt")

val CompletionParameters.moduleDefinition: C3ModuleDefinition?
    get() = siblingOf<C3ModuleDefinition>()

/*@Deprecated("change to dedicated methods")
val CompletionParameters.completion: Completion
    get() {
        val target = lookupTarget

        val lookupString = editor.document.getText(
            TextRange.create(
                lookupTarget.startOffset,
                editor.caretModel.offset
            )
        )

        return Completion(
            lookupTarget = target,
            matcher = NameUtil.buildMatcher(
                "*$lookupString*",
                NameUtil.MatchingCaseSensitivity.NONE
            ),
            lookupString = lookupString
        )
    }

private val CompletionParameters.lookupTarget: PsiElement
    get() {
        siblingOf<C3PathConstExpr>()?.let {
            // PathConstIdentExpr + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3PathIdentExpr>()?.let {
            // PathIdentExpr + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3ExprStmt>()?.let {
            // PathIdentExpr + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3OptionalType>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3StructMemberDeclaration>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3Parameter>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        siblingOf<C3ImportPath>()?.let {
            // Type + PsiError;
            return it.siblings().first()
        }

        if (originalPosition is PsiWhiteSpace) {
            // we are at "dummy"
            return position
        }

        log.error("position $position or originalPosition $originalPosition not (yet) supported.")
        error("position $position or originalPosition $originalPosition not (yet) supported.")
    }*/

inline fun <reified T : PsiElement> CompletionParameters.siblingOf(): T? {
    return (originalPosition?.parentOfType<T>() ?: position.parentOfType<T>())
}

fun CompletionParameters.getLookupString(lookupTarget: PsiElement): String {
    return editor.document.getText(
        TextRange.create(
            lookupTarget.startOffset,
            editor.caretModel.offset
        )
    )
}

fun getRootType(lookupTarget: PsiElement): FullyQualifiedName? {
    val compoundInitExpr = lookupTarget.parentOfType<C3CompoundInitExpr>()
    if (compoundInitExpr != null) {
        return FullyQualifiedName.from(compoundInitExpr.type)
    }

    val binaryExpr = lookupTarget.parentOfType<C3BinaryExpr>()

    val pathIdent = PsiTreeUtil.findChildOfType(binaryExpr, C3PathIdent::class.java)
    val localDeclAfterType = pathIdent?.findLocalDeclAfterType()?.singleOrNull()

    localDeclAfterType?.findTypeName()?.let { fqn ->
        val accessPath = PsiTreeUtil.findChildrenOfType(
            pathIdent.parentOfType<C3CallExpr>(),
            C3CallExprTail::class.java
        ).joinToString { it.text }

        return FullyQualifiedName(fqn.module, "${fqn.name}${accessPath}")
    }

    lookupTarget.parentOfType<C3FullyQualifiedTypeNameProvider>()?.findTypeName()?.let {
        return it
    }

    val types = PsiTreeUtil.collectElements(lookupTarget) {
        it is C3FullyQualifiedTypeNameProvider
    }.filterIsInstance<C3FullyQualifiedTypeNameProvider>()
        .mapNotNull { it.findTypeName() }

    return types.firstOrNull()
}

fun getMatcher(lookupString: String): MinusculeMatcher {
    return NameUtil.buildMatcher(
        "*$lookupString*",
        NameUtil.MatchingCaseSensitivity.NONE
    )
}

fun MinusculeMatcher.matchingDegreeOrZero(name: String): Int {
    return max(matchingDegree(name), 0)
}

data class Completion(
    val lookupTarget: PsiElement,
    val matcher: MinusculeMatcher,
    val lookupString: String,
)

const val DUMMY_IDENTIFIER: String = "dummy;"