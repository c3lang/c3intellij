package org.c3lang.intellij.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.c3lang.intellij.C3Language;
import org.jetbrains.annotations.NotNull;

public class C3FormattingModelBuilder implements FormattingModelBuilder
{
    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext)
    {
        PsiElement element = formattingContext.getPsiElement();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        C3CodeStyleSettings c3Settings = CodeStyle.getCustomSettings(element.getContainingFile(), C3CodeStyleSettings.class);
		CommonCodeStyleSettings commonSettings = settings.getCommonSettings(C3Language.INSTANCE);
        C3Block rootBlock = new C3Block(
                element.getNode(),
                null,
                null,
                com.intellij.formatting.Indent.getNoneIndent(),
                C3SpacingBuilder.createSpacingBuilder(settings),
				commonSettings,
                c3Settings
        );
        return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), rootBlock, settings);
    }
}
