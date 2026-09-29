package org.c3lang.intellij;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.c3lang.intellij.psi.C3File;
import org.jetbrains.annotations.NotNull;

public class C3TypedHandlerDelegate extends TypedHandlerDelegate
{
	@Override
	public @NotNull Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file)
	{
		if (!(file instanceof C3File)) return Result.CONTINUE;

		if (c == ':')
		{
			int offset = editor.getCaretModel().getOffset();
			if (offset >= 2)
			{
				CharSequence text = editor.getDocument().getCharsSequence();
				if (text.charAt(offset - 2) == ':')
				{
					AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
				}
			}
		}
		else if (c == '.' || c == '@' || c == '$')
		{
			AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
		}

		return Result.CONTINUE;
	}
}
