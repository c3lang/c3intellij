package org.c3lang.intellij.annotation.fix;

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3FuncDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helpers for intention/quickfix previews.
 *
 * <p>The platform renders intention previews by calling {@code invoke} on a copy
 * of the file inside a read action on a background thread. Fixes that modify
 * documents must therefore <b>not</b> start a write action for previews;
 * instead they return a precomputed {@link IntentionPreviewInfo.CustomDiff}.
 */
public final class IntentionPreviews
{
    private IntentionPreviews()
    {
    }

    @NotNull
    public static IntentionPreviewInfo insertionPreview(
            @NotNull PsiFile file,
            int insertOffset,
            @NotNull String insertedText)
    {
        String original = file.getText();
        int offset = Math.min(Math.max(insertOffset, 0), original.length());
        String modified = original.substring(0, offset) + insertedText + original.substring(offset);
        return new IntentionPreviewInfo.CustomDiff(file.getFileType(), original, modified);
    }

    /**
     * Locates the {@link C3FuncDef} in the given file (which may be a preview copy)
     * corresponding to the original anchor element.
     */
    public static @Nullable C3FuncDef findFuncDefIn(@NotNull PsiFile file, @Nullable PsiElement originalAnchor)
    {
        if (originalAnchor == null || !originalAnchor.isValid()) return null;
        int offset = originalAnchor.getTextRange().getStartOffset();
        if (offset < 0 || offset > file.getTextLength()) return null;
        PsiElement at = file.findElementAt(offset);
        if (at instanceof C3FuncDef funcDef) return funcDef;
        return PsiTreeUtil.getParentOfType(at, C3FuncDef.class);
    }
}
