package org.c3lang.intellij.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.c3lang.intellij.psi.C3Module;
import org.jetbrains.annotations.Nullable;

public class C3ModuleStub extends StubBase<C3Module> {

    private final StringRef name;

    public C3ModuleStub(@Nullable StubElement parent, IStubElementType elementType, StringRef name) {
        super(parent, elementType);
        this.name = name;
    }

    @Nullable
    public String getName() {
        return StringRef.toString(name);
    }
}
