package org.c3lang.intellij.psi;

import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.NotNull;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3TokenType extends IElementType
{
    public C3TokenType(@NotNull String debugName)
    {
        super(debugName, C3Language.INSTANCE);
    }

    @Override
    public String toString()
    {
        return "C3TokenType." + super.toString();
    }
}
