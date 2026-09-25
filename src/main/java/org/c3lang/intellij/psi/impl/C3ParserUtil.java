package org.c3lang.intellij.psi.impl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.psi.C3Types;

public class C3ParserUtil extends GeneratedParserUtilBase
{
	private static final TokenSet OTHER_IDENT_TYPES = TokenSet.create(
		C3Types.CONST_IDENT, C3Types.AT_IDENT, C3Types.TYPE_IDENT, C3Types.AT_TYPE_IDENT
	);

	public static boolean parsePathIdent(PsiBuilder b, int level)
	{
		PsiBuilder.Marker marker = b.mark();

		if (consumeToken(b, C3Types.CT_IDENT) || consumeToken(b, C3Types.HASH_IDENT))
		{
			marker.drop();
			return true;
		}

		if (!consumeToken(b, C3Types.IDENT))
		{
			marker.rollbackTo();
			return false;
		}

		boolean scope = b.getTokenType() == C3Types.SCOPE;
		while (consumeToken(b, C3Types.SCOPE))
		{
			if (b.lookAhead(1) != C3Types.SCOPE)
			{
				break;
			}
			consumeToken(b, C3Types.IDENT);
		}

		if (OTHER_IDENT_TYPES.contains(b.getTokenType()))
		{
			marker.rollbackTo();
			return false;
		}

		if (scope)
		{
			marker.done(C3Types.PATH);
		}
		else
		{
			marker.drop();
		}

		if (b.getTokenType() == C3Types.IDENT)
		{
			consumeToken(b, C3Types.IDENT);
		}

		return true;
	}
}
