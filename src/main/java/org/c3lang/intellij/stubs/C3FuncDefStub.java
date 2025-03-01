package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.psi.C3FuncDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3FuncDefStub extends StubBase<C3FuncDef> {

    private final StringRef functionName;
    private final @Nullable String module;
    private final @NotNull String returnType;

    public C3FuncDefStub(
            @Nullable StubElement parent,
            IStubElementType elementType,
            StringRef functionName,
            @Nullable String module,
            @NotNull String returnType
    ) {
        super(parent, elementType);
        this.functionName = functionName;
        this.module = module;
        this.returnType = returnType;
    }

    public @Nullable String getModule() {
        return module;
    }

    public @NotNull String getReturnType() {
        return returnType;
    }

    @NotNull
    public String getFunctionName() {
        return StringRef.toString(functionName);
    }
}
