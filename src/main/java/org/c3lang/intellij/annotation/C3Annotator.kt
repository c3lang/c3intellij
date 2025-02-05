package org.c3lang.intellij.annotation

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.c3lang.intellij.psi.C3CallExpr

class C3Annotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is C3CallExpr -> annotateMissingCallables(element, holder)
        }

        org.c3lang.intellij.C3Annotator.INSTANCE.annotate(element, holder)
    }

    private fun annotateMissingCallables(
        callExpr: C3CallExpr,
        holder: AnnotationHolder
    ) {
//        val pathIdentExpr = callExpr.expr as? C3PathIdentExpr ?: return
//        val nameIdentElement = pathIdentExpr.pathIdent.nameIdentElement ?: return
//        val callName = nameIdentElement.text
//
//        val resolve = callExpr.importProvider.resolve(pathIdentExpr)
//        val elements = resolve.flatMap {
//            NameIndexService.findByName<C3CallablePsiElement>(it, callExpr.project)
//        }
//
//        when (elements.size) {
//            0 -> {
//                holder.error("$callName not found", nameIdentElement);
//            }
//
//            1 -> {
//                /*OK*/
//            }
//
//            else -> {
//                holder.warning("Warning Too many $callName found.", nameIdentElement);
//            }
//        }
    }

    private fun AnnotationHolder.error(message: String, element: PsiElement) {
        return newAnnotation(HighlightSeverity.ERROR, message).range(element.textRange).create()
    }

    private fun AnnotationHolder.warning(message: String, element: PsiElement) {
        return newAnnotation(HighlightSeverity.WARNING, message).range(element.textRange).create()
    }

    private fun AnnotationHolder.weakWarning(message: String, element: PsiElement) {
        return newAnnotation(HighlightSeverity.WEAK_WARNING, message).range(element.textRange).create()
    }

    private fun AnnotationHolder.info(message: String, element: PsiElement) {
        return newAnnotation(HighlightSeverity.INFORMATION, message).range(element.textRange).create()
    }


