package org.c3lang.intellijplugin;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3SyntaxHighlighterFactory extends SyntaxHighlighterFactory
{
    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile)
    {
        return new C3SyntaxHighlighter();
    }
}
