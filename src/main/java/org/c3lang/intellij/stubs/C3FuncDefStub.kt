package org.c3lang.intellij.stubs

import com.intellij.psi.stubs.*
import com.intellij.util.io.DataInputOutputUtil
import org.c3lang.intellij.psi.*

class C3FuncDefStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>?,
    val sourceFileName: String,
    val module: ModuleName?,
    val type: TypeName?,
    val callName: FullyQualifiedName,
    val returnType: ReturnTypeName,
    val parameterTypes: String,
) : StubBase<C3FuncDef?>(parent, elementType) {

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        psi: C3FuncDef,
        module: ModuleName? = ModuleName.from(psi),
        imports: Map<String, ModuleName> = ModuleName.getImports(psi),
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = psi.containingFile.name,
        module = module,
        type = TypeName.from(psi),
        callName = FullyQualifiedName.from(psi, module),
        returnType = ReturnTypeName.from(psi, imports),
        parameterTypes = psi.fnParameterList.text/*ParameterTypeName.listOf(psi, imports)*/
    )

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>?,
        dataStream: StubInputStream
    ) : this(
        parent = parent,
        elementType = elementType,
        sourceFileName = dataStream.readUTFFast(),
        module = DataInputOutputUtil.readNullable(dataStream, dataStream::readUTFFast)?.let(ModuleName::deserialize),
        type = DataInputOutputUtil.readNullable(dataStream, dataStream::readUTFFast)?.let(TypeName::deserialize),
        callName = FullyQualifiedName.deserialize(dataStream.readUTFFast()),
        returnType = ReturnTypeName.deserialize(dataStream.readUTFFast()),
        parameterTypes = dataStream.readUTFFast(),
    )

    fun serialize(dataStream: StubOutputStream) {
        dataStream.writeUTFFast(sourceFileName)
        dataStream.writeNullableUTFFast(module?.value)
        dataStream.writeNullableUTFFast(type?.fullName)
        dataStream.writeUTFFast(callName.fullName)
        dataStream.writeUTFFast(returnType.type.fullName)
        dataStream.writeUTFFast(parameterTypes)
    }

}
