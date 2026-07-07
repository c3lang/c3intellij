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
	private static final Spacing LINE_BREAK = Spacing.createSpacing(0, 0, 1, true, 1);

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
		if (isStatementBlock(node) && (leftEnd == C3Types.LB || right == C3Types.RB)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (right == C3Types.LB && findFirstLeafOwner(rightNode, C3Types.COMPOUND_STATEMENT) != null)
		{
			if (node.getElementType() == C3Types.LAMBDA_DECL_EXPR) return SINGLE_SPACE;
			return useNextLineBrace() ? LINE_BREAK : SINGLE_SPACE;
		}
		if (leftEnd == C3Types.EOS && !isInsideForCondition()) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);
		if (leftEnd == C3Types.RB && shouldBreakAfterRightBrace(right)) return lineBreak(settings.KEEP_BLANK_LINES_IN_CODE);;
		if (right == C3Types.COMMA) return NO_SPACING;
		if (leftEnd == C3Types.COMMA) return SPACE;
		if (right == C3Types.EOS) return NO_SPACING;
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

	private static boolean isStatementBlock(@NotNull ASTNode node)
	{
		IElementType type = node.getElementType();
		return type == C3Types.COMPOUND_STATEMENT;
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
			if (child.getElementType() != TokenType.WHITE_SPACE)
			{
				return false;
			}
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
			if (child.getElementType() != TokenType.WHITE_SPACE)
			{
				return findFirstLeafOwner(child, targetType);
			}
			child = child.getTreeNext();
		}
		return null;
	}

	private @NotNull Indent getChildIndent(@NotNull ASTNode child)
	{
		if (node.getTreeParent() == null)
		{
			return Indent.getNoneIndent();
		}
		if (isBraceContainer(node) && child.getElementType() != C3Types.LB && child.getElementType() != C3Types.RB)
		{
			return Indent.getNormalIndent();
		}
		return Indent.getNoneIndent();
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
