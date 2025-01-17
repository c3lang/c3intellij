package org.c3lang.intellij.psi;

import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.NotNull;

public class C3ElementType extends IElementType
{
    public C3ElementType(@NotNull String debugName)
    {
        super(debugName, C3Language.INSTANCE);
    }

    @Override
    public String toString() {
        return C3TokenType.class.getSimpleName() + "." + super.toString();
    }
}
