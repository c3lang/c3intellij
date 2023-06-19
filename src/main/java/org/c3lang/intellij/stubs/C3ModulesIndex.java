package org.c3lang.intellij.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.c3lang.intellij.psi.C3File;
import org.c3lang.intellij.psi.C3Module;
import org.c3lang.intellij.psi.C3ModuleSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class C3ModulesIndex extends StringStubIndexExtension<C3ModuleSection>
{
    public C3ModulesIndex()
    {
    }

    public static StubIndexKey<String, C3ModuleSection> KEY = StubIndexKey.createIndexKey("c3.modules");

    @Override public @NotNull StubIndexKey<String, C3ModuleSection> getKey()
    {
        return KEY;
    }

    @Override
    public Collection<C3ModuleSection> get(@NotNull String s, @NotNull Project project, @NotNull GlobalSearchScope scope)
    {
        return StubIndex.getElements(KEY, s, project, GlobalSearchScope.everythingScope(project), C3ModuleSection.class);
    }
}
