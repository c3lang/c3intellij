package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3ParameterList

abstract class C3ParameterListMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3ParameterList