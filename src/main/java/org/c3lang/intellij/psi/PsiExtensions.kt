package org.c3lang.intellij.psi

import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType

/* functions */
fun C3FuncDef.getFunctionName(): String = funcHeader.funcName.text

fun C3FuncDef.getFunctionNameAndModuleSuffix(): String {
    val functionName = getFunctionName()

    return getModuleName()?.let {
        val moduleSuffix = it.split("::").last()
        return "$moduleSuffix::$functionName"
    } ?: functionName
}

fun C3FuncDef.getFunctionName(moduleSection: PsiElement): String {
    return if (this.getModuleSection() == moduleSection) {
        getFunctionName()
    } else {
        getFunctionNameAndModuleSuffix()
    }
}

fun C3FuncDef.getFunctionFullName(): String {
    return listOfNotNull(
        getModuleName(),
        getFunctionName()
    ).joinToString("::")
}

fun C3FuncDef.getFunctionParameters(): String {
    return fnParameterList.text
}

fun C3FuncDef.getFunctionReturnType(): String {
    return funcHeader.optionalType.text
}

fun C3FuncDef.getFunctionParameterTypesNames(): List<String> {
    val paramDeclList = fnParameterList.parameterList?.paramDeclList ?: return emptyList()
    return paramDeclList.getParameterTypesNames()
}

fun C3FuncDef.getFunctionExtensionType(): String? {
    return funcHeader.funcName.type?.text
}

/* macros */
fun C3MacroDefinition.getMacroName(): String {
    return this.macroHeader.macroName.text
}

fun C3MacroDefinition.getMacroFullName(): String {
    return listOfNotNull(
        getModuleName(),
        getMacroName()
    ).joinToString("::")
}

fun C3MacroDefinition.getMacroReturnType(): String? {
    return this.macroHeader.optionalType?.text
}

fun C3MacroDefinition.getMacroParameterTypesNames(): List<String> {
    val paramDeclList = this.macroParams.parameterList?.paramDeclList ?: return emptyList()
    return paramDeclList.getParameterTypesNames()
}

fun C3MacroDefinition.getMacroParameters(): String {
    // workaround for (bad?) bnf
    return "(${macroParams.text})"
}

fun C3MacroDefinition.getMacroExtensionType(): String? {
    return macroHeader.macroName.type?.text
}

fun List<C3ParamDecl>.getParameterTypesNames(): List<String> {
    val parameterType = first().parameter.type ?: return emptyList()
    var lastType = parameterType.text

    val result = mutableListOf<String>()
    for (param in this) {
        val type = param.parameter.type?.text
        result += lastType

        if (type != null) {
            lastType = type
        }
    }

    return result
}

/* module */
fun C3Module.getModuleName(): String {
    return modulePath.text
}

/* import */
//fun C3ImportPath.getSuffix(): String? {
//    return path?.getSuffix()
//}

/* types */
fun C3StructDeclaration.getStructDeclarationFullName(): String {
    return listOfNotNull(getModuleName(), typeName.text).joinToString("::")
}

fun C3TypeName.getTypeNameFullName(): String {
    return listOfNotNull(getModuleName(), text).joinToString("::")
}

fun C3PsiElement.getModule(): C3Module? = getModuleSection()?.module

fun C3PsiElement.getModuleName(): String? {
    return parentOfType<C3ModuleSection>()?.module?.modulePath?.text
}

fun C3PsiElement.getModuleSection(): C3ModuleSection? {
    val moduleSection = PsiTreeUtil.getParentOfType(this, C3ModuleSection::class.java)

    return moduleSection
}

fun C3PsiElement.getParameterTypesNames(): List<String> {
    return when (this) {
        is C3FuncDef -> getFunctionParameterTypesNames()
        is C3MacroDefinition -> getMacroParameterTypesNames()
        else -> emptyList()
    }
}

