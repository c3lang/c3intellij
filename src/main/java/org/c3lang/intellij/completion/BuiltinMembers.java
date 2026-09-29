package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.types.TypeChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in dot-completions that need no declaration lookup: fields of
 * {@code any} ({@code .ptr}, {@code .type}), slice fields ({@code .ptr},
 * {@code .len}), vector swizzling, scalar methods verified against the
 * compiler ({@code popcount}, {@code floor}, {@code is_digit}, ...), and
 * type properties on type names ({@code int.sizeof}, {@code Foo.members},
 * ...) plus {@code typeid} values ({@code arg.type.inner}).
 *
 * <p>Every entry here was probed against {@code c3c}: values never expose
 * type properties ({@code i.sizeof} is an error), methods that do not exist
 * ({@code byteswap}, {@code is_nan}, {@code abs} on floats) are excluded.
 */
public final class BuiltinMembers
{
    public record Item(
        @NotNull String name,
        @Nullable String tailText,
        @NotNull String typeText,
        boolean isMethod)
    {
    }

    private BuiltinMembers()
    {
    }

    /**
     * Completions for a value receiver of known type text, e.g. {@code "any"},
     * {@code "int[]"}, {@code "int"} or {@code "typeid"}.
     */
    public static @NotNull List<Item> forValueType(@NotNull String receiverType)
    {
        String clean = TypeChecker.normalize(receiverType);
        if (TypeChecker.isOptionalName(clean)) return List.of();
        String shortName = TypeChecker.shortName(clean);

        if (shortName.equals("any"))
        {
            return List.of(
                field("ptr", "void*"),
                field("type", "typeid"));
        }
        if (shortName.equals("typeid"))
        {
            return List.of(
                field("inner", "typeid"),
                field("kindof", "TypeKind"),
                field("sizeof", "usz"),
                field("len", "usz"),
                field("names", "String[]"),
                field("parentof", "typeid"),
                field("typeid", "typeid"));
        }
        if (TypeChecker.isSliceType(clean) || shortName.equals("String"))
        {
            String element = shortName.equals("String") ? "char" : TypeChecker.arrayElementType(clean);
            if (element == null || element.isEmpty()) return List.of();
            return List.of(
                field("ptr", element + "*"),
                field("len", "usz"));
        }
        TypeChecker.VectorInfo vector = TypeChecker.parseVector(clean);
        if (vector != null)
        {
            return swizzleFields(vector);
        }
        if (shortName.equals("char") || shortName.equals("ichar"))
        {
            return charMethods(shortName);
        }
        if (TypeChecker.isIntegerType(clean))
        {
            return intMethods(shortName);
        }
        if (TypeChecker.isFloatType(clean))
        {
            return floatMethods(shortName);
        }
        return List.of();
    }

