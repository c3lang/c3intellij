package org.c3lang.intellij.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DocCommentAnnotator
{
	private DocCommentAnnotator()
	{
	}

	public static void annotateDocComment(PsiComment element, AnnotationHolder holder)
	{
		annotateDocTags(element, holder);
		annotateParamTags(element, holder);
		annotateReturnTags(element, holder);
		annotateDeprecatedTags(element, holder);
		annotateStrings(element, holder);
	}

	public static void annotateStrings(PsiComment element, AnnotationHolder holder)
	{
		Pattern pattern = Pattern.compile("(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)");
		String commentText = element.getText();
		int commentStart = element.getTextRange().getStartOffset();

		Matcher matcher = pattern.matcher(commentText);
		while (matcher.find())
		{
			mark(holder, commentStart + matcher.start(), commentStart + matcher.end(), DefaultLanguageHighlighterColors.STRING);
		}
	}

	private static void annotateDeprecatedTags(PsiComment element, AnnotationHolder holder)
	{
		Pattern pattern = Pattern.compile("@deprecated\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)?");
		String commentText = element.getText();
		int commentStart = element.getTextRange().getStartOffset();

		Matcher matcher = pattern.matcher(commentText);
		while (matcher.find())
		{
			String description = matcher.group(1);
			if (description == null) continue;
			mark(holder, commentStart + matcher.start(1), commentStart + matcher.end(1), DefaultLanguageHighlighterColors.STRING);
		}
	}

	private static void annotateReturnTags(PsiComment element, AnnotationHolder holder)
	{
		Pattern pattern = Pattern.compile("@return\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`)?");
		String commentText = element.getText();
		int commentStart = element.getTextRange().getStartOffset();

		Matcher matcher = pattern.matcher(commentText);
		while (matcher.find())
		{
			String description = matcher.group(1);
			if (description == null) continue;
			mark(holder, commentStart + matcher.start(1), commentStart + matcher.end(1), DefaultLanguageHighlighterColors.STRING);
		}
	}

	private static void annotateDocTags(PsiComment element, AnnotationHolder holder)
	{
		Pattern pattern = Pattern.compile("@(param|return(\\?)?|deprecated|require|ensure|pure)");
		String commentText = element.getText();
		int commentStart = element.getTextRange().getStartOffset();

		Matcher matcher = pattern.matcher(commentText);
		while (matcher.find())
		{
			mark(holder, commentStart + matcher.start(), commentStart + matcher.end(), DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
		}
	}

	private static void addParameters(List<String> args, @Nullable C3ParameterList parameterList)
	{
		if (parameterList == null) return;
		List<C3ParamDecl> decls = parameterList.getParamDeclList();
		for (C3ParamDecl decl : decls)
		{
			C3Parameter parameter = decl.getParameter();
			if (parameter.getName() != null)
			{
				args.add(parameter.getName());
			}
			else if (parameter.getType() != null)
			{
				args.add(parameter.getType().getText());
			}
		}
	}

	private static void annotateParamTags(PsiComment element, AnnotationHolder holder)
	{
		PsiElement next = element.getNextSibling();

		while (next instanceof PsiWhiteSpace || next instanceof PsiComment)
		{
			next = next.getNextSibling();
		}

		if (next instanceof C3DefaultModuleSection) next = next.getFirstChild();

		ArrayList<String> args = new ArrayList<>();
		boolean is_function = false;
		if (next != null)
		{
			next = next.getFirstChild();
			if (next instanceof C3FuncDefinition d)
			{
				is_function = true;
				addParameters(args, d.getFuncDef().getFnParameterList().getParameterList());
			}
			else if (next instanceof C3MacroDefinition d)
			{
				addParameters(args, d.getMacroParams().getParameterList());
			}
		}

		Pattern pattern = Pattern.compile(
				"@param\\s+((\\[(in|&in|out|&out|inout|&inout|own|&own|init|&init|drop|&drop)])\\s+)?(([$#])?\\w+)(\\s+:\\s+(\"((?:[^\"\\\\]|\\\\.)*)\"|`((?:[^`\\\\]|\\\\.)*)`))?");
		String commentText = element.getText();
		int commentStart = element.getTextRange().getStartOffset();

		Matcher matcher = pattern.matcher(commentText);
		while (matcher.find())
		{
			String contract = matcher.group(1);
			String name = matcher.group(4);
			String description = matcher.group(6);

			if (contract != null)
			{
				mark(holder, commentStart + matcher.start(2), commentStart + matcher.end(2), DefaultLanguageHighlighterColors.CONSTANT);
			}

			if (description != null)
			{
				mark(holder, commentStart + matcher.start(6), commentStart + matcher.end(6), DefaultLanguageHighlighterColors.STRING);
			}

			if (name != null)
			{
				if (!args.contains(name))
				{
					TextRange range = TextRange.create(commentStart + matcher.start(), commentStart + matcher.end());
					holder.newAnnotation(HighlightSeverity.ERROR, "There is no argument named '" + name + "' in this " + (is_function ? "function." : "macro.")).range(range).create();
				}

				mark(holder, commentStart + matcher.start(4), commentStart + matcher.end(4), DefaultLanguageHighlighterColors.NUMBER);
			}
		}
	}
	private static void mark(AnnotationHolder holder, int start, int end, TextAttributesKey highlight)
	{
		TextRange nameRange = TextRange.create(start, end);

		holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
		      .range(nameRange)
		      .textAttributes(highlight)
		      .create();

	}
}