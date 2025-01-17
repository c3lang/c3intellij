package org.c3lang.intellij.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3PsiElement;
import org.jetbrains.annotations.NotNull;

public class C3PsiElementImpl extends ASTWrapperPsiElement implements C3PsiElement {
    public C3PsiElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
