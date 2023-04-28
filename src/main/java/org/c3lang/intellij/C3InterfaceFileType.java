package org.c3lang.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3InterfaceFileType extends LanguageFileType
{
    public static final C3InterfaceFileType INSTANCE = new C3InterfaceFileType();

    private C3InterfaceFileType()
    {
        super(C3Language.INSTANCE);
    }

    @Override public @Nls @NotNull String getDisplayName()
    {
        return "C3 Interface";
    }

    @NotNull
    @Override
    public String getName()
    {
        return "C3 Interface File";
    }

    @Override
    @NotNull
    public String getDescription()
    {
        return "C3 interface file";
    }

    @Override
    @NotNull
    public String getDefaultExtension()
    {
        return "c3i";
    }

    @Override
    @NotNull
    public Icon getIcon()
    {
        return C3Icons.FILE;
    }
}