    /**
     * Completions for a type-name receiver, e.g. {@code int} in
     * {@code int.sizeof} or {@code Foo} in {@code Foo.members}.
     */
    public static @NotNull List<Item> forTypeName(@NotNull String receiverText, @NotNull Project project)
    {
        String clean = TypeChecker.normalize(receiverText).strip();
        if (!clean.matches("[A-Za-z_][A-Za-z_0-9.:]*(\\[<[^\\]]*\\]|\\[[^\\]]*\\]|\\*)?")) return List.of();
        String shortName = TypeChecker.shortName(clean);
        // Values, not types.
        if (shortName.equals("self") || shortName.equals("this")
            || shortName.equals("null") || shortName.equals("true") || shortName.equals("false")) return List.of();

        List<Item> result = new ArrayList<>();
        result.add(field("sizeof", "usz"));
        result.add(field("alignof", "usz"));
        result.add(field("typeid", "typeid"));
        result.add(field("kindof", "TypeKind"));
        result.add(field("qnameof", "String"));
        result.add(field("nameof", "String"));
        result.add(field("parentof", "typeid"));
        result.add(method("is_eq", "(other)", "bool"));
        result.add(method("is_ordered", "()", "bool"));

        if (TypeChecker.isIntegerType(clean) || TypeChecker.isFloatType(clean)
            || shortName.equals("char") || shortName.equals("ichar"))
        {
            result.add(field("min", shortName));
            result.add(field("max", shortName));
            return result;
        }
        if (TypeChecker.parseVector(clean) != null || TypeChecker.parseArray(clean) != null)
        {
            result.add(field("len", "usz"));
            result.add(field("inner", "typeid"));
            return result;
        }
        if (TypeChecker.isSliceType(clean) || clean.endsWith("*"))
        {
            result.add(field("inner", "typeid"));
            return result;
        }
        if (!DumbService.isDumb(project))
        {
            try
            {
                for (C3TypeName typeName : InterfaceService.INSTANCE.findTypeDeclarations(
                    new FullyQualifiedName(null, shortName), project))
                {
                    if (!shortName.equals(typeName.getText().strip())) continue;
                    PsiElement parent = typeName.getParent();
                    if (parent instanceof C3StructDeclaration)
                    {
                        result.add(field("members", "ident[]"));
                        result.add(field("membersof", "type[]"));
                        result.add(field("names", "String[]"));
                        result.add(field("inner", "typeid"));
                        return result;
                    }
                    if (parent instanceof C3EnumDeclaration)
                    {
                        result.add(field("names", "String[]"));
                        result.add(field("values", "usz[]"));
                        result.add(field("elements", "usz"));
                        result.add(field("len", "usz"));
                        return result;
                    }
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return result;
    }

    /**
     * Type after one member step while walking a receiver chain
     * ({@code arg.type.inner}): {@code any} + {@code type} gives
     * {@code typeid}, a slice + {@code ptr} gives the element pointer, etc.
     * Index segments ({@code arr[i]}) are handled by the caller drilling to
     * the element type. Returns {@code null} when the step is unknown.
     */
    public static @Nullable String stepType(@NotNull String receiverType, @NotNull String member)
    {
        String clean = TypeChecker.normalize(receiverType);
        if (TypeChecker.isOptionalName(clean)) return null;
        String shortName = TypeChecker.shortName(clean);
        if (shortName.equals("any"))
        {
            if (member.equals("ptr")) return "void*";
            if (member.equals("type")) return "typeid";
            return null;
        }
        if (shortName.equals("typeid"))
        {
            return switch (member)
            {
                case "inner", "parentof" -> "typeid";
                case "sizeof", "alignof", "len", "elements", "min", "max" -> "usz";
                case "nameof", "qnameof" -> "String";
                case "kindof" -> "TypeKind";
                case "typeid" -> "typeid";
                default -> null;
            };
        }
        if (TypeChecker.isSliceType(clean) || shortName.equals("String"))
        {
            String element = shortName.equals("String") ? "char" : TypeChecker.arrayElementType(clean);
            if (element == null || element.isEmpty()) return null;
            if (member.equals("ptr")) return element + "*";
            if (member.equals("len")) return "usz";
            return null;
        }
        TypeChecker.VectorInfo vector = TypeChecker.parseVector(clean);
        if (vector != null)
        {
            if (isSwizzle(member, vector.size)) return vector.element;
            return null;
        }
        return null;
    }

    /**
     * Element type for an index step ({@code arr[i]}, {@code vec[0]}), or
     * {@code null} when the receiver is not indexable.
     */
    public static @Nullable String indexElementType(@NotNull String receiverType)
    {
        String clean = TypeChecker.normalize(receiverType);
        if (TypeChecker.isSliceType(clean)) return TypeChecker.arrayElementType(clean);
        TypeChecker.VectorInfo array = TypeChecker.parseArray(clean);
        if (array != null) return array.element;
        TypeChecker.VectorInfo vector = TypeChecker.parseVector(clean);
        if (vector != null) return vector.element;
        return null;
    }

    /**
     * Adds items to the result set with builtin priority and, for methods, a
     * paren-insert handler matching the other tail contributors.
     */
    public static void addTo(@NotNull CompletionResultSet result, @NotNull List<Item> items)
    {
        for (Item item : items)
        {
            LookupElementBuilder builder = LookupElementBuilder.create(item.name())
                .withPresentableText(item.name())
                .withIcon(item.isMethod() ? C3Icons.Nodes.FUNCTION : C3Icons.Nodes.STRUCT_FIELD)
                .withTypeText(item.typeText());
            if (item.isMethod())
            {
                builder = builder.appendTailText(item.tailText() != null ? item.tailText() : "()", false);
                boolean noParams = item.tailText() == null || item.tailText().equals("()");
                builder = builder.withInsertHandler((insertionContext, ignored) -> {
                    int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
                    CharSequence chars = insertionContext.getDocument().getCharsSequence();
                    if (caretOffset < chars.length() && chars.charAt(caretOffset) == '(')
                    {
                        insertionContext.getEditor().getCaretModel().moveToOffset(caretOffset + 1);
                    }
                    else
                    {
                        insertionContext.getDocument().insertString(caretOffset, "()");
                        insertionContext.getEditor().getCaretModel()
                            .moveToOffset(noParams ? caretOffset + 2 : caretOffset + 1);
                    }
                });
            }
            result.addElement(PrioritizedLookupElement.withPriority(builder, 5.0));
        }
    }

    private static @NotNull Item field(@NotNull String name, @NotNull String type)
    {
        return new Item(name, null, type, false);
    }

    private static @NotNull Item method(@NotNull String name, @NotNull String params, @NotNull String returns)
    {
        return new Item(name, params, returns, true);
    }

    private static @NotNull List<Item> intMethods(@NotNull String self)
    {
        return List.of(
            method("popcount", "()", "uint"),
            method("clz", "()", "uint"),
            method("ctz", "()", "uint"),
            method("rotl", "(shift)", self),
            method("rotr", "(shift)", self),
            method("overflow_add", "(other)", self + "?"),
            method("overflow_sub", "(other)", self + "?"),
            method("overflow_mul", "(other)", self + "?"));
    }

    private static @NotNull List<Item> floatMethods(@NotNull String self)
    {
        return List.of(
            method("floor", "()", self),
            method("ceil", "()", self),
            method("round", "()", self),
            method("trunc", "()", self),
            method("rint", "()", self),
            method("fma", "(other, t)", self),
            method("pow", "(exp)", self),
            method("copysign", "(sign)", self));
    }

    private static @NotNull List<Item> charMethods(@NotNull String self)
    {
        return List.of(
            method("is_digit", "()", "bool"),
            method("is_alpha", "()", "bool"),
            method("is_alnum", "()", "bool"),
            method("is_lower", "()", "bool"),
            method("is_upper", "()", "bool"),
            method("is_space", "()", "bool"),
            method("is_blank", "()", "bool"),
            method("is_cntrl", "()", "bool"),
            method("is_graph", "()", "bool"),
            method("is_print", "()", "bool"),
            method("is_punct", "()", "bool"),
            method("is_xdigit", "()", "bool"),
            method("to_lower", "()", self),
            method("to_upper", "()", self));
    }

    private static @NotNull List<Item> swizzleFields(@NotNull TypeChecker.VectorInfo vector)
    {
        List<Item> result = new ArrayList<>();
        result.add(field("x", vector.element));
        result.add(field("y", vector.element));
        result.add(field("r", vector.element));
        result.add(field("g", vector.element));
        // Unknown (symbolic/unbounded) size: offer everything, the compiler
        // sorts out invalid lanes.
        if (vector.size < 0 || vector.size >= 3)
        {
            result.add(field("z", vector.element));
            result.add(field("b", vector.element));
        }
        if (vector.size < 0 || vector.size >= 4)
        {
            result.add(field("w", vector.element));
            result.add(field("a", vector.element));
        }
        return result;
    }

    private static boolean isSwizzle(@NotNull String member, long size)
    {
        return switch (member)
        {
            case "x", "y", "r", "g" -> true;
            case "z", "b" -> size < 0 || size >= 3;
            case "w", "a" -> size < 0 || size >= 4;
            default -> false;
        };
    }
}
