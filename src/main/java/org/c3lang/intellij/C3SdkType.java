package org.c3lang.intellij;

import com.intellij.openapi.projectRoots.*;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class C3SdkType extends SdkType
{
    public C3SdkType()
    {
        super("C3");
    }

    @Override public @Nullable String suggestHomePath()
    {
        return null;
    }

    public static boolean isSdkHome(@NotNull String s)
    {
        if (!Files.exists(Paths.get(s, "c3c.exe")) && !Files.exists(Paths.get(s, "c3c"))) return false;
        return Files.exists(Paths.get(s, "lib", "std")) || Files.exists(Paths.get(s, "..", "lib", "std"));
    }

    @Override public boolean isValidSdkHome(@NotNull String s)
    {
        return isSdkHome(s);
    }

    @Override public @NotNull String suggestSdkName(@Nullable String s, @NotNull String s1)
    {
        return "C3 SDK";
    }

    @Override public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel,
                                                                                           @NotNull SdkModificator sdkModificator)
    {
        return null;
    }

    @Override public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName()
    {
        return "C3 SDK";
    }

    @Override public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element)
    {

    }
}

