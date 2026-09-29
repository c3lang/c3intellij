package org.c3lang.intellij.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3GenericDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds a missing {@code @dynamic} attribute to an interface method implementation,
 * e.g. {@code fn String Baz.myname(Baz* self) @dynamic}.
 */
public class AddDynamicAttributeFix extends LocalQuickFixAndIntentionActionOnPsiElement implements IntentionAction
{
    public AddDynamicAttributeFix(@NotNull C3FuncDef funcDef)
    {
        super(funcDef);
    }

    @Override
    public @NotNull String getText()
    {
        return "Add '@dynamic'";
    }

    @Override
    public @NotNull String getFamilyName()
    {
        return "Add '@dynamic'";
    }

    @Override
    public @NotNull IntentionPreviewInfo generatePreview(
            @NotNull Project project,
            @NotNull Editor editor,
            @NotNull PsiFile file)
    {
        // Previews run inside a read action on a background thread where write
        // actions are forbidden, so the diff is precomputed as plain text.
        C3FuncDef funcDef = IntentionPreviews.findFuncDefIn(file, getStartElement());
        if (funcDef == null) return IntentionPreviewInfo.EMPTY;
        int insertOffset = insertOffset(funcDef);
        if (insertOffset < 0) return IntentionPreviewInfo.EMPTY;
        return IntentionPreviews.insertionPreview(file, insertOffset, " @dynamic");
    }

    @Override
    public void invoke(
            @NotNull Project project,
            @NotNull PsiFile file,
            @Nullable Editor editor,
            @NotNull PsiElement startElement,
            @NotNull PsiElement endElement)
    {
        C3FuncDef funcDef = startElement instanceof C3FuncDef def
            ? def
            : PsiTreeUtil.getParentOfType(startElement, C3FuncDef.class);
        if (funcDef == null || !funcDef.isValid()) return;

        Document document = editor != null
            ? editor.getDocument()
            : PsiDocumentManager.getInstance(project).getDocument(file);
        if (document == null) return;

        int insertOffset = insertOffset(funcDef);
        if (insertOffset < 0) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (!funcDef.isValid()) return;
            document.insertString(insertOffset, " @dynamic");
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }

    private static int insertOffset(@NotNull C3FuncDef funcDef)
    {
        // func_def ::= KW_FN func_header fn_parameter_list generic_decl? attributes?
        // @dynamic goes after the parameter list / generic params / existing attributes.
        C3Attributes attributes = funcDef.getAttributes();
        if (attributes != null) return attributes.getTextRange().getEndOffset();
        C3GenericDecl genericDecl = funcDef.getGenericDecl();
        if (genericDecl != null) return genericDecl.getTextRange().getEndOffset();
        C3FnParameterList parameterList = funcDef.getFnParameterList();
        if (parameterList != null) return parameterList.getTextRange().getEndOffset();
        return -1;
    }
}
