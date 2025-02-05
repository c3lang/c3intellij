package org.c3lang.intellij.intention

import com.intellij.openapi.util.Key
import org.c3lang.intellij.psi.ModuleName

object IntentionKeys {
    val AUTO_IMPORT = Key<ModuleName>("auto_import")
}