//    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
//        if (psiElement is C3BytesExpr) {
//            annotateBytes(psiElement, annotationHolder)
//        } else if (psiElement is C3StringExpr) {
//            annotateString(psiElement, annotationHolder)
//        } else if (psiElement is C3DefDecl) {
//            annotate(psiElement, annotationHolder)
//        } else if (psiElement is C3Type || psiElement is C3OptionalType) {
//            val parent = psiElement.getParent()
//            if (parent is C3FuncName || parent is C3MacroName || parent is C3OptionalType) return
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                .textAttributes(C3SyntaxHighlighter.TYPE_KEY).create()
//        } else if (psiElement is C3AttributeName) {
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                .textAttributes(C3SyntaxHighlighter.ATTRIBUTE_KEY).create()
//        } else if (psiElement is C3FuncName) {
//            val color =
//                if (psiElement.getType() != null) C3SyntaxHighlighter.METHOD_KEY else C3SyntaxHighlighter.FUNCTION_KEY
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(color).create()
//        } else if (psiElement is C3MacroName) {
//            var color = C3SyntaxHighlighter.MACRO_KEY
//            val name = psiElement.getLastChild().getText()
//            val at_macro = name != null && name.length > 0 && name.get(0) == '@'
//            if (psiElement.getType() != null) {
//                color = if (at_macro) C3SyntaxHighlighter.AT_MACRO_METHOD_KEY else C3SyntaxHighlighter.MACRO_METHOD_KEY
//            } else if (at_macro) {
//                color = C3SyntaxHighlighter.AT_MACRO_KEY
//            }
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(color).create()
//        } else if (psiElement is C3TypeName) {
//            val parent = psiElement.getParent()
//            if (parent == null) return
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                .textAttributes(colorForTypeDefinition(parent)).create()
//        }
//    }
//
//    private fun colorForTypeDefinition(definition: PsiElement?): TextAttributesKey {
//        return when (definition) {
//            is C3StructDeclaration -> C3SyntaxHighlighter.STRUCT_NAME_KEY
//            is C3FaultDeclaration -> C3SyntaxHighlighter.FAULT_NAME_KEY
//            is C3EnumDeclaration -> C3SyntaxHighlighter.ENUM_NAME_KEY
//            is C3DefDecl -> C3SyntaxHighlighter.TYPEDEF_NAME_KEY
//            is C3BitstructDeclaration -> C3SyntaxHighlighter.BITSTRUCT_NAME_KEY
//            else -> C3SyntaxHighlighter.TYPE_DEFINITION_KEY
//        }
//    }
//
//    private fun annotate(element: C3DefDecl, annotationHolder: AnnotationHolder) {
//        val ident = element.getAnyIdent()
//        val ident_node = ident.getNode()
//        val is_attribute = ident_node.findChildByType(C3Types.AT_TYPE_IDENT) != null
//        val is_type = !is_attribute && ident_node.findChildByType(C3Types.TYPE_IDENT) != null
//        val is_ident = !is_type && ident_node.findChildByType(C3Types.IDENT) != null
//        val is_const = !is_ident && ident_node.findChildByType(C3Types.CONST_IDENT) != null
//
//        if (!is_attribute && !is_ident && !is_const && !is_type) {
//            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Invalid alias name.")
//                .range(ident)
//                .create()
//            return
//        }
//        val source = element.getDefDeclarationSource()
//        if (source!!.getDefAttrValues() != null && !is_attribute) {
//            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Expected an attribute name here.")
//                .range(ident)
//                .create()
//        }
//        if (is_attribute) {
//            if (source.getDefAttrValues() != null) {
//                if (source.getGenericParameters() != null) {
//                    annotationHolder.newAnnotation(
//                        HighlightSeverity.ERROR,
//                        "Attributes may not have generic parameterization."
//                    )
//                        .range(source.getGenericParameters()!!)
//                        .create()
//                }
//                return
//            }
//            annotationHolder.newAnnotation(
//                HighlightSeverity.ERROR,
//                "An attribute should be followed by a '{ ... }' containing attributes."
//            )
//                .range(ident)
//                .create()
//            return
//        }
//        if (is_ident || is_const) {
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                .textAttributes(C3SyntaxHighlighter.FUNCTION_KEY)
//                .range(ident)
//                .create()
//        } else if (is_type) {
//            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                .textAttributes(C3SyntaxHighlighter.TYPE_KEY).range(ident).create()
//        }
//        if (source.getAnyIdent() != null) {
//            annotationHolder.newAnnotation(
//                HighlightSeverity.ERROR,
//                if (is_type) "A type was expected." else "An identifier was expected."
//            ).range(source.getAnyIdent()!!).create()
//            return
//        }
//        if (is_type) {
//            if (source.getTypedefType() == null) {
//                annotationHolder.newAnnotation(
//                    HighlightSeverity.ERROR, "A type name cannot alias a non-type."
//                ).range(ident).create()
//            }
//            return
//        }
//        if (source.getTypedefType() != null) {
//            annotationHolder.newAnnotation(
//                HighlightSeverity.ERROR, "A capitalized type name was expected as the alias."
//            ).range(ident).create()
//        }
//        if (source.getPathConst() != null && !is_const) {
//            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An alias of a constant must also be all caps.")
//                .range(ident)
//                .create()
//        }
//        if (source.getPathIdent() != null && is_const) {
//            annotationHolder.newAnnotation(
//                HighlightSeverity.ERROR,
//                "A const alias may not alias non-const identifiers."
//            )
//                .range(source.getPathIdent()!!)
//                .create()
//        }
//    }
//
//    private fun is_valid_hex(c: Char): Boolean {
//        return c >= '0' && c <= 'f' && (c <= '9' || c >= 'A') && (c <= 'F' || c >= 'a')
//    }
//
//    private fun is_valid_b64(c: Char): Boolean {
//        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '+' || c == '/' || c == '='
//    }
//
//    private fun annotateBytesError(
//        annotationHolder: AnnotationHolder,
//        error: String,
//        element: ASTNode,
//        start: Int,
//        end: Int
//    ) {
//        val startOffset = element.getTextRange().getStartOffset()
//        annotationHolder.newAnnotation(HighlightSeverity.ERROR, error)
//            .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//            .range(TextRange(startOffset + start, startOffset + end)).create()
//    }
//
//    private fun annotateBytes(bytesExpr: C3BytesExpr, annotationHolder: AnnotationHolder) {
//        val element: ASTNode? = checkNotNull(bytesExpr.getNode().findChildByType(C3TokenSets.BYTES))
//        val text = element!!.getText()
//        assert(text.length > 3)
//        val is_hex = text.get(0) == 'x'
//        val start_offset = if (is_hex) 2 else 4
//        val type = text.get(start_offset - 1)
//        val end = text.length - 1
//        var index = start_offset
//        var digits = 0
//        var has_error = false
//        var error_start = -1
//        val error = if (is_hex) "Hex character expected." else "Invalid base 64 character."
//        while (index < end) {
//            val c = text.get(index)
//            if (c == type) {
//                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index)
//                error_start = -1
//                index++
//                while (text.get(index) != type) index++
//                index++
//                continue
//            }
//            index++
//            if (Character.isWhitespace(c)) {
//                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1)
//                error_start = -1
//                continue
//            }
//            if (is_hex) {
//                if (!is_valid_hex(c)) {
//                    if (error_start < 0) error_start = index - 1
//                    has_error = true
//                    continue
//                }
//                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1)
//                error_start = -1
//                digits++
//                continue
//            }
//            if (!is_valid_b64(c)) {
//                if (error_start < 0) error_start = index - 1
//                has_error = true
//                continue
//            }
//            if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1)
//            error_start = -1
//        }
//        if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1)
//        if (!has_error && digits % 2 != 0) {
//            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An even number of hex characters is required.")
//                .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//                .range(element).create()
//        }
//    }
//
//    private fun annotateString(stringExpr: C3StringExpr, annotationHolder: AnnotationHolder) {
//        for (element in stringExpr.getNode().getChildren(TokenSet.create(C3Types.STRING_LIT, C3Types.CHAR_LIT))) {
//            val text = element.getText()
//            assert(text.length > 1)
//            // No checking of raw strings.
//            if (text.get(0) == '`') continue
//            val len = text.length
//            val start_range_offset = element.getTextRange().getStartOffset()
//            var i = 1
//            while (i < len - 1) {
//                var c = text.get(i)
//                if (c < ' ') {
//                    annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Illegal character")
//                        .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//                        .range(TextRange(start_range_offset + i, start_range_offset + i + 1)).create()
//                    i++
//                    continue
//                }
//                if (c == '\\') {
//                    i++
//                    c = text.get(i)
//                    if (c < ' ') {
//                        annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Illegal character")
//                            .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//                            .range(TextRange(start_range_offset + i, start_range_offset + i + 1)).create()
//                    }
//                    val checked_follow: Int
//                    when (c) {
//                        'x' -> checked_follow = 2
//                        'u' -> checked_follow = 4
//                        'U' -> checked_follow = 8
//                        'a', 'b', 'e', 'f', 'n', 'r', 't', 'v', '"', '\'', '\\', '0' -> {
//                            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                                .range(TextRange(start_range_offset + i - 1, start_range_offset + i + 1))
//                                .textAttributes(C3SyntaxHighlighter.ESCAPE_SEQ_KEY).create()
//                            i++
//                            continue
//                        }
//
//                        else -> {
//                            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Invalid escape sequence.")
//                                .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//                                .range(TextRange(start_range_offset + i - 1, start_range_offset + i + 1))
//                                .create()
//                            i++
//                            continue
//                        }
//                    }
//                    i++
//                    var ok_sequence = true
//                    for (j in 0 until checked_follow) {
//                        val hex = text[i + j]
//                        if (!is_valid_hex(hex)) {
//                            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Unexpected character")
//                                .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
//                                .range(TextRange(start_range_offset + i - 2, start_range_offset + i + j))
//                                .create()
//                            ok_sequence = false
//                            break
//                        }
//                    }
//                    if (ok_sequence) {
//                        annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
//                            .range(TextRange(start_range_offset + i - 2, start_range_offset + i + checked_follow))
//                            .textAttributes(C3SyntaxHighlighter.ESCAPE_SEQ_KEY).create()
//                        i += checked_follow
//                    }
//                }
//                i++
//            }
//        }
//    }

}