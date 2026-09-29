package org.c3lang.intellij.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3FuncDefStub;
import org.jetbrains.annotations.Nullable;

public interface C3FuncDefMixin extends C3CallablePsiElement, StubBasedPsiElement<C3FuncDefStub>, C3PsiNamedElement, C3NameIdentProvider
{
	/**
	 * Conditional compilation key (section {@code @if} plus own {@code @if}/{@code @feat}),
	 * or {@code null} when unconditional. Stored in the stub so cross-file
	 * duplicate checks stay index-free.
	 */
	@Nullable String getConditionKey();
}
