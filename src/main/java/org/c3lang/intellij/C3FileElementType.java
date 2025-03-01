package org.c3lang.intellij;

import com.intellij.psi.tree.IStubFileElementType;
import org.c3lang.intellij.stubs.PsiC3FileStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class C3FileElementType extends IStubFileElementType<PsiC3FileStub>
{
    public static final int STUB_VERSION = 1;

    public C3FileElementType()
    {
        super("c3.FILE", C3Language.INSTANCE);
    }

    @Override public int getStubVersion()
    {
        return STUB_VERSION;
    }

    @Override public @NonNls @NotNull String getExternalId()
    {
        return "c3.FILE";
    }
}
