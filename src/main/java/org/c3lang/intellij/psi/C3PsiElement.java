package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public interface C3PsiElement extends PsiElement {

    /**
     * Nearest enclosing module section, or {@code null} for detached elements
     * (copies used by previews and quick fixes) and unparseable fragments.
     * Callers must degrade gracefully instead of assuming a section exists.
     */
    default @Nullable C3ModuleDefinition getModuleDefinition() {
        return PsiTreeUtil.getParentOfType(this, C3ModuleDefinition.class);
    }
}
