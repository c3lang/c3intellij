package org.c3lang.intellij.index;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StringStubIndexExtension;

public abstract class C3StringStubIndexExtension<T extends PsiElement> extends StringStubIndexExtension<T>
{
    @Override
    public int getVersion()
    {
        return 7;
    }
}
