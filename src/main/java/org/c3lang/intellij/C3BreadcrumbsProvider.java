package org.c3lang.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class C3BreadcrumbsProvider implements BreadcrumbsProvider
{
    @Override public Language[] getLanguages()
    {
        return new Language[] { C3Language.INSTANCE };
    }

    @Override public boolean acceptElement(@NotNull PsiElement psiElement)
    {
        return psiElement instanceof C3StructDeclaration
                | psiElement instanceof C3BitstructDeclaration
                | psiElement instanceof C3EnumDeclaration
                | psiElement instanceof C3FaultDeclaration
                | psiElement instanceof C3MacroDeclaration
                | psiElement instanceof C3FuncDefinition
                | psiElement instanceof C3StaticDeclaration
                | psiElement instanceof C3StructMemberDeclaration
                | psiElement instanceof C3BitstructDef
                | psiElement instanceof C3BitstructSimpleDef
                | psiElement instanceof C3DefDeclaration;
    }

    @Override public @NotNull String getElementInfo(@NotNull PsiElement psiElement)
    {
        if (psiElement instanceof C3StructDeclaration decl)
        {
            return decl.getTypeName().getText();
        }
        else if (psiElement instanceof C3EnumDeclaration decl)
        {
            return decl.getTypeName().getText();
        }
        else if (psiElement instanceof C3FaultDeclaration decl)
        {
            return decl.getTypeName().getText();
        }
        else if (psiElement instanceof C3MacroDeclaration decl)
        {
            return decl.getMacroHeader().getMacroName().getText();
        }
        else if (psiElement instanceof C3FuncDefinition decl)
        {
            return decl.getFuncHeader().getFuncName().getText();
        }
        else if (psiElement instanceof C3BitstructDeclaration decl)
        {
            return decl.getTypeName().getText();
        }
        else if (psiElement instanceof C3StructMemberDeclaration decl)
        {
            C3IdentifierList list = decl.getIdentifierList();
            if (list == null) return "anonymous";
            return list.getText();
        }
        else if (psiElement instanceof C3BitstructDef || psiElement instanceof C3BitstructSimpleDef)
        {
            ASTNode element = psiElement.getNode().findChildByType(C3Types.IDENT);
            if (element == null) return "anonymous";
            return element.getText();
        }
        else if (psiElement instanceof C3DefDeclaration decl)
        {
            return decl.getAnyIdent().getText();
        }
        else if (psiElement instanceof C3StaticDeclaration decl)
        {
            return "static " + decl.getText();
        }
        // TODO globals
        return "abc";
    }
}
