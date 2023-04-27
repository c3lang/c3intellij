package org.c3lang.intellij;

import com.intellij.lang.Language;

public class C3Language extends Language
{
    public static final C3Language INSTANCE = new C3Language();

    private C3Language()
    {
        super("C3");
    }
}
