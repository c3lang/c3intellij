package org.c3lang.intellij.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import org.c3lang.intellij.index.AttributeIndex;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.TypeIndex;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3Attribute;
import org.c3lang.intellij.psi.C3AttributeParamList;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3GlobalDecl;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3ParameterList;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ShortType;
import org.c3lang.intellij.types.TypeChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Validation of built-in attributes: unknown names, placement on the wrong
 * declaration kind, and argument checks for the attributes with a fixed
 * contract ({@code callconv}, {@code init}/{@code finalizer},
 * {@code test}/{@code benchmark}, {@code winmain}).
 */
public final class AttributeChecks
{
    private static final Set<String> CALLCONVS = Set.of("veccall", "cdecl", "stdcall");

    private AttributeChecks()
    {
    }

    public static void checkAttribute(@NotNull C3Attribute attribute, @NotNull AnnotationHolder holder)
    {
        Project project = attribute.getProject();
        String rawName;
        try
        {
            rawName = attribute.getAttributeName().getText();
        }
        catch (Exception e)
        {
            return;
        }
        String name = AttributeSpecs.normalizeName(rawName);
        AttributeSpecs.Spec spec = AttributeSpecs.specOf(name);
        if (spec == null)
        {
            if (!isUserAttribute(name, project))
            {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unknown attribute '@" + name + "'.")
                    .range(nameAnchor(attribute))
                    .create();
            }
            return;
        }
        if (name.equals("feat") || name.equals("if")) return;
        if (spec.requiresArgs() && attribute.getAttributeParamList() == null)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@" + name + "' requires an argument.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        PsiElement owner = declarationOf(attribute);
        if (owner == null) return;
        AttributeSpecs.Target target = AttributeSpecs.classifyOwner(owner);
        if (target == null) return;
        if (!AttributeSpecs.allows(spec, target, owner))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@" + name + "' cannot be used on " + AttributeSpecs.displayName(target) + ".")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        switch (name)
        {
            case "align" -> checkAlign(attribute, holder);
            case "callconv" -> checkCallconv(attribute, holder);
            case "init", "finalizer" -> checkStartupShutdown(owner, target, name, holder, attribute);
            case "test", "benchmark" -> checkPlainVoidFunction(owner, target, name, holder, attribute);
            case "format" -> checkFormat(owner, attribute, holder);
            case "noalias" -> checkNoalias(owner, attribute, holder);
            case "noinit" -> checkNoinit(owner, attribute, holder);
            case "nosanitize" -> checkSanitizer(attribute, holder);
            case "stackprobe" -> checkStackValue(attribute, holder, "none", "call", "inline");
            case "stackprotector", "stackprotection" -> checkStackValue(attribute, holder, "none", "basic", "strong", "all");
            case "simd" -> checkSimd(owner, attribute, holder);
            case "dynamic" -> checkDynamic(owner, attribute, holder);
            case "winmain" -> checkWinMain(owner, target, holder, attribute);
            default -> {}
        }
    }    private static @NotNull PsiElement nameAnchor(@NotNull C3Attribute attribute)
    {
        try
        {
            return attribute.getAttributeName();
        }
        catch (Exception e)
        {
            return attribute;
        }
    }

    private static @Nullable PsiElement declarationOf(@NotNull C3Attribute attribute)
    {
        PsiElement parent = attribute.getParent();
        if (parent instanceof C3Attributes) return parent.getParent();
        return parent;
    }

