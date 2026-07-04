package org.c3lang.intellij;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class C3RunConfigurationUtilTest
{
	@Test
	public void explicitCompilerPathWins()
	{
		assertEquals(
			"/tmp/c3c-custom",
			C3RunConfigurationUtil.findCompilerBinaryPath(null, "Default Compiler", "/tmp/c3c-custom")
		);
	}
}
