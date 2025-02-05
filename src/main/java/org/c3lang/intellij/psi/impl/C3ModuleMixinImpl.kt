package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.C3Icons
import org.c3lang.intellij.psi.C3Module
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.stubs.C3ModuleStub
import javax.swing.Icon

abstract class C3ModuleMixinImpl : C3StubBasedPsiElementBase<C3ModuleStub>, C3Module {
    constructor(node: ASTNode) : super(node)

    constructor(
        stub: C3ModuleStub,
        nodeType: IStubElementType<*, *>
    ) : super(stub, nodeType)

    constructor(
        stub: C3ModuleStub,
        nodeType: IElementType?,
        node: ASTNode?
    ) : super(stub, nodeType, node)

    override val moduleName: ModuleName?
        get() = stub?.module ?: ModuleName.from(this)

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return "${text.padEnd(100, ' ')} ${containingFile.virtualFile.path}"
            }

            override fun getIcon(unused: Boolean): Icon? = C3Icons.Nodes.MODULE
        }
    }
}