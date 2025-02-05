package org.c3lang.intellij.psi

interface C3CallablePsiElement : C3PsiElement {
    val sourceFileName: String
    val moduleName: ModuleName?
    val typeName: TypeName?
    val functionOrMacroName: FullyQualifiedName
    val returnTypeName: ReturnTypeName?
    val parameterTypeNames: String
//    val parameterTypeNames: List<ParameterTypeName>
}