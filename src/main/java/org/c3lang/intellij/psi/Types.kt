package org.c3lang.intellij.psi

import ai.grazie.utils.dropPostfix
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.text.nullize
import org.c3lang.intellij.psi.ShortType.Companion.toShortType
import org.c3lang.intellij.stubs.readNullableUTFFast
import org.c3lang.intellij.stubs.writeNullableUTFFast

data class ModuleName(
    val value: String,
) {
    val suffix: String = run {
        value.split("::").last()
    }

    companion object {
        fun from(psi: C3PsiElement): ModuleName? =
            psi.parentOfType<C3ModuleSection>(true)?.module?.modulePath?.text?.let { ModuleName(it) }

        fun deserialize(string: String): ModuleName = ModuleName(string)

        fun getImportList(psi: C3PsiElement): List<ModuleName> {
            val moduleSection = psi.parentOfType<C3ModuleDefinition>(true) ?: return emptyList()
            return moduleSection.importDeclarations.flatMap { it.importPaths.importPathList }.map {
                ModuleName(it.text)
            }
        }
    }
}

data class FullyQualifiedPath(
    val typeName: FullyQualifiedName,
    val path: String
) {
    val fullName: String get() = "${typeName.fullName}.${path}"
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

    fun asShortType(): ShortType {
        return ShortType(name, module?.suffix)
    }

    companion object {

        fun parse(string: String): FullyQualifiedName {
            val segments = string.split("::").toMutableList()
            val type = segments.removeLast()

            return FullyQualifiedName(
                module = segments.joinToString("::").nullize()?.let(::ModuleName),
                name = type
            )
        }

        fun from(psi: C3TypeName, module: ModuleName? = psi.moduleName): FullyQualifiedName {
            return FullyQualifiedName(module, psi.text)
        }

        fun from(psi: C3FuncHeader, module: ModuleName?): FullyQualifiedName {
            return FullyQualifiedName(module, psi.funcName.text)
        }

        fun from(psi: C3MacroHeader, module: ModuleName?): FullyQualifiedName {
            return FullyQualifiedName(module, psi.macroName.text)
        }

        fun from(psi: C3ConstDeclarationStmt, module: ModuleName? = ModuleName.from(psi)): FullyQualifiedName {
            val name = psi.node.findChildByType(C3Types.CONST_IDENT)?.psi?.text ?: error("CONST_IDENT missing")

            return FullyQualifiedName(module, name)
        }

        fun from(psi: C3OptionalType): FullyQualifiedName? {
            return psi.moduleDefinition.resolve(psi.type).singleOrNull()
        }

        fun from(psi: C3Type): FullyQualifiedName? {
            return psi.moduleDefinition.resolve(psi).singleOrNull()
        }

        fun from(psi: C3FaultDefinition, module: ModuleName? = ModuleName.from(psi)): FullyQualifiedName {
            val name = psi.node.findChildByType(C3Types.CONST_IDENT)?.psi?.text ?: error("CONST_IDENT missing")

            return FullyQualifiedName(module, name)
        }
    }
}

data class ParamType(
    val name: String,
    val type: ShortType?
) {

    companion object {

        fun from(list: List<C3ParamDecl>): List<ParamType> {
            return list.map {
                ParamType(
                    name = it.parameter.lastChild.text,
                    type = it.parameter.type?.toShortType()
                )
            }
        }

        fun List<C3ParamDecl>?.toParamTypeList(): List<ParamType> = this?.let {
            from(it)
        } ?: emptyList()

        fun deserialize(dataStream: StubInputStream): List<ParamType> {
            val count = dataStream.readVarInt()

            return (0 until count).map {
                val name = dataStream.readUTFFast()
                val typeValue = dataStream.readNullableUTFFast()
                val typePrefix = dataStream.readNullableUTFFast()

                ParamType(
                    name = name,
                    type = typeValue?.let {
                        ShortType(typeValue, typePrefix)
                    }
                )
            }
        }

        fun serialize(dataStream: StubOutputStream, parameterTypes: List<ParamType>) {
            dataStream.writeVarInt(parameterTypes.size)

            parameterTypes.forEach {
                dataStream.writeUTFFast(it.name)
                dataStream.writeNullableUTFFast(it.type?.value)
                dataStream.writeNullableUTFFast(it.type?.prefix)
            }
        }

    }
}

data class ShortType(
    val value: String,
    val prefix: String? = null
) {
    val fullName: String = listOfNotNull(prefix, value).joinToString("::")

    companion object {
        fun from(psi: C3Type): ShortType {
            return ShortType(psi.text)
        }

        fun C3Type.toShortType(): ShortType = from(this)

        fun parse(string: String): ShortType {
            return ShortType(string)
        }

        fun parse(strings: List<String>): List<ShortType> {
            return strings.map { parse(it) }
        }
    }
}

data class AccessPath(
    val segments: List<String>
) {

    constructor(path: String) : this(
        path.split(".")
    )

    init {
        assert(segments.isNotEmpty())
    }

    val name = segments.last()
    val path = segments.joinToString(".")
}

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
                val importSuffix = checkNotNull(psi.parentOfType<C3ModuleDefinition>())
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