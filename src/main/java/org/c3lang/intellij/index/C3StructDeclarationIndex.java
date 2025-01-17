package org.c3lang.intellij.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.jetbrains.annotations.NotNull;

public class C3StructDeclarationIndex extends StringStubIndexExtension<C3StructDeclaration> {
    public static final StubIndexKey<String, C3StructDeclaration> KEY = StubIndexKey.createIndexKey("c3.struct_declaration");

    @Override
    public @NotNull StubIndexKey<String, C3StructDeclaration> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return 1;
    }

}
