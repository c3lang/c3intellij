//package org.c3lang.intellij.annotation
//
//import com.intellij.lang.annotation.AnnotationHolder
//import com.intellij.lang.annotation.HighlightSeverity
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.roots.ProjectRootManager
//import com.intellij.openapi.vfs.LocalFileSystem
//import com.intellij.openapi.vfs.VfsUtilCore
//import com.intellij.openapi.vfs.VirtualFile
//import com.intellij.psi.PsiElement
//import com.intellij.psi.PsiManager
//import org.c3lang.intellij.C3Language
//import org.c3lang.intellij.psi.C3CallExpr
//import org.c3lang.intellij.psi.C3File
//import org.c3lang.intellij.psi.C3FuncDefinition
//import org.c3lang.intellij.psi.C3MacroDefinition
//import org.c3lang.intellij.psi.C3ModuleSection
//import org.c3lang.intellij.psi.C3TopLevel
//import java.io.File
//
//// TODO: add quick fix
//// TODO: check if import is present in case of other module and prompt to add it
//internal fun annotateCallExpr(element: C3CallExpr, holder: AnnotationHolder)
//{
//    val file = element.containingFile as C3File
//    val fullName = element.text.substring(0, element.text.indexOfFirst { it == '(' })
//    var module = element.moduleDefinition.moduleName?.value ?: ""
//    if (fullName.contains("::"))
//    {
//        module = fullName.substring(0, fullName.lastIndexOf("::"))
//    }
//    val name = fullName.substringAfterLast("::")
//
//    // TODO: add sout and soutf shortcuts
//
//    // TODO: if module is empty set module to current module
//    // TODO: if current module is default module search current file
//
//    /**
//     * @see C3FuncDefinition
//     * @see C3MacroDefinition
//     */
//    val declaration = findDeclaration(file.project, element, file, module, name, holder)
//
//    if (declaration == null)
//    {
//        holder.newAnnotation(HighlightSeverity.WARNING, "Declaration not found")
//            .create()
//    }
//}
//
//// TODO: scan all files in project and std lib for modules with matching name
//// TODO: then search that module for functions with the same name
//private fun findDeclaration(project: Project, element: C3CallExpr, file: C3File, module: String, name: String, holder: AnnotationHolder): PsiElement?
//{
//    if (module.isEmpty())
//    {
//        val decl = findDeclarationInFile(file, name)
//        return if (decl.isNotEmpty())
//        {
//            decl.first()
//        } else
//        {
//            null
//        }
//    } else
//    {
//        val decl = findDeclarationInModule(file.project, module, name)
//
//        if (decl.isNotEmpty())
//        {
//            val firstMatch = decl.first()
//            val validImports = arrayListOf<String>()
//
//            if (firstMatch is C3MacroDefinition)
//            {
//                validImports.addAll(element.moduleDefinition.imports.filter { it.value == firstMatch.moduleDefinition.moduleName?.value }.map { it.value })
//            } else if (firstMatch is C3FuncDefinition)
//            {
//                validImports.addAll(element.moduleDefinition.imports.filter { it.value == firstMatch.moduleDefinition.moduleName?.value }.map { it.value })
//            }
//
//            if (validImports.isEmpty())
//            {
////                holder.newAnnotation(HighlightSeverity.WARNING, "Missing import")
////                    .range(element)
////                    .newLocalQuickFix(AddMissingImportFix(), InspectionManager.getInstance(project).createProblemDescriptor("", AddMissingImportFix()) as ProblemDescriptor)
////                    .registerFix()
//            }
//
//            return firstMatch
//        } else
//        {
//            return null
//        }
//    }
//}
//
//fun findDeclarationInModule(project: Project, module: String, name: String): ArrayList<PsiElement>
//{
//    val stdLibPath = guessStdLibPath() ?: return arrayListOf()
//    val stdLibFiles = walkStdLib(project, stdLibPath)
//    val matches = arrayListOf<PsiElement>()
//
//    stdLibFiles.forEach { file ->
//        val modules = file.children.filterIsInstance<C3ModuleSection>().filter { it.moduleName?.value?.endsWith(module) ?: false }
//
//        modules.forEach { module ->
//            val topLevels = module.children.filterIsInstance<C3TopLevel>()
//
//            topLevels.forEach { topLevel ->
//                topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }.forEach { matches.add(it) }
//                topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }.forEach { matches.add(it) }
//            }
//        }
//    }
//
//    val projectFiles = mutableListOf<C3File>()
//    val psiManager = PsiManager.getInstance(project)
//
//    val contentRoots = ProjectRootManager.getInstance(project).contentRoots
//    for (root in contentRoots)
//    {
//        VfsUtilCore.iterateChildrenRecursively(root, null) { file ->
//            if (!file.isDirectory)
//            {
//                psiManager.findFile(file)?.let { psiFile ->
//                    if (psiFile.language == C3Language.INSTANCE)
//                    {
//                        projectFiles.add(psiFile as C3File)
//                    }
//                }
//            }
//            true
//        }
//    }
//
//    // TODO: add module search
//    projectFiles.forEach { file ->
//        val modules = file.children.filterIsInstance<C3ModuleSection>().filter { it.moduleName?.value?.endsWith(module) ?: false }
//
//        modules.forEach { module ->
//            val topLevels = module.children.filterIsInstance<C3TopLevel>()
//
//            topLevels.forEach { topLevel ->
//                topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }.forEach { matches.add(it) }
//                topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }.forEach { matches.add(it) }
//            }
//        }
//    }
//
//    return matches
//}
//
//fun walkStdLib(project: Project, stdLibPath: File): ArrayList<C3File>
//{
//    val virtualRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(stdLibPath) ?: return arrayListOf()
//    val psiManager = PsiManager.getInstance(project)
//    val files = arrayListOf<C3File>()
//
//    fun recurse(vf: VirtualFile)
//    {
//        if (vf.isDirectory)
//        {
//            vf.children.forEach { recurse(it) }
//        } else
//        {
//            val psi = psiManager.findFile(vf)
//            if (psi != null && psi.language == C3Language.INSTANCE)
//            {
//                files.add(psi as C3File)
//            }
//        }
//    }
//
//    recurse(virtualRoot)
//
//    return files
//}
//
///**
// * Expensive operation
// *
// * @param startDir the start directory to start looking from
// * @return the guessed library directory
// */
//fun guessStdLibPath(startDir: File = File("/")): File?
//{
//    val queue = ArrayDeque<File>()
//    queue.add(startDir)
//
//    while (queue.isNotEmpty())
//    {
//        val current = queue.removeFirst()
//
//        if (current.isDirectory)
//        {
//            val lib = File(current, "lib")
//            val std = File(lib, "std")
//            if (current.name == "c3c" && lib.exists() && std.exists() && std.isDirectory)
//            {
//                return std
//            }
//
//            current.listFiles()?.filter { it.isDirectory }?.let { queue.addAll(it) }
//        }
//    }
//
//    return null
//}
//
//fun findDeclarationInFile(file: C3File, name: String): ArrayList<PsiElement>
//{
//    // TODO: find module -> find top level -> find function, macro
//    val modules = file.children.filterIsInstance<C3ModuleSection>()
//    val matches = arrayListOf<PsiElement>()
//
//    modules.forEach { module ->
//        val topLevels = module.children.filterIsInstance<C3TopLevel>()
//        topLevels.forEach { topLevel ->
//            topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }.forEach { matches.add(it) }
//            topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }.forEach { matches.add(it) }
//        }
//    }
//
//    return matches
////    return file.children.filterIsInstance<C3FuncDefinition>().first { it.funcDef.funcHeader.funcName.text == name }
//}