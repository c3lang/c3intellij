package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3MacroDefinitionStub

interface C3MacroDefinitionMixin : C3CallablePsiElement, StubBasedPsiElement<C3MacroDefinitionStub>, C3PsiNamedElement,
    C3NameIdentProvider