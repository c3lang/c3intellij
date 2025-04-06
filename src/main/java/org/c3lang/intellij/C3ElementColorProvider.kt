package org.c3lang.intellij

import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import org.c3lang.intellij.psi.C3LiteralExpr
import java.awt.Color

class C3ElementColorProvider : ElementColorProvider
{
    override fun getColorFrom(element: PsiElement): Color?
    {
        if (element !is C3LiteralExpr) return null
        if (!element.text.matches(Regex("0x([\\dA-Fa-f]{8}|[\\dA-Fa-f]{6})"))) return null

        val rgb = element.text.substring(2).toLong(16)

        return JBColor(rgb.toInt(), rgb.toInt())
    }

    override fun setColorTo(element: PsiElement, color: Color)
    {
    }
}