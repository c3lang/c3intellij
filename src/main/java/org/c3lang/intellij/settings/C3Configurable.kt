package org.c3lang.intellij.settings

import com.intellij.openapi.options.Configurable
import org.c3lang.intellij.C3SettingsState
import javax.swing.JComponent

class C3Configurable : Configurable
{
    private var c3SettingsComponent: C3SettingsComponent? = null

    override fun createComponent(): JComponent?
    {
        val settings = C3SettingsState.getInstance()

        c3SettingsComponent = C3SettingsComponent()
        c3SettingsComponent!!.stdlibPath = settings.stdlibPath

        return c3SettingsComponent?.mainPanel
    }

    override fun isModified(): Boolean
    {
        val settings = C3SettingsState.getInstance()
        return c3SettingsComponent?.stdlibPath != settings.stdlibPath
    }

    override fun apply()
    {
        val settings = C3SettingsState.getInstance()
        settings.stdlibPath = c3SettingsComponent?.stdlibPath ?: ""
    }

    override fun getDisplayName() = "C3"
}