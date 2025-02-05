package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.c3lang.intellij.psi.C3Arg

class C3ArgElementReference(element: C3Arg) : PsiReferenceBase<C3Arg>(element) {

    override fun resolve(): PsiElement? {
//        val argName = element.text
//        // search for other args or statements within scope
//        val macroFuncBody = element.findParentOfType<C3StatementList>()
//        if (macroFuncBody != null) {
//            return macroFuncBody.childrenOfType<C3StatementElement>()
//                // TODO should be see only statements that are above?
////                .filter { it.textRange.endOffset < element.textRange.endOffset }
//                .singleOrNull {
//                    it.name == argName
//                }
//        }
        return null
    }
}