package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3PsiNamedElement

abstract class C3PsiNamedElementImpl(node: ASTNode) : C3PsiElementImpl(node), C3PsiNamedElement