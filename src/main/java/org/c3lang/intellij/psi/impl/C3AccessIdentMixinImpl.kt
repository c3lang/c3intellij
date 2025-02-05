package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.index.StructService
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3AccessIdentMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3AccessIdent {

    override fun getName(): String? {
        return nameIdent
    }

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(text)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override val nameIdent: String?
        get() {
            return nameIdentElement?.text
        }

    override val nameIdentElement: LeafPsiElement?
        get() {
            return firstChild as? LeafPsiElement
        }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override fun getTextRange(): TextRange {
        return nameIdentElement?.textRange ?: super.getTextRange()
    }

    override fun getReference(): PsiReference {
        return StructMemberReference(this)
    }

    override fun findTypeName(): FullyQualifiedName? {
        val typeProvider = StructMemberReference(this).resolve() as? C3FullyQualifiedTypeNameProvider
        return typeProvider?.findTypeName()
    }

    private class StructMemberReference(element: C3AccessIdent) : C3ReferenceBase<C3AccessIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val call = element.parentOfType<C3CallExpr>()
                ?: return emptyList()
            val project = element.project

            val (rootType, idents) = call.getAccessIdentSequence()
                ?: return emptyList()

            var query = rootType.fullName
            var structMembers = emptyList<C3StructMemberDeclaration>()

            for (ident in idents) {
                structMembers = StructService.getStructMembers("$query.$ident", project)

                val member = structMembers.singleOrNull()

                query = member?.structPathType?.fullName
                    ?: return emptyList()
            }

            return structMembers
        }

        override fun getRangeInElement(): TextRange {
            return super.getRangeInElement()
        }

        fun C3CallExpr.getAccessIdentSequence(): Pair<FullyQualifiedName, List<String>>? {
            val accessSequence = generateSequence<C3PsiElement>(this) {
                when (it) {
                    is C3ExprStmt -> it.expr
                    is C3CallExpr -> it.expr
                    else -> null
                }
            }.toMutableList()

            val rootExpr = accessSequence.removeLastOrNull() as? C3PathIdentExpr
            val rootType = rootExpr?.pathIdent?.findTypeName()
                ?: return null

            return rootType to accessSequence.filterIsInstance<C3CallExpr>().map {
                it.text.split(".").last()
            }.reversed()
        }
    }
}