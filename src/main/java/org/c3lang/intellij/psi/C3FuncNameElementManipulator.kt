package org.c3lang.intellij.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator

class C3FuncNameElementManipulator : AbstractElementManipulator<C3FuncName>() {

    override fun handleContentChange(element: C3FuncName, range: TextRange, newContent: String?): C3FuncName? {
        return newContent?.let {
            element.setName(it)
            element
        }
    }
}
