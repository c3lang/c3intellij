package org.c3lang.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Canonical registry of C3 built-in attributes: where each attribute may be
 * attached and whether it takes arguments. Single source of truth for
 * completion and for placement validation.
 *
 * <p>Target sets follow the "Used for" sections of the language documentation.
 * Attributes usable on "any declaration" ({@code local}, {@code private},
 * ...) skip placement validation: flagging them would risk false positives
 * on declaration kinds the plugin does not model yet. {@code feat} and
 * {@code if} are contextual (module sections, generic declarations) and are
 * consumed by {@link ConditionalGating} instead.
 *
 * <p>Contracts ({@code require}, {@code ensure}, {@code param},
 * {@code return}) are parsed as attributes and are part of the registry,
 * otherwise every function with contracts would be flagged as unknown.
 */
public final class AttributeSpecs
{
    public enum Target
    {
        FUNCTION,
        METHOD,
        MACRO,
        STRUCT,
        UNION,
        ENUM,
        BITSTRUCT,
        TYPEDEF,
        CONSTDEF,
        ALIAS,
        CONST,
        GLOBAL,
        VAR,
        PARAM,
        MEMBER,
        FAULT,
        MODULE_SECTION,
        INTERFACE_METHOD,
        IMPORT
    }

    public record Spec(
            @NotNull Set<Target> targets,
            boolean takesArgs,
            boolean requiresArgs)
    {
    }

    private static final Map<String, Spec> SPECS = new LinkedHashMap<>();

    private static final Set<Target> USER_TYPES = EnumSet.of(
        Target.STRUCT, Target.UNION, Target.ENUM, Target.BITSTRUCT, Target.TYPEDEF, Target.ALIAS);
    private static final Set<Target> LINKABLE = EnumSet.of(
        Target.FUNCTION, Target.GLOBAL, Target.CONST, Target.ENUM,
        Target.UNION, Target.STRUCT, Target.FAULT);

    static
    {
        // Keep alphabetical. Targets mirror the specification's "Attributes" chapter.
        addArgs("align", Target.STRUCT, Target.UNION, Target.BITSTRUCT, Target.VAR, Target.GLOBAL, Target.FUNCTION, Target.METHOD, Target.MEMBER);
        add("allow_deprecated", false, Target.FUNCTION, Target.METHOD);
        add("benchmark", false, Target.FUNCTION);
        add("bigendian", false, Target.BITSTRUCT);
        add("builtin", false, Target.FUNCTION, Target.MACRO, Target.GLOBAL, Target.CONST, Target.FAULT, Target.ALIAS);
        addArgs("callconv", Target.FUNCTION, Target.METHOD);
        addSet("cname", true, LINKABLE);
        add("compact", false, Target.STRUCT, Target.UNION);
        add("const", false, Target.MACRO);
        add("constinit", false, Target.CONSTDEF, Target.TYPEDEF);
        addSet("deprecated", true, withUserTypes(Target.FUNCTION, Target.METHOD, Target.MACRO, Target.GLOBAL, Target.CONST, Target.MEMBER, Target.FAULT));
        add("dynamic", false, Target.METHOD);
        addArgs("ensure", Target.FUNCTION, Target.METHOD, Target.MACRO);
        addSet("export", true, LINKABLE);
        addSet("extern", true, LINKABLE);
        add("finalizer", true, Target.FUNCTION);
        addArgs("format", Target.FUNCTION, Target.METHOD, Target.MACRO);
        add("init", true, Target.FUNCTION);
        add("inline", false, Target.FUNCTION, Target.METHOD);
        addArgs("link", Target.MODULE_SECTION, Target.FUNCTION, Target.MACRO, Target.GLOBAL, Target.CONST);
        add("littleendian", false, Target.BITSTRUCT);
        add("maydiscard", false, Target.FUNCTION, Target.METHOD, Target.MACRO);
        addSet("mustinit", false, USER_TYPES);
        add("naked", false, Target.FUNCTION);
        add("noalias", false, Target.PARAM);
        add("nodiscard", false, Target.FUNCTION, Target.METHOD, Target.MACRO);
        add("noinit", false, Target.GLOBAL, Target.VAR);
        add("noinline", false, Target.FUNCTION, Target.METHOD);
        add("nopadding", false, Target.STRUCT, Target.UNION);
        add("norecurse", false, Target.IMPORT);
        add("noredzone", false, Target.FUNCTION);
        add("noreturn", false, Target.FUNCTION, Target.METHOD, Target.MACRO);
        add("nosanitize", true, Target.FUNCTION);
        add("nostackprobe", false, Target.FUNCTION);
        add("nostackprotector", false, Target.FUNCTION);
        add("obfuscate", false);
        addArgs("operator", Target.METHOD);
        addArgs("operator_r", Target.METHOD);
        addArgs("operator_s", Target.METHOD);
        add("optional", false, Target.INTERFACE_METHOD);
        add("overlap", false, Target.BITSTRUCT, Target.STRUCT);
        add("packed", false, Target.STRUCT, Target.UNION, Target.BITSTRUCT);
        addArgs("param", Target.FUNCTION, Target.METHOD, Target.MACRO);
        add("pure", false, Target.FUNCTION, Target.METHOD);
        add("reflect", false);
        addArgs("require", Target.FUNCTION, Target.METHOD, Target.MACRO);
        addArgs("return", Target.FUNCTION, Target.METHOD, Target.MACRO);
        add("safemacro", false, Target.MACRO);
        add("safeinfer", false, Target.VAR);
        addArgs("section", Target.FUNCTION, Target.CONST, Target.GLOBAL);
        add("simd", false, Target.TYPEDEF, Target.ALIAS);
        addArgs("stackprobe", Target.FUNCTION);
        addArgs("stackprotector", Target.FUNCTION);
        addArgs("stackprotection", Target.FUNCTION);
        add("structlike", false, Target.TYPEDEF, Target.ALIAS);
        addSet("tag", true, withUserTypes(Target.FUNCTION, Target.METHOD, Target.MACRO, Target.PARAM, Target.MEMBER, Target.GLOBAL, Target.VAR));
        add("test", false, Target.MODULE_SECTION, Target.FUNCTION);
        addArgs("wasm", Target.FUNCTION, Target.GLOBAL, Target.CONST);
        add("weak", false, Target.FUNCTION, Target.CONST, Target.GLOBAL);
        add("weaklink", false, Target.FUNCTION, Target.CONST, Target.GLOBAL);
        add("winmain", false, Target.FUNCTION);
        // Placement is validated elsewhere or is contextual:
        // - feat/if feed ConditionalGating (module sections, generic members).
        // - local/private/public/nostrip/unused/used accept any declaration.
        add("feat", true);
        add("if", true);
        add("local", false);
        add("nostrip", false);
        add("private", false);
        add("public", false);
        add("unused", false);
        add("used", false);
    }

