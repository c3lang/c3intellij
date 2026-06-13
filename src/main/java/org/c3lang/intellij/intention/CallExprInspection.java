package org.c3lang.intellij.intention;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.c3lang.intellij.C3Util;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3ImportDecl;
import org.c3lang.intellij.psi.C3ImportPath;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3TopLevel;
import org.c3lang.intellij.psi.C3Visitor;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CallExprInspection extends LocalInspectionTool
{
    @Override
    public @NotNull PsiElementVisitor buildVisitor(
            @NotNull ProblemsHolder holder,
            boolean isOnTheFly,
            @NotNull LocalInspectionToolSession session)
    {
        if (!(session.getFile() instanceof C3File))
        {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return new C3Visitor()
        {
            @Override
            public void visitCallExpr(@NotNull C3CallExpr element)
            {
                List<String> imports = new ArrayList<>();
                for (C3ImportDecl importDecl : element.getModuleDefinition().getImportDeclarations())
                {
                    for (C3ImportPath importPath : importDecl.getImportPaths().getImportPathList())
                    {
                        imports.add(java.util.Objects.requireNonNull(importPath.getModuleName()).getValue());
                    }
                }

                Set<String> result = new HashSet<>();
                for (String importName : imports)
                {
                    result.addAll(C3Util.INSTANCE.findC3ModulesStartingWith(element.getProject(), importName));
                }

                C3File file = (C3File) element.getContainingFile();
                if (!element.getText().contains("("))
                {
                    return;
                }

                String fullName = element.getText().substring(0, element.getText().indexOf('('));
                int moduleSeparator = fullName.lastIndexOf("::");
                String name = moduleSeparator >= 0 ? fullName.substring(moduleSeparator + 2) : fullName;
                String callModule = moduleSeparator >= 0 ? fullName.substring(0, moduleSeparator) : "";

                if (!callModule.isEmpty())
                {
                    boolean hasImport = false;
                    for (String imported : result)
                    {
                        if (imported.endsWith(callModule))
                        {
                            hasImport = true;
                            break;
                        }
                    }

                    if (!hasImport)
                    {
                        holder.registerProblem(element, "No suitable import found for module '" + callModule + "'");
                        return;
                    }
                }
                else
                {
                    C3ModuleDefinition moduleDefinition = element.getModuleDefinition();
                    List<C3FuncDefinition> functions = new ArrayList<>();
                    List<C3MacroDefinition> macros = new ArrayList<>();

                    for (PsiElement child : moduleDefinition.getChildren())
                    {
                        if (child instanceof C3TopLevel topLevel)
                        {
                            C3FuncDefinition function = topLevel.getFuncDefinition();
                            if (function != null && function.getFuncDef().getFuncHeader().getFuncName().getText().equals(name))
                            {
                                functions.add(function);
                            }

                            C3MacroDefinition macro = topLevel.getMacroDefinition();
                            if (macro != null && macro.getMacroHeader().getMacroName().getText().equals(name))
                            {
                                macros.add(macro);
                            }
                        }
                    }

                    if (functions.isEmpty() && macros.isEmpty())
                    {
                        holder.registerProblem(element, "'" + name + "' not found in current module");
                    }

                    return;
                }

                Either<C3MacroDefinition, C3FuncDefinition> declaration = null;
                for (String imported : result)
                {
                    if (imported.endsWith(callModule))
                    {
                        declaration = C3Util.INSTANCE.findDeclarationInModule(element.getProject(), imported, name);
                        if (declaration != null) break;
                    }
                }

                if (declaration == null)
                {
                    ArrayList<Either<C3FuncDefinition, C3MacroDefinition>> declarations =
                        C3Util.INSTANCE.findDeclarationsInModule(file.getProject(), callModule);
                    List<String> candidates = new ArrayList<>();

                    for (Either<C3FuncDefinition, C3MacroDefinition> candidate : declarations)
                    {
                        if (candidate.isLeft())
                        {
                            candidates.add(candidate.getLeft().getFuncDef().getFuncHeader().getFuncName().getText());
                        }
                        if (candidate.isRight())
                        {
                            candidates.add(candidate.getRight().getMacroHeader().getMacroName().getText());
                        }
                    }

                    holder.registerProblem(
                        element,
                        "Declaration not found. did you mean " + callModule + "::"
                            + C3Util.INSTANCE.findBestMatch(name, candidates)
                    );
                }
            }
        };
    }
}
