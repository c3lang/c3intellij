package org.c3lang.intellij.index;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.c3lang.intellij.project.C3ProjectService;
import org.c3lang.intellij.psi.C3AliasTypeDecl;
import org.c3lang.intellij.psi.C3Attribute;
import org.c3lang.intellij.psi.C3Attributes;
import org.c3lang.intellij.psi.AttributeSpecs;
import org.c3lang.intellij.psi.C3BitstructDeclaration;
import org.c3lang.intellij.psi.C3CallablePsiElement;
import org.c3lang.intellij.psi.C3EnumDeclaration;
import org.c3lang.intellij.psi.C3FuncDef;
import org.c3lang.intellij.psi.C3FuncDefinition;
import org.c3lang.intellij.psi.C3InterfaceBody;
import org.c3lang.intellij.psi.C3InterfaceDefinition;
import org.c3lang.intellij.psi.C3InterfaceImpl;
import org.c3lang.intellij.psi.C3MacroDefinition;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.c3lang.intellij.psi.C3ParamDecl;
import org.c3lang.intellij.psi.C3Parameter;
import org.c3lang.intellij.psi.C3ParameterList;
import org.c3lang.intellij.psi.C3PathAtIdent;
import org.c3lang.intellij.psi.C3PsiElement;
import org.c3lang.intellij.psi.C3StructDeclaration;
import org.c3lang.intellij.psi.C3TrailingBlockParam;
import org.c3lang.intellij.psi.C3Type;
import org.c3lang.intellij.psi.C3TypeName;
import org.c3lang.intellij.psi.C3TypedefDecl;
import org.c3lang.intellij.psi.FullyQualifiedName;
import org.c3lang.intellij.psi.ModuleName;
import org.c3lang.intellij.psi.ParamType;
import org.c3lang.intellij.psi.ShortType;
import org.c3lang.intellij.psi.impl.C3ImportPathMixinImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Queries about C3 interfaces and struct contracts, e.g.
 * {@code interface MyName { ... }} and {@code struct Baz (MyName) { ... }}.
 *
 * <p>All stub index access is suffix-aware: index keys are fully qualified
 * ({@code test::Baz}) while queries may be short ({@code Baz}), and
 * {@link StubIndex#getElements} is only ever called with keys that are known
 * to exist (see {@code Stub ids not found} handling in {@link StructService}).
 */
public final class InterfaceService
{
    public static final InterfaceService INSTANCE = new InterfaceService();

    private InterfaceService()
    {
    }

    @NotNull
    public List<C3StructDeclaration> findStructDeclarations(
            @NotNull FullyQualifiedName structType,
            @NotNull Project project)
    {
        List<C3StructDeclaration> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String query = structType.getFullName();
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(StructDeclarationIndex.KEY, project))
            {
                if (key.equals(query) || key.endsWith("::" + query))
                {
                    for (C3PsiElement element : StubIndex.getElements(
                            StructDeclarationIndex.KEY,
                            key,
                            project,
                            C3ProjectService.getInstance(project).getSearchScope(),
                            C3PsiElement.class))
                    {
                        if (element instanceof C3StructDeclaration declaration && !result.contains(declaration))
                        {
                            result.add(declaration);
                        }
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            // A stale index entry (e.g. a file indexed before it was
            // recognised as C3) must not break highlighting; the worst case
            // is a partial result until the next reindex.
        }
        return result;
    }

    /**
     * Interfaces listed in the type contracts, e.g. {@code MyName} for
     * {@code struct Baz (MyName)} or {@code typedef DString (OutStream)}:
     * contracts may sit on structs and on typedefs alike.
     */
    @NotNull
    public List<FullyQualifiedName> getImplementedInterfaces(
            @NotNull FullyQualifiedName structType,
            @NotNull Project project)
    {
        List<FullyQualifiedName> result = new ArrayList<>();
        for (C3StructDeclaration declaration : findStructDeclarations(structType, project))
        {
            C3InterfaceImpl impl = declaration.getInterfaceImpl();
            if (impl == null) continue;
            ModuleName structModule = ModuleName.from(declaration);
            if (structModule == null) structModule = structType.getModule();
            addContracts(result, impl, declaration, structModule);
        }
        for (C3TypedefDecl declaration : findTypedefDeclarations(structType, project))
        {
            C3InterfaceImpl impl = declaration.getInterfaceImpl();
            if (impl == null) continue;
            ModuleName typedefModule = ModuleName.from(declaration);
            if (typedefModule == null) typedefModule = structType.getModule();
            addContracts(result, impl, declaration, typedefModule);
        }
        return result;
    }

    private void addContracts(
            @NotNull List<FullyQualifiedName> result,
            @NotNull C3InterfaceImpl impl,
            @NotNull C3PsiElement declaration,
            @Nullable ModuleName fallbackModule)
    {
        List<String> contractTexts = new ArrayList<>();
        contractTexts.add(impl.getTypeName().getText());
        for (C3Type contractType : impl.getTypeList())
        {
            contractTexts.add(contractType.getText());
        }
        List<ModuleName> imports = ModuleName.getImportList(declaration);
        for (String text : contractTexts)
        {
            for (FullyQualifiedName fqn : contractCandidates(text, fallbackModule, imports))
            {
                if (!result.contains(fqn)) result.add(fqn);
            }
        }
    }

    /**
     * Typedef declarations with a given name, e.g. {@code DString} for
     * {@code typedef DString (OutStream) = ...}. Same index discipline as
     * the struct/interface lookups above: {@link TypeIndex} scan by full and
     * short keys, stale entries swallowed.
     */
    @NotNull
    public List<C3TypedefDecl> findTypedefDeclarations(
            @NotNull FullyQualifiedName typeName,
            @NotNull Project project)
    {
        List<C3TypedefDecl> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String query = typeName.getFullName();
        String shortQuery = typeName.getName();
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(query) && !key.endsWith("::" + query)
                    && !key.equals(shortQuery) && !key.endsWith("::" + shortQuery)) continue;
                for (C3PsiElement element : StubIndex.getElements(
                        TypeIndex.KEY,
                        key,
                        project,
                        C3ProjectService.getInstance(project).getSearchScope(),
                        C3PsiElement.class))
                {
                    if (element instanceof C3TypeName name
                        && name.getText().strip().equals(shortQuery)
                        && name.getParent() instanceof C3TypedefDecl declaration
                        && !result.contains(declaration))
                    {
                        result.add(declaration);
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            // See findStructDeclarations: never break highlighting on index issues.
        }
        return result;
    }

    /**
     * Candidate fully-qualified names for a contract entry. A qualified
     * {@code mod::Name} pins the module; a bare {@code Name} may live in the
     * struct's own module or in any imported module (e.g. {@code Printable}
     * from {@code std::io} via {@code import std::io}), so all are tried.
     * Pure PSI text walk: no index access, safe from any context.
     */
    @NotNull
    public List<FullyQualifiedName> contractCandidates(
            @NotNull String text,
            @Nullable ModuleName structModule,
            @NotNull List<ModuleName> imports)
    {
        FullyQualifiedName pinned = parseContractReference(text, structModule);
        if (pinned == null || pinned.getName().isEmpty()) return List.of();
        // A qualified `mod::Name` pins the module; a bare `Name` may live in
        // the struct's own module or in any imported module, so all are tried.
        // parseContractReference already stamps bare names with the struct
        // module, hence the check below goes on the raw text, not the FQN.
        String clean = stripSuffixes(text);
        if (clean.contains("::")) return List.of(pinned);
        // Bare name: own module first, then every imported module.
        List<FullyQualifiedName> candidates = new ArrayList<>();
        candidates.add(pinned);
        for (ModuleName imported : imports)
        {
            FullyQualifiedName candidate = new FullyQualifiedName(imported, pinned.getName());
            if (!candidates.contains(candidate)) candidates.add(candidate);
        }
        return candidates;
    }

    @NotNull
    public List<C3InterfaceDefinition> findInterfaceDefinitions(
            @NotNull FullyQualifiedName iface,
            @NotNull Project project)
    {
        List<C3InterfaceDefinition> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String query = iface.getFullName();
        String shortQuery = iface.getName();
        try
        {
            for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
            {
                if (!key.equals(query) && !key.endsWith("::" + query)
                    && !key.equals(shortQuery) && !key.endsWith("::" + shortQuery)) continue;
                for (C3PsiElement element : StubIndex.getElements(
                        TypeIndex.KEY,
                        key,
                        project,
                        C3ProjectService.getInstance(project).getSearchScope(),
                        C3PsiElement.class))
                {
                    if (element instanceof C3TypeName typeName
                        && typeName.getParent() instanceof C3InterfaceDefinition definition
                        && isSameInterface(definition, iface)
                        && !result.contains(definition))
                    {
                        result.add(definition);
                    }
                }
            }
        }
        catch (Exception ignored)
        {
            // See findStructDeclarations: never break highlighting on index issues.
        }
        return result;
    }

    @NotNull
    public List<C3FuncDef> getInterfaceMethods(
            @NotNull FullyQualifiedName iface,
            @NotNull Project project)
    {
        List<C3FuncDef> result = new ArrayList<>();
        for (C3InterfaceDefinition definition : findInterfaceDefinitions(iface, project))
        {
            for (C3FuncDef funcDef : definition.getInterfaceBody().getFuncDefList())
            {
                if (!result.contains(funcDef))
                {
                    result.add(funcDef);
                }
            }
        }
        return result;
    }

    /**
     * Whether the name resolves to an interface definition (rather than a
     * struct with the same name).
     */
    public static boolean isInterfaceType(@NotNull FullyQualifiedName type, @NotNull Project project)
    {
        if (DumbService.isDumb(project)) return false;
        try
        {
            return !INSTANCE.findInterfaceDefinitions(type, project).isEmpty();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * The interface a method is declared in, or {@code null} for regular
     * functions and struct methods. Uses the direct parent on purpose:
     * it is cheap on stub-based PSI (no AST loading).
     */
    public static @Nullable C3InterfaceDefinition getDeclaringInterface(@NotNull C3CallablePsiElement callable)
    {
        if (callable instanceof C3FuncDef funcDef) return getDeclaringInterface(funcDef);
        return null;
    }
    public static @Nullable C3InterfaceDefinition getDeclaringInterface(@NotNull C3FuncDef funcDef)
    {
        PsiElement parent = funcDef.getParent();
        if (parent instanceof C3InterfaceBody body && body.getParent() instanceof C3InterfaceDefinition definition)
        {
            return definition;
        }
        return null;
    }

    /**
     * Whether {@code funcDef} is a method declared in the {@code type} interface,
     * e.g. {@code fn String myname();} in {@code interface MyName} for type {@code MyName}.
     */
    public static boolean isInterfaceMethodOf(
            @NotNull C3FuncDef funcDef,
            @NotNull FullyQualifiedName type,
            @NotNull String strippedTypeName)
    {
        C3InterfaceDefinition iface = getDeclaringInterface(funcDef);
        if (iface == null) return false;
        String clean = stripSuffixes(strippedTypeName);
        if (!iface.getTypeName().getText().strip().equals(clean)) return false;
        if (type.getModule() == null) return true;
        ModuleName ifaceModule = ModuleName.from(iface);
        return ifaceModule != null && ifaceModule.equals(type.getModule());
    }

    public static boolean hasAttribute(@Nullable C3FuncDef funcDef, @NotNull String attributeName)
    {
        if (funcDef == null) return false;
        C3Attributes attributes = funcDef.getAttributes();
        if (attributes == null) return false;
        for (C3Attribute attribute : attributes.getAttributeList())
        {
            String text = attribute.getAttributeName().getText();
            String clean = text.startsWith("@") ? text.substring(1) : text;
            int params = clean.indexOf('(');
            if (params >= 0) clean = clean.substring(0, params);
            if (clean.strip().equals(attributeName)) return true;
        }
        return false;
    }

    /**
     * Owner type from {@code fn ... Owner.name(...)}, e.g. {@code "Baz"}.
     */
    public static @Nullable String methodOwnerTypeName(@NotNull C3FuncDef funcDef)
    {
        C3Type ownerType = funcDef.getFuncHeader().getFuncName().getType();
        if (ownerType == null) return null;
        String clean = stripSuffixes(ownerType.getText());
        return clean.isEmpty() ? null : clean;
    }

    /**
     * Owner type from {@code macro ... Owner.name(...)}, e.g. {@code "Foo"}.
     */
    public static @Nullable String methodOwnerTypeName(@NotNull C3MacroDefinition macro)
    {
        C3Type ownerType = macro.getMacroHeader().getMacroName().getType();
        if (ownerType == null) return null;
        String clean = stripSuffixes(ownerType.getText());
        return clean.isEmpty() ? null : clean;
    }

    @NotNull
    public static FullyQualifiedName resolveOwnerType(
            @NotNull String ownerText,
            @Nullable ModuleName contextModule)
    {
        if (ownerText.contains("::")) return FullyQualifiedName.parse(ownerText);
        return new FullyQualifiedName(contextModule, ownerText);
    }

    /**
     * Whether the first parameter of a {@code Type.name} method has the owner type,
     * e.g. {@code Test* self} or {@code Test self} for {@code fn void Test.test(...)}.
     * Mirrors the compiler rule behind "A method must start with an argument of the
     * type it is a method of".
     */
    public static boolean firstParameterMatchesOwner(@NotNull C3FuncDef funcDef, @NotNull String ownerText)
    {
        return firstParameterMatchesOwner(
            funcDef.getParameterTypes(),
            funcDef.getFnParameterList().getParameterList(),
            ownerText);
    }

    /**
     * Whether the first parameter of a {@code Type.name} method (function or macro)
     * has the owner type. A typeless first parameter is always the implicit receiver
     * ({@code &self}, {@code self}, {@code &mutex}, ...).
     */
    public static boolean firstParameterMatchesOwner(
            @NotNull List<ParamType> paramTypes,
            @Nullable C3ParameterList parameterList,
            @NotNull String ownerText)
    {
        if (paramTypes.isEmpty()) return false;
        ShortType first = paramTypes.get(0).getType();
        if (first == null) return true;
        String paramType = stripSuffixes(first.getValue());
        String owner = stripSuffixes(ownerText);
        return !paramType.isEmpty()
            && !owner.isEmpty()
            && (paramType.equals(owner) || paramType.endsWith("::" + owner) || owner.endsWith("::" + paramType));
    }

    /**
     * Whether an {@code @name} reference is actually an invocation of the enclosing
     * macro's trailing block (e.g. {@code @body(x)}), not a global macro call.
     */
    public static boolean isTrailingBlockReference(@NotNull C3PathAtIdent atIdent)
    {
        String callName = shortAtName(atIdent.getText());
        if (callName == null) return false;
        C3MacroDefinition macro = PsiTreeUtil.getParentOfType(atIdent, C3MacroDefinition.class);
        if (macro == null || macro.getMacroParams() == null) return false;
        C3TrailingBlockParam trailing = macro.getMacroParams().getTrailingBlockParam();
        if (trailing == null) return false;
        String trailingText = trailing.getText().strip();
        if (!trailingText.startsWith("@")) return false;
        String trailingName = trailingText.substring(1);
        int paren = trailingName.indexOf('(');
        if (paren >= 0) trailingName = trailingName.substring(0, paren);
        return trailingName.strip().equals(callName);
    }

    private static @Nullable String shortAtName(@NotNull String text)
    {
        String clean = text.strip();
        int separator = clean.lastIndexOf("::");
        if (separator >= 0) clean = clean.substring(separator + 2);
        if (!clean.startsWith("@")) return null;
        return clean.substring(1);
    }

    /**
     * Strips macro parameter sigils ({@code #}, {@code $}, {@code @}) for name matching.
     */
    public static @NotNull String stripParamSigil(@Nullable String name)
    {
        if (name == null) return "";
        int index = 0;
        while (index < name.length() && "#$@".indexOf(name.charAt(index)) >= 0) index++;
        return name.substring(index);
    }

    @NotNull
    public static List<String> collectParameterNames(@NotNull C3FuncDef funcDef)
    {
        List<String> names = new ArrayList<>();
        for (ParamType param : funcDef.getParameterTypes())
        {
            names.add(param.getName());
        }
        return names;
    }

    @NotNull
    public static String shortName(@NotNull String qualifiedName)
    {
        String clean = stripSuffixes(qualifiedName);
        int separator = clean.lastIndexOf("::");
        return separator >= 0 ? clean.substring(separator + 2) : clean;
    }

    /**
     * Interface methods with no {@code StructName.method} implementation.
     */
    @NotNull
    public List<C3FuncDef> findMissingInterfaceMethods(
            @NotNull FullyQualifiedName structType,
            @NotNull FullyQualifiedName iface,
            @NotNull Project project)
    {
        List<C3FuncDef> result = new ArrayList<>();
        for (C3FuncDef interfaceMethod : getInterfaceMethods(iface, project))
        {
            String methodName = interfaceMethod.getNameIdent();
            if (methodName == null) continue;
            // `@optional` methods need no implementation.
            if (AttributeSpecs.hasAttribute(interfaceMethod.getAttributes(), "optional")) continue;
            if (!NameIndexService.INSTANCE.findMethodsForType(structType, methodName, project).isEmpty()) continue;
            result.add(interfaceMethod);
        }
        return result;
    }

    /**
     * Type declarations read directly from the module file (struct, union, enum,
     * bitstruct, interface, typedef, alias). Used as a fallback when the stub
     * index has no types for the module.
     */
    @NotNull
    public List<C3TypeName> findModuleTypeDeclarations(@NotNull String moduleName, @NotNull Project project)
    {
        List<C3TypeName> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        for (com.intellij.psi.PsiFile file : findModuleFiles(moduleName, project))
        {
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3StructDeclaration.class), moduleName);
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3EnumDeclaration.class), moduleName);
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3BitstructDeclaration.class), moduleName);
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3InterfaceDefinition.class), moduleName);
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3TypedefDecl.class), moduleName);
            addTypeNames(result, PsiTreeUtil.findChildrenOfType(file, C3AliasTypeDecl.class), moduleName);
        }
        return result;
    }

    private static <T extends C3PsiElement> void addTypeNames(
            @NotNull List<C3TypeName> result,
            @NotNull java.util.Collection<T> declarations,
            @NotNull String moduleName)
    {
        for (T declaration : declarations)
        {
            C3TypeName typeName = null;
            if (declaration instanceof C3StructDeclaration struct) typeName = struct.getTypeName();
            else if (declaration instanceof C3EnumDeclaration enumDecl) typeName = enumDecl.getTypeName();
            else if (declaration instanceof C3BitstructDeclaration bitstruct) typeName = bitstruct.getTypeName();
            else if (declaration instanceof C3InterfaceDefinition iface) typeName = iface.getTypeName();
            else if (declaration instanceof C3TypedefDecl typedef) typeName = typedef.getTypeName();
            else if (declaration instanceof C3AliasTypeDecl alias) typeName = alias.getTypeName();
            if (typeName == null || result.contains(typeName)) continue;
            ModuleName declarantModule = ModuleName.from(typeName);
            if (declarantModule == null || !declarantModule.getValue().equals(moduleName)) continue;
            result.add(typeName);
        }
    }

    /**
     * Top-level functions and macros read directly from the module file.
     * Used as a fallback when the stub index has no callables for the module.
     */
    @NotNull
    public List<C3CallablePsiElement> findModuleCallables(@NotNull String moduleName, @NotNull Project project)
    {
        List<C3CallablePsiElement> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        for (com.intellij.psi.PsiFile file : findModuleFiles(moduleName, project))
        {
            for (C3FuncDefinition definition : PsiTreeUtil.findChildrenOfType(file, C3FuncDefinition.class))
            {
                if (!moduleName.equals(moduleOf(definition))) continue;
                C3FuncDef funcDef = definition.getFuncDef();
                if (funcDef != null && !result.contains(funcDef))
                {
                    result.add(funcDef);
                }
            }
            for (C3MacroDefinition macro : PsiTreeUtil.findChildrenOfType(file, C3MacroDefinition.class))
            {
                if (!moduleName.equals(moduleOf(macro))) continue;
                if (!result.contains(macro)) result.add(macro);
            }
        }
        return result;
    }

    /**
     * Files conventionally belonging to a module: the single module file plus,
     * for umbrella modules like {@code std::io}, the files directly in the
     * module directory that declare the same module. Loaded without forcing
     * stub/AST reconciliation of unrelated files.
     */
    private static @NotNull List<com.intellij.psi.PsiFile> findModuleFiles(
            @NotNull String moduleName,
            @NotNull Project project)
    {
        List<com.intellij.psi.PsiFile> result = new ArrayList<>();
        String relativePath = moduleName.replace("::", "/");
        List<String> candidates = new ArrayList<>();
        candidates.add(relativePath + ".c3");
        candidates.add(relativePath + ".c3i");
        if (relativePath.startsWith("std/"))
        {
            candidates.add(relativePath.substring(4) + ".c3");
            candidates.add(relativePath.substring(4) + ".c3i");
        }
        com.intellij.openapi.vfs.LocalFileSystem lfs = com.intellij.openapi.vfs.LocalFileSystem.getInstance();
        com.intellij.psi.PsiManager psiManager = com.intellij.psi.PsiManager.getInstance(project);
        List<String> stdlibPaths = DumbService.isDumb(project)
            ? org.c3lang.intellij.project.C3ProjectService.getInstance(project).getKnownStdlibPaths()
            : org.c3lang.intellij.project.C3ProjectService.getInstance(project).getStdlibPaths();
        for (String stdlibPath : stdlibPaths)
        {
            for (String relPath : candidates)
            {
                com.intellij.openapi.vfs.VirtualFile vf = lfs.findFileByPath(stdlibPath + "/" + relPath);
                if (vf == null || !vf.isValid()) continue;
                com.intellij.psi.PsiFile psi = psiManager.findFile(vf);
                if (psi instanceof org.c3lang.intellij.psi.C3File && !result.contains(psi))
                {
                    result.add(psi);
                }
            }
            // Umbrella module (e.g. std::io spans io.c3, stream.c3, ...):
            // same-directory files declaring exactly this module.
            com.intellij.openapi.vfs.VirtualFile dir = lfs.findFileByPath(stdlibPath + "/" + relativePath);
            if (dir == null || !dir.isValid() || !dir.isDirectory()) continue;
            for (com.intellij.openapi.vfs.VirtualFile child : dir.getChildren())
            {
                if (child.isDirectory()) continue;
                String ext = child.getExtension();
                if (!"c3".equals(ext) && !"c3i".equals(ext)) continue;
                com.intellij.psi.PsiFile psi = psiManager.findFile(child);
                if (!(psi instanceof org.c3lang.intellij.psi.C3File) || result.contains(psi)) continue;
                for (PsiElement top : psi.getChildren())
                {
                    if (!(top instanceof C3ModuleSection section)) continue;
                    ModuleName declared = section.getModuleName();
                    if (declared != null && declared.getValue().equals(moduleName))
                    {
                        result.add(psi);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * The conventional single file for a module ({@code a/b.c3} for {@code a::b}),
     * loaded without forcing stub/AST reconciliation of unrelated files.
     * Returns {@code null} when the conventional file is absent.
     */
    private static @Nullable com.intellij.psi.PsiFile findSingleModuleFile(
            @NotNull String moduleName,
            @NotNull Project project)
    {
        List<com.intellij.psi.PsiFile> files = findModuleFiles(moduleName, project);
        return files.isEmpty() ? null : files.get(0);
    }

    private static @Nullable String moduleOf(@NotNull C3PsiElement element)
    {
        ModuleName moduleName = ModuleName.from(element);
        return moduleName != null ? moduleName.getValue() : null;
    }

    /**
     * Named type declarations (struct, union, enum, interface, typedef, bitstruct)
     * for diagnostics like "'X' is not an interface".
     */
    @NotNull
    public List<C3TypeName> findTypeDeclarations(
            @NotNull FullyQualifiedName name,
            @NotNull Project project)
    {
        List<C3TypeName> result = new ArrayList<>();
        if (DumbService.isDumb(project)) return result;
        String query = name.getFullName();
        for (String key : StubIndex.getInstance().getAllKeys(TypeIndex.KEY, project))
        {
            if (!key.equals(query) && !key.endsWith("::" + query)) continue;
            Collection<C3PsiElement> elements;
            try
            {
                elements = StubIndex.getElements(
                    TypeIndex.KEY,
                    key,
                    project,
                    C3ProjectService.getInstance(project).getSearchScope(),
                    C3PsiElement.class);
            }
            catch (Exception ignored)
            {
                // Stale index entry for a file without a stub tree.
                continue;
            }
            for (C3PsiElement element : elements)
            {
                if (!(element instanceof C3TypeName typeName)) continue;
                PsiElement parent = typeName.getParent();
                if (!(parent instanceof C3StructDeclaration
                    || parent instanceof C3EnumDeclaration
                    || parent instanceof C3InterfaceDefinition
                    || parent instanceof C3TypedefDecl
                    || parent instanceof C3BitstructDeclaration)) continue;
                if (!typeName.getText().strip().equals(name.getName())) continue;
                if (name.getModule() != null)
                {
                    ModuleName defModule = ModuleName.from(typeName);
                    if (defModule == null || !defModule.equals(name.getModule())) continue;
                }
                if (!result.contains(typeName)) result.add(typeName);
            }
        }
        return result;
    }

    private static boolean isSameInterface(
            @NotNull C3InterfaceDefinition definition,
            @NotNull FullyQualifiedName iface)
    {
        if (!definition.getTypeName().getText().strip().equals(iface.getName())) return false;
        if (iface.getModule() == null) return true;
        ModuleName defModule = ModuleName.from(definition);
        return defModule != null && defModule.equals(iface.getModule());
    }

    /**
     * Parses a contract entry like {@code MyName} or {@code mod::MyName};
     * unqualified names resolve to the given module.
     */
    public static @Nullable FullyQualifiedName parseContractReference(
            @NotNull String text,
            @Nullable ModuleName structModule)
    {
        String clean = stripSuffixes(text);
        if (clean.isEmpty()) return null;
        if (clean.contains("::")) return FullyQualifiedName.parse(clean);
        return new FullyQualifiedName(structModule, clean);
    }

    private static @NotNull String stripSuffixes(@NotNull String text)
    {
        String clean = text.strip();
        int generic = clean.indexOf('<');
        if (generic >= 0) clean = clean.substring(0, generic).strip();
        int paren = clean.indexOf('(');
        if (paren >= 0) clean = clean.substring(0, paren).strip();
        while (clean.endsWith("*")) clean = clean.substring(0, clean.length() - 1).strip();
        return clean;
    }
}
