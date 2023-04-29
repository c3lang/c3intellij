package org.c3lang.intellij.psi.impl;


import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3Types;

public class C3PsiImplUtils
{
    public static String getName(C3StructDeclaration decl)
    {
        ASTNode node = decl.getNode().findChildByType(C3Types.TYPE_IDENT);
        if (node == null) return null;
        return node.getText();
    }

    public static PsiElement setName(C3StructDeclaration decl, String newName)
    {
        return decl;
    }

    public static PsiElement getNameIdentifier(C3StructDeclaration decl)
    {
        ASTNode node = decl.getNode().findChildByType(C3Types.TYPE_IDENT);
        return node == null ? null : node.getPsi();
    }
}
