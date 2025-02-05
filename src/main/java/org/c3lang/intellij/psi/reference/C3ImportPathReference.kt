package org.c3lang.intellij.psi.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.c3lang.intellij.psi.C3ImportPath

class C3ImportPathReference(element: C3ImportPath) : PsiReferenceBase<C3ImportPath>(element) {
    override fun resolve(): PsiElement {
        return element
    }
}

//class C3ImportPathReference(
//    private val importProvider: C3ImportProvider,
//    private val element: C3Path
//) : PsiReferenceBase<C3Path>(element) {
//    override fun resolve(): PsiElement? {
//        val importPaths = importProvider.importDeclarations.flatMap { it.importPaths.importPathList }
//
//        val elementPrefix = element.text.split("::")
//            .mapNotNull { it.nullize() }.singleOrNull() ?: return null
//
//        val res = importPaths.singleOrNull { it.text == element.text } ?: importPaths.singleOrNull {
//            it.text.endsWith(elementPrefix)
//        }
//
//        return res
//    }
//
////        override fun isReferenceTo(element: PsiElement): Boolean {
////            return element is C3ImportPath && resolve() == element
////        }
//}
//class C3ImportPathReference(element: C3CallExpr) : PsiReferenceBase<C3CallExpr>(element)  {
//    override fun resolve(): PsiElement? {
//        val possibleModules = C3CallableReference(element).resolveVariants().mapNotNull {
//            it.moduleName?.value
//        }
//        val moduleName = possibleModules.singleOrNull() ?: return null
//        val moduleSection = element.parentOfType<C3ImportProvider>() ?: return null
//
//        return moduleSection.importDeclarations.flatMap { it.importPaths.importPathList }.singleOrNull {
//            it.text == moduleName
//        }
//    }
//
//}