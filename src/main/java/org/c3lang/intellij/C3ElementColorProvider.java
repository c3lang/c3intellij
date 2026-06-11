package org.c3lang.intellij;

import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.JBColor;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.regex.Pattern;

public class C3ElementColorProvider implements ElementColorProvider
{
	private static final Pattern COLOR_LITERAL = Pattern.compile("0x([\\dA-Fa-f]{8}|[\\dA-Fa-f]{6})");
	private static final Pattern UPPER_HEX = Pattern.compile("[A-F]");

	private PsiElement lastElement;

	@Override
	public @Nullable Color getColorFrom(@NotNull PsiElement element)
	{
		if (element.getNode().getElementType() != C3Types.INT_LITERAL) return null;
		if (!COLOR_LITERAL.matcher(element.getText()).matches()) return null;

		String rgba = element.getText().substring(2);
		long value = Long.parseLong(rgba, 16);
		if (rgba.length() == 6)
		{
			int rgb = (int) value;
			return new JBColor(rgb, rgb);
		}

		value = ((value & 0xFFFFFF00L) >> 8) + ((value & 0xFFL) << 24);
		Color color = new Color((int) value, true);
		return new JBColor(color, color);
	}

	@Override
	public void setColorTo(@NotNull PsiElement element, @NotNull Color color)
	{
		// This workaround is due to https://youtrack.jetbrains.com/issue/IDEA-331607/JavaColorProvider-issues
		// Remove when element actually always is the last element.
		if (lastElement == null || element.getParent().getParent() != null)
		{
			lastElement = element;
		}

		boolean upper = UPPER_HEX.matcher(element.getText()).find();
		String text;
		if (element.getText().length() == 8)
		{
			text = String.format(
				upper ? "0x%02X%02X%02X" : "0x%02x%02x%02x",
				color.getRed(),
				color.getGreen(),
				color.getBlue()
			);
		}
		else
		{
			text = String.format(
				upper ? "0x%02X%02X%02X%02X" : "0x%02x%02x%02x%02x",
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				color.getAlpha()
			);
		}

		PsiFile file = PsiFileFactory.getInstance(element.getProject()).createFileFromText(
			"color_replace.dummy.c3",
			C3SourceFileType.INSTANCE,
			"int a = " + text + ";"
		);

		PsiElement newElement = file.getFirstChild()
			.getFirstChild()
			.getFirstChild()
			.getChildren()[1]
			.getChildren()[0]
			.getFirstChild();

		PsiElement parent = lastElement.getParent();
		lastElement.replace(newElement);
		lastElement = parent.getFirstChild();
	}
}
