package org.c3lang.intellij.index;

import com.intellij.psi.stubs.StubIndexKey;
import org.c3lang.intellij.psi.C3AttrdefDecl;
import org.jetbrains.annotations.NotNull;

public class AttributeIndex extends C3StringStubIndexExtension<C3AttrdefDecl>
{
	public static final StubIndexKey<String, C3AttrdefDecl> KEY = StubIndexKey.createIndexKey("c3.attribute");

	@Override
	public @NotNull StubIndexKey<String, C3AttrdefDecl> getKey()
	{
		return KEY;
	}
}