package org.c3lang.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StructField
{
    private final String name;
    private final FullyQualifiedName type;

    public StructField(@NotNull String name, @NotNull FullyQualifiedName type)
    {
        this.name = name;
        this.type = type;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public FullyQualifiedName getType()
    {
        return type;
    }

    public static @NotNull List<StructField> collectFields(@NotNull C3StructBody body, @Nullable String parentName)
    {
        C3ModuleDefinition module = PsiTreeUtil.getParentOfType(body, C3ModuleDefinition.class);
        List<StructField> result = new ArrayList<>();

        for (C3StructMemberDeclaration declaration : body.getStructMemberDeclarationList())
        {
            String fieldName = getFieldName(declaration);
            String memberName = joinDot(parentName, fieldName);
            C3StructBody structBody = declaration.getStructBody();

            if (structBody != null)
            {
                List<StructField> fields = new ArrayList<>();
                if (memberName != null)
                {
                    FullyQualifiedName structType = declaration.getStructType();
                    if (structType == null) return List.of();
                    fields.add(new StructField(
                        memberName,
                        new FullyQualifiedName(structType.getModule(), structType.getName() + "." + memberName)
                    ));
                }

                result.addAll(collectFields(structBody, memberName));
                result.addAll(fields);
            }
            else
            {
                C3Type type = declaration.getType();
                if (type == null) return List.of();

                // Stub creation must never touch references or stub indices
                // (the file being indexed may itself be mapped in the index),
                // so resolve purely syntactically. Downstream lookups match by
                // suffix, so module imprecision here is harmless.
                FullyQualifiedName typeFqn = syntacticTypeName(module, type);

                if (memberName != null)
                {
                    result.add(new StructField(memberName, typeFqn));
                }
            }
        }

        return result;
    }

    public static @Nullable String getFieldName(@NotNull C3StructMemberDeclaration declaration)
    {
        C3IdentifierList identifierList = declaration.getIdentifierList();
        if (identifierList != null) return identifierList.getText();

        ASTNode[] children = declaration.getNode().getChildren(TokenSet.create(C3Types.IDENT));
        return children.length > 0 ? children[0].getText() : null;
    }

    public static @NotNull FullyQualifiedName syntacticTypeName(
            @Nullable C3ModuleDefinition module,
            @NotNull C3Type type)
    {
        // The full text keeps array/slice/pointer suffixes: a field
        // `int[] a` is `int[]`, not `int`.
        String fullText = type.getText().strip();
        C3BaseType baseType = type.getBaseType();
        if (baseType != null && baseType.getPath() == null)
        {
            ModuleName moduleName = module != null ? module.getModuleName() : null;
            return new FullyQualifiedName(moduleName, fullText);
        }
        return new FullyQualifiedName(null, fullText);
    }

    private static @Nullable String joinDot(@Nullable String parentName, @Nullable String fieldName)
    {
        if (parentName == null || parentName.isEmpty()) return nullize(fieldName);
        if (fieldName == null || fieldName.isEmpty()) return parentName;
        return parentName + "." + fieldName;
    }

    private static @Nullable String nullize(@Nullable String value)
    {
        return value == null || value.isEmpty() ? null : value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof StructField that)) return false;
        return name.equals(that.name) && type.equals(that.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, type);
    }

    @Override
    public String toString()
    {
        return "StructField(name=" + name + ", type=" + type + ")";
    }

}
