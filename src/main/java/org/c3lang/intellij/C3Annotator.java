package org.c3lang.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.annotation.fix.AddDynamicAttributeFix;
import org.c3lang.intellij.annotation.fix.AddSelfParameterFix;
import org.c3lang.intellij.annotation.fix.ImplementInterfaceMethodsFix;
import org.c3lang.intellij.annotation.AttributeChecks;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.types.CallChecker;
import org.c3lang.intellij.types.DuplicateChecker;
import org.c3lang.intellij.types.InferredType;
import org.c3lang.intellij.types.TypeCanonicalizer;
import org.c3lang.intellij.types.BitstructSupport;
import org.c3lang.intellij.types.TypeChecker;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO move to kotlin after first NPE
public class C3Annotator implements Annotator
{
    private C3Annotator() {}

    public static final C3Annotator INSTANCE = new C3Annotator();

    private static TextAttributesKey colorForTypeDefiniton(PsiElement definition)
    {
        if (definition instanceof C3StructDeclaration)
        {
            return C3SyntaxHighlighter.STRUCT_NAME_KEY;
        }
        else if (definition instanceof C3EnumDeclaration)
        {
            return C3SyntaxHighlighter.ENUM_NAME_KEY;
        }
        else if (definition instanceof C3AliasTypeDecl)
        {
            return C3SyntaxHighlighter.ALIAS_TYPE_NAME_KEY;
        }
        else if (definition instanceof C3TypedefDecl)
        {
            return C3SyntaxHighlighter.TYPEDEF_NAME_KEY;
        }
        else if (definition instanceof C3BitstructDeclaration)
        {
            return C3SyntaxHighlighter.BITSTRUCT_NAME_KEY;
        }
        return C3SyntaxHighlighter.TYPE_DEFINITION_KEY;
    }

