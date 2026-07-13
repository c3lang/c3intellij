package org.c3lang.intellij.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.C3ParserDefinition;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class C3Block implements Block
{
	private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, true, 0);
	private static final Spacing SPACE = Spacing.createSpacing(1, 1, 0, true, 0);
	private static final Spacing SINGLE_SPACE = Spacing.createSpacing(1, 1, 0, false, 0);

	private final @NotNull ASTNode node;
	private final @Nullable Wrap wrap;
	private final @Nullable Alignment alignment;
	private final @NotNull Indent indent;
	private final @NotNull SpacingBuilder spacingBuilder;
	private final @NotNull C3CodeStyleSettings c3Settings;
	private final @NotNull CommonCodeStyleSettings settings;
	private List<Block> subBlocks;

	C3Block(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @NotNull Indent indent,
	        @NotNull SpacingBuilder spacingBuilder, @NotNull CommonCodeStyleSettings settings, @NotNull C3CodeStyleSettings c3Settings)
	{
		this.node = node;
		this.wrap = wrap;
		this.alignment = alignment;
		this.indent = indent;
		this.spacingBuilder = spacingBuilder;
		this.c3Settings = c3Settings;
		this.settings = settings;
	}

	@Override public @NotNull TextRange getTextRange()
	{
		return node.getTextRange();
	}

	@Override public @NotNull List<Block> getSubBlocks()
	{
		if (subBlocks == null)
		{
			subBlocks = buildSubBlocks();
		}
		return subBlocks;
	}

	@Override public @Nullable Wrap getWrap()
	{
		return wrap;
	}

	@Override public @NotNull Indent getIndent()
	{
		return indent;
	}

	@Override public @Nullable Alignment getAlignment()
	{
		return alignment;
	}

	@Override public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2)
	{
		if (child1 == null)
		{
			return null;
		}

		ASTNode leftNode = getNode(child1);
		ASTNode rightNode = getNode(child2);
		IElementType left = firstLeafType(leftNode);
		IElementType right = firstLeafType(rightNode);
		IElementType leftEnd = lastLeafType(leftNode);
		IElementType rightEnd = lastLeafType(rightNode);

		if (leftEnd == C3Types.LB && right == C3Types.RB) return NO_SPACING;
		if (leftEnd == C3Types.LB && isMultilineInitializerList(node)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (leftEnd == C3Types.LB && isInitializerList(node)) return SPACE;
		if (leftEnd == C3Types.COMMA && right == C3Types.RB && isMultilineInitializerList(node)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (right == C3Types.RB && isInitializerList(node)) return SPACE;
		if (leftEnd == C3Types.LB && isMultilineBraceContainer(node)) return lineBreak(keepBlankLines(right));
		if (right == C3Types.RB && isMultilineBraceContainer(node)) return lineBreak(settings.KEEP_BLANK_LINES_BEFORE_RBRACE);

		if (right == C3Types.LB && findFirstLeafOwner(rightNode, C3Types.COMPOUND_STATEMENT) != null)
		{
			if (node.getElementType() == C3Types.LAMBDA_DECL_EXPR) return SINGLE_SPACE;
			return useNextLineBrace() ? lineBreak(0) : SINGLE_SPACE;
		}
		if (right == C3Types.LB && isDeclarationBrace(rightNode, node)) return useNextLineBrace() ? lineBreak(0) : SINGLE_SPACE;
		if (right == C3Types.LB && findFirstLeafOwner(rightNode, C3Types.INITIALIZER_LIST) != null) return SPACE;
		if (isComment(right)) return preserveSpaces(keepBlankLines(right));
		if (leftEnd == C3Types.EOS && !isInsideForCondition()) return lineBreak(keepBlankLines(right));
		if (leftEnd == C3Types.RB && right == C3Types.LP && containsNodeType(leftNode, C3Types.GENERIC_PARAMETERS)) return NO_SPACING;
		if (leftEnd == C3Types.RB && shouldBreakAfterRightBrace(right)) return lineBreak(keepBlankLines(right));
		if (leftEnd == C3Types.COLON && isCompileTimeCaseBody(node)) return hasLineBreakBetween(leftNode, rightNode) ? lineBreak(keepBlankLines(right)) : SINGLE_SPACE;
		if (leftEnd == C3Types.COLON && isCompileTimeBodyOwner(node) && isCompileTimeBody(rightNode)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (leftEnd == C3Types.KW_CT_ELSE && isCompileTimeBody(rightNode)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (isCompileTimeEndKeyword(leftEnd)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (isComment(leftEnd)) return preserveSpaces(keepBlankLines(right));
		if (isInterfaceImplementationSpacing(node, rightNode)) return SPACE;
		if (right == C3Types.COMMA) return NO_SPACING;
		if (leftEnd == C3Types.COMMA && isEnumLikeList(node)) return lineBreak(settings.KEEP_BLANK_LINES_IN_DECLARATIONS);
		if (leftEnd == C3Types.COMMA) return SPACE;
		if (right == C3Types.EOS) return NO_SPACING;
		if (leftEnd == C3Types.KW_RETURN) return SPACE;
		if (right == C3Types.IMPLIES || leftEnd == C3Types.IMPLIES) return SPACE;
		if (leftEnd == C3Types.COLON && isCaseBody(node)) return hasLineBreakBetween(leftNode, rightNode) ? lineBreak(keepBlankLines(right)) : SINGLE_SPACE;
		if ((right == C3Types.COLON || leftEnd == C3Types.COLON) && isSpacedColonContainer(node)) return SPACE;
		if (right == C3Types.COLON && isForeachColonWithSpaces(rightNode, node)) return SPACE;
		if (leftEnd == C3Types.COLON && isForeachColonWithSpaces(leftNode, node)) return SPACE;
		if (isTypedVariadicParameterSpacing(node, leftNode, leftEnd, right)) return right == C3Types.ELLIPSIS ? NO_SPACING : SPACE;
		if (isTypeDeclarationSpacing(node, leftNode, right)) return SPACE;
		if (isTypeModifierSpacing(node, leftEnd, right)) return NO_SPACING;
		if (isAssignmentOperator(right) || isAssignmentOperator(leftEnd)) return SPACE;
		if (node.getElementType() == C3Types.BINARY_EXPR && (isBinaryOperator(right) || isBinaryOperator(leftEnd))) return SPACE;
		if (right == C3Types.LP) return isControlKeyword(leftEnd) ? SPACE : NO_SPACING;

		return spacingBuilder.getSpacing(this, child1, child2);
	}

	@Override public @NotNull ChildAttributes getChildAttributes(int newChildIndex)
	{
		return new ChildAttributes(isBraceContainer(node) ? Indent.getNormalIndent() : Indent.getNoneIndent(), null);
	}

	private @NotNull Spacing lineBreak(int keepBlankLines)
	{
		return Spacing.createSpacing(0, 0, 1, true, keepBlankLines);
	}

	private @NotNull Spacing preserveSpaces(int keepBlankLines)
	{
		return Spacing.createSpacing(0, Integer.MAX_VALUE, 0, true, keepBlankLines);
	}

	/**
	 * Number of blank lines to preserve at a forced line break, chosen from the "keep lines" settings
	 * based on the break's context: before a closing brace, inside a declaration container, or in code.
	 */
	private int keepBlankLines(@Nullable IElementType right)
	{
		if (right == C3Types.RB) return settings.KEEP_BLANK_LINES_BEFORE_RBRACE;
		return isDeclarationContainer(node) ? settings.KEEP_BLANK_LINES_IN_DECLARATIONS
		                                    : settings.KEEP_BLANK_LINES_IN_CODE;
	}

	private static boolean isDeclarationContainer(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.DEFAULT_MODULE_SECTION || type == C3Types.MODULE_SECTION ||
		       type == C3Types.STRUCT_BODY || type == C3Types.BITSTRUCT_BODY ||
		       type == C3Types.INTERFACE_BODY || type == C3Types.ENUM_DECLARATION ||
		       type == C3Types.CONSTDEF_DECLARATION;
	}

	private static boolean isMultilineBraceContainer(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.COMPOUND_STATEMENT || type == C3Types.STRUCT_BODY ||
		       type == C3Types.BITSTRUCT_BODY || type == C3Types.INTERFACE_BODY ||
		       type == C3Types.ENUM_DECLARATION || type == C3Types.CONSTDEF_DECLARATION;
	}

	private static boolean isInitializerList(@NotNull ASTNode node)
	{
		return node.getElementType() == C3Types.INITIALIZER_LIST;
	}

	private static boolean isMultilineInitializerList(@NotNull ASTNode node)
	{
		return isInitializerList(node) && node.getText().indexOf('\n') >= 0;
	}

	private static boolean isEnumLikeList(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.ENUM_LIST || type == C3Types.CONSTDEF_LIST;
	}

	private static boolean isSpacedColonContainer(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.ENUM_DECLARATION || type == C3Types.CONSTDEF_DECLARATION ||
		       type == C3Types.BITSTRUCT_DECLARATION || type == C3Types.BITSTRUCT_DEF;
	}

	private static boolean isComment(@Nullable IElementType type)
	{
		return type == C3ParserDefinition.LINE_COMMENT ||
		       type == C3ParserDefinition.DOC_COMMENT ||
		       type == C3ParserDefinition.BLOCK_COMMENT;
	}

	private static boolean isCompileTimeBody(@Nullable ASTNode node)
	{
		if (node == null) return false;
		IElementType type = node.getElementType();
		return type == C3Types.STATEMENT_LIST || type == C3Types.CT_SWITCH_BODY;
	}

	private static boolean isCompileTimeBodyOwner(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.CT_IF_STMT || type == C3Types.CT_FOREACH_STMT ||
		       type == C3Types.CT_FOR_STMT || type == C3Types.CT_CASE_STMT ||
		       type == C3Types.CT_SWITCH_STMT;
	}

	private static boolean isCompileTimeEndKeyword(@Nullable IElementType type)
	{
		return type == C3Types.KW_CT_ENDIF || type == C3Types.KW_CT_ENDFOREACH ||
		       type == C3Types.KW_CT_ENDFOR || type == C3Types.KW_CT_ENDSWITCH;
	}

	private static boolean isForeachColonWithSpaces(@Nullable ASTNode colonNode, @NotNull ASTNode parentNode)
	{
		if (colonNode == null) return false;
		IElementType parentType = parentNode.getElementType();
		if (parentType != C3Types.FOREACH_STMT && parentType != C3Types.CT_FOREACH_STMT) return false;
		IElementType nextType = nextSiblingType(colonNode);
		return nextType != C3Types.STATEMENT_LIST && nextType != C3Types.KW_CT_ENDFOREACH;
	}

	private static boolean isCaseBody(@NotNull ASTNode parentNode)
	{
		IElementType parentType = parentNode.getElementType();
		return parentType == C3Types.CASE_STMT || parentType == C3Types.DEFAULT_STMT;
	}

	private static boolean isCompileTimeCaseBody(@NotNull ASTNode parentNode)
	{
		return parentNode.getElementType() == C3Types.CT_CASE_STMT;
	}

	private static boolean isInterfaceImplementationSpacing(@NotNull ASTNode parentNode, @Nullable ASTNode rightNode)
	{
		IElementType parentType = parentNode.getElementType();
		return isInterfaceImplementationOwner(parentType) &&
		       findFirstLeafOwner(rightNode, C3Types.INTERFACE_IMPL) != null;
	}

	private static boolean isInterfaceImplementationOwner(@Nullable IElementType type)
	{
		return type == C3Types.STRUCT_DECLARATION ||
		       type == C3Types.BITSTRUCT_DECLARATION ||
		       type == C3Types.TYPEDEF_DECL ||
		       type == C3Types.CONSTDEF_DECLARATION ||
		       type == C3Types.ENUM_DECLARATION;
	}

	private static boolean isTypedVariadicParameterSpacing(@NotNull ASTNode parentNode, @Nullable ASTNode leftNode,
	                                                       @Nullable IElementType leftEnd, @Nullable IElementType right)
	{
		if (parentNode.getElementType() != C3Types.PARAMETER) return false;
		return isTypeNode(leftNode == null ? null : leftNode.getElementType()) && right == C3Types.ELLIPSIS ||
		       leftEnd == C3Types.ELLIPSIS && (right == C3Types.IDENT || right == C3Types.CT_IDENT);
	}

	private static boolean isTypeDeclarationSpacing(@NotNull ASTNode parentNode, @Nullable ASTNode leftNode, @Nullable IElementType right)
	{
		if (leftNode == null || right == null || !isTypeNode(leftNode.getElementType())) return false;
		IElementType parentType = parentNode.getElementType();
		return parentType == C3Types.LOCAL_DECLARATION_STMT ||
		       parentType == C3Types.DECL_OR_EXPR ||
		       parentType == C3Types.GLOBAL_DECL ||
		       parentType == C3Types.PARAMETER ||
		       parentType == C3Types.ENUM_PARAM_DECL ||
		       parentType == C3Types.CONST_DECLARATION_STMT ||
		       parentType == C3Types.FUNC_HEADER ||
		       parentType == C3Types.MACRO_HEADER ||
		       parentType == C3Types.FOREACH_VAR ||
		       parentType == C3Types.TRY_UNWRAP ||
		       parentType == C3Types.CATCH_UNWRAP;
	}

	private static boolean isTypeNode(@Nullable IElementType type)
	{
		return type == C3Types.TYPE || type == C3Types.OPTIONAL_TYPE;
	}

	private static boolean isTypeModifierSpacing(@NotNull ASTNode parentNode, @Nullable IElementType leftEnd, @Nullable IElementType right)
	{
		IElementType parentType = parentNode.getElementType();
		if (parentType == C3Types.TYPE)
		{
			return isTypeSuffixStart(right);
		}
		if (parentType == C3Types.OPTIONAL_TYPE)
		{
			return right == C3Types.QUESTION;
		}
		if (parentType == C3Types.TYPE_SUFFIX)
		{
			return leftEnd == C3Types.LBT || leftEnd == C3Types.LVEC ||
			       right == C3Types.RBT || right == C3Types.RVEC;
		}
		return false;
	}

	private static boolean isTypeSuffixStart(@Nullable IElementType type)
	{
		return type == C3Types.STAR || type == C3Types.LBT || type == C3Types.LVEC;
	}

	private static boolean isDeclarationBrace(@Nullable ASTNode rightNode, @NotNull ASTNode parentNode)
	{
		IElementType parentType = parentNode.getElementType();
		return findFirstLeafOwner(rightNode, C3Types.STRUCT_BODY) != null ||
		       findFirstLeafOwner(rightNode, C3Types.BITSTRUCT_BODY) != null ||
		       findFirstLeafOwner(rightNode, C3Types.INTERFACE_BODY) != null ||
		       parentType == C3Types.ENUM_DECLARATION ||
		       parentType == C3Types.CONSTDEF_DECLARATION;
	}

	@Override public boolean isIncomplete()
	{
		return false;
	}

	@Override public boolean isLeaf()
	{
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE) return false;
			child = child.getTreeNext();
		}
		return true;
	}

	@Override public @NotNull String getDebugName()
	{
		return node.getElementType().toString();
	}

	private @NotNull List<Block> buildSubBlocks()
	{
		List<Block> blocks = new ArrayList<>();
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE)
			{
				blocks.add(new C3Block(child, null, null, getChildIndent(child), spacingBuilder, settings, c3Settings));
			}
			child = child.getTreeNext();
		}
		return blocks;
	}

	private static @Nullable ASTNode findFirstLeafOwner(@Nullable ASTNode node, @NotNull IElementType targetType)
	{
		if (node == null) return null;
		if (node.getElementType() == targetType) return node;
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE) return findFirstLeafOwner(child, targetType);
			child = child.getTreeNext();
		}
		return null;
	}

	private @NotNull Indent getChildIndent(@NotNull ASTNode child)
	{
		if (node.getTreeParent() == null) return Indent.getNoneIndent();
		if (node.getElementType() == C3Types.FAULTDEF_DECL && child.getElementType() == C3Types.FAULT_DEFINITION) return Indent.getNormalIndent();
		if (isCompileTimeIndentedChild(node, child)) return Indent.getNormalIndent();
		if (isBraceContainer(node) && isBetweenDirectBraces(child)) return Indent.getNormalIndent();
		return Indent.getNoneIndent();
	}

	private static boolean isCompileTimeIndentedChild(@NotNull ASTNode node, @NotNull ASTNode child)
	{
		IElementType parentType = node.getElementType();
		IElementType childType = child.getElementType();
		if (isComment(childType) && isCompileTimeBodyOwner(node) && hasLineBreakBefore(child)) return true;
		if (childType == C3Types.STATEMENT_LIST)
		{
			return parentType == C3Types.CT_IF_STMT || parentType == C3Types.CT_FOREACH_STMT ||
			       parentType == C3Types.CT_FOR_STMT ||
			       (parentType == C3Types.CT_CASE_STMT && hasLineBreakBefore(child)) ||
			       ((parentType == C3Types.CASE_STMT || parentType == C3Types.DEFAULT_STMT) && hasLineBreakBefore(child));
		}
		return parentType == C3Types.CT_SWITCH_STMT && childType == C3Types.CT_SWITCH_BODY;
	}

	private static boolean hasLineBreakBefore(@NotNull ASTNode child)
	{
		ASTNode sibling = child.getTreePrev();
		while (sibling != null)
		{
			IElementType type = sibling.getElementType();
			if (type == TokenType.WHITE_SPACE) return sibling.getText().indexOf('\n') >= 0;
			if (type != C3Types.COLON) return false;
			sibling = sibling.getTreePrev();
		}
		return false;
	}

	private static boolean hasLineBreakBetween(@Nullable ASTNode leftNode, @Nullable ASTNode rightNode)
	{
		if (leftNode == null || rightNode == null) return false;
		ASTNode sibling = leftNode.getTreeNext();
		while (sibling != null && sibling != rightNode)
		{
			if (sibling.getElementType() == TokenType.WHITE_SPACE && sibling.getText().indexOf('\n') >= 0) return true;
			sibling = sibling.getTreeNext();
		}
		return false;
	}

	private static boolean isBetweenDirectBraces(@NotNull ASTNode child)
	{
		IElementType type = child.getElementType();
		if (type == C3Types.LB || type == C3Types.RB)
		{
			return false;
		}
		return hasPreviousSibling(child, C3Types.LB) && hasNextSibling(child, C3Types.RB);
	}

	private static boolean hasPreviousSibling(@NotNull ASTNode child, @NotNull IElementType targetType)
	{
		ASTNode sibling = child.getTreePrev();
		while (sibling != null)
		{
			if (sibling.getElementType() != TokenType.WHITE_SPACE)
			{
				if (sibling.getElementType() == targetType) return true;
			}
			sibling = sibling.getTreePrev();
		}
		return false;
	}

	private static boolean hasNextSibling(@NotNull ASTNode child, @NotNull IElementType targetType)
	{
		ASTNode sibling = child.getTreeNext();
		while (sibling != null)
		{
			if (sibling.getElementType() != TokenType.WHITE_SPACE)
			{
				if (sibling.getElementType() == targetType) return true;
			}
			sibling = sibling.getTreeNext();
		}
		return false;
	}

	private static @Nullable IElementType nextSiblingType(@NotNull ASTNode child)
	{
		ASTNode sibling = child.getTreeNext();
		while (sibling != null)
		{
			if (sibling.getElementType() != TokenType.WHITE_SPACE) return sibling.getElementType();
			sibling = sibling.getTreeNext();
		}
		return null;
	}

	private static boolean containsNodeType(@Nullable ASTNode node, @NotNull IElementType targetType)
	{
		if (node == null) return false;
		if (node.getElementType() == targetType) return true;
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE && containsNodeType(child, targetType)) return true;
			child = child.getTreeNext();
		}
		return false;
	}

	private static boolean isBraceContainer(@NotNull ASTNode node)
	{
		boolean hasLeftBrace = false;
		boolean hasRightBrace = false;
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			IElementType type = child.getElementType();
			hasLeftBrace |= type == C3Types.LB;
			hasRightBrace |= type == C3Types.RB;
			child = child.getTreeNext();
		}
		return hasLeftBrace && hasRightBrace;
	}

	private boolean useNextLineBrace()
	{
		return c3Settings.C3_BRACE_STYLE == C3CodeStyleSettings.BRACE_STYLE_DEFAULT;
	}

	private boolean isInsideForCondition()
	{
		IElementType type = node.getElementType();
		return type == C3Types.FOR_COND || type == C3Types.FOR_STMT;
	}

	private static boolean isControlKeyword(@Nullable IElementType type)
	{
		return type == C3Types.KW_IF || type == C3Types.KW_FOR || type == C3Types.KW_FOREACH ||
		       type == C3Types.KW_FOREACH_R || type == C3Types.KW_WHILE || type == C3Types.KW_SWITCH ||
		       type == C3Types.KW_CATCH || type == C3Types.KW_CT_IF || type == C3Types.KW_CT_FOR ||
		       type == C3Types.KW_CT_FOREACH || type == C3Types.KW_CT_SWITCH;
	}

	private static boolean shouldBreakAfterRightBrace(@Nullable IElementType nextType)
	{
		return nextType != C3Types.COMMA && nextType != C3Types.EOS && nextType != C3Types.RP &&
		       nextType != C3Types.RBT && nextType != C3Types.RVEC;
	}

	private static boolean isBinaryOperator(@Nullable IElementType type)
	{
		return type == C3Types.AND || type == C3Types.OR || type == C3Types.CT_AND || type == C3Types.CT_OR ||
		       type == C3Types.EQ_OP || type == C3Types.NE_OP || type == C3Types.LT_OP || type == C3Types.LE_OP ||
		       type == C3Types.GT_OP || type == C3Types.GE_OP || type == C3Types.BIT_OR || type == C3Types.BIT_XOR ||
		       type == C3Types.AMP || type == C3Types.PLUS || type == C3Types.CT_PLUS || type == C3Types.MINUS ||
		       type == C3Types.STAR || type == C3Types.DIV || type == C3Types.MOD || type == C3Types.SHL ||
		       type == C3Types.SHR;
	}

	private static boolean isAssignmentOperator(@Nullable IElementType type)
	{
		return type == C3Types.EQ || type == C3Types.BIT_AND_ASSIGN || type == C3Types.BIT_OR_ASSIGN ||
		       type == C3Types.BIT_XOR_ASSIGN || type == C3Types.MULT_ASSIGN || type == C3Types.DIV_ASSIGN ||
		       type == C3Types.MOD_ASSIGN || type == C3Types.PLUS_ASSIGN || type == C3Types.MINUS_ASSIGN ||
		       type == C3Types.SHL_ASSIGN || type == C3Types.SHR_ASSIGN;
	}

	private static @Nullable ASTNode getNode(@NotNull Block block)
	{
		if (block instanceof C3Block c3Block)
		{
			return c3Block.node;
		}
		return null;
	}

	private static @Nullable IElementType firstLeafType(@Nullable ASTNode node)
	{
		ASTNode leaf = firstLeaf(node);
		return leaf == null ? null : leaf.getElementType();
	}

	private static @Nullable IElementType lastLeafType(@Nullable ASTNode node)
	{
		ASTNode leaf = lastLeaf(node);
		return leaf == null ? null : leaf.getElementType();
	}

	private static @Nullable ASTNode firstLeaf(@Nullable ASTNode node)
	{
		if (node == null)
		{
			return null;
		}
		ASTNode child = node.getFirstChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE)
			{
				ASTNode leaf = firstLeaf(child);
				return leaf == null ? child : leaf;
			}
			child = child.getTreeNext();
		}
		return node;
	}

	private static @Nullable ASTNode lastLeaf(@Nullable ASTNode node)
	{
		if (node == null)
		{
			return null;
		}
		ASTNode child = node.getLastChildNode();
		while (child != null)
		{
			if (child.getElementType() != TokenType.WHITE_SPACE)
			{
				ASTNode leaf = lastLeaf(child);
				return leaf == null ? child : leaf;
			}
			child = child.getTreePrev();
		}
		return node;
	}
}
