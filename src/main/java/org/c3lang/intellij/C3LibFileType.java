package org.c3lang.intellij;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3LibFileType implements FileType
{
    public static final C3LibFileType INSTANCE = new C3LibFileType();

    private C3LibFileType()
    {
    }

    @NotNull
    @Override
    public String getName()
    {
        return "C3 Library File";
    }

    @Override public boolean isBinary()
    {
        return true;
    }

    @Override public boolean isReadOnly()
    {
        return true;
    }

    @Override
    @NotNull
    public String getDescription()
    {
        return "C3 library file";
    }

    @Override
    @NotNull
    public String getDefaultExtension()
    {
        return "c3l";
    }

    @Override
    @NotNull
    public Icon getIcon()
    {
        return C3Icons.LIB_FILE;
    }
}
