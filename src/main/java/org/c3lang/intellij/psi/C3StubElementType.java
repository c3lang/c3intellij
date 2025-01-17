package org.c3lang.intellij.psi;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class C3StubElementType<S extends StubBase<T>, T extends C3PsiElement> extends IStubElementType<S, T> {
    public C3StubElementType(@NotNull @NonNls String debugName) {
        super(debugName, C3Language.INSTANCE);
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "c3." + super.toString();
    }

}