    private void annotate(@NotNull C3AliasDecl element, @NotNull AnnotationHolder annotationHolder)
    {
        annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                    .textAttributes(C3SyntaxHighlighter.ALIAS_NAME_KEY).range(element.getAliasName()).create();
        C3AliasDeclarationSource source = element.getAliasDeclarationSource();
        if (source == null) return;
        boolean is_ident = element.getAliasName().getNode().findChildByType(C3Types.IDENT) != null;
        boolean is_at_ident = !is_ident && element.getAliasName().getNode().findChildByType(C3Types.AT_IDENT) != null;
        boolean is_const = !is_at_ident && !is_ident;

        if (source.getPathConst() != null && !is_const)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An alias of a constant must also be all caps.")
                            .range(element.getAliasName())
                            .create();
        }
        if (source.getPathIdent() != null)
        {
            if (is_const)
            {
                annotationHolder.newAnnotation(HighlightSeverity.ERROR, "A const alias may not alias non-const identifiers.")
                                .range(source.getPathIdent())
                                .create();
            }
            else if (is_at_ident)
            {
                annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An @-alias alias may not alias non-@ identifiers.")
                                .range(source.getPathIdent())
                                .create();
            }
        }
        if (source.getPathAtIdent() != null)
        {
            if (is_const)
            {
                annotationHolder.newAnnotation(HighlightSeverity.ERROR, "A const alias may not alias an @-identifiers.")
                                .range(source.getPathAtIdent())
                                .create();
            }
            else if (is_ident)
            {
                annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An @-alias may not alias non-@ identifiers.")
                                .range(source.getPathAtIdent())
                                .create();
            }
        }
    }

    private void annotate(@NotNull C3ImportPath element, @NotNull AnnotationHolder annotationHolder)
    {
        if (element.hasValidImportAttributes()) return;

        C3Attributes attributes = element.getAttributes();
        if (attributes == null) return;

        annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Only @public is valid on imports.")
                        .range(attributes)
                        .create();
    }

    private static boolean is_valid_hex(char c)
    {
        return c >= '0' && c <= 'f' && (c <= '9' || c >= 'A') && (c <= 'F' || c >= 'a');
    }

    private static boolean is_valid_b64(char c)
    {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '+' || c == '/' ||
               c == '=';
    }

    private void annotateBytesError(@NotNull AnnotationHolder annotationHolder, String error, ASTNode element, int start, int end)
    {
        int startOffset = element.getTextRange().getStartOffset();
        annotationHolder.newAnnotation(HighlightSeverity.ERROR, error)
                        .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                        .range(new TextRange(startOffset + start, startOffset + end)).create();

    }

    private void annotateBytes(@NotNull C3BytesExpr bytesExpr, @NotNull AnnotationHolder annotationHolder)
    {
        ASTNode element = bytesExpr.getNode().findChildByType(C3TokenSets.BYTES);
        assert(element != null);
        String text = element.getText();
        assert(text.length() > 3);
        boolean is_hex = text.charAt(0) == 'x';
        int start_offset = is_hex ? 2 : 4;
        char type = text.charAt(start_offset - 1);
        int end = text.length() - 1;
        int index = start_offset;
        int digits = 0;
        boolean has_error = false;
        int error_start = -1;
        String error = is_hex ? "Hex character expected." : "Invalid base 64 character.";
        while (index < end)
        {
            char c = text.charAt(index);
            if (c == type)
            {
                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index);
                error_start = -1;
                index++;
                while (text.charAt(index) != type) index++;
                index++;
                continue;
            }
            index++;
            if (Character.isWhitespace(c))
            {
                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1);
                error_start = -1;
                continue;
            }
            if (is_hex)
            {
                if (!is_valid_hex(c))
                {
                    if (error_start < 0) error_start = index - 1;
                    has_error = true;
                    continue;
                }
                if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1);
                error_start = -1;
                digits++;
                continue;
            }
            if (!is_valid_b64(c))
            {
                if (error_start < 0) error_start = index - 1;
                has_error = true;
                continue;
            }
            if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1);
            error_start = -1;
        }
        if (error_start > -1) annotateBytesError(annotationHolder, error, element, error_start, index - 1);
        if (!has_error && digits % 2 != 0)
        {
            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "An even number of hex characters is required.")
                            .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                            .range(element).create();

        }
    }
    private void annotateString(@NotNull C3StringExpr stringExpr, @NotNull AnnotationHolder annotationHolder)
    {

        for (ASTNode element : stringExpr.getNode().getChildren(TokenSet.create(C3Types.STRING_LIT, C3Types.CHAR_LIT)))
        {
            String text = element.getText();
            assert(text.length() > 1);
            // No checking of raw strings.
            if (text.charAt(0) == '`') continue;
            int len = text.length();
            int start_range_offset = element.getTextRange().getStartOffset();
            for (int i = 1; i < len - 1; i++)
            {
                char c = text.charAt(i);
                if (c < ' ')
                {
                    annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Illegal character")
                                    .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                                    .range(new TextRange(start_range_offset + i, start_range_offset+ i + 1)).create();
                    continue;
                }
                if (c == '\\')
                {
                    i++;
                    c = text.charAt(i);
                    if (c < ' ')
                    {
                        annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Illegal character")
                                        .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                                        .range(new TextRange(start_range_offset + i, start_range_offset + i + 1)).create();
                    }
                    int checked_follow;
                    switch (c)
                    {
                        case 'x':
                            checked_follow = 2;
                            break;
                        case 'u':
                            checked_follow = 4;
                            break;
                        case 'U':
                            checked_follow = 8;
                            break;
                        case 'a':
                        case 'b':
                        case 'e':
                        case 'f':
                        case 'n':
                        case 'r':
                        case 't':
                        case 'v':
                        case '"':
                        case '\'':
                        case '\\':
                        case '0':
                            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                            .range(new TextRange(start_range_offset + i - 1, start_range_offset + i + 1))
                                            .textAttributes(C3SyntaxHighlighter.ESCAPE_SEQ_KEY).create();
                            continue;
                        default:
                            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Invalid escape sequence.")
                                            .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                                            .range(new TextRange(start_range_offset + i - 1, start_range_offset + i + 1))
                                            .create();
                            continue;
                    }
                    i++;
                    boolean ok_sequence = true;
                    for (int j = 0; j < checked_follow; j++)
                    {
                        char hex = text.charAt(i + j);
                        if (!is_valid_hex(hex))
                        {
                            annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Unexpected character")
                                            .textAttributes(C3SyntaxHighlighter.INVALID_ESCAPE_SEQ_KEY)
                                            .range(new TextRange(start_range_offset + i - 2, start_range_offset + i + j))
                                            .create();
                            ok_sequence = false;
                            break;
                        }
                    }
                    if (ok_sequence)
                    {
                        annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                        .range(new TextRange(start_range_offset + i - 2, start_range_offset + i + checked_follow))
                                        .textAttributes(C3SyntaxHighlighter.ESCAPE_SEQ_KEY).create();
                        i += checked_follow;
                    }

                }
            }
        }
    }

    @Override public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder)
    {
        if (psiElement instanceof C3BytesExpr expr)
        {
            annotateBytes(expr, annotationHolder);
        }
        else if (psiElement instanceof C3StringExpr expr)
        {
            annotateString(expr, annotationHolder);
        }
        else if (psiElement instanceof C3AliasDecl element)
        {
            annotate(element, annotationHolder);
        }
        else if (psiElement instanceof C3ImportPath element)
        {
            annotate(element, annotationHolder);
        }
        else if (psiElement instanceof C3AttrdefDecl element)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                        .textAttributes(C3SyntaxHighlighter.ATTRDEF_ATTRIBUTE_KEY).range(element.getAttributeUserName()).create();
        }
        else if (psiElement instanceof C3AliasTypeDecl element)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                        .textAttributes(C3SyntaxHighlighter.ALIAS_TYPE_NAME_KEY).range(element.getTypeName()).create();
        }
        else if (psiElement instanceof C3TypedefDecl element)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                        .textAttributes(C3SyntaxHighlighter.TYPEDEF_NAME_KEY).range(element.getTypeName()).create();
        }
        else if (psiElement instanceof C3Type || psiElement instanceof C3OptionalType)
        {
            PsiElement parent = psiElement.getParent();
            if (parent instanceof C3FuncName || parent instanceof C3MacroName || parent instanceof C3OptionalType) return;
            if (psiElement instanceof C3Type c3Type && c3Type.getBaseType() != null && c3Type.getBaseType().getPath() != null)
            {
                C3Path path = c3Type.getBaseType().getPath();
                TextRange range = new TextRange(path.getTextRange().getEndOffset(), c3Type.getTextRange().getEndOffset());
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.TYPE_KEY).range(range).create();
            }
            else
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.TYPE_KEY).create();
            }
        }
        else if (psiElement instanceof C3AttributeName)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.ATTRIBUTE_KEY).create();
        }
        else if (psiElement instanceof C3FuncName element)
        {
            if (element.getType() != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.TYPE_KEY).range(element.getType()).create();
                PsiElement last = element.getLastChild();
                if (last != null)
                {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.METHOD_KEY).range(last).create();
                }
            }
            else
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(C3SyntaxHighlighter.FUNCTION_KEY).create();
            }
        }
        else if (psiElement instanceof C3MacroName element)
        {

            TextAttributesKey color = C3SyntaxHighlighter.MACRO_KEY;
            String name = psiElement.getLastChild().getText();
            boolean at_macro = name != null && !name.isEmpty() && name.charAt(0) == '@';
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
        else if (psiElement instanceof C3ModulePath || psiElement instanceof C3ImportPath)
        {
            annotateModulePath(psiElement, annotationHolder);
        }
        else if (psiElement instanceof C3Path path)
        {
            annotatePath(path, annotationHolder);
        }
        else if (psiElement instanceof C3Parameter param)
        {
            annotateParameter(param, annotationHolder);
        }
        else if (psiElement instanceof C3LocalDeclAfterType localDecl)
        {
            PsiElement ident = localDecl.getNameIdentElement();
            if (ident != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(ident).create();
            }
        }
        else if (psiElement instanceof C3VarDecl varDecl)
        {
            ASTNode ident = varDecl.getNode().findChildByType(C3Types.IDENT);
            if (ident != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(ident.getTextRange()).create();
            }
        }
        else if (psiElement instanceof C3ForeachVar foreachVar)
        {
            ASTNode ident = foreachVar.getNode().findChildByType(C3Types.IDENT);
            if (ident != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(ident.getTextRange()).create();
            }
        }
        else if (psiElement instanceof C3IdentifierList identList && psiElement.getParent() instanceof C3StructMemberDeclaration)
        {
            for (ASTNode node : identList.getNode().getChildren(null))
            {
                if (node.getElementType() == C3Types.IDENT)
                {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                        .textAttributes(C3SyntaxHighlighter.FIELD_KEY).range(node.getTextRange()).create();
                }
            }
        }
        else if (psiElement instanceof C3StructMemberDeclaration memberDecl)
        {
            if (memberDecl.getIdentifierList() == null)
            {
                ASTNode ident = memberDecl.getNode().findChildByType(C3Types.IDENT);
                if (ident != null)
                {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                        .textAttributes(C3SyntaxHighlighter.FIELD_KEY).range(ident.getTextRange()).create();
                }
            }
        }
        else if (psiElement instanceof C3BitstructDef bitDef)
        {
            PsiElement nameElement = bitDef.getNameIdentElement();
            if (nameElement != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.FIELD_KEY).range(nameElement).create();
            }
        }
        else if (psiElement instanceof C3BitstructSimpleDef bitSimpleDef)
        {
            PsiElement nameElement = bitSimpleDef.getNameIdentElement();
            if (nameElement != null)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.FIELD_KEY).range(nameElement).create();
            }
        }
        else if (psiElement instanceof C3AccessIdent accessIdent)
        {
            annotateAccessIdent(accessIdent, annotationHolder);
        }
        else if (psiElement instanceof C3PathIdent pathIdent)
        {
            annotatePathIdent(pathIdent, annotationHolder);
        }
        else if (psiElement instanceof C3LocalDeclarationStmt localDecl)
        {
            annotateLocalDeclaration(localDecl, annotationHolder);
        }
        else if (psiElement instanceof C3BinaryExpr binaryExpr)
        {
            annotateAssignment(binaryExpr, annotationHolder);
        }
        else if (psiElement instanceof C3ReturnStmt returnStmt)
        {
            annotateReturnStmt(returnStmt, annotationHolder);
        }
        else if (psiElement instanceof C3CallExpr callExpr)
        {
            CallChecker.checkCall(callExpr, annotationHolder);
            AttributeChecks.checkCallAttributes(callExpr, annotationHolder);
        }
        else if (psiElement instanceof C3IfStmt ifStmt)
        {
            annotateIfCatchTry(ifStmt, annotationHolder);
        }
        else if (psiElement instanceof C3StructDeclaration structDecl)
        {
            annotateStructDeclaration(structDecl, annotationHolder);
        }
        else if (psiElement instanceof C3InterfaceImpl impl)
        {
            annotateInterfaceImpl(impl, annotationHolder);
        }
        else if (psiElement instanceof C3FuncDef funcDef)
        {
            annotateFuncDef(funcDef, annotationHolder);
        }
        else if (psiElement instanceof C3FuncDefinition funcDefinition)
        {
            annotateFuncDefinition(funcDefinition, annotationHolder);
        }
        else if (psiElement instanceof C3MacroDefinition macroDef)
        {
            DuplicateChecker.checkMacro(macroDef, annotationHolder);
            annotateMacroDefinition(macroDef, annotationHolder);
        }
        else if (psiElement instanceof C3UnaryExpr unaryExpr)
        {
            annotateCast(unaryExpr, annotationHolder);
            annotateBitstructAddress(unaryExpr, annotationHolder);
        }
        else if (psiElement instanceof C3Attribute attribute)
        {
            AttributeChecks.checkAttribute(attribute, annotationHolder);
        }
    }

    private static boolean isInThenBranch(@NotNull C3IfStmt ifStmt, @NotNull C3PathIdent pathIdent)
    {
        if (ifStmt.getCompoundStatement() != null)
        {
            return PsiTreeUtil.isAncestor(ifStmt.getCompoundStatement(), pathIdent, false);
        }
        return ifStmt.getStatement() != null && PsiTreeUtil.isAncestor(ifStmt.getStatement(), pathIdent, false);
    }

    private static boolean bindsCatchOrTryName(@NotNull C3IfStmt ifStmt, @NotNull String name)
    {
        C3ParenCond paren = ifStmt.getParenCond();
        C3Cond cond = paren != null ? paren.getCond() : null;
        if (cond == null) return false;
        for (C3CatchUnwrap unwrap : PsiTreeUtil.findChildrenOfType(cond, C3CatchUnwrap.class))
        {
            String binding = unwrap instanceof C3CatchUnwrapMixin mixin ? mixin.getBindingName() : null;
            if (name.equals(binding)) return true;
        }
        for (C3TryUnwrapChain chain : PsiTreeUtil.findChildrenOfType(cond, C3TryUnwrapChain.class))
        {
            for (C3TryUnwrap unwrap : chain.getTryUnwrapList())
            {
                String binding = unwrap instanceof C3TryUnwrapMixin mixin ? mixin.getBindingName() : null;
                if (name.equals(binding)) return true;
            }
        }
        return false;
    }

    /**
     * {@code if (catch)} and {@code if (try)} only accept Optional-tested
     * expressions: anything with a known non-Optional type is a compile
     * error in c3c, so it is flagged here. Unknown types (failed inference,
     * comptime parameters) stay silent.
     */
    private void annotateIfCatchTry(@NotNull C3IfStmt ifStmt, @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(ifStmt.getProject())) return;
        C3ParenCond paren = ifStmt.getParenCond();
        C3Cond cond = paren != null ? paren.getCond() : null;
        if (cond == null) return;
        for (C3CatchUnwrap unwrap : PsiTreeUtil.findChildrenOfType(cond, C3CatchUnwrap.class))
        {
            if (unwrap.getCatchUnwrapList() == null) continue;
            for (C3Expr tested : unwrap.getCatchUnwrapList().getExprList())
            {
                checkUnwrapOperand(tested, false, holder);
            }
        }
        for (C3TryUnwrapChain chain : PsiTreeUtil.findChildrenOfType(cond, C3TryUnwrapChain.class))
        {
            for (C3TryUnwrap unwrap : chain.getTryUnwrapList())
            {
                if (unwrap.getExpr() != null) checkUnwrapOperand(unwrap.getExpr(), true, holder);
            }
        }
    }

    private void checkUnwrapOperand(@NotNull C3Expr tested, boolean isTry, @NotNull AnnotationHolder holder)
    {
        InferredType inferred;
        try
        {
            inferred = TypeChecker.infer(tested);
        }
        catch (Exception e)
        {
            return;
        }
        if (inferred == null || TypeChecker.isOptionalName(inferred.getName())) return;
        if (TypeCanonicalizer.isComptimeParam(inferred.getName())) return;
        String message = isTry
            ? "Expected an optional expression to 'try' here. If it isn't an optional, remove 'try'."
            : "This expression is not optional, did you add it by mistake?";
        holder.newAnnotation(HighlightSeverity.ERROR, message).range(tested).create();
    }

    private void annotateStructDeclaration(@NotNull C3StructDeclaration structDecl, @NotNull AnnotationHolder holder)
    {
        C3TypeName typeName = structDecl.getTypeName();
        C3StructBody body = structDecl.getStructBody();
        if (body != null && body.getStructMemberDeclarationList().isEmpty())
        {
            PsiElement anchor = typeName.getNameIdentifier() != null ? typeName.getNameIdentifier() : typeName;
            holder.newAnnotation(HighlightSeverity.ERROR, "Zero sized structs are not permitted.")
                .range(anchor)
                .create();
        }

        Project project = structDecl.getProject();
        if (DumbService.isDumb(project)) return;

        String structName = typeName.getText().strip();
        FullyQualifiedName structFqn = new FullyQualifiedName(ModuleName.from(structDecl), structName);
        for (FullyQualifiedName iface : InterfaceService.INSTANCE.getImplementedInterfaces(structFqn, project))
        {
            List<C3FuncDef> missing =
                InterfaceService.INSTANCE.findMissingInterfaceMethods(structFqn, iface, project);
            for (C3FuncDef missed : missing)
            {
                PsiElement anchor = contractAnchor(structDecl, iface, typeName);
                var annotation = holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Struct '" + structName + "' does not implement interface method '"
                            + iface.getFullName() + "." + missed.getNameIdent() + "'.")
                    .range(anchor);
                if (missed == missing.get(0))
                {
                    annotation.withFix(new ImplementInterfaceMethodsFix(structDecl, iface));
                }
                annotation.create();
            }
        }
    }

    private static @NotNull PsiElement contractAnchor(
            @NotNull C3StructDeclaration structDecl,
            @NotNull FullyQualifiedName iface,
            @NotNull C3TypeName fallback)
    {
        C3InterfaceImpl impl = structDecl.getInterfaceImpl();
        if (impl != null)
        {
            if (isContractText(impl.getTypeName().getText(), iface)) return impl.getTypeName();
            for (C3Type contractType : impl.getTypeList())
            {
                if (isContractText(contractType.getText(), iface)) return contractType;
            }
        }
        return fallback.getNameIdentifier() != null ? fallback.getNameIdentifier() : fallback;
    }

    private static boolean isContractText(@NotNull String text, @NotNull FullyQualifiedName iface)
    {
        String clean = text.strip();
        return clean.equals(iface.getFullName())
            || clean.equals(iface.getName())
            || clean.endsWith("::" + iface.getName());
    }

    private void annotateFuncDef(@NotNull C3FuncDef funcDef, @NotNull AnnotationHolder holder)
    {
        annotateReturnCoverage(funcDef, holder);
        DuplicateChecker.checkFunction(funcDef, holder);
        annotateMainOptional(funcDef, holder);

        String ownerText = InterfaceService.methodOwnerTypeName(funcDef);
        if (ownerText == null) return;
        if (PsiTreeUtil.getParentOfType(funcDef, C3InterfaceBody.class) != null) return;

        Project project = funcDef.getProject();
        if (DumbService.isDumb(project)) return;

        String implName = funcDef.getNameIdent();
        if (implName == null) return;

        if (!InterfaceService.firstParameterMatchesOwner(funcDef, ownerText))
        {
            String ownerShort = InterfaceService.shortName(ownerText);
            String paramName = freeSelfParameterName(funcDef);
            String example = paramName.equals("self") ? "&self" : ownerShort + "* " + paramName;
            PsiElement anchor = funcDef.getNameIdentifier() != null ? funcDef.getNameIdentifier() : funcDef;
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Method '" + ownerText + "." + implName + "' must start with an argument of type '"
                        + ownerText + "', e.g. '" + example + "'.")
                .range(anchor)
                .withFix(new AddSelfParameterFix(funcDef, ownerShort, paramName))
                .create();
        }

        FullyQualifiedName ownerFqn = InterfaceService.resolveOwnerType(ownerText, ModuleName.from(funcDef));
        for (FullyQualifiedName iface : InterfaceService.INSTANCE.getImplementedInterfaces(ownerFqn, project))
        {
            for (C3FuncDef interfaceMethod : InterfaceService.INSTANCE.getInterfaceMethods(iface, project))
            {
                if (!implName.equals(interfaceMethod.getNameIdent())) continue;
                if (InterfaceService.hasAttribute(funcDef, "dynamic")) return;
                PsiElement anchor = funcDef.getNameIdentifier() != null ? funcDef.getNameIdentifier() : funcDef;
                holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Method '" + ownerText + "." + implName + "' implements interface method '"
                            + iface.getFullName() + "." + implName + "' and must be marked '@dynamic'.")
                    .range(anchor)
                    .withFix(new AddDynamicAttributeFix(funcDef))
                    .create();
                return;
            }
        }
    }

    private static @NotNull String freeSelfParameterName(@NotNull C3FuncDef funcDef)
    {
        List<String> taken = InterfaceService.collectParameterNames(funcDef);
        if (!taken.contains("self")) return "self";
        if (!taken.contains("this")) return "this";
        return "self_";
    }

    /**
     * Expression bodies live on the definition level
     * (`func_definition ::= func_def (macro_func_body | EOS)`), so the
     * `=> expr` range is only annotatable here, not on the bare `func_def`.
     */
    private void annotateFuncDefinition(@NotNull C3FuncDefinition definition, @NotNull AnnotationHolder holder)
    {
        C3FuncDef funcDef = definition.getFuncDef();
        if (funcDef == null) return;
        annotateImpliesBody(funcDef, holder);
    }

    /**
     * Expression bodies (`fn int f(int x) => x * x;`, macro `=>` bodies) are
     * `{ return expr; }` in disguise: the expression must fit the declared
     * return type. Void functions returning an expression body are fine.
     */
    private void annotateImpliesBody(@NotNull C3FuncDef funcDef, @NotNull AnnotationHolder holder)
    {
        C3FuncDefinition definition = PsiTreeUtil.getParentOfType(funcDef, C3FuncDefinition.class);
        C3MacroFuncBody body = definition != null ? definition.getMacroFuncBody() : null;
        checkImpliesBody(body, funcDef.getReturnType(), funcDef.getProject(), ModuleName.from(funcDef), holder);
    }

    private void checkImpliesBody(
            @Nullable C3MacroFuncBody body,
            @Nullable ShortType returnType,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull AnnotationHolder holder)
    {
        if (body == null || DumbService.isDumb(project)) return;
        C3Expr expr;
        try
        {
            if (body.getCompoundStatement() != null) return;
            expr = body.getExpr();
        }
        catch (Exception e)
        {
            return;
        }
        if (expr == null) return;
        String returnText = returnType != null && returnType.getValue() != null ? returnType.getValue() : "void";
        if (TypeChecker.isVoidType(returnText.strip())) return;
        String error;
        try
        {
            error = TypeChecker.returnError(project, contextModule, returnText, TypeChecker.infer(expr), expr);
        }
        catch (Exception e)
        {
            return;
        }
        if (error != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(expr).create();
    }

    /**
     * The program entry point cannot return an Optional.
     */
    private void annotateMainOptional(@NotNull C3FuncDef funcDef, @NotNull AnnotationHolder holder)
    {
        if (InterfaceService.methodOwnerTypeName(funcDef) != null) return;
        FullyQualifiedName fqName;
        try
        {
            fqName = funcDef.getFqName();
        }
        catch (Exception e)
        {
            return;
        }
        if (fqName == null || !"main".equals(fqName.getName())) return;
        ShortType returnType = funcDef.getReturnType();
        if (returnType == null || returnType.getValue() == null) return;
        if (!TypeChecker.isOptionalName(returnType.getValue().strip())) return;
        PsiElement anchor = funcDef.getNameIdentifier() != null ? funcDef.getNameIdentifier() : funcDef;
        holder.newAnnotation(HighlightSeverity.ERROR, "The 'main' function cannot return an Optional.")
            .range(anchor)
            .create();
    }

    private void annotateMacroDefinition(@NotNull C3MacroDefinition macroDef, @NotNull AnnotationHolder holder)
    {
        C3MacroFuncBody macroBody;
        try
        {
            macroBody = macroDef.getMacroFuncBody();
        }
        catch (Exception e)
        {
            macroBody = null;
        }
        if (macroBody != null)
        {
            ShortType macroReturn = macroDef.getReturnType();
            checkImpliesBody(macroBody, macroReturn, macroDef.getProject(),
                ModuleName.from(macroDef), holder);
        }
        String name = macroDef.getName();
        if (name == null || name.isEmpty() || name.charAt(0) == '@') return;
        // `@safemacro` explicitly allows dropping the `@` prefix.
        if (AttributeSpecs.hasAttribute(macroDef.getAttributes(), "safemacro")) return;
        C3MacroParams params = macroDef.getMacroParams();
        if (params == null || params.getParameterList() == null) return;
        for (C3ParamDecl decl : params.getParameterList().getParamDeclList())
        {
            C3Parameter parameter = decl.getParameter();
            if (parameter == null) continue;
            String text = parameter.getText();
            if (text == null) continue;
            String clean = text.strip();
            if (clean.startsWith("&") || clean.startsWith("#"))
            {
                // A warning, not an error: the compiler accepts such macros
                // in practice (e.g. stdlib method-macros), the `@` prefix is
                // merely the recommended style.
                PsiElement anchor = macroDef.getNameIdentifier() != null ? macroDef.getNameIdentifier() : macroDef;
                holder.newAnnotation(
                        HighlightSeverity.WARNING,
                        "Macro '" + name + "' uses reference/expression parameters and should have a name starting with '@'.")
                    .range(anchor)
                    .create();
                return;
            }
        }
    }

    private void annotateCast(@NotNull C3UnaryExpr unary, @NotNull AnnotationHolder holder)
    {
        C3UnaryOp op = unary.getUnaryOp();
        C3Type castType = op.getType();
        if (castType == null) return;
        if (DumbService.isDumb(unary.getProject())) return;
        C3Expr operand = unary.getExpr();
        if (operand == null) return;
        InferredType source = TypeChecker.infer(operand);
        TypeChecker.CastDiagnostic diagnostic = TypeChecker.checkCast(
            unary.getProject(), ModuleName.from(unary), castType.getText(), source, operand);
        if (diagnostic == null) return;
        HighlightSeverity severity = diagnostic.warning ? HighlightSeverity.WEAK_WARNING : HighlightSeverity.ERROR;
        holder.newAnnotation(severity, diagnostic.message).range(op).create();
    }

    /**
     * Taking the address of a bitstruct field is forbidden: the field has no
     * address of its own (verified against {@code c3c}).
     */
    private void annotateBitstructAddress(@NotNull C3UnaryExpr unary, @NotNull AnnotationHolder holder)
    {
        C3UnaryOp op = unary.getUnaryOp();
        if (op.getType() != null || op.getText() == null || !op.getText().strip().equals("&")) return;
        if (DumbService.isDumb(unary.getProject())) return;
        C3Expr operand = unary.getExpr();
        if (!(operand instanceof C3CallExpr call)
            || call.getCallExprTail() == null
            || call.getCallExprTail().getAccessIdent() == null) return;
        PsiElement resolved;
        try
        {
            resolved = call.getCallExprTail().getAccessIdent().getReference().resolve();
        }
        catch (Exception e)
        {
            return;
        }
        if (resolved instanceof C3BitstructDef || resolved instanceof C3BitstructSimpleDef)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "You cannot take the address of a bitstruct member.")
                .range(unary)
                .create();
        }
    }

    private void annotateInterfaceImpl(@NotNull C3InterfaceImpl impl, @NotNull AnnotationHolder holder)
    {
        Project project = impl.getProject();
        if (DumbService.isDumb(project)) return;
        ModuleName contextModule = ModuleName.from(impl);
        List<ModuleName> imports = ModuleName.getImportList(impl);

        C3TypeName firstName = impl.getTypeName();
        PsiElement firstAnchor = firstName.getNameIdentifier() != null ? firstName.getNameIdentifier() : firstName;
        checkContractReference(firstAnchor, firstName.getText(), contextModule, imports, project, holder);

        for (C3Type contractType : impl.getTypeList())
        {
            PsiElement anchor = contractType;
            if (contractType.getBaseType() != null && contractType.getBaseType().getNameIdentElement() != null)
            {
                anchor = contractType.getBaseType().getNameIdentElement();
            }
            checkContractReference(anchor, contractType.getText(), contextModule, imports, project, holder);
        }
    }

    private void checkContractReference(
            @NotNull PsiElement anchor,
            @NotNull String contractText,
            @Nullable ModuleName contextModule,
            @NotNull List<ModuleName> imports,
            @NotNull Project project,
            @NotNull AnnotationHolder holder)
    {
        List<FullyQualifiedName> candidates =
            InterfaceService.INSTANCE.contractCandidates(contractText, contextModule, imports);
        if (candidates.isEmpty()) return;
        for (FullyQualifiedName iface : candidates)
        {
            if (!InterfaceService.INSTANCE.findInterfaceDefinitions(iface, project).isEmpty()) return;
        }
        for (FullyQualifiedName iface : candidates)
        {
            if (!InterfaceService.INSTANCE.findTypeDeclarations(iface, project).isEmpty())
            {
                holder.newAnnotation(HighlightSeverity.ERROR, "'" + contractText.strip() + "' is not an interface.")
                    .range(anchor)
                    .create();
                return;
            }
        }
        holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved interface '" + contractText.strip() + "'.")
            .range(anchor)
            .create();
    }

    private void annotateLocalDeclaration(@NotNull C3LocalDeclarationStmt decl, @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(decl.getProject())) return;
        C3DeclStmtAfterType after = decl.getDeclStmtAfterType();
        if (after == null) return;
        String targetText = decl.getOptionalType().getType().getText();
        if (targetText.isBlank()) return;
        boolean nullableTarget = decl.getOptionalType().getNode().findChildByType(C3Types.QUESTION) != null;
        if (nullableTarget && TypeChecker.isVoidType(targetText.strip()))
        {
            // `void?` has no variable representation, only a return type.
            C3LocalDeclAfterType first = after.getLocalDeclAfterTypeList().isEmpty()
                ? null
                : after.getLocalDeclAfterTypeList().get(0);
            PsiElement anchor = first != null && first.getNameIdentifier() != null
                ? first.getNameIdentifier()
                : decl;
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Optional void (void?) cannot be used as a variable type, only as a function return type.")
                .range(anchor)
                .create();
            return;
        }
        if (nullableTarget && !TypeChecker.isOptionalName(targetText.strip())) targetText = targetText.strip() + "?";
        String typeofTarget = TypeChecker.resolveTypeofTarget(decl.getOptionalType().getType());
        if (typeofTarget != null) targetText = typeofTarget;
        for (C3LocalDeclAfterType declarator : after.getLocalDeclAfterTypeList())
        {
            if (declarator.getNameIdent() != null && declarator.getNameIdent().startsWith("$")) continue;
            C3Expr init = declarator.getExpr();
            if (init == null) continue;
            InferredType source = TypeChecker.infer(init);
            if (nullableTarget && source != null && source.getKind() == InferredType.Kind.NULL) continue;
            String error = TypeChecker.assignmentError(decl.getProject(), ModuleName.from(decl), targetText, source, init);
            if (error != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(init).create();
            annotateBitstructInit(decl.getProject(), ModuleName.from(decl), targetText, init, holder);
        }
    }

    private void annotateAssignment(@NotNull C3BinaryExpr binary, @NotNull AnnotationHolder holder)
    {
        String assignOp = TypeChecker.assignmentOperator(binary);
        if (assignOp == null) return;
        if (DumbService.isDumb(binary.getProject())) return;
        C3Expr rhs = binary.getRight();
        if (rhs == null) return;
        // `b = c ? x : y` parses with the assignment as the ternary condition,
        // but assignment binds loosest in C3: the real right-hand side is the
        // whole ternary. Walk up while this assignment is the condition.
        C3TernaryExpr outerTernary = outermostTernaryCondition(binary);
        if (outerTernary != null) rhs = outerTernary;
        String lhsType = assignmentTargetType(binary.getLeft());
        if (lhsType == null) return;
        String error = TypeChecker.assignmentError(binary.getProject(), ModuleName.from(binary), lhsType, TypeChecker.infer(rhs), rhs, assignOp);
        if (error != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(rhs).create();
        if ("=".equals(assignOp))
        {
            C3PsiElement bitField = bitstructFieldTarget(binary.getLeft());
            if (bitField != null)
            {
                String truncation = BitstructSupport.bitstructTruncationError(bitField, TypeChecker.infer(rhs));
                if (truncation != null) holder.newAnnotation(HighlightSeverity.ERROR, truncation).range(rhs).create();
            }
            annotateBitstructInit(binary.getProject(), ModuleName.from(binary), lhsType, rhs, holder);
        }
    }

    /**
     * Designated bitstruct initializers ({@code Sb t = { .a = 2, .b }}):
     * unknown members and over-wide constants are rejected like c3c
     * (`This is not a valid member of 'Sb'.`, truncation); a valueless
     * bool shorthand ({@code .b}) stays unchecked.
     */
    private void annotateBitstructInit(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull String targetText,
            @NotNull C3Expr init,
            @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(project)) return;
        if (BitstructSupport.bitstructBacking(targetText, project, contextModule) == null) return;
        if (!(init instanceof C3InitListExpr initList)
            || initList.getInitializerList() == null
            || initList.getInitializerList().getArgList() == null) return;
        String shortTarget = TypeChecker.shortName(targetText);
        for (C3Arg arg : initList.getInitializerList().getArgList().getArgList())
        {
            String name = designatedInitName(arg);
            if (name == null) continue;
            C3PsiElement field = BitstructSupport.findBitstructField(targetText, name, project, contextModule);
            if (field == null)
            {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        "This is not a valid member of '" + shortTarget + "'.")
                    .range(arg)
                    .create();
                continue;
            }
            C3Expr value = arg.getExpr();
            if (value == null) continue;
            InferredType inferred = TypeChecker.infer(value);
            String fieldType = BitstructSupport.bitstructFieldTypeText(field);
            if (fieldType != null)
            {
                String error = TypeChecker.assignmentError(project, contextModule, fieldType, inferred, value);
                if (error != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(value).create();
            }
            String truncation = BitstructSupport.bitstructTruncationError(field, inferred);
            if (truncation != null) holder.newAnnotation(HighlightSeverity.ERROR, truncation).range(value).create();
        }
    }

    private static @Nullable String designatedInitName(@NotNull C3Arg arg)
    {
        C3ParamPath path;
        try
        {
            path = arg.getParamPath();
        }
        catch (Exception e)
        {
            return null;
        }
        if (path == null || path.getText() == null) return null;
        String text = path.getText().strip();
        if (!text.startsWith(".")) return null;
        text = text.substring(1);
        if (!text.matches("[A-Za-z_][A-Za-z_0-9]*")) return null;
        return text;
    }

    /**
     * Outermost ternary having this assignment as its condition
     * ({@code b = c ? x : y}), or {@code null}. Parenthesized conditions
     * ({@code (b = c) ? x : y}) do not qualify: the assignment really is the
     * condition there.
     */
    private static @Nullable C3TernaryExpr outermostTernaryCondition(@NotNull C3BinaryExpr binary)
    {
        PsiElement current = binary;
        C3TernaryExpr result = null;
        while (current.getParent() instanceof C3TernaryExpr ternary)
        {
            List<C3Expr> parts = ternary.getExprList();
            if (parts.isEmpty() || parts.get(0) != current) break;
            result = ternary;
            current = ternary;
        }
        return result;
    }

    private static @Nullable String assignmentTargetType(@NotNull C3Expr lhs)
    {
        if (lhs instanceof C3PathIdentExpr pathIdentExpr)
        {
            C3PathIdent pathIdent = pathIdentExpr.getPathIdent();
            if (pathIdent.getPath() != null) return null;
            PsiElement resolved;
            try
            {
                resolved = pathIdent.getReference().resolve();
            }
            catch (Exception e)
            {
                return null;
            }
            if (resolved == null) return null;
            return TypeChecker.assignedTypeText(resolved);
        }
        if (lhs instanceof C3CallExpr call
            && call.getCallExprTail() != null
            && call.getCallExprTail().getCallInvocation() == null
            && call.getCallExprTail().getAccessIdent() != null)
        {
            PsiElement resolved;
            try
            {
                resolved = call.getCallExprTail().getAccessIdent().getReference().resolve();
            }
            catch (Exception e)
            {
                return null;
            }
            if (resolved instanceof C3StructMemberDeclaration member && member.getStructPathType() != null)
            {
                return member.getStructPathType().getFullName();
            }
            String bitFieldType = BitstructSupport.bitstructFieldTypeText(resolved);
            if (bitFieldType != null) return bitFieldType;
        }
        return null;
    }

    /**
     * Bitstruct field declaration assigned to ({@code s.a = ...}), or
     * {@code null} for anything else. Used for truncation checking.
     */
    private static @Nullable C3PsiElement bitstructFieldTarget(@NotNull C3Expr lhs)
    {
        if (!(lhs instanceof C3CallExpr call)
            || call.getCallExprTail() == null
            || call.getCallExprTail().getCallInvocation() != null
            || call.getCallExprTail().getAccessIdent() == null) return null;
        PsiElement resolved;
        try
        {
            resolved = call.getCallExprTail().getAccessIdent().getReference().resolve();
        }
        catch (Exception e)
        {
            return null;
        }
        if (resolved instanceof C3BitstructDef || resolved instanceof C3BitstructSimpleDef)
        {
            return (C3PsiElement) resolved;
        }
        return null;
    }

    private void annotateReturnStmt(@NotNull C3ReturnStmt ret, @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(ret.getProject())) return;
        C3FuncDef funcDef = TypeChecker.enclosingFunction(ret);
        if (funcDef == null) return;
        ShortType returnType = funcDef.getReturnType();
        String returnText = returnType != null && returnType.getValue() != null ? returnType.getValue() : "void";
        C3Expr expr = ret.getExpr();
        if (TypeChecker.isVoidOptionalType(returnText))
        {
            annotateVoidOptionalReturn(ret, expr, holder);
            return;
        }
        if (expr == null)
        {
            if (!TypeChecker.isVoidType(returnText))
            {
                holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Expected to return a value of type '" + TypeChecker.shortName(returnText) + "'.")
                    .range(returnKeywordOrSelf(ret))
                    .create();
            }
            return;
        }
        if (TypeChecker.isVoidType(returnText))
        {
            // `return voidExpr;` just forwards control (the callee returns
            // nothing either); only a real value is an error here. Unknown
            // expressions stay silent.
            InferredType source = TypeChecker.infer(expr);
            if (source != null && source.getKind() != InferredType.Kind.VOID)
            {
                holder.newAnnotation(HighlightSeverity.ERROR, "Cannot return a value from a void function.")
                    .range(expr)
                    .create();
            }
            return;
        }
        String error = TypeChecker.returnError(ret.getProject(), ModuleName.from(ret), returnText, TypeChecker.infer(expr), expr);
        if (error != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(expr).create();
    }

    /**
     * A {@code void?} function returns either nothing or an excuse: bare
     * {@code return}, {@code fault~} and other Optionals are fine, any plain
     * value is an error.
     */
    private void annotateVoidOptionalReturn(
            @NotNull C3ReturnStmt ret, @Nullable C3Expr expr, @NotNull AnnotationHolder holder)
    {
        if (expr == null) return;
        InferredType source = TypeChecker.infer(expr);
        if (source == null) return;
        String sourceName = TypeChecker.normalize(source.getName());
        if (TypeChecker.isOptionalName(sourceName) || sourceName.equals("fault")) return;
        holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Cannot return '" + source.getName() + "' from function returning 'void?'.")
            .range(expr)
            .create();
    }

    private static @NotNull PsiElement returnKeywordOrSelf(@NotNull C3ReturnStmt ret)
    {
        ASTNode keyword = ret.getNode().findChildByType(C3Types.KW_RETURN);
        return keyword != null ? keyword.getPsi() : ret;
    }

    private void annotateReturnCoverage(@NotNull C3FuncDef funcDef, @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(funcDef.getProject())) return;
        ShortType returnType = funcDef.getReturnType();
        if (returnType == null || returnType.getValue() == null
            || TypeChecker.isVoidType(returnType.getValue())
            || TypeChecker.isVoidOptionalType(returnType.getValue())) return;
        // A @noreturn function never falls through: no return needed.
        if (AttributeSpecs.hasAttribute(funcDef.getAttributes(), "noreturn")) return;
        PsiElement parent = funcDef.getParent();
        if (!(parent instanceof C3FuncDefinition definition)
            || definition.getMacroFuncBody() == null
            || definition.getMacroFuncBody().getCompoundStatement() == null) return;
        C3CompoundStatement body = definition.getMacroFuncBody().getCompoundStatement();
        // Loops may never fall off the end: skip instead of false-positive.
        if (!PsiTreeUtil.collectElementsOfType(body, C3ForStmt.class).isEmpty()
            || !PsiTreeUtil.collectElementsOfType(body, C3WhileStmt.class).isEmpty()
            || !PsiTreeUtil.collectElementsOfType(body, C3ForeachStmt.class).isEmpty()
            || !PsiTreeUtil.collectElementsOfType(body, C3DoStmt.class).isEmpty()) return;
        for (C3ReturnStmt ret : PsiTreeUtil.collectElementsOfType(body, C3ReturnStmt.class))
        {
            if (funcDef.equals(TypeChecker.enclosingFunction(ret))) return;
        }
        // The end of the body is unreachable (e.g. it ends with a call to a
        // @noreturn function like unreachable()): no return needed either.
        if (bodyNeverFallsThrough(body)) return;
        PsiElement anchor = funcDef.getNameIdentifier() != null ? funcDef.getNameIdentifier() : funcDef;
        holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Missing return of type '" + TypeChecker.shortName(returnType.getValue())
                    + "' in function '" + funcDef.getFqName().getName() + "'.")
            .range(anchor)
            .create();
    }

    /**
     * Whether control can never reach the end of a block: its last statement
     * returns or diverges (calls a {@code @noreturn} function), including
     * {@code if}/{@code else} chains where every branch diverges.
     */
    private static boolean bodyNeverFallsThrough(@NotNull C3CompoundStatement body)
    {
        List<C3Statement> statements = new ArrayList<>();
        for (C3StatementList statementList : body.getStatementListList())
        {
            statements.addAll(statementList.getStatementList());
        }
        if (statements.isEmpty()) return false;
        return statementDiverges(statements.get(statements.size() - 1));
    }

    private static boolean statementDiverges(@NotNull C3Statement statement)
    {
        if (statement.getReturnStmt() != null) return true;
        if (statement.getExprStmt() != null) return exprDiverges(statement.getExprStmt().getExpr());
        C3IfStmt ifStmt = statement.getIfStmt();
        if (ifStmt != null) return ifDiverges(ifStmt);
        return false;
    }

    private static boolean ifDiverges(@NotNull C3IfStmt ifStmt)
    {
        if (!branchDiverges(ifStmt.getCompoundStatement(), ifStmt.getStatement())) return false;
        C3ElsePart elsePart = ifStmt.getElsePart();
        if (elsePart == null) return false;
        if (elsePart.getIfStmt() != null) return ifDiverges(elsePart.getIfStmt());
        return branchDiverges(elsePart.getCompoundStatement(), null);
    }

    private static boolean branchDiverges(@Nullable C3CompoundStatement compound, @Nullable C3Statement single)
    {
        if (compound != null) return bodyNeverFallsThrough(compound);
        if (single != null) return statementDiverges(single);
        return false;
    }

    private static boolean exprDiverges(@NotNull C3Expr expr)
    {
        if (expr instanceof C3GroupedExpr grouped)
        {
            return grouped.getExpr() != null && exprDiverges(grouped.getExpr());
        }
        if (expr instanceof C3BinaryExpr binary && TypeChecker.assignmentOperator(binary) != null)
        {
            return binary.getRight() != null && exprDiverges(binary.getRight());
        }
        if (expr instanceof C3CallExpr call)
        {
            if (isNoreturnCall(call)) return true;
            return call.getExpr() instanceof C3CallExpr inner && exprDiverges(inner);
        }
        return false;
    }

    private static boolean isNoreturnCall(@NotNull C3CallExpr call)
    {
        C3CallablePsiElement target;
        try
        {
            target = CallChecker.resolveTarget(call);
        }
        catch (Exception e)
        {
            return false;
        }
        if (target == null) return false;
        C3Attributes attributes = null;
        if (target instanceof C3FuncDef funcDef) attributes = funcDef.getAttributes();
        else if (target instanceof C3MacroDefinition macro) attributes = macro.getAttributes();
        return AttributeSpecs.hasAttribute(attributes, "noreturn");
    }

    private void annotateModulePath(@NotNull PsiElement element, @NotNull AnnotationHolder annotationHolder)
    {
        for (ASTNode node : element.getNode().getChildren(null))
        {
            if (node.getElementType() == C3Types.IDENT)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.MODULE_KEY).range(node.getTextRange()).create();
            }
        }
    }

    private void annotatePath(@NotNull C3Path path, @NotNull AnnotationHolder annotationHolder)
    {
        for (ASTNode node : path.getNode().getChildren(null))
        {
            if (node.getElementType() == C3Types.IDENT)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.MODULE_KEY).range(node.getTextRange()).create();
            }
        }
    }

    private void annotateParameter(@NotNull C3Parameter param, @NotNull AnnotationHolder annotationHolder)
    {
        for (ASTNode node : param.getNode().getChildren(null))
        {
            if (node.getElementType() == C3Types.IDENT)
            {
                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                    .textAttributes(C3SyntaxHighlighter.PARAMETER_KEY).range(node.getTextRange()).create();
            }
        }
    }

    private void annotateAccessIdent(@NotNull C3AccessIdent accessIdent, @NotNull AnnotationHolder annotationHolder)
    {
        PsiElement nameElement = accessIdent.getNameIdentElement();
        if (nameElement == null) nameElement = accessIdent;

        boolean isCall = false;
        PsiElement parent = accessIdent.getParent();
        if (parent != null && parent.getParent() instanceof C3CallExpr accessCall)
        {
            PsiElement grandParent = accessCall.getParent();
            if (grandParent instanceof C3CallExpr callExpr
                && callExpr.getExpr() == accessCall
                && callExpr.getCallExprTail() != null
                && callExpr.getCallExprTail().getCallInvocation() != null)
            {
                isCall = true;
            }
        }

        TextAttributesKey key = isCall ? C3SyntaxHighlighter.METHOD_CALL_KEY : C3SyntaxHighlighter.FIELD_KEY;
        annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
            .textAttributes(key).range(nameElement).create();
        if (!isCall) annotateMissingBitstructField(accessIdent, nameElement, annotationHolder);
    }

    /**
     * {@code s.zzz} where {@code s} is a bitstruct with no such field. Only
     * fires for a resolved bitstruct receiver, so unknown types stay silent.
     */
    private void annotateMissingBitstructField(
            @NotNull C3AccessIdent accessIdent,
            @NotNull PsiElement nameElement,
            @NotNull AnnotationHolder holder)
    {
        if (DumbService.isDumb(accessIdent.getProject())) return;
        String field = accessIdent.getNameIdent();
        if (field == null || field.isEmpty()) return;
        PsiElement resolved;
        try
        {
            resolved = accessIdent.getReference().resolve();
        }
        catch (Exception e)
        {
            return;
        }
        if (resolved != null) return;
        FullyQualifiedName receiverType = bitstructReceiverType(accessIdent);
        if (receiverType == null) return;
        if (BitstructSupport.bitstructBacking(receiverType.getFullName(), accessIdent.getProject(), ModuleName.from(accessIdent)) == null)
        {
            return;
        }
        holder.newAnnotation(HighlightSeverity.ERROR,
                "There is no field or method '" + receiverType.getName() + "." + field + "'.")
            .range(nameElement)
            .create();
    }

    /**
     * Receiver type of a member access, walking down nested call tails to
     * the root path identifier ({@code LOOKUP[i].field} resolves through
     * the lookup's element type).
     */
    private static @Nullable FullyQualifiedName bitstructReceiverType(@NotNull C3AccessIdent accessIdent)
    {
        try
        {
            PsiElement parent = accessIdent.getParent();
            if (!(parent instanceof C3CallExprTail tail)) return null;
            if (!(parent.getParent() instanceof C3CallExpr access)) return null;
            if (access.getCallExprTail() != tail || tail.getCallInvocation() != null) return null;
            C3Expr receiver = access.getExpr();
            while (receiver instanceof C3CallExpr inner)
            {
                if (inner.getExpr() == null) return null;
                receiver = inner.getExpr();
            }
            if (receiver instanceof C3PathIdentExpr pathExpr) return pathExpr.getPathIdent().findTypeName();
            if (receiver instanceof C3PathConstExpr constExpr) return TypeCanonicalizer.constRootType(constExpr);
            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private void annotatePathIdent(@NotNull C3PathIdent pathIdent, @NotNull AnnotationHolder annotationHolder)
    {
        PsiElement nameElement = pathIdent.getNameIdentElement();
        if (nameElement == null) return;
        String name = nameElement.getText();
        if (name == null || name.isEmpty()) return;

        boolean isCall = false;
        PsiElement parent = pathIdent.getParent();
        if (parent != null && parent.getParent() instanceof C3CallExpr callExpr)
        {
            if (callExpr.getExpr() == parent
                && callExpr.getCallExprTail() != null
                && callExpr.getCallExprTail().getCallInvocation() != null)
            {
                isCall = true;
            }
        }

        if (isCall)
        {
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                .textAttributes(C3SyntaxHighlighter.FUNCTION_CALL_KEY).range(nameElement).create();
            return;
        }

        if (pathIdent.getPath() != null)
        {
            return;
        }

        if ("this".equals(name) || "self".equals(name))
        {
            if (enclosingLambda(pathIdent) != null)
            {
                annotateCapture(pathIdent, nameElement, name, annotationHolder);
                return;
            }
            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                .textAttributes(C3SyntaxHighlighter.PARAMETER_KEY).range(nameElement).create();
            return;
        }

        PsiElement lambda = enclosingLambda(pathIdent);
        if (lambda != null)
        {
            PsiElement reference = referenceInsideLambda(pathIdent, name, lambda);
            if (reference != null)
            {
                // Resolves within the lambda (own local, own parameter): a
                // normal highlight below, never a capture.
            }
            else if (matchesOuterParamOrBinding(pathIdent, name, lambda))
            {
                // A `#`-macro-parameter in a lambda signature type (e.g.
                // `$typeof(#array[0])` in a parameter type) is folded in the
                // macro's context before lambda analysis: c3c accepts it,
                // so it is not a capture. Elsewhere it still is.
                if (!isComptimeSignatureUse(pathIdent, lambda))
                {
                    annotateCapture(pathIdent, nameElement, name, annotationHolder);
                    return;
                }
            }
        }

        PsiElement current = pathIdent;
        while (current != null && !(current instanceof C3File))
        {
            if (current instanceof C3ForeachStmt foreachStmt)
            {
                C3ForeachVars vars = foreachStmt.getForeachVars();
                if (vars != null)
                {
                    for (C3ForeachVar v : vars.getForeachVarList())
                    {
                        ASTNode idNode = v.getNode().findChildByType(C3Types.IDENT);
                        if (idNode != null && name.equals(idNode.getText()))
                        {
                            if (lambda != null && !PsiTreeUtil.isAncestor(lambda, v, false))
                            {
                                annotateCapture(pathIdent, nameElement, name, annotationHolder);
                                return;
                            }
                            annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(nameElement).create();
                            return;
                        }
                    }
                }
            }
            else if (current instanceof C3IfStmt ifStmt)
            {
                if (isInThenBranch(ifStmt, pathIdent) && bindsCatchOrTryName(ifStmt, name))
                {
                    annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                        .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(nameElement).create();
                    return;
                }
            }
            else if (current instanceof C3CompoundStatement compoundStatement)
            {
                for (C3StatementList statementList : compoundStatement.getStatementListList())
                {
                    for (C3Statement stmt : statementList.getStatementList())
                    {
                        if (stmt.getTextOffset() >= pathIdent.getTextOffset()) break;
                        if (stmt.getLocalDeclarationStmt() != null)
                        {
                            C3DeclStmtAfterType afterType = stmt.getLocalDeclarationStmt().getDeclStmtAfterType();
                            if (afterType != null)
                            {
                                for (C3LocalDeclAfterType decl : afterType.getLocalDeclAfterTypeList())
                                {
                                    if (name.equals(decl.getNameIdent()))
                                    {
                                        if (lambda != null && !PsiTreeUtil.isAncestor(lambda, decl, false))
                                        {
                                            annotateCapture(pathIdent, nameElement, name, annotationHolder);
                                            return;
                                        }
                                        annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                            .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(nameElement).create();
                                        return;
                                    }
                                }
                            }
                        }
                        else if (stmt.getVarStmt() != null && stmt.getVarStmt().getVarDecl() != null)
                        {
                        ASTNode idNode = stmt.getVarStmt().getVarDecl().getNode().findChildByType(C3Types.IDENT);
                        if (idNode != null && name.equals(idNode.getText()))
                        {
                            if (lambda != null
                                && !PsiTreeUtil.isAncestor(lambda, stmt.getVarStmt().getVarDecl(), false))
                            {
                                annotateCapture(pathIdent, nameElement, name, annotationHolder);
                                return;
                            }
                                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                    .textAttributes(C3SyntaxHighlighter.LOCAL_VARIABLE_KEY).range(nameElement).create();
                                return;
                            }
                        }
                    }
                }
            }
            else if (current instanceof C3FuncDef funcDef)
            {
                C3ParameterList paramList = funcDef.getFnParameterList().getParameterList();
                if (paramList != null)
                {
                    for (C3ParamDecl paramDecl : paramList.getParamDeclList())
                    {
                        C3Parameter p = paramDecl.getParameter();
                        for (ASTNode node : p.getNode().getChildren(null))
                        {
                            if (node.getElementType() == C3Types.IDENT && name.equals(node.getText()))
                            {
                                if (lambda != null && !PsiTreeUtil.isAncestor(lambda, p, false))
                                {
                                    annotateCapture(pathIdent, nameElement, name, annotationHolder);
                                    return;
                                }
                                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                    .textAttributes(C3SyntaxHighlighter.PARAMETER_KEY).range(nameElement).create();
                                return;
                            }
                        }
                    }
                }
            }
            else if (current instanceof C3MacroDefinition macroDef)
            {
                C3MacroParams params = macroDef.getMacroParams();
                C3ParameterList paramList = params != null ? params.getParameterList() : null;
                if (paramList != null)
                {
                    for (C3ParamDecl paramDecl : paramList.getParamDeclList())
                    {
                        C3Parameter p = paramDecl.getParameter();
                        for (ASTNode node : p.getNode().getChildren(null))
                        {
                            if (node.getElementType() == C3Types.IDENT && name.equals(node.getText()))
                            {
                                if (lambda != null && !PsiTreeUtil.isAncestor(lambda, p, false))
                                {
                                    annotateCapture(pathIdent, nameElement, name, annotationHolder);
                                    return;
                                }
                                annotationHolder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                                    .textAttributes(C3SyntaxHighlighter.PARAMETER_KEY).range(nameElement).create();
                                return;
                            }
                        }
                    }
                }
            }
            current = current.getParent();
        }
    }

    private static @Nullable PsiElement enclosingLambda(@NotNull PsiElement element)
    {
        return PsiTreeUtil.getParentOfType(element, C3LambdaDeclExpr.class, C3LambdaDeclShortExpr.class);
    }

    /**
     * What the name resolves to without leaving the lambda: its own
     * parameter, a lambda-local declaration, or a lambda-local foreach /
     * catch / try binding. Anything else means the outer-name check decides
     * (capture error or a global).
     */
    private static @Nullable PsiElement referenceInsideLambda(
            @NotNull C3PathIdent pathIdent, @NotNull String name, @NotNull PsiElement lambda)
    {
        try
        {
            PsiReference reference = pathIdent.getReference();
            if (reference != null)
            {
                for (ResolveResult result : ((PsiPolyVariantReference) reference).multiResolve(false))
                {
                    PsiElement element = result.getElement();
                    if (element != null && PsiTreeUtil.isAncestor(lambda, element, false)) return element;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        int useOffset = pathIdent.getTextOffset();
        for (C3LocalDeclAfterType decl : PsiTreeUtil.findChildrenOfType(lambda, C3LocalDeclAfterType.class))
        {
            if (name.equals(decl.getNameIdent()) && decl.getTextOffset() < useOffset) return decl;
        }
        for (C3VarDecl varDecl : PsiTreeUtil.findChildrenOfType(lambda, C3VarDecl.class))
        {
            ASTNode idNode = varDecl.getNode().findChildByType(C3Types.IDENT);
            if (idNode != null && name.equals(idNode.getText()) && varDecl.getTextOffset() < useOffset) return varDecl;
        }
        for (C3ForeachVar var : PsiTreeUtil.findChildrenOfType(lambda, C3ForeachVar.class))
        {
            ASTNode idNode = var.getNode().findChildByType(C3Types.IDENT);
            if (idNode != null && name.equals(idNode.getText()) && var.getTextOffset() < useOffset) return var;
        }
        return null;
    }

    /**
     * Whether the name matches an outer function/macro parameter, an outer
     * lambda's parameter, or an outer catch/try binding: all invisible
     * inside the lambda (checked separately from the highlight walk, whose
     * function-parameter branches only see default-value positions).
     */
    private static boolean matchesOuterParamOrBinding(
            @NotNull C3PathIdent pathIdent, @NotNull String name, @NotNull PsiElement lambda)
    {
        C3FuncDefinition funcDef = PsiTreeUtil.getParentOfType(pathIdent, C3FuncDefinition.class);
        if (funcDef != null && funcDef.getFuncDef() != null
            && funcDef.getFuncDef().getFnParameterList() != null
            && funcDef.getFuncDef().getFnParameterList().getParameterList() != null)
        {
            for (C3ParamDecl paramDecl : funcDef.getFuncDef().getFnParameterList().getParameterList().getParamDeclList())
            {
                C3Parameter parameter = paramDecl.getParameter();
                if (parameter != null && name.equals(parameter.getNameIdent())) return true;
            }
        }
        C3MacroDefinition macroDef = PsiTreeUtil.getParentOfType(pathIdent, C3MacroDefinition.class);
        if (macroDef != null && macroDef.getMacroParams() != null
            && macroDef.getMacroParams().getParameterList() != null)
        {
            for (C3ParamDecl paramDecl : macroDef.getMacroParams().getParameterList().getParamDeclList())
            {
                C3Parameter parameter = paramDecl.getParameter();
                if (parameter != null && name.equals(parameter.getNameIdent())) return true;
            }
        }
        // Walk enclosing lambdas beyond the innermost one, plus any `if`
        // whose then-branch holds the lambda: bindings there are equally
        // invisible inside.
        PsiElement current = lambda.getParent();
        while (current != null)
        {
            if (current instanceof C3LambdaDeclExpr || current instanceof C3LambdaDeclShortExpr)
            {
                if (current != lambda && lambdaParamMatches(current, name)) return true;
            }
            if (current instanceof C3IfStmt ifStmt && isElementInThenBranch(ifStmt, lambda))
            {
                if (bindsCatchOrTryName(ifStmt, name)) return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static boolean isElementInThenBranch(@NotNull C3IfStmt ifStmt, @NotNull PsiElement element)
    {
        if (ifStmt.getCompoundStatement() != null)
        {
            return PsiTreeUtil.isAncestor(ifStmt.getCompoundStatement(), element, false);
        }
        return ifStmt.getStatement() != null && PsiTreeUtil.isAncestor(ifStmt.getStatement(), element, false);
    }

    /**
     * Whether the use sits in a lambda signature type (parameter or return
     * type) and names a `#`-macro-parameter: folded in the macro's context
     * before lambda analysis (verified against {@code c3c}, which accepts
     * `$typeof(#array[0])` in a parameter type but rejects any body use).
     * Default values are runtime expressions, so only declared types count.
     */
    private static boolean isComptimeSignatureUse(@NotNull C3PathIdent pathIdent, @NotNull PsiElement lambda)
    {
        String name = pathIdent.getNameIdent();
        if (name == null || !name.startsWith("#")) return false;
        C3LambdaDecl decl = null;
        if (lambda instanceof C3LambdaDeclExpr full) decl = full.getLambdaDecl();
        else if (lambda instanceof C3LambdaDeclShortExpr shortExpr) decl = shortExpr.getLambdaDecl();
        if (decl == null) return false;
        C3OptionalType returnType = decl.getOptionalType();
        if (returnType != null && PsiTreeUtil.isAncestor(returnType, pathIdent, false)) return true;
        C3FnParameterList paramList = decl.getFnParameterList();
        if (paramList == null || !PsiTreeUtil.isAncestor(paramList, pathIdent, false)) return false;
        C3ParamDecl paramDecl = PsiTreeUtil.getParentOfType(pathIdent, C3ParamDecl.class);
        if (paramDecl == null || !PsiTreeUtil.isAncestor(paramList, paramDecl, false)) return false;
        C3Parameter parameter = paramDecl.getParameter();
        return parameter != null && parameter.getType() != null
            && PsiTreeUtil.isAncestor(parameter.getType(), pathIdent, false);
    }

    private static boolean lambdaParamMatches(@NotNull PsiElement lambdaExpr, @NotNull String name)
    {
        C3LambdaDecl decl = null;
        if (lambdaExpr instanceof C3LambdaDeclExpr full) decl = full.getLambdaDecl();
        else if (lambdaExpr instanceof C3LambdaDeclShortExpr shortExpr) decl = shortExpr.getLambdaDecl();
        if (decl == null || decl.getFnParameterList() == null
            || decl.getFnParameterList().getParameterList() == null) return false;
        for (C3ParamDecl paramDecl : decl.getFnParameterList().getParameterList().getParamDeclList())
        {
            C3Parameter parameter = paramDecl.getParameter();
            if (parameter != null && name.equals(parameter.getNameIdent())) return true;
        }
        return false;
    }

    /**
     * A use inside a lambda that would resolve outside it: lambdas do not
     * capture enclosing locals, parameters or `self` (verified against
     * {@code c3c}, which reports the name as not found).
     */
    private void annotateCapture(
            @NotNull C3PathIdent pathIdent,
            @NotNull PsiElement nameElement,
            @NotNull String name,
            @NotNull AnnotationHolder holder)
    {
        holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Cannot capture '" + name + "' from the enclosing function: lambdas do not close over outer variables.")
            .range(nameElement != null ? nameElement : (PsiElement) pathIdent)
            .create();
    }
}
