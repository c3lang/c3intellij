package org.c3lang.intellij.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface C3FullyQualifiedNamePsiElement extends C3ModuleNamePsiElement
{
	@NotNull FullyQualifiedName getFqName();

	@Override
	default @Nullable ModuleName getModuleName()
	{
		C3ModuleDefinition moduleDefinition = getModuleDefinition();
		return moduleDefinition != null ? moduleDefinition.getModuleName() : null;
	}
}
