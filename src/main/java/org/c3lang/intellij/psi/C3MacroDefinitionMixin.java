package org.c3lang.intellij.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3MacroDefinitionStub;
import org.jetbrains.annotations.Nullable;

public interface C3MacroDefinitionMixin extends C3CallablePsiElement, StubBasedPsiElement<C3MacroDefinitionStub>, C3PsiNamedElement, C3NameIdentProvider
{
	/**
	 * Conditional compilation key (section {@code @if} plus own {@code @if}/{@code @feat}),
	 * or {@code null} when unconditional. Stored in the stub so cross-file
	 * duplicate checks stay index-free.
	 */
	@Nullable String getConditionKey();
}
