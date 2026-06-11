package org.c3lang.intellij.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.C3TokenSets;
import org.c3lang.intellij.lexer.C3LexerAdapter;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class C3FindUsagesProvider implements FindUsagesProvider
{
	@Override
	public @NotNull WordsScanner getWordsScanner()
	{
		return new DefaultWordsScanner(
			new C3LexerAdapter(),
			TokenSet.create(
				C3Types.IDENT,
				C3Types.CONST_IDENT,
				C3Types.TYPE_IDENT
			),
			C3TokenSets.COMMENTS,
			C3TokenSets.STRINGS
		);
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
	{
		return psiElement instanceof C3NameIdentProvider || psiElement instanceof C3ModulePath;
	}

	@Override
	public @Nullable String getHelpId(@NotNull PsiElement psiElement)
	{
		if (psiElement instanceof C3LocalDeclAfterType
			|| psiElement instanceof C3StructMemberDeclaration)
		{
			return "declaration";
		}
		return null;
	}

	@Override
	public @NotNull String getType(@NotNull PsiElement element)
	{
		switch (element)
		{
			case C3ConstDeclarationStmt ignored: return "constant";
			case C3ConstdefConstant ignored: return "constdef constant";
			case C3ModulePath ignored: return "module";
			case C3FuncName ignored: return "function";
			case C3FuncDef ignored: return "function";
			case C3MacroDefinition ignored: return "macro";
			case C3StructMemberDeclaration ignored: return "field";
			case C3AccessIdent ignored: return "field";
			case C3Arg ignored: return "argument";
			case C3Parameter ignored: return "parameter";
			case C3BaseType ignored: return "type";
			case C3EnumConstant ignored: return "enum constant";
			case C3LocalDeclAfterType ignored: return "local variable";
			case C3TypeName ignored: return "type";
			case C3FaultDefinition ignored: return "fault";
			case C3PathIdent ignored: return "identifier";
			case C3PathConst ignored: return "constant";
			case C3NameIdentProvider ignored: return "name";
			default: return "unknown";
		}
	}

	@Override
	public @NotNull String getDescriptiveName(@NotNull PsiElement element)
	{
		switch (element)
		{
			case C3NameIdentProvider provider:
				String name = provider.getNameIdent();
				return name != null ? name : "";
			case C3ModulePath path:
				return element.getText();
			default:
				return "";
		}
	}

	@Override
	public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName)
	{
		return element.getText();
	}
}
