package org.c3lang.intellij.psi;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class ExternConstParsingTest extends BasePlatformTestCase
{
	public void testExternConstWithTypeAndAttributeWithoutInitializerParses()
	{
		myFixture.configureByText("main.c3", """
			module app;
			extern const ObjId NS_PASTEBOARD_TYPE_COLOR @cname("NSPasteboardTypeColor");
			""");

		List<PsiErrorElement> errors =
			PsiTreeUtil.collectElementsOfType(myFixture.getFile(), PsiErrorElement.class)
				.stream()
				.toList();
		assertEmpty(errors);

		C3ConstDeclarationStmt declaration =
			PsiTreeUtil.findChildOfType(myFixture.getFile(), C3ConstDeclarationStmt.class);
		assertNotNull(declaration);
		assertEquals("NS_PASTEBOARD_TYPE_COLOR", declaration.getName());
		assertEquals("ObjId", declaration.getType().getText());
	}

	public void testExternConstWithoutTypeOrInitializerDoesNotParseAsValidDeclaration()
	{
		myFixture.configureByText("main.c3", """
			module app;
			extern const FOO;
			""");

		List<PsiErrorElement> errors =
			PsiTreeUtil.collectElementsOfType(myFixture.getFile(), PsiErrorElement.class)
				.stream()
				.toList();
		assertFalse(errors.isEmpty());
	}
}
