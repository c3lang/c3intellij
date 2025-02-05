@file:JvmName("C3ParserUtils")

package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.util.text.nullize
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.stubs.C3TypeEnum
import javax.swing.Icon

fun getSourceFileName(psi: C3PsiElement): String = psi.containingFile.name
fun getModuleName(psi: C3PsiElement): ModuleName? = ModuleName.from(psi)
fun getImports(psi: C3ImportProvider): List<ModuleName> = ModuleName.getImportList(psi)
fun getImportDeclarations(psi: C3ImportProvider): List<C3ImportDecl> = psi.childrenOfType<C3TopLevel>()
    .mapNotNull { it.importDecl }

fun getNameIdent(psi: C3Parameter) = psi.getNameIdentText()

fun getPresentation(psi: C3Module) : ItemPresentation {
    return object : ItemPresentation {
        override fun getPresentableText(): String {
            return "${psi.text.padEnd(100, ' ')} ${psi.containingFile.virtualFile.path}"
        }

        override fun getIcon(unused: Boolean): Icon? = C3Icons.Nodes.MODULE
    }
}

/* All C3NamedPsiElements */
fun getName(psi: C3PsiNamedElement): String = psi.text
fun getTextOffset(psi: C3PsiNamedElement): Int {
    return psi.nameIdentifier?.textOffset ?: (psi as C3PsiElementImpl).superTextOffset
}

fun getReference(psi: C3PsiNamedElement): PsiReference? {
    return psi.nameIdentifier?.textRangeInParent?.let {
        object : PsiReferenceBase<PsiElement>(psi, it) {
            override fun resolve(): PsiElement {
                return psi
            }
        }
    }
}

/* C3FuncName */
//fun setName(psi: C3FuncName, name: String): PsiElement {
//    psi.context?.project?.also {
//        val functionName = PsiElementUtils.createFunctionName(it, name)
//        val nameIdentifier = requireNotNull(psi.nameIdentifier)
//        functionName.nameIdentifier.let(nameIdentifier::replace)
//    }
//    return psi
//}
//
//fun getNameIdentifier(psi: C3FuncName): PsiElement {
//    val findChildByType = checkNotNull(
//        psi.node.findChildByType(C3Types.IDENT)
//    )
//
//    return findChildByType.psi
//}

/* C3Module */
//fun getModuleName(psi: C3Module) = psi.stub?.module ?: psi.moduleName
//
///* C3PathIdentExpr */
//fun setName(psi: C3PathIdentExpr, name: String): PsiElement {
//    psi.context?.project?.also {
//        val expression = PsiElementUtils.createPathIdentExpr(it, name)
//
//        expression.nameIdentifier?.let { newName ->
//            psi.nameIdentifier?.replace(newName)
//        }
//
//    }
//    return psi
//}

fun getNameIdentifier(psi: C3PathIdentExpr): PsiElement? {
    val expr = PsiTreeUtil.getChildOfType(psi, C3PathIdentExpr::class.java)

    var child = expr?.pathIdent?.lastChild
    while (child != null) {
        if (child.node.elementType == C3Types.IDENT) return child
        child = child.lastChild
    }

    return null
}

fun getReference(psi: C3PathIdentExpr): PsiReference = C3PathIdentExprReference(psi)

/* C3Path */
fun getImportIntention(psi: C3Path): String? {
    val text = psi.text
    // delete last ::
    return text.dropPostfix("::").nullize()
}

/*C3Arg*/
//fun setName(psi: C3Arg, name: String): PsiElement {
//    psi.context?.project?.also {
//        val result = PsiElementUtils.createArg(it, name)
//        psi.nameIdentifier?.replace(result)
//    }
//    return psi
//}

fun getNameIdentifier(psi: C3Arg): PsiElement? {
    return when (val expr = psi.expr) {
        is C3PathIdentExpr -> {
            expr.pathIdent.path ?: expr.pathIdent
        }

        else -> null
    }
}

fun getReference(psi: C3Arg): PsiReference = C3ArgElementReference(psi)

/*C3TypeName*/
fun getTypeName(psi: C3TypeName): TypeName = psi.stub?.typeName ?: TypeName.from(psi)
fun getModuleName(psi: C3TypeName): ModuleName? = psi.stub?.moduleName ?: ModuleName.from(psi)
fun getTypeEnum(psi: C3TypeName): C3TypeEnum = psi.stub?.typeEnum ?: C3TypeEnum.find(psi)

/*C3FuncDef*/
fun getTypeName(psi: C3FuncDef): TypeName? = psi.stub?.type ?: TypeName.from(psi)
fun getFunctionOrMacroName(psi: C3FuncDef): FullyQualifiedName = psi.stub?.callName ?: FullyQualifiedName.from(psi)
fun getReturnTypeName(psi: C3FuncDef): ReturnTypeName = psi.stub?.returnType ?: ReturnTypeName.from(psi)
fun getParameterTypeNames(psi: C3FuncDef): String =
    psi.stub?.parameterTypes ?: psi.fnParameterList.text ?: ""

/*C3MacroDefinition*/
fun getTypeName(psi: C3MacroDefinition): TypeName? = psi.stub?.type ?: TypeName.from(psi)
fun getFunctionOrMacroName(psi: C3MacroDefinition): FullyQualifiedName =
    psi.stub?.callName ?: FullyQualifiedName.from(psi)

fun getReturnTypeName(psi: C3MacroDefinition): ReturnTypeName? = psi.stub?.returnType ?: ReturnTypeName.from(psi)
fun getParameterTypeNames(psi: C3MacroDefinition): String =
    psi.stub?.parameterTypes ?: "(${psi.macroParams.text})"