package org.c3lang.intellij.index;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import kotlin.Pair;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.AccessPath;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class StructService
{
    public static final StructService INSTANCE = new StructService();

    private StructService()
    {
    }

    @NotNull
    public List<C3StructMemberDeclaration> getStructMembers(@NotNull String query, @NotNull Project project)
    {
        List<C3StructMemberDeclaration> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        // Never call StubIndex.getElements() with a key that is not in the index:
        // it logs "Stub ids not found for key" (see StubProcessingHelper).
        // The query may be short ("MyTr.a") while index keys are fully
        // qualified ("testproject::MyTr.a"), so match by suffix and only
        // query keys that actually exist.
        for (String key : StubIndex.getInstance().getAllKeys(StructMemberDeclarationIndex.KEY, project))
        {
            if (key.equals(query) || key.endsWith("::" + query))
            {
                result.addAll(getStructMembersByExactKey(key, project));
            }
        }
        return result;
    }

    @NotNull
    private List<C3StructMemberDeclaration> getStructMembersByExactKey(@NotNull String key, @NotNull Project project)
    {
        List<C3StructMemberDeclaration> result = new ArrayList<>();
        Collection<C3PsiElement> elements;
        try
        {
            elements = StubIndex.getElements(
                StructMemberDeclarationIndex.KEY,
                key,
                project,
                C3ProjectService.getInstance(project).getSearchScope(),
                C3PsiElement.class);
        }
        catch (Exception ignored)
        {
            // Stale index entry for a file without a stub tree.
            return result;
        }
        for (C3PsiElement element : elements)
        {
            if (element instanceof C3StructMemberDeclaration declaration)
            {
                result.add(declaration);
            }
        }
        return result;
    }

    @NotNull
    public List<C3StructMemberDeclaration> findStructMembers(@NotNull String query, @NotNull Project project)
    {
        List<C3StructMemberDeclaration> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String shortQuery = stripModule(query);
        for (String key : StubIndex.getInstance().getAllKeys(StructMemberDeclarationIndex.KEY, project))
        {
            if (key.startsWith(query)
                || key.equals(query)
                || key.endsWith("::" + query)
                || stripModule(key).startsWith(shortQuery))
            {
                result.addAll(getStructMembersByExactKey(key, project));
            }
        }
        return result;
    }

    @NotNull
    public List<C3StructMemberDeclaration> findStructMembersByName(@NotNull String name, @NotNull Project project)
    {
        List<C3StructMemberDeclaration> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String suffix = "." + name;
        for (String key : StubIndex.getInstance().getAllKeys(StructMemberDeclarationIndex.KEY, project))
        {
            if (key.endsWith(suffix))
            {
                result.addAll(getStructMembersByExactKey(key, project));
            }
        }
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public List<Pair<AccessPath, String>> getFields(
            @NotNull FullyQualifiedName rootType,
            @NotNull List<String> path,
            @NotNull Project project)
    {
        String query = rootType.getFullName();
        List<Pair<AccessPath, String>> fields = List.of();

        for (String ident : path)
        {
            List<C3StructMemberDeclaration> structMembers = getStructMembers(query + "." + ident, project);
            C3StructMemberDeclaration member = structMembers.size() == 1 ? structMembers.get(0) : null;

            List<Pair<AccessPath, String>> nextFields = new ArrayList<>();
            for (C3StructMemberDeclaration structField : findStructMembers(query + "." + ident, project))
            {
                String structPath = structField.getStructPath();
                if (structPath != null)
                {
                    nextFields.add(new Pair<>(
                        new AccessPath(structPath),
                        structField.getType() != null ? structField.getType().getText() : ""
                    ));
                }
            }
            fields = nextFields;

            String structPathType = member != null && member.getStructPathType() != null
                ? member.getStructPathType().getFullName()
                : null;
            if (structPathType == null) break;
            query = structPathType;
        }

        int expectedSize = dotCount(query) + 1;
        List<Pair<AccessPath, String>> result = new ArrayList<>();
        for (Pair<AccessPath, String> field : fields)
        {
            if (field.getFirst().getSegments().size() == expectedSize)
            {
                result.add(field);
            }
        }
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public List<C3StructMemberDeclaration> getStructMemberDeclaration(
            @NotNull FullyQualifiedName rootType,
            @NotNull List<String> path,
            @NotNull Project project)
    {
        String query = rootType.getFullName();
        List<C3StructMemberDeclaration> structMembers = List.of();

        for (String ident : path)
        {
            structMembers = getStructMembers(query + "." + ident, project);
            C3StructMemberDeclaration member = structMembers.size() == 1 ? structMembers.get(0) : null;

            String structPathType = member != null && member.getStructPathType() != null
                ? member.getStructPathType().getFullName()
                : null;
            if (structPathType == null) break;
            query = structPathType;
        }

        return structMembers;
    }

    @NotNull
    public List<C3StructMemberDeclaration> findStructMemberFields(@NotNull String query, @NotNull Project project)
    {
        List<C3StructMemberDeclaration> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String prefix = query + ".";
        String shortPrefix = stripModule(query) + ".";
        for (String key : StubIndex.getInstance().getAllKeys(StructMemberDeclarationIndex.KEY, project))
        {
            if (key.startsWith(prefix) || stripModule(key).startsWith(shortPrefix))
            {
                result.addAll(getStructMembersByExactKey(key, project));
            }
        }
        return result;
    }

    @NotNull
    public List<C3StructMemberDeclaration> getStructMembers(
            @NotNull FullyQualifiedName type,
            @NotNull String name,
            @NotNull Project project)
    {
        return getStructMembers(type.getFullName() + "." + name, project);
    }

    private static int dotCount(@NotNull String value)
    {
        int count = 0;
        for (int i = 0; i < value.length(); i++)
        {
            if (value.charAt(i) == '.') count++;
        }
        return count;
    }

    @NotNull
    private static String stripModule(@NotNull String key)
    {
        int separator = key.lastIndexOf("::");
        return separator >= 0 ? key.substring(separator + 2) : key;
    }
}
