package org.c3lang.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.c3lang.intellij.psi.C3TopLevel;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class C3Util
{
    public static final C3Util INSTANCE = new C3Util();

    private static final Log LOG = LogFactory.getLog(C3Util.class);

    private C3Util()
    {
    }

	public static @NotNull String projectNameToModuleName(@NotNull String projectName)
	{
		StringBuilder builder = new StringBuilder();
		for (char c : projectName.toCharArray())
		{
			c = Character.toLowerCase(c);
			if (Character.isLetter(c))
			{
				builder.append(c);
				continue;
			}
			if ((Character.isDigit(c) || c == '_'))
			{
				if (builder.isEmpty()) builder.append("project");
				builder.append(c);
				continue;
			}
			if (builder.isEmpty()) continue;
			if (builder.charAt(builder.length() - 1) == '_') continue;
			builder.append('_');
		}
		if (builder.isEmpty()) builder.append("project");
		if (builder.length() > 31) builder.setLength(31);
		return builder.toString();
	}

	public static @NotNull String projectNameToTargetName(@NotNull String projectName)
	{
		StringBuilder builder = new StringBuilder();
		for (char c : projectName.toCharArray())
		{
			if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.')
			{
				builder.append(c);
				continue;
			}
			if (!builder.isEmpty() && builder.charAt(builder.length() - 1) == '-') continue;
			builder.append('-');
		}
		if (builder.isEmpty()) builder.append("project");
		return builder.toString();
	}

    public void writeToFile(@Nullable String moduleName, @Nullable String name, @NotNull File path)
    {
        InputStream inputStream = C3Util.class.getClassLoader().getResourceAsStream(name);
        if (inputStream == null)
        {
            throw new RuntimeException("Unable to load file '" + name + "'");
        }

        try (inputStream; BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                content.append(line).append('\n');
            }
            String contentString = String.format(content.toString(), moduleName);
            FileUtil.writeToFile(path, contentString);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
        }
    }

    public @Nullable Either<C3MacroDefinition, C3FuncDefinition> findDeclarationInModule(
            @NotNull Project project,
            @NotNull String module,
            @NotNull String name)
    {
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile virtualFile : C3ProjectService.getInstance(project).getSourceFiles())
        {
            if (!virtualFile.isValid()) continue;

            PsiFile psiFile = psiManager.findFile(virtualFile);
            if (psiFile == null || psiFile.getLanguage() != C3Language.INSTANCE) continue;

            Either<C3MacroDefinition, C3FuncDefinition> match = findDeclarationInPsiFile(psiFile, module, name);
            if (match != null) return match;
        }

        for (String stdLibPath : C3ProjectService.getInstance(project).getStdlibPaths())
        {
            for (File file : walkFiles(new File(stdLibPath)))
            {
                if (!file.isFile() || !"c3".equals(extension(file))) continue;

                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                if (virtualFile == null) continue;

                PsiFile psiFile = psiManager.findFile(virtualFile);
                if (psiFile == null || psiFile.getLanguage() != C3Language.INSTANCE) continue;

                Either<C3MacroDefinition, C3FuncDefinition> match = findDeclarationInPsiFile(psiFile, module, name);
                if (match != null) return match;
            }
        }

        return null;
    }

    public @NotNull Set<String> findC3ModulesStartingWith(@NotNull Project project, @NotNull String prefix)
    {
        Set<String> modules = new HashSet<>();
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile virtualFile : C3ProjectService.getInstance(project).getSourceFiles())
        {
            if (!virtualFile.isValid()) continue;

            PsiFile psiFile = psiManager.findFile(virtualFile);
            if (psiFile == null || psiFile.getLanguage() != C3Language.INSTANCE) continue;
            addModulesStartingWith(psiFile, prefix, modules);
        }

        for (String stdLibPath : C3ProjectService.getInstance(project).getStdlibPaths())
        {
            for (File file : walkFiles(new File(stdLibPath)))
            {
                if (!file.isFile() || !"c3".equals(extension(file))) continue;

                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                if (virtualFile == null) continue;

                PsiFile psiFile = psiManager.findFile(virtualFile);
                if (psiFile == null || psiFile.getLanguage() != C3Language.INSTANCE) continue;
                addModulesStartingWith(psiFile, prefix, modules);
            }
        }

        return modules;
    }

    public @NotNull ArrayList<Either<C3FuncDefinition, C3MacroDefinition>> findDeclarationsInModule(
            @NotNull Project project,
            @NotNull String module)
    {
        ArrayList<Either<C3FuncDefinition, C3MacroDefinition>> matches = new ArrayList<>();

        for (String stdLibPath : C3ProjectService.getInstance(project).getStdlibPaths())
        {
            for (C3File file : walkStdLib(project, new File(stdLibPath)))
            {
                addDeclarationsFromModuleSections(file, module, matches);
            }
        }

        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile file : C3ProjectService.getInstance(project).getSourceFiles())
        {
            PsiFile psiFile = psiManager.findFile(file);
            if (psiFile instanceof C3File c3File && psiFile.getLanguage() == C3Language.INSTANCE)
            {
                addDeclarationsFromModuleSections(c3File, module, matches);
            }
        }

        return matches;
    }

    public @Nullable String findBestMatch(@NotNull String target, @NotNull List<String> candidates)
    {
        if (candidates.isEmpty()) return null;

        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String candidate : candidates)
        {
            int distance = calculateLevenshteinDistance(target, candidate);
            if (distance < bestDistance)
            {
                best = candidate;
                bestDistance = distance;
            }
        }
        return best;
    }

    private static @Nullable Either<C3MacroDefinition, C3FuncDefinition> findDeclarationInPsiFile(
            @NotNull PsiFile file,
            @NotNull String module,
            @NotNull String name)
    {
        for (PsiElement child : file.getChildren())
        {
            if (!(child instanceof C3ModuleDefinition moduleDefinition)) continue;
            if (moduleDefinition.getModuleName() == null || !moduleDefinition.getModuleName().getValue().equals(module)) continue;

            for (PsiElement moduleChild : moduleDefinition.getChildren())
            {
                if (!(moduleChild instanceof C3TopLevel topLevel)) continue;

                C3MacroDefinition macro = topLevel.getMacroDefinition();
                if (macro != null && macro.getMacroHeader().getMacroName().getText().equals(name))
                {
                    return Either.forLeft(macro);
                }

                C3FuncDefinition function = topLevel.getFuncDefinition();
                if (function != null && function.getFuncDef().getFuncHeader().getFuncName().getText().equals(name))
                {
                    return Either.forRight(function);
                }
            }
        }

        return null;
    }

    private static void addModulesStartingWith(
            @NotNull PsiFile file,
            @NotNull String prefix,
            @NotNull Set<String> modules)
    {
        for (PsiElement child : file.getChildren())
        {
            if (child instanceof C3ModuleDefinition moduleDefinition
                && moduleDefinition.getModuleName() != null
                && moduleDefinition.getModuleName().getValue().startsWith(prefix))
            {
                modules.add(moduleDefinition.getModuleName().getValue());
            }
        }
    }

    private static void addDeclarationsFromModuleSections(
            @NotNull C3File file,
            @NotNull String module,
            @NotNull ArrayList<Either<C3FuncDefinition, C3MacroDefinition>> matches)
    {
        for (PsiElement child : file.getChildren())
        {
            if (!(child instanceof C3ModuleSection moduleSection)) continue;
            if (moduleSection.getModuleName() == null || !moduleSection.getModuleName().getValue().endsWith(module)) continue;

            for (PsiElement moduleChild : moduleSection.getChildren())
            {
                if (!(moduleChild instanceof C3TopLevel topLevel)) continue;

                C3FuncDefinition function = topLevel.getFuncDefinition();
                if (function != null) matches.add(Either.forLeft(function));

                C3MacroDefinition macro = topLevel.getMacroDefinition();
                if (macro != null) matches.add(Either.forRight(macro));
            }
        }
    }

    private static @NotNull ArrayList<C3File> walkStdLib(@NotNull Project project, @NotNull File stdLibPath)
    {
        VirtualFile virtualRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(stdLibPath);
        ArrayList<C3File> files = new ArrayList<>();
        if (virtualRoot == null) return files;

        PsiManager psiManager = PsiManager.getInstance(project);
        collectStdLibFiles(virtualRoot, psiManager, files);
        return files;
    }

    private static void collectStdLibFiles(
            @NotNull VirtualFile virtualFile,
            @NotNull PsiManager psiManager,
            @NotNull ArrayList<C3File> files)
    {
		if (virtualFile.isDirectory())
        {
            for (VirtualFile child : virtualFile.getChildren())
            {
                collectStdLibFiles(child, psiManager, files);
            }
            return;
        }

        PsiFile psi = psiManager.findFile(virtualFile);
        if (psi instanceof C3File c3File && psi.getLanguage() == C3Language.INSTANCE)
        {
            files.add(c3File);
        }
    }

    private static @NotNull List<File> walkFiles(@NotNull File root)
    {
        List<File> files = new ArrayList<>();
        collectFiles(root, files);
        return files;
    }

    private static void collectFiles(@NotNull File file, @NotNull List<File> files)
    {
        files.add(file);
        File[] children = file.listFiles();
        if (children == null) return;

        for (File child : children)
        {
            collectFiles(child, files);
        }
    }

    private static @NotNull String extension(@NotNull File file)
    {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : "";
    }

    private static int calculateLevenshteinDistance(@NotNull String s1, @NotNull String s2)
    {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++)
        {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++)
        {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++)
        {
            for (int j = 1; j <= n; j++)
            {
                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                else
                {
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + 1
                    );
                }
            }
        }

        return dp[m][n];
    }
}
