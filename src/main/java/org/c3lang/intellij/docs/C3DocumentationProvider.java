package org.c3lang.intellij.docs;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.jetbrains.annotations.Nullable;

public final class C3DocumentationProvider extends AbstractDocumentationProvider
{
	@Override
	public @Nullable String generateDoc(@Nullable PsiElement element, @Nullable PsiElement originalElement)
	{
		if (element instanceof C3FuncDef)
		{
			return FuncDefinitionDocs.generateFuncDefDoc((C3FuncDef) element);
		}
		if (element instanceof C3MacroDefinition)
		{
			return MacroDefinitionDocs.generateMacroDefinitionDoc((C3MacroDefinition) element);
		}
		if (element instanceof C3LocalDeclAfterType)
		{
			return VarDeclDocs.generateVarDeclDoc((C3LocalDeclAfterType) element);
		}
		if (element instanceof C3ConstDeclarationStmt)
		{
			return ConstDeclDocs.generateConstDeclDoc((C3ConstDeclarationStmt) element);
		}
		return null;
	}

	@Override
	public @Nullable String generateHoverDoc(@Nullable PsiElement element, @Nullable PsiElement originalElement)
	{
		return generateDoc(element, originalElement);
	}
}
