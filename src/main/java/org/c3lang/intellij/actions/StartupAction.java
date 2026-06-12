package org.c3lang.intellij.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StartupAction implements ProjectActivity
{
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation)
    {
        C3SettingsState settings = C3SettingsState.getInstance();
        if (settings.stdlibPath != null && !settings.stdlibPath.isEmpty()) return Unit.INSTANCE;

        try
        {
            Process process = Runtime.getRuntime().exec(new String[]{"c3c", "compile", "--build-env"});
            process.waitFor();

            String result = readText(process.getInputStream());
            settings.stdlibPath = findStdlibPath(result);
        }
        catch (Exception ignored)
        {
            settings.stdlibPath = "";
        }

        return Unit.INSTANCE;
    }

    @Nullable
    private static String findStdlibPath(@NotNull String result)
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
        return null;
    }

    @NotNull
    private static String readText(@NotNull InputStream inputStream) throws IOException
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
}
