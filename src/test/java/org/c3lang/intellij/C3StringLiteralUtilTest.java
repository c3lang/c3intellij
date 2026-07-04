package org.c3lang.intellij;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class C3StringLiteralUtilTest
{
    @Test
    public void parsesQuotedString()
    {
        assertEquals("hello\nworld", C3StringLiteralUtil.unescapeStringLiteralSequence("\"hello\\nworld\""));
    }

    @Test
    public void parsesHexEscapes()
    {
        assertEquals("ABC", C3StringLiteralUtil.unescapeStringLiteralSequence("\"\\x41\\u0042\\U00000043\""));
    }

    @Test
    public void doesNotParseOctalEscapes()
    {
        assertEquals("\\123", C3StringLiteralUtil.unescapeStringLiteralSequence("\"\\123\""));
    }

    @Test
    public void parsesRawString()
    {
        assertEquals("hello ` world", C3StringLiteralUtil.unescapeStringLiteralSequence("`hello `` world`"));
    }

    @Test
    public void joinsAdjacentStringLiterals()
    {
        assertEquals("raw and quoted\n",
                     C3StringLiteralUtil.unescapeStringLiteralSequence("`raw` \" and \" \"quoted\\n\""));
    }

    @Test
    public void returnsTrimmedTextWhenInputIsNotAStringLiteral()
    {
        assertEquals("plain documentation", C3StringLiteralUtil.unescapeStringLiteralSequence(" plain documentation "));
    }
}
