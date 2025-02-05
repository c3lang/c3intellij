package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3LocalDeclarationStmt
import org.c3lang.intellij.psi.FullyQualifiedName

abstract class C3LocalDeclarationStmtMixinImpl(node: ASTNode) : C3LocalDeclarationStmt, C3PsiElementImpl(node) {

    override fun findTypeName(): FullyQualifiedName? {
        return FullyQualifiedName.from(optionalType)
    }
}