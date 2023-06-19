package org.c3lang.intellij;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.OSFileIdeAssociation;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */

public class C3SourceFileType extends LanguageFileType implements OSFileIdeAssociation
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
        return "C3 source file";
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

    @Override public @NotNull ExtensionMode getExtensionMode()
    {
        return ExtensionMode.All;
    }

    @Override public boolean isFileAssociationAllowed()
    {
        return true;
    }
}
