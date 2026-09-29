package org.c3lang.intellij.types;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.C3Util;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3ArgList;
import org.c3lang.intellij.psi.C3Attribute;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3CallArgList;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CallInvocation;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3ExprStmt;
import org.c3lang.intellij.psi.C3LambdaDecl;
import org.c3lang.intellij.psi.C3LambdaDeclShortExpr;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3GroupedExpr;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3MacroParams;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3NamedIdent;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3ParameterList;
import org.c3lang.intellij.psi.C3PathAtIdent;
import org.c3lang.intellij.psi.C3PathAtIdentExpr;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3TrailingBlockParam;
import org.c3lang.intellij.psi.C3TypeExpr;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.ParamType;
import org.c3lang.intellij.psi.reference.C3AtMacroReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantic checks for function and method calls: unknown named arguments,
 * named/positional ordering, arity (vaarg- and default-aware) and per-argument types.
 *
 * <p>Rules follow {@code docs/functions.md}: named arguments, defaults, vaargs and splats.
 * Anything unresolvable is skipped silently.
 */
public final class CallChecker
{
    private CallChecker()
    {
    }

    public static void checkCall(@NotNull C3CallExpr call, @NotNull AnnotationHolder holder)
    {
        Project project = call.getProject();
        if (DumbService.isDumb(project)) return;
        C3CallExprTail tail = call.getCallExprTail();
        if (tail == null || tail.getCallInvocation() == null) return;
        if (tail.getGenericParameters() != null) return;

        Callee callee = resolveCallee(call, tail);
        if (callee == null || callee.callable == null) return;

        checkCalleeAttributes(call, callee.callable, holder);

        Signature signature = buildSignature(callee.callable);
        List<ParamInfo> params = signature.params;
        int startIndex = 0;
        if (callee.ownerText != null && !callee.staticReceiver && !params.isEmpty()
            && InterfaceService.firstParameterMatchesOwner(signature.paramTypes, signature.parameterList, callee.ownerText))
        {
            // Dot-form method call: the receiver fills the first (self) parameter.
            startIndex = 1;
        }

        List<ArgInfo> args = buildArgs(tail.getCallInvocation());
        match(call, callee, signature, startIndex, args, holder);
    }

    // ------------------------------------------------------------------
    // Callee resolution
    // ------------------------------------------------------------------

    private static final class Callee
    {
        final @NotNull C3CallablePsiElement callable;
        final @Nullable String ownerText;
        final boolean staticReceiver;

        Callee(@NotNull C3CallablePsiElement callable, @Nullable String ownerText, boolean staticReceiver)
        {
            this.callable = callable;
            this.ownerText = ownerText;
            this.staticReceiver = staticReceiver;
        }
    }

    private static @Nullable Callee resolveCallee(@NotNull C3CallExpr call, @NotNull C3CallExprTail tail)
    {
        C3AccessIdent methodIdent = tail.getAccessIdent();
        if (methodIdent != null)
        {
            // Tail carries both the method and the invocation.
            return selectOverload(call, tail, methodIdent.getReference(), isStaticReceiver(call.getExpr()), false);
        }
        C3Expr calleeExpr = call.getExpr();
        if (calleeExpr instanceof C3PathIdentExpr pathIdentExpr)
        {
            return selectOverload(call, tail, pathIdentExpr.getPathIdent().getReference(), false, true);
        }
        if (calleeExpr instanceof C3PathAtIdentExpr pathAtIdentExpr)
        {
            // `@macro(args)` calls.
            C3CallablePsiElement callable = MacroSupport.resolveAtCallable(pathAtIdentExpr.getPathAtIdent());
            if (callable == null) return null;
            return new Callee(callable, ownerOf(callable), false);
        }
        if (calleeExpr instanceof C3CallExpr inner
            && inner.getCallExprTail() != null
            && inner.getCallExprTail().getAccessIdent() != null
            && inner.getCallExprTail().getCallInvocation() == null)
        {
            // Split form: recv.method(args) or Type.method(args).
            return selectOverload(
                call, tail, inner.getCallExprTail().getAccessIdent().getReference(), isStaticReceiver(inner.getExpr()), false);
        }
        return null;
    }

