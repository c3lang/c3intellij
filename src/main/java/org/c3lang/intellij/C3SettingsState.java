package org.c3lang.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = @Storage("C3SettingsPlugin.xml"))
public class C3SettingsState implements PersistentStateComponent<C3SettingsState>
{
    public String sdk = "c3c";
    public String stdlibPath = "";

    public static C3SettingsState getInstance()
    {
        return ApplicationManager.getApplication().getService(C3SettingsState.class);
    }

    public C3SettingsState getState()
    {
        return this;
    }

    @Override public void loadState(@NotNull C3SettingsState state)
    {
        XmlSerializerUtil.copyBean(state, this);
    }

}