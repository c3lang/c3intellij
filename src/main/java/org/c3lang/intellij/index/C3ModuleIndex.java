package org.c3lang.intellij.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3Module;
import org.jetbrains.annotations.NotNull;

public class C3ModuleIndex extends StringStubIndexExtension<C3Module> {
    public static final StubIndexKey<String, C3Module> KEY = StubIndexKey.createIndexKey("c3.module");

    @Override
    public @NotNull StubIndexKey<String, C3Module> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
