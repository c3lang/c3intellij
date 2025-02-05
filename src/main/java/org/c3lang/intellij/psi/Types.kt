package org.c3lang.intellij.psi

import ai.grazie.utils.dropPostfix
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.text.nullize

data class ModuleName(
    val value: String,
) {
    val suffix: String = run {
        value.split("::").last()
    }

    companion object {
        fun from(psi: C3PsiElement): ModuleName? = psi.getModuleName()?.let { ModuleName(it) }

        fun deserialize(string: String): ModuleName = ModuleName(string)

        fun getImports(psi: C3PsiElement): Map<String, ModuleName> {
            return getImportList(psi).associateBy { it.suffix }
        }

        fun getImportList(psi: C3PsiElement): List<ModuleName> {
            val moduleSection = psi.parentOfType<C3ImportProvider>(true) ?: return emptyList()
            return moduleSection.importDeclarations.flatMap { it.importPaths.importPathList }.map {
                ModuleName(it.text)
            }
        }
    }
}

data class FullyQualifiedName(
    val module: ModuleName?,
    val name: String
) {
    val suffixName: String = listOfNotNull(
        module?.suffix,
        name
    ).joinToString("::")

    val fullName: String = listOfNotNull(
        module?.value,
        name
    ).joinToString("::")

    companion object {

        fun deserialize(string: String): FullyQualifiedName {
            val segments = string.split("::").toMutableList()
            val type = segments.removeLast()

            return FullyQualifiedName(
                module = segments.joinToString("::").nullize()?.let(::ModuleName),
                name = type
            )
        }

        fun from(psi: C3FuncDef, module: ModuleName? = psi.moduleName): FullyQualifiedName {
            return FullyQualifiedName(module, psi.funcHeader.funcName.text)
        }

        fun from(psi: C3MacroDefinition, module: ModuleName? = psi.moduleName): FullyQualifiedName {
            return FullyQualifiedName(module, psi.macroHeader.macroName.text)
        }

        fun from(psi: C3ConstDeclarationStmt, module: ModuleName? = ModuleName.from(psi)): FullyQualifiedName {
            return FullyQualifiedName(module, checkNotNull(psi.node.findChildByType(C3Types.CONST_IDENT)).psi.text)
        }
    }
}

data class TypeName(
    val module: ModuleName?,
    val name: String
) {

    val suffixName: String = listOfNotNull(
        module?.suffix,
        name
    ).joinToString("::")

    val fullName: String = listOfNotNull(
        module?.value,
        name
    ).joinToString("::")

    companion object {
        fun from(psi: C3FuncDef): TypeName? = psi.funcHeader.funcName.type?.text?.let {
            TypeName(ModuleName.from(psi), it)
        }

        fun from(psi: C3MacroDefinition): TypeName? = psi.macroHeader.macroName.type?.text?.let {
            TypeName(ModuleName.from(psi), it)
        }

        fun from(type: String, imports: Map<String, ModuleName>): TypeName {
            val split = type.split("::")

            return if (split.size == 1) {
                TypeName(null, type)
            } else {
                TypeName(imports[split[0]], type)
            }
        }

        fun deserialize(string: String): TypeName {
            val segments = string.split("::").toMutableList()
            val type = segments.removeLast()

            return TypeName(
                module = segments.joinToString("::").nullize()?.let(::ModuleName),
                name = type
            )
        }

        fun from(psi: C3TypeName): TypeName {
            return TypeName(ModuleName.from(psi), psi.text)
        }

        fun from(psi: C3Type): TypeName {
            return TypeName(ModuleName.from(psi), psi.text)
        }
    }
}

data class ReturnTypeName(
    val type: TypeName
) {
    companion object {
        fun from(psi: C3FuncDef, imports: Map<String, ModuleName> = ModuleName.getImports(psi)): ReturnTypeName {
            return ReturnTypeName(
                TypeName.from(
                    type = psi.funcHeader.optionalType.text,
                    imports = imports
                )
            )
        }

        fun from(
            psi: C3MacroDefinition,
            imports: Map<String, ModuleName> = ModuleName.getImports(psi)
        ): ReturnTypeName? {
            return psi.macroHeader.optionalType?.text?.let {
                ReturnTypeName(
                    TypeName.from(
                        type = it,
                        imports = imports
                    )
                )
            }
        }

        fun deserialize(string: String): ReturnTypeName = ReturnTypeName(TypeName.deserialize(string))
    }
}

//typealias ParameterTypeNameList = List<ParameterTypeName>
//
//data class ParameterTypeName(
//    val name: String?,
//    val type: TypeName?
//) {
//
////    init {
////        requireNotNull(name ?: type) {
////            "Should be at least one identifier"
////        }
////    }
//
//    companion object {
//        fun listOf(
//            psi: C3FuncDef,
//            imports: Map<String, ModuleName> = ModuleName.getImports(psi)
//        ): List<ParameterTypeName> {
//            return from(psi.fnParameterList.parameterList?.paramDeclList, imports)
//        }
//
//        fun listOf(
//            psi: C3MacroDefinition,
//            imports: Map<String, ModuleName> = ModuleName.getImports(psi)
//        ): List<ParameterTypeName> {
//            return from(psi.macroParams.parameterList?.paramDeclList, imports)
//        }
//
//        fun deserialize(string: String): List<ParameterTypeName> {
//            return string.split(",").map {
//                val typeAndName = it.split(" ")
//
//                ParameterTypeName(
//                    name = typeAndName.last(),
//                    type = TypeName.deserialize(typeAndName.first())
//                )
//            }
//        }
//
//        private fun from(paramDeclList: List<C3ParamDecl>?, imports: Map<String, ModuleName>): List<ParameterTypeName> {
//            val parameter = paramDeclList?.first()?.parameter ?: return emptyList()
//
//            return paramDeclList.map {
//                ParameterTypeName(
//                    name = it.parameter.nameIdent,
//                    type = it.parameter.type?.let(TypeName::from)
//                )
//            }
//        }
//
//        val ParameterTypeNameList.fullNames get() = joinToString(",") {
//            it.type?.fullName ?: it.name ?: ""
//        }.trim()
//    }
//}

sealed interface ImportPossibility {
    object Noop : ImportPossibility

    data class Possible(
        val importIntention: ModuleName
    ) : ImportPossibility

    companion object {
        fun create(psi: C3PathIdent): ImportPossibility {
            val path = psi.path ?: return Noop

            val scopes = path.node?.getChildren(TokenSet.create(C3Types.SCOPE)) ?: return Noop
            if (scopes.size > 1) {
                val importSuffix = checkNotNull(psi.parentOfType<C3ImportProvider>())
                    .imports.map { it.suffix }
                val importIntention = ModuleName(path.text.dropPostfix("::"))

                if (importSuffix.contains(importIntention.suffix)) {
                    return Noop
                }

                return Possible(importIntention)
            }
            return Noop
        }
    }
}