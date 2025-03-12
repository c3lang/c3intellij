package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3FuncName

abstract class C3FuncNameMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3FuncName {

}