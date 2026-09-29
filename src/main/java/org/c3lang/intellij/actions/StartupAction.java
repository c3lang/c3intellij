package org.c3lang.intellij.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.c3lang.intellij.C3CompilerDetector;
import org.c3lang.intellij.C3SettingsState;
import org.c3lang.intellij.C3StdLibRootsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StartupAction implements ProjectActivity
{
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation)
    {
        C3SettingsState settings = C3SettingsState.getInstance();
        boolean needsDetection = !settings.hasCompilerProfiles() || settings.getDefaultStdlibPath().isBlank();

        try
        {
            if (needsDetection)
            {
                String compilerPath = settings.getDefaultCompilerBinaryPath();
                C3CompilerDetector.DetectionResult result = C3CompilerDetector.detect(compilerPath);
                if (result.hasAnyValue())
                {
                    C3SettingsState.CompilerProfile profile = settings.getDefaultCompilerProfile();
                    profile.version = result.versionOr(profile.version);
                    profile.stdlibPath = C3StdLibRootsProvider.normalizePath(result.stdlibPathOr(profile.stdlibPath));
                    if (profile.stdlibPath == null) profile.stdlibPath = "";
                    settings.setCompilerProfiles(List.of(profile));
                }
            }
            else
            {
                C3SettingsState.CompilerProfile profile = settings.getDefaultCompilerProfile();
                String clean = C3StdLibRootsProvider.normalizePath(profile.stdlibPath);
                if (clean != null && !clean.equals(profile.stdlibPath))
                {
                    profile.stdlibPath = clean;
                    settings.setCompilerProfiles(List.of(profile));
                }
            }
        }
        catch (Exception ignored)
        {
        }

        return Unit.INSTANCE;
    }
}
