package org.c3lang.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
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
        else if (definition instanceof C3TypedefDeclaration || definition instanceof C3DefDecl)
        {
            return C3SyntaxHighlighter.TYPEDEF_NAME_KEY;
        }
        else if (definition instanceof C3BitstructDeclaration)
        {
            return C3SyntaxHighlighter.BITSTRUCT_NAME_KEY;
        }
        return C3SyntaxHighlighter.TYPE_DEFINITION_KEY;
    }

    private void annotate(@NotNull C3DefDecl element, @NotNull AnnotationHolder annotationHolder)
    {
        C3AnyIdent ident = element.getAnyIdent();
        ASTNode ident_node = ident.getNode();
        boolean is_attribute = ident_node.findChildByType(C3Types.AT_TYPE_IDENT) != null;
        boolean is_type = !is_attribute && ident_node.findChildByType(C3Types.TYPE_IDENT) != null;
        boolean is_ident = !is_type && ident_node.findChildByType(C3Types.IDENT) != null;
        boolean is_const = !is_ident && ident_node.findChildByType(C3Types.CONST_IDENT) != null;

        if (!is_attribute && !is_ident && !is_const && !is_type)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Invalid alias name.")
                            .range(ident)
                            .create();
            return;
        }
        if (element.getDistinctInline() != null && !is_type)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "'distinct' and 'inline' may only be used with types.")
                            .range(element.getDistinctInline())
                            .create();
        }
        if (element.getParameterList() != null && !is_attribute)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Unexpected parameter list.")
                            .range(element.getDistinctInline())
                            .create();
        }
        C3DefDeclarationSource source = element.getDefDeclarationSource();
        if (source.getDefAttrValues() != null && !is_attribute)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Expected an attribute name here.")
                            .range(ident)
                            .create();
        }
        if (is_attribute)
        {
            if (source.getDefAttrValues() != null)
            {
                if (source.getGenericParameter() != null)
                {
                    annotationHolder.newAnnotation(HighlightSeverity.ERROR,
                                                   "Attributes may not have generic parameterization.")
                                    .range(source.getGenericParameter())
                                    .create();
                }
                return;
            }
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An attribute should be followed by a '{ ... }' containing attributes.")
                            .range(ident)
                            .create();
            return;
        }
        if (source.getAnyIdent() != null)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR,
                                           is_type ? "A type was expected." : "An identifier was expected.")
                            .range(source.getAnyIdent())
                            .create();
            return;
        }
        if (is_type)
        {
            if (source.getTypedefType() == null)
            {
                annotationHolder.newAnnotation(HighlightSeverity.ERROR, "A type name cannot alias a non-type.")
                                .range(ident)
                                .create();
            }
            return;
        }
        if (source.getTypedefType() != null)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "A capitalized type name was expected as the alias.")
                            .range(ident)
                            .create();
        }
        if (source.getPathConst() != null && !is_const)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An alias of a constant must also be all caps.")
                            .range(ident)
                            .create();
        }
        if (source.getPathIdent() != null && is_const)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "A const alias may not alias non-const identifiers.")
                            .range(source.getPathIdent())
                            .create();
        }
    }

    @Override public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder)
    {
        if (psiElement instanceof C3DefDecl element)
        {
            annotate(element, annotationHolder);
        }
        else if (psiElement instanceof C3IdentAliasName)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.FUNCTION_KEY).create();
        }
        else if (psiElement instanceof C3Type || psiElement instanceof C3OptionalType)
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
