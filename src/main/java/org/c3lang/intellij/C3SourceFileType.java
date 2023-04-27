package org.c3lang.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3SourceFileType extends LanguageFileType
{
    public static final C3SourceFileType INSTANCE = new C3SourceFileType();

    private C3SourceFileType()
    {
        super(C3Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "C3 File";
    }

    @Override
    @NotNull
    public String getDescription()
    {
        return "C3 language file";
    }

    @Override
    @NotNull
    public String getDefaultExtension()
    {
        return "c3";
    }

    @Override
    @NotNull
    public Icon getIcon()
    {
        return C3Icons.FILE;
    }
}
