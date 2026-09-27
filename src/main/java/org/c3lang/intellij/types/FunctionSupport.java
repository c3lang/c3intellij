package org.c3lang.intellij.types;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.psi.C3Arg;
import org.c3lang.intellij.psi.C3CallArgList;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CallInvocation;
import org.c3lang.intellij.psi.C3ArgList;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3Expr;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3LambdaDecl;
import org.c3lang.intellij.psi.C3LocalDeclAfterType;
import org.c3lang.intellij.psi.C3LocalDeclarationStmt;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3ParameterList;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3TypeExpr;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.ShortType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Function-type support: {@code fn} type parsing, callable shapes, lambda
 * parameter types from the expected signature, and return types. Pure
 * static helpers; anything unresolvable is {@code null}.
 */
public final class FunctionSupport
{
    private FunctionSupport()
    {
    }
    /**
     * Expected type of an untyped lambda parameter from the surrounding
     * function-pointer type, e.g. {@code int} for {@code i} in
     * {@code apply(x, fn (i) => i * i)} with
     * {@code fn void apply(int[] arr, IntTransform t)} where
     * {@code alias IntTransform = fn int(int)}. Pure PSI walk plus alias
     * resolution; anything unrecognized yields {@code null} (unchecked).
     */
    public static @Nullable String lambdaParamType(@NotNull C3Parameter param)
    {
        if (param.getType() != null) return null;
        C3LambdaDecl lambdaDecl = PsiTreeUtil.getParentOfType(param, C3LambdaDecl.class);
        if (lambdaDecl == null) return null;
        C3ParameterList lambdaParams = lambdaDecl.getFnParameterList() != null
            ? lambdaDecl.getFnParameterList().getParameterList()
            : null;
        if (lambdaParams == null) return null;
        int paramIndex = -1;
        List<C3ParamDecl> lambdaDecls = lambdaParams.getParamDeclList();
        for (int i = 0; i < lambdaDecls.size(); i++)
        {
            if (lambdaDecls.get(i).getParameter() == param)
            {
                paramIndex = i;
                break;
            }
        }
        if (paramIndex < 0) return null;
        PsiElement lambdaExpr = lambdaDecl.getParent();
        if (lambdaExpr == null) return null;
        PsiElement context = lambdaExpr.getParent();
        String expectedFn = null;
        if (context instanceof C3Arg arg)
        {
            expectedFn = callArgFnType(arg, paramIndex);
        }
        else if (context instanceof C3LocalDeclAfterType declarator)
        {
            C3LocalDeclarationStmt stmt = PsiTreeUtil.getParentOfType(declarator, C3LocalDeclarationStmt.class);
            if (stmt != null && stmt.getOptionalType() != null && stmt.getOptionalType().getType() != null)
            {
                expectedFn = stmt.getOptionalType().getType().getText();
            }
        }
        else if (context instanceof C3ConstDeclarationStmt constDecl && constDecl.getType() != null)
        {
            expectedFn = constDecl.getType().getText();
        }
        if (expectedFn == null) return null;
        String fnText = underlyingFnType(expectedFn, param);
        if (fnText == null) return null;
        FnType fnType = parseFnType(fnText);
        if (fnType == null || paramIndex >= fnType.params.size()) return null;
        String typeText = fnType.params.get(paramIndex);
        return typeText.isBlank() ? null : typeText.strip();
    }

    record FnType(@NotNull String returns, @NotNull List<String> params)
    {
    }
    /**
     * Declared type text of the call parameter receiving the lambda's
     * argument (positional by order, named by name), or {@code null}.
     */
    private static @Nullable String callArgFnType(@NotNull C3Arg arg, int lambdaParamIndex)
    {
        C3CallExpr call = PsiTreeUtil.getParentOfType(arg, C3CallExpr.class);
        if (call == null) return null;
        C3CallablePsiElement callee;
        try
        {
            callee = CallChecker.resolveTarget(call);
        }
        catch (Exception e)
        {
            return null;
        }
        if (callee == null)
        {
            return null;
        }
        CallChecker.Signature signature;
        try
        {
            signature = CallChecker.buildSignature(callee);
        }
        catch (Exception e)
        {
            return null;
        }
        List<CallChecker.ParamInfo> params = signature.params;
        String ownerText = callee instanceof C3FuncDef funcDef
            ? InterfaceService.methodOwnerTypeName(funcDef)
            : (callee instanceof C3MacroDefinition macro
                ? InterfaceService.methodOwnerTypeName(macro)
                : null);
        int startIndex = 0;
        if (ownerText != null && !params.isEmpty())
        {
            C3Expr receiver = call.getExpr() instanceof C3CallExpr inner ? inner.getExpr() : call.getExpr();
            boolean staticReceiver = receiver instanceof C3TypeExpr;
            if (!staticReceiver && InterfaceService.firstParameterMatchesOwner(
                signature.paramTypes, signature.parameterList, ownerText))
            {
                startIndex = 1;
            }
        }
        String named = arg.getNamedIdent() != null ? arg.getNamedIdent().getText() : null;
        if (named != null)
        {
            for (int i = startIndex; i < params.size(); i++)
            {
                if (named.equals(params.get(i).name)) return params.get(i).typeText;
            }
            return null;
        }
        List<C3Arg> siblings = callArgList(call);
        int positional = 0;
        for (C3Arg sibling : siblings)
        {
            if (sibling == arg) break;
            if (sibling.getNamedIdent() == null) positional++;
        }
        boolean ownNamed = false;
        for (C3Arg sibling : siblings)
        {
            if (sibling != arg && sibling.getNamedIdent() != null) ownNamed = true;
        }
        // Positional-after-named is rejected by the compiler; bail out.
        if (ownNamed) return null;
        int slot = startIndex + positional;
        if (slot >= params.size())
        {
            for (int i = params.size() - 1; i >= startIndex; i--)
            {
                if (params.get(i).vaarg) return params.get(i).vaargElement;
            }
            return null;
        }
        return params.get(slot).typeText;
    }

