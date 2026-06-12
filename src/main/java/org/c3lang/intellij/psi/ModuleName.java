package org.c3lang.intellij.psi;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ModuleName
{
    public static final Companion Companion = new Companion();

    private static final ModuleName CORE_MODULE = new ModuleName("std::core");

    private final String value;
    private final String suffix;

    public ModuleName(@NotNull String value)
    {
        this.value = value;
        int separator = value.lastIndexOf("::");
        this.suffix = separator >= 0 ? value.substring(separator + 2) : value;
    }

    @NotNull
    public String getValue()
    {
        return value;
    }

    @NotNull
    public String getSuffix()
    {
        return suffix;
    }

    public boolean covers(@Nullable ModuleName moduleName)
    {
        if (moduleName == null) return false;
        return moduleName.value.equals(value) || moduleName.value.startsWith(value + "::");
    }

    public @Nullable String relativePathTo(@Nullable ModuleName moduleName)
    {
        if (!covers(moduleName)) return null;
        if (moduleName == null || moduleName.value.equals(value)) return "";
        return moduleName.value.substring((value + "::").length());
    }

    public static @Nullable ModuleName from(@NotNull C3PsiElement psi)
    {
        return Companion.from(psi);
    }

    public static @NotNull ModuleName deserialize(@NotNull String string)
    {
        return Companion.deserialize(string);
    }

    public static @Nullable ModuleName autoImportedPrefix(@Nullable ModuleName moduleName)
    {
        return Companion.autoImportedPrefix(moduleName);
    }

    public static @NotNull List<ModuleName> getImportList(@NotNull C3PsiElement psi)
    {
        return Companion.getImportList(psi);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ModuleName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

    @Override
    public String toString()
    {
        return "ModuleName(value=" + value + ")";
    }

    public static final class Companion
    {
        private Companion()
        {
        }

        public @Nullable ModuleName from(@NotNull C3PsiElement psi)
        {
            C3ModuleSection moduleSection = PsiTreeUtil.getParentOfType(psi, C3ModuleSection.class, true);
            if (moduleSection == null) return null;
            return new ModuleName(moduleSection.getModule().getModulePath().getText());
        }

        public @NotNull ModuleName deserialize(@NotNull String string)
        {
            return new ModuleName(string);
        }

        public @Nullable ModuleName autoImportedPrefix(@Nullable ModuleName moduleName)
        {
            return CORE_MODULE.covers(moduleName) ? CORE_MODULE : null;
        }

        public @NotNull List<ModuleName> getImportList(@NotNull C3PsiElement psi)
        {
            C3ModuleDefinition moduleSection = PsiTreeUtil.getParentOfType(psi, C3ModuleDefinition.class, true);
            if (moduleSection == null) return List.of();

            List<ModuleName> imports = new ArrayList<>();
            for (C3ImportDecl importDecl : moduleSection.getImportDeclarations())
            {
                for (C3ImportPath importPath : importDecl.getImportPaths().getImportPathList())
                {
                    imports.add(new ModuleName(importPath.getText()));
                }
            }
            return imports;
        }
    }
}
