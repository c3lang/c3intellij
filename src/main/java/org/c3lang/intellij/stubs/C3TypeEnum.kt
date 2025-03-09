package org.c3lang.intellij.stubs

import com.intellij.psi.util.PsiTreeUtil
import org.c3lang.intellij.psi.*

enum class C3TypeEnum {
    FALLBACK,
    BITSTRUCT,
    ENUM,
    FAULT,
    INTERFACE,
    STRUCT,
    UNION;

    companion object {
        fun find(psi: C3TypeName): C3TypeEnum {
            val structDeclaration = PsiTreeUtil.getParentOfType(psi, C3StructDeclaration::class.java)
            return when {
                structDeclaration != null -> {
                    structDeclaration.node.findChildByType(C3Types.KW_UNION)?.let {
                        UNION
                    } ?: STRUCT
                }

                PsiTreeUtil.getParentOfType(psi, C3InterfaceDefinition::class.java) != null -> INTERFACE
                PsiTreeUtil.getParentOfType(psi, C3EnumDeclaration::class.java) != null -> ENUM
                PsiTreeUtil.getParentOfType(psi, C3BitstructDeclaration::class.java) != null -> BITSTRUCT

                else -> FALLBACK
            }
        }
    }
}