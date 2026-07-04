package org.c3lang.intellij;

import org.jetbrains.annotations.NotNull;

public final class C3StringLiteralUtil
{
	private C3StringLiteralUtil()
	{
	}

	public static @NotNull String unescapeStringLiteralSequence(@NotNull String text)
	{
		int index = skipWhitespace(text, 0);
		StringBuilder result = new StringBuilder();
		boolean foundLiteral = false;
		OUTER:
		while (index < text.length())
		{
			switch (text.charAt(index))
			{
				case '"':
					index = appendQuotedString(text, index + 1, result);
					foundLiteral = true;
					break;
				case '`':
					index = appendRawString(text, index + 1, result);
					foundLiteral = true;
					break;
				default:
					break OUTER;
			}
			index = skipWhitespace(text, index);
		}
		return foundLiteral ? result.toString() : text.trim();
	}

	private static int appendQuotedString(@NotNull String text, int index, @NotNull StringBuilder result)
	{
		while (index < text.length())
		{
			char c = text.charAt(index++);
			if (c == '"') return index;
			if (c == '\\' && index < text.length())
			{
				index = appendEscapeSequence(text, index, result);
				continue;
			}
			result.append(c);
		}
		return index;
	}

	private static int appendRawString(@NotNull String text, int index, @NotNull StringBuilder result)
	{
		while (index < text.length())
		{
			char c = text.charAt(index++);
			if (c == '`')
			{
				if (index < text.length() && text.charAt(index) == '`')
				{
					result.append('`');
					index++;
					continue;
				}
				return index;
			}
			result.append(c);
		}
		return index;
	}

	private static int appendEscapeSequence(@NotNull String text, int index, @NotNull StringBuilder result)
	{
		char c = text.charAt(index++);
		switch (c)
		{
			case 'b':
				result.append('\b');
				return index;
			case 'a':
				result.append('\007');
				return index;
			case 't':
				result.append('\t');
				return index;
			case 'e':
				result.append('\033');
				return index;
			case 'n':
				result.append('\n');
				return index;
			case 'f':
				result.append('\f');
				return index;
			case 'r':
				result.append('\r');
				return index;
			case 'v':
				result.append('\013');
				return index;
			case 's':
				result.append(' ');
				return index;
			case '"':
			case '\'':
			case '\\':
				result.append(c);
				return index;
			case 'x':
				return appendHexEscape(text, index, result, 2, "\\x");
			case 'u':
				return appendHexEscape(text, index, result, 4, "\\u");
			case 'U':
				return appendCodePointEscape(text, index, result);
			case '0':
				result.append('\0');
				return index;
			default:
				result.append('\\').append(c);
				return index;
		}
	}

	private static int appendHexEscape(@NotNull String text,
	                                   int index,
	                                   @NotNull StringBuilder result,
	                                   int length,
	                                   @NotNull String escapePrefix)
	{
		if (index + length > text.length())
		{
			result.append(escapePrefix);
			return index;
		}

		int value = 0;
		for (int i = 0; i < length; i++)
		{
			int digit = Character.digit(text.charAt(index + i), 16);
			if (digit < 0)
			{
				result.append(escapePrefix);
				return index;
			}
			value = value * 16 + digit;
		}
		result.append((char) value);
		return index + length;
	}

	private static int appendCodePointEscape(@NotNull String text, int index, @NotNull StringBuilder result)
	{
		if (index + 8 > text.length())
		{
			result.append("\\U");
			return index;
		}

		int value = 0;
		for (int i = 0; i < 8; i++)
		{
			int digit = Character.digit(text.charAt(index + i), 16);
			if (digit < 0)
			{
				result.append("\\U");
				return index;
			}
			value = value * 16 + digit;
		}
		if (!Character.isValidCodePoint(value))
		{
			result.append("\\U");
			return index;
		}
		result.appendCodePoint(value);
		return index + 8;
	}

	private static int skipWhitespace(@NotNull String text, int index)
	{
		while (index < text.length() && Character.isWhitespace(text.charAt(index)))
		{
			index++;
		}
		return index;
	}

}
