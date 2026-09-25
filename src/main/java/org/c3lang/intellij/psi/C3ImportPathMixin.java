package org.c3lang.intellij.psi;

public interface C3ImportPathMixin extends C3PsiElement, C3ModuleNamePsiElement
{
	boolean endsWith(C3Path path);
	boolean isPublicImport();
	boolean hasValidImportAttributes();
}