    /**
     * User-defined attributes from {@code attrdef} declarations. Unknown in
     * dumb mode, so never flag there.
     */
    static boolean isUserAttribute(@NotNull String name, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return true;
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(AttributeIndex.KEY, project))
            {
                String clean = key.replace("@", "");
                if (clean.equals(name) || clean.endsWith("::" + name)) return true;
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    private static void checkCallconv(@NotNull C3Attribute attribute, @NotNull AnnotationHolder holder)
    {
        String convention = AttributeSpecs.firstStringArg(attribute);
        if (convention == null || !CALLCONVS.contains(convention.toLowerCase(Locale.ROOT)))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "@callconv must be one of \"veccall\", \"cdecl\", \"stdcall\".")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    /**
     * {@code test}/{@code benchmark} functions take no arguments and return
     * nothing (or an Optional result for tests).
     */
    private static void checkPlainVoidFunction(
            @NotNull PsiElement owner,
            @NotNull AttributeSpecs.Target target,
            @NotNull String name,
            @NotNull AnnotationHolder holder,
            @NotNull C3Attribute attribute)
    {
        if (!(owner instanceof C3FuncDef funcDef) || target != AttributeSpecs.Target.FUNCTION)
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@" + name + "' requires a plain function without parameters returning void.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        boolean hasParams = funcDef.getFnParameterList().getParameterList() != null
            && !funcDef.getFnParameterList().getParameterList().getParamDeclList().isEmpty();
        ShortType returnType = funcDef.getReturnType();
        String returnText = returnType != null ? returnType.getValue() : null;
        boolean returnsNothing = returnText != null
            && (TypeChecker.isVoidType(returnText)
                || ((name.equals("test") || name.equals("benchmark")) && TypeChecker.isVoidOptionalType(returnText)));
        if (hasParams || !returnsNothing)
        {
            // Anchored on the attribute: the function name sits outside it.
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@" + name + "' requires a plain function without parameters returning void.")
                .range(nameAnchor(attribute))
                .create();
        }
    }
    private static void checkWinMain(
            @NotNull PsiElement owner,
            @NotNull AttributeSpecs.Target target,
            @NotNull AnnotationHolder holder,
            @NotNull C3Attribute attribute)
    {
        String funcName = owner instanceof C3FuncDef funcDef ? funcDef.getNameIdent() : null;
        if (target != AttributeSpecs.Target.FUNCTION || !"main".equals(funcName))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@winmain' is only valid for the 'main' function.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    /**
     * {@code @align(n)} raises alignment to at least {@code n}, a power of two.
     */
    private static void checkAlign(@NotNull C3Attribute attribute, @NotNull AnnotationHolder holder)
    {
        Long value = firstIntegerArg(attribute);
        if (value == null || value <= 0 || (value & (value - 1)) != 0)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@align' requires a power-of-two argument.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    /**
     * {@code @init}/{@code @finalizer} take no arguments, return nothing, and
     * accept an optional priority in 1..65535.
     */
    private static void checkStartupShutdown(
            @NotNull PsiElement owner,
            @NotNull AttributeSpecs.Target target,
            @NotNull String name,
            @NotNull AnnotationHolder holder,
            @NotNull C3Attribute attribute)
    {
        if (!(owner instanceof C3FuncDef funcDef) || target != AttributeSpecs.Target.FUNCTION)
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@" + name + "' requires a plain function without parameters returning void.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        boolean hasParams = funcDef.getFnParameterList().getParameterList() != null
            && !funcDef.getFnParameterList().getParameterList().getParamDeclList().isEmpty();
        ShortType returnType = funcDef.getReturnType();
        boolean returnsVoid = returnType != null && TypeChecker.isVoidType(returnType.getValue());
        if (hasParams || !returnsVoid)
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@" + name + "' requires a plain function without parameters returning void.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        Long priority = firstIntegerArg(attribute);
        if (priority != null && (priority < 1 || priority > 65535))
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@" + name + "' priority must be between 1 and 65535.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    /**
     * {@code @format(index)}: the function must take a {@code String} format
     * parameter with an {@code args...} variadic directly after it.
     */
    private static void checkFormat(
            @NotNull PsiElement owner,
            @NotNull C3Attribute attribute,
            @NotNull AnnotationHolder holder)
    {
        Long index = firstIntegerArg(attribute);
        if (index == null || index < 0)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@format' requires a zero-based format string index.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        List<C3ParamDecl> params = functionParams(owner);
        if (params == null || index >= params.size())
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@format' index is out of range for the parameter list.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        C3Parameter formatParam = params.get(index.intValue()).getParameter();
        if (formatParam == null || formatParam.getType() == null
            || !TypeChecker.normalize(formatParam.getType().getText()).equals("String"))
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@format' parameter must be of type 'String'.")
                .range(nameAnchor(attribute))
                .create();
            return;
        }
        if (params.size() <= index + 1 || !isArgsVariadic(params.get(index.intValue() + 1)))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@format' requires an 'args...' variadic parameter directly after the format string.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    private static @Nullable List<C3ParamDecl> functionParams(@NotNull PsiElement owner)
    {
        C3ParameterList parameterList = null;
        if (owner instanceof C3FuncDef funcDef) parameterList = funcDef.getFnParameterList().getParameterList();
        else if (owner instanceof C3MacroDefinition macro) parameterList = macro.getMacroParams().getParameterList();
        return parameterList != null ? parameterList.getParamDeclList() : List.of();
    }

    private static boolean isArgsVariadic(@NotNull C3ParamDecl paramDecl)
    {
        C3Parameter parameter = paramDecl.getParameter();
        if (parameter == null) return false;
        String text = parameter.getText();
        if (text != null && text.contains("...")) return true;
        try
        {
            return parameter.getNode().findChildByType(C3Types.ELLIPSIS) != null;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * {@code @noalias} is a {@code restrict}-like promise on a pointer parameter.
     */
    private static void checkNoalias(
            @NotNull PsiElement owner,
            @NotNull C3Attribute attribute,
            @NotNull AnnotationHolder holder)
    {
        if (!(owner instanceof C3ParamDecl paramDecl) && !(owner instanceof C3Parameter))
        {
            return;
        }
        C3Parameter parameter = owner instanceof C3ParamDecl decl ? decl.getParameter() : (C3Parameter) owner;
        if (parameter == null || parameter.getType() == null) return;
        String typeText = TypeChecker.normalize(parameter.getType().getText());
        if (!typeText.endsWith("*") && !typeText.endsWith("[]"))
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@noalias' requires a pointer parameter.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    private static void checkSanitizer(@NotNull C3Attribute attribute, @NotNull AnnotationHolder holder)
    {
        if (AttributeSpecs.firstStringArg(attribute) == null)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@nosanitize' requires a check name, e.g. \"address\".")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    /**
     * {@code @noinit} is refused on {@code @mustinit} types: the opt-out is
     * revoked by the type. Type lookup is best-effort and index-free; when
     * the declared type cannot be inspected, nothing is reported.
     */
    private static void checkNoinit(
            @NotNull PsiElement owner,
            @NotNull C3Attribute attribute,
            @NotNull AnnotationHolder holder)
    {
        String declared = declaredTypeOf(owner);
        if (declared == null) return;
        Project project = owner.getProject();
        if (DumbService.isDumb(project)) return;
        String clean = TypeChecker.normalize(declared);
        if (hasMustinit(TypeChecker.shortName(clean), project))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@noinit' cannot be used on type '" + clean + "' marked '@mustinit'.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    private static @Nullable String declaredTypeOf(@NotNull PsiElement owner)
    {
        try
        {
            if (owner instanceof C3GlobalDecl globalDecl && globalDecl.getOptionalType() != null)
            {
                return globalDecl.getOptionalType().getText();
            }
            if (owner instanceof C3LocalDeclarationStmt localDecl && localDecl.getOptionalType() != null)
            {
                return localDecl.getOptionalType().getText();
            }
            if (owner instanceof C3LocalDeclAfterType afterType)
            {
                C3LocalDeclarationStmt stmt =
                    com.intellij.psi.util.PsiTreeUtil.getParentOfType(afterType, C3LocalDeclarationStmt.class);
                if (stmt != null && stmt.getOptionalType() != null) return stmt.getOptionalType().getText();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    private static boolean hasMustinit(@NotNull String shortName, @NotNull Project project)
    {
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(shortName) && !key.endsWith("::" + shortName)) continue;
                for (C3PsiElement element : StubIndex.getElements(
                        TypeIndex.KEY,
                        key,
                        project,
                        org.c3lang.intellij.project.C3ProjectService.getInstance(project).getSearchScope(),
                        C3PsiElement.class))
                {
                    if (!(element instanceof C3TypeName typeName)) continue;
                    if (!typeName.getText().strip().equals(shortName)) continue;
                    PsiElement parent = typeName.getParent();
                    C3Attributes attributes = null;
                    if (parent instanceof C3StructDeclaration structDecl) attributes = structDecl.getAttributes();
                    else if (parent instanceof C3EnumDeclaration enumDecl) attributes = enumDecl.getAttributes();
                    else if (parent instanceof C3BitstructDeclaration bitDecl) attributes = bitDecl.getAttributes();
                    else if (parent instanceof C3TypedefDecl typedefDecl)
                    {
                        for (C3Attributes listAttributes : typedefDecl.getAttributesList())
                        {
                            if (AttributeSpecs.hasAttribute(listAttributes, "mustinit")) return true;
                        }
                        continue;
                    }
                    else if (parent instanceof C3AliasTypeDecl aliasDecl) attributes = aliasDecl.getAttributes();
                    if (attributes != null && AttributeSpecs.hasAttribute(attributes, "mustinit")) return true;
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    private static void checkStackValue(
            @NotNull C3Attribute attribute, @NotNull AnnotationHolder holder, @NotNull String... allowed)
    {
        String value = AttributeSpecs.firstStringArg(attribute);
        if (value == null) return;
        for (String option : allowed)
        {
            if (option.equalsIgnoreCase(value)) return;
        }
        holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Invalid value '\"" + value + "\"', expected one of " + String.join(", ", allowed) + ".")
            .range(nameAnchor(attribute))
            .create();
    }

    /**
     * {@code @simd} vectors must have a power-of-two length.
     */
    private static void checkSimd(
            @NotNull PsiElement owner,
            @NotNull C3Attribute attribute,
            @NotNull AnnotationHolder holder)
    {
        String text = ownerText(owner);
        TypeChecker.VectorInfo vector = text != null ? TypeChecker.parseVector(text) : null;
        if (vector == null || vector.size < 0 || (vector.size & (vector.size - 1)) != 0)
        {
            holder.newAnnotation(HighlightSeverity.ERROR, "'@simd' requires a vector type with power-of-two length.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    private static @Nullable String ownerText(@NotNull PsiElement owner)
    {
        if (owner instanceof C3TypedefDecl typedefDecl && typedefDecl.getTypedefType() != null)
        {
            return typedefDecl.getTypedefType().getText();
        }
        if (owner instanceof C3AliasTypeDecl aliasDecl && aliasDecl.getTypedefType() != null)
        {
            return aliasDecl.getTypedefType().getText();
        }
        return null;
    }

    /**
     * {@code @dynamic} only participates in dispatch on concrete user-defined
     * types, never on {@code any} or on an interface itself.
     */
    private static void checkDynamic(
            @NotNull PsiElement owner,
            @NotNull C3Attribute attribute,
            @NotNull AnnotationHolder holder)
    {
        String ownerText = null;
        if (owner instanceof C3FuncDef funcDef) ownerText = InterfaceService.methodOwnerTypeName(funcDef);
        else if (owner instanceof C3MacroDefinition macro) ownerText = InterfaceService.methodOwnerTypeName(macro);
        if (ownerText == null) return;
        String clean = ownerText.strip();
        if (clean.equals("any") || isInterfaceOwner(clean, owner.getProject()))
        {
            holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'@dynamic' cannot be used on methods of 'any' or an interface.")
                .range(nameAnchor(attribute))
                .create();
        }
    }

    private static boolean isInterfaceOwner(@NotNull String ownerText, @NotNull Project project)
    {
        try
        {
            return !InterfaceService.INSTANCE.findInterfaceDefinitions(
                FullyQualifiedName.parse(ownerText), project).isEmpty();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static @Nullable Long firstIntegerArg(@NotNull C3Attribute attribute)
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
        java.util.regex.Matcher matcher = INTEGER_ARG.matcher(params.getText());
        if (!matcher.find()) return null;
        try
        {
            return Long.parseLong(matcher.group(1));
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static final java.util.regex.Pattern INTEGER_ARG =
        java.util.regex.Pattern.compile("(?<![A-Za-z_0-9])(\\d+)(?![A-Za-z_0-9])");

    /**
     * Deprecation message from {@code @deprecated} or
     * {@code @deprecated("message")}.
     */
    public static @Nullable String deprecatedMessage(@NotNull C3Attribute attribute)
    {
        return AttributeSpecs.firstStringArg(attribute);
    }

    /**
     * Attributes trailing a call ({@code foo() @inline}): only
     * {@code inline}, {@code noinline} and {@code pure} are known there.
     * Only tails with an invocation carry attributes; a bare
     * {@code foo.@bar} tail holds the method name instead. The attributes
     * are direct children of the invocation, so nested calls are unaffected.
     */
    public static void checkCallAttributes(@NotNull C3CallExpr call, @NotNull AnnotationHolder holder)
    {
        C3CallExprTail tail = call.getCallExprTail();
        if (tail == null || tail.getCallInvocation() == null) return;
        for (com.intellij.lang.ASTNode child : tail.getCallInvocation().getNode().getChildren(null))
        {
            if (child.getElementType() != C3Types.AT_IDENT) continue;
            String name = child.getText();
            if (name == null) continue;
            String clean = name.strip();
            if (clean.equals("@inline") || clean.equals("@noinline") || clean.equals("@pure")) continue;
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown call attribute '" + clean + "'.")
                .range(child.getPsi())
                .create();
        }
    }
}
