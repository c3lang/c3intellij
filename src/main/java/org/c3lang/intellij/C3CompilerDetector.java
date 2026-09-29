package org.c3lang.intellij;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class C3CompilerDetector
{
    private C3CompilerDetector()
    {
    }

    public static @NotNull DetectionResult detect(@NotNull String compilerPath)
    {
        String resolvedExecutable = findCompilerExecutable(compilerPath);
        return new DetectionResult(
            detectVersion(resolvedExecutable),
            detectStdlibPath(resolvedExecutable)
        );
    }

    public static @NotNull String detectVersion(@NotNull String compilerPath)
    {
        String resolved = findCompilerExecutable(compilerPath);
        try
        {
            Process process = new ProcessBuilder(resolved, "--version").start();
            process.waitFor(10, TimeUnit.SECONDS);
            return firstNonBlankLine(readText(process.getInputStream()));
        }
        catch (Exception ignored)
        {
            return "";
        }
    }

    public static @NotNull String detectStdlibPath(@NotNull String compilerPath)
    {
        String resolved = findCompilerExecutable(compilerPath);
        try
        {
            Process process = new ProcessBuilder(resolved, "compile", "--build-env").start();
            process.waitFor(10, TimeUnit.SECONDS);
            String stdlib = parseStdlibPath(readText(process.getInputStream()));
            if (!stdlib.isBlank()) return stdlib;
        }
        catch (Exception ignored)
        {
        }

        // Fallback: check relative to resolved binary
        try
        {
            Path binPath = Paths.get(resolved);
            if (Files.isRegularFile(binPath))
            {
                Path parent = binPath.getParent();
                if (parent != null)
                {
                    Path libDir = parent.resolve("lib");
                    if (Files.isDirectory(libDir))
                    {
                        return normalizePathString(libDir.toString());
                    }
                    Path siblingLib = parent.resolve("../lib").normalize();
                    if (Files.isDirectory(siblingLib))
                    {
                        return normalizePathString(siblingLib.toString());
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }

        return "";
    }

    public static @NotNull String parseStdlibPath(@NotNull String result)
    {
        for (String line : result.split("\\R"))
        {
            String trimmed = line.trim();
            if (trimmed.startsWith("Stdlib"))
            {
                int colon = trimmed.indexOf(':');
                if (colon >= 0)
                {
                    return normalizePathString(trimmed.substring(colon + 1));
                }
            }
        }
        return "";
    }

    public static @NotNull String findCompilerExecutable(@Nullable String preferredPath)
    {
        if (preferredPath != null && !preferredPath.isBlank())
        {
            try
            {
                Path path = Paths.get(preferredPath);
                if (Files.isRegularFile(path))
                {
                    return path.toAbsolutePath().toString();
                }
                if (SystemInfo.isWindows && !preferredPath.toLowerCase().endsWith(".exe"))
                {
                    Path exePath = Paths.get(preferredPath + ".exe");
                    if (Files.isRegularFile(exePath))
                    {
                        return exePath.toAbsolutePath().toString();
                    }
                }
            }
            catch (Exception ignored)
            {
            }
        }

        // Check PATH
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null)
        {
            for (String dir : pathEnv.split(File.pathSeparator))
            {
                if (dir.isBlank()) continue;
                try
                {
                    File exe = new File(dir.trim(), SystemInfo.isWindows ? "c3c.exe" : "c3c");
                    if (exe.isFile())
                    {
                        return exe.getAbsolutePath();
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        }

        // Check well-known installation paths
        List<String> knownPaths = new ArrayList<>();
        if (SystemInfo.isWindows)
        {
            knownPaths.add("C:\\Program Files\\c3\\c3c.exe");
            knownPaths.add("C:\\Program Files (x86)\\c3\\c3c.exe");
            knownPaths.add("C:\\c3\\c3c.exe");
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null) knownPaths.add(localAppData + "\\Programs\\c3\\c3c.exe");
            String userProfile = System.getenv("USERPROFILE");
            if (userProfile != null) knownPaths.add(userProfile + "\\.cargo\\bin\\c3c.exe");
            String c3Home = System.getenv("C3_HOME");
            if (c3Home != null) knownPaths.add(c3Home + "\\c3c.exe");
            String c3Path = System.getenv("C3PATH");
            if (c3Path != null) knownPaths.add(c3Path + "\\c3c.exe");
        }
        else
        {
            knownPaths.add("/usr/bin/c3c");
            knownPaths.add("/usr/local/bin/c3c");
            knownPaths.add("/opt/c3/bin/c3c");
            knownPaths.add("/opt/c3c/bin/c3c");
            knownPaths.add("/opt/homebrew/bin/c3c");
            String home = System.getProperty("user.home");
            if (home != null)
            {
                knownPaths.add(home + "/.local/bin/c3c");
                knownPaths.add(home + "/.cargo/bin/c3c");
            }
        }

        for (String candidate : knownPaths)
        {
            try
            {
                if (Files.isRegularFile(Paths.get(candidate)))
                {
                    return candidate;
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return preferredPath != null && !preferredPath.isBlank() ? preferredPath : (SystemInfo.isWindows ? "c3c.exe" : "c3c");
    }

    private static @NotNull String normalizePathString(@NotNull String raw)
    {
        String normalized = raw.trim().replace('\\', '/');
        while (normalized.endsWith("/") && normalized.length() > 1)
        {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static @NotNull String firstNonBlankLine(@NotNull String text)
    {
        for (String line : text.split("\\R"))
        {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) return trimmed;
        }
        return "";
    }

    private static @NotNull String readText(@NotNull InputStream inputStream) throws IOException
    {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                result.append(line).append('\n');
            }
        }
        return result.toString();
    }

    public record DetectionResult(@NotNull String version, @NotNull String stdlibPath)
    {
        public boolean hasAnyValue()
        {
            return !version.isBlank() || !stdlibPath.isBlank();
        }

        public @NotNull String versionOr(@Nullable String fallback)
        {
            return version.isBlank() ? fallback == null ? "" : fallback : version;
        }

        public @NotNull String stdlibPathOr(@Nullable String fallback)
        {
            return stdlibPath.isBlank() ? fallback == null ? "" : fallback : stdlibPath;
        }
    }
}
