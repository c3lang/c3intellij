package org.c3lang.intellij.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Objects;

public class C3PsiImplUtils {

    public static String getType(C3PsiElement source) {
        final C3Type element = PsiTreeUtil.findChildOfAnyType(source, C3Type.class);
        return element == null ? null : element.getText();
    }

}
