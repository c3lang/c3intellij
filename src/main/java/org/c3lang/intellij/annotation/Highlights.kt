package org.c3lang.intellij.annotation

import com.intellij.openapi.editor.colors.TextAttributesKey
import java.awt.Font

val DOC_COMMENT_TAG = TextAttributesKey.createTextAttributesKey("C3_DOC_COMMENT_TAG").apply {
    defaultAttributes.fontType = Font.BOLD or Font.ITALIC // kotlin bitwise-or
}