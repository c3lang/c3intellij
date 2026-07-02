package org.c3lang.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public sealed interface ImportPossibility permits ImportPossibility.Noop, ImportPossibility.Possible
{
    static @NotNull ImportPossibility create(@NotNull C3PathIdent psi)
    {
        C3Path path = psi.getPath();
        if (path == null) return Noop.INSTANCE;

        ASTNode node = path.getNode();
        if (node == null) return Noop.INSTANCE;

        ASTNode[] scopes = node.getChildren(TokenSet.create(C3Types.SCOPE));
        if (scopes.length <= 1) return Noop.INSTANCE;

        C3ModuleDefinition moduleDefinition = Objects.requireNonNull(
            PsiTreeUtil.getParentOfType(psi, C3ModuleDefinition.class)
        );
        ModuleName importIntention = new ModuleName(dropPostfix(path.getText(), "::"));
        List<String> importSuffixes = moduleDefinition.getImports().stream()
            .map(ModuleName::getSuffix)
            .toList();

        if (importSuffixes.contains(importIntention.getSuffix()))
        {
            return Noop.INSTANCE;
        }

        return new Possible(importIntention);
    }

    private static @NotNull String dropPostfix(@NotNull String value, @NotNull String postfix)
    {
        return value.endsWith(postfix) ? value.substring(0, value.length() - postfix.length()) : value;
    }

    final class Noop implements ImportPossibility
    {
        public static final Noop INSTANCE = new Noop();

        private Noop()
        {
        }
    }

    final class Possible implements ImportPossibility
    {
        private final ModuleName importIntention;

        public Possible(@NotNull ModuleName importIntention)
        {
            this.importIntention = importIntention;
        }

        @NotNull
        public ModuleName getImportIntention()
        {
            return importIntention;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Possible possible)) return false;
            return importIntention.equals(possible.importIntention);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(importIntention);
        }

        @Override
        public String toString()
        {
            return "Possible(importIntention=" + importIntention + ")";
        }
    }

}
