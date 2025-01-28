package org.c3lang.intellij.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3SourceFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3File extends PsiFileBase {
    public C3File(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, C3Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return C3SourceFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "C3 File";
    }

    @Override
    public @Nullable Icon getIcon(int flags) {
        return C3Icons.FILE;
    }
}
