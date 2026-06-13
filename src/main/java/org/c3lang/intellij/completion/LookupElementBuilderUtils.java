package org.c3lang.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.ParamType;
import org.c3lang.intellij.psi.ShortType;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

public final class LookupElementBuilderUtils
{
    public static final LookupElementBuilderUtils INSTANCE = new LookupElementBuilderUtils();

    private LookupElementBuilderUtils()
    {
    }

    public static @NotNull LookupElementBuilder createFunctionDef(@NotNull C3CallablePsiElement element)
    {
        Icon icon = null;
        if (element instanceof C3FuncDef)
        {
            icon = C3Icons.Nodes.FUNCTION;
        }
        else if (element instanceof C3MacroDefinition)
        {
            icon = C3Icons.Nodes.MACRO;
        }

        List<String> parameters = new ArrayList<>();
        for (ParamType parameterType : element.getParameterTypes())
        {
            List<String> parts = new ArrayList<>();
            ShortType type = parameterType.getType();
            if (type != null) parts.add(type.getFullName());
            parts.add(parameterType.getName());
            parameters.add(String.join(" ", parts));
        }
        String parameterList = String.join(",", parameters);

        return LookupElementBuilder.create(element, element.getFqName().getFullName())
            .withLookupStrings(List.of(
                element.getFqName().getName(),
                element.getFqName().getFullName()
            ))
            .withIcon(icon)
            .appendTailText("(" + parameterList + ")", false)
            .withTypeText(element.getReturnType() != null ? element.getReturnType().getFullName() : "");
    }
}
