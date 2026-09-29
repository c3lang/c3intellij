package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.psi.C3AccessIdent;
import org.c3lang.intellij.psi.C3CallExprTail;
import org.c3lang.intellij.psi.C3CompoundStatement;
import org.c3lang.intellij.psi.C3FnParameterList;
import org.c3lang.intellij.psi.C3PathIdent;
import org.c3lang.intellij.psi.C3StructBody;
import org.c3lang.intellij.psi.C3Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class C3KeywordCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final C3KeywordCompletionContributor INSTANCE = new C3KeywordCompletionContributor();

	public static final List<String> PRIMITIVE_TYPES = List.of(
		"void", "bool", "char", "double", "float", "float16", "bfloat16",
		"int128", "ichar", "int", "iptr", "sz", "isz", "long", "short",
		"uint128", "uint", "ulong", "uptr", "ushort", "usz",
		"float128", "any", "fault", "typeid"
	);

	public static final List<String> LITERALS = List.of(
		"true", "false", "null"
	);

	public static final List<String> STATEMENT_KEYWORDS = List.of(
		"var", "if", "else", "while", "do", "for", "foreach", "foreach_r",
		"switch", "case", "default", "nextcase", "break", "continue",
		"defer", "try", "catch", "assert", "asm", "const"
	);

	public static final List<String> TOP_LEVEL_KEYWORDS = List.of(
		"fn", "struct", "union", "enum", "bitstruct", "faultdef",
		"macro", "alias", "typedef", "attrdef", "module", "import",
		"extern", "const", "tlocal", "inline", "static", "asm"
	);

	private C3KeywordCompletionContributor() {}

	@Override
	protected void addCompletions(
		@NotNull CompletionParameters parameters,
		@NotNull ProcessingContext context,
		@NotNull CompletionResultSet result)
	{
		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), PsiComment.class) != null)
		{
			return;
		}

		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), C3AccessIdent.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3CallExprTail.class) != null)
		{
			return;
		}
		C3PathIdent pathIdent = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3PathIdent.class);
		if (pathIdent != null && pathIdent.getPath() != null)
		{
			return;
		}

		int caretOffset = parameters.getOffset();
		CharSequence chars = parameters.getEditor().getDocument().getCharsSequence();
		int start = caretOffset - 1;
		while (start >= 0 && (Character.isJavaIdentifierPart(chars.charAt(start)) || chars.charAt(start) == '@' || chars.charAt(start) == '$'))
		{
			start--;
		}
		String word = chars.subSequence(start + 1, caretOffset).toString();
		if (word.startsWith("@") || word.startsWith("$"))
		{
			return;
		}

		boolean isTypeContext = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3Type.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3FnParameterList.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3StructBody.class) != null;

		boolean isInsideCompound = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3CompoundStatement.class) != null;

		// 1. Primitive types
		double typePriority = isTypeContext ? 18.0 : (isInsideCompound ? 10.0 : 6.0);
		for (String type : PRIMITIVE_TYPES)
		{
			result.addElement(PrioritizedLookupElement.withPriority(
				LookupElementBuilder.create(type).bold().withTypeText("primitive type"),
				typePriority
			));
		}

		// 2. Literals (true, false, null)
		if (isInsideCompound || !isTypeContext)
		{
			for (String lit : LITERALS)
			{
				result.addElement(PrioritizedLookupElement.withPriority(
					LookupElementBuilder.create(lit).bold().withTypeText("keyword"),
					12.0
				));
			}
		}

		// 3. Statement keywords inside compound statement
		if (isInsideCompound)
		{
			for (String kw : STATEMENT_KEYWORDS)
			{
				result.addElement(PrioritizedLookupElement.withPriority(
					LookupElementBuilder.create(kw).bold().withTypeText("keyword"),
					5.0
				));
			}
		}

		// 4. Top-level keywords
		if (!isInsideCompound && !isTypeContext)
		{
			for (String kw : TOP_LEVEL_KEYWORDS)
			{
				result.addElement(PrioritizedLookupElement.withPriority(
					LookupElementBuilder.create(kw).bold().withTypeText("keyword"),
					7.0
				));
			}
		}
	}
}
