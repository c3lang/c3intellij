package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import org.c3lang.intellij.psi.*

class C3MacroDefinitionStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val sourceFileName: String,
    val module: ModuleName?,
    val type: TypeName?,
    val callName: FullyQualifiedName,
    val returnType: ReturnTypeName?,
    val parameterTypes: String,
) : StubBase<C3MacroDefinition?>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3MacroDefinition,
        module: ModuleName? = ModuleName.from(psi),
        imports: Map<String, ModuleName> = ModuleName.getImports(psi),
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = psi.containingFile.name,
        module = module,
        type = TypeName.from(psi),
        callName = FullyQualifiedName(module, psi.macroHeader.macroName.text),
        returnType = ReturnTypeName.from(psi, imports),
        parameterTypes = "(${psi.macroParams.text})"/*ParameterTypeName.listOf(psi, imports)*/
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = dataStream.readUTFFast(),
        module = dataStream.readNullableUTFFast()?.let(::ModuleName),
        type = dataStream.readNullableUTFFast()?.let(TypeName::deserialize),
        callName = FullyQualifiedName.deserialize(dataStream.readUTFFast()),
        returnType = dataStream.readNullableUTFFast()?.let(ReturnTypeName::deserialize),
        parameterTypes = dataStream.readUTFFast()/*ParameterTypeName.deserialize(dataStream.readUTFFast()),*/,
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(sourceFileName)
        dataStream.writeNullableUTFFast(module?.value)
        dataStream.writeNullableUTFFast(type?.fullName)
        dataStream.writeUTFFast(callName.fullName)
        dataStream.writeNullableUTFFast(returnType?.type?.fullName)
        dataStream.writeUTFFast(parameterTypes)
    }

}