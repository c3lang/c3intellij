package org.c3lang.intellij

import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.JBColor
import org.c3lang.intellij.psi.C3Types
import java.awt.Color

class C3ElementColorProvider : ElementColorProvider
{
    private var lastElement : PsiElement? = null

    override fun getColorFrom(element: PsiElement): Color?
    {   if (element.node.elementType != C3Types.INT_LITERAL) return null
        if (!element.text.matches(Regex("0x([\\dA-Fa-f]{8}|[\\dA-Fa-f]{6})"))) return null
        val rgba = element.text.substring(2)
        var intval = rgba.toLong(16)
        if (rgba.length == 6) {
            return JBColor(intval.toInt(), intval.toInt())
        }
        intval = ((intval and 0xFFFFFF00) shr 8) + ((intval and 0xFF) shl 24)
        return JBColor(Color(intval.toInt(), true), Color(intval.toInt(), true));
    }

    override fun setColorTo(element: PsiElement, color: Color)
    {
        if (lastElement == null || element.parent.parent != null) {
            lastElement = element
        }
        val upper = element.text.contains(Regex("[A-F]"))
        val str = if (element.text.length == 8) {
            String.format(if (upper) "0x%02X%02X%02X" else "0x%02x%02x%02x", color.red, color.green, color.blue)
        } else {
            String.format(if (upper) "0x%02X%02X%02X%02X" else "0x%02x%02x%02x%02x", color.red, color.green, color.blue, color.alpha)
        }
        val file = PsiFileFactory.getInstance(element.project).createFileFromText(
                "color_replace.dummy.c3",
                C3SourceFileType.INSTANCE,
                "int a = $str;",
            )
        val newElement = file.firstChild.firstChild.firstChild.children[1].children[0].firstChild
        val parent = lastElement!!.parent
        lastElement!!.replace(newElement)
        lastElement = parent.firstChild
    }
}