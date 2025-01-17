package org.c3lang.intellij.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.c3lang.intellij.psi.C3FuncDef;
import org.jetbrains.annotations.NotNull;

public class C3FunctionSuffixNameIndex extends StringStubIndexExtension<C3FuncDef> {
    public static final StubIndexKey<String, C3FuncDef> KEY = StubIndexKey.createIndexKey("c3.function_name.suffix");

    @Override
    public @NotNull StubIndexKey<String, C3FuncDef> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
