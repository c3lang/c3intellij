package org.c3lang.intellij

import com.intellij.psi.tree.IElementType
import org.c3lang.intellij.lexer.C3LexerAdapter
import org.c3lang.intellij.psi.C3ElementType
import org.c3lang.intellij.psi.C3Types

class C3SyntaxHighlighterLexer : C3LexerAdapter() {

    private var overrideTokenType : IElementType? = null
    private var prev : IElementType? = null

    override fun advance() {
        super.advance()

        if (prev == CONST_IDENT_FAULT) {
            overrideTokenType = CONST_IDENT_FAULT_QUESTION
        } else if (tokenType == C3Types.CONST_IDENT && bufferSequence[tokenEnd] == '?' ) {
            overrideTokenType = CONST_IDENT_FAULT
        } else {
            overrideTokenType = null
        }

        prev = tokenType
    }

    override fun getTokenType(): IElementType? {
        return overrideTokenType ?: super.getTokenType()
    }

    companion object {
        @JvmField
        val CONST_IDENT_FAULT = C3ElementType("CONST_IDENT_FAULT")
        @JvmField
        val CONST_IDENT_FAULT_QUESTION = C3ElementType("CONST_IDENT_FAULT_QUESTION")
    }
}