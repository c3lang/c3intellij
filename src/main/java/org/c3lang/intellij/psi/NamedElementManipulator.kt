package org.c3lang.intellij.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.PsiNamedElement

class NamedElementManipulator : AbstractElementManipulator<PsiNamedElement>() {

    override fun handleContentChange(element: PsiNamedElement, range: TextRange, newContent: String?): PsiNamedElement? {
        return newContent?.let {
            element.setName(it)
            element
        }
    }
}
