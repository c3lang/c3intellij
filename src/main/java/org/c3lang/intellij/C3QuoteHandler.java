package org.c3lang.intellij;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import org.c3lang.intellij.psi.C3Types;

public class C3QuoteHandler extends SimpleTokenSetQuoteHandler
{
    public C3QuoteHandler()
    {
        super(C3Types.STRING_LIT, C3Types.CHAR_LIT, TokenType.BAD_CHARACTER);
    }

}
