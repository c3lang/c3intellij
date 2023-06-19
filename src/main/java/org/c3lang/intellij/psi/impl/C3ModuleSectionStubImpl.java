package org.c3lang.intellij.psi.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.c3lang.intellij.psi.C3ModuleSectionStub;
import org.c3lang.intellij.psi.C3ModuleSectionStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3ModuleSectionStubImpl extends StubBase<C3ModuleSection> implements C3ModuleSectionStub
{
    private final @NotNull String moduleName;
    public C3ModuleSectionStubImpl(@Nullable StubElement<?> parent, C3ModuleSectionStubElementType type, @NotNull String moduleName)
    {
        super(parent, type);
        this.moduleName = moduleName;
    }

    @NotNull @Override public String getModuleName()
    {
        return moduleName;
    }

    @Override public String printTree()
    {
        return "Module " + super.printTree();
    }
}
