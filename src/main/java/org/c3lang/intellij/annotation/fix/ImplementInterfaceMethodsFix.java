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
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Generates missing interface method implementations for a struct, e.g. for
 * {@code struct Baz (MyName)} inserts
 * {@code fn String Baz.myname(Baz* self) @dynamic { ... }} after the struct.
 */
public class ImplementInterfaceMethodsFix extends LocalQuickFixAndIntentionActionOnPsiElement implements IntentionAction
{
    private final FullyQualifiedName iface;

    public ImplementInterfaceMethodsFix(@NotNull C3StructDeclaration structDecl, @NotNull FullyQualifiedName iface)
    {
        super(structDecl);
        this.iface = iface;
    }

    @Override
    public @NotNull String getText()
    {
        return "Implement methods of '" + iface.getName() + "'";
    }

    @Override
    public @NotNull String getFamilyName()
    {
        return "Implement interface methods";
    }

    @Override
    public @NotNull IntentionPreviewInfo generatePreview(
            @NotNull Project project,
            @NotNull Editor editor,
            @NotNull PsiFile file)
    {
        // Previews run inside a read action on a background thread where write
        // actions are forbidden, so the diff is precomputed as plain text.
        C3StructDeclaration structDecl = findStructIn(file, getStartElement());
        if (structDecl == null) return IntentionPreviewInfo.EMPTY;
        Plan plan = plan(structDecl, project);
        if (plan == null) return IntentionPreviewInfo.EMPTY;
        return IntentionPreviews.insertionPreview(file, plan.insertOffset, plan.text);
    }

    @Override
    public void invoke(
            @NotNull Project project,
            @NotNull PsiFile file,
            @Nullable Editor editor,
            @NotNull PsiElement startElement,
            @NotNull PsiElement endElement)
    {
        C3StructDeclaration structDecl = startElement instanceof C3StructDeclaration declaration
            ? declaration
            : PsiTreeUtil.getParentOfType(startElement, C3StructDeclaration.class);
        if (structDecl == null || !structDecl.isValid()) return;

        Plan plan = plan(structDecl, project);
        if (plan == null) return;

        Document document = editor != null
            ? editor.getDocument()
            : PsiDocumentManager.getInstance(project).getDocument(file);
        if (document == null) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (!structDecl.isValid()) return;
            document.insertString(plan.insertOffset, plan.text);
            PsiDocumentManager.getInstance(project).commitDocument(document);
            if (editor != null && plan.caretOffset >= 0 && plan.caretOffset <= document.getTextLength())
            {
                editor.getCaretModel().moveToOffset(plan.caretOffset);
            }
        });
    }

    private @Nullable Plan plan(@NotNull C3StructDeclaration structDecl, @NotNull Project project)
    {
        C3TypeName typeName = structDecl.getTypeName();
        String structName = InterfaceService.shortName(typeName.getText());
        if (structName.isEmpty()) return null;
        FullyQualifiedName structFqn = new FullyQualifiedName(ModuleName.from(structDecl), structName);

        List<C3FuncDef> missing = InterfaceService.INSTANCE.findMissingInterfaceMethods(structFqn, iface, project);
        if (missing.isEmpty()) return null;

        int insertOffset = structDecl.getTextRange().getEndOffset();
        StringBuilder text = new StringBuilder("\n");
        int caretOffset = -1;
        boolean firstMethod = true;
        for (C3FuncDef method : missing)
        {
            String methodText = buildMethod(structName, method);
            if (methodText == null) continue;
            if (caretOffset < 0)
            {
                int bodyIndent = methodText.indexOf("{\n\t");
                if (bodyIndent >= 0) caretOffset = insertOffset + text.length() + bodyIndent + "{\n\t".length();
            }
            // Blank line after the struct and between methods; the file tail
            // (usually a single newline) is left untouched.
            text.append(firstMethod ? "\n" : "\n\n").append(methodText);
            firstMethod = false;
        }
        if (caretOffset < 0) return null;
        return new Plan(insertOffset, text.toString(), caretOffset);
    }

    private static @Nullable String buildMethod(@NotNull String structName, @NotNull C3FuncDef method)
    {
        String methodName = method.getNameIdent();
        if (methodName == null || methodName.isEmpty()) return null;
        return "fn " + returnTypeText(method) + " " + structName + "." + methodName
            + selfParameters(structName, method) + " @dynamic\n{\n\t\n}";
    }

    private static @NotNull String returnTypeText(@NotNull C3FuncDef method)
    {
        try
        {
            C3Type type = method.getFuncHeader().getOptionalType().getType();
            if (type != null && !type.getText().isBlank()) return type.getText().strip();
        }
        catch (Exception ignored)
        {
        }
        return "void";
    }

    private static @NotNull String selfParameters(@NotNull String structName, @NotNull C3FuncDef method)
    {
        String selfName = "self";
        List<String> taken = InterfaceService.collectParameterNames(method);
        if (taken.contains("self")) selfName = taken.contains("this") ? "self_" : "this";
        // Prefer the `&self` sugar; anything else uses the explicit form.
        String selfDecl = selfName.equals("self") ? "&self" : structName + "* " + selfName;

        String listText = method.getFnParameterList().getText();
        String inner = listText.length() >= 2 ? listText.substring(1, listText.length() - 1).strip() : "";
        return "(" + selfDecl + (inner.isEmpty() ? "" : ", " + inner) + ")";
    }

    private static @Nullable C3StructDeclaration findStructIn(@NotNull PsiFile file, @Nullable PsiElement originalAnchor)
    {
        if (originalAnchor == null || !originalAnchor.isValid()) return null;
        int offset = originalAnchor.getTextRange().getStartOffset();
        if (offset < 0 || offset > file.getTextLength()) return null;
        PsiElement at = file.findElementAt(offset);
        if (at instanceof C3StructDeclaration declaration) return declaration;
        return PsiTreeUtil.getParentOfType(at, C3StructDeclaration.class);
    }

    private record Plan(int insertOffset, @NotNull String text, int caretOffset)
    {
    }
}