    private static @NotNull List<C3Arg> callArgList(@NotNull C3CallExpr call)
    {
        try
        {
            C3CallExprTail tail = call.getCallExprTail();
            C3CallInvocation invocation = tail != null ? tail.getCallInvocation() : null;
            C3CallArgList callArgs = invocation != null ? invocation.getCallArgList() : null;
            C3ArgList args = callArgs != null ? callArgs.getArgList() : null;
            if (args != null) return args.getArgList();
        }
        catch (Exception ignored)
        {
        }
        return List.of();
    }
    private static @Nullable String underlyingFnType(@NotNull String expectedFn, @NotNull C3Parameter param)
    {
        return underlyingFnType(expectedFn, param.getProject(), ModuleName.from(param));
    }

    static @Nullable String underlyingFnTypeForCheck(
            @NotNull String expectedFn, @NotNull C3PsiElement context)
    {
        return underlyingFnType(expectedFn, context.getProject(), ModuleName.from(context));
    }

    private static @Nullable String underlyingFnType(
            @NotNull String expectedFn, @NotNull Project project, @Nullable ModuleName contextModule)
    {
        if (parseFnType(expectedFn) != null) return expectedFn;
        try
        {
            String resolved = TypeCanonicalizer.resolveAlias(expectedFn, project, contextModule, 0);
            if (resolved != null && parseFnType(resolved) != null) return resolved;
        }
        catch (Exception ignored)
        {
        }
        return null;
    }
    /**
     * Parses a function-pointer type (`fn int(int)`, `fn void()`) into its
     * return and parameter type texts. Anything else yields {@code null}.
     * Note: {@link #normalize} must not run before the prefix check, it
     * strips the space in `fn `.
     */
    static @Nullable FnType parseFnType(@NotNull String text)
    {
        String clean = text.strip();
        if (!clean.startsWith("fn ")) return null;
        String rest = clean.substring(3).strip();
        int open = rest.indexOf('(');
        int close = rest.lastIndexOf(')');
        if (open <= 0 || close <= open) return null;
        String returns = TypeChecker.normalize(rest.substring(0, open).strip());
        if (returns.isEmpty()) return null;
        List<String> params = splitTopLevel(rest.substring(open + 1, close), ',');
        List<String> types = new ArrayList<>();
        for (String entry : params)
        {
            String item = entry.strip();
            if (item.isEmpty()) continue;
            // `type name` form degrades to the leading type.
            int space = item.indexOf(' ');
            if (space > 0 && item.substring(0, space).matches("[A-Za-z_][A-Za-z_0-9.:*\\[\\]]*")) item = item.substring(0, space);
            types.add(TypeChecker.normalize(item));
        }
        return new FnType(returns, types);
    }

    private static @NotNull List<String> splitTopLevel(@NotNull String text, char separator)
    {
        List<String> parts = new ArrayList<>();
        int depthRound = 0;
        int depthSquare = 0;
        int depthAngle = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '(') depthRound++;
            else if (c == ')') depthRound--;
            else if (c == '[') depthSquare++;
            else if (c == ']') depthSquare--;
            else if (c == '<') depthAngle++;
            else if (c == '>') depthAngle--;
            else if (c == separator && depthRound == 0 && depthSquare == 0 && depthAngle == 0)
            {
                parts.add(text.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(text.substring(start));
        return parts;
    }
    static @Nullable InferredType returnTypeOf(@NotNull C3CallablePsiElement callable)
    {
        ShortType returnType = callable.getReturnType();
        if (returnType == null || returnType.getValue() == null) return null;
        return TypeChecker.kindOf(returnType.getValue());
    }
}
