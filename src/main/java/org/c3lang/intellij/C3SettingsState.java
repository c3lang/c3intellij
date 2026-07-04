package org.c3lang.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = @Storage("C3SettingsPlugin.xml"))
public class C3SettingsState implements PersistentStateComponent<C3SettingsState>
{
    public static final String DEFAULT_COMPILER_NAME = "Default Compiler";

    /**
     * Kept for migration from older plugin versions that stored a single compiler path.
     */
    public String sdk = "c3c";
    /**
     * Kept for migration from older plugin versions that stored a single stdlib path.
     */
    public String stdlibPath = "";
    /**
     * Kept for migration from an intermediate plugin version that stored stdlib paths separately.
     */
    public List<StdlibEntry> stdlibEntries = new ArrayList<>();
    public List<CompilerProfile> compilerProfiles = new ArrayList<>();

    public static C3SettingsState getInstance()
    {
        return ApplicationManager.getApplication().getService(C3SettingsState.class);
    }

    public C3SettingsState getState()
    {
        migrateCompilerProfiles();
        return this;
    }

    @Override public void loadState(@NotNull C3SettingsState state)
    {
        XmlSerializerUtil.copyBean(state, this);
        migrateCompilerProfiles();
    }

    public @NotNull List<CompilerProfile> getCompilerProfiles()
    {
        migrateCompilerProfiles();
        return List.copyOf(compilerProfiles);
    }

    public void setCompilerProfiles(@NotNull List<CompilerProfile> profiles)
    {
        ArrayList<CompilerProfile> normalized = new ArrayList<>();
        for (CompilerProfile profile : profiles)
        {
            CompilerProfile copy = normalizeProfile(profile);
            if (copy != null) normalized.add(copy);
        }

        compilerProfiles = normalized;
        CompilerProfile defaultProfile = compilerProfiles.isEmpty()
            ? new CompilerProfile(DEFAULT_COMPILER_NAME, sdk == null ? "" : sdk, "", stdlibPath == null ? "" : stdlibPath)
            : compilerProfiles.get(0);
        sdk = defaultProfile.binaryPath;
        stdlibPath = defaultProfile.stdlibPath;
        stdlibEntries = new ArrayList<>();
        for (CompilerProfile profile : compilerProfiles)
        {
            if (!profile.stdlibPath.isBlank())
            {
                stdlibEntries.add(new StdlibEntry(profile.name, profile.stdlibPath));
            }
        }
    }

    public void addCompilerProfile(@NotNull CompilerProfile profile)
    {
        ArrayList<CompilerProfile> profiles = new ArrayList<>(getCompilerProfiles());
        profiles.add(profile);
        setCompilerProfiles(profiles);
    }

    public boolean hasCompilerProfiles()
    {
        return !getCompilerProfiles().isEmpty();
    }

    public @NotNull CompilerProfile getDefaultCompilerProfile()
    {
        migrateCompilerProfiles();
        return compilerProfiles.isEmpty()
            ? new CompilerProfile(
                DEFAULT_COMPILER_NAME,
                sdk == null ? "" : sdk,
                "",
                stdlibPath == null ? "" : stdlibPath
            )
            : compilerProfiles.get(0);
    }

    public @NotNull String getDefaultCompilerBinaryPath()
    {
        return getDefaultCompilerProfile().binaryPath;
    }

    public @NotNull List<StdlibEntry> getStdlibEntries()
    {
        ArrayList<StdlibEntry> entries = new ArrayList<>();
        for (CompilerProfile profile : getCompilerProfiles())
        {
            if (!profile.stdlibPath.isBlank())
            {
                entries.add(new StdlibEntry(profile.name, profile.stdlibPath));
            }
        }
        return List.copyOf(entries);
    }

    public void setStdlibEntries(@NotNull List<StdlibEntry> entries)
    {
        ArrayList<CompilerProfile> profiles = new ArrayList<>(getCompilerProfiles());
        if (profiles.isEmpty())
        {
            for (StdlibEntry entry : entries)
            {
                StdlibEntry copy = normalizeEntry(entry);
                if (copy != null)
                {
                    profiles.add(new CompilerProfile(copy.name, sdk, "", copy.path));
                }
            }
        }
        setCompilerProfiles(profiles);
    }

