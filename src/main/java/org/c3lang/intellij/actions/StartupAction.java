package org.c3lang.intellij.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.c3lang.intellij.C3CompilerDetector;
import org.c3lang.intellij.C3SettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StartupAction implements ProjectActivity
{
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation)
    {
        C3SettingsState settings = C3SettingsState.getInstance();
        if (settings.hasCompilerProfiles() && !settings.getDefaultStdlibPath().isBlank()) return Unit.INSTANCE;

        try
        {
            String compilerPath = settings.getDefaultCompilerBinaryPath();
            C3CompilerDetector.DetectionResult result = C3CompilerDetector.detect(compilerPath);
            if (result.hasAnyValue())
            {
                C3SettingsState.CompilerProfile profile = settings.getDefaultCompilerProfile();
                profile.version = result.versionOr(profile.version);
                profile.stdlibPath = result.stdlibPathOr(profile.stdlibPath);
                settings.setCompilerProfiles(java.util.List.of(profile));
            }
        }
        catch (Exception ignored)
        {
        }

        return Unit.INSTANCE;
    }
}