fun C3PsiElement.getFuncOrMacroName(): C3PsiElement? {
    return when (this) {
        is C3FuncDef -> funcHeader.funcName
        is C3MacroDefinition -> macroHeader.macroName
        else -> null
    }
}

fun PsiElement.getFullName(): String {
    return when (this) {
        is C3FuncDef -> getFunctionFullName()
        is C3MacroDefinition -> getMacroFullName()
        is C3TypeName -> getTypeNameFullName()
        is C3StructDeclaration -> getStructDeclarationFullName()
        else -> error("$this not supported")
    }
}

fun PsiElement.getName(): String {
    return when (this) {
        is C3StructDeclaration -> this.typeName.text
        else -> error("$this not supported")
    }
}

fun PsiElement.moduleContainsImport(module: String): Boolean {
    return getModuleImports().contains(module);
}

fun PsiElement.getModuleImports(): List<String> {
    if (this is C3ModuleSection || this is C3DefaultModuleSection) {
        return PsiTreeUtil.findChildrenOfType(this, C3ImportPath::class.java).map {
            it.text
        }
    }

    return emptyList()
}

fun C3PsiElement.getModuleSectionOrDefault(): C3PsiElement {
    return getModuleSection() ?: checkNotNull(PsiTreeUtil.getParentOfType(this, C3DefaultModuleSection::class.java)) {
        "Can't find the default module section for $text"
    }
}

fun C3PsiElement.getNameIdentText(): String? {
//    val child = node.findChildByType(C3Types.IDENT)
//        ?: node.findChildByType(C3Types.CT_CONST_IDENT)
//        ?: node.findChildByType(C3Types.CONST_IDENT)
//        ?: node.findChildByType(C3Types.AT_TYPE_IDENT)
//        ?: node.findChildByType(C3Types.CT_TYPE_IDENT)
//        ?: node.findChildByType(C3Types.TYPE_IDENT)
//        ?: node.findChildByType(C3Types.AT_IDENT)
//        ?: node.findChildByType(C3Types.HASH_IDENT)
//        ?: node.findChildByType(C3Types.CT_IDENT)
//        ?: node.findChildByType(C3Types.TYPE)
//        ?: node.findChildByType(C3Types.ELLIPSIS)
//    return requireNotNull(child) {
//        "IDENT not found in ${this.text} at ${this.containingFile.virtualFile.path}"
//    }.text

    val child = node.findChildByType(C3Types.IDENT)
        ?: node.findChildByType(C3Types.CT_IDENT)
        ?: node.findChildByType(C3Types.HASH_IDENT)

    return child?.text
}

fun C3PsiElement.getCtIdent(): String? {
    return node.findChildByType(C3Types.CT_IDENT)?.text
}

fun PsiElement.getIdentPsi(): PsiElement? {
    return node.findChildByType(C3Types.IDENT)?.psi
}

fun C3PsiElement.isTypeExtension(): Boolean {
    return when (this) {
        is C3FuncDef -> this.funcHeader.funcName.type != null
        is C3MacroDefinition -> this.macroHeader.macroName.type != null
        else -> false
    }
}

fun C3Path.getSuffix(): String? {
    return text?.split("::")?.last()
}

fun PsiElement.getTextRangeForCompletion(caret: CaretModel): TextRange {
    var prev: PsiElement = prevSibling ?: return TextRange.create(textOffset, caret.offset)

    // getting moving backwards to get first C3Element (IDENT)
    while (prev.node.elementType === C3Types.IDENT || prev.node.elementType === C3Types.SCOPE) {
        prev = prev.prevSibling ?: break
    }

    return TextRange.create(
        prev.textOffset,
        caret.offset
    )
}

fun PsiElement.isAnyPrevSibling(type: IElementType): Boolean {
    if (this.elementType == type) return true
    var sibling = prevSibling
    while (sibling != null) {
        if (sibling.elementType == type) return true

        sibling = sibling.prevSibling
    }

    return false
}