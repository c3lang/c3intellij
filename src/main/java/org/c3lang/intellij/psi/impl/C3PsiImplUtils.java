package org.c3lang.intellij.psi.impl;


import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.psi.*;

public class C3PsiImplUtils
{
    public static String getName(C3TypeDecl element)
    {
        ASTNode node = element.getNode().findChildByType(C3Types.TYPE_IDENT);
        if (node == null) return null;
        return node.getText();
    }


    public static PsiElement setName(C3TypeDecl decl, String newName)
    {
        ASTNode nameNode = decl.getNode().findChildByType(C3Types.TYPE_IDENT);;
        if (nameNode == null) return decl;
        C3TypeName name = C3ElementFactory.createTypeName(decl.getProject(), newName);
        decl.getNode().replaceChild(nameNode, name.getNode());
        return decl;
    }


    public static PsiElement getNameIdentifier(C3TypeDecl decl)
    {
        ASTNode node = decl.getNode().findChildByType(C3Types.TYPE_IDENT);
        return node == null ? null : node.getPsi();
    }
}
