package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import kotlin.Pair;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.index.StructService;
import org.c3lang.intellij.psi.AccessPath;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3ExprStmt;
import org.c3lang.intellij.psi.C3Types;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.jetbrains.annotations.NotNull;

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
		if (!PATTERN.accepts(parameters.getPosition()) && !PATTERN.accepts(parameters.getOriginalPosition()))
		{
			return;
		}

		C3ExprStmt lookupTarget = CompletionExtensionsKt.siblingOf(parameters, C3ExprStmt.class);
		if (lookupTarget == null) return;

		String lookupString = CompletionExtensionsKt.getLookupString(parameters, lookupTarget);
		FullyQualifiedName rootType = CompletionExtensionsKt.getRootType(lookupTarget);
		if (rootType == null) return;

		List<String> idents = List.of(lookupString.substring(lookupString.indexOf('.') + 1).split("\\."));
		List<Pair<AccessPath, String>> fields =
			StructService.INSTANCE.getFields(rootType, idents, parameters.getPosition().getProject());

		for (Pair<AccessPath, String> field : fields)
		{
			AccessPath accessPath = field.getFirst();
			if (accessPath.getSegments().size() != 1) continue;

			result.addElement(
				LookupElementBuilder.create(accessPath.getName())
					.withPresentableText(accessPath.getName())
					.withIcon(C3Icons.Nodes.STRUCT_FIELD)
					.withTypeText(field.getSecond())
			);
		}
	}
}
