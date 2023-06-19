package org.c3lang.intellij.psi;

import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public interface C3ModuleSectionStub extends StubElement<C3ModuleSection>
{
    @NotNull String getModuleName();
}
