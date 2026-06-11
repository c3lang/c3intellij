package org.c3lang.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import org.c3lang.intellij.psi.C3AliasDecl;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3AttrdefDecl;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3BitstructDef;
import org.c3lang.intellij.psi.C3BitstructSimpleDef;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3IdentifierList;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;

public class C3BreadcrumbsProvider implements BreadcrumbsProvider
{
	@Override
	public Language @NotNull [] getLanguages()
	{
		return new Language[]{C3Language.INSTANCE};
	}

	@Override
	public boolean acceptElement(@NotNull PsiElement element)
	{
		return element instanceof C3StructDeclaration
			|| element instanceof C3BitstructDeclaration
			|| element instanceof C3EnumDeclaration
			|| element instanceof C3MacroDefinition
			|| element instanceof C3FuncDefinition
			|| element instanceof C3StructMemberDeclaration
			|| element instanceof C3BitstructDef
			|| element instanceof C3BitstructSimpleDef
			|| element instanceof C3AttrdefDecl
			|| element instanceof C3TypedefDecl
			|| element instanceof C3AliasDecl;
	}

	@Override
	public @NotNull String getElementInfo(@NotNull PsiElement element)
	{
		if (element instanceof C3StructDeclaration declaration) return declaration.getTypeName().getText();
		if (element instanceof C3EnumDeclaration declaration) return declaration.getTypeName().getText();
		if (element instanceof C3MacroDefinition definition) return definition.getMacroHeader().getMacroName().getText();
		if (element instanceof C3TypedefDecl declaration) return declaration.getTypeName().getText();
		if (element instanceof C3AttrdefDecl declaration) return declaration.getAttributeUserName().getText();
		if (element instanceof C3AliasTypeDecl declaration) return declaration.getTypeName().getText();
		if (element instanceof C3InterfaceDefinition definition) return definition.getTypeName().getText();
		if (element instanceof C3FuncDefinition definition)
		{
			return definition.getFuncDef().getFuncHeader().getFuncName().getText();
		}
		if (element instanceof C3BitstructDeclaration declaration) return declaration.getTypeName().getText();
		if (element instanceof C3StructMemberDeclaration declaration)
		{
			C3IdentifierList list = declaration.getIdentifierList();
			return list != null ? list.getText() : "anonymous";
		}
		if (element instanceof C3BitstructDef || element instanceof C3BitstructSimpleDef)
		{
			ASTNode ident = element.getNode().findChildByType(C3Types.IDENT);
			return ident != null ? ident.getText() : "anonymous";
		}
		return "";
	}
}
