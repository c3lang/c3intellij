package org.c3lang.intellij.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3StructDeclarationStub;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface C3StructDeclarationMixin extends C3PsiNamedElement, C3NameIdentProvider, C3DeclaredInProvider, StubBasedPsiElement<C3StructDeclarationStub>
{
	@NotNull List<StructField> getFields();
}
