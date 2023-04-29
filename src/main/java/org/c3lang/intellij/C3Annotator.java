package org.c3lang.intellij;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class C3Annotator implements Annotator
{
    private static TextAttributesKey colorForTypeDefiniton(PsiElement definition)
    {
        if (definition instanceof C3StructDeclaration)
        {
            return C3SyntaxHighlighter.STRUCT_NAME_KEY;
        }
        else if (definition instanceof C3FaultDeclaration)
        {
            return C3SyntaxHighlighter.FAULT_NAME_KEY;
        }
        else if (definition instanceof C3EnumDeclaration)
        {
            return C3SyntaxHighlighter.ENUM_NAME_KEY;
        }
        else if (definition instanceof C3TypedefDeclaration)
        {
            return C3SyntaxHighlighter.TYPEDEF_NAME_KEY;
        }
        else if (definition instanceof C3BitstructDeclaration)
        {
            return C3SyntaxHighlighter.BITSTRUCT_NAME_KEY;
        }
        return C3SyntaxHighlighter.TYPE_DEFINITION_KEY;
    }

    @Override public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder)
    {
        if (psiElement instanceof C3Type || psiElement instanceof C3OptionalType)
        {
            PsiElement parent = psiElement.getParent();
            if (parent instanceof C3FuncName || parent instanceof C3MacroName || parent instanceof C3OptionalType) return;
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.TYPE_KEY).create();
        }
        else if (psiElement instanceof C3AttributeName)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.ATTRIBUTE_KEY).create();
        }
        else if (psiElement instanceof C3FuncName element)
        {
            TextAttributesKey color = element.getType() != null ? C3SyntaxHighlighter.METHOD_KEY : C3SyntaxHighlighter.FUNCTION_KEY;
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(color).create();
        }
        else if (psiElement instanceof C3MacroName element)
        {

            TextAttributesKey color = C3SyntaxHighlighter.MACRO_KEY;
            String name = psiElement.getLastChild().getText();
            boolean at_macro = name != null && name.length() > 0 && name.charAt(0) == '@';
            if (element.getType() != null)
            {
                color = at_macro ? C3SyntaxHighlighter.AT_MACRO_METHOD_KEY : C3SyntaxHighlighter.MACRO_METHOD_KEY;
            }
            else if (at_macro)
            {
                color = C3SyntaxHighlighter.AT_MACRO_KEY;
            }
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(color).create();
        }
        else if (psiElement instanceof C3TypeName)
        {
            PsiElement parent = psiElement.getParent();
            if (parent == null) return;
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(colorForTypeDefiniton(parent)).create();
        }
    }
}
