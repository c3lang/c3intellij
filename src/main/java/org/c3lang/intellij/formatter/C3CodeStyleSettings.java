package org.c3lang.intellij.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class C3CodeStyleSettings extends CustomCodeStyleSettings
{
	public static final int BRACE_STYLE_DEFAULT = 0;
    public static final int BRACE_STYLE_KNR = 1;

    public int C3_BRACE_STYLE = BRACE_STYLE_DEFAULT;

    public C3CodeStyleSettings(CodeStyleSettings settings)
    {
        super("C3CodeStyleSettings", settings);
    }
}