    /**
     * Callee selection across overloads sharing one name (e.g. a 1-parameter
     * {@code macro alloc($Type)} and a 2-parameter {@code fn alloc(usz, ...)}):
     * declarations from the call's own module shadow imported ones first
     * (verified against c3c: a same-module declaration wins even when its
     * signature fits worse), then the first candidate whose arity fits wins,
     * then argument types break remaining ties. Single candidates and total
     * ties keep the previous first-wins behavior.
     */
    private static @Nullable Callee selectOverload(
            @NotNull C3CallExpr call,
            @NotNull C3CallExprTail tail,
            @NotNull PsiReference reference,
            boolean staticReceiver,
            boolean plainCall)
    {
        List<C3CallablePsiElement> candidates = overloadCandidates(reference);
        if (candidates.isEmpty()) return null;
        // Method dispatch resolves by receiver type, never by caller module.
        if (plainCall) candidates = preferSameModule(call, candidates);
        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1)
        {
            C3CallablePsiElement only = candidates.get(0);
            return new Callee(only, ownerOf(only), staticReceiver);
        }
        C3CallInvocation invocation = tail.getCallInvocation();
        List<ArgInfo> args = invocation != null ? buildArgs(invocation) : List.of();
        List<C3CallablePsiElement> fitting = new ArrayList<>();
        for (C3CallablePsiElement candidate : candidates)
        {
            if (arityFits(candidate, staticReceiver, args)) fitting.add(candidate);
        }
        if (fitting.size() == 1) return picked(call, fitting.get(0), staticReceiver);
        if (fitting.size() > 1)
        {
            // Several candidates fit by arity (e.g. two 2-parameter `alloc`
            // overloads): prefer the first whose argument types all match,
            // mirroring the compiler's overload choice.
            for (C3CallablePsiElement candidate : fitting)
            {
                if (typeMismatches(call, candidate, staticReceiver, args) == 0)
                {
                    return picked(call, candidate, staticReceiver);
                }
            }
            return picked(call, fitting.get(0), staticReceiver);
        }
        C3CallablePsiElement first = candidates.get(0);
        return new Callee(first, ownerOf(first), staticReceiver);
    }

    private static @NotNull Callee picked(
            @NotNull C3CallExpr call, @NotNull C3CallablePsiElement callable, boolean staticReceiver)
    {
        return new Callee(callable, ownerOf(callable), staticReceiver);
    }

    /**
     * Number of per-argument type mismatches for a candidate, using the same
     * positional/named/vaarg slot mapping as the annotating match. Unknown
     * (un-inferrable) arguments never count against a candidate.
     */
    private static int typeMismatches(
            @NotNull C3CallExpr call,
            @NotNull C3CallablePsiElement candidate,
            boolean staticReceiver,
            @NotNull List<ArgInfo> args)
    {
        Project project = call.getProject();
        ModuleName contextModule = ModuleName.from(call);
        Signature signature;
        try
        {
            signature = buildSignature(candidate);
        }
        catch (Exception e)
        {
            return 0;
        }
        List<ParamInfo> params = signature.params;
        String ownerText = ownerOf(candidate);
        int startIndex = 0;
        if (ownerText != null && !staticReceiver && !params.isEmpty()
            && InterfaceService.firstParameterMatchesOwner(signature.paramTypes, signature.parameterList, ownerText))
        {
            startIndex = 1;
        }
        int vaargIndex = -1;
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).vaarg)
            {
                vaargIndex = i;
                break;
            }
        }
        int mismatches = 0;
        int positionalCount = 0;
        for (ArgInfo arg : args)
        {
            if (arg.splat) continue;
            if (arg.named)
            {
                if (arg.name == null) continue;
                int index = findParam(params, startIndex, arg.name);
                if (index < 0) return Integer.MAX_VALUE;
                if (argMismatch(project, contextModule, arg, params.get(index), index == vaargIndex) != null)
                {
                    mismatches++;
                }
                continue;
            }
            ParamInfo slot = null;
            int slotIndex = -1;
            int seen = 0;
            for (int i = startIndex; i < params.size(); i++)
            {
                if (i == vaargIndex) continue;
                if (seen == positionalCount)
                {
                    slot = params.get(i);
                    slotIndex = i;
                    break;
                }
                seen++;
            }
            if (slot == null)
            {
                if (vaargIndex >= 0
                    && argMismatch(project, contextModule, arg, params.get(vaargIndex), true) != null)
                {
                    mismatches++;
                }
            }
            else if (argMismatch(project, contextModule, arg, slot, false) != null)
            {
                mismatches++;
            }
            positionalCount++;
        }
        return mismatches;
    }

    private static @NotNull List<C3CallablePsiElement> overloadCandidates(@NotNull PsiReference reference)
    {
        List<C3CallablePsiElement> result = new ArrayList<>();
        try
        {
            if (reference instanceof org.c3lang.intellij.psi.reference.C3ReferenceBase<?> base)
            {
                for (C3PsiElement element : base.multiResolve())
                {
                    if (element instanceof C3CallablePsiElement callable && !result.contains(callable))
                    {
                        result.add(callable);
                    }
                }
                if (!result.isEmpty()) return result;
            }
            PsiElement resolved = reference.resolve();
            if (resolved instanceof C3CallablePsiElement callable) result.add(callable);
        }
        catch (Exception ignored)
        {
        }
        return result;
    }

    /**
     * Same-module declarations shadow imported ones for plain calls: when at
     * least one candidate lives in the call's own module, the rest are
     * invisible to checking (verified against c3c, which reports against the
     * local definition even when an imported overload would fit better).
     * Without a same-module candidate everything stays visible.
     */
    private static @NotNull List<C3CallablePsiElement> preferSameModule(
            @NotNull C3CallExpr call,
            @NotNull List<C3CallablePsiElement> candidates)
    {
        ModuleName callModule;
        try
        {
            callModule = ModuleName.from(call);
        }
        catch (Exception e)
        {
            return candidates;
        }
        if (callModule == null) return candidates;
        List<C3CallablePsiElement> same = new ArrayList<>();
        for (C3CallablePsiElement candidate : candidates)
        {
            ModuleName candidateModule = null;
            try
            {
                candidateModule = ModuleName.from(candidate);
            }
            catch (Exception ignored)
            {
            }
            if (callModule.equals(candidateModule)) same.add(candidate);
        }
        return same.isEmpty() ? candidates : same;
    }

    /**
     * Whether the call arguments fit the candidate's parameters: every named
     * argument names a real parameter, and the positional count covers the
     * required parameters without overflowing a non-variadic list. Splat
     * forwards ({@code $vasplat}) and trailing blocks are lenient: their
     * exact shape is validated by the full match, not by selection.
     */
    private static boolean arityFits(
            @NotNull C3CallablePsiElement candidate,
            boolean staticReceiver,
            @NotNull List<ArgInfo> args)
    {
        Signature signature;
        try
        {
            signature = buildSignature(candidate);
        }
        catch (Exception e)
        {
            return true;
        }
        List<ParamInfo> params = signature.params;
        String ownerText = ownerOf(candidate);
        int startIndex = 0;
        if (ownerText != null && !staticReceiver && !params.isEmpty()
            && InterfaceService.firstParameterMatchesOwner(signature.paramTypes, signature.parameterList, ownerText))
        {
            startIndex = 1;
        }
        if (startIndex > params.size()) return false;

        java.util.Set<String> paramNames = new java.util.HashSet<>();
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).name != null) paramNames.add(params.get(i).name);
        }
        int positional = 0;
        for (ArgInfo arg : args)
        {
            if (arg.splat) continue;
            if (arg.named)
            {
                if (arg.name != null && !paramNames.contains(arg.name)) return false;
                continue;
            }
            positional++;
        }
        int required = 0;
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).required) required++;
        }
        if (positional < required) return false;
        boolean hasVaarg = false;
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).vaarg) hasVaarg = true;
        }
        if (!hasVaarg && positional > params.size() - startIndex) return false;
        return true;
    }

    private static @Nullable String ownerOf(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef) return InterfaceService.methodOwnerTypeName(funcDef);
        if (callable instanceof C3MacroDefinition macro) return InterfaceService.methodOwnerTypeName(macro);
        return null;
    }

    // ------------------------------------------------------------------
    // Callee attributes: deprecated use, discarded nodiscard results
    // ------------------------------------------------------------------

    private static void checkCalleeAttributes(
            @NotNull C3CallExpr call,
            @NotNull C3CallablePsiElement callable,
            @NotNull AnnotationHolder holder)
    {
        C3Attributes attributes = attributesOf(callable);
        if (attributes == null) return;
        if (AttributeSpecs.hasAttribute(attributes, "deprecated") && !hasAllowDeprecated(call))
        {
            String message = deprecatedMessage(attributes);
            String kind = callable instanceof C3MacroDefinition ? "macro" : "function";
            String name = callable.getName();
            String text = "Call to deprecated " + kind + (name != null ? " '" + name + "'" : "")
                + (message != null && !message.isBlank() ? ": " + message : ".");
            holder.newAnnotation(HighlightSeverity.WARNING, text).range(calleeAnchor(call)).create();
        }
        if (AttributeSpecs.hasAttribute(attributes, "nodiscard") && isDiscarded(call))
        {
            String name = callable.getName();
            holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    "Return value of '" + (name != null ? name : "?") + "' must not be discarded.")
                .range(calleeAnchor(call))
                .create();
        }
    }

    private static @Nullable C3Attributes attributesOf(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef) return funcDef.getAttributes();
        if (callable instanceof C3MacroDefinition macro) return macro.getAttributes();
        return null;
    }

    private static @Nullable String deprecatedMessage(@NotNull C3Attributes attributes)
    {
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
            if (AttributeSpecs.normalizeName(attributeName).equals("deprecated"))
            {
                return AttributeSpecs.firstStringArg(attribute);
            }
        }
        return null;
    }

    private static boolean hasAllowDeprecated(@NotNull C3CallExpr call)
    {
        // The function body is a sibling of C3FuncDef under C3FuncDefinition,
        // not a child: hop through the definition first.
        C3FuncDefinition funcDefinition = PsiTreeUtil.getParentOfType(call, C3FuncDefinition.class);
        if (funcDefinition != null && funcDefinition.getFuncDef() != null)
        {
            return AttributeSpecs.hasAttribute(funcDefinition.getFuncDef().getAttributes(), "allow_deprecated");
        }
        C3MacroDefinition macro = PsiTreeUtil.getParentOfType(call, C3MacroDefinition.class);
        if (macro != null)
        {
            return AttributeSpecs.hasAttribute(macro.getAttributes(), "allow_deprecated");
        }
        return false;
    }

    private static @NotNull PsiElement calleeAnchor(@NotNull C3CallExpr call)
    {
        C3CallExprTail tail = call.getCallExprTail();
        if (tail != null && tail.getAccessIdent() != null) return tail.getAccessIdent();
        C3Expr calleeExpr = call.getExpr();
        if (calleeExpr instanceof C3PathIdentExpr pathIdentExpr) return pathIdentExpr.getPathIdent();
        if (calleeExpr instanceof C3PathAtIdentExpr pathAtIdentExpr) return pathAtIdentExpr.getPathAtIdent();
        return call;
    }

    /**
     * Whether the call result is unused: a bare statement, possibly wrapped
     * in rethrow/force-unwrap postfixes or parentheses.
     */
    private static boolean isDiscarded(@NotNull C3CallExpr call)
    {
        PsiElement current = call;
        while (true)
        {
            PsiElement parent = current.getParent();
            if (parent instanceof C3CallExpr || parent instanceof C3GroupedExpr)
            {
                current = parent;
                continue;
            }
            return parent instanceof C3ExprStmt;
        }
    }

    private static boolean isStaticReceiver(@Nullable C3Expr receiver)
    {
        return receiver instanceof C3TypeExpr;
    }

    /**
     * Callee of a call expression, or {@code null} when it cannot be
     * resolved. Shared by checks that need the declaration (attributes)
     * without repeating resolution logic.
     */
    public static @Nullable C3CallablePsiElement resolveTarget(@NotNull C3CallExpr call)
    {
        C3CallExprTail tail = call.getCallExprTail();
        if (tail == null) return null;
        Callee callee;
        try
        {
            callee = resolveCallee(call, tail);
        }
        catch (Exception e)
        {
            return null;
        }
        return callee != null ? callee.callable : null;
    }

    // ------------------------------------------------------------------
    // Parameters and arguments (callee signatures)
    // ------------------------------------------------------------------

    static final class ParamInfo
    {
        final @Nullable String name;
        final @Nullable String typeText;
        final boolean required;
        final boolean vaarg;
        final @Nullable String vaargElement;

        ParamInfo(@Nullable String name, @Nullable String typeText, boolean required, boolean vaarg, @Nullable String vaargElement)
        {
            this.name = name;
            this.typeText = typeText;
            this.required = required;
            this.vaarg = vaarg;
            this.vaargElement = vaargElement;
        }
    }

    static final class ArgInfo
    {
        final @NotNull C3Arg arg;
        final @Nullable String name;
        final boolean named;
        final boolean splat;
        final @Nullable C3Expr expr;
        final @NotNull PsiElement anchor;

        ArgInfo(@NotNull C3Arg arg, @Nullable String name, boolean named, boolean splat, @Nullable C3Expr expr, @NotNull PsiElement anchor)
        {
            this.arg = arg;
            this.name = name;
            this.named = named;
            this.splat = splat;
            this.expr = expr;
            this.anchor = anchor;
        }
    }

    static final class Signature
    {
        final @NotNull List<ParamInfo> params;
        final @NotNull List<ParamType> paramTypes;
        final @Nullable C3ParameterList parameterList;
        final boolean needsTrailingBlock;

        Signature(
                @NotNull List<ParamInfo> params,
                @NotNull List<ParamType> paramTypes,
                @Nullable C3ParameterList parameterList,
                boolean needsTrailingBlock)
        {
            this.params = params;
            this.paramTypes = paramTypes;
            this.parameterList = parameterList;
            this.needsTrailingBlock = needsTrailingBlock;
        }
    }

    static @NotNull Signature buildSignature(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef)
        {
            C3ParameterList list = funcDef.getFnParameterList().getParameterList();
            return new Signature(paramsFrom(list), funcDef.getParameterTypes(), list, false);
        }
        if (callable instanceof C3MacroDefinition macro && macro.getMacroParams() != null)
        {
            C3MacroParams macroParams = macro.getMacroParams();
            C3ParameterList list = macroParams.getParameterList();
            return new Signature(
                paramsFrom(list),
                macro.getParameterTypes(),
                list,
                macroParams.getTrailingBlockParam() != null);
        }
        return new Signature(List.of(), List.of(), null, false);
    }

    static @NotNull List<ParamInfo> paramsFrom(@Nullable C3ParameterList list)
    {
        List<ParamInfo> result = new ArrayList<>();
        if (list == null) return result;
        for (C3ParamDecl decl : list.getParamDeclList())
        {
            C3Parameter parameter = decl.getParameter();
            if (parameter == null) continue;
            String name = parameter.getNameIdent();
            if (name != null) name = name.replaceAll("^[#$@]+", "");
            boolean vaarg = parameter.getNode().findChildByType(C3Types.ELLIPSIS) != null;
            String typeText = parameter.getType() != null ? parameter.getType().getText() : null;
            // A bare `...` default (`cmp = ...`) leaves the parameter unset:
            // it is optional even though there is no value expression.
            boolean hasDefault = decl.getExpr() != null || hasEllipsisDefault(decl);
            result.add(new ParamInfo(
                name,
                typeText,
                !hasDefault && !vaarg,
                vaarg,
                vaarg ? (typeText != null ? typeText : "any") : null));
        }
        return result;
    }

    /**
     * Whether the parameter declaration carries a bare {@code ...} default
     * (`cmp = ...`): an `ELLIPSIS` leaf directly under the declaration, past
     * the `=`. Variadic parameters (`args...`) hold theirs inside the
     * parameter itself and never reach this check.
     */
    private static boolean hasEllipsisDefault(@NotNull C3ParamDecl decl)
    {
        try
        {
            boolean seenEq = false;
            for (ASTNode child : decl.getNode().getChildren(null))
            {
                if (child.getElementType() == C3Types.EQ) seenEq = true;
                else if (seenEq && child.getElementType() == C3Types.ELLIPSIS) return true;
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    static @NotNull List<ArgInfo> buildArgs(@NotNull C3CallInvocation invocation)
    {
        List<ArgInfo> result = new ArrayList<>();
        C3CallArgList callArgList = invocation.getCallArgList();
        if (callArgList == null) return result;
        C3ArgList argList = callArgList.getArgList();
        if (argList == null) return result;
        for (C3Arg arg : argList.getArgList())
        {
            if (arg.getParamPath() != null)
            {
                result.add(new ArgInfo(arg, null, true, false, arg.getExpr(), arg));
                continue;
            }
            C3NamedIdent namedIdent = arg.getNamedIdent();
            if (namedIdent != null)
            {
                result.add(new ArgInfo(arg, namedIdent.getText(), true, false, arg.getExpr(), namedIdent));
                continue;
            }
            if (arg.getText().strip().startsWith("..."))
            {
                result.add(new ArgInfo(arg, null, false, true, arg.getExpr(), arg));
                continue;
            }
            C3Expr expr = arg.getExpr();
            if (expr != null && isVaargForward(expr))
            {
                // `$vasplat` / `$vaarg[...]` forward the caller's variadics:
                // they fill the callee's `...`, not a positional slot.
                result.add(new ArgInfo(arg, null, false, true, expr, arg));
                continue;
            }
            result.add(new ArgInfo(arg, null, false, false, expr, expr != null ? expr : (PsiElement) arg));
        }
        return result;
    }

    /**
     * A variadic forward (`$vasplat`, `$vaarg[...]`, `$vaexpr...`): passes the
     * caller's variadics into the callee's `...`, not into a positional slot.
     */
    private static boolean isVaargForward(@NotNull C3Expr expr)
    {
        String text = expr.getText();
        if (text == null) return false;
        String clean = text.strip();
        if (clean.equals("$vasplat") || clean.startsWith("$vasplat ")
            || clean.startsWith("$vaarg") || clean.startsWith("$vaexpr")) return true;
        // `$vaarg[i]` parses as an indexed access: check the base.
        C3Expr base = expr;
        while (base instanceof C3CallExpr call && call.getCallExprTail() == null) break;
        if (base instanceof C3CallExpr call)
        {
            C3Expr callee = call.getExpr();
            if (callee != null)
            {
                String calleeText = callee.getText();
                if (calleeText != null && calleeText.strip().startsWith("$vaarg")) return true;
            }
        }
        return clean.endsWith("...") && clean.contains("$");
    }

    // ------------------------------------------------------------------
    // Matching
    // ------------------------------------------------------------------

    private static void match(
            @NotNull C3CallExpr call,
            @NotNull Callee callee,
            @NotNull Signature signature,
            int startIndex,
            @NotNull List<ArgInfo> args,
            @NotNull AnnotationHolder holder)
    {
        Project project = call.getProject();
        ModuleName contextModule = ModuleName.from(call);
        List<ParamInfo> params = signature.params;

        C3CallExprTail callTail = call.getCallExprTail();
        boolean hasTrailingBlock = callTail != null && callTail.getCompoundStatement() != null;
        if (signature.needsTrailingBlock && !hasTrailingBlock && callTail != null && callTail.getCallInvocation() != null)
        {
            error(holder, callTail.getCallInvocation(), "Missing trailing block.");
        }
        if (!signature.needsTrailingBlock && hasTrailingBlock)
        {
            String calleeName = callee.callable.getName();
            error(holder, callTail.getCompoundStatement(),
                "'" + (calleeName != null ? calleeName : callee.callable.getText()) + "' takes no trailing block.");
        }

        int vaargIndex = -1;
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).vaarg)
            {
                vaargIndex = i;
                break;
            }
        }

        boolean[] filled = new boolean[params.size()];
        boolean namedSeen = false;
        boolean splatSeen = false;
        int positionalCount = 0;

        for (ArgInfo arg : args)
        {
            if (arg.splat)
            {
                splatSeen = true;
                continue;
            }
            if (arg.named)
            {
                namedSeen = true;
                if (arg.name == null) continue;
                int index = findParam(params, startIndex, arg.name);
                if (index < 0)
                {
                    error(holder, arg.anchor, "Unknown parameter '" + arg.name + "'." + suggestion(params, startIndex, arg.name));
                    continue;
                }
                if (filled[index])
                {
                    error(holder, arg.anchor, "Parameter '" + arg.name + "' is already set.");
                    continue;
                }
                filled[index] = true;
                checkArgType(project, contextModule, holder, arg, params.get(index), index == vaargIndex);
                continue;
            }
            if (namedSeen)
            {
                error(holder, arg.anchor, "Unnamed arguments may not follow named arguments.");
                continue;
            }
            ParamInfo slot = null;
            int slotIndex = -1;
            int seen = 0;
            for (int i = startIndex; i < params.size(); i++)
            {
                if (i == vaargIndex) continue;
                if (seen == positionalCount)
                {
                    slot = params.get(i);
                    slotIndex = i;
                    break;
                }
                seen++;
            }
            if (slot == null)
            {
                if (vaargIndex >= 0)
                {
                    checkArgType(project, contextModule, holder, arg, params.get(vaargIndex), true);
                }
                else if (!splatSeen)
                {
                    error(holder, arg.anchor, "Too many arguments.");
                }
            }
            else
            {
                filled[slotIndex] = true;
                checkArgType(project, contextModule, holder, arg, slot, false);
            }
            positionalCount++;
        }

        if (!splatSeen)
        {
            C3CallInvocation invocation = call.getCallExprTail().getCallInvocation();
            for (int i = startIndex; i < params.size(); i++)
            {
                ParamInfo param = params.get(i);
                if (i != vaargIndex && param.required && !filled[i] && param.name != null)
                {
                    error(holder, invocation, "Missing argument for parameter '" + param.name + "'.");
                }
            }
        }
    }

    private static int findParam(@NotNull List<ParamInfo> params, int startIndex, @NotNull String name)
    {
        String stripped = InterfaceService.stripParamSigil(name);
        for (int i = startIndex; i < params.size(); i++)
        {
            String candidate = params.get(i).name;
            if (candidate == null) continue;
            if (name.equals(candidate) || stripped.equals(InterfaceService.stripParamSigil(candidate))) return i;
        }
        return -1;
    }

    private static @NotNull String suggestion(@NotNull List<ParamInfo> params, int startIndex, @NotNull String name)
    {
        List<String> candidates = new ArrayList<>();
        for (int i = startIndex; i < params.size(); i++)
        {
            if (params.get(i).name != null) candidates.add(params.get(i).name);
        }
        if (candidates.isEmpty()) return "";
        String best = C3Util.INSTANCE.findBestMatch(name, candidates);
        if (best == null || best.equals(name)) return "";
        return " Did you mean '" + best + "'?";
    }

    private static void checkArgType(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull AnnotationHolder holder,
            @NotNull ArgInfo arg,
            @NotNull ParamInfo param,
            boolean vaargElement)
    {
        if (arg.expr instanceof C3LambdaDeclShortExpr lambda)
        {
            checkLambdaBody(project, contextModule, holder, lambda, param, vaargElement);
        }
        String error = argMismatch(project, contextModule, arg, param, vaargElement);
        if (error != null && arg.expr != null) holder.newAnnotation(HighlightSeverity.ERROR, error).range(arg.expr).create();
    }

    /**
     * Return-type check for a short lambda argument (`fn (i) => i * i`)
     * against the expected function-pointer type, e.g. `IntTransform` =
     * {@code fn int(int)}. The body infers through the expected parameter
     * types; block bodies and unknown expectations stay silent.
     */
    private static void checkLambdaBody(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull AnnotationHolder holder,
            @NotNull C3LambdaDeclShortExpr lambda,
            @NotNull ParamInfo param,
            boolean vaargElement)
    {
        String typeText = vaargElement ? param.vaargElement : param.typeText;
        if (typeText == null) return;
        C3LambdaDecl lambdaDecl = lambda.getLambdaDecl();
        C3Expr body = lambda.getExpr();
        if (lambdaDecl == null || body == null) return;
        String fnText = FunctionSupport.underlyingFnTypeForCheck(typeText.strip(), lambda);
        FunctionSupport.FnType expected = fnText != null ? FunctionSupport.parseFnType(fnText) : null;
        if (expected == null) return;
        String required = "'" + typeText.strip() + "'"
            + (typeText.strip().equals(fnText) ? "" : " (" + fnText + ")");
        int lambdaParams = lambdaDecl.getFnParameterList() != null
            && lambdaDecl.getFnParameterList().getParameterList() != null
            ? lambdaDecl.getFnParameterList().getParameterList().getParamDeclList().size()
            : 0;
        if (lambdaParams != expected.params().size())
        {
            error(holder, body, "The lambda doesn't match the required type " + required + ".");
            return;
        }
        InferredType bodyType;
        try
        {
            bodyType = TypeChecker.infer(body);
        }
        catch (Exception e)
        {
            return;
        }
        if (bodyType == null) return;
        TypeChecker.CastDiagnostic diagnostic;
        try
        {
            diagnostic = TypeChecker.checkCast(project, contextModule, expected.returns(), bodyType, body);
        }
        catch (Exception e)
        {
            return;
        }
        if (diagnostic != null && !diagnostic.warning)
        {
            error(holder, body, diagnostic.message);
        }
    }

    /**
     * Per-argument type mismatch, or {@code null} when the argument fits.
     * Shared by the annotating check and by overload selection's dry run.
     */
    private static @Nullable String argMismatch(
            @NotNull Project project,
            @Nullable ModuleName contextModule,
            @NotNull ArgInfo arg,
            @NotNull ParamInfo param,
            boolean vaargElement)
    {
        if (arg.expr == null) return null;
        String typeText = vaargElement ? param.vaargElement : param.typeText;
        if (typeText == null) return null;
        InferredType inferred = TypeChecker.infer(arg.expr);
        if (inferred == null) return null;
        if (MacroSupport.isGenericTypeParam(arg.expr, inferred)) return null;
        inferred = TypeChecker.narrowedSource(arg.expr, inferred);
        return TypeChecker.argumentError(
            project,
            contextModule,
            param.name != null ? param.name : typeText,
            typeText,
            inferred);
    }

    private static void error(@NotNull AnnotationHolder holder, @NotNull PsiElement anchor, @NotNull String message)
    {
        holder.newAnnotation(HighlightSeverity.ERROR, message).range(anchor).create();
    }
}
