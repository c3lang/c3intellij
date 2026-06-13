package org.c3lang.intellij.intention;

import com.intellij.openapi.util.Key;
import org.c3lang.intellij.psi.ModuleName;

public final class IntentionKeys
{
    public static final IntentionKeys INSTANCE = new IntentionKeys();
    public static final Key<ModuleName> AUTO_IMPORT = Key.create("auto_import");

    private IntentionKeys()
    {
    }
}
