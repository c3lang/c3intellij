package org.c3lang.intellij.psi;

public interface C3CatchUnwrapMixin extends C3PsiNamedElement, C3NameIdentProvider
{
	/**
	 * Binding identifier (`err` in `catch err = x`), or {@code null} for the
	 * binding-less form (`catch x`).
	 */
	@org.jetbrains.annotations.Nullable String getBindingName();
}
