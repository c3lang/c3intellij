package org.c3lang.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.TokenSet;
import org.c3lang.intellij.index.ModuleIndex;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.*;
import org.c3lang.intellij.psi.reference.C3ReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class C3ImportPathMixinImpl extends C3PsiElementImpl implements C3ImportPath
{
	public C3ImportPathMixinImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public boolean endsWith(@NotNull C3Path path)
	{
		String pathText = path.getText();
		String stripped = pathText.endsWith("::") ? pathText.substring(0, pathText.length() - 2) : pathText;
		ModuleName moduleName = getModuleName();
		return moduleName != null && moduleName.getValue().endsWith(stripped);
	}

	@Override
	public int getTextOffset()
	{
		return getFirstChild().getTextOffset();
	}

	@Override
	public @NotNull PsiReference getReference()
	{
		return new C3ImportPathReference(this);
	}

	@Override
	public @Nullable ModuleName getModuleName()
	{
		ASTNode[] identifiers = getNode().getChildren(TokenSet.create(C3Types.IDENT));
		if (identifiers.length == 0) return null;

		StringBuilder moduleName = new StringBuilder();
		for (ASTNode identifier : identifiers)
		{
			if (!moduleName.isEmpty()) moduleName.append("::");
			moduleName.append(identifier.getText());
		}
		return new ModuleName(moduleName.toString());
	}

	@Override
	public boolean isPublicImport()
	{
		C3Attributes attributes = getAttributes();
		if (attributes == null) return false;
		for (C3Attribute attribute : attributes.getAttributeList())
		{
			if (isPublicAttribute(attribute)) return true;
		}
		return false;
	}

	@Override
	public boolean hasValidImportAttributes()
	{
		C3Attributes attributes = getAttributes();
		if (attributes == null) return true;
		for (C3Attribute attribute : attributes.getAttributeList())
		{
			if (!isPublicAttribute(attribute)) return false;
		}
		return true;
	}

	private static boolean isPublicAttribute(@NotNull C3Attribute attribute)
	{
		return "@public".equals(attribute.getAttributeName().getText());
	}

	public static @Nullable C3Module findModuleDirectly(@NotNull String moduleName, @NotNull Project project)
	{
		PsiManager psiManager = PsiManager.getInstance(project);
		LocalFileSystem lfs = LocalFileSystem.getInstance();
		// This helper runs from reference resolution, which can happen while
		// building stubs on an indexing thread: in dumb mode use only already-known
		// stdlib paths (no compiler detection, no settings writes) and never trigger
		// a synchronous VFS refresh.
		boolean dumb = com.intellij.openapi.project.DumbService.isDumb(project);
		List<String> stdlibPaths = dumb
			? C3ProjectService.getInstance(project).getKnownStdlibPaths()
			: C3ProjectService.getInstance(project).getStdlibPaths();

		String relativePath = moduleName.replace("::", "/");
		List<String> candidateRelPaths = new ArrayList<>();
		candidateRelPaths.add(relativePath + ".c3");
		candidateRelPaths.add(relativePath + ".c3i");
		if (relativePath.startsWith("std/"))
		{
			candidateRelPaths.add(relativePath.substring(4) + ".c3");
			candidateRelPaths.add(relativePath.substring(4) + ".c3i");
		}

		for (String stdlibPath : stdlibPaths)
		{
			for (String relPath : candidateRelPaths)
			{
				String fullPath = stdlibPath + "/" + relPath;
				VirtualFile vf = lfs.findFileByPath(fullPath);
				if (vf == null && !dumb) vf = lfs.refreshAndFindFileByPath(fullPath);
				if (vf != null && vf.isValid())
				{
					C3Module mod = findModuleInSingleFile(vf, moduleName, psiManager);
					if (mod != null) return mod;
				}
			}
		}

		// Full stdlib scan is a last resort: it walks every file's PSI and can
		// trigger stub/AST reconciliation on unrelated files (UpToDateStubIndexMismatch).
		String singleRoot = singleConventionalRoot(moduleName, stdlibPaths);
		if (singleRoot != null)
		{
			VirtualFile root = lfs.findFileByPath(singleRoot);
			if (root == null && !dumb) root = lfs.refreshAndFindFileByPath(singleRoot);
			if (root != null && root.isValid())
			{
				C3Module match = findModuleInVirtualFile(root, moduleName, psiManager);
				if (match != null) return match;
			}
			return null;
		}

		for (String stdlibPath : stdlibPaths)
		{
			VirtualFile root = lfs.findFileByPath(stdlibPath);
			if (root == null && !dumb) root = lfs.refreshAndFindFileByPath(stdlibPath);
			if (root != null && root.isValid())
			{
				C3Module match = findModuleInVirtualFile(root, moduleName, psiManager);
				if (match != null) return match;
			}
		}

		return null;
	}

	/**
	 * Reads only the header (module declaration) of a single file. Unlike a full
	 * PSI walk this does not force stub/AST reconciliation of unrelated content.
	 */
	private static @Nullable C3Module findModuleInSingleFile(
			@NotNull VirtualFile file,
			@NotNull String moduleName,
			@NotNull PsiManager psiManager)
	{
		String ext = file.getExtension();
		if (!"c3".equals(ext) && !"c3i".equals(ext)) return null;
		PsiFile psi = psiManager.findFile(file);
		if (!(psi instanceof C3File c3File)) return null;
		for (PsiElement child : c3File.getChildren())
		{
			if (child instanceof C3Module direct)
			{
				ModuleName mn = direct.getModuleName();
				if (mn != null && moduleName.equals(mn.getValue()))
				{
					return direct;
				}
			}
		}
		return null;
	}

	/**
	 * Conventional module root for multi-segment names, e.g. {@code std/os/linux}
	 * for {@code std::os::linux}, when it exists. Narrows the fallback scan to a
	 * single directory instead of the whole stdlib.
	 */
	private static @Nullable String singleConventionalRoot(
			@NotNull String moduleName,
			@NotNull List<String> stdlibPaths)
	{
		int firstSeparator = moduleName.indexOf("::");
		int lastSeparator = moduleName.lastIndexOf("::");
		if (firstSeparator < 0 || firstSeparator == lastSeparator) return null;
		String dirPath = moduleName.substring(0, lastSeparator).replace("::", "/");
		for (String stdlibPath : stdlibPaths)
		{
			String candidate = stdlibPath + "/" + dirPath;
			VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(candidate);
			if (vf != null && vf.isValid() && vf.isDirectory()) return candidate;
		}
		return null;
	}

	private static @Nullable C3Module findModuleInVirtualFile(@NotNull VirtualFile file, @NotNull String moduleName, @NotNull PsiManager psiManager)
	{
		if (file.isDirectory())
		{
			for (VirtualFile child : file.getChildren())
			{
				C3Module match = findModuleInVirtualFile(child, moduleName, psiManager);
				if (match != null) return match;
			}
			return null;
		}

		String ext = file.getExtension();
		if ("c3".equals(ext) || "c3i".equals(ext))
		{
			C3Module mod = findModuleInSingleFile(file, moduleName, psiManager);
			if (mod != null) return mod;
		}
		return null;
	}

	private static class C3ImportPathReference extends C3ReferenceBase<C3ImportPath>
	{
		C3ImportPathReference(@NotNull C3ImportPath element)
		{
			super(element);
		}

		@Override
		public @NotNull Collection<C3PsiElement> multiResolve()
		{
			// Key off the attribute-free module name (attributes now live on
			// the import path itself): `import std::io @public` resolves `std::io`.
			ModuleName moduleName = myElement.getModuleName();
			if (moduleName == null) return Collections.emptyList();
			String targetModuleName = moduleName.getValue();
			if (com.intellij.openapi.project.DumbService.isDumb(myElement.getProject()))
			{
				C3Module direct = findModuleDirectly(targetModuleName, myElement.getProject());
				return direct != null ? List.of(direct) : Collections.emptyList();
			}
			Collection<C3PsiElement> elements;
			try
			{
				elements = StubIndex.getElements(
					ModuleIndex.KEY,
					targetModuleName,
					myElement.getProject(),
					C3ProjectService.getInstance(myElement.getProject()).getSearchScope(),
					C3PsiElement.class)
					.stream()
					.filter(C3Module.class::isInstance)
					.toList();
			}
			catch (Exception ignored)
			{
				elements = List.of();
			}

			if (!elements.isEmpty())
			{
				return elements;
			}

			C3Module direct = findModuleDirectly(targetModuleName, myElement.getProject());
			return direct != null ? List.of(direct) : Collections.emptyList();
		}

		@Override
		public @NotNull TextRange getRangeInElement()
		{
			ModuleName moduleName = myElement.getModuleName();
			return TextRange.from(0, moduleName != null ? moduleName.getValue().length() : myElement.getTextLength());
		}
	}
}
