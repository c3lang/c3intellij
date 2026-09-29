package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import kotlin.Pair;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.AccessPath;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3CallExpr;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3ExprStmt;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3PathIdentExpr;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.c3lang.intellij.types.TypeChecker;

import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.ParamType;
import org.c3lang.intellij.psi.ShortType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public final class TailExprCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final TailExprCompletionContributor INSTANCE = new TailExprCompletionContributor();

	private static final ElementPattern<PsiElement> PATTERN = or(
		psiElement(C3Types.IDENT).inside(C3AccessIdent.class),
		psiElement(PsiWhiteSpace.class).inside(C3CallExprTail.class)
	);

	private TailExprCompletionContributor() {}

	@Override
	protected void addCompletions(
		@NotNull CompletionParameters parameters,
		@NotNull ProcessingContext context,
		@NotNull CompletionResultSet result)
	{
		if (com.intellij.openapi.project.DumbService.isDumb(parameters.getPosition().getProject())) return;
		if (!PATTERN.accepts(parameters.getPosition()) && !PATTERN.accepts(parameters.getOriginalPosition()))
		{
			return;
		}

		PsiElement lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3CallExpr.class);
		if (lookupTarget == null)
		{
			lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3ExprStmt.class);
		}
		if (lookupTarget == null) return;

		String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
		int lastDot = lookupString.lastIndexOf('.');
		String prefix = lastDot >= 0 ? lookupString.substring(lastDot + 1) : "";
		CompletionResultSet scopedResult = result.withPrefixMatcher(prefix);

		addBuiltinCompletions(parameters, lookupTarget, lookupString, scopedResult);

		FullyQualifiedName rootType = CompletionExtensionsKt.getRootType(lookupTarget);
		if (rootType == null) return;

		List<String> idents = List.of(lookupString.substring(lookupString.indexOf('.') + 1).split("\\.", -1));

		if (idents.size() == 1)
		{
			String receiverType = receiverRawType(lookupTarget);
			if (receiverType != null)
			{
				TypeChecker.VectorInfo vector = TypeChecker.parseVector(receiverType);
				if (vector != null && addVectorCompletions(vector, receiverType, scopedResult))
				{
					return;
				}
				if (addArrayCompletions(receiverType, scopedResult))
				{
					return;
				}
			}
		}

		List<Pair<AccessPath, String>> fields =
			StructService.INSTANCE.getFields(rootType, idents, parameters.getPosition().getProject());

		for (Pair<AccessPath, String> field : fields)
		{
			AccessPath accessPath = field.getFirst();
			if (accessPath.getSegments().size() != 1) continue;

			scopedResult.addElement(
				LookupElementBuilder.create(accessPath.getName())
					.withPresentableText(accessPath.getName())
					.withIcon(C3Icons.Nodes.STRUCT_FIELD)
					.withTypeText(field.getSecond())
			);
		}

		Collection<C3CallablePsiElement> methods =
			NameIndexService.INSTANCE.findMethodsForType(rootType, null, parameters.getPosition().getProject());
		for (C3CallablePsiElement method : methods)
		{
			String fullMethodName = method.getFqName().getName();
			int dotIndex = fullMethodName.lastIndexOf('.');
			String methodName = dotIndex >= 0 ? fullMethodName.substring(dotIndex + 1) : fullMethodName;

			List<String> params = new ArrayList<>();
			List<ParamType> paramTypes = method.getParameterTypes();
			int startIdx = 0;
			if (!paramTypes.isEmpty())
			{
				ParamType first = paramTypes.get(0);
				ShortType ft = first.getType();
				// The receiver is implicit: either an explicitly typed self parameter
				// or a typeless first parameter (`&self`, `self`, `&mutex`, ...).
				if (ft == null
					|| ft.getValue().equals(rootType.getName())
					|| ft.getValue().equals(rootType.getName() + "*"))
				{
					startIdx = 1;
				}
			}
			for (int i = startIdx; i < paramTypes.size(); i++)
			{
				ParamType p = paramTypes.get(i);
				ShortType t = p.getType();
				params.add((t != null ? t.getFullName() + " " : "") + p.getName());
			}
			String paramListStr = String.join(", ", params);
			boolean noParams = params.isEmpty();

			LookupElementBuilder builder = LookupElementBuilder.create(method, methodName)
				.withPresentableText(methodName)
				.withIcon(C3Icons.Nodes.FUNCTION)
				.appendTailText("(" + paramListStr + ")", false)
				.withTypeText(method.getReturnType() != null ? method.getReturnType().getFullName() : "void")
				.withInsertHandler((insertionContext, item) -> {
					int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
					CharSequence chars = insertionContext.getDocument().getCharsSequence();
					if (caretOffset < chars.length() && chars.charAt(caretOffset) == '(')
					{
						insertionContext.getEditor().getCaretModel().moveToOffset(caretOffset + 1);
					}
					else
					{
						insertionContext.getDocument().insertString(caretOffset, "()");
						insertionContext.getEditor().getCaretModel().moveToOffset(noParams ? caretOffset + 2 : caretOffset + 1);
					}
				});

			scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 1.0));
		}
	}

	/**
	 * Built-in members needing no declaration: {@code any} fields, slice
	 * fields, vector swizzling, scalar methods and type properties (see
	 * {@link BuiltinMembers}). Runs before the struct/method index lookup and
	 * never blocks it.
	 */
	private static void addBuiltinCompletions(
			@NotNull CompletionParameters parameters,
			@NotNull PsiElement lookupTarget,
			@NotNull String lookupString,
			@NotNull CompletionResultSet scopedResult)
	{
		int lastDot = lookupString.lastIndexOf('.');
		if (lastDot <= 0) return;
		String chain = lookupString.substring(0, lastDot);
		String[] segments;
		try
		{
			segments = chain.split("\\.", -1);
		}
		catch (Exception e)
		{
			return;
		}
		if (segments.length == 0 || segments[0].isEmpty()) return;

		String rootName = plainSegmentName(segments[0]);
		if (rootName == null) return;
		String rootType = receiverRawType(lookupTarget);
		if (rootType == null)
		{
			// Possibly a type name (`int.sizeof`): only for a bare single
			// segment, anything else is an unresolvable expression.
			if (segments.length == 1 && rootName.equals(chain.strip()))
			{
				BuiltinMembers.addTo(scopedResult,
					BuiltinMembers.forTypeName(chain.strip(), parameters.getPosition().getProject()));
			}
			return;
		}
		String current = rootType;
		for (int i = 0; i < segments.length; i++)
		{
			String base = plainSegmentName(segments[i]);
			if (base == null) return;
			boolean indexed = !base.equals(segments[i].strip());
			if (i > 0)
			{
				if (base.isEmpty()) return;
				current = BuiltinMembers.stepType(current, base);
				if (current == null) return;
			}
			if (indexed)
			{
				String element = BuiltinMembers.indexElementType(current);
				if (element == null) return;
				current = element;
			}
		}
		BuiltinMembers.addTo(scopedResult, BuiltinMembers.forValueType(current));
	}

	/**
	 * Segment text without any index/call suffix ({@code arr} for
	 * {@code arr[i]}), or {@code null} for call segments ({@code foo()}),
	 * which this walk does not follow.
	 */
	private static @Nullable String plainSegmentName(@NotNull String segment)
	{
		String clean = segment.strip();
		if (clean.contains("(") || clean.contains(")")) return null;
		int bracket = clean.indexOf('[');
		String base = bracket >= 0 ? clean.substring(0, bracket).strip() : clean;
		if (!base.isEmpty() && !base.matches("[A-Za-z_][A-Za-z_0-9]*")) return null;
		return base;
	}

	private enum VectorReturn
	{
		ELEMENT,
		VECTOR,
		BOOL_VECTOR
	}

	private record VectorBuiltin(String name, String params, boolean floatOnly, VectorReturn returns)
	{
	}

	private static final List<VectorBuiltin> VECTOR_BUILTINS = List.of(
		new VectorBuiltin("sum", "()", false, VectorReturn.ELEMENT),
		new VectorBuiltin("product", "()", false, VectorReturn.ELEMENT),
		new VectorBuiltin("max", "()", false, VectorReturn.ELEMENT),
		new VectorBuiltin("min", "()", false, VectorReturn.ELEMENT),
		new VectorBuiltin("dot", "(other)", false, VectorReturn.ELEMENT),
		new VectorBuiltin("length", "()", true, VectorReturn.ELEMENT),
		new VectorBuiltin("distance", "(other)", true, VectorReturn.ELEMENT),
		new VectorBuiltin("normalize", "()", true, VectorReturn.VECTOR),
		new VectorBuiltin("lerp", "(other, t)", false, VectorReturn.VECTOR),
		new VectorBuiltin("reflect", "(other)", false, VectorReturn.VECTOR),
		new VectorBuiltin("comp_lt", "(other)", false, VectorReturn.BOOL_VECTOR),
		new VectorBuiltin("comp_le", "(other)", false, VectorReturn.BOOL_VECTOR),
		new VectorBuiltin("comp_eq", "(other)", false, VectorReturn.BOOL_VECTOR),
		new VectorBuiltin("comp_gt", "(other)", false, VectorReturn.BOOL_VECTOR),
		new VectorBuiltin("comp_ge", "(other)", false, VectorReturn.BOOL_VECTOR),
		new VectorBuiltin("comp_ne", "(other)", false, VectorReturn.BOOL_VECTOR)
	);

	/**
	 * Raw declared type of the receiver, e.g. {@code "int[<8>]"} for {@code v} in
	 * {@code int[<8>] v = ...; v.<caret>}.
	 */
	private static @Nullable String receiverRawType(@NotNull PsiElement lookupTarget)
	{
		C3PathIdentExpr root = PsiTreeUtil.findChildOfType(lookupTarget, C3PathIdentExpr.class, false);
		if (root == null || root.getPathIdent() == null || root.getPathIdent().getPath() != null) return null;
		PsiElement resolved;
		try
		{
			resolved = root.getPathIdent().getReference().resolve();
		}
		catch (Exception e)
		{
			return null;
		}
		String declared = TypeChecker.declaredTypeText(resolved);
		if (declared == null) return null;
		return declared;
	}

	/**
	 * Adds {@code .len} for arrays and slices (plus {@code .ptr} for slices,
	 * see {@code docs/arrays.md}).
	 *
	 * @return true when the receiver is an array or slice and normal struct
	 * lookup must be skipped.
	 */
	private static boolean addArrayCompletions(
			@NotNull String receiverType,
			@NotNull CompletionResultSet scopedResult)
	{
		String element = TypeChecker.arrayElementType(receiverType);
		if (element == null || element.isEmpty()) return false;

		scopedResult.addElement(PrioritizedLookupElement.withPriority(
			LookupElementBuilder.create("len")
				.withPresentableText("len")
				.withIcon(C3Icons.Nodes.STRUCT_FIELD)
				.withTypeText("sz"),
			5.0));

		if (TypeChecker.isSliceType(receiverType))
		{
			scopedResult.addElement(PrioritizedLookupElement.withPriority(
				LookupElementBuilder.create("ptr")
					.withPresentableText("ptr")
					.withIcon(C3Icons.Nodes.STRUCT_FIELD)
					.withTypeText(element + "*"),
				5.0));
		}
		return true;
	}

	/**
	 * Adds builtin vector methods (see {@code docs/vectors.md}) for a vector receiver.
	 *
	 * @return true when the receiver is a supported vector and normal struct
	 * lookup must be skipped.
	 */
	private static boolean addVectorCompletions(
			@NotNull TypeChecker.VectorInfo vector,
			@NotNull String receiverType,
			@NotNull CompletionResultSet scopedResult)
	{
		String element = vector.element;
		boolean isFloat = TypeChecker.isFloatType(element);
		if (!isFloat && !TypeChecker.isIntegerType(element)) return false;

		String boolVector = vector.size >= 0 ? "bool[<" + vector.size + ">]" : "bool[<*>]";
		for (VectorBuiltin builtin : VECTOR_BUILTINS)
		{
			if (builtin.floatOnly && !isFloat) continue;
			String returnType = switch (builtin.returns)
			{
				case ELEMENT -> element;
				case VECTOR -> receiverType;
				case BOOL_VECTOR -> boolVector;
			};
			boolean noParams = builtin.params.equals("()");
			LookupElementBuilder builder = LookupElementBuilder.create(builtin.name)
				.withPresentableText(builtin.name)
				.withIcon(C3Icons.Nodes.FUNCTION)
				.appendTailText(builtin.params, false)
				.withTypeText(returnType)
				.withInsertHandler((insertionContext, item) -> {
					int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
					CharSequence chars = insertionContext.getDocument().getCharsSequence();
					if (caretOffset < chars.length() && chars.charAt(caretOffset) == '(')
					{
						insertionContext.getEditor().getCaretModel().moveToOffset(caretOffset + 1);
					}
					else
					{
						insertionContext.getDocument().insertString(caretOffset, "()");
						insertionContext.getEditor().getCaretModel().moveToOffset(noParams ? caretOffset + 2 : caretOffset + 1);
					}
				});
			scopedResult.addElement(PrioritizedLookupElement.withPriority(builder, 5.0));
		}
		return true;
	}
}
