package org.c3lang.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.index.NameIndex

class C3PathIdentExprReference(
    element: C3PathIdentExpr
) : PsiReferenceBase<C3PathIdentExpr>(element) {

    override fun resolve(): PsiElement? {
//        val moduleSection = element.getModuleSectionOrDefault()
//
//        // maybe there are more imports ending with suffix
//        val prefix = element.getPrefix() ?: ""
//        val modules = moduleSection.getModuleImports().filter {
//            it.endsWith(prefix)
//        } + listOfNotNull(element.getModuleName())
//
//        val ident = element.getIdent() ?: return null
//
//        // TODO we should search by argument type name in order to identify function/macro
//        val numberOrArgs = element.callExprTail.callInvocation?.callArgList?.argList?.argList?.count() ?: 0
//
//        val functions = modules.flatMap { moduleName ->
//            StubIndex.getElements(
//                NameIndex.KEY,
//                "$moduleName::$ident",
//                element.project,
//                GlobalSearchScope.allScope(element.project),
//                C3PsiElement::class.java
//            )
//        } + StubIndex.getElements(
//            NameIndex.KEY,
//            ident,
//            element.project,
//            GlobalSearchScope.allScope(element.project),
//            C3PsiElement::class.java
//        )
//
//        if (functions.count() == 1) {
//            return functions.first().getFuncOrMacroName()
//        }
//
//        val funcOrMacroName = functions.singleOrNull {
//            // TODO we should check by types of parameters, but for now I cant get types of variables etc
//            it.getParameterTypesNames().count() == numberOrArgs
//        }?.getFuncOrMacroName()
//
//        return funcOrMacroName

        return null
    }

}