package org.c3lang.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
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
        C3ModuleDefinition module = body.getModuleDefinition();
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

                List<FullyQualifiedName> resolved = module.resolve(type);
                FullyQualifiedName typeFqn = resolved.size() == 1
                    ? resolved.get(0)
                    : new FullyQualifiedName(null, type.getText());

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
