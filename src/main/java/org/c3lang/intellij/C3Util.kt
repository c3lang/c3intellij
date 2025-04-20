package org.c3lang.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.c3lang.intellij.psi.C3File
import org.c3lang.intellij.psi.C3FuncDefinition
import org.c3lang.intellij.psi.C3MacroDefinition
import org.c3lang.intellij.psi.C3ModuleDefinition
import org.c3lang.intellij.psi.C3ModuleSection
import org.c3lang.intellij.psi.C3TopLevel
import org.eclipse.lsp4j.jsonrpc.messages.Either
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object C3Util
{
    private val log: Log = LogFactory.getLog(C3Util::class.java)

    private fun getAllFiles(project: Project): Collection<VirtualFile>
    {
        val virtualFilesInterface =
            FileTypeIndex.getFiles(C3InterfaceFileType.INSTANCE, GlobalSearchScope.allScope(project))
        val virtualFiles =
            FileTypeIndex.getFiles(C3SourceFileType.INSTANCE, GlobalSearchScope.allScope(project))
        if (virtualFiles.isEmpty()) return virtualFilesInterface
        if (virtualFilesInterface.isEmpty()) return virtualFiles
        val all = ArrayList<VirtualFile>(virtualFiles.size + virtualFilesInterface.size)
        all.addAll(virtualFiles)
        all.addAll(virtualFilesInterface)
        return all
    }

    fun writeToFile(moduleName: String?, name: String?, path: File)
    {
        val inputStream = C3Util::class.java.classLoader.getResourceAsStream(name)

        if (inputStream != null)
        {
            try
            {
                val content = StringBuilder()
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while ((reader.readLine().also { line = it }) != null)
                    {
                        content.append(line).append("\n")
                    }
                }
                val contentString = content.toString().format(moduleName)

                FileUtil.writeToFile(path, contentString)
            } catch (e: IOException)
            {
                log.error(e.message, e)
            } finally
            {
                try
                {
                    inputStream.close()
                } catch (e: IOException)
                {
                    log.error(e.message, e)
                }
            }
        } else
        {
            throw RuntimeException("Unable to load file '$name'")
        }
    }

    fun findDeclarationInModule(project: Project, module: String, name: String): Either<C3MacroDefinition, C3FuncDefinition>?
    {
        val psiManager = PsiManager.getInstance(project)
        val settings = C3SettingsState.getInstance()
        val stdLibPath = settings.stdlibPath

        // search project
        FilenameIndex.getAllFilesByExt(project, C3Language.INSTANCE.associatedFileType?.defaultExtension!!)
            .asSequence()
            .filter { it.isValid }
            .mapNotNull { PsiManager.getInstance(project).findFile(it) }
            .filter { it.language is C3Language }
            .forEach { file ->
                val modules = file.children.filterIsInstance<C3ModuleDefinition>()
                modules.filter { it.moduleName?.value == module }.forEach { module ->
                    module.children.filterIsInstance<C3TopLevel>().forEach { topLevel ->
                        val macros = topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }
                        if (macros.isNotEmpty()) return Either.forLeft(macros[0])

                        val functions = topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }
                        if (functions.isNotEmpty()) return Either.forRight(functions[0])
                    }
                }
            }

        // search stdlib after if not found
        File(stdLibPath).walk()
            .asSequence()
            .filter { file ->
                file.isFile && file.extension == "c3"
            }
            .forEach { file ->
                LocalFileSystem.getInstance().findFileByIoFile(file)?.let { virtualFile ->
                    psiManager.findFile(virtualFile)?.takeIf { psiFile ->
                        psiFile.language == C3Language.INSTANCE
                    }?.children?.filterIsInstance<C3ModuleDefinition>()?.filter { moduleDef ->
                        moduleDef.moduleName?.value == module
                    }?.forEach { moduleDef ->
                        val topLevels = moduleDef.children.filterIsInstance<C3TopLevel>()

                        topLevels.forEach { topLevel ->
                            val macros = topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }
                            if (macros.isNotEmpty()) return Either.forLeft(macros[0])

                            val functions = topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }
                            if (functions.isNotEmpty()) return Either.forRight(functions[0])
                        }
                    }
                }
            }

        return null
    }

    fun findC3ModulesStartingWith(project: Project, prefix: String): Set<String>
    {
        val modules = mutableSetOf<String>()
        val psiManager = PsiManager.getInstance(project)
        val settings = C3SettingsState.getInstance()
        val stdLibPath = settings.stdlibPath

        // search project
        FilenameIndex.getAllFilesByExt(project, C3Language.INSTANCE.associatedFileType?.defaultExtension!!)
            .asSequence()
            .filter { it.isValid }
            .mapNotNull { PsiManager.getInstance(project).findFile(it) }
            .filter { it.language is C3Language }
            .forEach { file ->
                file.children.filterIsInstance<C3ModuleDefinition>().forEach { module ->
                    module.moduleName?.value?.takeIf { it.startsWith(prefix) }?.let { moduleName ->
                        modules.add(moduleName)
                    }
                }
            }

        // search stdlib after
        File(stdLibPath).walk()
            .asSequence()
            .filter { file ->
                file.isFile && file.extension == "c3"
            }
            .forEach { file ->
                LocalFileSystem.getInstance().findFileByIoFile(file)?.let { virtualFile ->
                    psiManager.findFile(virtualFile)?.takeIf { psiFile ->
                        psiFile.language == C3Language.INSTANCE
                    }?.children?.filterIsInstance<C3ModuleDefinition>()?.forEach { moduleDef ->
                        moduleDef.moduleName?.value?.takeIf { it.startsWith(prefix) }?.let { moduleName ->
                            modules.add(moduleName)
                        }
                    }
                }
            }

        return modules
    }

    fun findDeclarationsInModule(project: Project, module: String): ArrayList<Either<C3FuncDefinition, C3MacroDefinition>>
    {
        val settings = C3SettingsState.getInstance()
        val stdLibPath = settings.stdlibPath ?: return arrayListOf()
        val stdLibFiles = walkStdLib(project, File(stdLibPath))
        val matches = arrayListOf<Either<C3FuncDefinition, C3MacroDefinition>>()

        stdLibFiles.forEach { file ->
            val modules = file.children.filterIsInstance<C3ModuleSection>().filter { it.moduleName?.value?.endsWith(module) ?: false }

            modules.forEach { module ->
                val topLevels = module.children.filterIsInstance<C3TopLevel>()

                topLevels.forEach { topLevel ->
                    topLevel.children.filterIsInstance<C3FuncDefinition>().forEach { matches.add(Either.forLeft(it)) }
                    topLevel.children.filterIsInstance<C3MacroDefinition>().forEach { matches.add(Either.forRight(it)) }
                }
            }
        }

        val projectFiles = mutableListOf<C3File>()
        val psiManager = PsiManager.getInstance(project)

        val contentRoots = ProjectRootManager.getInstance(project).contentRoots
        for (root in contentRoots)
        {
            VfsUtilCore.iterateChildrenRecursively(root, null) { file ->
                if (!file.isDirectory)
                {
                    psiManager.findFile(file)?.let { psiFile ->
                        if (psiFile.language == C3Language.INSTANCE)
                        {
                            projectFiles.add(psiFile as C3File)
                        }
                    }
                }
                true
            }
        }

        projectFiles.forEach { file ->
            val modules = file.children.filterIsInstance<C3ModuleSection>().filter { it.moduleName?.value?.endsWith(module) ?: false }

            modules.forEach { module ->
                val topLevels = module.children.filterIsInstance<C3TopLevel>()

                topLevels.forEach { topLevel ->
                    topLevel.children.filterIsInstance<C3FuncDefinition>().forEach { matches.add(Either.forLeft(it)) }
                    topLevel.children.filterIsInstance<C3MacroDefinition>().forEach { matches.add(Either.forRight(it)) }
                }
            }
        }

        return matches
    }

    private fun walkStdLib(project: Project, stdLibPath: File): ArrayList<C3File>
    {
        val virtualRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(stdLibPath) ?: return arrayListOf()
        val psiManager = PsiManager.getInstance(project)
        val files = arrayListOf<C3File>()

        fun recurse(vf: VirtualFile)
        {
            if (vf.isDirectory)
            {
                vf.children.forEach { recurse(it) }
            } else
            {
                val psi = psiManager.findFile(vf)
                if (psi != null && psi.language == C3Language.INSTANCE)
                {
                    files.add(psi as C3File)
                }
            }
        }

        recurse(virtualRoot)

        return files
    }

    fun findBestMatch(target: String, candidates: List<String>): String?
    {
        if (candidates.isEmpty()) return null

        return candidates.minByOrNull { calculateLevenshteinDistance(target, it) }
    }

    private fun calculateLevenshteinDistance(s1: String, s2: String): Int
    {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m)
        {
            dp[i][0] = i
        }

        for (j in 0..n)
        {
            dp[0][j] = j
        }

        for (i in 1..m)
        {
            for (j in 1..n)
            {
                dp[i][j] = when
                {
                    s1[i - 1] == s2[j - 1] -> dp[i - 1][j - 1]
                    else                   -> minOf(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + 1
                    )
                }
            }
        }

        return dp[m][n]
    }
}