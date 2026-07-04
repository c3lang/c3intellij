package org.c3lang.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class C3CompilerDetector
{
    private C3CompilerDetector()
    {
    }

    public static @NotNull DetectionResult detect(@NotNull String compilerPath)
    {
        return new DetectionResult(detectVersion(compilerPath), detectStdlibPath(compilerPath));
    }

    public static @NotNull String detectVersion(@NotNull String compilerPath)
    {
        try
        {
            Process process = new ProcessBuilder(compilerPath, "--version").start();
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
        try
        {
            Process process = new ProcessBuilder(compilerPath, "compile", "--build-env").start();
            process.waitFor(10, TimeUnit.SECONDS);
            return parseStdlibPath(readText(process.getInputStream()));
        }
        catch (Exception ignored)
        {
            return "";
        }
    }

    public static @NotNull String parseStdlibPath(@NotNull String result)
    {
        for (String line : result.split("\\R"))
        {
            String trimmed = line.trim();
            if (trimmed.startsWith("Stdlib"))
            {
                int colon = trimmed.indexOf(':');
                return colon >= 0 ? trimmed.substring(colon + 1).trim() : "";
            }
        }
        return "";
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
