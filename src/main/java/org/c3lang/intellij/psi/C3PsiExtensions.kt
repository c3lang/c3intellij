package org.c3lang.intellij.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

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

fun C3FuncDef.getReturnType(): String {
    return funcHeader.optionalType.text
}

fun C3FuncDef.getModule(): C3Module? = getModuleSection()?.module

fun C3Module.getModuleName(): String {
    return modulePath.text
}

fun C3ImportPath.getSuffix(): String? {
    return path?.getSuffix()
}

fun C3StructDeclaration.getStructDeclarationFullName(): String {
    return listOfNotNull(getModuleName(), typeName.text).joinToString("::")
}

fun C3PsiElement.getModuleName(): String? {
    val moduleSection = PsiTreeUtil.getParentOfType(this, C3ModuleSection::class.java)

    return moduleSection?.module?.getModuleName()
}

fun C3PsiElement.getModuleSection(): C3ModuleSection? {
    val moduleSection = PsiTreeUtil.getParentOfType(this, C3ModuleSection::class.java)

    return moduleSection
}

fun C3Path.getSuffix(): String? {
    return text?.split("::")?.last()
}

fun PsiElement.getTextRange(parent: C3PsiElement?): TextRange {
    val originalElement = this

    val range: TextRange
    if (parent == null) {
        var prev = originalElement.prevSibling
        // getting moving backwards to get first C3Element (IDENT)
        while (prev.node.elementType === C3Types.IDENT || prev.node.elementType === C3Types.SCOPE) {
            prev = prev.prevSibling
        }
        range = TextRange.create(prev.nextSibling.textRange.startOffset, originalElement.textRange.endOffset)
    } else {
        range = parent.textRange
    }
    return range
}

//public static String getFunctionParameters(C3PsiElement source) {
//    C3ParameterList element = PsiTreeUtil.findChildOfAnyType(source, C3ParameterList.class);
//
//    return element == null ? null : element.getText();
//}


//public static @Unmodifiable @NotNull Collection<C3ImportDecl> getImportDecl(C3PsiElement source) {
//    final C3ModuleSection moduleSection = PsiTreeUtil.getParentOfType(source, C3ModuleSection.class);
//    if (moduleSection != null) {
//        return PsiTreeUtil.findChildrenOfType(moduleSection, C3ImportDecl.class);
//    }
//
//    final C3DefaultModuleSection defaultModuleSection = PsiTreeUtil.getParentOfType(source, C3DefaultModuleSection.class);
//    return PsiTreeUtil.findChildrenOfType(defaultModuleSection, C3ImportDecl.class);
//}
