package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.jetbrains.annotations.Nullable;

public class C3StructDeclarationStub extends StubBase<C3StructDeclaration> {

    private final StringRef name;

    public C3StructDeclarationStub(@Nullable StubElement parent, IStubElementType elementType, StringRef name) {
        super(parent, elementType);
        this.name = name;
    }

    @Nullable
    public String getName() {
        return StringRef.toString(name);
    }
}
