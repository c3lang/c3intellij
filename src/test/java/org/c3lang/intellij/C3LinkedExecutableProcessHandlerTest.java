package org.c3lang.intellij;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class C3LinkedExecutableProcessHandlerTest
{
	@Test
	public void parsesLinkedExecutableOutput()
	{
		String executablePath =
				C3LinkedExecutableProcessHandler.parseLinkedExecutable("""
						Compiling project...
						Program linked to executable './bug'.
						Build complete.
						""");

		assertEquals("./bug", executablePath);
	}
}
