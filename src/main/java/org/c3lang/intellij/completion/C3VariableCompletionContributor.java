package org.c3lang.intellij.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.c3lang.intellij.C3Icons;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class C3VariableCompletionContributor extends CompletionProvider<CompletionParameters>
{
	public static final C3VariableCompletionContributor INSTANCE = new C3VariableCompletionContributor();

	private static final ElementPattern<PsiElement> PATTERN =
		psiElement().inside(C3CompoundStatement.class).andNot(psiElement().inside(PsiComment.class));

	private C3VariableCompletionContributor() {}

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

		C3PathIdent pathIdent = PsiTreeUtil.getParentOfType(parameters.getPosition(), C3PathIdent.class);
		if (pathIdent != null && pathIdent.getPath() != null)
		{
			return;
		}
		if (PsiTreeUtil.getParentOfType(parameters.getPosition(), C3AccessIdent.class) != null
			|| PsiTreeUtil.getParentOfType(parameters.getPosition(), C3CallExprTail.class) != null)
		{
			return;
		}

		Set<String> seen = new HashSet<>();
		int caretOffset = parameters.getOffset();

		PsiElement current = parameters.getPosition();
		while (current != null && !(current instanceof C3File))
		{
			if (current instanceof C3ForeachStmt foreachStmt)
			{
				C3ForeachVars vars = foreachStmt.getForeachVars();
				if (vars != null)
				{
					for (C3ForeachVar v : vars.getForeachVarList())
					{
						ASTNode idNode = v.getNode().findChildByType(C3Types.IDENT);
						if (idNode != null)
						{
							String name = idNode.getText();
							if (seen.add(name))
							{
								result.addElement(PrioritizedLookupElement.withPriority(
									LookupElementBuilder.create(name)
										.withIcon(C3Icons.Nodes.VARIABLE)
										.withTypeText("foreach var"),
									15.0
								));
							}
						}
					}
				}
			}
			else if (current instanceof C3CompoundStatement compoundStatement)
			{
				for (C3StatementList statementList : compoundStatement.getStatementListList())
				{
					for (C3Statement stmt : statementList.getStatementList())
					{
						if (stmt.getTextOffset() >= caretOffset) break;
						if (stmt.getLocalDeclarationStmt() != null)
						{
							C3DeclStmtAfterType afterType = stmt.getLocalDeclarationStmt().getDeclStmtAfterType();
							C3OptionalType optType = stmt.getLocalDeclarationStmt().getOptionalType();
							String typeText = optType != null ? optType.getText() : "";
							if (afterType != null)
							{
								for (C3LocalDeclAfterType decl : afterType.getLocalDeclAfterTypeList())
								{
									String name = decl.getNameIdent();
									if (name != null && seen.add(name))
									{
										result.addElement(PrioritizedLookupElement.withPriority(
											LookupElementBuilder.create(name)
												.withIcon(C3Icons.Nodes.VARIABLE)
												.withTypeText(typeText),
											15.0
										));
									}
								}
							}
						}
						else if (stmt.getVarStmt() != null && stmt.getVarStmt().getVarDecl() != null)
						{
							ASTNode idNode = stmt.getVarStmt().getVarDecl().getNode().findChildByType(C3Types.IDENT);
							if (idNode != null)
							{
								String name = idNode.getText();
								if (seen.add(name))
								{
									result.addElement(PrioritizedLookupElement.withPriority(
										LookupElementBuilder.create(name)
											.withIcon(C3Icons.Nodes.VARIABLE)
											.withTypeText("var"),
										15.0
									));
								}
							}
						}
					}
				}
			}
			else if (current instanceof C3FuncDefinition funcDefinition)
			{
				addFuncParams(funcDefinition.getFuncDef(), result, seen);
			}
			else if (current instanceof C3FuncDef funcDef)
			{
				addFuncParams(funcDef, result, seen);
			}
			else if (current instanceof C3MacroDefinition macroDef)
			{
				C3MacroParams params = macroDef.getMacroParams();
				C3ParameterList paramList = params != null ? params.getParameterList() : null;
				if (paramList != null)
				{
					for (C3ParamDecl paramDecl : paramList.getParamDeclList())
					{
						C3Parameter p = paramDecl.getParameter();
						String typeText = p.getType() != null ? p.getType().getText() : "";
						for (ASTNode node : p.getNode().getChildren(null))
						{
							if (node.getElementType() == C3Types.IDENT)
							{
								String name = node.getText();
								if (seen.add(name))
								{
									result.addElement(PrioritizedLookupElement.withPriority(
										LookupElementBuilder.create(name)
											.withIcon(C3Icons.Nodes.VARIABLE)
											.withTypeText(typeText),
										16.0
									));
								}
							}
						}
					}
				}
			}
			current = current.getParent();
		}
	}

	private static void addFuncParams(C3FuncDef funcDef, CompletionResultSet result, Set<String> seen)
	{
		C3ParameterList paramList = funcDef.getFnParameterList().getParameterList();
		if (paramList != null)
		{
			for (C3ParamDecl paramDecl : paramList.getParamDeclList())
			{
				C3Parameter p = paramDecl.getParameter();
				String typeText = p.getType() != null ? p.getType().getText() : "";
				String pName = p.getNameIdent();
				if (pName != null && !pName.isEmpty() && seen.add(pName))
				{
					result.addElement(PrioritizedLookupElement.withPriority(
						LookupElementBuilder.create(pName)
							.withIcon(C3Icons.Nodes.VARIABLE)
							.withTypeText(typeText),
						16.0
					));
				}
				for (ASTNode node : p.getNode().getChildren(null))
				{
					if (node.getElementType() == C3Types.IDENT)
					{
						String name = node.getText();
						if (seen.add(name))
						{
							result.addElement(PrioritizedLookupElement.withPriority(
								LookupElementBuilder.create(name)
									.withIcon(C3Icons.Nodes.VARIABLE)
									.withTypeText(typeText),
								16.0
							));
						}
					}
				}
			}
		}
		if (funcDef.getType() != null)
		{
			if (seen.add("this"))
			{
				result.addElement(PrioritizedLookupElement.withPriority(
					LookupElementBuilder.create("this")
						.withIcon(C3Icons.Nodes.VARIABLE)
						.withTypeText(funcDef.getType().getFullName()),
					16.0
				));
			}
			if (seen.add("self"))
			{
				result.addElement(PrioritizedLookupElement.withPriority(
					LookupElementBuilder.create("self")
						.withIcon(C3Icons.Nodes.VARIABLE)
						.withTypeText(funcDef.getType().getFullName()),
					16.0
				));
			}
		}
	}
}
