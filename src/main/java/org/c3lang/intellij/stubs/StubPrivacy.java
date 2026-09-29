package org.c3lang.intellij.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.c3lang.intellij.psi.C3AttrdefDecl;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.C3ConstDeclarationStmt;
import org.c3lang.intellij.psi.C3ConstdefConstant;
import org.c3lang.intellij.psi.C3EnumConstant;
import org.c3lang.intellij.psi.C3FaultDefinition;
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Whether a declaration carries {@code @private}, resolved stub-first:
 * the flag is serialized into stubs, so index-backed PSI never forces an
 * AST load (which would raise {@code UpToDateStubIndexMismatch} during
 * index reads). Falls back to PSI attributes only when no stub exists
 * (the element was built from a live AST, where reading is free).
 */
public final class StubPrivacy
{
	private StubPrivacy() {}

	public static boolean isPrivate(@NotNull C3FullyQualifiedNamePsiElement element)
	{
		if (element instanceof StubBasedPsiElement<?> stubbed)
		{
			Boolean flag = flagFromStub(stubbed.getStub());
			if (flag != null) return flag;
		}
		return hasPrivateAttribute(attributesOf(element));
	}

	private static @Nullable Boolean flagFromStub(@Nullable Object stub)
	{
		if (stub instanceof C3FuncDefStub s) return s.isPrivate();
		if (stub instanceof C3MacroDefinitionStub s) return s.isPrivate();
		if (stub instanceof C3ConstDeclarationStmtStub s) return s.isPrivate();
		if (stub instanceof C3StructDeclarationStub s) return s.isPrivate();
		if (stub instanceof C3FaultDefinitionStub s) return s.isPrivate();
		if (stub instanceof C3EnumConstantStub s) return s.isPrivate();
		if (stub instanceof C3ConstdefConstantStub s) return s.isPrivate();
		if (stub instanceof C3AttrdefDeclStub s) return s.isPrivate();
		return null;
	}

	private static @Nullable C3Attributes attributesOf(@NotNull C3FullyQualifiedNamePsiElement element)
	{
		try
		{
			if (element instanceof C3FuncDef funcDef) return funcDef.getAttributes();
			if (element instanceof C3MacroDefinition macroDefinition) return macroDefinition.getAttributes();
			if (element instanceof C3ConstDeclarationStmt constDeclaration) return constDeclaration.getAttributes();
			if (element instanceof C3StructDeclaration structDeclaration) return structDeclaration.getAttributes();
			if (element instanceof C3FaultDefinition faultDefinition) return faultDefinition.getAttributes();
			if (element instanceof C3EnumConstant enumConstant) return enumConstant.getAttributes();
			if (element instanceof C3ConstdefConstant constdefConstant) return constdefConstant.getAttributes();
			if (element instanceof C3AttrdefDecl attrdefDecl) return attrdefDecl.getAttributes();
		}
		catch (Exception ignored)
		{
		}
		return null;
	}

	public static boolean hasPrivateAttribute(@Nullable C3Attributes attributes)
	{
		if (attributes == null) return false;
		try
		{
			for (org.c3lang.intellij.psi.C3Attribute attribute : attributes.getAttributeList())
			{
				if (attribute.getAttributeName() != null
					&& "@private".equals(attribute.getAttributeName().getText())) return true;
			}
		}
		catch (Exception ignored)
		{
		}
		return false;
	}

	/**
	 * Computes the flag at stub build time, when the PSI still has its AST.
	 */
	public static boolean computeFlag(@NotNull PsiElement psi)
	{
		try
		{
			if (psi instanceof C3FuncDef funcDef) return hasPrivateAttribute(funcDef.getAttributes());
			if (psi instanceof C3MacroDefinition macroDefinition) return hasPrivateAttribute(macroDefinition.getAttributes());
			if (psi instanceof C3ConstDeclarationStmt constDeclaration) return hasPrivateAttribute(constDeclaration.getAttributes());
			if (psi instanceof C3StructDeclaration structDeclaration) return hasPrivateAttribute(structDeclaration.getAttributes());
			if (psi instanceof C3FaultDefinition faultDefinition) return hasPrivateAttribute(faultDefinition.getAttributes());
			if (psi instanceof C3EnumConstant enumConstant) return hasPrivateAttribute(enumConstant.getAttributes());
			if (psi instanceof C3ConstdefConstant constdefConstant) return hasPrivateAttribute(constdefConstant.getAttributes());
			if (psi instanceof C3AttrdefDecl attrdefDecl) return hasPrivateAttribute(attrdefDecl.getAttributes());
		}
		catch (Exception ignored)
		{
		}
		return false;
	}
}
