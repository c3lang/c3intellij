package org.c3lang.intellij.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds a missing owner-type first parameter to a method, e.g. turns
 * {@code fn void Test.test()} into {@code fn void Test.test(&self)}.
 */
public class AddSelfParameterFix extends LocalQuickFixAndIntentionActionOnPsiElement implements IntentionAction
{
    private final String ownerShortName;
    private final String paramName;

    public AddSelfParameterFix(
            @NotNull C3FuncDef funcDef,
            @NotNull String ownerShortName,
            @NotNull String paramName)
    {
        super(funcDef);
        this.ownerShortName = ownerShortName;
        this.paramName = paramName;
    }

    private @NotNull String selfDeclaration()
    {
        // Prefer the `&self` sugar; other names use the explicit form.
        if (paramName.equals("self")) return "&self";
        return ownerShortName + "* " + paramName;
    }

    @Override
    public @NotNull String getText()
    {
        return "Add '" + selfDeclaration() + "' parameter";
    }

    @Override
    public @NotNull String getFamilyName()
    {
        return "Add missing self parameter";
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
        C3FnParameterList parameterList = funcDef.getFnParameterList();
        if (parameterList == null) return IntentionPreviewInfo.EMPTY;
        ASTNode leftParen = parameterList.getNode().findChildByType(C3Types.LP);
        if (leftParen == null) return IntentionPreviewInfo.EMPTY;

        String listText = parameterList.getText();
        String inner = listText.length() >= 2 ? listText.substring(1, listText.length() - 1).strip() : "";
        String toInsert = selfDeclaration() + (inner.isEmpty() ? "" : ", ");
        return IntentionPreviews.insertionPreview(file, leftParen.getTextRange().getEndOffset(), toInsert);
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

        C3FnParameterList parameterList = funcDef.getFnParameterList();
        if (parameterList == null || !parameterList.isValid()) return;
        ASTNode leftParen = parameterList.getNode().findChildByType(C3Types.LP);
        if (leftParen == null) return;
        int insertOffset = leftParen.getTextRange().getEndOffset();

        String listText = parameterList.getText();
        String inner = listText.length() >= 2 ? listText.substring(1, listText.length() - 1).strip() : "";
        String toInsert = selfDeclaration() + (inner.isEmpty() ? "" : ", ");

        Document document = editor != null
            ? editor.getDocument()
            : PsiDocumentManager.getInstance(project).getDocument(file);
        if (document == null) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (!funcDef.isValid()) return;
            document.insertString(insertOffset, toInsert);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }
}
