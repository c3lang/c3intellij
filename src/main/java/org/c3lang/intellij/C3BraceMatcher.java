package org.c3lang.intellij;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

/**
 * Undocumented Class
 *
 * @author Christoffer Lerno
 */
public class C3BraceMatcher implements PairedBraceMatcher
{
    @Override public BracePair @NotNull [] getPairs()
    {
        return new BracePair[] {
                new BracePair(C3Types.LB, C3Types.RB, true),
                new BracePair(C3Types.LP, C3Types.RP, false),
                new BracePair(C3Types.LBT, C3Types.RBT, false),
                new BracePair(C3Types.LBRAPIPE, C3Types.RBRAPIPE, true)
        };
    }

    @Override public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType,@Nullable IElementType iElementType1)
    {
        return false;
    }

    @Override public int getCodeConstructStart(PsiFile psiFile, int offset)
    {
        return offset;
    }
}
