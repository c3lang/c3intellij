package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;

public abstract class C3CallExprMixinImpl extends C3PsiElementImpl implements C3CallExpr
{
	public C3CallExprMixinImpl(@NotNull ASTNode node) { super(node); }

	@Override
	public @NotNull FullyQualifiedName getFqName()
	{
		C3ModuleDefinition moduleDefinition = getModuleDefinition();
		return new FullyQualifiedName(
			moduleDefinition != null ? moduleDefinition.getModuleName() : null,
			getLastChild().getText());
	}
}
