package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3ModuleStub

interface C3ModuleMixin : C3ModuleNamePsiElement, StubBasedPsiElement<C3ModuleStub>