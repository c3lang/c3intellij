package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.completion.getRootType
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.index.StructService
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3PathIdentMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3PathIdent {

    override fun getName(): String? {
        return nameIdent
    }

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override val nameIdent: String?
        get() {
            return nameIdentElement?.text
        }

    override val nameIdentElement: LeafPsiElement?
        get() {
            return lastChild as? LeafPsiElement
        }

    override fun findTypeName(): FullyQualifiedName? {
        val typeProvider = findLocalDeclAfterType().singleOrNull() as? C3FullyQualifiedTypeNameProvider

        return typeProvider?.findTypeName()
    }

    override fun findLocalDeclAfterType(): List<C3LocalDeclAfterType> {
        val compoundStatement = parentOfType<C3CompoundStatement>() ?: return emptyList()

        val results = PsiTreeUtil.collectElementsOfType(
            compoundStatement,
            C3LocalDeclAfterType::class.java
        ).filter {
            it.textOffset < textOffset && it.nameIdent == nameIdent
        }

        return results

    }

    override fun getReference(): PsiReference {
        return PsiMultiReference(
            arrayOf(
                C3LocalDeclAfterTypeReference(this),
                C3ParameterReference(this),
                C3FuncNameReference(this),
                C3StructMemberReference(this)
            ),
            this
        )
    }

    private class C3LocalDeclAfterTypeReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val compoundStatement = element.parentOfType<C3CompoundStatement>() ?: return emptyList()

            val results = PsiTreeUtil.collectElementsOfType(
                compoundStatement,
                C3LocalDeclAfterType::class.java
            ).filter {
                it.textOffset < element.textOffset && it.nameIdent == element.nameIdent
            }

            return results
        }

    }

    private class C3ParameterReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val functionDef = element.parentOfType<C3FuncDefinition>() ?: return emptyList()

            val parameters = PsiTreeUtil.collectElementsOfType(
                functionDef, C3Parameter::class.java
            )
            val results = parameters.filter {
                it.textOffset < element.textOffset && it.nameIdent == element.nameIdent
            }

            return results
        }
    }

    private class C3FuncNameReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val results = NameIndexService.findByNameEndsWith(element.text, element.project)
                .filterIsInstance<C3CallablePsiElement>()
                .filter { element.moduleDefinition.containsImportOrSameModule(it) }
            return results
        }

        override fun getRangeInElement(): TextRange {
            return TextRange.create(element.path?.textLength ?: 0, element.textLength)
        }
    }

    private class C3StructMemberReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val rootType = getRootType(element)
                ?: return emptyList()

            val parentArg = element.parentOfType<C3Arg>()
            val path = element.parentOfType<C3PathNameProvider>()?.findPathName(false)
                ?: return emptyList()

            val fieldNames = generateSequence(parentArg?.parentOfType<C3PathNameProvider>()) {
                it.parentOfType<C3PathNameProvider>()
            }.flatMap {
                it.findPathName(false)
            }.toList()

            val paths = fieldNames.reversed() + path + element.text
            val members = StructService.getStructMemberDeclaration(rootType, paths, element.project)

            return members
        }

    }
}