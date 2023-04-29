package org.c3lang.intellij.psi.impl;


import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class C3NamedElementImpl extends ASTWrapperPsiElement
{
    public C3NamedElementImpl(@NotNull ASTNode node)
    {
        super(node);
    }
}
