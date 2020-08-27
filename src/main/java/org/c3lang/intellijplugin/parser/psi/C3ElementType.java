package org.c3lang.intellijplugin.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.c3lang.intellijplugin.C3Language;
import org.jetbrains.annotations.NotNull;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3ElementType extends IElementType
{
    public C3ElementType(@NotNull String debugName)
    {
        super(debugName, C3Language.INSTANCE);
    }
}