    public void addStdlibEntry(@NotNull String name, @NotNull String path)
    {
        addCompilerProfile(new CompilerProfile(name, sdk, "", path));
    }

    public boolean hasStdlibEntries()
    {
        return !getStdlibEntries().isEmpty();
    }

    public @NotNull List<String> getStdlibPaths()
    {
        ArrayList<String> paths = new ArrayList<>();
        for (StdlibEntry entry : getStdlibEntries())
        {
            paths.add(entry.path);
        }
        return List.copyOf(paths);
    }

    public @NotNull String getDefaultStdlibPath()
    {
        return getDefaultCompilerProfile().stdlibPath;
    }

    private void migrateCompilerProfiles()
    {
        if (compilerProfiles == null)
        {
            compilerProfiles = new ArrayList<>();
        }
        if (!compilerProfiles.isEmpty())
        {
            setCompilerProfiles(compilerProfiles);
            return;
        }

        if (stdlibEntries == null)
        {
            stdlibEntries = new ArrayList<>();
        }

        StdlibEntry stdlibEntry = null;
        if (!stdlibEntries.isEmpty())
        {
            stdlibEntry = normalizeEntry(stdlibEntries.get(0));
        }
        if (stdlibEntry == null)
        {
            stdlibEntry = normalizeEntry(new StdlibEntry(DEFAULT_COMPILER_NAME, stdlibPath));
        }

        CompilerProfile profile = new CompilerProfile(
            DEFAULT_COMPILER_NAME,
            sdk == null ? "" : sdk.trim(),
            "",
            stdlibEntry == null ? "" : stdlibEntry.path
        );
        setCompilerProfiles(List.of(profile));
    }

    private static @Nullable CompilerProfile normalizeProfile(@Nullable CompilerProfile profile)
    {
        if (profile == null) return null;

        String name = profile.name == null ? "" : profile.name.trim();
        if (name.isEmpty()) name = DEFAULT_COMPILER_NAME;

        String binaryPath = profile.binaryPath == null ? "" : profile.binaryPath.trim();
        String version = profile.version == null ? "" : profile.version.trim();
        String stdlibPath = profile.stdlibPath == null ? "" : profile.stdlibPath.trim();

        return new CompilerProfile(name, binaryPath, version, stdlibPath);
    }

    private static @Nullable StdlibEntry normalizeEntry(@Nullable StdlibEntry entry)
    {
        if (entry == null) return null;

        String path = entry.path == null ? "" : entry.path.trim();
        if (path.isEmpty()) return null;

        String name = entry.name == null ? "" : entry.name.trim();
        if (name.isEmpty()) name = "Stdlib";
        return new StdlibEntry(name, path);
    }

    public static class StdlibEntry
    {
        public String name = "";
        public String path = "";

        public StdlibEntry()
        {
        }

        public StdlibEntry(@NotNull String name, @NotNull String path)
        {
            this.name = name;
            this.path = path;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object) return true;
            if (!(object instanceof StdlibEntry that)) return false;
            return Objects.equals(name, that.name) && Objects.equals(path, that.path);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(name, path);
        }

        @Override
        public String toString()
        {
            return name == null || name.isBlank() ? path : name;
        }
    }

    public static class CompilerProfile
    {
        public String name = DEFAULT_COMPILER_NAME;
        public String binaryPath = "";
        public String version = "";
        public String stdlibPath = "";

        public CompilerProfile()
        {
        }

        public CompilerProfile(
                @NotNull String name,
                @NotNull String binaryPath,
                @NotNull String version,
                @NotNull String stdlibPath)
        {
            this.name = name;
            this.binaryPath = binaryPath;
            this.version = version;
            this.stdlibPath = stdlibPath;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object) return true;
            if (!(object instanceof CompilerProfile that)) return false;
            return Objects.equals(name, that.name)
                && Objects.equals(binaryPath, that.binaryPath)
                && Objects.equals(version, that.version)
                && Objects.equals(stdlibPath, that.stdlibPath);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(name, binaryPath, version, stdlibPath);
        }

        @Override
        public String toString()
        {
            return name == null || name.isBlank() ? DEFAULT_COMPILER_NAME : name;
        }
    }

}
