package org.c3lang.intellij.formatter;

import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.C3Language;
import org.c3lang.intellij.C3TokenSets;
import org.c3lang.intellij.psi.C3Types;
import org.jetbrains.annotations.NotNull;

final class C3SpacingBuilder
{
    private static final TokenSet COMPILE_TIME_BINARY_OPS = TokenSet.create(
            C3Types.CT_AND,
            C3Types.CT_OR,
            C3Types.CT_PLUS
    );

    private C3SpacingBuilder()
    {
    }

    static @NotNull SpacingBuilder createSpacingBuilder(@NotNull CodeStyleSettings settings)
    {
        return new SpacingBuilder(settings, C3Language.INSTANCE)
                .around(C3TokenSets.ASSIGNMENT).spaces(1)
                .around(C3TokenSets.LOGICAL_OPS).spaces(1)
                .around(COMPILE_TIME_BINARY_OPS).spaces(1)
                .around(C3TokenSets.EQUALITY_OPS).spaces(1)
                .around(C3TokenSets.RELATIONAL_OPS).spaces(1)
                .around(C3TokenSets.BITWISE_OPS).spaces(1)
                .around(C3TokenSets.ADDITIVE_OPS).spaces(1)
                .around(C3TokenSets.MULTIPLICATIVE_OPS).spaces(1)
                .around(C3TokenSets.SHIFT_OPS).spaces(1)
                .before(C3Types.COMMA).spaces(0)
                .after(C3Types.COMMA).spaces(1)
                .before(C3Types.EOS).spaces(0)
                .after(C3Types.EOS).spaces(1)
                .after(C3Types.LP).spaces(0)
                .before(C3Types.RP).spaces(0)
                .after(C3Types.LBT).spaces(0)
                .before(C3Types.RBT).spaces(0)
                .before(C3Types.LBT).spaces(0)
                .after(C3Types.DOT).spaces(0)
                .before(C3Types.DOT).spaces(0)
                .after(C3Types.SCOPE).spaces(0)
                .before(C3Types.SCOPE).spaces(0)
                .before(C3Types.LP).spaces(0);
    }
}
