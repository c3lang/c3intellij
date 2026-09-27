package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.completion.CompletionExtensionsKt;
import org.c3lang.intellij.index.InterfaceService;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.c3lang.intellij.types.InferredType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class C3PathIdentMixinImpl extends C3PsiNamedElementImpl implements C3PathIdent
{
	public C3PathIdentMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @Nullable String getName()
	{
		return getNameIdent();
	}

	@Override
	public @Nullable PsiElement setName(@NotNull String name)
	{
		LeafPsiElement ident = getNameIdentElement();
		if (ident != null) ident.replaceWithText(name);
		return this;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier()
	{
		return getNameIdentElement();
	}

	@Override
	public int getTextOffset()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getTextOffset() : super.getTextOffset();
	}

	@Override
	public @Nullable String getNameIdent()
	{
		LeafPsiElement ident = getNameIdentElement();
		return ident != null ? ident.getText() : null;
	}

	@Override
	public @Nullable LeafPsiElement getNameIdentElement()
	{
		PsiElement last = getLastChild();
		return last instanceof LeafPsiElement ? (LeafPsiElement) last : null;
	}

	@Override
	public @Nullable FullyQualifiedName findTypeName()
	{
		CatchBinding binding = null;
		try
		{
			binding = findCatchBinding();
		}
		catch (Exception ignored)
		{
		}
		if (binding != null)
		{
			// `catch` bindings are always `fault`; `try` bindings carry the
			// unwrapped tested type (unknown when it cannot be inferred).
			if (binding.isCatch()) return new FullyQualifiedName(null, "fault");
			return tryBindingType(binding);
		}

		List<C3LocalDeclAfterType> decls = findLocalDeclAfterType();
		if (decls.size() == 1)
		{
			FullyQualifiedName fqn = decls.getFirst().findTypeName();
			if (fqn != null) return fqn;
		}

		String myName = getNameIdent();
		if (myName == null) return null;

		FullyQualifiedName foreachType = findForeachVarType(myName);
		if (foreachType != null) return foreachType;

		C3FuncDef funcDef = null;
		C3FuncDefinition funcDefinition = PsiTreeUtil.getParentOfType(this, C3FuncDefinition.class);
		if (funcDefinition != null)
		{
			funcDef = funcDefinition.getFuncDef();
		}
		else
		{
			funcDef = PsiTreeUtil.getParentOfType(this, C3FuncDef.class);
		}

		C3MacroDefinition macroDefinition = PsiTreeUtil.getParentOfType(this, C3MacroDefinition.class);

		// Lambdas do not capture: outer function/macro parameters and
		// `this`/`self` are invisible inside (only the lambda's own
		// parameters resolve, via C3ParameterReference).
		boolean inLambda = enclosingLambda() != null;

		if (funcDef != null && !inLambda)
		{
			if ("this".equals(myName) || "self".equals(myName))
			{
				C3Type type = funcDef.getFuncHeader().getFuncName().getType();
				if (type != null)
				{
					FullyQualifiedName fqn = resolveBaseTypeFqn(type, funcDef);
					if (fqn != null) return fqn;
				}
				ShortType methodType = funcDef.getType();
				if (methodType != null)
				{
					return new FullyQualifiedName(funcDef.getModuleName(), methodType.getValue());
				}
			}

			C3ParameterList parameterList = funcDef.getFnParameterList().getParameterList();
			if (parameterList != null)
			{
				for (C3ParamDecl paramDecl : parameterList.getParamDeclList())
				{
					C3Parameter param = paramDecl.getParameter();
					boolean matches = myName.equals(param.getNameIdent()) || myName.equals(param.getName());
					if (!matches)
					{
						for (ASTNode node : param.getNode().getChildren(null))
						{
							if (node.getElementType() == C3Types.IDENT && myName.equals(node.getText()))
							{
								matches = true;
								break;
							}
						}
					}
					if (matches)
					{
						C3Type type = param.getType();
						if (type != null)
						{
							return resolveBaseTypeFqn(type, funcDef);
						}
					}
				}
			}
		}

		if (macroDefinition != null && !inLambda && macroDefinition.getMacroParams().getParameterList() != null)
		{
			List<C3ParamDecl> macroParamDecls =
				macroDefinition.getMacroParams().getParameterList().getParamDeclList();
			for (int index = 0; index < macroParamDecls.size(); index++)
			{
				C3ParamDecl paramDecl = macroParamDecls.get(index);
				C3Parameter macroParam = paramDecl.getParameter();
				if (macroParam == null) continue;
				if (myName.equals(macroParam.getNameIdent()) || myName.equals(macroParam.getName()))
				{
					C3Type type = macroParam.getType();
					if (type != null) return macroParamTypeFqn(type, macroDefinition);
					// A typeless first parameter is the implicit receiver
					// (`&self`, `self`, `&mutex`, ...): it has the macro
					// owner type, e.g. `Blake3Output` in
					// `macro void Blake3Output.chaining_value(&self, ...)`.
					if (index == 0)
					{
						FullyQualifiedName owner = macroOwnerType(macroDefinition);
						if (owner != null) return owner;
					}
				}
			}
			// `self`/`this` are sugar for the receiver even when the first
			// parameter is named otherwise.
			if ("this".equals(myName) || "self".equals(myName))
			{
				FullyQualifiedName owner = macroOwnerType(macroDefinition);
				if (owner != null) return owner;
			}
		}

		C3CompoundStatement compoundStatement =
			PsiTreeUtil.getParentOfType(this, C3CompoundStatement.class);
		if (compoundStatement != null)
		{
			Collection<C3VarDecl> varDecls =
				PsiTreeUtil.collectElementsOfType(compoundStatement, C3VarDecl.class);
			for (C3VarDecl v : varDecls)
			{
				if (v.getTextOffset() < getTextOffset() && sameLambdaScope(v))
				{
					ASTNode identNode = v.getNode().findChildByType(C3Types.IDENT);
					if (identNode != null && myName.equals(identNode.getText()))
					{
						if (v.getExpr() instanceof C3CompoundInitExpr initExpr)
						{
							if (funcDef != null)
							{
								return resolveBaseTypeFqn(initExpr.getType(), funcDef);
							}
							C3ModuleDefinition initModule = initExpr.getModuleDefinition();
							if (initModule == null) return null;
							List<FullyQualifiedName> res = initModule.resolve(initExpr.getType());
							if (!res.isEmpty()) return res.get(0);
						}
					}
				}
			}
		}

		return findTopLevelConstType(myName);
	}

	/**
	 * Type of a top-level {@code const} or {@code global} declared in the
	 * same file, e.g. {@code ascii::GlyphInfo} for
	 * {@code const GlyphInfo[4] ASCII_LOOKUP}. Only the base type is
	 * resolved (array suffixes are dropped), so a subscripted use
	 * ({@code ASCII_LOOKUP[c].alpha}) chains onto the element type.
	 * Same-file PSI only: cross-file declarations stay unknown here rather
	 * than forcing AST loads of unrelated files from a resolver.
	 */
	private @Nullable FullyQualifiedName findTopLevelConstType(@NotNull String name)
	{
		PsiFile file = getContainingFile();
		if (file == null) return null;
		try
		{
			for (C3ConstDeclarationStmt constDecl : PsiTreeUtil.findChildrenOfType(file, C3ConstDeclarationStmt.class))
			{
				if (name.equals(constDecl.getName()))
				{
					FullyQualifiedName type = declaredTopLevelType(constDecl.getType());
					if (type != null) return type;
				}
			}
			for (C3GlobalDecl globalDecl : PsiTreeUtil.findChildrenOfType(file, C3GlobalDecl.class))
			{
				if (name.equals(globalDeclName(globalDecl)))
				{
					FullyQualifiedName type = declaredTopLevelType(globalDecl.getOptionalType().getType());
					if (type != null) return type;
				}
			}
		}
		catch (Exception ignored)
		{
		}
		return null;
	}

	private static @Nullable String globalDeclName(@NotNull C3GlobalDecl decl)
	{
		try
		{
			ASTNode ident = decl.getNode().findChildByType(C3Types.IDENT);
			return ident != null ? ident.getText() : null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static @Nullable FullyQualifiedName declaredTopLevelType(@Nullable C3Type type)
	{
		if (type == null) return null;
		try
		{
			return FullyQualifiedName.from(type);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static @Nullable FullyQualifiedName resolveBaseTypeFqn(@NotNull C3Type type, @NotNull C3FuncDef funcDef)
	{
		C3ModuleDefinition funcModule = funcDef.getModuleDefinition();
		ModuleName fallback = funcDef.getModuleName();
		return resolveBaseTypeFqn(type, funcModule, fallback);
	}

	private static @Nullable FullyQualifiedName macroParamTypeFqn(@NotNull C3Type type, @NotNull C3MacroDefinition macro)
	{
		return resolveBaseTypeFqn(type, macro.getModuleDefinition(), ModuleName.from(macro));
	}

	private static @Nullable FullyQualifiedName macroOwnerType(@NotNull C3MacroDefinition macro)
	{
		String owner = InterfaceService.methodOwnerTypeName(macro);
		if (owner == null || owner.isEmpty()) return null;
		return InterfaceService.resolveOwnerType(owner, ModuleName.from(macro));
	}

	private static @Nullable FullyQualifiedName resolveBaseTypeFqn(
			@NotNull C3Type type,
			@Nullable C3ModuleDefinition moduleDefinition,
			@Nullable ModuleName fallbackModule)
	{
		C3BaseType baseType = type.getBaseType();
		if (baseType.isPrimitiveType()) return null;
		PsiReference ref = baseType.getReference();
		if (ref != null)
		{
			PsiElement resolved = ref.resolve();
			if (resolved instanceof C3TypeName tn)
			{
				return tn.getFqName();
			}
		}

		String nameIdent = baseType.getNameIdent();
		if (nameIdent == null)
		{
			nameIdent = baseType.getText();
			int idx = nameIdent.indexOf('<');
			if (idx > 0) nameIdent = nameIdent.substring(0, idx).trim();
			idx = nameIdent.indexOf('(');
			if (idx > 0) nameIdent = nameIdent.substring(0, idx).trim();
		}

		C3Path path = baseType.getPath();
		if (path != null)
		{
			String pathText = path.getText();
			if (pathText.endsWith("::")) pathText = pathText.substring(0, pathText.length() - 2);
			return new FullyQualifiedName(new ModuleName(pathText), nameIdent);
		}
		if (moduleDefinition != null)
		{
			List<FullyQualifiedName> resolved = moduleDefinition.resolve(type);
			if (!resolved.isEmpty()) return resolved.get(0);
		}
		return new FullyQualifiedName(fallbackModule, nameIdent);
	}

	/**
	 * Type of a {@code foreach} iteration variable, e.g. {@code Entry*} for
	 * {@code Entry* e} in {@code foreach (... Entry* e : src)}. The nearest
	 * enclosing {@code foreach} that declares the name wins: either its
	 * explicit type, or the iterated collection's element type.
	 */
	private @Nullable FullyQualifiedName findForeachVarType(@NotNull String myName)
	{
		PsiElement lambda = enclosingLambda();
		C3ForeachStmt stmt = PsiTreeUtil.getParentOfType(this, C3ForeachStmt.class);
		while (stmt != null)
		{
			// Iteration variables do not cross lambda boundaries either way.
			if (lambda != null && !PsiTreeUtil.isAncestor(lambda, stmt, false)) return null;
			if (lambda == null && enclosingLambda(stmt) != null)
			{
				stmt = PsiTreeUtil.getParentOfType(stmt, C3ForeachStmt.class);
				continue;
			}
			FullyQualifiedName declared = foreachDeclaredType(stmt, myName);
			if (declared != null) return declared;
			FullyQualifiedName inferred = foreachElementType(stmt, myName);
			if (inferred != null) return inferred;
			stmt = PsiTreeUtil.getParentOfType(stmt, C3ForeachStmt.class);
		}
		return null;
	}

	private static @Nullable FullyQualifiedName foreachDeclaredType(
			@NotNull C3ForeachStmt stmt, @NotNull String myName)
	{
		if (stmt.getForeachVars() == null) return null;
		for (C3ForeachVar var : stmt.getForeachVars().getForeachVarList())
		{
			ASTNode ident = var.getNode().findChildByType(C3Types.IDENT);
			if (ident == null || !myName.equals(ident.getText())) continue;
			if (var.getOptionalType() == null || var.getOptionalType().getType() == null) return null;
			String text = var.getOptionalType().getType().getText();
			if (text == null || text.isBlank()) return null;
			text = text.strip();
			// A split-out `&` (address-of iteration) makes the variable a
			// pointer on top of the declared type.
			if (!text.endsWith("*") && var.getNode().findChildByType(C3Types.AMP) != null) text += "*";
			return FullyQualifiedName.parse(text);
		}
		return null;
	}

	private @Nullable FullyQualifiedName foreachElementType(
			@NotNull C3ForeachStmt stmt, @NotNull String myName)
	{
		if (stmt.getForeachVars() == null || stmt.getExpr() == null) return null;
		C3ForeachVar target = null;
		for (C3ForeachVar var : stmt.getForeachVars().getForeachVarList())
		{
			ASTNode ident = var.getNode().findChildByType(C3Types.IDENT);
			if (ident != null && myName.equals(ident.getText())) target = var;
		}
		if (target == null) return null;
		InferredType element = elementTypeOf(stmt.getExpr());
		if (element == null)
		{
			// Last resort for generic slicing macros
			// (`find_segment_section_body(mh, seg, sect, DynamicMethod)`):
			// a trailing type argument at a `$Type` parameter position names
			// the element type the macro builds its collection from.
			String substituted = comptimeElementType(stmt.getExpr());
			if (substituted == null) return null;
			if (target.getNode().findChildByType(C3Types.AMP) != null) substituted += "*";
			return FullyQualifiedName.parse(substituted);
		}
		String name = element.getName();
		// `foreach (&dm : ...)` iterates by reference: the variable is a
		// pointer to the collection's element type.
		if (target.getNode().findChildByType(C3Types.AMP) != null) name += "*";
		return FullyQualifiedName.parse(name);
	}

	/**
	 * Element type from a `$Type`-parameterized macro call used as the
	 * iterated collection, e.g. {@code DynamicMethod} for
	 * {@code find_segment_section_body(mh, seg, sect, DynamicMethod)} backed
	 * by {@code macro find_segment_section_body(..., $Type)}. Only fires when
	 * normal inference drew a blank, and only for bare type-name arguments at
	 * `$Name` parameter positions. Pure PSI walk plus a guarded index check
	 * for the type-ness of the argument.
	 */
	private @Nullable String comptimeElementType(@NotNull C3Expr collection)
	{
		if (!(collection instanceof C3CallExpr call) || call.getCallExprTail() == null) return null;
		C3CallInvocation invocation = call.getCallExprTail().getCallInvocation();
		if (invocation == null || invocation.getCallArgList() == null
			|| invocation.getCallArgList().getArgList() == null) return null;
		C3MacroDefinition macro = resolveCallMacro(call);
		if (macro == null || macro.getMacroParams() == null
			|| macro.getMacroParams().getParameterList() == null) return null;
		List<C3ParamDecl> params = macro.getMacroParams().getParameterList().getParamDeclList();
		List<C3Arg> args = invocation.getCallArgList().getArgList().getArgList();
		String last = null;
		for (int i = 0; i < params.size() && i < args.size(); i++)
		{
			C3Parameter param = params.get(i).getParameter();
			if (param == null || param.getText() == null) continue;
			if (!param.getText().strip().matches("\\$[A-Za-z_][A-Za-z_0-9]*")) continue;
			C3Expr arg = args.get(i).getExpr();
			if (arg == null || arg.getText() == null) continue;
			String argText = arg.getText().strip();
			if (!argText.matches("[A-Za-z_][A-Za-z_0-9.:]*")) continue;
			if (!isTypeNameArg(arg, argText)) continue;
			last = argText;
		}
		return last;
	}

	private @Nullable C3MacroDefinition resolveCallMacro(@NotNull C3CallExpr call)
	{
		try
		{
			C3Expr callee = call.getExpr();
			PsiElement resolved = null;
			if (callee instanceof C3PathIdentExpr pathIdentExpr)
			{
				resolved = pathIdentExpr.getPathIdent().getReference().resolve();
			}
			else if (callee instanceof C3CallExpr inner && inner.getCallExprTail() != null
				&& inner.getCallExprTail().getAccessIdent() != null)
			{
				resolved = inner.getCallExprTail().getAccessIdent().getReference().resolve();
			}
			return resolved instanceof C3MacroDefinition macro ? macro : null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private boolean isTypeNameArg(@NotNull C3Expr arg, @NotNull String argText)
	{
		// A value in disguise (a resolved local/parameter/field) is not a type.
		try
		{
			if (arg instanceof C3PathIdentExpr pathIdentExpr && pathIdentExpr.getPathIdent().getPath() == null)
			{
				PsiElement resolved = pathIdentExpr.getPathIdent().getReference().resolve();
				if (resolved != null && !(resolved instanceof C3TypeName)) return false;
			}
		}
		catch (Exception ignored)
		{
		}
		// Otherwise the name must declare a type somewhere.
		try
		{
			return !org.c3lang.intellij.index.InterfaceService.INSTANCE
				.findTypeDeclarations(FullyQualifiedName.parse(argText), getProject()).isEmpty();
		}
		catch (Exception e)
		{
			return false;
		}
	}

	private @Nullable InferredType elementTypeOf(@NotNull C3Expr collection)
	{
		InferredType inferred;
		try
		{
			inferred = org.c3lang.intellij.types.TypeChecker.infer(collection);
		}
		catch (Exception e)
		{
			return null;
		}
		if (inferred == null) return null;
		String element = org.c3lang.intellij.types.TypeChecker.arrayElementType(inferred.getName());
		if (element == null) return null;
		return org.c3lang.intellij.types.TypeChecker.kindOf(element);
	}

	@Override
	public @NotNull List<C3LocalDeclAfterType> findLocalDeclAfterType()
	{
		C3CompoundStatement scope =
			PsiTreeUtil.getParentOfType(this, C3CompoundStatement.class);
		while (scope != null)
		{
			// Nearest declaration visible from here: an inner block shadows
			// the outer ones, and within one block the last declaration
			// before the use wins.
			C3LocalDeclAfterType best = null;
			for (C3LocalDeclAfterType decl : PsiTreeUtil.findChildrenOfType(scope, C3LocalDeclAfterType.class))
			{
				if (!PsiTreeUtil.isAncestor(scope, decl, false)) continue;
				if (!isVisibleFrom(decl)) continue;
				if (!sameLambdaScope(decl)) continue;
				if (decl.getTextOffset() < getTextOffset()
					&& decl.getNameIdent() != null
					&& decl.getNameIdent().equals(getNameIdent())
					&& (best == null || decl.getTextOffset() > best.getTextOffset()))
				{
					best = decl;
				}
			}
			if (best != null) return Collections.singletonList(best);
			scope = PsiTreeUtil.getParentOfType(scope, C3CompoundStatement.class);
		}
		return Collections.emptyList();
	}

	/**
	 * A declaration is visible from this use when no nested block boundary
	 * sits between them, unless the use itself is inside that nested block.
	 * In other words: the declaration's innermost owning block must also own
	 * (or be) the use, or own an ancestor of the use. Declarations in a
	 * statement condition (`while (T x = ..., x)`) are additionally scoped
	 * to that statement: uses after the loop do not see them.
	 */
	private boolean isVisibleFrom(@NotNull C3LocalDeclAfterType decl)
	{
		PsiElement guard = guardStatement(decl);
		if (guard != null && !PsiTreeUtil.isAncestor(guard, this, false)) return false;
		C3CompoundStatement declScope =
			PsiTreeUtil.getParentOfType(decl, C3CompoundStatement.class);
		if (declScope == null) return true;
		PsiElement current = this;
		while (current != null && current != declScope)
		{
			current = current.getParent();
		}
		if (current == null) return false;
		return decl.getTextOffset() < getTextOffset();
	}

	/**
	 * Nearest {@code if}/{@code while}/{@code for}/{@code switch} owning the
	 * declaration through its condition, or {@code null} for ordinary
	 * block-scoped declarations. Conditions never cross a compound boundary,
	 * so hitting one first means no guard.
	 */
	private static @Nullable PsiElement guardStatement(@NotNull C3LocalDeclAfterType decl)
	{
		PsiElement current = decl.getParent();
		while (current != null)
		{
			if (current instanceof C3IfStmt
				|| current instanceof C3WhileStmt
				|| current instanceof C3ForStmt
				|| current instanceof C3SwitchStmt) return current;
			if (current instanceof C3CompoundStatement) return null;
			current = current.getParent();
		}
		return null;
	}


	/**
	 * Nearest enclosing lambda (`fn (i) => ...`, `fn int(int i) {...}`), or
	 * {@code null} outside one. Lambdas do not capture: names from outside
	 * are invisible inside, and lambda parameters are invisible outside.
	 */
	private @Nullable PsiElement enclosingLambda()
	{
		return PsiTreeUtil.getParentOfType(
			(PsiElement) this, C3LambdaDeclExpr.class, C3LambdaDeclShortExpr.class);
	}

	private static @Nullable PsiElement enclosingLambda(@NotNull PsiElement element)
	{
		return PsiTreeUtil.getParentOfType(
			element, C3LambdaDeclExpr.class, C3LambdaDeclShortExpr.class);
	}

	/**
	 * Whether a declaration may be seen from this use across lambda
	 * boundaries: both must live in the same innermost lambda (or both
	 * outside any lambda).
	 */
	private boolean sameLambdaScope(@NotNull PsiElement declaration)
	{
		return Objects.equals(enclosingLambda(), enclosingLambda(declaration));
	}

	private boolean hasLocalDeclBeforeUse()
	{
		return !new C3LocalDeclAfterTypeReference(this).multiResolve().isEmpty();
	}

	private boolean hasParameterBeforeUse()
	{
		return !new C3ParameterReference(this).multiResolve().isEmpty();
	}

	private boolean isStructMemberAccess()
	{
		return CompletionExtensionsKt.getRootType(this) != null
			&& PsiTreeUtil.getParentOfType(this, C3PathNameProvider.class) != null;
	}

	private boolean isCallablePosition()
	{
		return isCallCallee() || isReflectOperand() || isAddressOfOperand();
	}

	private boolean isCallCallee()
	{
		C3PathIdentExpr expr = PsiTreeUtil.getParentOfType(this, C3PathIdentExpr.class);
		if (expr == null) return false;

		C3CallExpr call = PsiTreeUtil.getParentOfType(expr, C3CallExpr.class);
		return call != null && call.getExpr() == expr;
	}

	private boolean isReflectOperand()
	{
		C3PathIdentExpr expr = PsiTreeUtil.getParentOfType(this, C3PathIdentExpr.class);
		if (expr == null) return false;

		C3CtAnalyzeExpr analyzeExpr = PsiTreeUtil.getParentOfType(expr, C3CtAnalyzeExpr.class);
		if (analyzeExpr == null) return false;

		return analyzeExpr.getCtAnalyze().getText().equals("$reflect")
			&& analyzeExpr.getGroupedExpr() != null
			&& PsiTreeUtil.isAncestor(analyzeExpr.getGroupedExpr(), expr, false);
	}

	private boolean isAddressOfOperand()
	{
		C3PathIdentExpr expr = PsiTreeUtil.getParentOfType(this, C3PathIdentExpr.class);
		if (expr == null) return false;

		C3UnaryExpr unaryExpr = PsiTreeUtil.getParentOfType(expr, C3UnaryExpr.class);
		if (unaryExpr == null) return false;

		return unaryExpr.getExpr() == expr && unaryExpr.getUnaryOp().getText().equals("&");
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		if (hasCatchBindingBeforeUse()) return new C3CatchBindingReference(this);
		if (hasLocalDeclBeforeUse()) return new C3LocalDeclAfterTypeReference(this);
		if (hasParameterBeforeUse()) return new C3ParameterReference(this);
		if (hasForeachVarBeforeUse()) return new C3ForeachVarReference(this);
		if (isCallablePosition()) return new C3FuncNameReference(this);
		if (isStructMemberAccess()) return new C3StructMemberReference(this);
		return new C3LocalDeclAfterTypeReference(this);
	}

	private boolean hasCatchBindingBeforeUse()
	{
		if (!(this instanceof C3PathIdentMixinImpl mixin)) return false;
		try
		{
			return mixin.findCatchBinding() != null;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	private boolean hasForeachVarBeforeUse()
	{
		if (!(this instanceof C3PathIdentMixinImpl mixin)) return false;
		return mixin.findForeachVar() != null;
	}

	/**
	 * An {@code if (catch)}/{@code if (try)} binding in whose then-branch
	 * this use sits, e.g. {@code err} in {@code if (catch err = x) {...}}.
	 * Bindings shadow everything else and are scoped to the branch, so they
	 * are searched before locals, parameters and iteration variables.
	 */
	@Nullable CatchBinding findCatchBinding()
	{
		String myName = getNameIdent();
		if (myName == null) return null;
		PsiElement lambda = enclosingLambda();
		PsiElement child = this;
		PsiElement parent = getParent();
		int depth = 0;
		while (parent != null && depth < 16)
		{
			if (parent instanceof C3IfStmt ifStmt)
			{
				// Bindings do not cross lambda boundaries either way.
				if (lambda != null && !PsiTreeUtil.isAncestor(lambda, ifStmt, false)) return null;
				if (lambda == null && enclosingLambda(ifStmt) != null) return null;
				if (isThenBranch(ifStmt, child))
				{
					CatchBinding binding = bindingInCond(ifStmt, myName);
					if (binding != null) return binding;
				}
			}
			child = parent;
			parent = parent.getParent();
			depth++;
		}
		return null;
	}

	private static boolean isThenBranch(@NotNull C3IfStmt ifStmt, @NotNull PsiElement child)
	{
		if (child == ifStmt.getCompoundStatement() || child == ifStmt.getStatement()) return true;
		return false;
	}

	private static @Nullable CatchBinding bindingInCond(@NotNull C3IfStmt ifStmt, @NotNull String name)
	{
		C3ParenCond paren = ifStmt.getParenCond();
		C3Cond cond = paren != null ? paren.getCond() : null;
		if (cond == null) return null;
		for (C3CatchUnwrap unwrap : PsiTreeUtil.findChildrenOfType(cond, C3CatchUnwrap.class))
		{
			String binding = unwrap instanceof C3CatchUnwrapMixin mixin ? mixin.getBindingName() : null;
			if (name.equals(binding)) return new CatchBinding(unwrap, true);
		}
		for (C3TryUnwrapChain chain : PsiTreeUtil.findChildrenOfType(cond, C3TryUnwrapChain.class))
		{
			for (C3TryUnwrap unwrap : chain.getTryUnwrapList())
			{
				String binding = unwrap instanceof C3TryUnwrapMixin mixin ? mixin.getBindingName() : null;
				if (name.equals(binding)) return new CatchBinding(unwrap, false);
			}
		}
		return null;
	}

	record CatchBinding(@NotNull C3PsiElement unwrap, boolean isCatch)
	{
	}

	private @Nullable FullyQualifiedName tryBindingType(@NotNull CatchBinding binding)
	{
		if (binding.isCatch() || !(binding.unwrap() instanceof C3TryUnwrap tryUnwrap)) return null;
		C3Expr tested;
		try
		{
			tested = tryUnwrap.getExpr();
		}
		catch (Exception e)
		{
			return null;
		}
		if (tested == null) return null;
		// The tested expression is usually a bare identifier: reuse its
		// declaration type without full inference. Anything else goes
		// through inference with its own depth budget (cycle-safe: the
		// binding never matches inside its own condition).
		try
		{
			if (tested instanceof C3PathIdentExpr pathExpr && pathExpr.getPathIdent().getPath() == null)
			{
				FullyQualifiedName fqn = pathExpr.getPathIdent().findTypeName();
				if (fqn == null) return null;
				return FullyQualifiedName.parse(stripOptionalSuffix(
					org.c3lang.intellij.types.TypeChecker.normalize(fqn.getFullName())));
			}
			InferredType inferred = org.c3lang.intellij.types.TypeChecker.infer(tested);
			if (inferred == null) return null;
			return FullyQualifiedName.parse(stripOptionalSuffix(
				org.c3lang.intellij.types.TypeChecker.normalize(inferred.getName())));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static @NotNull String stripOptionalSuffix(@NotNull String typeName)
	{
		if ((typeName.endsWith("?") || typeName.endsWith("!")) && !typeName.endsWith("*"))
		{
			return typeName.substring(0, typeName.length() - 1);
		}
		return typeName;
	}

	private static class C3CatchBindingReference extends C3ReferenceBase<C3PathIdent>
	{
		C3CatchBindingReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			if (!(myElement instanceof C3PathIdentMixinImpl mixin)) return Collections.emptyList();
			CatchBinding binding;
			try
			{
				binding = mixin.findCatchBinding();
			}
			catch (Exception e)
			{
				return Collections.emptyList();
			}
			return binding != null ? Collections.singletonList(binding.unwrap()) : Collections.emptyList();
		}
	}

	private static class C3LocalDeclAfterTypeReference extends C3ReferenceBase<C3PathIdent>
	{
		C3LocalDeclAfterTypeReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			if (!(myElement instanceof C3PathIdentMixinImpl mixin)) return Collections.emptyList();
			return new ArrayList<>(mixin.findLocalDeclAfterType());
		}
	}

	/**
	 * Declaration site of a {@code foreach} iteration variable, so Find
	 * Usages and rename work on it like on a local.
	 */
	@Nullable C3ForeachVar findForeachVar()
	{
		String myName = getNameIdent();
		if (myName == null) return null;
		PsiElement lambda = enclosingLambda();
		C3ForeachStmt stmt = PsiTreeUtil.getParentOfType(this, C3ForeachStmt.class);
		while (stmt != null)
		{
			if (lambda != null && !PsiTreeUtil.isAncestor(lambda, stmt, false)) return null;
			if (lambda == null && enclosingLambda(stmt) != null)
			{
				stmt = PsiTreeUtil.getParentOfType(stmt, C3ForeachStmt.class);
				continue;
			}
			if (stmt.getForeachVars() != null)
			{
				for (C3ForeachVar var : stmt.getForeachVars().getForeachVarList())
				{
					ASTNode ident = var.getNode().findChildByType(C3Types.IDENT);
					if (ident != null && myName.equals(ident.getText())
						&& var.getTextOffset() < getTextOffset()) return var;
				}
			}
			stmt = PsiTreeUtil.getParentOfType(stmt, C3ForeachStmt.class);
		}
		return null;
	}

	private static class C3ForeachVarReference extends C3ReferenceBase<C3PathIdent>
	{
		C3ForeachVarReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			if (!(myElement instanceof C3PathIdentMixinImpl mixin)) return Collections.emptyList();
			C3ForeachVar var = mixin.findForeachVar();
			return var != null ? Collections.singletonList(var) : Collections.emptyList();
		}
	}

	private static class C3ParameterReference extends C3ReferenceBase<C3PathIdent>
	{
		C3ParameterReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			Collection<C3Parameter> params = null;
			C3FuncDefinition funcDef =
				PsiTreeUtil.getParentOfType(myElement, C3FuncDefinition.class);
			if (funcDef != null)
			{
				params = PsiTreeUtil.collectElementsOfType(funcDef, C3Parameter.class);
			}
			else
			{
				C3MacroDefinition macroDef =
					PsiTreeUtil.getParentOfType(myElement, C3MacroDefinition.class);
				if (macroDef != null)
				{
					params = PsiTreeUtil.collectElementsOfType(macroDef, C3Parameter.class);
				}
			}
			if (params == null) return Collections.emptyList();

			// Lambdas do not capture: only parameters in the same innermost
			// lambda (or outside any lambda, for uses outside) are visible.
			PsiElement useLambda = PsiTreeUtil.getParentOfType(
				myElement, C3LambdaDeclExpr.class, C3LambdaDeclShortExpr.class);
			for (C3Parameter param : params)
			{
				if (param.getNameIdent() == null || !param.getNameIdent().equals(myElement.getNameIdent())) continue;
				PsiElement paramLambda = PsiTreeUtil.getParentOfType(
					param, C3LambdaDeclExpr.class, C3LambdaDeclShortExpr.class);
				if (!Objects.equals(useLambda, paramLambda)) continue;
				return Collections.singleton(param);
			}
			return Collections.emptyList();
		}

	}

	private static class C3FuncNameReference extends C3ReferenceBase<C3PathIdent>
	{
		C3FuncNameReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			C3ModuleDefinition moduleDefinition = myElement.getModuleDefinition();
			List<C3PsiElement> result = new ArrayList<>();
			if (moduleDefinition == null) return result;
			for (C3FullyQualifiedNamePsiElement el :
				NameIndexService.INSTANCE.findByNameEndsWith(myElement.getText(), myElement.getProject()))
			{
				if (el instanceof C3CallablePsiElement
					&& el.getFqName().getName().equals(myElement.getNameIdent())
					&& moduleDefinition.containsImportOrSameModule(el))
				{
					result.add(el);
				}
			}
			return result;
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			C3Path path = myElement.getPath();
			return TextRange.create(path != null ? path.getTextLength() : 0, myElement.getTextLength());
		}
	}

	private static class C3StructMemberReference extends C3ReferenceBase<C3PathIdent>
	{
		C3StructMemberReference(@NotNull C3PathIdent element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			FullyQualifiedName rootType = CompletionExtensionsKt.getRootType(myElement);
			if (rootType == null) return Collections.emptyList();

			C3Arg parentArg = PsiTreeUtil.getParentOfType(myElement, C3Arg.class);
			C3PathNameProvider pathNameProvider =
				PsiTreeUtil.getParentOfType(myElement, C3PathNameProvider.class);
			if (pathNameProvider == null) return Collections.emptyList();
			List<String> path = pathNameProvider.findPathName(false);

			// Walk up through all C3PathNameProvider ancestors of parentArg
			List<String> fieldNames = new ArrayList<>();
			C3PathNameProvider currentProvider = parentArg != null
				? PsiTreeUtil.getParentOfType(parentArg, C3PathNameProvider.class)
				: null;
			while (currentProvider != null)
			{
				fieldNames.addAll(currentProvider.findPathName(false));
				currentProvider = PsiTreeUtil.getParentOfType(currentProvider, C3PathNameProvider.class);
			}
			Collections.reverse(fieldNames);

			List<String> paths = new ArrayList<>(fieldNames);
			paths.addAll(path);
			paths.add(myElement.getText());

			List<C3StructMemberDeclaration> structResult = new ArrayList<>(
				StructService.INSTANCE.getStructMemberDeclaration(rootType, paths, myElement.getProject()));
			if (!structResult.isEmpty()) return new ArrayList<>(structResult);
			// Bitstruct segments resolve outside the struct-member index.
			return bitstructPathResolve(rootType, paths);
		}

		/**
		 * Member walk for paths passing through a bitstruct or a range
		 * designator: struct-typed segments resolve through the struct
		 * index, a bitstruct segment resolves its field directly (it must
		 * be last: fields are scalars), and a {@code [...] } segment
		 * unwraps one array level (range inits like
		 * {@code { [0..31] = { .control } } }). Anything else stays
		 * unresolved.
		 */
		private @NotNull List<C3PsiElement> bitstructPathResolve(
				@NotNull FullyQualifiedName rootType,
				@NotNull List<String> paths)
		{
			FullyQualifiedName current = rootType;
			for (int i = 0; i < paths.size(); i++)
			{
				String segment = paths.get(i);
				boolean last = i == paths.size() - 1;
				if (segment.strip().startsWith("["))
				{
					String element = org.c3lang.intellij.types.TypeChecker.arrayElementType(current.getFullName());
					if (element == null) return Collections.emptyList();
					current = FullyQualifiedName.parse(element);
					continue;
				}
				if (org.c3lang.intellij.types.TypeChecker.isBitstruct(
					current.getFullName(), myElement.getProject(), ModuleName.from(myElement)))
				{
					C3PsiElement bitField = org.c3lang.intellij.types.TypeChecker.findBitstructField(
						current.getFullName(), segment, myElement.getProject(), ModuleName.from(myElement));
					return bitField != null && last ? List.of(bitField) : Collections.emptyList();
				}
				List<C3StructMemberDeclaration> members;
				try
				{
					members = StructService.INSTANCE.getStructMembers(
						current.getFullName() + "." + segment, myElement.getProject());
				}
				catch (Exception e)
				{
					return Collections.emptyList();
				}
				if (members.size() != 1) return Collections.emptyList();
				FullyQualifiedName next = members.get(0).getStructPathType();
				if (next == null) return Collections.emptyList();
				current = next;
			}
			return Collections.emptyList();
		}
	}
}
