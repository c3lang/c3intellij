package org.c3lang.intellij.intention;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.completion.PsiElementUtils;
import org.c3lang.intellij.psi.C3DefaultModuleSection;
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3ImportDecl;
import org.c3lang.intellij.psi.C3ModuleDefinition;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.c3lang.intellij.psi.C3Path;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AddImportQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement
{
    public static final Companion Companion = new Companion();
    public static final Key<ModuleName> KEY = Key.create("AddImportQuickFix");

    private final ModuleName importIntention;

    public AddImportQuickFix(@NotNull C3Path target, @NotNull ModuleName importIntention)
    {
        super(target);
        this.importIntention = importIntention;
    }

    @Override
    public @NotNull String getFamilyName()
    {
        return "Add import " + importIntention.getValue();
    }

    @Override
    public @NotNull String getText()
    {
        return "Add import " + importIntention.getValue();
    }

    @Override
    public void invoke(
            @NotNull Project project,
            @NotNull PsiFile file,
            @Nullable Editor editor,
            @NotNull PsiElement startElement,
            @NotNull PsiElement endElement)
    {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            C3Path path = (C3Path) startElement;
            C3ModuleDefinition moduleSection = java.util.Objects.requireNonNull(
                PsiTreeUtil.getParentOfType(path, C3ModuleDefinition.class)
            );

            path.putUserData(KEY, importIntention);

            C3FullyQualifiedNamePsiElement element =
                PsiTreeUtil.getParentOfType(path, C3FullyQualifiedNamePsiElement.class);
            if (element != null)
            {
                addImport(element, moduleSection, project);
            }

            path.shorten();
        });
    }

    public static @Nullable ModuleName addImport(
            @NotNull C3FullyQualifiedNamePsiElement element,
            @NotNull C3ModuleDefinition moduleSection,
            @NotNull Project project)
    {
        return Companion.addImport(element, moduleSection, project);
    }

    public static @Nullable ImportAction addImportAsText(
            @NotNull C3FullyQualifiedNamePsiElement element,
            @NotNull C3ModuleDefinition moduleSection)
    {
        return Companion.addImportAsText(element, moduleSection);
    }

    public static void writeImport(@NotNull Document document, int offset, @NotNull ModuleName moduleName)
    {
        Companion.writeImport(document, offset, moduleName);
    }

    public static final class Companion
    {
        private Companion()
        {
        }

        public @NotNull Key<ModuleName> getKEY()
        {
            return KEY;
        }

        public @Nullable ModuleName addImport(
                @NotNull C3FullyQualifiedNamePsiElement element,
                @NotNull C3ModuleDefinition moduleSection,
                @NotNull Project project)
        {
            ModuleName moduleName = element.getModuleName();
            if (moduleName == null) return null;

            ModuleName visibleModulePrefix = moduleSection.getVisibleModulePrefix(moduleName);
            if (visibleModulePrefix != null)
            {
                return visibleModulePrefix;
            }

            List<C3ImportDecl> imports = moduleSection.getImportDeclarations();
            if (!imports.isEmpty())
            {
                PsiElement importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, moduleName.getValue()),
                    imports.get(imports.size() - 1).getParent()
                );
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement);
            }
            else if (moduleSection instanceof C3ModuleSection module)
            {
                PsiElement importDeclarationElement = moduleSection.addAfter(
                    PsiElementUtils.createImport(project, moduleName.getValue()),
                    module.getModule()
                );
                moduleSection.addBefore(PsiElementUtils.createNewLine(project), importDeclarationElement);
            }
            else if (moduleSection instanceof C3DefaultModuleSection)
            {
                PsiElement importDeclarationElement = moduleSection.addBefore(
                    PsiElementUtils.createImport(project, moduleName.getValue()),
                    moduleSection.getFirstChild()
                );
                moduleSection.addAfter(PsiElementUtils.createNewLine(project), importDeclarationElement);
            }

            return moduleName;
        }

        public @Nullable ImportAction addImportAsText(
                @NotNull C3FullyQualifiedNamePsiElement element,
                @NotNull C3ModuleDefinition moduleSection)
        {
            ModuleName moduleName = element.getModuleName();
            if (moduleName == null) return null;

            ModuleName visibleModulePrefix = moduleSection.getVisibleModulePrefix(moduleName);
            if (visibleModulePrefix != null)
            {
                return new ImportAction.Imported(visibleModulePrefix);
            }

            List<C3ImportDecl> imports = moduleSection.getImportDeclarations();
            if (!imports.isEmpty())
            {
                C3ImportDecl lastImport = imports.get(imports.size() - 1);
                return new ImportAction.AppendToImport(
                    lastImport.getImportPaths().getTextRange().getEndOffset(),
                    moduleName
                );
            }

            int startOffset;
            if (moduleSection instanceof C3ModuleSection module)
            {
                startOffset = module.getModule().getTextRange().getEndOffset();
            }
            else
            {
                startOffset = moduleSection.getFirstChild().getTextRange().getStartOffset();
            }

            return new ImportAction.InsertImport(startOffset, moduleName);
        }

        public void writeImport(@NotNull Document document, int offset, @NotNull ModuleName moduleName)
        {
            document.insertString(offset, "\nimport " + moduleName.getValue() + ";\n");
        }
    }

    public sealed interface ImportAction permits ImportAction.InsertImport, ImportAction.AppendToImport, ImportAction.Imported
    {
        @NotNull
        ModuleName getModuleName();

        default void write(@NotNull Document document)
        {
        }

        final class InsertImport implements ImportAction
        {
            private final int offset;
            private final ModuleName moduleName;

            public InsertImport(int offset, @NotNull ModuleName moduleName)
            {
                this.offset = offset;
                this.moduleName = moduleName;
            }

            public int getOffset()
            {
                return offset;
            }

            @Override
            public @NotNull ModuleName getModuleName()
            {
                return moduleName;
            }

            @Override
            public void write(@NotNull Document document)
            {
                document.insertString(offset, "\nimport " + moduleName.getValue() + ";\n");
            }
        }

        final class AppendToImport implements ImportAction
        {
            private final int offset;
            private final ModuleName moduleName;

            public AppendToImport(int offset, @NotNull ModuleName moduleName)
            {
                this.offset = offset;
                this.moduleName = moduleName;
            }

            public int getOffset()
            {
                return offset;
            }

            @Override
            public @NotNull ModuleName getModuleName()
            {
                return moduleName;
            }

            @Override
            public void write(@NotNull Document document)
            {
                document.insertString(offset, ", " + moduleName.getValue());
            }
        }

        final class Imported implements ImportAction
        {
            private final ModuleName moduleName;

            public Imported(@NotNull ModuleName moduleName)
            {
                this.moduleName = moduleName;
            }

            @Override
            public @NotNull ModuleName getModuleName()
            {
                return moduleName;
            }
        }
    }
}
