package org.c3lang.intellij.psi;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class FunctionParameterParsingTest extends BasePlatformTestCase
{
	private static final String SET_MOUSE = """
		<*
		 @param [&own] mouse
		*>
		fn void GfxWindow.set_mouse(&self, GfxMouse* mouse)
		{
			if (mouse != _gfx.hiddenMouse) self.internal.mouse = mouse;
			_window_set_mouse_platform(self, mouse);
		}
		""";
	private static final String SET_MOUSE_IN_MODULE = "module gfx;\n\n" + SET_MOUSE;

	public void testMethodWithSelfAndPointerParameterParsesParameterNames()
	{
		myFixture.configureByText("main.c3", SET_MOUSE);

		List<PsiErrorElement> errors =
			PsiTreeUtil.collectElementsOfType(myFixture.getFile(), PsiErrorElement.class)
				.stream()
				.toList();
		assertEmpty(errors);

		C3FuncDefinition function =
			PsiTreeUtil.findChildOfType(myFixture.getFile(), C3FuncDefinition.class);
		assertNotNull(function);

		C3ParameterList parameters = function.getFuncDef().getFnParameterList().getParameterList();
		assertNotNull(parameters);
		assertEquals(
			List.of("self", "mouse"),
			parameters.getParamDeclList().stream()
				.map(decl -> decl.getParameter().getName())
				.toList()
		);

		assertEquals(
			List.of("self", "mouse"),
			function.getFuncDef().getParameterTypes().stream()
				.map(ParamType::getName)
				.toList()
		);
	}

	public void testDocParamOwnPointerParameterDoesNotError()
	{
		myFixture.configureByText("main.c3", SET_MOUSE);

		List<HighlightInfo> errors = myFixture.doHighlighting(HighlightSeverity.ERROR);
		assertEmpty(errors);
	}

	public void testDocParamOwnPointerParameterInModuleDoesNotError()
	{
		myFixture.configureByText("main.c3", SET_MOUSE_IN_MODULE);

		List<HighlightInfo> errors = myFixture.doHighlighting(HighlightSeverity.ERROR);
		assertEmpty(errors);
	}
}
