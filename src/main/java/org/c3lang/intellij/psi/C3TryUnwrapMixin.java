package org.c3lang.intellij.psi;

public interface C3TryUnwrapMixin extends C3PsiNamedElement, C3NameIdentProvider
{
	/**
	 * Binding identifier (`t` in `try t = x`), or {@code null} for the
	 * binding-less form (`try x`).
	 */
	@org.jetbrains.annotations.Nullable String getBindingName();
}
