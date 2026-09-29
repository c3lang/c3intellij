package org.c3lang.intellij.types;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3StructBody;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3StructMemberDeclaration;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Struct-type support: naming, substruct relations and struct layout.
 * Pure static helpers; anything unresolvable is {@code null} or {@code false}.
 */
public final class StructSupport
{
    private StructSupport()
    {
    }
    static boolean isStructName(@NotNull String typeName, @NotNull Project project)
    {
        PsiElement parent = TypeCanonicalizer.findTypeParent(typeName, project);
        return parent instanceof C3StructDeclaration || parent instanceof C3BitstructDeclaration;
    }
    /**
     * Struct behind a pointer source for interface conversion, e.g.
     * {@code File} for {@code File*}. Only pointers convert implicitly: a
     * struct value would need an explicit address-of (an rvalue would
     * otherwise dangle behind the interface reference).
     */
    static @Nullable String interfaceSourceStruct(@NotNull InferredType source)
    {
        if (source.getKind() != InferredType.Kind.POINTER) return null;
        String pointee = TypeChecker.normalize(source.getName());
        while (pointee.endsWith("*")) pointee = pointee.substring(0, pointee.length() - 1).strip();
        if (!TypeCanonicalizer.isUserTypeName(pointee)) return null;
        return pointee;
    }

    static boolean isSubstructOf(
            @NotNull String childName, @NotNull String parentName, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return false;
        try
        {
            FullyQualifiedName child = FullyQualifiedName.parse(childName);
            List<C3StructDeclaration> declarations =
                org.c3lang.intellij.index.InterfaceService.INSTANCE.findStructDeclarations(child, project);
            String wanted = TypeChecker.shortName(parentName);
            for (C3StructDeclaration declaration : declarations)
            {
                C3StructBody body = declaration.getStructBody();
                if (body == null) continue;
                for (C3StructMemberDeclaration member : body.getStructMemberDeclarationList())
                {
                    // An inline substruct member is written as a bare type (`inline Foo;`).
                    if (member.getIdentifierList() != null) continue;
                    if (member.getStructBody() != null || member.getBitstructBody() != null) continue;
                    C3Type memberType = member.getType();
                    if (memberType == null) continue;
                    if (TypeChecker.shortName(TypeChecker.normalize(memberType.getText())).equals(wanted)) return true;
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    static boolean arraySubstructCast(
            @NotNull String resolvedTarget, @NotNull String resolvedSource, @NotNull Project project)
    {
        String targetElement = TypeChecker.arrayElementType(resolvedTarget);
        String sourceElement = TypeChecker.arrayElementType(resolvedSource);
        if (targetElement == null || sourceElement == null) return false;
        if (TypeChecker.namesEqual(targetElement, sourceElement)) return false;
        return isStructName(targetElement, project) && isStructName(sourceElement, project)
            && (isSubstructOf(sourceElement, targetElement, project)
                || isSubstructOf(targetElement, sourceElement, project));
    }
    static @Nullable TypeChecker.Layout structLayout(
            @NotNull String typeText,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            @NotNull Set<String> visiting)
    {
        FullyQualifiedName name = FullyQualifiedName.parse(typeText);
        List<C3StructDeclaration> declarations;
        try
        {
            declarations =
                org.c3lang.intellij.index.InterfaceService.INSTANCE.findStructDeclarations(name, project);
        }
        catch (Exception e)
        {
            return null;
        }
        C3StructDeclaration declaration = preferModule(declarations, contextModule);
        if (declaration == null || declaration.getStructBody() == null) return null;
        if (hasAnyAttribute(declaration, "compact", "overlap", "structlike")) return null;
        String key = declaration.getTypeName().getText().strip() + "@"
            + (ModuleName.from(declaration) != null ? ModuleName.from(declaration).getValue() : "");
        if (!visiting.add(key)) return null;
        try
        {
            return membersLayout(declaration.getStructBody(), isUnion(declaration), project, contextModule, depth, visiting);
        }
        finally
        {
            visiting.remove(key);
        }
    }

    private static @Nullable TypeChecker.Layout membersLayout(
            @NotNull C3StructBody body,
            boolean union,
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            int depth,
            @NotNull Set<String> visiting)
    {
        boolean packed = false;
        try
        {
            PsiElement owner = body.getParent();
            if (owner instanceof C3StructDeclaration structDecl && structDecl.getAttributes() != null)
            {
                packed = AttributeSpecs.hasAttribute(structDecl.getAttributes(), "packed");
            }
        }
        catch (Exception ignored)
        {
        }
        long offset = 0;
        long maxAlign = 1;
        long maxSize = 0;
        for (C3StructMemberDeclaration member : body.getStructMemberDeclarationList())
        {
            TypeChecker.Layout memberLayout;
            try
            {
                if (member.getStructBody() != null)
                {
                    // Anonymous nested struct/union: expanded inline.
                    memberLayout = membersLayout(member.getStructBody(), isUnion(member),
                        project, contextModule, depth + 1, visiting);
                }
                else if (member.getBitstructBody() != null)
                {
                    return null;
                }
                else
                {
                    FullyQualifiedName memberType = member.getStructPathType();
                    if (memberType == null) return null;
                    memberLayout = TypeChecker.layoutOf(memberType.getFullName(), project, contextModule, depth + 1, visiting);
                }
            }
            catch (Exception e)
            {
                return null;
            }
            if (memberLayout == null) return null;
            if (union)
            {
                maxSize = Math.max(maxSize, memberLayout.size());
                maxAlign = Math.max(maxAlign, packed ? 1 : memberLayout.align());
            }
            else
            {
                long align = packed ? 1 : memberLayout.align();
                offset = alignUp(offset, align);
                offset += memberLayout.size();
                maxAlign = Math.max(maxAlign, align);
            }
        }
        if (union) return new TypeChecker.Layout(maxSize, maxAlign);
        return new TypeChecker.Layout(alignUp(offset, packed ? 1 : maxAlign), packed ? 1 : maxAlign);
    }

    private static long alignUp(long offset, long align)
    {
        if (align <= 1) return offset;
        return (offset + align - 1) / align * align;
    }

    private static boolean isUnion(@NotNull PsiElement element)
    {
        try
        {
            return element.getNode() != null && element.getNode().findChildByType(C3Types.KW_UNION) != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean hasAnyAttribute(@NotNull C3StructDeclaration declaration, @NotNull String... names)
    {
        try
        {
            if (declaration.getAttributes() == null) return false;
            for (String name : names)
            {
                if (AttributeSpecs.hasAttribute(declaration.getAttributes(), name)) return true;
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    private static @Nullable C3StructDeclaration preferModule(
            @NotNull List<C3StructDeclaration> declarations,
            @Nullable ModuleName contextModule)
    {
        if (declarations.isEmpty()) return null;
        if (contextModule != null)
        {
            for (C3StructDeclaration declaration : declarations)
            {
                if (contextModule.equals(ModuleName.from(declaration))) return declaration;
            }
        }
        return declarations.get(0);
    }
}
