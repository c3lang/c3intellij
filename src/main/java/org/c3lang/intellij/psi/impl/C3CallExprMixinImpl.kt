package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.FullyQualifiedName

abstract class C3CallExprMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3CallExpr {

    override val fqName: FullyQualifiedName
        get() = FullyQualifiedName(moduleDefinition.moduleName, lastChild.text)


}

