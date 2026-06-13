package org.c3lang.intellij.completion;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3FuncName;
import org.c3lang.intellij.psi.C3ImportDecl;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3Statement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PsiElementUtils
{
    public static final PsiElementUtils INSTANCE = new PsiElementUtils();

    private PsiElementUtils()
    {
    }

    public static @NotNull C3ImportDecl createImport(@NotNull Project project, @NotNull String importPath)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "import " + importPath + ";");
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(file, C3ImportDecl.class));
    }

    public static @NotNull PsiElement createNewLine(@NotNull Project project)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "\n");
        return Objects.requireNonNull(file.getFirstChild());
    }

    public static @NotNull C3FuncName createFunctionName(@NotNull Project project, @NotNull String functionName)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "fn void " + functionName + "() {}");
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(file, C3FuncName.class));
    }

    public static @NotNull C3PathIdentExpr createPathIdentExpr(@NotNull Project project, @NotNull String expression)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "fn void dummy() { " + expression + "(); }");
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(file, C3PathIdentExpr.class));
    }

    public static @NotNull C3Arg createArg(@NotNull Project project, @NotNull String name)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "int i = dummy(" + name + ");");
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(file, C3Arg.class));
    }

    public static @NotNull C3Statement createStatement(@NotNull Project project, @NotNull String name)
    {
        PsiFile file = PsiFileFactory.getInstance(project)
            .createFileFromText(C3Language.INSTANCE, "fn void dummy() { int " + name + " = 0; }");
        return Objects.requireNonNull(PsiTreeUtil.findChildOfType(file, C3Statement.class));
    }
}
