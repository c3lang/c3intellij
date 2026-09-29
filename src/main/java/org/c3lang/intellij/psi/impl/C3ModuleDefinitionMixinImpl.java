package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.index.NameIndexService;
import org.c3lang.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class C3ModuleDefinitionMixinImpl extends C3PsiElementImpl implements C3ModuleDefinition
{
	public C3ModuleDefinitionMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public @NotNull List<ModuleName> getImports()
	{
		return ModuleName.getImportList(this);
	}

	@Override
	public @NotNull List<C3ImportDecl> getImportDeclarations()
	{
		List<C3TopLevel> topLevels = PsiTreeUtil.getChildrenOfTypeAsList(this, C3TopLevel.class);
		List<C3ImportDecl> result = new ArrayList<>();
		for (C3TopLevel topLevel : topLevels)
		{
			C3ImportDecl importDecl = topLevel.getImportDecl();
			if (importDecl != null) result.add(importDecl);
		}
		return result;
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		return ModuleName.from(this);
	}

	@Override
	public @NotNull List<C3ImportPath> getImportPaths()
	{
		List<C3ImportPath> result = new ArrayList<>();
		for (C3ImportDecl decl : getImportDeclarations())
		{
			result.addAll(decl.getImportPaths().getImportPathList());
		}
		return result;
	}

	@Override
	public boolean containsImportOrSameModule(@NotNull C3FullyQualifiedNamePsiElement callable)
	{
		if (isVisible(callable)) return true;
		// Upstream `isVisible` has no sibling-module rule: keep ours on top
		// (verified against c3c and the language docs: same-parent modules
		// see each other without imports).
		return isInModuleFamily(callable.getModuleName());
	}

	@Override
	public boolean contains(@NotNull C3PathIdent pathIdent)
	{
		return !getImportOf(pathIdent).isEmpty();
	}

	@Override
	public boolean contains(@NotNull C3Path path)
	{
		C3PathIdent pathIdentParent = PsiTreeUtil.getParentOfType(path, C3PathIdent.class);
		return pathIdentParent != null && contains(pathIdentParent);
	}

	@Override
	public @NotNull List<C3ImportPath> getImportOf(@NotNull C3PathIdent pathIdent)
	{
		return getImportOf(pathIdent.getText());
	}

	@Override
	public @NotNull List<C3ImportPath> getImportOf(@NotNull C3PathIdentExpr pathIdentExpr)
	{
		return getImportOf(pathIdentExpr.getText());
	}

	private @NotNull List<C3ImportPath> getImportOf(@NotNull String text)
	{
		List<String> moduleValues = new ArrayList<>();
		for (C3FullyQualifiedNamePsiElement element :
			NameIndexService.INSTANCE.findByNameEndsWith(text, getProject()))
		{
			if (element.getFqName().getSuffixName().equals(text))
			{
				ModuleName moduleName = element.getModuleName();
				if (moduleName != null) moduleValues.add(moduleName.getValue());
			}
		}

		List<C3ImportPath> result = new ArrayList<>();
		for (C3ImportPath importPath : getImportPaths())
		{
			ModuleName mn = importPath.getModuleName();
				if (mn != null)
				{
					for (String moduleValue : moduleValues)
					{
						if (mn.covers(new ModuleName(moduleValue)))
						{
							result.add(importPath);
							break;
						}
					}
				}
		}
		return result;
	}

	@Override
	public @NotNull List<FullyQualifiedName> resolve(@NotNull C3PathIdentExpr expr)
	{
		String nameIdent = expr.getText();
		if (expr.getPathIdent().getPath() == null)
		{
			return Collections.singletonList(new FullyQualifiedName(null, nameIdent));
		}

		List<FullyQualifiedName> result = new ArrayList<>();
		for (C3ImportPath importPath : getImportOf(expr))
		{
			result.add(new FullyQualifiedName(importPath.getModuleName(), nameIdent));
		}
		return result;
	}

	@Override
	public @NotNull List<FullyQualifiedName> resolve(@NotNull C3Type type)
	{
		if (type.getBaseType().isPrimitiveType())
		{
			return Collections.singletonList(new FullyQualifiedName(null, type.getBaseType().getText()));
		}

		com.intellij.psi.PsiReference ref = type.getBaseType().getReference();
		if (ref != null)
		{
			com.intellij.psi.PsiElement resolved = ref.resolve();
			if (resolved instanceof C3TypeName tn)
			{
				return Collections.singletonList(tn.getFqName());
			}
		}

		String typeName = type.getBaseType().getNameIdent();
		if (typeName == null)
		{
			typeName = type.getBaseType().getText();
			int idx = typeName.indexOf('<');
			if (idx > 0) typeName = typeName.substring(0, idx).trim();
			idx = typeName.indexOf('(');
			if (idx > 0) typeName = typeName.substring(0, idx).trim();
		}

		if (type.getBaseType().getPath() == null)
		{
			return Collections.singletonList(new FullyQualifiedName(getModuleName(), typeName));
		}

		List<ModuleName> imports = new ArrayList<>();
		for (C3ImportPath importPath : getImportPaths())
		{
			ModuleName mn = importPath.getModuleName();
			if (mn != null) imports.add(mn);
		}

		List<FullyQualifiedName> result = new ArrayList<>();
		for (C3FullyQualifiedNamePsiElement element :
			NameIndexService.INSTANCE.findByNameEndsWith(typeName, getProject()))
		{
			if (element.getFqName().getFullName().endsWith(typeName)
				&& imports.contains(element.getModuleName()))
			{
				result.add(element.getFqName());
			}
		}
		return result;
	}

	@Override
	public @NotNull List<C3ImportPath> getImportPaths(@NotNull ModuleName moduleName)
	{
		List<C3ImportPath> result = new ArrayList<>();
		for (C3ImportPath importPath : getImportPaths())
		{
			ModuleName mn = importPath.getModuleName();
			if (mn != null && mn.getValue().equals(moduleName.getValue())) result.add(importPath);
		}
		return result;
	}
}
