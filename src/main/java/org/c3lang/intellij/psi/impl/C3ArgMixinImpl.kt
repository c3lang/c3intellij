package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.psi.C3Arg
import org.c3lang.intellij.psi.C3NameIdentProvider

abstract class C3ArgMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3Arg {

    override val nameIdentElement: LeafPsiElement?
        get() = expr?.let { it as? C3NameIdentProvider }?.nameIdentElement

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override fun findPathName(includeSelf: Boolean): List<String> {
        return paramPath?.paramPathElementList?.lastOrNull()?.findPathName(true)?.reversed() ?: emptyList()
    }
}