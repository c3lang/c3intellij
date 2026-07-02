package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class C3LocalDeclarationStmtMixinImpl extends C3PsiElementImpl implements C3LocalDeclarationStmt
{
	public C3LocalDeclarationStmtMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @Nullable FullyQualifiedName findTypeName()
	{
		return FullyQualifiedName.from(getOptionalType());
	}
}
