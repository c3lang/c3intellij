package org.c3lang.intellij.psi

import org.c3lang.intellij.C3TokenSets

interface C3BaseTypeMixin : C3PsiNamedElement, C3NameIdentProvider {

    val primitiveType: Boolean
        get() {
            return node.getChildren(C3TokenSets.KW_TYPES).isNotEmpty() ||
                    firstChild.node.getChildren(C3TokenSets.KW_TYPES).isNotEmpty()
        }
}