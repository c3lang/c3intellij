package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPrefix
import com.intellij.lang.ASTNode
import org.c3lang.intellij.psi.C3ParamPathElement

abstract class C3ParamPathElementMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3ParamPathElement {

    override fun findPathName(includeSelf: Boolean): List<String> {
        val prev = prevSibling as? C3ParamPathElement

        val prevPaths = prev?.findPathName(true) ?: emptyList()
        return if (includeSelf) {
            prevPaths + listOfNotNull(text.dropPrefix("."))
        } else {
            prevPaths
        }
    }
}
