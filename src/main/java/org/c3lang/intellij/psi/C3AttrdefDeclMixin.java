package org.c3lang.intellij.psi;

import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.stubs.C3AttrdefDeclStub;

/**
 * @author Christoffer Lerno
 */
public interface C3AttrdefDeclMixin extends StubBasedPsiElement<C3AttrdefDeclStub>, C3PsiNamedElement, C3FullyQualifiedNamePsiElement
{
}