    private AttributeSpecs()
    {
    }

    private static void add(@NotNull String name, boolean takesArgs, @NotNull Target... targets)
    {
        SPECS.put(name, new Spec(Set.of(targets), takesArgs, false));
    }

    private static void addArgs(@NotNull String name, @NotNull Target... targets)
    {
        SPECS.put(name, new Spec(Set.of(targets), true, true));
    }

    private static void addSet(@NotNull String name, boolean takesArgs, @NotNull Set<Target> targets)
    {
        SPECS.put(name, new Spec(EnumSet.copyOf(targets), takesArgs, false));
    }

    private static @NotNull EnumSet<Target> withUserTypes(@NotNull Target... more)
    {
        EnumSet<Target> all = EnumSet.copyOf(USER_TYPES);
        all.addAll(Set.of(more));
        return all;
    }

    public static @Nullable Spec specOf(@NotNull String name)
    {
        return SPECS.get(normalizeName(name));
    }

    public static boolean isBuiltin(@NotNull String name)
    {
        return SPECS.containsKey(normalizeName(name));
    }

    public static @NotNull Map<String, Spec> all()
    {
        return Map.copyOf(SPECS);
    }

    /**
     * Attribute name without the {@code @} prefix and any path qualification.
     */
    public static @NotNull String normalizeName(@NotNull String rawName)
    {
        String clean = rawName.strip();
        if (clean.startsWith("@")) clean = clean.substring(1);
        int separator = clean.lastIndexOf("::");
        if (separator >= 0) clean = clean.substring(separator + 2);
        return clean;
    }

    /**
     * Whether an attribute list carries the given built-in attribute.
     */
    public static boolean hasAttribute(@Nullable C3Attributes attributes, @NotNull String name)
    {
        if (attributes == null) return false;
        String wanted = normalizeName(name);
        for (C3Attribute attribute : attributes.getAttributeList())
        {
            String attributeName;
            try
            {
                attributeName = attribute.getAttributeName().getText();
            }
            catch (Exception e)
            {
                continue;
            }
            if (normalizeName(attributeName).equals(wanted)) return true;
        }
        return false;
    }

    /**
     * First double-quoted string argument of an attribute, e.g. the message
     * in {@code @deprecated("use bar instead")}, or {@code null}.
     */
    public static @Nullable String firstStringArg(@NotNull C3Attribute attribute)
    {
        C3AttributeParamList params;
        try
        {
            params = attribute.getAttributeParamList();
        }
        catch (Exception e)
        {
            return null;
        }
        if (params == null) return null;
        java.util.regex.Matcher matcher = QUOTED_STRING.matcher(params.getText());
        return matcher.find() ? matcher.group(1) : null;
    }

