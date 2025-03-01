package org.c3lang.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class C3SdkType extends SdkType {
    private final static Logger log = Logger.getInstance(C3SdkType.class.getName());
    public C3SdkType() {
        super("C3");
    }

    @Override
    public @Nullable String suggestHomePath() {
        return null;
    }

    public static boolean isSdkHome(@NotNull String s) {
        if (!Files.exists(Paths.get(s, "c3c.exe")) && !Files.exists(Paths.get(s, "c3c"))) return false;
        return Files.exists(Paths.get(s, "lib", "std")) || Files.exists(Paths.get(s, "..", "lib", "std"));
    }

    @Override
    public boolean isValidSdkHome(@NotNull String s) {
        return isSdkHome(s);
    }

    @Override
    public @NotNull String suggestSdkName(@Nullable String s, @NotNull String s1) {
        return "C3 SDK";
    }

    @Override
    public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel,
                                                                                 @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
        return "C3 SDK";
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }

    @Override
    public @Nullable String getVersionString(@NotNull String sdkHome) {
        return "TODO";
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        final SdkModificator sdkModificator = sdk.getSdkModificator();

        final VirtualFile homeDirectory = sdk.getHomeDirectory();
        if (homeDirectory == null) {
            log.error("Cannot find sdk home directory");
            return;
        }
        final VirtualFile lib = homeDirectory.findFileByRelativePath("lib");
        if (lib == null) {
            log.error("Cannot find lib directory");
            return;
        }

        final VirtualFile std = lib.findFileByRelativePath("std");
        if (std == null) {
            log.error("Cannot find std directory");
            return;
        }

        sdkModificator.addRoot(std, OrderRootType.SOURCES);
        sdkModificator.addRoot(std, OrderRootType.CLASSES);

        ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges);
    }
}

