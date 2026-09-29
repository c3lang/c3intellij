package org.c3lang.intellij.index;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3BaseType;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3Path;
import org.c3lang.intellij.psi.C3PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class NameIndexService
{
    public static final NameIndexService INSTANCE = new NameIndexService();

    private NameIndexService()
    {
    }

    @NotNull
    public Collection<C3FullyQualifiedNamePsiElement> findByNameEndsWith(@NotNull String name, @NotNull Project project)
    {
        List<C3FullyQualifiedNamePsiElement> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (key.endsWith(name))
            {
                for (C3PsiElement element : getElementsByName(key, project))
                {
                    if (element instanceof C3FullyQualifiedNamePsiElement named)
                    {
                        result.add(named);
                    }
                }
            }
        }
        return result;
    }

    @NotNull
    public Collection<C3CallablePsiElement> findMethodsForType(@NotNull org.c3lang.intellij.psi.FullyQualifiedName type, @org.jetbrains.annotations.Nullable String methodName, @NotNull Project project)
    {
        List<C3CallablePsiElement> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String suffix = methodName != null ? "." + methodName : null;
        String typeName = type.getName();
        int ltIndex = typeName.indexOf('<');
        if (ltIndex > 0)
        {
            typeName = typeName.substring(0, ltIndex).trim();
        }
        int parenIndex = typeName.indexOf('(');
        if (parenIndex > 0)
        {
            typeName = typeName.substring(0, parenIndex).trim();
        }

        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (suffix != null && !key.endsWith(suffix) && (methodName == null || !key.endsWith("::" + methodName))) continue;
            for (C3PsiElement element : getElementsByName(key, project))
            {
                if (!(element instanceof C3CallablePsiElement callable)) continue;
                if (callable.getType() != null)
                {
                    String targetTypeName = callable.getType().getValue();
                    if (targetTypeName.equals(typeName) || targetTypeName.equals(type.getFullName()))
                    {
                        if (suffix == null || callable.getFqName().getName().endsWith(suffix))
                        {
                            result.add(callable);
                        }
                    }
                }
                else if (callable instanceof C3FuncDef funcDef
                    && InterfaceService.isInterfaceMethodOf(funcDef, type, typeName)
                    && (suffix == null || (funcDef.getNameIdent() != null && funcDef.getNameIdent().equals(methodName))))
                {
                    // Method declared in a matching interface, e.g. `fn String myname();`
                    // in `interface MyName` for a `MyName` receiver. Interface methods
                    // have no owner type, they are matched via the parent interface.
                    result.add(funcDef);
                }
            }
        }
        return result;
    }

    @NotNull
    public Collection<C3CallablePsiElement> findMethodsByName(@NotNull String name, @NotNull Project project)
    {
        List<C3CallablePsiElement> result = new ArrayList<>();
        for (C3FullyQualifiedNamePsiElement element : findByNameEndsWith(name, project))
        {
            if (element instanceof C3CallablePsiElement callable
                && callable.getType() != null
                && callable.getFqName().getName().endsWith("." + name))
            {
                result.add(callable);
            }
        }
        return result;
    }

    @NotNull
    public Collection<C3FullyQualifiedNamePsiElement> findType(@NotNull C3BaseType type, @NotNull Project project)
    {
        String nameIdent = type.getNameIdent();
        if (nameIdent == null) return Collections.emptyList();

        C3Path path = type.getPath();
        String target = path != null ? path.getText() + nameIdent : nameIdent;

        List<C3FullyQualifiedNamePsiElement> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        for (String key : StubIndex.getInstance().getAllKeys(NameIndex.KEY, project))
        {
            if (key.equals(target) || key.endsWith("::" + target))
            {
                for (C3PsiElement element : getElementsByName(key, project))
                {
                    if (element instanceof C3FullyQualifiedNamePsiElement named)
                    {
                        result.add(named);
                    }
                }
            }
        }
        return result;
    }

    @NotNull
    private Collection<C3PsiElement> getElementsByName(@NotNull String string, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return Collections.emptyList();
        try
        {
            return StubIndex.getElements(
                NameIndex.KEY,
                string,
                project,
                C3ProjectService.getInstance(project).getSearchScope(),
                C3PsiElement.class
            );
        }
        catch (Exception ignored)
        {
            // Stale index entry for a file without a stub tree: degrade to
            // empty instead of breaking highlighting/resolution.
            return Collections.emptyList();
        }
    }
}