    private static final java.util.regex.Pattern QUOTED_STRING =
        java.util.regex.Pattern.compile("\"([^\"]*)\"");

    /**
     * Declaration kind owning an attribute list, or {@code null} when the
     * owner is not modeled (imports of unknown shape, asm blocks, attribute
     * definitions themselves): placement is not validated then.
     */
    public static @Nullable Target classifyOwner(@NotNull PsiElement owner)
    {
        if (owner instanceof C3FuncDef)
        {
            if (PsiTreeUtil.getParentOfType(owner, C3InterfaceBody.class) != null) return Target.INTERFACE_METHOD;
            return isMethod(owner) ? Target.METHOD : Target.FUNCTION;
        }
        if (owner instanceof C3MacroDefinition)
        {
            return isMethod(owner) ? Target.METHOD : Target.MACRO;
        }
        if (owner instanceof C3StructDeclaration structDecl)
        {
            return structDecl.getNode().findChildByType(C3Types.KW_UNION) != null ? Target.UNION : Target.STRUCT;
        }
        if (owner instanceof C3EnumDeclaration) return Target.ENUM;
        if (owner instanceof C3BitstructDeclaration) return Target.BITSTRUCT;
        if (owner instanceof C3TypedefDecl) return Target.TYPEDEF;
        if (owner instanceof C3ConstdefDeclaration) return Target.CONSTDEF;
        if (owner instanceof C3AliasTypeDecl || owner instanceof C3AliasDecl) return Target.ALIAS;
        if (owner instanceof C3ConstDeclarationStmt) return Target.CONST;
        if (owner instanceof C3GlobalDecl) return Target.GLOBAL;
        if (owner instanceof C3VarDecl
            || owner instanceof C3LocalDeclAfterType
            || owner instanceof C3LocalDeclarationStmt) return Target.VAR;
        if (owner instanceof C3Parameter || owner instanceof C3ParamDecl) return Target.PARAM;
        if (owner instanceof C3StructMemberDeclaration
            || owner instanceof C3EnumConstant
            || owner instanceof C3ConstdefConstant) return Target.MEMBER;
        if (owner instanceof C3FaultDefinition) return Target.FAULT;
        if (owner instanceof C3Module) return Target.MODULE_SECTION;
        if (owner instanceof C3ImportDecl) return Target.IMPORT;
        if (owner instanceof C3LambdaDeclExpr || owner instanceof C3LambdaDeclShortExpr) return Target.FUNCTION;
        return null;
    }

    private static boolean isMethod(@NotNull PsiElement owner)
    {
        if (owner instanceof C3FuncDef funcDef)
        {
            C3FuncName funcName = funcDef.getFuncHeader().getFuncName();
            return funcName != null && funcName.getType() != null;
        }
        if (owner instanceof C3MacroDefinition macro)
        {
            C3MacroName macroName = macro.getMacroHeader().getMacroName();
            return macroName != null && macroName.getType() != null;
        }
        return false;
    }

    /**
     * Whether the attribute may be attached to the target. Methods additionally
     * accept function-level attributes; empty specs (any-declaration and
     * contextual attributes) accept everything.
     */
    public static boolean allows(@NotNull Spec spec, @NotNull Target target)
    {
        if (spec.targets().isEmpty()) return true;
        if (spec.targets().contains(target)) return true;
        return target == Target.METHOD && spec.targets().contains(Target.FUNCTION);
    }

    /**
     * Owner-aware variant: a macro method ({@code Type.@macro}) is still a
     * macro, so it additionally accepts macro-level attributes
     * ({@code @const} on a macro method is legal, on an fn method it is
     * not — both verified against {@code c3c}).
     */
    public static boolean allows(@NotNull Spec spec, @NotNull Target target, @NotNull PsiElement owner)
    {
        if (allows(spec, target)) return true;
        return target == Target.METHOD
            && owner instanceof C3MacroDefinition
            && spec.targets().contains(Target.MACRO);
    }

    public static @NotNull String displayName(@NotNull Target target)
    {
        return switch (target)
        {
            case FUNCTION -> "function";
            case METHOD -> "method";
            case MACRO -> "macro";
            case STRUCT -> "struct";
            case UNION -> "union";
            case ENUM -> "enum";
            case BITSTRUCT -> "bitstruct";
            case TYPEDEF -> "typedef";
            case CONSTDEF -> "constdef";
            case ALIAS -> "alias";
            case CONST -> "const";
            case GLOBAL -> "global";
            case VAR -> "variable";
            case PARAM -> "parameter";
            case MEMBER -> "member";
            case FAULT -> "fault";
            case MODULE_SECTION -> "module section";
            case INTERFACE_METHOD -> "interface method";
            case IMPORT -> "import";
        };
    }
}
