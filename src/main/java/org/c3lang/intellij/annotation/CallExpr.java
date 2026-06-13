package org.c3lang.intellij.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.c3lang.intellij.psi.C3TopLevel;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class CallExpr
{
    private CallExpr()
    {
    }

    public static void annotateCallExpr(@NotNull C3CallExpr element, @NotNull AnnotationHolder holder)
    {
        if (!(element.getContainingFile() instanceof C3File file)) return;

        String text = element.getText();
        int callStart = text.indexOf('(');
        if (callStart < 0) return;

        String fullName = text.substring(0, callStart);
        String module = "";
        ModuleName moduleName = element.getModuleDefinition().getModuleName();
        if (moduleName != null)
        {
            module = moduleName.getValue();
        }
        int moduleSeparator = fullName.lastIndexOf("::");
        if (moduleSeparator >= 0)
        {
            module = fullName.substring(0, moduleSeparator);
        }
        String name = moduleSeparator >= 0 ? fullName.substring(moduleSeparator + 2) : fullName;

        PsiElement declaration = findDeclaration(element.getProject(), element, file, module, name, holder);
        if (declaration == null)
        {
            holder.newAnnotation(HighlightSeverity.WARNING, "Declaration not found").create();
        }
    }

    @Nullable
    private static PsiElement findDeclaration(
            @NotNull Project project,
            @NotNull C3CallExpr element,
            @NotNull C3File file,
            @NotNull String module,
            @NotNull String name,
            @NotNull AnnotationHolder holder)
    {
        if (module.isEmpty())
        {
            ArrayList<PsiElement> declarations = findDeclarationInFile(file, name);
            return declarations.isEmpty() ? null : declarations.get(0);
        }

        ArrayList<PsiElement> declarations = findDeclarationInModule(project, module, name);
        if (declarations.isEmpty()) return null;

        PsiElement firstMatch = declarations.get(0);
        C3ModuleDefinition moduleDefinition = element.getModuleDefinition();
        ModuleName matchModule = null;
        if (firstMatch instanceof C3FuncDefinition definition)
        {
            matchModule = definition.getModuleDefinition().getModuleName();
        }
        else if (firstMatch instanceof C3MacroDefinition definition)
        {
            matchModule = definition.getModuleDefinition().getModuleName();
        }

        boolean hasValidImport = false;
        if (matchModule != null)
        {
            for (ModuleName imported : moduleDefinition.getImports())
            {
                if (imported.getValue().equals(matchModule.getValue()))
                {
                    hasValidImport = true;
                    break;
                }
            }
        }

        if (!hasValidImport)
        {
//            holder.newAnnotation(HighlightSeverity.WARNING, "Missing import")
//                    .range(element)
//                    .newLocalQuickFix(new AddMissingImportFix(), InspectionManager.getInstance(project).createProblemDescriptor("", new AddMissingImportFix()))
//                    .registerFix();
        }

        return firstMatch;
    }

    @NotNull
    public static ArrayList<PsiElement> findDeclarationInModule(
            @NotNull Project project,
            @NotNull String module,
            @NotNull String name)
    {
        File stdLibPath = guessStdLibPath();
        ArrayList<PsiElement> matches = new ArrayList<>();
        if (stdLibPath != null)
        {
            for (C3File file : walkStdLib(project, stdLibPath))
            {
                addModuleMatches(file, module, name, matches);
            }
        }

        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        for (VirtualFile root : contentRoots)
        {
            VfsUtilCore.iterateChildrenRecursively(root, null, file -> {
                if (!file.isDirectory())
                {
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile instanceof C3File c3File && psiFile.getLanguage() == C3Language.INSTANCE)
                    {
                        addModuleMatches(c3File, module, name, matches);
                    }
                }
                return true;
            });
        }

        return matches;
    }

    @NotNull
    public static ArrayList<C3File> walkStdLib(@NotNull Project project, @NotNull File stdLibPath)
    {
        VirtualFile virtualRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(stdLibPath);
        ArrayList<C3File> files = new ArrayList<>();
        if (virtualRoot == null) return files;

        PsiManager psiManager = PsiManager.getInstance(project);
        walkStdLib(virtualRoot, psiManager, files);
        return files;
    }

    private static void walkStdLib(
            @NotNull VirtualFile virtualFile,
            @NotNull PsiManager psiManager,
            @NotNull List<C3File> files)
    {
        if (virtualFile.isDirectory())
        {
            for (VirtualFile child : virtualFile.getChildren())
            {
                walkStdLib(child, psiManager, files);
            }
            return;
        }

        PsiFile psi = psiManager.findFile(virtualFile);
        if (psi instanceof C3File c3File && psi.getLanguage() == C3Language.INSTANCE)
        {
            files.add(c3File);
        }
    }

    @Nullable
    public static File guessStdLibPath()
    {
        return guessStdLibPath(new File("/"));
    }

    @Nullable
    public static File guessStdLibPath(@NotNull File startDir)
    {
        ArrayDeque<File> queue = new ArrayDeque<>();
        queue.add(startDir);

        while (!queue.isEmpty())
        {
            File current = queue.removeFirst();
            if (!current.isDirectory()) continue;

            File lib = new File(current, "lib");
            File std = new File(lib, "std");
            if ("c3c".equals(current.getName()) && lib.exists() && std.exists() && std.isDirectory())
            {
                return std;
            }

            File[] children = current.listFiles(File::isDirectory);
            if (children != null)
            {
                for (File child : children)
                {
                    queue.add(child);
                }
            }
        }

        return null;
    }

    @NotNull
    public static ArrayList<PsiElement> findDeclarationInFile(@NotNull C3File file, @NotNull String name)
    {
        ArrayList<PsiElement> matches = new ArrayList<>();
        for (PsiElement child : file.getChildren())
        {
            if (child instanceof C3ModuleSection moduleSection)
            {
                addTopLevelMatches(moduleSection.getTopLevelList(), name, matches);
            }
        }
        return matches;
    }

    private static void addModuleMatches(
            @NotNull C3File file,
            @NotNull String module,
            @NotNull String name,
            @NotNull List<PsiElement> matches)
    {
        for (PsiElement child : file.getChildren())
        {
            if (!(child instanceof C3ModuleSection moduleSection)) continue;

            ModuleName moduleName = moduleSection.getModuleName();
            if (moduleName != null && moduleName.getValue().endsWith(module))
            {
                addTopLevelMatches(moduleSection.getTopLevelList(), name, matches);
            }
        }
    }

    private static void addTopLevelMatches(
            @NotNull List<C3TopLevel> topLevels,
            @NotNull String name,
            @NotNull List<PsiElement> matches)
    {
        for (C3TopLevel topLevel : topLevels)
        {
            C3FuncDefinition function = topLevel.getFuncDefinition();
            if (function != null && function.getFuncDef().getFuncHeader().getFuncName().getText().equals(name))
            {
                matches.add(function);
            }

            C3MacroDefinition macro = topLevel.getMacroDefinition();
            if (macro != null && macro.getMacroHeader().getMacroName().getText().equals(name))
            {
                matches.add(macro);
            }
        }
    }
}
