package org.c3lang.intellij;

import com.intellij.psi.tree.IElementType;
import org.c3lang.intellij.lexer.C3LexerAdapter;
import org.c3lang.intellij.psi.C3ElementType;
import org.c3lang.intellij.psi.C3Types;

public class C3SyntaxHighlighterLexer extends C3LexerAdapter
{
	private IElementType overrideTokenType = null;
	private IElementType prev = null;

	@Override
	public void advance()
	{
		super.advance();
		do
		{
			if (prev == CONST_IDENT_FAULT)
			{
				overrideTokenType = CONST_IDENT_FAULT_QUESTION;
				break;
			}
			if (getTokenType() == C3Types.CONST_IDENT
				&& getTokenEnd() < getBufferSequence().length()
				&& getBufferSequence().charAt(getTokenEnd()) == '~')
			{
				overrideTokenType = CONST_IDENT_FAULT;
				break;
			}
			overrideTokenType = null;
		} while (false);
		prev = getTokenType();
	}

	@Override
	public IElementType getTokenType()
	{
		return overrideTokenType != null ? overrideTokenType : super.getTokenType();
	}

	public static final C3ElementType CONST_IDENT_FAULT = new C3ElementType("CONST_IDENT_FAULT");
	public static final C3ElementType CONST_IDENT_FAULT_QUESTION = new C3ElementType("CONST_IDENT_FAULT_QUESTION");
}