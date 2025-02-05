package org.c3lang.intellij.psi.impl;

import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3Type;

public class C3PsiImplUtils {

    public static String getType(C3PsiElement source) {
        final C3Type element = PsiTreeUtil.findChildOfAnyType(source, C3Type.class);
        return element == null ? null : element.getText();
    }


}
