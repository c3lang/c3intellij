package org.c3lang.intellij.intention

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import org.c3lang.intellij.C3Util.findBestMatch
import org.c3lang.intellij.C3Util.findC3ModulesStartingWith
import org.c3lang.intellij.C3Util.findDeclarationInModule
import org.c3lang.intellij.C3Util.findDeclarationsInModule
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3File
import org.c3lang.intellij.psi.C3FuncDefinition
import org.c3lang.intellij.psi.C3MacroDefinition
import org.c3lang.intellij.psi.C3TopLevel
import org.c3lang.intellij.psi.C3Visitor
import org.eclipse.lsp4j.jsonrpc.messages.Either

class CallExprInspection : LocalInspectionTool()
{
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor
    {
        if (session.file !is C3File)
        {
            return PsiElementVisitor.EMPTY_VISITOR
        }

        return object : C3Visitor()
        {
            override fun visitCallExpr(element: C3CallExpr)
            {
                val imports = arrayListOf<String>()
                element.moduleDefinition.importDeclarations.forEach { it.importPaths.importPathList.map { list -> imports.add(list.moduleName?.value!!) } }

                val result = mutableSetOf<String>()

                imports.forEach { import ->
                    result.addAll(findC3ModulesStartingWith(element.project, import))
                }

                val file = element.containingFile as C3File
                val fullName = element.text.substring(0, element.text.indexOfFirst { it == '(' })
                val module = element.moduleDefinition.moduleName?.value ?: ""
                val name = fullName.substringAfterLast("::")
                var callModule = ""

                if (fullName.contains("::"))
                {
                    callModule = fullName.substringBeforeLast("::")
                }

                if (callModule.isNotEmpty())
                {
                    if (result.none { it.endsWith(callModule) })
                    {
                        holder.registerProblem(element, "No suitable import found for module '$callModule'")
                        return
                    }
                }
                else
                {
                    val topLevels = element.moduleDefinition.children.filterIsInstance<C3TopLevel>()
                    val functions = arrayListOf<C3FuncDefinition>()
                    val macros = arrayListOf<C3MacroDefinition>()

                    topLevels.forEach { topLevel -> functions.addAll(topLevel.children.filterIsInstance<C3FuncDefinition>().filter { it.funcDef.funcHeader.funcName.text == name }) }
                    topLevels.forEach { topLevel -> macros.addAll(topLevel.children.filterIsInstance<C3MacroDefinition>().filter { it.macroHeader.macroName.text == name }) }

                    if (functions.isEmpty() && macros.isEmpty())
                    {
                        holder.registerProblem(element, "'$name' not found in current module")
                    }

                    return
                }
//                    // check if is in current module
//                    val declarations = findDeclarationsInModule(file.project, module)
//
//                    val functions = declarations.filter { it.isLeft }.filter { it.left.funcDef.funcHeader.funcName.text == name }
//                    val macros    = declarations.filter { it.isRight }.filter { it.right.macroHeader.macroName.text == name }
//
//                    if (functions.isNotEmpty() || macros.isNotEmpty())
//                    {
//
//                        return
//                    }
//                }

                // TODO: else check if it's builtin
                // TODO: else check if it's in the current module


                var declaration: Either<C3MacroDefinition, C3FuncDefinition>? = null

                result.forEach {
                    if (it.endsWith(module))
                    {
                        // TODO: filter module for function or macro with name
                        declaration = findDeclarationInModule(element.project, it, name)

                        if (declaration != null) return@forEach
                    }
                }

                if (declaration == null)
                {
                    val declarations = findDeclarationsInModule(file.project, callModule)
                    val candidates = arrayListOf<String>()

                    candidates.addAll(declarations.filter { it.isLeft }.map { it.left.funcDef.funcHeader.funcName.text })
                    candidates.addAll(declarations.filter { it.isRight }.map { it.right.macroHeader.macroName.text })

                    holder.registerProblem(element, "Declaration not found. did you mean $callModule::${findBestMatch(name, candidates)}")
                }
            }
        }
    }
}