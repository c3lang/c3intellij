package org.c3lang.intellij.psi.impl

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.psi.C3PsiElement

open class C3StubBasedPsiElementBase<StubT : StubElement<*>> : StubBasedPsiElementBase<StubT>, C3PsiElement {

    constructor(stub: StubT, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    constructor(stub: StubT, nodeType: IElementType?, node: ASTNode?) : super(stub, nodeType, node)

    override fun getStub(): StubT? = greenStub

    override fun toString(): String {
        return javaClass.simpleName + "(" + node.elementType + ")"
    }
}
