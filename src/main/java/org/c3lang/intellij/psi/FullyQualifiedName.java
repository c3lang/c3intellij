package org.c3lang.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class FullyQualifiedName
{
    private final ModuleName module;
    private final String name;
    private final String suffixName;
    private final String fullName;

    public FullyQualifiedName(@Nullable ModuleName module, @NotNull String name)
    {
        this.module = module;
        this.name = name;
        this.suffixName = module != null ? module.getSuffix() + "::" + name : name;
        this.fullName = module != null ? module.getValue() + "::" + name : name;
    }

    @Nullable
    public ModuleName getModule()
    {
        return module;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getSuffixName()
    {
        return suffixName;
    }

    @NotNull
    public String getFullName()
    {
        return fullName;
    }

    @NotNull
    public ShortType asShortType()
    {
        return new ShortType(name, module != null ? module.getSuffix() : null);
    }

    public static @NotNull FullyQualifiedName parse(@NotNull String string)
    {
        int separator = string.lastIndexOf("::");
        if (separator < 0)
        {
            return new FullyQualifiedName(null, string);
        }
        String module = string.substring(0, separator);
        String name = string.substring(separator + 2);
        return new FullyQualifiedName(module.isEmpty() ? null : new ModuleName(module), name);
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3TypeName psi)
    {
        return from(psi, psi.getModuleName());
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3TypeName psi, @Nullable ModuleName module)
    {
        return new FullyQualifiedName(module, psi.getText());
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3FuncHeader psi, @Nullable ModuleName module)
    {
        return new FullyQualifiedName(module, psi.getFuncName().getText());
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3MacroHeader psi, @Nullable ModuleName module)
    {
        return new FullyQualifiedName(module, psi.getMacroName().getText());
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3ConstDeclarationStmt psi)
    {
        return from(psi, ModuleName.from(psi));
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3ConstDeclarationStmt psi, @Nullable ModuleName module)
    {
        return new FullyQualifiedName(module, readConstIdent(psi.getNode()));
    }

    public static @Nullable FullyQualifiedName from(@NotNull C3OptionalType psi)
    {
        List<FullyQualifiedName> resolved = psi.getModuleDefinition().resolve(psi.getType());
        return resolved.size() == 1 ? resolved.get(0) : null;
    }

    public static @Nullable FullyQualifiedName from(@NotNull C3Type psi)
    {
        List<FullyQualifiedName> resolved = psi.getModuleDefinition().resolve(psi);
        return resolved.size() == 1 ? resolved.get(0) : null;
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3FaultDefinition psi)
    {
        return from(psi, ModuleName.from(psi));
    }

    public static @NotNull FullyQualifiedName from(@NotNull C3FaultDefinition psi, @Nullable ModuleName module)
    {
        return new FullyQualifiedName(module, readConstIdent(psi.getNode()));
    }

    private static @NotNull String readConstIdent(@NotNull ASTNode node)
    {
        ASTNode[] children = node.getChildren(TokenSet.create(C3Types.CONST_IDENT));
        if (children.length == 0)
        {
            throw new IllegalStateException("CONST_IDENT missing");
        }
        PsiElement psi = children[0].getPsi();
        return psi.getText();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FullyQualifiedName that)) return false;
        return Objects.equals(module, that.module) && name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(module, name);
    }

    @Override
    public String toString()
    {
        return "FullyQualifiedName(module=" + module + ", name=" + name + ")";
    }

}
